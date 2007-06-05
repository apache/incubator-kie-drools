// $ANTLR 3.0 /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2007-06-05 12:19:41

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ATTRIBUTES", "PACKAGE", "IMPORT", "FUNCTION", "ID", "DOT", "GLOBAL", "LEFT_PAREN", "COMMA", "RIGHT_PAREN", "QUERY", "END", "TEMPLATE", "RULE", "WHEN", "DATE_EFFECTIVE", "STRING", "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "RULEFLOW_GROUP", "AGENDA_GROUP", "DURATION", "DIALECT", "LOCK_ON_ACTIVE", "OR", "DOUBLE_PIPE", "AND", "DOUBLE_AMPER", "FROM", "EXISTS", "NOT", "EVAL", "FORALL", "ACCUMULATE", "INIT", "ACTION", "RESULT", "COLLECT", "CONTAINS", "EXCLUDES", "MATCHES", "MEMBEROF", "IN", "FLOAT", "NULL", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "THEN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "':'", "'.*'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='"
    };
    public static final int COMMA=12;
    public static final int EXISTS=39;
    public static final int AUTO_FOCUS=27;
    public static final int END=15;
    public static final int HexDigit=63;
    public static final int FORALL=42;
    public static final int TEMPLATE=16;
    public static final int MISC=69;
    public static final int FLOAT=53;
    public static final int QUERY=14;
    public static final int THEN=59;
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
    public static final int EscapeSequence=62;
    public static final int DIALECT=32;
    public static final int INT=25;
    public static final int LOCK_ON_ACTIVE=33;
    public static final int DATE_EXPIRES=21;
    public static final int LEFT_SQUARE=57;
    public static final int CONTAINS=48;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=66;
    public static final int ATTRIBUTES=4;
    public static final int LEFT_CURLY=55;
    public static final int RESULT=46;
    public static final int ID=8;
    public static final int FROM=38;
    public static final int LEFT_PAREN=11;
    public static final int ACTIVATION_GROUP=28;
    public static final int DOUBLE_AMPER=37;
    public static final int RIGHT_CURLY=56;
    public static final int EXCLUDES=49;
    public static final int BOOL=23;
    public static final int MEMBEROF=51;
    public static final int WHEN=18;
    public static final int RULEFLOW_GROUP=29;
    public static final int WS=61;
    public static final int STRING=20;
    public static final int ACTION=45;
    public static final int COLLECT=47;
    public static final int IN=52;
    public static final int NO_LOOP=26;
    public static final int ACCUMULATE=43;
    public static final int UnicodeEscape=64;
    public static final int DURATION=31;
    public static final int EVAL=41;
    public static final int MATCHES=50;
    public static final int EOF=-1;
    public static final int EOL=60;
    public static final int NULL=54;
    public static final int AGENDA_GROUP=30;
    public static final int OctalEscape=65;
    public static final int SALIENCE=24;
    public static final int MULTI_LINE_COMMENT=68;
    public static final int RIGHT_PAREN=13;
    public static final int NOT=40;
    public static final int ENABLED=22;
    public static final int RIGHT_SQUARE=58;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=67;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[74+1];
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:176:1: opt_semicolon : ( ';' )? ;
    public final void opt_semicolon() throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:177:4: ( ( ';' )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:177:4: ( ';' )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:177:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==70) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:177:4: ';'
                    {
                    match(input,70,FOLLOW_70_in_opt_semicolon40); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:180:1: compilation_unit : prolog ( statement )+ ;
    public final void compilation_unit() throws RecognitionException {

        		// reset Location information
        		this.location = new Location( Location.LOCATION_UNKNOWN );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:185:4: ( prolog ( statement )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:185:4: prolog ( statement )+
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit58);
            prolog();
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:186:3: ( statement )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:186:3: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit63);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:189:1: prolog : (pkgstmt= package_statement )? ( ATTRIBUTES ':' )? (a= rule_attribute ( ( ',' )? a= rule_attribute )* )? ;
    public final void prolog() throws RecognitionException {
        String pkgstmt = null;

        AttributeDescr a = null;



        		String packageName = "";
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:193:4: ( (pkgstmt= package_statement )? ( ATTRIBUTES ':' )? (a= rule_attribute ( ( ',' )? a= rule_attribute )* )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:193:4: (pkgstmt= package_statement )? ( ATTRIBUTES ':' )? (a= rule_attribute ( ( ',' )? a= rule_attribute )* )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:193:4: (pkgstmt= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:193:6: pkgstmt= package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_prolog86);
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:197:3: ( ATTRIBUTES ':' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==ATTRIBUTES) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:197:4: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_prolog100); if (failed) return ;
                    match(input,71,FOLLOW_71_in_prolog102); if (failed) return ;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:198:3: (a= rule_attribute ( ( ',' )? a= rule_attribute )* )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==DATE_EFFECTIVE||(LA7_0>=DATE_EXPIRES && LA7_0<=ENABLED)||LA7_0==SALIENCE||(LA7_0>=NO_LOOP && LA7_0<=LOCK_ON_ACTIVE)) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:198:5: a= rule_attribute ( ( ',' )? a= rule_attribute )*
                    {
                    pushFollow(FOLLOW_rule_attribute_in_prolog112);
                    a=rule_attribute();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                       	  	        	this.packageDescr.addAttribute( a );
                      	                
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:202:6: ( ( ',' )? a= rule_attribute )*
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==COMMA||LA6_0==DATE_EFFECTIVE||(LA6_0>=DATE_EXPIRES && LA6_0<=ENABLED)||LA6_0==SALIENCE||(LA6_0>=NO_LOOP && LA6_0<=LOCK_ON_ACTIVE)) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:202:14: ( ',' )? a= rule_attribute
                    	    {
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:202:14: ( ',' )?
                    	    int alt5=2;
                    	    int LA5_0 = input.LA(1);

                    	    if ( (LA5_0==COMMA) ) {
                    	        alt5=1;
                    	    }
                    	    switch (alt5) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:202:14: ','
                    	            {
                    	            match(input,COMMA,FOLLOW_COMMA_in_prolog135); if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_rule_attribute_in_prolog140);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:209:1: package_statement returns [String packageName] : PACKAGE n= dotted_name[null] opt_semicolon ;
    public final String package_statement() throws RecognitionException {
        String packageName = null;

        String n = null;



        		packageName = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:214:3: ( PACKAGE n= dotted_name[null] opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:214:3: PACKAGE n= dotted_name[null] opt_semicolon
            {
            match(input,PACKAGE,FOLLOW_PACKAGE_in_package_statement184); if (failed) return packageName;
            pushFollow(FOLLOW_dotted_name_in_package_statement188);
            n=dotted_name(null);
            _fsp--;
            if (failed) return packageName;
            pushFollow(FOLLOW_opt_semicolon_in_package_statement191);
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


    // $ANTLR start statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:219:1: statement : ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query );
    public final void statement() throws RecognitionException {
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:220:4: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )
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
                        new NoViableAltException("219:1: statement : ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query );", 8, 1, input);

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
                    new NoViableAltException("219:1: statement : ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query );", 8, 0, input);

                throw nvae;
            }

            switch (alt8) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:220:4: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement205);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:221:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement211);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:222:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement217);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:223:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement223);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:224:10: t= template
                    {
                    pushFollow(FOLLOW_template_in_statement237);
                    t=template();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addFactTemplate( t ); 
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:225:4: r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement246);
                    r=rule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       this.packageDescr.addRule( r ); 
                    }

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:226:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement258);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:231:1: import_statement : IMPORT import_name[importDecl] opt_semicolon ;
    public final void import_statement() throws RecognitionException {
        Token IMPORT1=null;


                	ImportDescr importDecl = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:235:4: ( IMPORT import_name[importDecl] opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:235:4: IMPORT import_name[importDecl] opt_semicolon
            {
            IMPORT1=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_import_statement287); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createImport( );
              	            importDecl.setStartCharacter( ((CommonToken)IMPORT1).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_import_statement310);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_import_statement313);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:246:1: function_import_statement : IMPORT FUNCTION import_name[importDecl] opt_semicolon ;
    public final void function_import_statement() throws RecognitionException {
        Token IMPORT2=null;


                	FunctionImportDescr importDecl = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:250:4: ( IMPORT FUNCTION import_name[importDecl] opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:250:4: IMPORT FUNCTION import_name[importDecl] opt_semicolon
            {
            IMPORT2=(Token)input.LT(1);
            match(input,IMPORT,FOLLOW_IMPORT_in_function_import_statement337); if (failed) return ;
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function_import_statement339); if (failed) return ;
            if ( backtracking==0 ) {

              	            importDecl = factory.createFunctionImport();
              	            importDecl.setStartCharacter( ((CommonToken)IMPORT2).getStartIndex() );
              		    if (packageDescr != null) {
              			packageDescr.addFunctionImport( importDecl );
              		    }
              	        
            }
            pushFollow(FOLLOW_import_name_in_function_import_statement362);
            import_name(importDecl);
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement365);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:262:1: import_name[ImportDescr importDecl] returns [String name] : ID ( DOT id= identifier )* (star= '.*' )? ;
    public final String import_name(ImportDescr importDecl) throws RecognitionException {
        String name = null;

        Token star=null;
        Token ID3=null;
        Token DOT4=null;
        identifier_return id = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:267:3: ( ID ( DOT id= identifier )* (star= '.*' )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:267:3: ID ( DOT id= identifier )* (star= '.*' )?
            {
            ID3=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name391); if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name =ID3.getText(); 
              		    importDecl.setTarget( name );
              		    importDecl.setEndCharacter( ((CommonToken)ID3).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:273:3: ( DOT id= identifier )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==DOT) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:273:5: DOT id= identifier
            	    {
            	    DOT4=(Token)input.LT(1);
            	    match(input,DOT,FOLLOW_DOT_in_import_name403); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_import_name407);
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

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:280:3: (star= '.*' )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==72) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:280:5: star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,72,FOLLOW_72_in_import_name431); if (failed) return name;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:290:1: global : GLOBAL type= dotted_name[null] id= identifier opt_semicolon ;
    public final void global() throws RecognitionException {
        Token GLOBAL5=null;
        String type = null;

        identifier_return id = null;



        	    GlobalDescr global = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:295:3: ( GLOBAL type= dotted_name[null] id= identifier opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:295:3: GLOBAL type= dotted_name[null] id= identifier opt_semicolon
            {
            GLOBAL5=(Token)input.LT(1);
            match(input,GLOBAL,FOLLOW_GLOBAL_in_global465); if (failed) return ;
            if ( backtracking==0 ) {

              		    global = factory.createGlobal();
              	            global.setStartCharacter( ((CommonToken)GLOBAL5).getStartIndex() );
              		    packageDescr.addGlobal( global );
              		
            }
            pushFollow(FOLLOW_dotted_name_in_global476);
            type=dotted_name(null);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		    global.setType( type );
              		
            }
            pushFollow(FOLLOW_identifier_in_global488);
            id=identifier();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_global490);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:313:1: function : FUNCTION (retType= dotted_name[null] )? id= identifier LEFT_PAREN ( (paramType= dotted_name[null] )? paramName= argument ( COMMA (paramType= dotted_name[null] )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk[f] ;
    public final void function() throws RecognitionException {
        Token FUNCTION6=null;
        String retType = null;

        identifier_return id = null;

        String paramType = null;

        String paramName = null;

        String body = null;



        		FunctionDescr f = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:318:3: ( FUNCTION (retType= dotted_name[null] )? id= identifier LEFT_PAREN ( (paramType= dotted_name[null] )? paramName= argument ( COMMA (paramType= dotted_name[null] )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk[f] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:318:3: FUNCTION (retType= dotted_name[null] )? id= identifier LEFT_PAREN ( (paramType= dotted_name[null] )? paramName= argument ( COMMA (paramType= dotted_name[null] )? paramName= argument )* )? RIGHT_PAREN body= curly_chunk[f]
            {
            FUNCTION6=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function515); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:318:19: (retType= dotted_name[null] )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0>=ATTRIBUTES && LA11_0<=ID)||LA11_0==GLOBAL||(LA11_0>=QUERY && LA11_0<=WHEN)||LA11_0==ENABLED||LA11_0==SALIENCE||LA11_0==DURATION||LA11_0==FROM||(LA11_0>=INIT && LA11_0<=RESULT)||(LA11_0>=CONTAINS && LA11_0<=IN)||LA11_0==NULL||LA11_0==THEN) ) {
                int LA11_1 = input.LA(2);

                if ( ((LA11_1>=ATTRIBUTES && LA11_1<=GLOBAL)||(LA11_1>=QUERY && LA11_1<=WHEN)||LA11_1==ENABLED||LA11_1==SALIENCE||LA11_1==DURATION||LA11_1==FROM||(LA11_1>=INIT && LA11_1<=RESULT)||(LA11_1>=CONTAINS && LA11_1<=IN)||LA11_1==NULL||LA11_1==LEFT_SQUARE||LA11_1==THEN) ) {
                    alt11=1;
                }
            }
            switch (alt11) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:318:19: retType= dotted_name[null]
                    {
                    pushFollow(FOLLOW_dotted_name_in_function519);
                    retType=dotted_name(null);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_identifier_in_function525);
            id=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			//System.err.println( "function :: " + n.getText() );
              			f = factory.createFunction( input.toString(id.start,id.stop), retType );
              			f.setLocation(offset(FUNCTION6.getLine()), FUNCTION6.getCharPositionInLine());
              	        	f.setStartCharacter( ((CommonToken)FUNCTION6).getStartIndex() );
              			packageDescr.addFunction( f );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function534); if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:4: ( (paramType= dotted_name[null] )? paramName= argument ( COMMA (paramType= dotted_name[null] )? paramName= argument )* )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( ((LA15_0>=ATTRIBUTES && LA15_0<=ID)||LA15_0==GLOBAL||(LA15_0>=QUERY && LA15_0<=WHEN)||LA15_0==ENABLED||LA15_0==SALIENCE||LA15_0==DURATION||LA15_0==FROM||(LA15_0>=INIT && LA15_0<=RESULT)||(LA15_0>=CONTAINS && LA15_0<=IN)||LA15_0==NULL||LA15_0==THEN) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:6: (paramType= dotted_name[null] )? paramName= argument ( COMMA (paramType= dotted_name[null] )? paramName= argument )*
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:15: (paramType= dotted_name[null] )?
                    int alt12=2;
                    alt12 = dfa12.predict(input);
                    switch (alt12) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:327:15: paramType= dotted_name[null]
                            {
                            pushFollow(FOLLOW_dotted_name_in_function543);
                            paramType=dotted_name(null);
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function549);
                    paramName=argument();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      					f.addParameter( paramType, paramName );
                      				
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:331:5: ( COMMA (paramType= dotted_name[null] )? paramName= argument )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( (LA14_0==COMMA) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:331:7: COMMA (paramType= dotted_name[null] )? paramName= argument
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_function563); if (failed) return ;
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:331:22: (paramType= dotted_name[null] )?
                    	    int alt13=2;
                    	    alt13 = dfa13.predict(input);
                    	    switch (alt13) {
                    	        case 1 :
                    	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:331:22: paramType= dotted_name[null]
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function567);
                    	            paramType=dotted_name(null);
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function573);
                    	    paramName=argument();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {

                    	      						f.addParameter( paramType, paramName );
                    	      					
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function597); if (failed) return ;
            pushFollow(FOLLOW_curly_chunk_in_function603);
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


    // $ANTLR start argument
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:345:1: argument returns [String name] : id= identifier ( '[' ']' )* ;
    public final String argument() throws RecognitionException {
        String name = null;

        identifier_return id = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:349:4: (id= identifier ( '[' ']' )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:349:4: id= identifier ( '[' ']' )*
            {
            pushFollow(FOLLOW_identifier_in_argument631);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               name =input.toString(id.start,id.stop); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:349:38: ( '[' ']' )*
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==LEFT_SQUARE) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:349:40: '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument637); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument639); if (failed) return name;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:354:1: query returns [QueryDescr query] : QUERY queryName= name ( LEFT_PAREN (paramName= ID ( ',' paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END ;
    public final QueryDescr query() throws RecognitionException {
        QueryDescr query = null;

        Token paramName=null;
        Token QUERY7=null;
        Token END8=null;
        String queryName = null;



        		query = null;
        		AndDescr lhs = null;
        		List params = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:361:3: ( QUERY queryName= name ( LEFT_PAREN (paramName= ID ( ',' paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:361:3: QUERY queryName= name ( LEFT_PAREN (paramName= ID ( ',' paramName= ID )* )? RIGHT_PAREN )? normal_lhs_block[lhs] END
            {
            QUERY7=(Token)input.LT(1);
            match(input,QUERY,FOLLOW_QUERY_in_query669); if (failed) return query;
            pushFollow(FOLLOW_name_in_query673);
            queryName=name();
            _fsp--;
            if (failed) return query;
            if ( backtracking==0 ) {
               
              			query = factory.createQuery( queryName ); 
              			query.setLocation( offset(QUERY7.getLine()), QUERY7.getCharPositionInLine() );
              			query.setStartCharacter( ((CommonToken)QUERY7).getStartIndex() );
              			lhs = new AndDescr(); query.setLhs( lhs ); 
              			lhs.setLocation( offset(QUERY7.getLine()), QUERY7.getCharPositionInLine() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:369:3: ( LEFT_PAREN (paramName= ID ( ',' paramName= ID )* )? RIGHT_PAREN )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==LEFT_PAREN) ) {
                int LA19_1 = input.LA(2);

                if ( (LA19_1==ID) ) {
                    int LA19_3 = input.LA(3);

                    if ( ((LA19_3>=COMMA && LA19_3<=RIGHT_PAREN)) ) {
                        alt19=1;
                    }
                }
                else if ( (LA19_1==RIGHT_PAREN) ) {
                    alt19=1;
                }
            }
            switch (alt19) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:369:5: LEFT_PAREN (paramName= ID ( ',' paramName= ID )* )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_query683); if (failed) return query;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:370:11: (paramName= ID ( ',' paramName= ID )* )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0==ID) ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:370:13: paramName= ID ( ',' paramName= ID )*
                            {
                            if ( backtracking==0 ) {
                               params = new ArrayList(); 
                            }
                            paramName=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_query715); if (failed) return query;
                            if ( backtracking==0 ) {
                               params.add( paramName.getText() ); 
                            }
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:372:15: ( ',' paramName= ID )*
                            loop17:
                            do {
                                int alt17=2;
                                int LA17_0 = input.LA(1);

                                if ( (LA17_0==COMMA) ) {
                                    alt17=1;
                                }


                                switch (alt17) {
                            	case 1 :
                            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:372:16: ',' paramName= ID
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_query763); if (failed) return query;
                            	    paramName=(Token)input.LT(1);
                            	    match(input,ID,FOLLOW_ID_in_query767); if (failed) return query;
                            	    if ( backtracking==0 ) {
                            	       params.add( paramName.getText() ); 
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop17;
                                }
                            } while (true);

                            if ( backtracking==0 ) {
                               query.setParameters( (String[]) params.toArray( new String[params.size()] ) ); 
                            }

                            }
                            break;

                    }

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_query815); if (failed) return query;

                    }
                    break;

            }

            pushFollow(FOLLOW_normal_lhs_block_in_query833);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;
            END8=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query838); if (failed) return query;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:385:1: template returns [FactTemplateDescr template] : TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ END opt_semicolon ;
    public final FactTemplateDescr template() throws RecognitionException {
        FactTemplateDescr template = null;

        Token TEMPLATE9=null;
        Token END10=null;
        identifier_return templateName = null;

        FieldTemplateDescr slot = null;



        		template = null;		
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:390:3: ( TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:390:3: TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ END opt_semicolon
            {
            TEMPLATE9=(Token)input.LT(1);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_template866); if (failed) return template;
            pushFollow(FOLLOW_identifier_in_template870);
            templateName=identifier();
            _fsp--;
            if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template872);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {

              			template = new FactTemplateDescr(input.toString(templateName.start,templateName.stop));
              			template.setLocation( offset(TEMPLATE9.getLine()), TEMPLATE9.getCharPositionInLine() );			
              			template.setStartCharacter( ((CommonToken)TEMPLATE9).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:396:3: (slot= template_slot )+
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
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:397:4: slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template887);
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
            match(input,END,FOLLOW_END_in_template902); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template904);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:408:1: template_slot returns [FieldTemplateDescr field] : fieldType= qualified_id[$field] id= identifier opt_semicolon ;
    public final FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr field = null;

        String fieldType = null;

        identifier_return id = null;



        		field = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:11: (fieldType= qualified_id[$field] id= identifier opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:413:11: fieldType= qualified_id[$field] id= identifier opt_semicolon
            {
            if ( backtracking==0 ) {

              			field = factory.createFieldTemplate();
              	         
            }
            pushFollow(FOLLOW_qualified_id_in_template_slot950);
            fieldType=qualified_id(field);
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {

              		        field.setClassType( fieldType );
              		 
            }
            pushFollow(FOLLOW_identifier_in_template_slot968);
            id=identifier();
            _fsp--;
            if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot970);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:429:1: rule returns [RuleDescr rule] : RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule] ;
    public final RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token RULE11=null;
        Token WHEN12=null;
        String ruleName = null;



        		rule = null;
        		AndDescr lhs = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:435:3: ( RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:435:3: RULE ruleName= name ( rule_attributes[$rule] )? ( WHEN ( ':' )? normal_lhs_block[lhs] )? rhs_chunk[$rule]
            {
            RULE11=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule1001); if (failed) return rule;
            pushFollow(FOLLOW_name_in_rule1005);
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:443:3: ( rule_attributes[$rule] )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==ATTRIBUTES||LA21_0==DATE_EFFECTIVE||(LA21_0>=DATE_EXPIRES && LA21_0<=ENABLED)||LA21_0==SALIENCE||(LA21_0>=NO_LOOP && LA21_0<=LOCK_ON_ACTIVE)) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:443:3: rule_attributes[$rule]
                    {
                    pushFollow(FOLLOW_rule_attributes_in_rule1014);
                    rule_attributes(rule);
                    _fsp--;
                    if (failed) return rule;

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:444:3: ( WHEN ( ':' )? normal_lhs_block[lhs] )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==WHEN) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:445:4: WHEN ( ':' )? normal_lhs_block[lhs]
                    {
                    WHEN12=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule1026); if (failed) return rule;
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:445:9: ( ':' )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0==71) ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:445:9: ':'
                            {
                            match(input,71,FOLLOW_71_in_rule1028); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
                      				lhs = new AndDescr(); rule.setLhs( lhs ); 
                      				lhs.setLocation( offset(WHEN12.getLine()), WHEN12.getCharPositionInLine() );
                      				lhs.setStartCharacter( ((CommonToken)WHEN12).getStartIndex() );
                      			
                    }
                    pushFollow(FOLLOW_normal_lhs_block_in_rule1039);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1049);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:459:1: rule_attributes[RuleDescr rule] : ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )* ;
    public final void rule_attributes(RuleDescr rule) throws RecognitionException {
        AttributeDescr attr = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:2: ( ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:2: ( ATTRIBUTES ':' )? attr= rule_attribute ( ( ',' )? attr= rule_attribute )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:2: ( ATTRIBUTES ':' )?
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0==ATTRIBUTES) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:461:4: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_rule_attributes1069); if (failed) return ;
                    match(input,71,FOLLOW_71_in_rule_attributes1071); if (failed) return ;

                    }
                    break;

            }

            pushFollow(FOLLOW_rule_attribute_in_rule_attributes1079);
            attr=rule_attribute();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               rule.addAttribute( attr ); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:463:2: ( ( ',' )? attr= rule_attribute )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==COMMA||LA26_0==DATE_EFFECTIVE||(LA26_0>=DATE_EXPIRES && LA26_0<=ENABLED)||LA26_0==SALIENCE||(LA26_0>=NO_LOOP && LA26_0<=LOCK_ON_ACTIVE)) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:463:4: ( ',' )? attr= rule_attribute
            	    {
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:463:4: ( ',' )?
            	    int alt25=2;
            	    int LA25_0 = input.LA(1);

            	    if ( (LA25_0==COMMA) ) {
            	        alt25=1;
            	    }
            	    switch (alt25) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:463:4: ','
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1086); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1091);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:468:1: rule_attribute returns [AttributeDescr attr] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr attr = null;

        AttributeDescr a = null;



        		attr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:475:4: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect )
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
                    new NoViableAltException("468:1: rule_attribute returns [AttributeDescr attr] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:475:4: a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute1128);
                    a=salience();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:476:4: a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute1136);
                    a=no_loop();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:477:4: a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1145);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:478:4: a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute1154);
                    a=duration();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:479:4: a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute1163);
                    a=activation_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:480:4: a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1171);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:481:4: a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute1179);
                    a=date_effective();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:4: a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute1187);
                    a=date_expires();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:483:4: a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute1195);
                    a=enabled();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:4: a= ruleflow_group
                    {
                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1203);
                    a=ruleflow_group();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:4: a= lock_on_active
                    {
                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1211);
                    a=lock_on_active();
                    _fsp--;
                    if (failed) return attr;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:486:4: a= dialect
                    {
                    pushFollow(FOLLOW_dialect_in_rule_attribute1218);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:489:1: date_effective returns [AttributeDescr descr] : DATE_EFFECTIVE STRING ;
    public final AttributeDescr date_effective() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING13=null;
        Token DATE_EFFECTIVE14=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:3: ( DATE_EFFECTIVE STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:3: DATE_EFFECTIVE STRING
            {
            DATE_EFFECTIVE14=(Token)input.LT(1);
            match(input,DATE_EFFECTIVE,FOLLOW_DATE_EFFECTIVE_in_date_effective1244); if (failed) return descr;
            STRING13=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1246); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:504:1: date_expires returns [AttributeDescr descr] : DATE_EXPIRES STRING ;
    public final AttributeDescr date_expires() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING15=null;
        Token DATE_EXPIRES16=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:508:4: ( DATE_EXPIRES STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:508:4: DATE_EXPIRES STRING
            {
            DATE_EXPIRES16=(Token)input.LT(1);
            match(input,DATE_EXPIRES,FOLLOW_DATE_EXPIRES_in_date_expires1275); if (failed) return descr;
            STRING15=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1277); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:517:1: enabled returns [AttributeDescr descr] : ENABLED BOOL ;
    public final AttributeDescr enabled() throws RecognitionException {
        AttributeDescr descr = null;

        Token BOOL17=null;
        Token ENABLED18=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:5: ( ENABLED BOOL )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:521:5: ENABLED BOOL
            {
            ENABLED18=(Token)input.LT(1);
            match(input,ENABLED,FOLLOW_ENABLED_in_enabled1306); if (failed) return descr;
            BOOL17=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1308); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:530:1: salience returns [AttributeDescr descr] : SALIENCE ( INT | txt= paren_chunk[$descr] ) ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr descr = null;

        Token SALIENCE19=null;
        Token INT20=null;
        String txt = null;



        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:3: ( SALIENCE ( INT | txt= paren_chunk[$descr] ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:535:3: SALIENCE ( INT | txt= paren_chunk[$descr] )
            {
            SALIENCE19=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience1341); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "salience" );
              			descr.setLocation( offset(SALIENCE19.getLine()), SALIENCE19.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)SALIENCE19).getStartIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:541:3: ( INT | txt= paren_chunk[$descr] )
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
                    new NoViableAltException("541:3: ( INT | txt= paren_chunk[$descr] )", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:541:5: INT
                    {
                    INT20=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_salience1352); if (failed) return descr;
                    if ( backtracking==0 ) {

                      			descr.setValue( INT20.getText() );
                      			descr.setEndCharacter( ((CommonToken)INT20).getStopIndex() );
                      		
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:546:5: txt= paren_chunk[$descr]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1367);
                    txt=paren_chunk(descr);
                    _fsp--;
                    if (failed) return descr;
                    if ( backtracking==0 ) {

                      			descr.setValue( txt );
                      		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:553:1: no_loop returns [AttributeDescr descr] : NO_LOOP ( BOOL )? ;
    public final AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr descr = null;

        Token NO_LOOP21=null;
        Token BOOL22=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:557:4: ( NO_LOOP ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:557:4: NO_LOOP ( BOOL )?
            {
            NO_LOOP21=(Token)input.LT(1);
            match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1398); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "no-loop", "true" );
              			descr.setLocation( offset(NO_LOOP21.getLine()), NO_LOOP21.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)NO_LOOP21).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)NO_LOOP21).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:3: ( BOOL )?
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==BOOL) ) {
                alt29=1;
            }
            switch (alt29) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:564:5: BOOL
                    {
                    BOOL22=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1411); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:572:1: auto_focus returns [AttributeDescr descr] : AUTO_FOCUS ( BOOL )? ;
    public final AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr descr = null;

        Token AUTO_FOCUS23=null;
        Token BOOL24=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:576:4: ( AUTO_FOCUS ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:576:4: AUTO_FOCUS ( BOOL )?
            {
            AUTO_FOCUS23=(Token)input.LT(1);
            match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1446); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "auto-focus", "true" );
              			descr.setLocation( offset(AUTO_FOCUS23.getLine()), AUTO_FOCUS23.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)AUTO_FOCUS23).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)AUTO_FOCUS23).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:3: ( BOOL )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==BOOL) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:583:5: BOOL
                    {
                    BOOL24=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1459); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:591:1: activation_group returns [AttributeDescr descr] : ACTIVATION_GROUP STRING ;
    public final AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING25=null;
        Token ACTIVATION_GROUP26=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:595:4: ( ACTIVATION_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:595:4: ACTIVATION_GROUP STRING
            {
            ACTIVATION_GROUP26=(Token)input.LT(1);
            match(input,ACTIVATION_GROUP,FOLLOW_ACTIVATION_GROUP_in_activation_group1495); if (failed) return descr;
            STRING25=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1497); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:604:1: ruleflow_group returns [AttributeDescr descr] : RULEFLOW_GROUP STRING ;
    public final AttributeDescr ruleflow_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING27=null;
        Token RULEFLOW_GROUP28=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:608:4: ( RULEFLOW_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:608:4: RULEFLOW_GROUP STRING
            {
            RULEFLOW_GROUP28=(Token)input.LT(1);
            match(input,RULEFLOW_GROUP,FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1525); if (failed) return descr;
            STRING27=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1527); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:617:1: agenda_group returns [AttributeDescr descr] : AGENDA_GROUP STRING ;
    public final AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING29=null;
        Token AGENDA_GROUP30=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:621:4: ( AGENDA_GROUP STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:621:4: AGENDA_GROUP STRING
            {
            AGENDA_GROUP30=(Token)input.LT(1);
            match(input,AGENDA_GROUP,FOLLOW_AGENDA_GROUP_in_agenda_group1555); if (failed) return descr;
            STRING29=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1557); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:630:1: duration returns [AttributeDescr descr] : DURATION INT ;
    public final AttributeDescr duration() throws RecognitionException {
        AttributeDescr descr = null;

        Token INT31=null;
        Token DURATION32=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:4: ( DURATION INT )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:634:4: DURATION INT
            {
            DURATION32=(Token)input.LT(1);
            match(input,DURATION,FOLLOW_DURATION_in_duration1585); if (failed) return descr;
            INT31=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1587); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:643:1: dialect returns [AttributeDescr descr] : DIALECT STRING ;
    public final AttributeDescr dialect() throws RecognitionException {
        AttributeDescr descr = null;

        Token STRING33=null;
        Token DIALECT34=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:647:4: ( DIALECT STRING )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:647:4: DIALECT STRING
            {
            DIALECT34=(Token)input.LT(1);
            match(input,DIALECT,FOLLOW_DIALECT_in_dialect1615); if (failed) return descr;
            STRING33=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1617); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:656:1: lock_on_active returns [AttributeDescr descr] : LOCK_ON_ACTIVE ( BOOL )? ;
    public final AttributeDescr lock_on_active() throws RecognitionException {
        AttributeDescr descr = null;

        Token LOCK_ON_ACTIVE35=null;
        Token BOOL36=null;


        		descr = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:660:4: ( LOCK_ON_ACTIVE ( BOOL )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:660:4: LOCK_ON_ACTIVE ( BOOL )?
            {
            LOCK_ON_ACTIVE35=(Token)input.LT(1);
            match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1649); if (failed) return descr;
            if ( backtracking==0 ) {

              			descr = new AttributeDescr( "lock-on-active", "true" );
              			descr.setLocation( offset(LOCK_ON_ACTIVE35.getLine()), LOCK_ON_ACTIVE35.getCharPositionInLine() );
              			descr.setStartCharacter( ((CommonToken)LOCK_ON_ACTIVE35).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)LOCK_ON_ACTIVE35).getStopIndex() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:667:3: ( BOOL )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==BOOL) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:667:5: BOOL
                    {
                    BOOL36=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1662); if (failed) return descr;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:675:1: normal_lhs_block[AndDescr descr] : (d= lhs[$descr] )* ;
    public final void normal_lhs_block(AndDescr descr) throws RecognitionException {
        BaseDescr d = null;


        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:677:3: ( (d= lhs[$descr] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:677:3: (d= lhs[$descr] )*
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:677:3: (d= lhs[$descr] )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);

                if ( (LA32_0==ID||LA32_0==LEFT_PAREN||(LA32_0>=EXISTS && LA32_0<=FORALL)) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:677:5: d= lhs[$descr]
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1695);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:683:1: lhs[ConditionalElementDescr ce] returns [BaseDescr d] : l= lhs_or ;
    public final BaseDescr lhs(ConditionalElementDescr ce) throws RecognitionException {
        BaseDescr d = null;

        BaseDescr l = null;



        		d =null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:687:4: (l= lhs_or )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:687:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1732);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:691:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );
    public final BaseDescr lhs_or() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr lhsand = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		OrDescr or = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:696:4: ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* )
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
                        new NoViableAltException("691:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );", 35, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA35_0==ID||(LA35_0>=EXISTS && LA35_0<=FORALL)) ) {
                alt35=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("691:1: lhs_or returns [BaseDescr d] : ( LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN | left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )* );", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:696:4: LEFT_PAREN OR (lhsand= lhs_and )+ RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_or1757); if (failed) return d;
                    match(input,OR,FOLLOW_OR_in_lhs_or1759); if (failed) return d;
                    if ( backtracking==0 ) {

                      			or = new OrDescr();
                      			d = or;
                      			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:702:9: (lhsand= lhs_and )+
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:702:9: lhsand= lhs_and
                    	    {
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1770);
                    	    lhsand=lhs_and();
                    	    _fsp--;
                    	    if (failed) return d;

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

                    if ( backtracking==0 ) {

                      			or.addDescr( lhsand );
                      		
                    }
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_or1780); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:708:10: left= lhs_and ( ( OR | DOUBLE_PIPE ) right= lhs_and )*
                    {
                    pushFollow(FOLLOW_lhs_and_in_lhs_or1798);
                    left=lhs_and();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = left; 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:709:3: ( ( OR | DOUBLE_PIPE ) right= lhs_and )*
                    loop34:
                    do {
                        int alt34=2;
                        int LA34_0 = input.LA(1);

                        if ( ((LA34_0>=OR && LA34_0<=DOUBLE_PIPE)) ) {
                            alt34=1;
                        }


                        switch (alt34) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:709:5: ( OR | DOUBLE_PIPE ) right= lhs_and
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or1806);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                    	      			
                    	    }
                    	    pushFollow(FOLLOW_lhs_and_in_lhs_or1822);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:726:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );
    public final BaseDescr lhs_and() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr lhsunary = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		AndDescr and = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:4: ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* )
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
                        new NoViableAltException("726:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );", 38, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA38_0==ID||(LA38_0>=EXISTS && LA38_0<=FORALL)) ) {
                alt38=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("726:1: lhs_and returns [BaseDescr d] : ( LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN | left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* );", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:731:4: LEFT_PAREN AND (lhsunary= lhs_unary )+ RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_and1853); if (failed) return d;
                    match(input,AND,FOLLOW_AND_in_lhs_and1855); if (failed) return d;
                    if ( backtracking==0 ) {

                      			and = new AndDescr();
                      			d = and;
                      			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                      		
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:737:11: (lhsunary= lhs_unary )+
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
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:737:11: lhsunary= lhs_unary
                    	    {
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and1866);
                    	    lhsunary=lhs_unary();
                    	    _fsp--;
                    	    if (failed) return d;

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

                    if ( backtracking==0 ) {

                      			and.addDescr( lhsunary );
                      		
                    }
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_and1876); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:743:10: left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
                    {
                    pushFollow(FOLLOW_lhs_unary_in_lhs_and1894);
                    left=lhs_unary();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = left; 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:3: ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
                    loop37:
                    do {
                        int alt37=2;
                        int LA37_0 = input.LA(1);

                        if ( ((LA37_0>=AND && LA37_0<=DOUBLE_AMPER)) ) {
                            alt37=1;
                        }


                        switch (alt37) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:744:5: ( AND | DOUBLE_AMPER ) right= lhs_unary
                    	    {
                    	    if ( (input.LA(1)>=AND && input.LA(1)<=DOUBLE_AMPER) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and1902);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
                    	      			
                    	    }
                    	    pushFollow(FOLLOW_lhs_unary_in_lhs_and1918);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:761:1: lhs_unary returns [BaseDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? | u= lhs_forall | LEFT_PAREN u= lhs_or RIGHT_PAREN ) opt_semicolon ;
    public final BaseDescr lhs_unary() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:765:4: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? | u= lhs_forall | LEFT_PAREN u= lhs_or RIGHT_PAREN ) opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:765:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? | u= lhs_forall | LEFT_PAREN u= lhs_or RIGHT_PAREN ) opt_semicolon
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:765:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? | u= lhs_forall | LEFT_PAREN u= lhs_or RIGHT_PAREN )
            int alt41=6;
            switch ( input.LA(1) ) {
            case EXISTS:
                {
                alt41=1;
                }
                break;
            case NOT:
                {
                alt41=2;
                }
                break;
            case EVAL:
                {
                alt41=3;
                }
                break;
            case ID:
                {
                alt41=4;
                }
                break;
            case FORALL:
                {
                alt41=5;
                }
                break;
            case LEFT_PAREN:
                {
                alt41=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("765:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )? | u= lhs_forall | LEFT_PAREN u= lhs_or RIGHT_PAREN )", 41, 0, input);

                throw nvae;
            }

            switch (alt41) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:765:6: u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary1955);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:766:5: u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary1965);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:767:5: u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary1975);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:768:5: u= lhs_pattern ( FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )?
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_unary1985);
                    u=lhs_pattern();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:768:34: ( FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) ) )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);

                    if ( (LA40_0==FROM) ) {
                        alt40=1;
                    }
                    switch (alt40) {
                        case 1 :
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:769:13: FROM ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )
                            {
                            match(input,FROM,FOLLOW_FROM_in_lhs_unary2003); if (failed) return d;
                            if ( backtracking==0 ) {

                              				location.setType(Location.LOCATION_LHS_FROM);
                              				location.setProperty(Location.LOCATION_FROM_CONTENT, "");
                              		          
                            }
                            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:774:13: ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )
                            int alt39=3;
                            switch ( input.LA(1) ) {
                            case ACCUMULATE:
                                {
                                alt39=1;
                                }
                                break;
                            case COLLECT:
                                {
                                alt39=2;
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
                            case RESULT:
                            case CONTAINS:
                            case EXCLUDES:
                            case MATCHES:
                            case MEMBEROF:
                            case IN:
                            case NULL:
                            case THEN:
                                {
                                alt39=3;
                                }
                                break;
                            default:
                                if (backtracking>0) {failed=true; return d;}
                                NoViableAltException nvae =
                                    new NoViableAltException("774:13: ( options {k=1; } : (ac= accumulate_statement ) | (cs= collect_statement ) | (fm= from_statement ) )", 39, 0, input);

                                throw nvae;
                            }

                            switch (alt39) {
                                case 1 :
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:775:15: (ac= accumulate_statement )
                                    {
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:775:15: (ac= accumulate_statement )
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:775:17: ac= accumulate_statement
                                    {
                                    pushFollow(FOLLOW_accumulate_statement_in_lhs_unary2063);
                                    ac=accumulate_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                       ac.setResultPattern((PatternDescr) u); d =ac; 
                                    }

                                    }


                                    }
                                    break;
                                case 2 :
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:776:15: (cs= collect_statement )
                                    {
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:776:15: (cs= collect_statement )
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:776:17: cs= collect_statement
                                    {
                                    pushFollow(FOLLOW_collect_statement_in_lhs_unary2086);
                                    cs=collect_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                       cs.setResultPattern((PatternDescr) u); d =cs; 
                                    }

                                    }


                                    }
                                    break;
                                case 3 :
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:777:15: (fm= from_statement )
                                    {
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:777:15: (fm= from_statement )
                                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:777:17: fm= from_statement
                                    {
                                    pushFollow(FOLLOW_from_statement_in_lhs_unary2110);
                                    fm=from_statement();
                                    _fsp--;
                                    if (failed) return d;
                                    if ( backtracking==0 ) {
                                      fm.setPattern((PatternDescr) u); d =fm; 
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:780:5: u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary2149);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:781:5: LEFT_PAREN u= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2158); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary2162);
                    u=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2164); if (failed) return d;
                    if ( backtracking==0 ) {
                       d = u; 
                    }

                    }
                    break;

            }

            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary2175);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:786:1: lhs_exist returns [BaseDescr d] : EXISTS ( ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) ;
    public final BaseDescr lhs_exist() throws RecognitionException {
        BaseDescr d = null;

        Token EXISTS37=null;
        Token RIGHT_PAREN38=null;
        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:790:4: ( EXISTS ( ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:790:4: EXISTS ( ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            {
            EXISTS37=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_lhs_exist2197); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new ExistsDescr( ); 
              			d.setLocation( offset(EXISTS37.getLine()), EXISTS37.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)EXISTS37).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:797:10: ( ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
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
                    new NoViableAltException("797:10: ( ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:797:12: ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:797:12: ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:797:14: LEFT_PAREN pattern= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist2217); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist2221);
                    pattern=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( pattern != null ) ((ExistsDescr)d).addDescr( pattern ); 
                    }
                    RIGHT_PAREN38=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist2251); if (failed) return d;
                    if ( backtracking==0 ) {
                       d.setEndCharacter( ((CommonToken)RIGHT_PAREN38).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:802:12: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist2301);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:812:1: lhs_not returns [NotDescr d] : NOT ( ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) ;
    public final NotDescr lhs_not() throws RecognitionException {
        NotDescr d = null;

        Token NOT39=null;
        Token RIGHT_PAREN40=null;
        BaseDescr pattern = null;



        		d = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:816:4: ( NOT ( ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:816:4: NOT ( ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
            {
            NOT39=(Token)input.LT(1);
            match(input,NOT,FOLLOW_NOT_in_lhs_not2353); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new NotDescr( ); 
              			d.setLocation( offset(NOT39.getLine()), NOT39.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)NOT39).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:3: ( ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )
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
                    new NoViableAltException("823:3: ( ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN ) | pattern= lhs_pattern )", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:5: ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN )
                    {
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:5: ( LEFT_PAREN pattern= lhs_or RIGHT_PAREN )
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:823:7: LEFT_PAREN pattern= lhs_or RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not2366); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not2370);
                    pattern=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( pattern != null ) d.addDescr( pattern ); 
                    }
                    RIGHT_PAREN40=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not2401); if (failed) return d;
                    if ( backtracking==0 ) {
                       d.setEndCharacter( ((CommonToken)RIGHT_PAREN40).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:829:3: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not2438);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:839:1: lhs_eval returns [BaseDescr d] : EVAL c= paren_chunk[$d] ;
    public final BaseDescr lhs_eval() throws RecognitionException {
        BaseDescr d = null;

        Token EVAL41=null;
        String c = null;



        		d = new EvalDescr( );
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:844:3: ( EVAL c= paren_chunk[$d] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:844:3: EVAL c= paren_chunk[$d]
            {
            EVAL41=(Token)input.LT(1);
            match(input,EVAL,FOLLOW_EVAL_in_lhs_eval2484); if (failed) return d;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_LHS_INSIDE_EVAL );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2495);
            c=paren_chunk(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setStartCharacter( ((CommonToken)EVAL41).getStartIndex() );
              		        if( c != null ) {
              	  		    this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              		            String body = c.length() > 1 ? c.substring(1, c.length()-1) : "";
              			    checkTrailingSemicolon( body, offset(EVAL41.getLine()) );
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:861:1: lhs_forall returns [ForallDescr d] : FORALL LEFT_PAREN base= lhs_pattern ( ( COMMA )? pattern= lhs_pattern )+ RIGHT_PAREN ;
    public final ForallDescr lhs_forall() throws RecognitionException {
        ForallDescr d = null;

        Token FORALL42=null;
        Token RIGHT_PAREN43=null;
        BaseDescr base = null;

        BaseDescr pattern = null;



        		d = factory.createForall();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:865:4: ( FORALL LEFT_PAREN base= lhs_pattern ( ( COMMA )? pattern= lhs_pattern )+ RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:865:4: FORALL LEFT_PAREN base= lhs_pattern ( ( COMMA )? pattern= lhs_pattern )+ RIGHT_PAREN
            {
            FORALL42=(Token)input.LT(1);
            match(input,FORALL,FOLLOW_FORALL_in_lhs_forall2522); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall2524); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2528);
            base=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			d.setStartCharacter( ((CommonToken)FORALL42).getStartIndex() );
              		        // adding the base pattern
              		        d.addDescr( base );
              			d.setLocation( offset(FORALL42.getLine()), FORALL42.getCharPositionInLine() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:872:3: ( ( COMMA )? pattern= lhs_pattern )+
            int cnt45=0;
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( (LA45_0==ID||LA45_0==COMMA) ) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:872:5: ( COMMA )? pattern= lhs_pattern
            	    {
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:872:5: ( COMMA )?
            	    int alt44=2;
            	    int LA44_0 = input.LA(1);

            	    if ( (LA44_0==COMMA) ) {
            	        alt44=1;
            	    }
            	    switch (alt44) {
            	        case 1 :
            	            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:872:6: COMMA
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_lhs_forall2542); if (failed) return d;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall2548);
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
            	    if ( cnt45 >= 1 ) break loop45;
            	    if (backtracking>0) {failed=true; return d;}
                        EarlyExitException eee =
                            new EarlyExitException(45, input);
                        throw eee;
                }
                cnt45++;
            } while (true);

            RIGHT_PAREN43=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall2561); if (failed) return d;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:884:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr f = null;



        		d =null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:891:4: (f= fact_binding | f= fact[null] )
            int alt46=2;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==ID) ) {
                int LA46_1 = input.LA(2);

                if ( (LA46_1==71) ) {
                    alt46=1;
                }
                else if ( (LA46_1==DOT||LA46_1==LEFT_PAREN||LA46_1==LEFT_SQUARE) ) {
                    alt46=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("884:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );", 46, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("884:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact[null] );", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:891:4: f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern2594);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:892:4: f= fact[null]
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern2602);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:895:1: from_statement returns [FromDescr d] : ds= from_source[$d] ;
    public final FromDescr from_statement() throws RecognitionException {
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;



        		d =factory.createFrom();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:900:2: (ds= from_source[$d] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:900:2: ds= from_source[$d]
            {
            pushFollow(FOLLOW_from_source_in_from_statement2629);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:906:1: from_source[FromDescr from] returns [DeclarativeInvokerDescr ds] : ident= identifier ( options {k=1; } : args= paren_chunk[$from] )? ( expression_chain[$from, ad] )? ;
    public final DeclarativeInvokerDescr from_source(FromDescr from) throws RecognitionException {
        DeclarativeInvokerDescr ds = null;

        identifier_return ident = null;

        String args = null;



        		ds = null;
        		AccessorDescr ad = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:911:4: (ident= identifier ( options {k=1; } : args= paren_chunk[$from] )? ( expression_chain[$from, ad] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:911:4: ident= identifier ( options {k=1; } : args= paren_chunk[$from] )? ( expression_chain[$from, ad] )?
            {
            pushFollow(FOLLOW_identifier_in_from_source2658);
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:920:3: ( options {k=1; } : args= paren_chunk[$from] )?
            int alt47=2;
            int LA47_0 = input.LA(1);

            if ( (LA47_0==LEFT_PAREN) ) {
                alt47=1;
            }
            switch (alt47) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:927:5: args= paren_chunk[$from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source2686);
                    args=paren_chunk(from);
                    _fsp--;
                    if (failed) return ds;
                    if ( backtracking==0 ) {

                      			if( args != null ) {
                      				ad.setVariableName( null );
                      				FunctionCallDescr fc = new FunctionCallDescr(((Token)ident.start).getText());
                      				fc.setLocation( offset(((Token)ident.start).getLine()), ((Token)ident.start).getCharPositionInLine() );			
                      				fc.setArguments(args);
                      				fc.setStartCharacter( ((CommonToken)((Token)ident.start)).getStartIndex() );
                      				fc.setEndCharacter( ((CommonToken)((Token)ident.start)).getStopIndex() );
                      				ad.addInvoker(fc);
                      				location.setProperty(Location.LOCATION_FROM_CONTENT, args);
                      			}
                      		
                    }

                    }
                    break;

            }

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:941:3: ( expression_chain[$from, ad] )?
            int alt48=2;
            int LA48_0 = input.LA(1);

            if ( (LA48_0==DOT) ) {
                alt48=1;
            }
            switch (alt48) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:941:3: expression_chain[$from, ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source2700);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:949:1: expression_chain[FromDescr from, AccessorDescr as] : ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[$from] | ( LEFT_PAREN )=>paarg= paren_chunk[$from] )? ( expression_chain[from, as] )? ) ;
    public final void expression_chain(FromDescr from, AccessorDescr as) throws RecognitionException {
        identifier_return field = null;

        String sqarg = null;

        String paarg = null;



          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:955:2: ( ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[$from] | ( LEFT_PAREN )=>paarg= paren_chunk[$from] )? ( expression_chain[from, as] )? ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:955:2: ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[$from] | ( LEFT_PAREN )=>paarg= paren_chunk[$from] )? ( expression_chain[from, as] )? )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:955:2: ( DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[$from] | ( LEFT_PAREN )=>paarg= paren_chunk[$from] )? ( expression_chain[from, as] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:955:4: DOT field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[$from] | ( LEFT_PAREN )=>paarg= paren_chunk[$from] )? ( expression_chain[from, as] )?
            {
            match(input,DOT,FOLLOW_DOT_in_expression_chain2729); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_expression_chain2733);
            field=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              	        fa = new FieldAccessDescr(((Token)field.start).getText());	
              		fa.setLocation( offset(((Token)field.start).getLine()), ((Token)field.start).getCharPositionInLine() );
              		fa.setStartCharacter( ((CommonToken)((Token)field.start)).getStartIndex() );
              		fa.setEndCharacter( ((CommonToken)((Token)field.start)).getStopIndex() );
              	    
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:962:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk[$from] | ( LEFT_PAREN )=>paarg= paren_chunk[$from] )?
            int alt49=3;
            alt49 = dfa49.predict(input);
            switch (alt49) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:6: ( LEFT_SQUARE )=>sqarg= square_chunk[$from]
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain2764);
                    sqarg=square_chunk(from);
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      	          fa.setArgument( sqarg );	
                      	      
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:968:6: ( LEFT_PAREN )=>paarg= paren_chunk[$from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain2798);
                    paarg=paren_chunk(from);
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      	    	  ma = new MethodAccessDescr( ((Token)field.start).getText(), paarg );	
                      		  ma.setLocation( offset(((Token)field.start).getLine()), ((Token)field.start).getCharPositionInLine() );
                      		  ma.setStartCharacter( ((CommonToken)((Token)field.start)).getStartIndex() );
                      		
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:982:4: ( expression_chain[from, as] )?
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==DOT) ) {
                alt50=1;
            }
            switch (alt50) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:982:4: expression_chain[from, as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain2819);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:986:1: accumulate_statement returns [AccumulateDescr d] : ACCUMULATE LEFT_PAREN pattern= lhs_pattern ( COMMA )? INIT text= paren_chunk[null] ( COMMA )? ACTION text= paren_chunk[null] ( COMMA )? RESULT text= paren_chunk[null] RIGHT_PAREN ;
    public final AccumulateDescr accumulate_statement() throws RecognitionException {
        AccumulateDescr d = null;

        Token ACCUMULATE44=null;
        Token RIGHT_PAREN45=null;
        BaseDescr pattern = null;

        String text = null;



        		d = factory.createAccumulate();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:991:10: ( ACCUMULATE LEFT_PAREN pattern= lhs_pattern ( COMMA )? INIT text= paren_chunk[null] ( COMMA )? ACTION text= paren_chunk[null] ( COMMA )? RESULT text= paren_chunk[null] RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:991:10: ACCUMULATE LEFT_PAREN pattern= lhs_pattern ( COMMA )? INIT text= paren_chunk[null] ( COMMA )? ACTION text= paren_chunk[null] ( COMMA )? RESULT text= paren_chunk[null] RIGHT_PAREN
            {
            ACCUMULATE44=(Token)input.LT(1);
            match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement2858); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(ACCUMULATE44.getLine()), ACCUMULATE44.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)ACCUMULATE44).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2868); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_accumulate_statement2872);
            pattern=lhs_pattern();
            _fsp--;
            if (failed) return d;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:997:34: ( COMMA )?
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==COMMA) ) {
                alt51=1;
            }
            switch (alt51) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:997:34: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2874); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {

              		        d.setSourcePattern( (PatternDescr) pattern );
              		
            }
            match(input,INIT,FOLLOW_INIT_in_accumulate_statement2884); if (failed) return d;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_INIT );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2895);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1005:26: ( COMMA )?
            int alt52=2;
            int LA52_0 = input.LA(1);

            if ( (LA52_0==COMMA) ) {
                alt52=1;
            }
            switch (alt52) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1005:26: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2898); if (failed) return d;

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
            match(input,ACTION,FOLLOW_ACTION_in_accumulate_statement2907); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2911);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1013:33: ( COMMA )?
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==COMMA) ) {
                alt53=1;
            }
            switch (alt53) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1013:33: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2914); if (failed) return d;

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
            match(input,RESULT,FOLLOW_RESULT_in_accumulate_statement2923); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2927);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			if( text != null ) {
              			        d.setResultCode( text.substring(1, text.length()-1) );
              				location.setProperty(Location.LOCATION_PROPERTY_FROM_ACCUMULATE_RESULT_CONTENT, d.getResultCode());
              			}
              		
            }
            RIGHT_PAREN45=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2937); if (failed) return d;
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


    // $ANTLR start collect_statement
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1035:1: collect_statement returns [CollectDescr d] : COLLECT LEFT_PAREN pattern= lhs_pattern RIGHT_PAREN ;
    public final CollectDescr collect_statement() throws RecognitionException {
        CollectDescr d = null;

        Token COLLECT46=null;
        Token RIGHT_PAREN47=null;
        BaseDescr pattern = null;



        		d = factory.createCollect();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:10: ( COLLECT LEFT_PAREN pattern= lhs_pattern RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1040:10: COLLECT LEFT_PAREN pattern= lhs_pattern RIGHT_PAREN
            {
            COLLECT46=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement2978); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(COLLECT46.getLine()), COLLECT46.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)COLLECT46).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_COLLECT );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement2988); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_collect_statement2992);
            pattern=lhs_pattern();
            _fsp--;
            if (failed) return d;
            RIGHT_PAREN47=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement2994); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setSourcePattern( (PatternDescr)pattern );
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1054:1: fact_binding returns [BaseDescr d] : ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN ) ;
    public final BaseDescr fact_binding() throws RecognitionException {
        BaseDescr d = null;

        Token ID48=null;
        BaseDescr fe = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d =null;
        		OrDescr or = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1060:4: ( ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1060:4: ID ':' (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )
            {
            ID48=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding3026); if (failed) return d;
            match(input,71,FOLLOW_71_in_fact_binding3028); if (failed) return d;
            if ( backtracking==0 ) {

               		        // handling incomplete parsing
               		        d = new PatternDescr( );
               		        ((PatternDescr) d).setIdentifier( ID48.getText() );
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1066:3: (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )
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
                    new NoViableAltException("1066:3: (fe= fact[$ID.text] | LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN )", 55, 0, input);

                throw nvae;
            }
            switch (alt55) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1066:5: fe= fact[$ID.text]
                    {
                    pushFollow(FOLLOW_fact_in_fact_binding3042);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1075:4: LEFT_PAREN left= fact[$ID.text] ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )* RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_binding3058); if (failed) return d;
                    pushFollow(FOLLOW_fact_in_fact_binding3062);
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1083:4: ( ( OR | DOUBLE_PIPE ) right= fact[$ID.text] )*
                    loop54:
                    do {
                        int alt54=2;
                        int LA54_0 = input.LA(1);

                        if ( ((LA54_0>=OR && LA54_0<=DOUBLE_PIPE)) ) {
                            alt54=1;
                        }


                        switch (alt54) {
                    	case 1 :
                    	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1083:6: ( OR | DOUBLE_PIPE ) right= fact[$ID.text]
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return d;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_binding3075);    throw mse;
                    	    }

                    	    pushFollow(FOLLOW_fact_in_fact_binding3087);
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
                    	    break loop54;
                        }
                    } while (true);

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_binding3105); if (failed) return d;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1098:1: fact[String ident] returns [BaseDescr d] : id= qualified_id[$d] LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN ;
    public final BaseDescr fact(String ident) throws RecognitionException {
        BaseDescr d = null;

        Token LEFT_PAREN49=null;
        Token RIGHT_PAREN50=null;
        String id = null;



        		d =null;
        		PatternDescr pattern = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1104:11: (id= qualified_id[$d] LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1104:11: id= qualified_id[$d] LEFT_PAREN ( constraints[pattern] )? RIGHT_PAREN
            {
            if ( backtracking==0 ) {

               			pattern = new PatternDescr( );
               			if( ident != null ) {
               				pattern.setIdentifier( ident );
               			}
               			d = pattern; 
               	        
            }
            pushFollow(FOLLOW_qualified_id_in_fact3160);
            id=qualified_id(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               		        pattern.setObjectType( id );
               		        pattern.setEndCharacter( -1 );
               		
            }
            LEFT_PAREN49=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact3172); if (failed) return d;
            if ( backtracking==0 ) {

              		        location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START );
                          		location.setProperty( Location.LOCATION_PROPERTY_CLASS_NAME, id );
               				
               			pattern.setLocation( offset(LEFT_PAREN49.getLine()), LEFT_PAREN49.getCharPositionInLine() );
               			pattern.setLeftParentCharacter( ((CommonToken)LEFT_PAREN49).getStartIndex() );
               		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1124:4: ( constraints[pattern] )?
            int alt56=2;
            int LA56_0 = input.LA(1);

            if ( ((LA56_0>=ATTRIBUTES && LA56_0<=ID)||(LA56_0>=GLOBAL && LA56_0<=LEFT_PAREN)||(LA56_0>=QUERY && LA56_0<=WHEN)||LA56_0==ENABLED||LA56_0==SALIENCE||LA56_0==DURATION||LA56_0==FROM||LA56_0==EVAL||(LA56_0>=INIT && LA56_0<=RESULT)||(LA56_0>=CONTAINS && LA56_0<=IN)||LA56_0==NULL||LA56_0==THEN) ) {
                alt56=1;
            }
            switch (alt56) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1124:6: constraints[pattern]
                    {
                    pushFollow(FOLLOW_constraints_in_fact3186);
                    constraints(pattern);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            RIGHT_PAREN50=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact3197); if (failed) return d;
            if ( backtracking==0 ) {

              			this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
              			pattern.setEndLocation( offset(RIGHT_PAREN50.getLine()), RIGHT_PAREN50.getCharPositionInLine() );	
              			pattern.setEndCharacter( ((CommonToken)RIGHT_PAREN50).getStopIndex() );
              		        pattern.setRightParentCharacter( ((CommonToken)RIGHT_PAREN50).getStartIndex() );
               		
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1135:1: constraints[PatternDescr pattern] : constraint[$pattern] ( COMMA constraint[$pattern] )* ;
    public final void constraints(PatternDescr pattern) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1136:4: ( constraint[$pattern] ( COMMA constraint[$pattern] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1136:4: constraint[$pattern] ( COMMA constraint[$pattern] )*
            {
            pushFollow(FOLLOW_constraint_in_constraints3217);
            constraint(pattern);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1137:3: ( COMMA constraint[$pattern] )*
            loop57:
            do {
                int alt57=2;
                int LA57_0 = input.LA(1);

                if ( (LA57_0==COMMA) ) {
                    alt57=1;
                }


                switch (alt57) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1137:5: COMMA constraint[$pattern]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constraints3224); if (failed) return ;
            	    if ( backtracking==0 ) {
            	       location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START ); 
            	    }
            	    pushFollow(FOLLOW_constraint_in_constraints3233);
            	    constraint(pattern);
            	    _fsp--;
            	    if (failed) return ;

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
        return ;
    }
    // $ANTLR end constraints


    // $ANTLR start constraint
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1142:1: constraint[PatternDescr pattern] : or_constr[top] ;
    public final void constraint(PatternDescr pattern) throws RecognitionException {

        		ConditionalElementDescr top = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1147:3: ( or_constr[top] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1147:3: or_constr[top]
            {
            if ( backtracking==0 ) {

              			top = pattern.getConstraint();
              		
            }
            pushFollow(FOLLOW_or_constr_in_constraint3266);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1153:1: or_constr[ConditionalElementDescr base] : and_constr[or] ( DOUBLE_PIPE and_constr[or] )* ;
    public final void or_constr(ConditionalElementDescr base) throws RecognitionException {

        		OrDescr or = new OrDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1158:3: ( and_constr[or] ( DOUBLE_PIPE and_constr[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1158:3: and_constr[or] ( DOUBLE_PIPE and_constr[or] )*
            {
            pushFollow(FOLLOW_and_constr_in_or_constr3289);
            and_constr(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1159:3: ( DOUBLE_PIPE and_constr[or] )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);

                if ( (LA58_0==DOUBLE_PIPE) ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1159:5: DOUBLE_PIPE and_constr[or]
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr3297); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      		
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr3306);
            	    and_constr(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop58;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1174:1: and_constr[ConditionalElementDescr base] : unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )* ;
    public final void and_constr(ConditionalElementDescr base) throws RecognitionException {

        		AndDescr and = new AndDescr();
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1179:3: ( unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1179:3: unary_constr[and] ( DOUBLE_AMPER unary_constr[and] )*
            {
            pushFollow(FOLLOW_unary_constr_in_and_constr3338);
            unary_constr(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1180:3: ( DOUBLE_AMPER unary_constr[and] )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( (LA59_0==DOUBLE_AMPER) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1180:5: DOUBLE_AMPER unary_constr[and]
            	    {
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr3346); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      		
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr3355);
            	    unary_constr(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop59;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1195:1: unary_constr[ConditionalElementDescr base] : ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] ) ;
    public final void unary_constr(ConditionalElementDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1197:3: ( ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1197:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1197:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )
            int alt60=3;
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
            case RESULT:
            case CONTAINS:
            case EXCLUDES:
            case MATCHES:
            case MEMBEROF:
            case IN:
            case NULL:
            case THEN:
                {
                alt60=1;
                }
                break;
            case LEFT_PAREN:
                {
                alt60=2;
                }
                break;
            case EVAL:
                {
                alt60=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1197:3: ( field_constraint[$base] | LEFT_PAREN or_constr[$base] RIGHT_PAREN | EVAL predicate[$base] )", 60, 0, input);

                throw nvae;
            }

            switch (alt60) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1197:5: field_constraint[$base]
                    {
                    pushFollow(FOLLOW_field_constraint_in_unary_constr3383);
                    field_constraint(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1198:5: LEFT_PAREN or_constr[$base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr3391); if (failed) return ;
                    pushFollow(FOLLOW_or_constr_in_unary_constr3393);
                    or_constr(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr3396); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1199:5: EVAL predicate[$base]
                    {
                    match(input,EVAL,FOLLOW_EVAL_in_unary_constr3402); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_unary_constr3404);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1203:1: field_constraint[ConditionalElementDescr base] : ( ID ':' )? f= identifier ( or_restr_connective[top] | '->' predicate[$base] )? ;
    public final void field_constraint(ConditionalElementDescr base) throws RecognitionException {
        Token ID51=null;
        identifier_return f = null;



        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		RestrictionConnectiveDescr top = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1210:3: ( ( ID ':' )? f= identifier ( or_restr_connective[top] | '->' predicate[$base] )? )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1210:3: ( ID ':' )? f= identifier ( or_restr_connective[top] | '->' predicate[$base] )?
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1210:3: ( ID ':' )?
            int alt61=2;
            int LA61_0 = input.LA(1);

            if ( (LA61_0==ID) ) {
                int LA61_1 = input.LA(2);

                if ( (LA61_1==71) ) {
                    alt61=1;
                }
            }
            switch (alt61) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1210:5: ID ':'
                    {
                    ID51=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_field_constraint3434); if (failed) return ;
                    match(input,71,FOLLOW_71_in_field_constraint3436); if (failed) return ;
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

            pushFollow(FOLLOW_identifier_in_field_constraint3457);
            f=identifier();
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
               			    fbd.setEndCharacter( ((CommonToken)((Token)f.start)).getStopIndex() );
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
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1243:3: ( or_restr_connective[top] | '->' predicate[$base] )?
            int alt62=3;
            int LA62_0 = input.LA(1);

            if ( (LA62_0==LEFT_PAREN||LA62_0==NOT||(LA62_0>=CONTAINS && LA62_0<=IN)||(LA62_0>=74 && LA62_0<=79)) ) {
                alt62=1;
            }
            else if ( (LA62_0==73) ) {
                alt62=2;
            }
            switch (alt62) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1244:4: or_restr_connective[top]
                    {
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint3471);
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
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1252:4: '->' predicate[$base]
                    {
                    match(input,73,FOLLOW_73_in_field_constraint3486); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_field_constraint3488);
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1257:1: or_restr_connective[ RestrictionConnectiveDescr base ] : and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )* ;
    public final void or_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {

        		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1262:3: ( and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1262:3: and_restr_connective[or] ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*
            {
            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3517);
            and_restr_connective(or);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1263:3: ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*
            loop63:
            do {
                int alt63=2;
                alt63 = dfa63.predict(input);
                switch (alt63) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1264:5: DOUBLE_PIPE and_restr_connective[or]
            	    {
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective3536); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective3548);
            	    and_restr_connective(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop63;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1279:1: and_restr_connective[ RestrictionConnectiveDescr base ] : constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )* ;
    public final void and_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;


        		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1284:3: ( constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1284:3: constraint_expression[and] ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*
            {
            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3580);
            constraint_expression(and);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1285:3: ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*
            loop64:
            do {
                int alt64=2;
                alt64 = dfa64.predict(input);
                switch (alt64) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1286:5: t= DOUBLE_AMPER constraint_expression[and]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective3601); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective3612);
            	    constraint_expression(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop64;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1301:1: constraint_expression[RestrictionConnectiveDescr base] : ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN ) ;
    public final void constraint_expression(RestrictionConnectiveDescr base) throws RecognitionException {
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:3: ( ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )
            int alt65=3;
            switch ( input.LA(1) ) {
            case IN:
                {
                alt65=1;
                }
                break;
            case NOT:
                {
                int LA65_2 = input.LA(2);

                if ( (LA65_2==CONTAINS||(LA65_2>=MATCHES && LA65_2<=MEMBEROF)) ) {
                    alt65=2;
                }
                else if ( (LA65_2==IN) ) {
                    alt65=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1303:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )", 65, 2, input);

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
                alt65=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt65=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1303:3: ( compound_operator[$base] | simple_operator[$base] | LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN )", 65, 0, input);

                throw nvae;
            }

            switch (alt65) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:5: compound_operator[$base]
                    {
                    pushFollow(FOLLOW_compound_operator_in_constraint_expression3648);
                    compound_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1304:5: simple_operator[$base]
                    {
                    pushFollow(FOLLOW_simple_operator_in_constraint_expression3655);
                    simple_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1305:5: LEFT_PAREN or_restr_connective[$base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression3662); if (failed) return ;
                    if ( backtracking==0 ) {

                      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
                      		
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression3671);
                    or_restr_connective(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression3677); if (failed) return ;

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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1314:1: simple_operator[RestrictionConnectiveDescr base] : (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[$base, op] ;
    public final void simple_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;
        Token n=null;


        		String op = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1319:3: ( (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[$base, op] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1319:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[$base, op]
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1319:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )
            int alt66=13;
            switch ( input.LA(1) ) {
            case 74:
                {
                alt66=1;
                }
                break;
            case 75:
                {
                alt66=2;
                }
                break;
            case 76:
                {
                alt66=3;
                }
                break;
            case 77:
                {
                alt66=4;
                }
                break;
            case 78:
                {
                alt66=5;
                }
                break;
            case 79:
                {
                alt66=6;
                }
                break;
            case CONTAINS:
                {
                alt66=7;
                }
                break;
            case NOT:
                {
                switch ( input.LA(2) ) {
                case MEMBEROF:
                    {
                    alt66=13;
                    }
                    break;
                case CONTAINS:
                    {
                    alt66=8;
                    }
                    break;
                case MATCHES:
                    {
                    alt66=11;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1319:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )", 66, 8, input);

                    throw nvae;
                }

                }
                break;
            case EXCLUDES:
                {
                alt66=9;
                }
                break;
            case MATCHES:
                {
                alt66=10;
                }
                break;
            case MEMBEROF:
                {
                alt66=12;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1319:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )", 66, 0, input);

                throw nvae;
            }

            switch (alt66) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1319:5: t= '=='
                    {
                    t=(Token)input.LT(1);
                    match(input,74,FOLLOW_74_in_simple_operator3708); if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1320:5: t= '>'
                    {
                    t=(Token)input.LT(1);
                    match(input,75,FOLLOW_75_in_simple_operator3716); if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1321:5: t= '>='
                    {
                    t=(Token)input.LT(1);
                    match(input,76,FOLLOW_76_in_simple_operator3724); if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1322:5: t= '<'
                    {
                    t=(Token)input.LT(1);
                    match(input,77,FOLLOW_77_in_simple_operator3732); if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1323:5: t= '<='
                    {
                    t=(Token)input.LT(1);
                    match(input,78,FOLLOW_78_in_simple_operator3740); if (failed) return ;

                    }
                    break;
                case 6 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1324:5: t= '!='
                    {
                    t=(Token)input.LT(1);
                    match(input,79,FOLLOW_79_in_simple_operator3748); if (failed) return ;

                    }
                    break;
                case 7 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1325:5: t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator3756); if (failed) return ;

                    }
                    break;
                case 8 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1326:5: n= NOT t= CONTAINS
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3764); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator3768); if (failed) return ;

                    }
                    break;
                case 9 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1327:5: t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_simple_operator3776); if (failed) return ;

                    }
                    break;
                case 10 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1328:5: t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator3784); if (failed) return ;

                    }
                    break;
                case 11 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1329:5: n= NOT t= MATCHES
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3792); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator3796); if (failed) return ;

                    }
                    break;
                case 12 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1330:5: t= MEMBEROF
                    {
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator3804); if (failed) return ;

                    }
                    break;
                case 13 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1331:5: n= NOT t= MEMBEROF
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3812); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator3816); if (failed) return ;

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
            pushFollow(FOLLOW_expression_value_in_simple_operator3830);
            expression_value(base,  op);
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
    // $ANTLR end simple_operator


    // $ANTLR start compound_operator
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1353:1: compound_operator[RestrictionConnectiveDescr base] : ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op] ( COMMA rd= expression_value[group, op] )* RIGHT_PAREN ;
    public final void compound_operator(RestrictionConnectiveDescr base) throws RecognitionException {

        		String op = null;
        		RestrictionConnectiveDescr group = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1359:3: ( ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op] ( COMMA rd= expression_value[group, op] )* RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1359:3: ( IN | NOT IN ) LEFT_PAREN rd= expression_value[group, op] ( COMMA rd= expression_value[group, op] )* RIGHT_PAREN
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1359:3: ( IN | NOT IN )
            int alt67=2;
            int LA67_0 = input.LA(1);

            if ( (LA67_0==IN) ) {
                alt67=1;
            }
            else if ( (LA67_0==NOT) ) {
                alt67=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1359:3: ( IN | NOT IN )", 67, 0, input);

                throw nvae;
            }
            switch (alt67) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1359:5: IN
                    {
                    match(input,IN,FOLLOW_IN_in_compound_operator3857); if (failed) return ;
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
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1367:5: NOT IN
                    {
                    match(input,NOT,FOLLOW_NOT_in_compound_operator3869); if (failed) return ;
                    match(input,IN,FOLLOW_IN_in_compound_operator3871); if (failed) return ;
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

            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator3886); if (failed) return ;
            pushFollow(FOLLOW_expression_value_in_compound_operator3890);
            expression_value(group,  op);
            _fsp--;
            if (failed) return ;
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1377:3: ( COMMA rd= expression_value[group, op] )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==COMMA) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1377:5: COMMA rd= expression_value[group, op]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_compound_operator3897); if (failed) return ;
            	    pushFollow(FOLLOW_expression_value_in_compound_operator3901);
            	    expression_value(group,  op);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop68;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator3910); if (failed) return ;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1384:1: expression_value[RestrictionConnectiveDescr base, String op] : ( ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) ;
    public final void expression_value(RestrictionConnectiveDescr base, String op) throws RecognitionException {
        Token ID52=null;
        String lc = null;

        String rvc = null;



        		RestrictionDescr rd = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1389:3: ( ( ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1389:3: ( ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1389:3: ( ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            int alt69=4;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA69_1 = input.LA(2);

                if ( (LA69_1==EOF||(LA69_1>=COMMA && LA69_1<=RIGHT_PAREN)||LA69_1==DOUBLE_PIPE||LA69_1==DOUBLE_AMPER) ) {
                    alt69=1;
                }
                else if ( (LA69_1==DOT) ) {
                    alt69=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1389:3: ( ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 69, 1, input);

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
                alt69=3;
                }
                break;
            case LEFT_PAREN:
                {
                alt69=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1389:3: ( ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 69, 0, input);

                throw nvae;
            }

            switch (alt69) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1389:5: ID
                    {
                    ID52=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_expression_value3938); if (failed) return ;
                    if ( backtracking==0 ) {

                      				rd = new VariableRestrictionDescr(op, ID52.getText());
                      			
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1393:5: lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_expression_value3951);
                    lc=enum_constraint();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      				rd  = new QualifiedIdentifierRestrictionDescr(op, lc);
                      			
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1397:5: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_expression_value3971);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op, lc);
                      			
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1401:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_expression_value3985);
                    rvc=retval_constraint();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      				rd = new ReturnValueRestrictionDescr(op, rvc);							
                      			
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {

              			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_END);
              			if( rd != null ) {
              				base.addRestriction( rd );
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
    // $ANTLR end expression_value


    // $ANTLR start literal_constraint
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1414:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final String literal_constraint() throws RecognitionException {
        String text = null;

        Token t=null;


        		text = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1418:4: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1418:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1418:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt70=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt70=1;
                }
                break;
            case INT:
                {
                alt70=2;
                }
                break;
            case FLOAT:
                {
                alt70=3;
                }
                break;
            case BOOL:
                {
                alt70=4;
                }
                break;
            case NULL:
                {
                alt70=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("1418:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )", 70, 0, input);

                throw nvae;
            }

            switch (alt70) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1418:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint4028); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t.getText() ); 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1419:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint4039); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1420:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint4052); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1421:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint4063); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1422:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint4075); if (failed) return text;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1426:1: enum_constraint returns [String text] : ID ( '.' ident= identifier )+ ;
    public final String enum_constraint() throws RecognitionException {
        String text = null;

        Token ID53=null;
        identifier_return ident = null;



        		text = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1431:3: ( ID ( '.' ident= identifier )+ )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1431:3: ID ( '.' ident= identifier )+
            {
            ID53=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint4108); if (failed) return text;
            if ( backtracking==0 ) {
               text=ID53.getText(); 
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1431:25: ( '.' ident= identifier )+
            int cnt71=0;
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);

                if ( (LA71_0==DOT) ) {
                    alt71=1;
                }


                switch (alt71) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1431:27: '.' ident= identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_enum_constraint4114); if (failed) return text;
            	    pushFollow(FOLLOW_identifier_in_enum_constraint4118);
            	    ident=identifier();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	       text += "." + ((Token)ident.start).getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt71 >= 1 ) break loop71;
            	    if (backtracking>0) {failed=true; return text;}
                        EarlyExitException eee =
                            new EarlyExitException(71, input);
                        throw eee;
                }
                cnt71++;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1435:1: predicate[ConditionalElementDescr base] : text= paren_chunk[d] ;
    public final void predicate(ConditionalElementDescr base) throws RecognitionException {
        String text = null;



        		PredicateDescr d = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1440:3: (text= paren_chunk[d] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1440:3: text= paren_chunk[d]
            {
            if ( backtracking==0 ) {

              			d = new PredicateDescr( );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_predicate4160);
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


    // $ANTLR start curly_chunk
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1454:1: curly_chunk[BaseDescr descr] returns [String text] : loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY ;
    public final String curly_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1460:3: (loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1460:3: loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk4199); if (failed) return text;
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              		    
              		    buf.append( loc.getText() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1468:3: (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )*
            loop72:
            do {
                int alt72=3;
                int LA72_0 = input.LA(1);

                if ( ((LA72_0>=ATTRIBUTES && LA72_0<=NULL)||(LA72_0>=LEFT_SQUARE && LA72_0<=79)) ) {
                    alt72=1;
                }
                else if ( (LA72_0==LEFT_CURLY) ) {
                    alt72=2;
                }


                switch (alt72) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1469:4: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=NULL)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk4215);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1474:4: chunk= curly_chunk[descr]
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk4239);
            	    chunk=curly_chunk(descr);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop72;
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
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk4276); if (failed) return text;
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


    // $ANTLR start paren_chunk
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1495:1: paren_chunk[BaseDescr descr] returns [String text] : loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* end= RIGHT_PAREN ;
    public final String paren_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        Token end=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:10: (loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* end= RIGHT_PAREN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1501:10: loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* end= RIGHT_PAREN
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk4337); if (failed) return text;
            if ( backtracking==0 ) {

              		    buf.append( loc.getText() );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1510:3: (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )*
            loop73:
            do {
                int alt73=3;
                int LA73_0 = input.LA(1);

                if ( ((LA73_0>=ATTRIBUTES && LA73_0<=GLOBAL)||LA73_0==COMMA||(LA73_0>=QUERY && LA73_0<=79)) ) {
                    alt73=1;
                }
                else if ( (LA73_0==LEFT_PAREN) ) {
                    alt73=2;
                }


                switch (alt73) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1511:4: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=GLOBAL)||input.LA(1)==COMMA||(input.LA(1)>=QUERY && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk4353);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1516:4: chunk= paren_chunk[null]
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk4377);
            	    chunk=paren_chunk(null);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop73;
                }
            } while (true);

            if ( backtracking==0 ) {

              		    if( channel != null ) {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
              		    } else {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
              		    }
              		
            }
            end=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk4414); if (failed) return text;
            if ( backtracking==0 ) {

                                  buf.append( end.getText() );
              		    text = buf.toString();
              		    if( descr != null ) {
              		        descr.setEndCharacter( ((CommonToken)end).getStopIndex() );
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


    // $ANTLR start square_chunk
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1539:1: square_chunk[BaseDescr descr] returns [String text] : loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE ;
    public final String square_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1545:10: (loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1545:10: loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk4478); if (failed) return text;
            if ( backtracking==0 ) {

              		    buf.append( loc.getText());
               
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1555:3: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )*
            loop74:
            do {
                int alt74=3;
                int LA74_0 = input.LA(1);

                if ( ((LA74_0>=ATTRIBUTES && LA74_0<=RIGHT_CURLY)||(LA74_0>=THEN && LA74_0<=79)) ) {
                    alt74=1;
                }
                else if ( (LA74_0==LEFT_SQUARE) ) {
                    alt74=2;
                }


                switch (alt74) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1556:4: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=RIGHT_CURLY)||(input.LA(1)>=THEN && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk4494);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1561:4: chunk= square_chunk[null]
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk4518);
            	    chunk=square_chunk(null);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop74;
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
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk4555); if (failed) return text;
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1583:1: retval_constraint returns [String text] : c= paren_chunk[null] ;
    public final String retval_constraint() throws RecognitionException {
        String text = null;

        String c = null;



        		text = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1588:3: (c= paren_chunk[null] )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1588:3: c= paren_chunk[null]
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint4600);
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


    // $ANTLR start qualified_id
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1591:1: qualified_id[BaseDescr descr] returns [String name] : ID ( '.' ident= identifier )* ( '[' loc= ']' )* ;
    public final String qualified_id(BaseDescr descr) throws RecognitionException {
        String name = null;

        Token loc=null;
        Token ID54=null;
        identifier_return ident = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1596:3: ( ID ( '.' ident= identifier )* ( '[' loc= ']' )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1596:3: ID ( '.' ident= identifier )* ( '[' loc= ']' )*
            {
            ID54=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_qualified_id4628); if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name =ID54.getText(); 
              		    if( descr != null ) {
              			descr.setStartCharacter( ((CommonToken)ID54).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)ID54).getStopIndex() );
              		    }
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1604:3: ( '.' ident= identifier )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);

                if ( (LA75_0==DOT) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1604:5: '.' ident= identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_qualified_id4639); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_qualified_id4643);
            	    ident=identifier();
            	    _fsp--;
            	    if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name += "." + ident.start.getText(); 
            	          		        if( descr != null ) {
            	      			    descr.setEndCharacter( ((CommonToken)((Token)ident.start)).getStopIndex() );
            	      		        }
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop75;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1612:3: ( '[' loc= ']' )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);

                if ( (LA76_0==LEFT_SQUARE) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1612:5: '[' loc= ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_qualified_id4665); if (failed) return name;
            	    loc=(Token)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_qualified_id4669); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name += "[]";
            	          		        if( descr != null ) {
            	      			    descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
            	      		        }
            	      		    
            	    }

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
        }
        return name;
    }
    // $ANTLR end qualified_id


    // $ANTLR start dotted_name
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1622:1: dotted_name[BaseDescr descr] returns [String name] : id= identifier ( '.' ident= identifier )* ( '[' loc= ']' )* ;
    public final String dotted_name(BaseDescr descr) throws RecognitionException {
        String name = null;

        Token loc=null;
        identifier_return id = null;

        identifier_return ident = null;



        		name = null;
        	
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1627:3: (id= identifier ( '.' ident= identifier )* ( '[' loc= ']' )* )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1627:3: id= identifier ( '.' ident= identifier )* ( '[' loc= ']' )*
            {
            pushFollow(FOLLOW_identifier_in_dotted_name4710);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name=input.toString(id.start,id.stop); 
              		    if( descr != null ) {
              			descr.setStartCharacter( ((CommonToken)((Token)id.start)).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)((Token)id.start)).getStopIndex() );
              		    }
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1635:3: ( '.' ident= identifier )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);

                if ( (LA77_0==DOT) ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1635:5: '.' ident= identifier
            	    {
            	    match(input,DOT,FOLLOW_DOT_in_dotted_name4721); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_dotted_name4725);
            	    ident=identifier();
            	    _fsp--;
            	    if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name += "." + input.toString(ident.start,ident.stop); 
            	          		        if( descr != null ) {
            	      			    descr.setEndCharacter( ((CommonToken)((Token)ident.start)).getStopIndex() );
            	      		        }
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop77;
                }
            } while (true);

            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1643:3: ( '[' loc= ']' )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);

                if ( (LA78_0==LEFT_SQUARE) ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1643:5: '[' loc= ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name4747); if (failed) return name;
            	    loc=(Token)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name4751); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name += "[]";
            	          		        if( descr != null ) {
            	      			    descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
            	      		        }
            	      		    
            	    }

            	    }
            	    break;

            	default :
            	    break loop78;
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


    // $ANTLR start rhs_chunk
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1653:1: rhs_chunk[RuleDescr rule] : THEN (~ END )* loc= END opt_semicolon ;
    public final void rhs_chunk(RuleDescr rule) throws RecognitionException {
        Token loc=null;
        Token THEN55=null;


                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1659:10: ( THEN (~ END )* loc= END opt_semicolon )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1659:10: THEN (~ END )* loc= END opt_semicolon
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            THEN55=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk4803); if (failed) return ;
            if ( backtracking==0 ) {

              			location.setType( Location.LOCATION_RHS );
              		
            }
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1668:3: (~ END )*
            loop79:
            do {
                int alt79=2;
                int LA79_0 = input.LA(1);

                if ( ((LA79_0>=ATTRIBUTES && LA79_0<=QUERY)||(LA79_0>=TEMPLATE && LA79_0<=79)) ) {
                    alt79=1;
                }


                switch (alt79) {
            	case 1 :
            	    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1669:6: ~ END
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=QUERY)||(input.LA(1)>=TEMPLATE && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk4819);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop79;
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
            match(input,END,FOLLOW_END_in_rhs_chunk4856); if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_rhs_chunk4858);
            opt_semicolon();
            _fsp--;
            if (failed) return ;
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


    // $ANTLR start name
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1700:1: name returns [String name] : ( ID | STRING );
    public final String name() throws RecognitionException {
        String name = null;

        Token ID56=null;
        Token STRING57=null;

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1701:5: ( ID | STRING )
            int alt80=2;
            int LA80_0 = input.LA(1);

            if ( (LA80_0==ID) ) {
                alt80=1;
            }
            else if ( (LA80_0==STRING) ) {
                alt80=2;
            }
            else {
                if (backtracking>0) {failed=true; return name;}
                NoViableAltException nvae =
                    new NoViableAltException("1700:1: name returns [String name] : ( ID | STRING );", 80, 0, input);

                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1701:5: ID
                    {
                    ID56=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_name4892); if (failed) return name;
                    if ( backtracking==0 ) {
                       name = ID56.getText(); 
                    }

                    }
                    break;
                case 2 :
                    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1702:5: STRING
                    {
                    STRING57=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_name4900); if (failed) return name;
                    if ( backtracking==0 ) {
                       name = getString( STRING57.getText() ); 
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
    // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1705:1: identifier : ( ID | PACKAGE | FUNCTION | GLOBAL | IMPORT | RULE | QUERY | TEMPLATE | ATTRIBUTES | ENABLED | SALIENCE | DURATION | FROM | INIT | ACTION | RESULT | CONTAINS | EXCLUDES | MEMBEROF | MATCHES | NULL | WHEN | THEN | END | IN );
    public final identifier_return identifier() throws RecognitionException {
        identifier_return retval = new identifier_return();
        retval.start = input.LT(1);

        try {
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1706:10: ( ID | PACKAGE | FUNCTION | GLOBAL | IMPORT | RULE | QUERY | TEMPLATE | ATTRIBUTES | ENABLED | SALIENCE | DURATION | FROM | INIT | ACTION | RESULT | CONTAINS | EXCLUDES | MEMBEROF | MATCHES | NULL | WHEN | THEN | END | IN )
            // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:
            {
            if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=ID)||input.LA(1)==GLOBAL||(input.LA(1)>=QUERY && input.LA(1)<=WHEN)||input.LA(1)==ENABLED||input.LA(1)==SALIENCE||input.LA(1)==DURATION||input.LA(1)==FROM||(input.LA(1)>=INIT && input.LA(1)<=RESULT)||(input.LA(1)>=CONTAINS && input.LA(1)<=IN)||input.LA(1)==NULL||input.LA(1)==THEN ) {
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
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:6: ( LEFT_SQUARE )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:963:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred12756); if (failed) return ;

        }
    }
    // $ANTLR end synpred1

    // $ANTLR start synpred2
    public final void synpred2_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:968:6: ( LEFT_PAREN )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:968:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred22790); if (failed) return ;

        }
    }
    // $ANTLR end synpred2

    // $ANTLR start synpred3
    public final void synpred3_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1264:5: ( DOUBLE_PIPE and_restr_connective[or] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1264:5: DOUBLE_PIPE and_restr_connective[or]
        {
        match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred33536); if (failed) return ;
        pushFollow(FOLLOW_and_restr_connective_in_synpred33548);
        and_restr_connective(or);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred3

    // $ANTLR start synpred4
    public final void synpred4_fragment() throws RecognitionException {   
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1286:5: ( DOUBLE_AMPER constraint_expression[and] )
        // /home/etirelli/workspace/jboss/jbossrules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1286:5: DOUBLE_AMPER constraint_expression[and]
        {
        match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred43601); if (failed) return ;
        pushFollow(FOLLOW_constraint_expression_in_synpred43612);
        constraint_expression(and);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred4

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


    protected DFA12 dfa12 = new DFA12(this);
    protected DFA13 dfa13 = new DFA13(this);
    protected DFA49 dfa49 = new DFA49(this);
    protected DFA63 dfa63 = new DFA63(this);
    protected DFA64 dfa64 = new DFA64(this);
    static final String DFA12_eotS =
        "\6\uffff";
    static final String DFA12_eofS =
        "\6\uffff";
    static final String DFA12_minS =
        "\2\4\1\72\2\uffff\1\4";
    static final String DFA12_maxS =
        "\2\73\1\72\2\uffff\1\73";
    static final String DFA12_acceptS =
        "\3\uffff\1\2\1\1\1\uffff";
    static final String DFA12_specialS =
        "\6\uffff}>";
    static final String[] DFA12_transitionS = {
            "\5\1\1\uffff\1\1\3\uffff\5\1\3\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\1\1\6\uffff\1\1\5\uffff\3\1\1\uffff\5\1\1\uffff\1\1\4\uffff"+
            "\1\1",
            "\7\4\1\uffff\2\3\5\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4\6\uffff"+
            "\1\4\5\uffff\3\4\1\uffff\5\4\1\uffff\1\4\2\uffff\1\2\1\uffff"+
            "\1\4",
            "\1\5",
            "",
            "",
            "\5\4\1\uffff\1\4\1\uffff\2\3\5\4\3\uffff\1\4\1\uffff\1\4\6\uffff"+
            "\1\4\6\uffff\1\4\5\uffff\3\4\1\uffff\5\4\1\uffff\1\4\2\uffff"+
            "\1\2\1\uffff\1\4"
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
            return "327:15: (paramType= dotted_name[null] )?";
        }
    }
    static final String DFA13_eotS =
        "\6\uffff";
    static final String DFA13_eofS =
        "\6\uffff";
    static final String DFA13_minS =
        "\2\4\1\uffff\1\72\1\uffff\1\4";
    static final String DFA13_maxS =
        "\2\73\1\uffff\1\72\1\uffff\1\73";
    static final String DFA13_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    static final String DFA13_specialS =
        "\6\uffff}>";
    static final String[] DFA13_transitionS = {
            "\5\1\1\uffff\1\1\3\uffff\5\1\3\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\1\1\6\uffff\1\1\5\uffff\3\1\1\uffff\5\1\1\uffff\1\1\4\uffff"+
            "\1\1",
            "\7\2\1\uffff\2\4\5\2\3\uffff\1\2\1\uffff\1\2\6\uffff\1\2\6\uffff"+
            "\1\2\5\uffff\3\2\1\uffff\5\2\1\uffff\1\2\2\uffff\1\3\1\uffff"+
            "\1\2",
            "",
            "\1\5",
            "",
            "\5\2\1\uffff\1\2\1\uffff\2\4\5\2\3\uffff\1\2\1\uffff\1\2\6\uffff"+
            "\1\2\6\uffff\1\2\5\uffff\3\2\1\uffff\5\2\1\uffff\1\2\2\uffff"+
            "\1\3\1\uffff\1\2"
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
            return "331:22: (paramType= dotted_name[null] )?";
        }
    }
    static final String DFA49_eotS =
        "\150\uffff";
    static final String DFA49_eofS =
        "\150\uffff";
    static final String DFA49_minS =
        "\1\10\1\uffff\1\4\1\uffff\3\4\1\0\1\uffff\4\4\1\uffff\1\0\12\4\2"+
        "\0\1\4\1\0\1\4\1\0\3\4\3\0\1\4\1\0\1\4\1\0\3\4\3\0\1\4\1\0\1\4\1"+
        "\0\3\4\2\0\3\4\1\0\3\4\1\0\1\4\1\0\1\4\1\uffff\44\0";
    static final String DFA49_maxS =
        "\1\106\1\uffff\1\117\1\uffff\3\117\1\0\1\uffff\4\117\1\uffff\1\0"+
        "\12\117\2\0\1\117\1\0\1\117\1\0\3\117\3\0\1\117\1\0\1\117\1\0\3"+
        "\117\3\0\1\117\1\0\1\117\1\0\3\117\2\0\3\117\1\0\3\117\1\0\1\117"+
        "\1\0\1\117\1\uffff\44\0";
    static final String DFA49_acceptS =
        "\1\uffff\1\1\1\uffff\1\3\4\uffff\1\2\4\uffff\1\2\65\uffff\1\2\44"+
        "\uffff";
    static final String DFA49_specialS =
        "\1\23\1\uffff\1\62\1\uffff\1\43\1\75\1\15\1\14\1\uffff\1\47\1\24"+
        "\1\54\1\22\1\uffff\1\63\1\65\1\5\1\44\1\60\1\35\1\6\1\36\1\16\1"+
        "\66\1\73\1\27\1\13\1\53\1\72\1\52\1\42\1\10\1\1\1\37\1\26\1\61\1"+
        "\20\1\57\1\51\1\56\1\34\1\46\1\50\1\0\1\70\1\4\1\21\1\64\1\33\1"+
        "\67\1\7\1\32\1\55\1\25\1\17\1\45\1\11\1\2\1\40\1\30\1\12\1\3\1\41"+
        "\1\31\1\71\1\76\1\74\45\uffff}>";
    static final String[] DFA49_transitionS = {
            "\2\3\1\uffff\1\2\1\uffff\1\3\1\uffff\1\3\22\uffff\4\3\1\uffff"+
            "\4\3\16\uffff\1\1\1\uffff\1\3\12\uffff\1\3",
            "",
            "\4\15\1\13\2\15\1\7\1\15\1\10\24\15\1\4\1\15\1\5\2\15\1\6\1"+
            "\11\1\12\1\14\45\15",
            "",
            "\4\15\1\22\2\15\1\16\1\15\1\10\31\15\1\17\1\20\1\21\1\23\45"+
            "\15",
            "\4\15\1\27\2\15\1\31\1\15\1\10\31\15\1\24\1\25\1\26\1\30\45"+
            "\15",
            "\4\15\1\33\2\15\1\32\1\15\1\10\102\15",
            "\1\uffff",
            "",
            "\4\15\1\35\2\15\1\34\1\15\1\10\102\15",
            "\7\15\1\36\1\15\1\10\102\15",
            "\5\15\1\40\1\15\1\42\1\15\1\10\53\15\1\41\15\15\1\37\10\15",
            "\7\15\1\43\1\15\1\10\102\15",
            "",
            "\1\uffff",
            "\4\15\1\45\2\15\1\44\1\15\1\10\102\15",
            "\4\15\1\47\2\15\1\46\1\15\1\10\102\15",
            "\7\15\1\50\1\15\1\10\102\15",
            "\5\15\1\52\1\15\1\54\1\15\1\10\53\15\1\53\15\15\1\51\10\15",
            "\7\15\1\55\1\15\1\10\102\15",
            "\4\15\1\57\2\15\1\56\1\15\1\10\102\15",
            "\4\15\1\61\2\15\1\60\1\15\1\10\102\15",
            "\7\15\1\62\1\15\1\10\102\15",
            "\5\15\1\64\1\15\1\66\1\15\1\10\53\15\1\65\15\15\1\63\10\15",
            "\7\15\1\67\1\15\1\10\102\15",
            "\1\uffff",
            "\1\uffff",
            "\5\15\1\71\1\15\1\73\1\15\1\10\53\15\1\72\15\15\1\70\10\15",
            "\1\uffff",
            "\5\15\1\75\1\15\1\77\1\15\1\10\53\15\1\76\15\15\1\74\10\15",
            "\1\uffff",
            "\4\15\1\100\2\15\1\101\1\15\1\10\102\15",
            "\5\102\1\15\1\102\1\103\1\15\1\10\5\102\3\15\1\102\1\15\1\102"+
            "\6\15\1\102\6\15\1\102\5\15\3\102\1\15\5\102\1\15\1\102\4\15"+
            "\1\102\24\15",
            "\7\15\1\103\1\15\1\10\54\15\1\104\25\15",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\5\15\1\106\1\15\1\110\1\15\1\10\53\15\1\107\15\15\1\105\10"+
            "\15",
            "\1\uffff",
            "\5\15\1\112\1\15\1\114\1\15\1\10\53\15\1\113\15\15\1\111\10"+
            "\15",
            "\1\uffff",
            "\4\15\1\115\2\15\1\116\1\15\1\10\102\15",
            "\5\117\1\15\1\117\1\103\1\15\1\10\5\117\3\15\1\117\1\15\1\117"+
            "\6\15\1\117\6\15\1\117\5\15\3\117\1\15\5\117\1\15\1\117\4\15"+
            "\1\117\24\15",
            "\7\15\1\103\1\15\1\10\54\15\1\120\25\15",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\5\15\1\122\1\15\1\124\1\15\1\10\53\15\1\123\15\15\1\121\10"+
            "\15",
            "\1\uffff",
            "\5\15\1\126\1\15\1\130\1\15\1\10\53\15\1\127\15\15\1\125\10"+
            "\15",
            "\1\uffff",
            "\4\15\1\131\2\15\1\132\1\15\1\10\102\15",
            "\5\133\1\15\1\133\1\103\1\15\1\10\5\133\3\15\1\133\1\15\1\133"+
            "\6\15\1\133\6\15\1\133\5\15\3\133\1\15\5\133\1\15\1\133\4\15"+
            "\1\133\24\15",
            "\7\15\1\103\1\15\1\10\54\15\1\134\25\15",
            "\1\uffff",
            "\1\uffff",
            "\4\15\1\135\2\15\1\136\1\15\1\10\102\15",
            "\5\137\1\15\1\137\1\103\1\15\1\10\5\137\3\15\1\137\1\15\1\137"+
            "\6\15\1\137\6\15\1\137\5\15\3\137\1\15\5\137\1\15\1\137\4\15"+
            "\1\137\24\15",
            "\7\15\1\103\1\15\1\10\54\15\1\140\25\15",
            "\1\uffff",
            "\4\15\1\141\2\15\1\142\1\15\1\10\102\15",
            "\5\143\1\15\1\143\1\103\1\15\1\10\5\143\3\15\1\143\1\15\1\143"+
            "\6\15\1\143\6\15\1\143\5\15\3\143\1\15\5\143\1\15\1\143\4\15"+
            "\1\143\24\15",
            "\7\15\1\103\1\15\1\10\54\15\1\144\25\15",
            "\1\uffff",
            "\5\15\1\145\1\15\1\147\1\15\1\10\53\15\1\146\26\15",
            "\1\uffff",
            "\5\15\1\40\1\15\1\42\1\15\1\10\53\15\1\41\26\15",
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

    static final short[] DFA49_eot = DFA.unpackEncodedString(DFA49_eotS);
    static final short[] DFA49_eof = DFA.unpackEncodedString(DFA49_eofS);
    static final char[] DFA49_min = DFA.unpackEncodedStringToUnsignedChars(DFA49_minS);
    static final char[] DFA49_max = DFA.unpackEncodedStringToUnsignedChars(DFA49_maxS);
    static final short[] DFA49_accept = DFA.unpackEncodedString(DFA49_acceptS);
    static final short[] DFA49_special = DFA.unpackEncodedString(DFA49_specialS);
    static final short[][] DFA49_transition;

    static {
        int numStates = DFA49_transitionS.length;
        DFA49_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA49_transition[i] = DFA.unpackEncodedString(DFA49_transitionS[i]);
        }
    }

    class DFA49 extends DFA {

        public DFA49(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 49;
            this.eot = DFA49_eot;
            this.eof = DFA49_eof;
            this.min = DFA49_min;
            this.max = DFA49_max;
            this.accept = DFA49_accept;
            this.special = DFA49_special;
            this.transition = DFA49_transition;
        }
        public String getDescription() {
            return "962:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk[$from] | ( LEFT_PAREN )=>paarg= paren_chunk[$from] )?";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA49_43 = input.LA(1);

                         
                        int index49_43 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_43==RIGHT_SQUARE) ) {s = 80;}

                        else if ( (LA49_43==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_43>=ATTRIBUTES && LA49_43<=GLOBAL)||LA49_43==COMMA||(LA49_43>=QUERY && LA49_43<=LEFT_SQUARE)||(LA49_43>=THEN && LA49_43<=79)) && (synpred2())) {s = 13;}

                        else if ( (LA49_43==LEFT_PAREN) && (synpred2())) {s = 67;}

                         
                        input.seek(index49_43);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA49_32 = input.LA(1);

                         
                        int index49_32 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA49_32>=ATTRIBUTES && LA49_32<=ID)||LA49_32==GLOBAL||(LA49_32>=QUERY && LA49_32<=WHEN)||LA49_32==ENABLED||LA49_32==SALIENCE||LA49_32==DURATION||LA49_32==FROM||(LA49_32>=INIT && LA49_32<=RESULT)||(LA49_32>=CONTAINS && LA49_32<=IN)||LA49_32==NULL||LA49_32==THEN) ) {s = 66;}

                        else if ( (LA49_32==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( (LA49_32==DOT||LA49_32==COMMA||(LA49_32>=DATE_EFFECTIVE && LA49_32<=DATE_EXPIRES)||LA49_32==BOOL||(LA49_32>=INT && LA49_32<=AGENDA_GROUP)||(LA49_32>=DIALECT && LA49_32<=DOUBLE_AMPER)||(LA49_32>=EXISTS && LA49_32<=ACCUMULATE)||LA49_32==COLLECT||LA49_32==FLOAT||(LA49_32>=LEFT_CURLY && LA49_32<=RIGHT_SQUARE)||(LA49_32>=EOL && LA49_32<=79)) && (synpred2())) {s = 13;}

                        else if ( (LA49_32==LEFT_PAREN) && (synpred2())) {s = 67;}

                         
                        input.seek(index49_32);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA49_57 = input.LA(1);

                         
                        int index49_57 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA49_57>=ATTRIBUTES && LA49_57<=ID)||LA49_57==GLOBAL||(LA49_57>=QUERY && LA49_57<=WHEN)||LA49_57==ENABLED||LA49_57==SALIENCE||LA49_57==DURATION||LA49_57==FROM||(LA49_57>=INIT && LA49_57<=RESULT)||(LA49_57>=CONTAINS && LA49_57<=IN)||LA49_57==NULL||LA49_57==THEN) ) {s = 95;}

                        else if ( (LA49_57==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( (LA49_57==DOT||LA49_57==COMMA||(LA49_57>=DATE_EFFECTIVE && LA49_57<=DATE_EXPIRES)||LA49_57==BOOL||(LA49_57>=INT && LA49_57<=AGENDA_GROUP)||(LA49_57>=DIALECT && LA49_57<=DOUBLE_AMPER)||(LA49_57>=EXISTS && LA49_57<=ACCUMULATE)||LA49_57==COLLECT||LA49_57==FLOAT||(LA49_57>=LEFT_CURLY && LA49_57<=RIGHT_SQUARE)||(LA49_57>=EOL && LA49_57<=79)) && (synpred2())) {s = 13;}

                        else if ( (LA49_57==LEFT_PAREN) && (synpred2())) {s = 67;}

                         
                        input.seek(index49_57);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA49_61 = input.LA(1);

                         
                        int index49_61 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA49_61>=ATTRIBUTES && LA49_61<=ID)||LA49_61==GLOBAL||(LA49_61>=QUERY && LA49_61<=WHEN)||LA49_61==ENABLED||LA49_61==SALIENCE||LA49_61==DURATION||LA49_61==FROM||(LA49_61>=INIT && LA49_61<=RESULT)||(LA49_61>=CONTAINS && LA49_61<=IN)||LA49_61==NULL||LA49_61==THEN) ) {s = 99;}

                        else if ( (LA49_61==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( (LA49_61==DOT||LA49_61==COMMA||(LA49_61>=DATE_EFFECTIVE && LA49_61<=DATE_EXPIRES)||LA49_61==BOOL||(LA49_61>=INT && LA49_61<=AGENDA_GROUP)||(LA49_61>=DIALECT && LA49_61<=DOUBLE_AMPER)||(LA49_61>=EXISTS && LA49_61<=ACCUMULATE)||LA49_61==COLLECT||LA49_61==FLOAT||(LA49_61>=LEFT_CURLY && LA49_61<=RIGHT_SQUARE)||(LA49_61>=EOL && LA49_61<=79)) && (synpred2())) {s = 13;}

                        else if ( (LA49_61==LEFT_PAREN) && (synpred2())) {s = 67;}

                         
                        input.seek(index49_61);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA49_45 = input.LA(1);

                         
                        int index49_45 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_45);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA49_16 = input.LA(1);

                         
                        int index49_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_16==LEFT_PAREN) ) {s = 38;}

                        else if ( (LA49_16==ID) ) {s = 39;}

                        else if ( (LA49_16==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_16>=ATTRIBUTES && LA49_16<=FUNCTION)||(LA49_16>=DOT && LA49_16<=GLOBAL)||LA49_16==COMMA||(LA49_16>=QUERY && LA49_16<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_16);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA49_20 = input.LA(1);

                         
                        int index49_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_20==LEFT_PAREN) ) {s = 46;}

                        else if ( (LA49_20==ID) ) {s = 47;}

                        else if ( (LA49_20==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_20>=ATTRIBUTES && LA49_20<=FUNCTION)||(LA49_20>=DOT && LA49_20<=GLOBAL)||LA49_20==COMMA||(LA49_20>=QUERY && LA49_20<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_20);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA49_50 = input.LA(1);

                         
                        int index49_50 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_50);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA49_31 = input.LA(1);

                         
                        int index49_31 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_31==ID) ) {s = 64;}

                        else if ( (LA49_31==LEFT_PAREN) ) {s = 65;}

                        else if ( (LA49_31==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_31>=ATTRIBUTES && LA49_31<=FUNCTION)||(LA49_31>=DOT && LA49_31<=GLOBAL)||LA49_31==COMMA||(LA49_31>=QUERY && LA49_31<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_31);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA49_56 = input.LA(1);

                         
                        int index49_56 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_56==ID) ) {s = 93;}

                        else if ( (LA49_56==LEFT_PAREN) ) {s = 94;}

                        else if ( (LA49_56==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_56>=ATTRIBUTES && LA49_56<=FUNCTION)||(LA49_56>=DOT && LA49_56<=GLOBAL)||LA49_56==COMMA||(LA49_56>=QUERY && LA49_56<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_56);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA49_60 = input.LA(1);

                         
                        int index49_60 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_60==ID) ) {s = 97;}

                        else if ( (LA49_60==LEFT_PAREN) ) {s = 98;}

                        else if ( (LA49_60==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_60>=ATTRIBUTES && LA49_60<=FUNCTION)||(LA49_60>=DOT && LA49_60<=GLOBAL)||LA49_60==COMMA||(LA49_60>=QUERY && LA49_60<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_60);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA49_26 = input.LA(1);

                         
                        int index49_26 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_26);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA49_7 = input.LA(1);

                         
                        int index49_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_7);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA49_6 = input.LA(1);

                         
                        int index49_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_6==LEFT_PAREN) ) {s = 26;}

                        else if ( (LA49_6==ID) ) {s = 27;}

                        else if ( (LA49_6==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_6>=ATTRIBUTES && LA49_6<=FUNCTION)||(LA49_6>=DOT && LA49_6<=GLOBAL)||LA49_6==COMMA||(LA49_6>=QUERY && LA49_6<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_6);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA49_22 = input.LA(1);

                         
                        int index49_22 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_22==LEFT_PAREN) ) {s = 50;}

                        else if ( (LA49_22==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_22>=ATTRIBUTES && LA49_22<=GLOBAL)||LA49_22==COMMA||(LA49_22>=QUERY && LA49_22<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_22);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA49_54 = input.LA(1);

                         
                        int index49_54 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_54);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA49_36 = input.LA(1);

                         
                        int index49_36 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_36);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA49_46 = input.LA(1);

                         
                        int index49_46 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_46);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA49_12 = input.LA(1);

                         
                        int index49_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_12==LEFT_PAREN) ) {s = 35;}

                        else if ( (LA49_12==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_12>=ATTRIBUTES && LA49_12<=GLOBAL)||LA49_12==COMMA||(LA49_12>=QUERY && LA49_12<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_12);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA49_0 = input.LA(1);

                         
                        int index49_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_0==LEFT_SQUARE) && (synpred1())) {s = 1;}

                        else if ( (LA49_0==LEFT_PAREN) ) {s = 2;}

                        else if ( ((LA49_0>=ID && LA49_0<=DOT)||LA49_0==RIGHT_PAREN||LA49_0==END||(LA49_0>=OR && LA49_0<=DOUBLE_AMPER)||(LA49_0>=EXISTS && LA49_0<=FORALL)||LA49_0==THEN||LA49_0==70) ) {s = 3;}

                         
                        input.seek(index49_0);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA49_10 = input.LA(1);

                         
                        int index49_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_10==LEFT_PAREN) ) {s = 30;}

                        else if ( (LA49_10==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_10>=ATTRIBUTES && LA49_10<=GLOBAL)||LA49_10==COMMA||(LA49_10>=QUERY && LA49_10<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_10);
                        if ( s>=0 ) return s;
                        break;
                    case 21 : 
                        int LA49_53 = input.LA(1);

                         
                        int index49_53 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_53==RIGHT_SQUARE) ) {s = 92;}

                        else if ( (LA49_53==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_53>=ATTRIBUTES && LA49_53<=GLOBAL)||LA49_53==COMMA||(LA49_53>=QUERY && LA49_53<=LEFT_SQUARE)||(LA49_53>=THEN && LA49_53<=79)) && (synpred2())) {s = 13;}

                        else if ( (LA49_53==LEFT_PAREN) && (synpred2())) {s = 67;}

                         
                        input.seek(index49_53);
                        if ( s>=0 ) return s;
                        break;
                    case 22 : 
                        int LA49_34 = input.LA(1);

                         
                        int index49_34 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_34);
                        if ( s>=0 ) return s;
                        break;
                    case 23 : 
                        int LA49_25 = input.LA(1);

                         
                        int index49_25 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_25);
                        if ( s>=0 ) return s;
                        break;
                    case 24 : 
                        int LA49_59 = input.LA(1);

                         
                        int index49_59 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_59);
                        if ( s>=0 ) return s;
                        break;
                    case 25 : 
                        int LA49_63 = input.LA(1);

                         
                        int index49_63 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_63);
                        if ( s>=0 ) return s;
                        break;
                    case 26 : 
                        int LA49_51 = input.LA(1);

                         
                        int index49_51 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_51==ID) ) {s = 89;}

                        else if ( (LA49_51==LEFT_PAREN) ) {s = 90;}

                        else if ( (LA49_51==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_51>=ATTRIBUTES && LA49_51<=FUNCTION)||(LA49_51>=DOT && LA49_51<=GLOBAL)||LA49_51==COMMA||(LA49_51>=QUERY && LA49_51<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_51);
                        if ( s>=0 ) return s;
                        break;
                    case 27 : 
                        int LA49_48 = input.LA(1);

                         
                        int index49_48 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_48);
                        if ( s>=0 ) return s;
                        break;
                    case 28 : 
                        int LA49_40 = input.LA(1);

                         
                        int index49_40 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_40);
                        if ( s>=0 ) return s;
                        break;
                    case 29 : 
                        int LA49_19 = input.LA(1);

                         
                        int index49_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_19==LEFT_PAREN) ) {s = 45;}

                        else if ( (LA49_19==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_19>=ATTRIBUTES && LA49_19<=GLOBAL)||LA49_19==COMMA||(LA49_19>=QUERY && LA49_19<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_19);
                        if ( s>=0 ) return s;
                        break;
                    case 30 : 
                        int LA49_21 = input.LA(1);

                         
                        int index49_21 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_21==LEFT_PAREN) ) {s = 48;}

                        else if ( (LA49_21==ID) ) {s = 49;}

                        else if ( (LA49_21==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_21>=ATTRIBUTES && LA49_21<=FUNCTION)||(LA49_21>=DOT && LA49_21<=GLOBAL)||LA49_21==COMMA||(LA49_21>=QUERY && LA49_21<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_21);
                        if ( s>=0 ) return s;
                        break;
                    case 31 : 
                        int LA49_33 = input.LA(1);

                         
                        int index49_33 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_33==RIGHT_SQUARE) ) {s = 68;}

                        else if ( (LA49_33==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_33>=ATTRIBUTES && LA49_33<=GLOBAL)||LA49_33==COMMA||(LA49_33>=QUERY && LA49_33<=LEFT_SQUARE)||(LA49_33>=THEN && LA49_33<=79)) && (synpred2())) {s = 13;}

                        else if ( (LA49_33==LEFT_PAREN) && (synpred2())) {s = 67;}

                         
                        input.seek(index49_33);
                        if ( s>=0 ) return s;
                        break;
                    case 32 : 
                        int LA49_58 = input.LA(1);

                         
                        int index49_58 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_58==RIGHT_SQUARE) ) {s = 96;}

                        else if ( (LA49_58==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_58>=ATTRIBUTES && LA49_58<=GLOBAL)||LA49_58==COMMA||(LA49_58>=QUERY && LA49_58<=LEFT_SQUARE)||(LA49_58>=THEN && LA49_58<=79)) && (synpred2())) {s = 13;}

                        else if ( (LA49_58==LEFT_PAREN) && (synpred2())) {s = 67;}

                         
                        input.seek(index49_58);
                        if ( s>=0 ) return s;
                        break;
                    case 33 : 
                        int LA49_62 = input.LA(1);

                         
                        int index49_62 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_62==RIGHT_SQUARE) ) {s = 100;}

                        else if ( (LA49_62==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_62>=ATTRIBUTES && LA49_62<=GLOBAL)||LA49_62==COMMA||(LA49_62>=QUERY && LA49_62<=LEFT_SQUARE)||(LA49_62>=THEN && LA49_62<=79)) && (synpred2())) {s = 13;}

                        else if ( (LA49_62==LEFT_PAREN) && (synpred2())) {s = 67;}

                         
                        input.seek(index49_62);
                        if ( s>=0 ) return s;
                        break;
                    case 34 : 
                        int LA49_30 = input.LA(1);

                         
                        int index49_30 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_30);
                        if ( s>=0 ) return s;
                        break;
                    case 35 : 
                        int LA49_4 = input.LA(1);

                         
                        int index49_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_4==LEFT_PAREN) ) {s = 14;}

                        else if ( (LA49_4==EXISTS) ) {s = 15;}

                        else if ( (LA49_4==NOT) ) {s = 16;}

                        else if ( (LA49_4==EVAL) ) {s = 17;}

                        else if ( (LA49_4==ID) ) {s = 18;}

                        else if ( (LA49_4==FORALL) ) {s = 19;}

                        else if ( (LA49_4==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_4>=ATTRIBUTES && LA49_4<=FUNCTION)||(LA49_4>=DOT && LA49_4<=GLOBAL)||LA49_4==COMMA||(LA49_4>=QUERY && LA49_4<=FROM)||(LA49_4>=ACCUMULATE && LA49_4<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_4);
                        if ( s>=0 ) return s;
                        break;
                    case 36 : 
                        int LA49_17 = input.LA(1);

                         
                        int index49_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_17==LEFT_PAREN) ) {s = 40;}

                        else if ( (LA49_17==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_17>=ATTRIBUTES && LA49_17<=GLOBAL)||LA49_17==COMMA||(LA49_17>=QUERY && LA49_17<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_17);
                        if ( s>=0 ) return s;
                        break;
                    case 37 : 
                        int LA49_55 = input.LA(1);

                         
                        int index49_55 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_55);
                        if ( s>=0 ) return s;
                        break;
                    case 38 : 
                        int LA49_41 = input.LA(1);

                         
                        int index49_41 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_41==ID) ) {s = 77;}

                        else if ( (LA49_41==LEFT_PAREN) ) {s = 78;}

                        else if ( (LA49_41==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_41>=ATTRIBUTES && LA49_41<=FUNCTION)||(LA49_41>=DOT && LA49_41<=GLOBAL)||LA49_41==COMMA||(LA49_41>=QUERY && LA49_41<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_41);
                        if ( s>=0 ) return s;
                        break;
                    case 39 : 
                        int LA49_9 = input.LA(1);

                         
                        int index49_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_9==LEFT_PAREN) ) {s = 28;}

                        else if ( (LA49_9==ID) ) {s = 29;}

                        else if ( (LA49_9==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_9>=ATTRIBUTES && LA49_9<=FUNCTION)||(LA49_9>=DOT && LA49_9<=GLOBAL)||LA49_9==COMMA||(LA49_9>=QUERY && LA49_9<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_9);
                        if ( s>=0 ) return s;
                        break;
                    case 40 : 
                        int LA49_42 = input.LA(1);

                         
                        int index49_42 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA49_42>=ATTRIBUTES && LA49_42<=ID)||LA49_42==GLOBAL||(LA49_42>=QUERY && LA49_42<=WHEN)||LA49_42==ENABLED||LA49_42==SALIENCE||LA49_42==DURATION||LA49_42==FROM||(LA49_42>=INIT && LA49_42<=RESULT)||(LA49_42>=CONTAINS && LA49_42<=IN)||LA49_42==NULL||LA49_42==THEN) ) {s = 79;}

                        else if ( (LA49_42==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( (LA49_42==DOT||LA49_42==COMMA||(LA49_42>=DATE_EFFECTIVE && LA49_42<=DATE_EXPIRES)||LA49_42==BOOL||(LA49_42>=INT && LA49_42<=AGENDA_GROUP)||(LA49_42>=DIALECT && LA49_42<=DOUBLE_AMPER)||(LA49_42>=EXISTS && LA49_42<=ACCUMULATE)||LA49_42==COLLECT||LA49_42==FLOAT||(LA49_42>=LEFT_CURLY && LA49_42<=RIGHT_SQUARE)||(LA49_42>=EOL && LA49_42<=79)) && (synpred2())) {s = 13;}

                        else if ( (LA49_42==LEFT_PAREN) && (synpred2())) {s = 67;}

                         
                        input.seek(index49_42);
                        if ( s>=0 ) return s;
                        break;
                    case 41 : 
                        int LA49_38 = input.LA(1);

                         
                        int index49_38 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_38);
                        if ( s>=0 ) return s;
                        break;
                    case 42 : 
                        int LA49_29 = input.LA(1);

                         
                        int index49_29 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_29==71) ) {s = 60;}

                        else if ( (LA49_29==DOT) ) {s = 61;}

                        else if ( (LA49_29==LEFT_SQUARE) ) {s = 62;}

                        else if ( (LA49_29==LEFT_PAREN) ) {s = 63;}

                        else if ( (LA49_29==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_29>=ATTRIBUTES && LA49_29<=ID)||LA49_29==GLOBAL||LA49_29==COMMA||(LA49_29>=QUERY && LA49_29<=RIGHT_CURLY)||(LA49_29>=RIGHT_SQUARE && LA49_29<=70)||(LA49_29>=72 && LA49_29<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_29);
                        if ( s>=0 ) return s;
                        break;
                    case 43 : 
                        int LA49_27 = input.LA(1);

                         
                        int index49_27 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_27==71) ) {s = 56;}

                        else if ( (LA49_27==DOT) ) {s = 57;}

                        else if ( (LA49_27==LEFT_SQUARE) ) {s = 58;}

                        else if ( (LA49_27==LEFT_PAREN) ) {s = 59;}

                        else if ( (LA49_27==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_27>=ATTRIBUTES && LA49_27<=ID)||LA49_27==GLOBAL||LA49_27==COMMA||(LA49_27>=QUERY && LA49_27<=RIGHT_CURLY)||(LA49_27>=RIGHT_SQUARE && LA49_27<=70)||(LA49_27>=72 && LA49_27<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_27);
                        if ( s>=0 ) return s;
                        break;
                    case 44 : 
                        int LA49_11 = input.LA(1);

                         
                        int index49_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_11==71) ) {s = 31;}

                        else if ( (LA49_11==DOT) ) {s = 32;}

                        else if ( (LA49_11==LEFT_SQUARE) ) {s = 33;}

                        else if ( (LA49_11==LEFT_PAREN) ) {s = 34;}

                        else if ( (LA49_11==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_11>=ATTRIBUTES && LA49_11<=ID)||LA49_11==GLOBAL||LA49_11==COMMA||(LA49_11>=QUERY && LA49_11<=RIGHT_CURLY)||(LA49_11>=RIGHT_SQUARE && LA49_11<=70)||(LA49_11>=72 && LA49_11<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_11);
                        if ( s>=0 ) return s;
                        break;
                    case 45 : 
                        int LA49_52 = input.LA(1);

                         
                        int index49_52 = input.index();
                        input.rewind();
                        s = -1;
                        if ( ((LA49_52>=ATTRIBUTES && LA49_52<=ID)||LA49_52==GLOBAL||(LA49_52>=QUERY && LA49_52<=WHEN)||LA49_52==ENABLED||LA49_52==SALIENCE||LA49_52==DURATION||LA49_52==FROM||(LA49_52>=INIT && LA49_52<=RESULT)||(LA49_52>=CONTAINS && LA49_52<=IN)||LA49_52==NULL||LA49_52==THEN) ) {s = 91;}

                        else if ( (LA49_52==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( (LA49_52==DOT||LA49_52==COMMA||(LA49_52>=DATE_EFFECTIVE && LA49_52<=DATE_EXPIRES)||LA49_52==BOOL||(LA49_52>=INT && LA49_52<=AGENDA_GROUP)||(LA49_52>=DIALECT && LA49_52<=DOUBLE_AMPER)||(LA49_52>=EXISTS && LA49_52<=ACCUMULATE)||LA49_52==COLLECT||LA49_52==FLOAT||(LA49_52>=LEFT_CURLY && LA49_52<=RIGHT_SQUARE)||(LA49_52>=EOL && LA49_52<=79)) && (synpred2())) {s = 13;}

                        else if ( (LA49_52==LEFT_PAREN) && (synpred2())) {s = 67;}

                         
                        input.seek(index49_52);
                        if ( s>=0 ) return s;
                        break;
                    case 46 : 
                        int LA49_39 = input.LA(1);

                         
                        int index49_39 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_39==71) ) {s = 73;}

                        else if ( (LA49_39==DOT) ) {s = 74;}

                        else if ( (LA49_39==LEFT_SQUARE) ) {s = 75;}

                        else if ( (LA49_39==LEFT_PAREN) ) {s = 76;}

                        else if ( (LA49_39==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_39>=ATTRIBUTES && LA49_39<=ID)||LA49_39==GLOBAL||LA49_39==COMMA||(LA49_39>=QUERY && LA49_39<=RIGHT_CURLY)||(LA49_39>=RIGHT_SQUARE && LA49_39<=70)||(LA49_39>=72 && LA49_39<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_39);
                        if ( s>=0 ) return s;
                        break;
                    case 47 : 
                        int LA49_37 = input.LA(1);

                         
                        int index49_37 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_37==71) ) {s = 69;}

                        else if ( (LA49_37==DOT) ) {s = 70;}

                        else if ( (LA49_37==LEFT_SQUARE) ) {s = 71;}

                        else if ( (LA49_37==LEFT_PAREN) ) {s = 72;}

                        else if ( (LA49_37==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_37>=ATTRIBUTES && LA49_37<=ID)||LA49_37==GLOBAL||LA49_37==COMMA||(LA49_37>=QUERY && LA49_37<=RIGHT_CURLY)||(LA49_37>=RIGHT_SQUARE && LA49_37<=70)||(LA49_37>=72 && LA49_37<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_37);
                        if ( s>=0 ) return s;
                        break;
                    case 48 : 
                        int LA49_18 = input.LA(1);

                         
                        int index49_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_18==71) ) {s = 41;}

                        else if ( (LA49_18==DOT) ) {s = 42;}

                        else if ( (LA49_18==LEFT_SQUARE) ) {s = 43;}

                        else if ( (LA49_18==LEFT_PAREN) ) {s = 44;}

                        else if ( (LA49_18==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_18>=ATTRIBUTES && LA49_18<=ID)||LA49_18==GLOBAL||LA49_18==COMMA||(LA49_18>=QUERY && LA49_18<=RIGHT_CURLY)||(LA49_18>=RIGHT_SQUARE && LA49_18<=70)||(LA49_18>=72 && LA49_18<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_18);
                        if ( s>=0 ) return s;
                        break;
                    case 49 : 
                        int LA49_35 = input.LA(1);

                         
                        int index49_35 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_35);
                        if ( s>=0 ) return s;
                        break;
                    case 50 : 
                        int LA49_2 = input.LA(1);

                         
                        int index49_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_2==OR) ) {s = 4;}

                        else if ( (LA49_2==AND) ) {s = 5;}

                        else if ( (LA49_2==EXISTS) ) {s = 6;}

                        else if ( (LA49_2==LEFT_PAREN) ) {s = 7;}

                        else if ( (LA49_2==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( (LA49_2==NOT) ) {s = 9;}

                        else if ( (LA49_2==EVAL) ) {s = 10;}

                        else if ( (LA49_2==ID) ) {s = 11;}

                        else if ( (LA49_2==FORALL) ) {s = 12;}

                        else if ( ((LA49_2>=ATTRIBUTES && LA49_2<=FUNCTION)||(LA49_2>=DOT && LA49_2<=GLOBAL)||LA49_2==COMMA||(LA49_2>=QUERY && LA49_2<=LOCK_ON_ACTIVE)||LA49_2==DOUBLE_PIPE||(LA49_2>=DOUBLE_AMPER && LA49_2<=FROM)||(LA49_2>=ACCUMULATE && LA49_2<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_2);
                        if ( s>=0 ) return s;
                        break;
                    case 51 : 
                        int LA49_14 = input.LA(1);

                         
                        int index49_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_14);
                        if ( s>=0 ) return s;
                        break;
                    case 52 : 
                        int LA49_47 = input.LA(1);

                         
                        int index49_47 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_47==71) ) {s = 81;}

                        else if ( (LA49_47==DOT) ) {s = 82;}

                        else if ( (LA49_47==LEFT_SQUARE) ) {s = 83;}

                        else if ( (LA49_47==LEFT_PAREN) ) {s = 84;}

                        else if ( (LA49_47==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_47>=ATTRIBUTES && LA49_47<=ID)||LA49_47==GLOBAL||LA49_47==COMMA||(LA49_47>=QUERY && LA49_47<=RIGHT_CURLY)||(LA49_47>=RIGHT_SQUARE && LA49_47<=70)||(LA49_47>=72 && LA49_47<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_47);
                        if ( s>=0 ) return s;
                        break;
                    case 53 : 
                        int LA49_15 = input.LA(1);

                         
                        int index49_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_15==LEFT_PAREN) ) {s = 36;}

                        else if ( (LA49_15==ID) ) {s = 37;}

                        else if ( (LA49_15==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_15>=ATTRIBUTES && LA49_15<=FUNCTION)||(LA49_15>=DOT && LA49_15<=GLOBAL)||LA49_15==COMMA||(LA49_15>=QUERY && LA49_15<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_15);
                        if ( s>=0 ) return s;
                        break;
                    case 54 : 
                        int LA49_23 = input.LA(1);

                         
                        int index49_23 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_23==71) ) {s = 51;}

                        else if ( (LA49_23==DOT) ) {s = 52;}

                        else if ( (LA49_23==LEFT_SQUARE) ) {s = 53;}

                        else if ( (LA49_23==LEFT_PAREN) ) {s = 54;}

                        else if ( (LA49_23==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_23>=ATTRIBUTES && LA49_23<=ID)||LA49_23==GLOBAL||LA49_23==COMMA||(LA49_23>=QUERY && LA49_23<=RIGHT_CURLY)||(LA49_23>=RIGHT_SQUARE && LA49_23<=70)||(LA49_23>=72 && LA49_23<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_23);
                        if ( s>=0 ) return s;
                        break;
                    case 55 : 
                        int LA49_49 = input.LA(1);

                         
                        int index49_49 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_49==71) ) {s = 85;}

                        else if ( (LA49_49==DOT) ) {s = 86;}

                        else if ( (LA49_49==LEFT_SQUARE) ) {s = 87;}

                        else if ( (LA49_49==LEFT_PAREN) ) {s = 88;}

                        else if ( (LA49_49==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_49>=ATTRIBUTES && LA49_49<=ID)||LA49_49==GLOBAL||LA49_49==COMMA||(LA49_49>=QUERY && LA49_49<=RIGHT_CURLY)||(LA49_49>=RIGHT_SQUARE && LA49_49<=70)||(LA49_49>=72 && LA49_49<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_49);
                        if ( s>=0 ) return s;
                        break;
                    case 56 : 
                        int LA49_44 = input.LA(1);

                         
                        int index49_44 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_44);
                        if ( s>=0 ) return s;
                        break;
                    case 57 : 
                        int LA49_64 = input.LA(1);

                         
                        int index49_64 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_64==DOT) ) {s = 101;}

                        else if ( (LA49_64==LEFT_SQUARE) ) {s = 102;}

                        else if ( (LA49_64==LEFT_PAREN) ) {s = 103;}

                        else if ( (LA49_64==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_64>=ATTRIBUTES && LA49_64<=ID)||LA49_64==GLOBAL||LA49_64==COMMA||(LA49_64>=QUERY && LA49_64<=RIGHT_CURLY)||(LA49_64>=RIGHT_SQUARE && LA49_64<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_64);
                        if ( s>=0 ) return s;
                        break;
                    case 58 : 
                        int LA49_28 = input.LA(1);

                         
                        int index49_28 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 13;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_28);
                        if ( s>=0 ) return s;
                        break;
                    case 59 : 
                        int LA49_24 = input.LA(1);

                         
                        int index49_24 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_24==LEFT_PAREN) ) {s = 55;}

                        else if ( (LA49_24==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_24>=ATTRIBUTES && LA49_24<=GLOBAL)||LA49_24==COMMA||(LA49_24>=QUERY && LA49_24<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_24);
                        if ( s>=0 ) return s;
                        break;
                    case 60 : 
                        int LA49_66 = input.LA(1);

                         
                        int index49_66 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_66==LEFT_SQUARE) ) {s = 33;}

                        else if ( (LA49_66==LEFT_PAREN) ) {s = 34;}

                        else if ( (LA49_66==DOT) ) {s = 32;}

                        else if ( (LA49_66==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_66>=ATTRIBUTES && LA49_66<=ID)||LA49_66==GLOBAL||LA49_66==COMMA||(LA49_66>=QUERY && LA49_66<=RIGHT_CURLY)||(LA49_66>=RIGHT_SQUARE && LA49_66<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_66);
                        if ( s>=0 ) return s;
                        break;
                    case 61 : 
                        int LA49_5 = input.LA(1);

                         
                        int index49_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA49_5==EXISTS) ) {s = 20;}

                        else if ( (LA49_5==NOT) ) {s = 21;}

                        else if ( (LA49_5==EVAL) ) {s = 22;}

                        else if ( (LA49_5==ID) ) {s = 23;}

                        else if ( (LA49_5==FORALL) ) {s = 24;}

                        else if ( (LA49_5==LEFT_PAREN) ) {s = 25;}

                        else if ( (LA49_5==RIGHT_PAREN) && (synpred2())) {s = 8;}

                        else if ( ((LA49_5>=ATTRIBUTES && LA49_5<=FUNCTION)||(LA49_5>=DOT && LA49_5<=GLOBAL)||LA49_5==COMMA||(LA49_5>=QUERY && LA49_5<=FROM)||(LA49_5>=ACCUMULATE && LA49_5<=79)) && (synpred2())) {s = 13;}

                         
                        input.seek(index49_5);
                        if ( s>=0 ) return s;
                        break;
                    case 62 : 
                        int LA49_65 = input.LA(1);

                         
                        int index49_65 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2()) ) {s = 67;}

                        else if ( (true) ) {s = 3;}

                         
                        input.seek(index49_65);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 49, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA63_eotS =
        "\14\uffff";
    static final String DFA63_eofS =
        "\14\uffff";
    static final String DFA63_minS =
        "\1\14\1\uffff\1\4\1\13\1\0\1\10\1\uffff\1\10\4\0";
    static final String DFA63_maxS =
        "\1\45\1\uffff\2\117\1\0\1\117\1\uffff\1\117\4\0";
    static final String DFA63_acceptS =
        "\1\uffff\1\2\4\uffff\1\1\5\uffff";
    static final String DFA63_specialS =
        "\4\uffff\1\0\7\uffff}>";
    static final String[] DFA63_transitionS = {
            "\2\1\25\uffff\1\2\1\uffff\1\1",
            "",
            "\5\1\1\uffff\1\1\1\4\2\uffff\5\1\3\uffff\1\1\1\uffff\1\1\6\uffff"+
            "\1\1\6\uffff\1\1\1\uffff\1\6\1\1\2\uffff\3\1\1\uffff\1\5\1\7"+
            "\1\10\1\11\1\3\1\uffff\1\1\4\uffff\1\1\16\uffff\6\6",
            "\1\12\2\1\25\uffff\1\1\1\uffff\1\1\2\uffff\1\1\7\uffff\5\1\24"+
            "\uffff\7\1",
            "\1\uffff",
            "\1\6\2\uffff\1\13\2\1\6\uffff\1\6\2\uffff\1\6\1\uffff\1\6\11"+
            "\uffff\1\1\1\uffff\1\1\2\uffff\1\1\7\uffff\5\1\2\6\22\uffff"+
            "\7\1",
            "",
            "\1\6\2\uffff\1\13\2\1\6\uffff\1\6\2\uffff\1\6\1\uffff\1\6\11"+
            "\uffff\1\1\1\uffff\1\1\2\uffff\1\1\7\uffff\5\1\2\6\22\uffff"+
            "\7\1",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff"
    };

    static final short[] DFA63_eot = DFA.unpackEncodedString(DFA63_eotS);
    static final short[] DFA63_eof = DFA.unpackEncodedString(DFA63_eofS);
    static final char[] DFA63_min = DFA.unpackEncodedStringToUnsignedChars(DFA63_minS);
    static final char[] DFA63_max = DFA.unpackEncodedStringToUnsignedChars(DFA63_maxS);
    static final short[] DFA63_accept = DFA.unpackEncodedString(DFA63_acceptS);
    static final short[] DFA63_special = DFA.unpackEncodedString(DFA63_specialS);
    static final short[][] DFA63_transition;

    static {
        int numStates = DFA63_transitionS.length;
        DFA63_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA63_transition[i] = DFA.unpackEncodedString(DFA63_transitionS[i]);
        }
    }

    class DFA63 extends DFA {

        public DFA63(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 63;
            this.eot = DFA63_eot;
            this.eof = DFA63_eof;
            this.min = DFA63_min;
            this.max = DFA63_max;
            this.accept = DFA63_accept;
            this.special = DFA63_special;
            this.transition = DFA63_transition;
        }
        public String getDescription() {
            return "()* loopback of 1263:3: ( options {backtrack=true; } : DOUBLE_PIPE and_restr_connective[or] )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA63_4 = input.LA(1);

                         
                        int index63_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3()) ) {s = 6;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index63_4);
                        if ( s>=0 ) return s;
                        break;
            }
            if (backtracking>0) {failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 63, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA64_eotS =
        "\42\uffff";
    static final String DFA64_eofS =
        "\1\1\41\uffff";
    static final String DFA64_minS =
        "\1\14\1\uffff\1\4\1\13\1\uffff\4\10\1\4\1\10\1\4\1\13\1\0\2\10\22"+
        "\0";
    static final String DFA64_maxS =
        "\1\45\1\uffff\2\117\1\uffff\10\117\1\0\2\117\22\0";
    static final String DFA64_acceptS =
        "\1\uffff\1\2\2\uffff\1\1\35\uffff";
    static final String DFA64_specialS =
        "\15\uffff\1\0\24\uffff}>";
    static final String[] DFA64_transitionS = {
            "\2\1\25\uffff\1\1\1\uffff\1\2",
            "",
            "\5\1\1\uffff\1\1\1\11\2\uffff\5\1\3\uffff\1\1\1\uffff\1\1\6"+
            "\uffff\1\1\6\uffff\1\1\1\uffff\1\4\1\1\2\uffff\3\1\1\uffff\1"+
            "\5\1\6\1\7\1\10\1\3\1\uffff\1\1\4\uffff\1\1\16\uffff\6\4",
            "\1\12\2\1\25\uffff\1\1\1\uffff\1\1\2\uffff\1\1\7\uffff\5\1\24"+
            "\uffff\7\1",
            "",
            "\1\4\2\uffff\1\13\2\1\6\uffff\1\4\2\uffff\1\4\1\uffff\1\4\11"+
            "\uffff\1\1\1\uffff\1\1\2\uffff\1\1\7\uffff\5\1\2\4\22\uffff"+
            "\7\1",
            "\1\4\2\uffff\1\13\2\1\6\uffff\1\4\2\uffff\1\4\1\uffff\1\4\11"+
            "\uffff\1\1\1\uffff\1\1\2\uffff\1\1\7\uffff\5\1\2\4\22\uffff"+
            "\7\1",
            "\1\4\2\uffff\1\13\2\1\6\uffff\1\4\2\uffff\1\4\1\uffff\1\4\11"+
            "\uffff\1\1\1\uffff\1\1\2\uffff\1\1\7\uffff\5\1\2\4\22\uffff"+
            "\7\1",
            "\1\4\2\uffff\1\13\2\1\6\uffff\1\4\2\uffff\1\4\1\uffff\1\4\11"+
            "\uffff\1\1\1\uffff\1\1\2\uffff\1\1\7\uffff\5\1\2\4\22\uffff"+
            "\7\1",
            "\5\1\1\uffff\1\1\1\15\2\uffff\5\1\3\uffff\1\1\1\uffff\1\1\6"+
            "\uffff\1\1\6\uffff\1\1\1\uffff\1\4\1\1\2\uffff\3\1\1\uffff\1"+
            "\16\1\17\1\20\1\21\1\14\1\uffff\1\1\4\uffff\1\1\16\uffff\6\4",
            "\1\4\2\uffff\1\22\10\uffff\1\4\2\uffff\1\4\1\uffff\1\4\16\uffff"+
            "\1\1\7\uffff\5\1\2\4\23\uffff\6\1",
            "\7\4\1\37\34\4\1\24\7\4\1\33\1\34\1\35\1\36\1\23\25\4\1\25\1"+
            "\26\1\27\1\30\1\31\1\32",
            "\1\40\1\uffff\1\1\25\uffff\1\1\1\uffff\1\1\2\uffff\1\1\7\uffff"+
            "\5\1\24\uffff\7\1",
            "\1\uffff",
            "\1\4\2\uffff\1\41\1\uffff\1\1\6\uffff\1\4\2\uffff\1\4\1\uffff"+
            "\1\4\11\uffff\1\1\1\uffff\1\1\2\uffff\1\1\7\uffff\5\1\2\4\22"+
            "\uffff\7\1",
            "\1\4\2\uffff\1\41\1\uffff\1\1\6\uffff\1\4\2\uffff\1\4\1\uffff"+
            "\1\4\11\uffff\1\1\1\uffff\1\1\2\uffff\1\1\7\uffff\5\1\2\4\22"+
            "\uffff\7\1",
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
            return "()* loopback of 1285:3: ( options {backtrack=true; } : t= DOUBLE_AMPER constraint_expression[and] )*";
        }
        public int specialStateTransition(int s, IntStream input) throws NoViableAltException {
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA64_13 = input.LA(1);

                         
                        int index64_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4()) ) {s = 4;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index64_13);
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
 

    public static final BitSet FOLLOW_70_in_opt_semicolon40 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit58 = new BitSet(new long[]{0x00000000000344C0L});
    public static final BitSet FOLLOW_statement_in_compilation_unit63 = new BitSet(new long[]{0x00000000000344C2L});
    public static final BitSet FOLLOW_package_statement_in_prolog86 = new BitSet(new long[]{0x00000003FD680012L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_prolog100 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_prolog102 = new BitSet(new long[]{0x00000003FD680002L});
    public static final BitSet FOLLOW_rule_attribute_in_prolog112 = new BitSet(new long[]{0x00000003FD681002L});
    public static final BitSet FOLLOW_COMMA_in_prolog135 = new BitSet(new long[]{0x00000003FD680000L});
    public static final BitSet FOLLOW_rule_attribute_in_prolog140 = new BitSet(new long[]{0x00000003FD681002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_statement184 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement188 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_statement287 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_import_name_in_import_statement310 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement337 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement339 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement362 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name391 = new BitSet(new long[]{0x0000000000000202L,0x0000000000000100L});
    public static final BitSet FOLLOW_DOT_in_import_name403 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_import_name407 = new BitSet(new long[]{0x0000000000000202L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_import_name431 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_global465 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_dotted_name_in_global476 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_global488 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_opt_semicolon_in_global490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function515 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_dotted_name_in_function519 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_function525 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function534 = new BitSet(new long[]{0x085F70408147E5F0L});
    public static final BitSet FOLLOW_dotted_name_in_function543 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_argument_in_function549 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_COMMA_in_function563 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_dotted_name_in_function567 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_argument_in_function573 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function597 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_argument631 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument637 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument639 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_QUERY_in_query669 = new BitSet(new long[]{0x0000000000100100L});
    public static final BitSet FOLLOW_name_in_query673 = new BitSet(new long[]{0x0000078000008900L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_query683 = new BitSet(new long[]{0x0000000000002100L});
    public static final BitSet FOLLOW_ID_in_query715 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_COMMA_in_query763 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_query767 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_query815 = new BitSet(new long[]{0x0000078000008900L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query833 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_END_in_query838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_template866 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_template870 = new BitSet(new long[]{0x0000000000000100L,0x0000000000000040L});
    public static final BitSet FOLLOW_opt_semicolon_in_template872 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_template_slot_in_template887 = new BitSet(new long[]{0x0000000000008100L});
    public static final BitSet FOLLOW_END_in_template902 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_opt_semicolon_in_template904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_in_template_slot950 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_template_slot968 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot970 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule1001 = new BitSet(new long[]{0x0000000000100100L});
    public static final BitSet FOLLOW_name_in_rule1005 = new BitSet(new long[]{0x08000003FD6C0010L});
    public static final BitSet FOLLOW_rule_attributes_in_rule1014 = new BitSet(new long[]{0x0800000000040000L});
    public static final BitSet FOLLOW_WHEN_in_rule1026 = new BitSet(new long[]{0x0800078000000900L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_rule1028 = new BitSet(new long[]{0x0800078000000900L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule1039 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes1069 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_rule_attributes1071 = new BitSet(new long[]{0x00000003FD680000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1079 = new BitSet(new long[]{0x00000003FD681002L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1086 = new BitSet(new long[]{0x00000003FD680000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1091 = new BitSet(new long[]{0x00000003FD681002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1244 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1275 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_enabled1306 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience1341 = new BitSet(new long[]{0x0000000002000800L});
    public static final BitSet FOLLOW_INT_in_salience1352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1398 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1446 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1495 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1525 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1555 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_duration1585 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_INT_in_duration1587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIALECT_in_dialect1615 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_STRING_in_dialect1617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1649 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1695 = new BitSet(new long[]{0x0000078000000902L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_or1757 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_OR_in_lhs_or1759 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1770 = new BitSet(new long[]{0x0000078000002900L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_or1780 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1798 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_set_in_lhs_or1806 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or1822 = new BitSet(new long[]{0x0000000C00000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_and1853 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_AND_in_lhs_and1855 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1866 = new BitSet(new long[]{0x0000078000002900L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_and1876 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1894 = new BitSet(new long[]{0x0000003000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and1902 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and1918 = new BitSet(new long[]{0x0000003000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary1955 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary1965 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary1975 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_unary1985 = new BitSet(new long[]{0x0000004000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_FROM_in_lhs_unary2003 = new BitSet(new long[]{0x085FF8408147C5F0L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary2063 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary2086 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary2110 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary2149 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2158 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary2162 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2164 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary2175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist2197 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist2217 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist2221 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist2251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist2301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_lhs_not2353 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not2366 = new BitSet(new long[]{0x0000078000000900L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not2370 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not2401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not2438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_lhs_eval2484 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_lhs_forall2522 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall2524 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2528 = new BitSet(new long[]{0x0000000000001100L});
    public static final BitSet FOLLOW_COMMA_in_lhs_forall2542 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall2548 = new BitSet(new long[]{0x0000000000003100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall2561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern2594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern2602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_from_statement2629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source2658 = new BitSet(new long[]{0x0000000000000A02L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source2686 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_expression_chain_in_from_source2700 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_expression_chain2729 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_expression_chain2733 = new BitSet(new long[]{0x0200000000000A02L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain2764 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain2798 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain2819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement2858 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2868 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_lhs_pattern_in_accumulate_statement2872 = new BitSet(new long[]{0x0000100000001000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2874 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_INIT_in_accumulate_statement2884 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2895 = new BitSet(new long[]{0x0000200000001000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2898 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement2907 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2911 = new BitSet(new long[]{0x0000400000001000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2914 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement2923 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2927 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement2978 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement2988 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_lhs_pattern_in_collect_statement2992 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement2994 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding3026 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_fact_binding3028 = new BitSet(new long[]{0x0000000000000900L});
    public static final BitSet FOLLOW_fact_in_fact_binding3042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_binding3058 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_fact_in_fact_binding3062 = new BitSet(new long[]{0x0000000C00002000L});
    public static final BitSet FOLLOW_set_in_fact_binding3075 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_fact_in_fact_binding3087 = new BitSet(new long[]{0x0000000C00002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_binding3105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_qualified_id_in_fact3160 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact3172 = new BitSet(new long[]{0x085F72408147EDF0L});
    public static final BitSet FOLLOW_constraints_in_fact3186 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact3197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints3217 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_COMMA_in_constraints3224 = new BitSet(new long[]{0x085F72408147CDF0L});
    public static final BitSet FOLLOW_constraint_in_constraints3233 = new BitSet(new long[]{0x0000000000001002L});
    public static final BitSet FOLLOW_or_constr_in_constraint3266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3289 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr3297 = new BitSet(new long[]{0x085F72408147CDF0L});
    public static final BitSet FOLLOW_and_constr_in_or_constr3306 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3338 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr3346 = new BitSet(new long[]{0x085F72408147CDF0L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr3355 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr3383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr3391 = new BitSet(new long[]{0x085F72408147CDF0L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr3393 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr3396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_unary_constr3402 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_predicate_in_unary_constr3404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_field_constraint3434 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_field_constraint3436 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_field_constraint3457 = new BitSet(new long[]{0x001F010000000802L,0x000000000000FE00L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint3471 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_field_constraint3486 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_predicate_in_field_constraint3488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3517 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective3536 = new BitSet(new long[]{0x001F010000000800L,0x000000000000FC00L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective3548 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3580 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective3601 = new BitSet(new long[]{0x001F010000000800L,0x000000000000FC00L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective3612 = new BitSet(new long[]{0x0000002000000002L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression3648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression3655 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression3662 = new BitSet(new long[]{0x001F010000000800L,0x000000000000FC00L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression3671 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression3677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_simple_operator3708 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_75_in_simple_operator3716 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_76_in_simple_operator3724 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_77_in_simple_operator3732 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_78_in_simple_operator3740 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_79_in_simple_operator3748 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator3756 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3764 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator3768 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_EXCLUDES_in_simple_operator3776 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator3784 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3792 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator3796 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator3804 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3812 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator3816 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator3830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_compound_operator3857 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_NOT_in_compound_operator3869 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_IN_in_compound_operator3871 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator3886 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3890 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator3897 = new BitSet(new long[]{0x0060000002900900L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3901 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator3910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_expression_value3938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_expression_value3951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value3971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_expression_value3985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint4028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint4039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint4052 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint4063 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint4075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint4108 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_DOT_in_enum_constraint4114 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_enum_constraint4118 = new BitSet(new long[]{0x0000000000000202L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate4160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk4199 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk4215 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk4239 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk4276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk4337 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk4353 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk4377 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk4414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk4478 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk4494 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk4518 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk4555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint4600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_qualified_id4628 = new BitSet(new long[]{0x0200000000000202L});
    public static final BitSet FOLLOW_DOT_in_qualified_id4639 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_qualified_id4643 = new BitSet(new long[]{0x0200000000000202L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_qualified_id4665 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_qualified_id4669 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_identifier_in_dotted_name4710 = new BitSet(new long[]{0x0200000000000202L});
    public static final BitSet FOLLOW_DOT_in_dotted_name4721 = new BitSet(new long[]{0x085F70408147C5F0L});
    public static final BitSet FOLLOW_identifier_in_dotted_name4725 = new BitSet(new long[]{0x0200000000000202L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name4747 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name4751 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk4803 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk4819 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk4856 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_opt_semicolon_in_rhs_chunk4858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name4892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_name4900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_identifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred12756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred22790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred33536 = new BitSet(new long[]{0x001F010000000800L,0x000000000000FC00L});
    public static final BitSet FOLLOW_and_restr_connective_in_synpred33548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred43601 = new BitSet(new long[]{0x001F010000000800L,0x000000000000FC00L});
    public static final BitSet FOLLOW_constraint_expression_in_synpred43612 = new BitSet(new long[]{0x0000000000000002L});

}