// $ANTLR 3.0 C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g 2007-06-03 02:26:07

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ATTRIBUTES", "PACKAGE", "IMPORT", "FUNCTION", "GLOBAL", "QUERY", "LEFT_PAREN", "ID", "RIGHT_PAREN", "END", "TEMPLATE", "RULE", "WHEN", "DATE_EFFECTIVE", "STRING", "DATE_EXPIRES", "ENABLED", "BOOL", "SALIENCE", "INT", "NO_LOOP", "AUTO_FOCUS", "ACTIVATION_GROUP", "RULEFLOW_GROUP", "AGENDA_GROUP", "DURATION", "DIALECT", "LOCK_ON_ACTIVE", "ACCUMULATE", "COMMA", "INIT", "ACTION", "RESULT", "COLLECT", "OR", "DOUBLE_PIPE", "DOUBLE_AMPER", "EVAL", "CONTAINS", "NOT", "EXCLUDES", "MATCHES", "MEMBEROF", "IN", "FLOAT", "NULL", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "AND", "FROM", "EXISTS", "FORALL", "THEN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "':'", "'.'", "'.*'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='"
    };
    public static final int PACKAGE=5;
    public static final int FUNCTION=7;
    public static final int ACCUMULATE=32;
    public static final int RIGHT_SQUARE=53;
    public static final int ACTIVATION_GROUP=26;
    public static final int RIGHT_CURLY=51;
    public static final int ATTRIBUTES=4;
    public static final int DIALECT=30;
    public static final int CONTAINS=42;
    public static final int NO_LOOP=24;
    public static final int MEMBEROF=46;
    public static final int LOCK_ON_ACTIVE=31;
    public static final int AGENDA_GROUP=28;
    public static final int FLOAT=48;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=65;
    public static final int NOT=43;
    public static final int AND=54;
    public static final int ID=11;
    public static final int EOF=-1;
    public static final int HexDigit=62;
    public static final int DATE_EFFECTIVE=17;
    public static final int ACTION=35;
    public static final int DOUBLE_PIPE=39;
    public static final int RIGHT_PAREN=12;
    public static final int IMPORT=6;
    public static final int EOL=59;
    public static final int DOUBLE_AMPER=40;
    public static final int THEN=58;
    public static final int IN=47;
    public static final int MATCHES=45;
    public static final int COMMA=33;
    public static final int ENABLED=20;
    public static final int EXISTS=56;
    public static final int RULE=15;
    public static final int EXCLUDES=44;
    public static final int AUTO_FOCUS=25;
    public static final int NULL=49;
    public static final int BOOL=21;
    public static final int FORALL=57;
    public static final int SALIENCE=22;
    public static final int RULEFLOW_GROUP=27;
    public static final int RESULT=36;
    public static final int INT=23;
    public static final int MULTI_LINE_COMMENT=67;
    public static final int DURATION=29;
    public static final int WS=60;
    public static final int TEMPLATE=14;
    public static final int EVAL=41;
    public static final int WHEN=16;
    public static final int UnicodeEscape=63;
    public static final int LEFT_CURLY=50;
    public static final int OR=38;
    public static final int LEFT_PAREN=10;
    public static final int QUERY=9;
    public static final int MISC=68;
    public static final int FROM=55;
    public static final int END=13;
    public static final int GLOBAL=8;
    public static final int COLLECT=37;
    public static final int LEFT_SQUARE=52;
    public static final int INIT=34;
    public static final int OctalEscape=64;
    public static final int EscapeSequence=61;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=66;
    public static final int DATE_EXPIRES=19;
    public static final int STRING=18;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[225+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g"; }

    
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:177:1: opt_semicolon : ( ';' )? ;
    public final void opt_semicolon() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:178:4: ( ( ';' )? )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:178:4: ( ';' )?
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:178:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==69) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ';'
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:181:1: compilation_unit : prolog ( statement )+ ;
    public final void compilation_unit() throws RecognitionException {
        
        		// reset Location information
        		this.location = new Location( Location.LOCATION_UNKNOWN );
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:186:4: ( prolog ( statement )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:186:4: prolog ( statement )+
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit64);
            prolog();
            _fsp--;
            if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:187:3: ( statement )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:187:5: statement
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:190:1: prolog : (n= package_statement )? ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* ;
    public final void prolog() throws RecognitionException {
        String n = null;

        AttributeDescr a = null;


        
        		String packageName = "";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:194:4: ( (n= package_statement )? ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:194:4: (n= package_statement )? ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )*
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:194:4: (n= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==PACKAGE) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:194:6: n= package_statement
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:198:4: ( ATTRIBUTES ':' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==ATTRIBUTES) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:198:5: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_prolog111); if (failed) return ;
                    match(input,70,FOLLOW_70_in_prolog113); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:199:4: ( ( ',' )? a= rule_attribute )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==DATE_EFFECTIVE||(LA6_0>=DATE_EXPIRES && LA6_0<=ENABLED)||LA6_0==SALIENCE||(LA6_0>=NO_LOOP && LA6_0<=LOCK_ON_ACTIVE)||LA6_0==COMMA) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:199:6: ( ',' )? a= rule_attribute
            	    {
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:199:6: ( ',' )?
            	    int alt5=2;
            	    int LA5_0 = input.LA(1);

            	    if ( (LA5_0==COMMA) ) {
            	        alt5=1;
            	    }
            	    switch (alt5) {
            	        case 1 :
            	            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ','
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:206:1: statement : ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query ) ;
    public final void statement() throws RecognitionException {
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:208:2: ( ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:208:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:208:2: ( function_import_statement | import_statement | global | function | t= template | r= rule | q= query )
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:208:4: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement156);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:209:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement162);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:210:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement168);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:211:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement174);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:212:10: t= template
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:213:4: r= rule
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:214:4: q= query
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:218:1: package_statement returns [String packageName] : PACKAGE n= dotted_name[null] opt_semicolon ;
    public final String package_statement() throws RecognitionException {
        String packageName = null;

        String n = null;


        
        		packageName = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:223:3: ( PACKAGE n= dotted_name[null] opt_semicolon )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:223:3: PACKAGE n= dotted_name[null] opt_semicolon
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:230:1: import_statement : imp= IMPORT import_name[importDecl] opt_semicolon ;
    public final void import_statement() throws RecognitionException {
        Token imp=null;

        
                	ImportDescr importDecl = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:234:4: (imp= IMPORT import_name[importDecl] opt_semicolon )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:234:4: imp= IMPORT import_name[importDecl] opt_semicolon
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:245:1: function_import_statement : imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon ;
    public final void function_import_statement() throws RecognitionException {
        Token imp=null;

        
                	FunctionImportDescr importDecl = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:249:4: (imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:249:4: imp= IMPORT FUNCTION import_name[importDecl] opt_semicolon
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:261:1: import_name[ImportDescr importDecl] returns [String name] : id= identifier ( '.' id= identifier )* (star= '.*' )? ;
    public final String import_name(ImportDescr importDecl) throws RecognitionException {
        String name = null;

        Token star=null;
        Token id = null;


        
        		name = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:266:3: (id= identifier ( '.' id= identifier )* (star= '.*' )? )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:266:3: id= identifier ( '.' id= identifier )* (star= '.*' )?
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:272:3: ( '.' id= identifier )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==71) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:272:5: '.' id= identifier
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:279:3: (star= '.*' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==72) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:279:5: star= '.*'
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:289:1: global : loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon ;
    public final void global() throws RecognitionException {
        Token loc=null;
        String type = null;

        Token id = null;


        
        	    GlobalDescr global = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:294:3: (loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:294:3: loc= GLOBAL type= dotted_name[null] id= identifier opt_semicolon
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:312:1: function : loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] ;
    public final void function() throws RecognitionException {
        Token loc=null;
        String retType = null;

        Token n = null;

        String paramType = null;

        String paramName = null;

        String body = null;


        
        		FunctionDescr f = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:317:3: (loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:317:3: loc= FUNCTION (retType= dotted_name[null] )? n= identifier '(' ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )? ')' body= curly_chunk[f]
            {
            loc=(Token)input.LT(1);
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function513); if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:317:16: (retType= dotted_name[null] )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==ID) ) {
                int LA10_1 = input.LA(2);

                if ( ((LA10_1>=ATTRIBUTES && LA10_1<=QUERY)||LA10_1==ID||(LA10_1>=END && LA10_1<=WHEN)||LA10_1==ENABLED||LA10_1==SALIENCE||LA10_1==DURATION||LA10_1==ACCUMULATE||(LA10_1>=INIT && LA10_1<=OR)||(LA10_1>=EVAL && LA10_1<=IN)||LA10_1==NULL||LA10_1==LEFT_SQUARE||(LA10_1>=AND && LA10_1<=THEN)||LA10_1==71) ) {
                    alt10=1;
                }
            }
            switch (alt10) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:317:17: retType= dotted_name[null]
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:326:4: ( (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )* )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( ((LA14_0>=ATTRIBUTES && LA14_0<=QUERY)||LA14_0==ID||(LA14_0>=END && LA14_0<=WHEN)||LA14_0==ENABLED||LA14_0==SALIENCE||LA14_0==DURATION||LA14_0==ACCUMULATE||(LA14_0>=INIT && LA14_0<=OR)||(LA14_0>=EVAL && LA14_0<=IN)||LA14_0==NULL||(LA14_0>=AND && LA14_0<=THEN)) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:326:6: (paramType= dotted_name[null] )? paramName= argument ( ',' (paramType= dotted_name[null] )? paramName= argument )*
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:326:6: (paramType= dotted_name[null] )?
                    int alt11=2;
                    alt11 = dfa11.predict(input);
                    switch (alt11) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:326:7: paramType= dotted_name[null]
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:330:5: ( ',' (paramType= dotted_name[null] )? paramName= argument )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( (LA13_0==COMMA) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:330:7: ',' (paramType= dotted_name[null] )? paramName= argument
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_function565); if (failed) return ;
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:330:11: (paramType= dotted_name[null] )?
                    	    int alt12=2;
                    	    alt12 = dfa12.predict(input);
                    	    switch (alt12) {
                    	        case 1 :
                    	            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:330:12: paramType= dotted_name[null]
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:1: query returns [QueryDescr query] : loc= QUERY queryName= name ( LEFT_PAREN (paramName= ID ( ',' paramName= ID )* )? RIGHT_PAREN )? ( normal_lhs_block[lhs] ) loc= END ;
    public final QueryDescr query() throws RecognitionException {
        QueryDescr query = null;

        Token loc=null;
        Token paramName=null;
        String queryName = null;


        
        		query = null;
        		AndDescr lhs = null;
        		List params = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:353:3: (loc= QUERY queryName= name ( LEFT_PAREN (paramName= ID ( ',' paramName= ID )* )? RIGHT_PAREN )? ( normal_lhs_block[lhs] ) loc= END )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:353:3: loc= QUERY queryName= name ( LEFT_PAREN (paramName= ID ( ',' paramName= ID )* )? RIGHT_PAREN )? ( normal_lhs_block[lhs] ) loc= END
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:361:3: ( LEFT_PAREN (paramName= ID ( ',' paramName= ID )* )? RIGHT_PAREN )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==LEFT_PAREN) ) {
                int LA17_1 = input.LA(2);

                if ( (LA17_1==ID) ) {
                    int LA17_3 = input.LA(3);

                    if ( (LA17_3==RIGHT_PAREN||LA17_3==COMMA) ) {
                        alt17=1;
                    }
                }
                else if ( (LA17_1==RIGHT_PAREN) ) {
                    alt17=1;
                }
            }
            switch (alt17) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:362:4: LEFT_PAREN (paramName= ID ( ',' paramName= ID )* )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_query656); if (failed) return query;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:363:4: (paramName= ID ( ',' paramName= ID )* )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0==ID) ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:364:5: paramName= ID ( ',' paramName= ID )*
                            {
                            if ( backtracking==0 ) {
                               params = new ArrayList(); 
                            }
                            paramName=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_query680); if (failed) return query;
                            if ( backtracking==0 ) {
                               params.add( paramName.getText() ); 
                            }
                            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:367:5: ( ',' paramName= ID )*
                            loop15:
                            do {
                                int alt15=2;
                                int LA15_0 = input.LA(1);

                                if ( (LA15_0==COMMA) ) {
                                    alt15=1;
                                }


                                switch (alt15) {
                            	case 1 :
                            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:367:6: ',' paramName= ID
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_query693); if (failed) return query;
                            	    paramName=(Token)input.LT(1);
                            	    match(input,ID,FOLLOW_ID_in_query697); if (failed) return query;
                            	    if ( backtracking==0 ) {
                            	       params.add( paramName.getText() ); 
                            	    }

                            	    }
                            	    break;

                            	default :
                            	    break loop15;
                                }
                            } while (true);

                            if ( backtracking==0 ) {
                               query.setParameters( (String[]) params.toArray( new String[params.size()] ) ); 
                            }

                            }
                            break;

                    }

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_query734); if (failed) return query;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:373:3: ( normal_lhs_block[lhs] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:374:4: normal_lhs_block[lhs]
            {
            pushFollow(FOLLOW_normal_lhs_block_in_query751);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;

            }

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_query768); if (failed) return query;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:384:1: template returns [FactTemplateDescr template] : loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon ;
    public final FactTemplateDescr template() throws RecognitionException {
        FactTemplateDescr template = null;

        Token loc=null;
        Token templateName = null;

        FieldTemplateDescr slot = null;


        
        		template = null;		
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:389:3: (loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:389:3: loc= TEMPLATE templateName= identifier opt_semicolon (slot= template_slot )+ loc= END opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,TEMPLATE,FOLLOW_TEMPLATE_in_template798); if (failed) return template;
            pushFollow(FOLLOW_identifier_in_template802);
            templateName=identifier();
            _fsp--;
            if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template804);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {
              
              			template = new FactTemplateDescr(templateName.getText());
              			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
              			template.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:395:3: (slot= template_slot )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:396:4: slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template819);
            	    slot=template_slot();
            	    _fsp--;
            	    if (failed) return template;
            	    if ( backtracking==0 ) {
            	      
            	      				template.addFieldTemplate(slot);
            	      			
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

            loc=(Token)input.LT(1);
            match(input,END,FOLLOW_END_in_template836); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template838);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:407:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name[field] n= identifier opt_semicolon ;
    public final FieldTemplateDescr template_slot() throws RecognitionException {
        FieldTemplateDescr field = null;

        String fieldType = null;

        Token n = null;


        
        		field = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:412:11: (fieldType= dotted_name[field] n= identifier opt_semicolon )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:412:11: fieldType= dotted_name[field] n= identifier opt_semicolon
            {
            if ( backtracking==0 ) {
              
              			field = factory.createFieldTemplate();
              	         
            }
            pushFollow(FOLLOW_dotted_name_in_template_slot884);
            fieldType=dotted_name(field);
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {
              
              		        field.setClassType( fieldType );
              		 
            }
            pushFollow(FOLLOW_identifier_in_template_slot902);
            n=identifier();
            _fsp--;
            if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot904);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:428:1: rule returns [RuleDescr rule] : loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] ;
    public final RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        String ruleName = null;


        
        		rule = null;
        		String consequence = "";
        		AndDescr lhs = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:435:3: (loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:435:3: loc= RULE ruleName= name rule_attributes[rule] (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule]
            {
            loc=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule937); if (failed) return rule;
            pushFollow(FOLLOW_name_in_rule941);
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
            pushFollow(FOLLOW_rule_attributes_in_rule950);
            rule_attributes(rule);
            _fsp--;
            if (failed) return rule;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:444:3: (loc= WHEN ( ':' )? ( normal_lhs_block[lhs] ) )?
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==WHEN) ) {
                alt20=1;
            }
            switch (alt20) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:444:5: loc= WHEN ( ':' )? ( normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule959); if (failed) return rule;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:444:14: ( ':' )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);

                    if ( (LA19_0==70) ) {
                        alt19=1;
                    }
                    switch (alt19) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ':'
                            {
                            match(input,70,FOLLOW_70_in_rule961); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				this.location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION );
                      				lhs = new AndDescr(); rule.setLhs( lhs ); 
                      				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      				lhs.setStartCharacter( ((CommonToken)loc).getStartIndex() );
                      			
                    }
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:451:4: ( normal_lhs_block[lhs] )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:453:5: normal_lhs_block[lhs]
                    {
                    pushFollow(FOLLOW_normal_lhs_block_in_rule979);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule1000);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:462:1: rule_attributes[RuleDescr rule] : ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* ;
    public final void rule_attributes(RuleDescr rule) throws RecognitionException {
        AttributeDescr a = null;


        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:464:4: ( ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:464:4: ( ATTRIBUTES ':' )? ( ( ',' )? a= rule_attribute )*
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:464:4: ( ATTRIBUTES ':' )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==ATTRIBUTES) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:464:5: ATTRIBUTES ':'
                    {
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_rule_attributes1021); if (failed) return ;
                    match(input,70,FOLLOW_70_in_rule_attributes1023); if (failed) return ;

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:4: ( ( ',' )? a= rule_attribute )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==DATE_EFFECTIVE||(LA23_0>=DATE_EXPIRES && LA23_0<=ENABLED)||LA23_0==SALIENCE||(LA23_0>=NO_LOOP && LA23_0<=LOCK_ON_ACTIVE)||LA23_0==COMMA) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:6: ( ',' )? a= rule_attribute
            	    {
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:6: ( ',' )?
            	    int alt22=2;
            	    int LA22_0 = input.LA(1);

            	    if ( (LA22_0==COMMA) ) {
            	        alt22=1;
            	    }
            	    switch (alt22) {
            	        case 1 :
            	            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ','
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_rule_attributes1032); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes1037);
            	    a=rule_attribute();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	      
            	      					rule.addAttribute( a );
            	      				
            	    }

            	    }
            	    break;

            	default :
            	    break loop23;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:474:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );
    public final AttributeDescr rule_attribute() throws RecognitionException {
        AttributeDescr d = null;

        AttributeDescr a = null;


        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:479:4: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect )
            int alt24=12;
            switch ( input.LA(1) ) {
            case SALIENCE:
                {
                alt24=1;
                }
                break;
            case NO_LOOP:
                {
                alt24=2;
                }
                break;
            case AGENDA_GROUP:
                {
                alt24=3;
                }
                break;
            case DURATION:
                {
                alt24=4;
                }
                break;
            case ACTIVATION_GROUP:
                {
                alt24=5;
                }
                break;
            case AUTO_FOCUS:
                {
                alt24=6;
                }
                break;
            case DATE_EFFECTIVE:
                {
                alt24=7;
                }
                break;
            case DATE_EXPIRES:
                {
                alt24=8;
                }
                break;
            case ENABLED:
                {
                alt24=9;
                }
                break;
            case RULEFLOW_GROUP:
                {
                alt24=10;
                }
                break;
            case LOCK_ON_ACTIVE:
                {
                alt24=11;
                }
                break;
            case DIALECT:
                {
                alt24=12;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("474:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus | a= date_effective | a= date_expires | a= enabled | a= ruleflow_group | a= lock_on_active | a= dialect );", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:479:4: a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute1078);
                    a=salience();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:480:5: a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute1088);
                    a=no_loop();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:481:5: a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute1099);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:482:5: a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute1112);
                    a=duration();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:483:5: a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute1126);
                    a=activation_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:484:5: a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute1137);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:485:5: a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute1148);
                    a=date_effective();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:486:5: a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute1158);
                    a=date_expires();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 9 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:487:5: a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute1168);
                    a=enabled();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d=a;
                    }

                    }
                    break;
                case 10 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:488:5: a= ruleflow_group
                    {
                    pushFollow(FOLLOW_ruleflow_group_in_rule_attribute1178);
                    a=ruleflow_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 11 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:489:5: a= lock_on_active
                    {
                    pushFollow(FOLLOW_lock_on_active_in_rule_attribute1188);
                    a=lock_on_active();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 12 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:490:5: a= dialect
                    {
                    pushFollow(FOLLOW_dialect_in_rule_attribute1197);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:494:1: date_effective returns [AttributeDescr d] : loc= DATE_EFFECTIVE val= STRING ;
    public final AttributeDescr date_effective() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:499:3: (loc= DATE_EFFECTIVE val= STRING )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:499:3: loc= DATE_EFFECTIVE val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EFFECTIVE,FOLLOW_DATE_EFFECTIVE_in_date_effective1229); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective1233); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:509:1: date_expires returns [AttributeDescr d] : loc= DATE_EXPIRES val= STRING ;
    public final AttributeDescr date_expires() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:514:3: (loc= DATE_EXPIRES val= STRING )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:514:3: loc= DATE_EXPIRES val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DATE_EXPIRES,FOLLOW_DATE_EXPIRES_in_date_expires1266); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1270); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:525:1: enabled returns [AttributeDescr d] : loc= ENABLED t= BOOL ;
    public final AttributeDescr enabled() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:530:4: (loc= ENABLED t= BOOL )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:530:4: loc= ENABLED t= BOOL
            {
            loc=(Token)input.LT(1);
            match(input,ENABLED,FOLLOW_ENABLED_in_enabled1305); if (failed) return d;
            t=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1309); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:543:1: salience returns [AttributeDescr d ] : loc= SALIENCE (i= INT | txt= paren_chunk[d] ) ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;
        String txt = null;


        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:548:3: (loc= SALIENCE (i= INT | txt= paren_chunk[d] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:548:3: loc= SALIENCE (i= INT | txt= paren_chunk[d] )
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience1354); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new AttributeDescr( "salience" );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:554:3: (i= INT | txt= paren_chunk[d] )
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==INT) ) {
                alt25=1;
            }
            else if ( (LA25_0==LEFT_PAREN) ) {
                alt25=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("554:3: (i= INT | txt= paren_chunk[d] )", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:554:5: i= INT
                    {
                    i=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_salience1367); if (failed) return d;
                    if ( backtracking==0 ) {
                      
                      			d.setValue( i.getText() );
                      			d.setEndCharacter( ((CommonToken)i).getStopIndex() );
                      		
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:559:5: txt= paren_chunk[d]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_salience1382);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:566:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );
    public final AttributeDescr no_loop() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:571:3: ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==NO_LOOP) ) {
                int LA26_1 = input.LA(2);

                if ( (LA26_1==BOOL) ) {
                    alt26=2;
                }
                else if ( (LA26_1==EOF||(LA26_1>=IMPORT && LA26_1<=QUERY)||(LA26_1>=TEMPLATE && LA26_1<=DATE_EFFECTIVE)||(LA26_1>=DATE_EXPIRES && LA26_1<=ENABLED)||LA26_1==SALIENCE||(LA26_1>=NO_LOOP && LA26_1<=LOCK_ON_ACTIVE)||LA26_1==COMMA||LA26_1==THEN) ) {
                    alt26=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("566:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 26, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("566:1: no_loop returns [AttributeDescr d] : ( (loc= NO_LOOP ) | (loc= NO_LOOP t= BOOL ) );", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:571:3: (loc= NO_LOOP )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:571:3: (loc= NO_LOOP )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:572:4: loc= NO_LOOP
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1422); if (failed) return d;
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:581:3: (loc= NO_LOOP t= BOOL )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:581:3: (loc= NO_LOOP t= BOOL )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:582:4: loc= NO_LOOP t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,NO_LOOP,FOLLOW_NO_LOOP_in_no_loop1450); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1454); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:594:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );
    public final AttributeDescr auto_focus() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:599:3: ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==AUTO_FOCUS) ) {
                int LA27_1 = input.LA(2);

                if ( (LA27_1==BOOL) ) {
                    alt27=2;
                }
                else if ( (LA27_1==EOF||(LA27_1>=IMPORT && LA27_1<=QUERY)||(LA27_1>=TEMPLATE && LA27_1<=DATE_EFFECTIVE)||(LA27_1>=DATE_EXPIRES && LA27_1<=ENABLED)||LA27_1==SALIENCE||(LA27_1>=NO_LOOP && LA27_1<=LOCK_ON_ACTIVE)||LA27_1==COMMA||LA27_1==THEN) ) {
                    alt27=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("594:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 27, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("594:1: auto_focus returns [AttributeDescr d] : ( (loc= AUTO_FOCUS ) | (loc= AUTO_FOCUS t= BOOL ) );", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:599:3: (loc= AUTO_FOCUS )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:599:3: (loc= AUTO_FOCUS )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:600:4: loc= AUTO_FOCUS
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1503); if (failed) return d;
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:609:3: (loc= AUTO_FOCUS t= BOOL )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:609:3: (loc= AUTO_FOCUS t= BOOL )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:610:4: loc= AUTO_FOCUS t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,AUTO_FOCUS,FOLLOW_AUTO_FOCUS_in_auto_focus1531); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1535); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:622:1: activation_group returns [AttributeDescr d] : loc= ACTIVATION_GROUP n= STRING ;
    public final AttributeDescr activation_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:627:3: (loc= ACTIVATION_GROUP n= STRING )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:627:3: loc= ACTIVATION_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,ACTIVATION_GROUP,FOLLOW_ACTIVATION_GROUP_in_activation_group1580); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1584); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:636:1: ruleflow_group returns [AttributeDescr d] : loc= RULEFLOW_GROUP n= STRING ;
    public final AttributeDescr ruleflow_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:641:3: (loc= RULEFLOW_GROUP n= STRING )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:641:3: loc= RULEFLOW_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,RULEFLOW_GROUP,FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1616); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_ruleflow_group1620); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:650:1: agenda_group returns [AttributeDescr d] : loc= AGENDA_GROUP n= STRING ;
    public final AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:655:3: (loc= AGENDA_GROUP n= STRING )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:655:3: loc= AGENDA_GROUP n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,AGENDA_GROUP,FOLLOW_AGENDA_GROUP_in_agenda_group1652); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1656); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:665:1: duration returns [AttributeDescr d] : loc= DURATION i= INT ;
    public final AttributeDescr duration() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:670:3: (loc= DURATION i= INT )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:670:3: loc= DURATION i= INT
            {
            loc=(Token)input.LT(1);
            match(input,DURATION,FOLLOW_DURATION_in_duration1690); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1694); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:1: dialect returns [AttributeDescr d] : loc= DIALECT n= STRING ;
    public final AttributeDescr dialect() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token n=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:684:3: (loc= DIALECT n= STRING )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:684:3: loc= DIALECT n= STRING
            {
            loc=(Token)input.LT(1);
            match(input,DIALECT,FOLLOW_DIALECT_in_dialect1726); if (failed) return d;
            n=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_dialect1730); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:698:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );
    public final AttributeDescr lock_on_active() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:703:3: ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) )
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==LOCK_ON_ACTIVE) ) {
                int LA28_1 = input.LA(2);

                if ( (LA28_1==BOOL) ) {
                    alt28=2;
                }
                else if ( (LA28_1==EOF||(LA28_1>=IMPORT && LA28_1<=QUERY)||(LA28_1>=TEMPLATE && LA28_1<=DATE_EFFECTIVE)||(LA28_1>=DATE_EXPIRES && LA28_1<=ENABLED)||LA28_1==SALIENCE||(LA28_1>=NO_LOOP && LA28_1<=LOCK_ON_ACTIVE)||LA28_1==COMMA||LA28_1==THEN) ) {
                    alt28=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("698:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );", 28, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("698:1: lock_on_active returns [AttributeDescr d] : ( (loc= LOCK_ON_ACTIVE ) | (loc= LOCK_ON_ACTIVE t= BOOL ) );", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:703:3: (loc= LOCK_ON_ACTIVE )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:703:3: (loc= LOCK_ON_ACTIVE )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:704:4: loc= LOCK_ON_ACTIVE
                    {
                    loc=(Token)input.LT(1);
                    match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1779); if (failed) return d;
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:713:3: (loc= LOCK_ON_ACTIVE t= BOOL )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:713:3: (loc= LOCK_ON_ACTIVE t= BOOL )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:714:4: loc= LOCK_ON_ACTIVE t= BOOL
                    {
                    loc=(Token)input.LT(1);
                    match(input,LOCK_ON_ACTIVE,FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1807); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lock_on_active1811); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:726:1: normal_lhs_block[AndDescr descr] : (d= lhs[descr] )* ;
    public final void normal_lhs_block(AndDescr descr) throws RecognitionException {
        BaseDescr d = null;


        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:728:3: ( (d= lhs[descr] )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:728:3: (d= lhs[descr] )*
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:728:3: (d= lhs[descr] )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( ((LA29_0>=LEFT_PAREN && LA29_0<=ID)||LA29_0==EVAL||LA29_0==NOT||(LA29_0>=EXISTS && LA29_0<=FORALL)) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:728:5: d= lhs[descr]
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1849);
            	    d=lhs(descr);
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       if(d != null) descr.addDescr( d ); 
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
        return ;
    }
    // $ANTLR end normal_lhs_block


    // $ANTLR start lhs
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:734:1: lhs[ConditionalElementDescr ce] returns [BaseDescr d] : l= lhs_or ;
    public final BaseDescr lhs(ConditionalElementDescr ce) throws RecognitionException {
        BaseDescr d = null;

        BaseDescr l = null;


        
        		d=null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:738:4: (l= lhs_or )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:738:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1886);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:742:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );
    public final BaseDescr lhs_pattern() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr f = null;


        
        		d=null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:746:4: (f= fact_binding | f= fact )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==ID) ) {
                int LA30_1 = input.LA(2);

                if ( (LA30_1==70) ) {
                    alt30=1;
                }
                else if ( (LA30_1==LEFT_PAREN||LA30_1==LEFT_SQUARE||LA30_1==71) ) {
                    alt30=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("742:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );", 30, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("742:1: lhs_pattern returns [BaseDescr d] : (f= fact_binding | f= fact );", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:746:4: f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_pattern1914);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = f; 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:747:4: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_pattern1923);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:750:1: from_statement returns [FromDescr d] : ds= from_source[d] ;
    public final FromDescr from_statement() throws RecognitionException {
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;


        
        		d=factory.createFrom();
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:755:2: (ds= from_source[d] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:755:2: ds= from_source[d]
            {
            pushFollow(FOLLOW_from_source_in_from_statement1950);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:765:1: from_source[FromDescr from] returns [DeclarativeInvokerDescr ds] : ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )? ;
    public final DeclarativeInvokerDescr from_source(FromDescr from) throws RecognitionException {
        DeclarativeInvokerDescr ds = null;

        Token ident = null;

        String args = null;


        
        		ds = null;
        		AccessorDescr ad = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:771:3: (ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )? )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:771:3: ident= identifier (args= paren_chunk[from] )? ( expression_chain[from, ad] )?
            {
            pushFollow(FOLLOW_identifier_in_from_source1992);
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:780:3: (args= paren_chunk[from] )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==LEFT_PAREN) ) {
                int LA31_1 = input.LA(2);

                if ( (synpred46()) ) {
                    alt31=1;
                }
            }
            switch (alt31) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:780:4: args= paren_chunk[from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_from_source2003);
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

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:794:3: ( expression_chain[from, ad] )?
            int alt32=2;
            int LA32_0 = input.LA(1);

            if ( (LA32_0==71) ) {
                alt32=1;
            }
            switch (alt32) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: expression_chain[from, ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source2017);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:802:1: expression_chain[FromDescr from, AccessorDescr as] : ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? ) ;
    public final void expression_chain(FromDescr from, AccessorDescr as) throws RecognitionException {
        Token field = null;

        String sqarg = null;

        String paarg = null;


        
          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:808:2: ( ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:808:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:808:2: ( '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )? )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:808:4: '.' field= identifier ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )? ( expression_chain[from, as] )?
            {
            match(input,71,FOLLOW_71_in_expression_chain2046); if (failed) return ;
            pushFollow(FOLLOW_identifier_in_expression_chain2050);
            field=identifier();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              	        fa = new FieldAccessDescr(field.getText());	
              		fa.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
              		fa.setStartCharacter( ((CommonToken)field).getStartIndex() );
              		fa.setEndCharacter( ((CommonToken)field).getStopIndex() );
              	    
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:815:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk[from] | ( LEFT_PAREN )=>paarg= paren_chunk[from] )?
            int alt33=3;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==LEFT_SQUARE) && (synpred48())) {
                alt33=1;
            }
            else if ( (LA33_0==LEFT_PAREN) ) {
                int LA33_2 = input.LA(2);

                if ( (synpred49()) ) {
                    alt33=2;
                }
            }
            switch (alt33) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:816:6: ( LEFT_SQUARE )=>sqarg= square_chunk[from]
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain2081);
                    sqarg=square_chunk(from);
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      	          fa.setArgument( sqarg );	
                      	      
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:821:6: ( LEFT_PAREN )=>paarg= paren_chunk[from]
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain2115);
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:835:4: ( expression_chain[from, as] )?
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==71) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: expression_chain[from, as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain2136);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:839:1: accumulate_statement returns [AccumulateDescr d] : loc= ACCUMULATE LEFT_PAREN pattern= lhs_pattern ( COMMA )? INIT text= paren_chunk[null] ( COMMA )? ACTION text= paren_chunk[null] ( COMMA )? RESULT text= paren_chunk[null] loc= RIGHT_PAREN ;
    public final AccumulateDescr accumulate_statement() throws RecognitionException {
        AccumulateDescr d = null;

        Token loc=null;
        BaseDescr pattern = null;

        String text = null;


        
        		d = factory.createAccumulate();
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:844:10: (loc= ACCUMULATE LEFT_PAREN pattern= lhs_pattern ( COMMA )? INIT text= paren_chunk[null] ( COMMA )? ACTION text= paren_chunk[null] ( COMMA )? RESULT text= paren_chunk[null] loc= RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:844:10: loc= ACCUMULATE LEFT_PAREN pattern= lhs_pattern ( COMMA )? INIT text= paren_chunk[null] ( COMMA )? ACTION text= paren_chunk[null] ( COMMA )? RESULT text= paren_chunk[null] loc= RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_accumulate_statement2177); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement2187); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_accumulate_statement2191);
            pattern=lhs_pattern();
            _fsp--;
            if (failed) return d;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:850:34: ( COMMA )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==COMMA) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2193); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
              
              		        d.setSourcePattern( (PatternDescr)pattern );
              		
            }
            match(input,INIT,FOLLOW_INIT_in_accumulate_statement2203); if (failed) return d;
            if ( backtracking==0 ) {
              
              			location.setType( Location.LOCATION_LHS_FROM_ACCUMULATE_INIT );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2214);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:858:26: ( COMMA )?
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==COMMA) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2217); if (failed) return d;

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
            match(input,ACTION,FOLLOW_ACTION_in_accumulate_statement2226); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2230);
            text=paren_chunk(null);
            _fsp--;
            if (failed) return d;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:866:33: ( COMMA )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==COMMA) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_accumulate_statement2233); if (failed) return d;

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
            match(input,RESULT,FOLLOW_RESULT_in_accumulate_statement2242); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement2246);
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
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement2258); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:888:1: collect_statement returns [CollectDescr d] : loc= COLLECT LEFT_PAREN pattern= lhs_pattern loc= RIGHT_PAREN ;
    public final CollectDescr collect_statement() throws RecognitionException {
        CollectDescr d = null;

        Token loc=null;
        BaseDescr pattern = null;


        
        		d = factory.createCollect();
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:893:10: (loc= COLLECT LEFT_PAREN pattern= lhs_pattern loc= RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:893:10: loc= COLLECT LEFT_PAREN pattern= lhs_pattern loc= RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,COLLECT,FOLLOW_COLLECT_in_collect_statement2301); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_FROM_COLLECT );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement2311); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_collect_statement2315);
            pattern=lhs_pattern();
            _fsp--;
            if (failed) return d;
            loc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement2319); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:907:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public final BaseDescr fact_binding() throws RecognitionException {
        BaseDescr d = null;

        Token id=null;
        BaseDescr fe = null;


        
        		d=null;
        		boolean multi=false;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:913:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:913:4: id= ID ':' fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding2353); if (failed) return d;
            match(input,70,FOLLOW_70_in_fact_binding2355); if (failed) return d;
            if ( backtracking==0 ) {
              
               		        // handling incomplete parsing
               		        d = new PatternDescr( );
               		        ((PatternDescr) d).setIdentifier( id.getText() );
               		
            }
            pushFollow(FOLLOW_fact_expression_in_fact_binding2368);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:929:2: fact_expression[String id] returns [BaseDescr pd] : ( LEFT_PAREN fe= fact_expression[id] RIGHT_PAREN | f= fact ( ( OR | DOUBLE_PIPE ) f= fact )* );
    public final BaseDescr fact_expression(String id) throws RecognitionException {
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:934:5: ( LEFT_PAREN fe= fact_expression[id] RIGHT_PAREN | f= fact ( ( OR | DOUBLE_PIPE ) f= fact )* )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==LEFT_PAREN) ) {
                alt39=1;
            }
            else if ( (LA39_0==ID) ) {
                alt39=2;
            }
            else {
                if (backtracking>0) {failed=true; return pd;}
                NoViableAltException nvae =
                    new NoViableAltException("929:2: fact_expression[String id] returns [BaseDescr pd] : ( LEFT_PAREN fe= fact_expression[id] RIGHT_PAREN | f= fact ( ( OR | DOUBLE_PIPE ) f= fact )* );", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:934:5: LEFT_PAREN fe= fact_expression[id] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression2400); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_fact_expression2404);
                    fe=fact_expression(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression2407); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:935:6: f= fact ( ( OR | DOUBLE_PIPE ) f= fact )*
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression2418);
                    f=fact();
                    _fsp--;
                    if (failed) return pd;
                    if ( backtracking==0 ) {
                      
                       			((PatternDescr)f).setIdentifier( id );
                       			pd = f;
                       		
                    }
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:940:4: ( ( OR | DOUBLE_PIPE ) f= fact )*
                    loop38:
                    do {
                        int alt38=2;
                        int LA38_0 = input.LA(1);

                        if ( ((LA38_0>=OR && LA38_0<=DOUBLE_PIPE)) ) {
                            int LA38_7 = input.LA(2);

                            if ( (synpred56()) ) {
                                alt38=1;
                            }


                        }


                        switch (alt38) {
                    	case 1 :
                    	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:940:6: ( OR | DOUBLE_PIPE ) f= fact
                    	    {
                    	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return pd;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression2430);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      	if ( ! multi ) {
                    	       					BaseDescr first = pd;
                    	       					pd = new OrDescr();
                    	       					((OrDescr)pd).addDescr( first );
                    	       					multi=true;
                    	       				}
                    	       			
                    	    }
                    	    pushFollow(FOLLOW_fact_in_fact_expression2448);
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
                    	    break loop38;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:956:1: fact returns [BaseDescr d] : id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN ;
    public final BaseDescr fact() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        Token endLoc=null;
        String id = null;


        
        		d=null;
        		PatternDescr pattern = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:962:11: (id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:962:11: id= dotted_name[d] loc= LEFT_PAREN ( constraints[pattern] )? endLoc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {
              
               			pattern = new PatternDescr( );
               			d = pattern; 
               	        
            }
            pushFollow(FOLLOW_dotted_name_in_fact2509);
            id=dotted_name(d);
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               		        pattern.setObjectType( id );
               		        pattern.setEndCharacter( -1 );
               		
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact2523); if (failed) return d;
            if ( backtracking==0 ) {
              
              		                location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START );
                          			location.setProperty( Location.LOCATION_PROPERTY_CLASS_NAME, id );
               				
               				pattern.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
               			        pattern.setLeftParentCharacter( ((CommonToken)loc).getStartIndex() );
               			
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:978:4: ( constraints[pattern] )?
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( ((LA40_0>=ATTRIBUTES && LA40_0<=ID)||(LA40_0>=END && LA40_0<=WHEN)||LA40_0==ENABLED||LA40_0==SALIENCE||LA40_0==DURATION||LA40_0==ACCUMULATE||(LA40_0>=INIT && LA40_0<=OR)||(LA40_0>=EVAL && LA40_0<=IN)||LA40_0==NULL||(LA40_0>=AND && LA40_0<=THEN)) ) {
                alt40=1;
            }
            switch (alt40) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:978:6: constraints[pattern]
                    {
                    pushFollow(FOLLOW_constraints_in_fact2533);
                    constraints(pattern);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            endLoc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact2546); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:991:1: constraints[PatternDescr pattern] : constraint[pattern] ( ',' constraint[pattern] )* ;
    public final void constraints(PatternDescr pattern) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:992:4: ( constraint[pattern] ( ',' constraint[pattern] )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:992:4: constraint[pattern] ( ',' constraint[pattern] )*
            {
            pushFollow(FOLLOW_constraint_in_constraints2566);
            constraint(pattern);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:993:3: ( ',' constraint[pattern] )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0==COMMA) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:993:5: ',' constraint[pattern]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_constraints2573); if (failed) return ;
            	    if ( backtracking==0 ) {
            	       location.setType( Location.LOCATION_LHS_INSIDE_CONDITION_START ); 
            	    }
            	    pushFollow(FOLLOW_constraint_in_constraints2582);
            	    constraint(pattern);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop41;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:998:1: constraint[PatternDescr pattern] : or_constr[top] ;
    public final void constraint(PatternDescr pattern) throws RecognitionException {
        
        		ConditionalElementDescr top = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1003:3: ( or_constr[top] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1003:3: or_constr[top]
            {
            if ( backtracking==0 ) {
              
              			top = pattern.getConstraint();
              		
            }
            pushFollow(FOLLOW_or_constr_in_constraint2615);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1009:1: or_constr[ConditionalElementDescr base] : and_constr[or] (t= DOUBLE_PIPE and_constr[or] )* ;
    public final void or_constr(ConditionalElementDescr base) throws RecognitionException {
        Token t=null;

        
        		OrDescr or = new OrDescr();
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1014:3: ( and_constr[or] (t= DOUBLE_PIPE and_constr[or] )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1014:3: and_constr[or] (t= DOUBLE_PIPE and_constr[or] )*
            {
            pushFollow(FOLLOW_and_constr_in_or_constr2638);
            and_constr(or);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1015:3: (t= DOUBLE_PIPE and_constr[or] )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==DOUBLE_PIPE) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1015:5: t= DOUBLE_PIPE and_constr[or]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_constr2648); if (failed) return ;
            	    if ( backtracking==0 ) {
            	      
            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_constr_in_or_constr2658);
            	    and_constr(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop42;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1030:1: and_constr[ConditionalElementDescr base] : unary_constr[and] (t= DOUBLE_AMPER unary_constr[and] )* ;
    public final void and_constr(ConditionalElementDescr base) throws RecognitionException {
        Token t=null;

        
        		AndDescr and = new AndDescr();
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1035:3: ( unary_constr[and] (t= DOUBLE_AMPER unary_constr[and] )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1035:3: unary_constr[and] (t= DOUBLE_AMPER unary_constr[and] )*
            {
            pushFollow(FOLLOW_unary_constr_in_and_constr2690);
            unary_constr(and);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1036:3: (t= DOUBLE_AMPER unary_constr[and] )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==DOUBLE_AMPER) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1036:5: t= DOUBLE_AMPER unary_constr[and]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_constr2700); if (failed) return ;
            	    if ( backtracking==0 ) {
            	      
            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_START);
            	      			
            	    }
            	    pushFollow(FOLLOW_unary_constr_in_and_constr2710);
            	    unary_constr(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop43;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1051:1: unary_constr[ConditionalElementDescr base] : ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] ) ;
    public final void unary_constr(ConditionalElementDescr base) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1053:3: ( ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1053:3: ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1053:3: ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] )
            int alt44=3;
            switch ( input.LA(1) ) {
            case ATTRIBUTES:
            case PACKAGE:
            case IMPORT:
            case FUNCTION:
            case GLOBAL:
            case QUERY:
            case ID:
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
                alt44=1;
                }
                break;
            case EVAL:
                {
                int LA44_28 = input.LA(2);

                if ( (synpred61()) ) {
                    alt44=1;
                }
                else if ( (true) ) {
                    alt44=3;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1053:3: ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] )", 44, 28, input);

                    throw nvae;
                }
                }
                break;
            case LEFT_PAREN:
                {
                alt44=2;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1053:3: ( field_constraint[base] | LEFT_PAREN or_constr[base] RIGHT_PAREN | EVAL predicate[base] )", 44, 0, input);

                throw nvae;
            }

            switch (alt44) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1053:5: field_constraint[base]
                    {
                    pushFollow(FOLLOW_field_constraint_in_unary_constr2738);
                    field_constraint(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1054:5: LEFT_PAREN or_constr[base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_unary_constr2746); if (failed) return ;
                    pushFollow(FOLLOW_or_constr_in_unary_constr2748);
                    or_constr(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_unary_constr2751); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1055:5: EVAL predicate[base]
                    {
                    match(input,EVAL,FOLLOW_EVAL_in_unary_constr2757); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_unary_constr2759);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1059:1: field_constraint[ConditionalElementDescr base] : (fb= ID ':' )? f= identifier ( or_restr_connective[top] | '->' predicate[base] )? ;
    public final void field_constraint(ConditionalElementDescr base) throws RecognitionException {
        Token fb=null;
        Token f = null;


        
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		RestrictionConnectiveDescr top = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1066:3: ( (fb= ID ':' )? f= identifier ( or_restr_connective[top] | '->' predicate[base] )? )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1066:3: (fb= ID ':' )? f= identifier ( or_restr_connective[top] | '->' predicate[base] )?
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1066:3: (fb= ID ':' )?
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( (LA45_0==ID) ) {
                int LA45_1 = input.LA(2);

                if ( (LA45_1==70) ) {
                    alt45=1;
                }
            }
            switch (alt45) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1066:5: fb= ID ':'
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_field_constraint2791); if (failed) return ;
                    match(input,70,FOLLOW_70_in_field_constraint2793); if (failed) return ;
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

            pushFollow(FOLLOW_identifier_in_field_constraint2814);
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1097:3: ( or_restr_connective[top] | '->' predicate[base] )?
            int alt46=3;
            int LA46_0 = input.LA(1);

            if ( (LA46_0==LEFT_PAREN||(LA46_0>=CONTAINS && LA46_0<=IN)||(LA46_0>=74 && LA46_0<=79)) ) {
                alt46=1;
            }
            else if ( (LA46_0==73) ) {
                alt46=2;
            }
            switch (alt46) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1098:4: or_restr_connective[top]
                    {
                    pushFollow(FOLLOW_or_restr_connective_in_field_constraint2828);
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1106:4: '->' predicate[base]
                    {
                    match(input,73,FOLLOW_73_in_field_constraint2843); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_field_constraint2845);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1111:1: or_restr_connective[ RestrictionConnectiveDescr base ] : and_restr_connective[or] (t= DOUBLE_PIPE and_restr_connective[or] )* ;
    public final void or_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;

        
        		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1116:3: ( and_restr_connective[or] (t= DOUBLE_PIPE and_restr_connective[or] )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1116:3: and_restr_connective[or] (t= DOUBLE_PIPE and_restr_connective[or] )*
            {
            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective2874);
            and_restr_connective(or);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1117:3: (t= DOUBLE_PIPE and_restr_connective[or] )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==DOUBLE_PIPE) ) {
                    int LA47_2 = input.LA(2);

                    if ( (synpred66()) ) {
                        alt47=1;
                    }


                }


                switch (alt47) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1117:5: t= DOUBLE_PIPE and_restr_connective[or]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_or_restr_connective2884); if (failed) return ;
            	    if ( backtracking==0 ) {
            	      
            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective2896);
            	    and_restr_connective(or);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop47;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1133:1: and_restr_connective[ RestrictionConnectiveDescr base ] : constraint_expression[and] (t= DOUBLE_AMPER constraint_expression[and] )* ;
    public final void and_restr_connective(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;

        
        		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1138:3: ( constraint_expression[and] (t= DOUBLE_AMPER constraint_expression[and] )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1138:3: constraint_expression[and] (t= DOUBLE_AMPER constraint_expression[and] )*
            {
            pushFollow(FOLLOW_constraint_expression_in_and_restr_connective2930);
            constraint_expression(and);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1139:3: (t= DOUBLE_AMPER constraint_expression[and] )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==DOUBLE_AMPER) ) {
                    int LA48_2 = input.LA(2);

                    if ( (synpred67()) ) {
                        alt48=1;
                    }


                }


                switch (alt48) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1139:5: t= DOUBLE_AMPER constraint_expression[and]
            	    {
            	    t=(Token)input.LT(1);
            	    match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_and_restr_connective2940); if (failed) return ;
            	    if ( backtracking==0 ) {
            	      
            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_constraint_expression_in_and_restr_connective2950);
            	    constraint_expression(and);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop48;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1154:1: constraint_expression[RestrictionConnectiveDescr base] : ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN ) ;
    public final void constraint_expression(RestrictionConnectiveDescr base) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1156:3: ( ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1156:3: ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1156:3: ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN )
            int alt49=3;
            switch ( input.LA(1) ) {
            case IN:
                {
                alt49=1;
                }
                break;
            case NOT:
                {
                int LA49_2 = input.LA(2);

                if ( (LA49_2==CONTAINS||(LA49_2>=MATCHES && LA49_2<=MEMBEROF)) ) {
                    alt49=2;
                }
                else if ( (LA49_2==IN) ) {
                    alt49=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1156:3: ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN )", 49, 2, input);

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
                alt49=2;
                }
                break;
            case LEFT_PAREN:
                {
                alt49=3;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1156:3: ( compound_operator[base] | simple_operator[base] | LEFT_PAREN or_restr_connective[base] RIGHT_PAREN )", 49, 0, input);

                throw nvae;
            }

            switch (alt49) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1156:5: compound_operator[base]
                    {
                    pushFollow(FOLLOW_compound_operator_in_constraint_expression2985);
                    compound_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1157:5: simple_operator[base]
                    {
                    pushFollow(FOLLOW_simple_operator_in_constraint_expression2992);
                    simple_operator(base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1158:5: LEFT_PAREN or_restr_connective[base] RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_constraint_expression2999); if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      			location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
                      		
                    }
                    pushFollow(FOLLOW_or_restr_connective_in_constraint_expression3008);
                    or_restr_connective(base);
                    _fsp--;
                    if (failed) return ;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_constraint_expression3014); if (failed) return ;

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1167:1: simple_operator[RestrictionConnectiveDescr base] : (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[op] ;
    public final void simple_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        Token t=null;
        Token n=null;
        RestrictionDescr rd = null;


        
        		String op = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1172:3: ( (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[op] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1172:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF ) rd= expression_value[op]
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1172:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )
            int alt50=13;
            switch ( input.LA(1) ) {
            case 74:
                {
                alt50=1;
                }
                break;
            case 75:
                {
                alt50=2;
                }
                break;
            case 76:
                {
                alt50=3;
                }
                break;
            case 77:
                {
                alt50=4;
                }
                break;
            case 78:
                {
                alt50=5;
                }
                break;
            case 79:
                {
                alt50=6;
                }
                break;
            case CONTAINS:
                {
                alt50=7;
                }
                break;
            case NOT:
                {
                switch ( input.LA(2) ) {
                case MEMBEROF:
                    {
                    alt50=13;
                    }
                    break;
                case MATCHES:
                    {
                    alt50=11;
                    }
                    break;
                case CONTAINS:
                    {
                    alt50=8;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("1172:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )", 50, 8, input);

                    throw nvae;
                }

                }
                break;
            case EXCLUDES:
                {
                alt50=9;
                }
                break;
            case MATCHES:
                {
                alt50=10;
                }
                break;
            case MEMBEROF:
                {
                alt50=12;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1172:3: (t= '==' | t= '>' | t= '>=' | t= '<' | t= '<=' | t= '!=' | t= CONTAINS | n= NOT t= CONTAINS | t= EXCLUDES | t= MATCHES | n= NOT t= MATCHES | t= MEMBEROF | n= NOT t= MEMBEROF )", 50, 0, input);

                throw nvae;
            }

            switch (alt50) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1172:5: t= '=='
                    {
                    t=(Token)input.LT(1);
                    match(input,74,FOLLOW_74_in_simple_operator3045); if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1173:5: t= '>'
                    {
                    t=(Token)input.LT(1);
                    match(input,75,FOLLOW_75_in_simple_operator3053); if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1174:5: t= '>='
                    {
                    t=(Token)input.LT(1);
                    match(input,76,FOLLOW_76_in_simple_operator3061); if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1175:5: t= '<'
                    {
                    t=(Token)input.LT(1);
                    match(input,77,FOLLOW_77_in_simple_operator3069); if (failed) return ;

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1176:5: t= '<='
                    {
                    t=(Token)input.LT(1);
                    match(input,78,FOLLOW_78_in_simple_operator3077); if (failed) return ;

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1177:5: t= '!='
                    {
                    t=(Token)input.LT(1);
                    match(input,79,FOLLOW_79_in_simple_operator3085); if (failed) return ;

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1178:5: t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator3093); if (failed) return ;

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1179:5: n= NOT t= CONTAINS
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3101); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_simple_operator3105); if (failed) return ;

                    }
                    break;
                case 9 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1180:5: t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_simple_operator3113); if (failed) return ;

                    }
                    break;
                case 10 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1181:5: t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator3121); if (failed) return ;

                    }
                    break;
                case 11 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1182:5: n= NOT t= MATCHES
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3129); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_simple_operator3133); if (failed) return ;

                    }
                    break;
                case 12 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1183:5: t= MEMBEROF
                    {
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator3141); if (failed) return ;

                    }
                    break;
                case 13 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1184:5: n= NOT t= MEMBEROF
                    {
                    n=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_simple_operator3149); if (failed) return ;
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_simple_operator3153); if (failed) return ;

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
            pushFollow(FOLLOW_expression_value_in_simple_operator3167);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1206:1: compound_operator[RestrictionConnectiveDescr base] : ( IN | NOT IN ) LEFT_PAREN rd= expression_value[op] ( COMMA rd= expression_value[op] )* RIGHT_PAREN ;
    public final void compound_operator(RestrictionConnectiveDescr base) throws RecognitionException {
        RestrictionDescr rd = null;


        
        		String op = null;
        		RestrictionConnectiveDescr group = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1212:3: ( ( IN | NOT IN ) LEFT_PAREN rd= expression_value[op] ( COMMA rd= expression_value[op] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1212:3: ( IN | NOT IN ) LEFT_PAREN rd= expression_value[op] ( COMMA rd= expression_value[op] )* RIGHT_PAREN
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1212:3: ( IN | NOT IN )
            int alt51=2;
            int LA51_0 = input.LA(1);

            if ( (LA51_0==IN) ) {
                alt51=1;
            }
            else if ( (LA51_0==NOT) ) {
                alt51=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("1212:3: ( IN | NOT IN )", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1212:5: IN
                    {
                    match(input,IN,FOLLOW_IN_in_compound_operator3196); if (failed) return ;
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1220:5: NOT IN
                    {
                    match(input,NOT,FOLLOW_NOT_in_compound_operator3208); if (failed) return ;
                    match(input,IN,FOLLOW_IN_in_compound_operator3210); if (failed) return ;
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

            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_compound_operator3225); if (failed) return ;
            pushFollow(FOLLOW_expression_value_in_compound_operator3229);
            rd=expression_value(op);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              			    if( rd != null ) {
              			        group.addRestriction( rd );
              			    }
              			
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:3: ( COMMA rd= expression_value[op] )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);

                if ( (LA52_0==COMMA) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:5: COMMA rd= expression_value[op]
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_compound_operator3241); if (failed) return ;
            	    pushFollow(FOLLOW_expression_value_in_compound_operator3245);
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
            	    break loop52;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_compound_operator3261); if (failed) return ;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1248:1: expression_value[String op] returns [RestrictionDescr rd] : (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) ;
    public final RestrictionDescr expression_value(String op) throws RecognitionException {
        RestrictionDescr rd = null;

        Token bvc=null;
        String lc = null;

        String rvc = null;


        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1250:3: ( (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1250:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1250:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            int alt53=4;
            switch ( input.LA(1) ) {
            case ID:
                {
                int LA53_1 = input.LA(2);

                if ( (LA53_1==71) ) {
                    alt53=2;
                }
                else if ( (LA53_1==EOF||LA53_1==RIGHT_PAREN||LA53_1==COMMA||(LA53_1>=DOUBLE_PIPE && LA53_1<=DOUBLE_AMPER)) ) {
                    alt53=1;
                }
                else {
                    if (backtracking>0) {failed=true; return rd;}
                    NoViableAltException nvae =
                        new NoViableAltException("1250:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 53, 1, input);

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
                alt53=3;
                }
                break;
            case LEFT_PAREN:
                {
                alt53=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("1250:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 53, 0, input);

                throw nvae;
            }

            switch (alt53) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1250:5: bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_expression_value3289); if (failed) return rd;
                    if ( backtracking==0 ) {
                      
                      				rd = new VariableRestrictionDescr(op, bvc.getText());
                      			
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1254:5: lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_expression_value3302);
                    lc=enum_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new QualifiedIdentifierRestrictionDescr(op, lc);
                      			
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1258:5: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_expression_value3322);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op, lc);
                      			
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1262:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_expression_value3336);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1272:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final String literal_constraint() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1276:4: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1276:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1276:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt54=5;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt54=1;
                }
                break;
            case INT:
                {
                alt54=2;
                }
                break;
            case FLOAT:
                {
                alt54=3;
                }
                break;
            case BOOL:
                {
                alt54=4;
                }
                break;
            case NULL:
                {
                alt54=5;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("1276:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )", 54, 0, input);

                throw nvae;
            }

            switch (alt54) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1276:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint3379); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1277:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint3390); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1278:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint3403); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1279:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint3414); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1280:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint3426); if (failed) return text;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1284:1: enum_constraint returns [String text] : id= ID ( '.' ident= identifier )+ ;
    public final String enum_constraint() throws RecognitionException {
        String text = null;

        Token id=null;
        Token ident = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1289:3: (id= ID ( '.' ident= identifier )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1289:3: id= ID ( '.' ident= identifier )+
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint3461); if (failed) return text;
            if ( backtracking==0 ) {
               text=id.getText(); 
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1289:32: ( '.' ident= identifier )+
            int cnt55=0;
            loop55:
            do {
                int alt55=2;
                int LA55_0 = input.LA(1);

                if ( (LA55_0==71) ) {
                    alt55=1;
                }


                switch (alt55) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1289:34: '.' ident= identifier
            	    {
            	    match(input,71,FOLLOW_71_in_enum_constraint3467); if (failed) return text;
            	    pushFollow(FOLLOW_identifier_in_enum_constraint3471);
            	    ident=identifier();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	       text += "." + ident.getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt55 >= 1 ) break loop55;
            	    if (backtracking>0) {failed=true; return text;}
                        EarlyExitException eee =
                            new EarlyExitException(55, input);
                        throw eee;
                }
                cnt55++;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1293:1: predicate[ConditionalElementDescr base] : text= paren_chunk[d] ;
    public final void predicate(ConditionalElementDescr base) throws RecognitionException {
        String text = null;


        
        		PredicateDescr d = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1298:3: (text= paren_chunk[d] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1298:3: text= paren_chunk[d]
            {
            if ( backtracking==0 ) {
              
              			d = new PredicateDescr( );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_predicate3513);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1311:1: paren_chunk[BaseDescr descr] returns [String text] : loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN ;
    public final String paren_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1317:10: (loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1317:10: loc= LEFT_PAREN (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )* loc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk3562); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
              		
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1326:3: (~ ( LEFT_PAREN | RIGHT_PAREN ) | chunk= paren_chunk[null] )*
            loop56:
            do {
                int alt56=3;
                int LA56_0 = input.LA(1);

                if ( ((LA56_0>=ATTRIBUTES && LA56_0<=QUERY)||LA56_0==ID||(LA56_0>=END && LA56_0<=79)) ) {
                    alt56=1;
                }
                else if ( (LA56_0==LEFT_PAREN) ) {
                    alt56=2;
                }


                switch (alt56) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1327:4: ~ ( LEFT_PAREN | RIGHT_PAREN )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=QUERY)||input.LA(1)==ID||(input.LA(1)>=END && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk3578);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1332:4: chunk= paren_chunk[null]
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk3602);
            	    chunk=paren_chunk(null);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop56;
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
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk3639); if (failed) return text;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1354:1: curly_chunk[BaseDescr descr] returns [String text] : loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY ;
    public final String curly_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1360:3: (loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1360:3: loc= LEFT_CURLY (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )* loc= RIGHT_CURLY
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk3690); if (failed) return text;
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              		    
              		    buf.append( loc.getText() );
              		
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1368:3: (~ ( LEFT_CURLY | RIGHT_CURLY ) | chunk= curly_chunk[descr] )*
            loop57:
            do {
                int alt57=3;
                int LA57_0 = input.LA(1);

                if ( ((LA57_0>=ATTRIBUTES && LA57_0<=NULL)||(LA57_0>=LEFT_SQUARE && LA57_0<=79)) ) {
                    alt57=1;
                }
                else if ( (LA57_0==LEFT_CURLY) ) {
                    alt57=2;
                }


                switch (alt57) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1369:4: ~ ( LEFT_CURLY | RIGHT_CURLY )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=NULL)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk3706);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1374:4: chunk= curly_chunk[descr]
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk3730);
            	    chunk=curly_chunk(descr);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop57;
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
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk3767); if (failed) return text;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1396:1: square_chunk[BaseDescr descr] returns [String text] : loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE ;
    public final String square_chunk(BaseDescr descr) throws RecognitionException {
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1402:10: (loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1402:10: loc= LEFT_SQUARE (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )* loc= RIGHT_SQUARE
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk3830); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
               
              		
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1412:3: (~ ( LEFT_SQUARE | RIGHT_SQUARE ) | chunk= square_chunk[null] )*
            loop58:
            do {
                int alt58=3;
                int LA58_0 = input.LA(1);

                if ( ((LA58_0>=ATTRIBUTES && LA58_0<=RIGHT_CURLY)||(LA58_0>=AND && LA58_0<=79)) ) {
                    alt58=1;
                }
                else if ( (LA58_0==LEFT_SQUARE) ) {
                    alt58=2;
                }


                switch (alt58) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1413:4: ~ ( LEFT_SQUARE | RIGHT_SQUARE )
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=RIGHT_CURLY)||(input.LA(1)>=AND && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk3846);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1418:4: chunk= square_chunk[null]
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk3870);
            	    chunk=square_chunk(null);
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( chunk );
            	      			  
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
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk3907); if (failed) return text;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1440:1: retval_constraint returns [String text] : c= paren_chunk[null] ;
    public final String retval_constraint() throws RecognitionException {
        String text = null;

        String c = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1445:3: (c= paren_chunk[null] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1445:3: c= paren_chunk[null]
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint3952);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1448:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ( OR | '||' ) right= lhs_and )* ;
    public final BaseDescr lhs_or() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		OrDescr or = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1454:3: (left= lhs_and ( ( OR | '||' ) right= lhs_and )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1454:3: left= lhs_and ( ( OR | '||' ) right= lhs_and )*
            {
            pushFollow(FOLLOW_lhs_and_in_lhs_or3980);
            left=lhs_and();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              d = left; 
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1455:3: ( ( OR | '||' ) right= lhs_and )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);

                if ( ((LA59_0>=OR && LA59_0<=DOUBLE_PIPE)) ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1455:5: ( OR | '||' ) right= lhs_and
            	    {
            	    if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or3988);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
            	      			
            	    }
            	    pushFollow(FOLLOW_lhs_and_in_lhs_or4004);
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
            	    break loop59;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1472:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* ;
    public final BaseDescr lhs_and() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		AndDescr and = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1478:3: (left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1478:3: left= lhs_unary ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
            {
            pushFollow(FOLLOW_lhs_unary_in_lhs_and4040);
            left=lhs_unary();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = left; 
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1479:3: ( ( AND | DOUBLE_AMPER ) right= lhs_unary )*
            loop60:
            do {
                int alt60=2;
                int LA60_0 = input.LA(1);

                if ( (LA60_0==DOUBLE_AMPER||LA60_0==AND) ) {
                    alt60=1;
                }


                switch (alt60) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1479:5: ( AND | DOUBLE_AMPER ) right= lhs_unary
            	    {
            	    if ( input.LA(1)==DOUBLE_AMPER||input.LA(1)==AND ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and4048);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      				location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_AND_OR );
            	      			
            	    }
            	    pushFollow(FOLLOW_lhs_unary_in_lhs_and4064);
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
        return d;
    }
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1496:1: lhs_unary returns [BaseDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon ;
    public final BaseDescr lhs_unary() throws RecognitionException {
        BaseDescr d = null;

        BaseDescr u = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;

        FromDescr fm = null;


        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1500:4: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1500:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' ) opt_semicolon
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1500:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' )
            int alt63=6;
            switch ( input.LA(1) ) {
            case EXISTS:
                {
                alt63=1;
                }
                break;
            case NOT:
                {
                alt63=2;
                }
                break;
            case EVAL:
                {
                alt63=3;
                }
                break;
            case ID:
                {
                alt63=4;
                }
                break;
            case FORALL:
                {
                alt63=5;
                }
                break;
            case LEFT_PAREN:
                {
                alt63=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1500:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )? | u= lhs_forall | '(' u= lhs_or ')' )", 63, 0, input);

                throw nvae;
            }

            switch (alt63) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1500:6: u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary4101);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1501:5: u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary4109);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1502:5: u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary4117);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1503:5: u= lhs_pattern ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )?
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_unary4125);
                    u=lhs_pattern();
                    _fsp--;
                    if (failed) return d;
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1503:19: ( FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) ) )?
                    int alt62=2;
                    int LA62_0 = input.LA(1);

                    if ( (LA62_0==FROM) ) {
                        alt62=1;
                    }
                    switch (alt62) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1504:13: FROM ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )
                            {
                            match(input,FROM,FOLLOW_FROM_in_lhs_unary4141); if (failed) return d;
                            if ( backtracking==0 ) {
                              
                              				location.setType(Location.LOCATION_LHS_FROM);
                              				location.setProperty(Location.LOCATION_FROM_CONTENT, "");
                              		          
                            }
                            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1509:13: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )
                            int alt61=3;
                            int LA61_0 = input.LA(1);

                            if ( (LA61_0==ACCUMULATE) ) {
                                int LA61_1 = input.LA(2);

                                if ( (synpred108()) ) {
                                    alt61=1;
                                }
                                else if ( (synpred111()) ) {
                                    alt61=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1509:13: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 61, 1, input);

                                    throw nvae;
                                }
                            }
                            else if ( (LA61_0==COLLECT) ) {
                                int LA61_2 = input.LA(2);

                                if ( (synpred109()) ) {
                                    alt61=2;
                                }
                                else if ( (synpred111()) ) {
                                    alt61=3;
                                }
                                else {
                                    if (backtracking>0) {failed=true; return d;}
                                    NoViableAltException nvae =
                                        new NoViableAltException("1509:13: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 61, 2, input);

                                    throw nvae;
                                }
                            }
                            else if ( (LA61_0==ID) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==PACKAGE) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==FUNCTION) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==GLOBAL) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==IMPORT) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==RULE) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==QUERY) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==TEMPLATE) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==ATTRIBUTES) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==ENABLED) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==SALIENCE) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==DURATION) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==FROM) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==INIT) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==ACTION) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==RESULT) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==OR) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==AND) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==CONTAINS) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==EXCLUDES) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==MEMBEROF) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==MATCHES) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==NULL) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==EXISTS) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==NOT) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==EVAL) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==FORALL) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==WHEN) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==THEN) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==END) && (synpred111())) {
                                alt61=3;
                            }
                            else if ( (LA61_0==IN) && (synpred111())) {
                                alt61=3;
                            }
                            else {
                                if (backtracking>0) {failed=true; return d;}
                                NoViableAltException nvae =
                                    new NoViableAltException("1509:13: ( ( ACCUMULATE )=> (ac= accumulate_statement ) | ( COLLECT )=> (cs= collect_statement ) | (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement ) )", 61, 0, input);

                                throw nvae;
                            }
                            switch (alt61) {
                                case 1 :
                                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1510:14: ( ACCUMULATE )=> (ac= accumulate_statement )
                                    {
                                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1510:32: (ac= accumulate_statement )
                                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1510:33: ac= accumulate_statement
                                    {
                                    pushFollow(FOLLOW_accumulate_statement_in_lhs_unary4196);
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
                                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1511:14: ( COLLECT )=> (cs= collect_statement )
                                    {
                                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1511:29: (cs= collect_statement )
                                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1511:30: cs= collect_statement
                                    {
                                    pushFollow(FOLLOW_collect_statement_in_lhs_unary4225);
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
                                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1512:14: (~ ( ACCUMULATE | COLLECT ) )=> (fm= from_statement )
                                    {
                                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1512:43: (fm= from_statement )
                                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1512:44: fm= from_statement
                                    {
                                    pushFollow(FOLLOW_from_statement_in_lhs_unary4260);
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
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1515:5: u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary4299);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1516:5: '(' u= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary4307); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_unary4311);
                    u=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary4313); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               d = u; 
            }
            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary4323);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1521:1: lhs_exist returns [BaseDescr d] : loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) ;
    public final BaseDescr lhs_exist() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr pattern = null;


        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1525:4: (loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1525:4: loc= EXISTS ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            {
            loc=(Token)input.LT(1);
            match(input,EXISTS,FOLLOW_EXISTS_in_lhs_exist4347); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new ExistsDescr( ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_EXISTS );
              		
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1532:10: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            int alt64=2;
            int LA64_0 = input.LA(1);

            if ( (LA64_0==LEFT_PAREN) ) {
                alt64=1;
            }
            else if ( (LA64_0==ID) ) {
                alt64=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1532:10: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )", 64, 0, input);

                throw nvae;
            }
            switch (alt64) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1532:12: ( '(' pattern= lhs_or end= ')' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1532:12: ( '(' pattern= lhs_or end= ')' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1532:14: '(' pattern= lhs_or end= ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist4367); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist4371);
                    pattern=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( pattern != null ) ((ExistsDescr)d).addDescr( pattern ); 
                    }
                    end=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist4403); if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1537:12: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_exist4453);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1547:1: lhs_not returns [NotDescr d] : loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) ;
    public final NotDescr lhs_not() throws RecognitionException {
        NotDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr pattern = null;


        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1551:4: (loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1551:4: loc= NOT ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            {
            loc=(Token)input.LT(1);
            match(input,NOT,FOLLOW_NOT_in_lhs_not4507); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new NotDescr( ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			location.setType( Location.LOCATION_LHS_BEGIN_OF_CONDITION_NOT );
              		
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1558:3: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )
            int alt65=2;
            int LA65_0 = input.LA(1);

            if ( (LA65_0==LEFT_PAREN) ) {
                alt65=1;
            }
            else if ( (LA65_0==ID) ) {
                alt65=2;
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1558:3: ( ( '(' pattern= lhs_or end= ')' ) | pattern= lhs_pattern )", 65, 0, input);

                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1558:5: ( '(' pattern= lhs_or end= ')' )
                    {
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1558:5: ( '(' pattern= lhs_or end= ')' )
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1558:7: '(' pattern= lhs_or end= ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not4520); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not4524);
                    pattern=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( pattern != null ) d.addDescr( pattern ); 
                    }
                    end=(Token)input.LT(1);
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not4557); if (failed) return d;
                    if ( backtracking==0 ) {
                       if ( end != null ) d.setEndCharacter( ((CommonToken)end).getStopIndex() ); 
                    }

                    }


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1564:3: pattern= lhs_pattern
                    {
                    pushFollow(FOLLOW_lhs_pattern_in_lhs_not4594);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1574:1: lhs_eval returns [BaseDescr d] : loc= EVAL c= paren_chunk[d] ;
    public final BaseDescr lhs_eval() throws RecognitionException {
        BaseDescr d = null;

        Token loc=null;
        String c = null;


        
        		d = new EvalDescr( );
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1579:3: (loc= EVAL c= paren_chunk[d] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1579:3: loc= EVAL c= paren_chunk[d]
            {
            loc=(Token)input.LT(1);
            match(input,EVAL,FOLLOW_EVAL_in_lhs_eval4642); if (failed) return d;
            if ( backtracking==0 ) {
              
              			location.setType( Location.LOCATION_LHS_INSIDE_EVAL );
              		
            }
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval4653);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1596:1: lhs_forall returns [ForallDescr d] : loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')' ;
    public final ForallDescr lhs_forall() throws RecognitionException {
        ForallDescr d = null;

        Token loc=null;
        Token end=null;
        BaseDescr base = null;

        BaseDescr pattern = null;


        
        		d = factory.createForall();
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1600:4: (loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')' )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1600:4: loc= FORALL '(' base= lhs_pattern ( ( ',' )? pattern= lhs_pattern )+ end= ')'
            {
            loc=(Token)input.LT(1);
            match(input,FORALL,FOLLOW_FORALL_in_lhs_forall4682); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall4684); if (failed) return d;
            pushFollow(FOLLOW_lhs_pattern_in_lhs_forall4688);
            base=lhs_pattern();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              
              			if ( loc != null ) d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		        // adding the base pattern
              		        d.addDescr( base );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1607:3: ( ( ',' )? pattern= lhs_pattern )+
            int cnt67=0;
            loop67:
            do {
                int alt67=2;
                int LA67_0 = input.LA(1);

                if ( (LA67_0==ID||LA67_0==COMMA) ) {
                    alt67=1;
                }


                switch (alt67) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1607:5: ( ',' )? pattern= lhs_pattern
            	    {
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1607:5: ( ',' )?
            	    int alt66=2;
            	    int LA66_0 = input.LA(1);

            	    if ( (LA66_0==COMMA) ) {
            	        alt66=1;
            	    }
            	    switch (alt66) {
            	        case 1 :
            	            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1607:6: ','
            	            {
            	            match(input,COMMA,FOLLOW_COMMA_in_lhs_forall4702); if (failed) return d;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_lhs_pattern_in_lhs_forall4708);
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
            	    if ( cnt67 >= 1 ) break loop67;
            	    if (backtracking>0) {failed=true; return d;}
                        EarlyExitException eee =
                            new EarlyExitException(67, input);
                        throw eee;
                }
                cnt67++;
            } while (true);

            end=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall4723); if (failed) return d;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1619:1: dotted_name[BaseDescr descr] returns [String name] : id= ID ( '.' ident= identifier )* ( '[' loc= ']' )* ;
    public final String dotted_name(BaseDescr descr) throws RecognitionException {
        String name = null;

        Token id=null;
        Token loc=null;
        Token ident = null;


        
        		name = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1624:3: (id= ID ( '.' ident= identifier )* ( '[' loc= ']' )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1624:3: id= ID ( '.' ident= identifier )* ( '[' loc= ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name4754); if (failed) return name;
            if ( backtracking==0 ) {
               
              		    name=id.getText(); 
              		    if( descr != null ) {
              			descr.setStartCharacter( ((CommonToken)id).getStartIndex() );
              			descr.setEndCharacter( ((CommonToken)id).getStopIndex() );
              		    }
              		
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1632:3: ( '.' ident= identifier )*
            loop68:
            do {
                int alt68=2;
                int LA68_0 = input.LA(1);

                if ( (LA68_0==71) ) {
                    alt68=1;
                }


                switch (alt68) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1632:5: '.' ident= identifier
            	    {
            	    match(input,71,FOLLOW_71_in_dotted_name4766); if (failed) return name;
            	    pushFollow(FOLLOW_identifier_in_dotted_name4770);
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
            	    break loop68;
                }
            } while (true);

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1640:3: ( '[' loc= ']' )*
            loop69:
            do {
                int alt69=2;
                int LA69_0 = input.LA(1);

                if ( (LA69_0==LEFT_SQUARE) ) {
                    alt69=1;
                }


                switch (alt69) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1640:5: '[' loc= ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name4792); if (failed) return name;
            	    loc=(Token)input.LT(1);
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name4796); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       
            	      		        name = name + "[]";
            	          		        if( descr != null ) {
            	      			    descr.setEndCharacter( ((CommonToken)loc).getStopIndex() );
            	      		        }
            	      		    
            	    }

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
        }
        return name;
    }
    // $ANTLR end dotted_name


    // $ANTLR start argument
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1650:1: argument returns [String name] : id= identifier ( '[' ']' )* ;
    public final String argument() throws RecognitionException {
        String name = null;

        Token id = null;


        
        		name = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1655:3: (id= identifier ( '[' ']' )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1655:3: id= identifier ( '[' ']' )*
            {
            pushFollow(FOLLOW_identifier_in_argument4835);
            id=identifier();
            _fsp--;
            if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1655:40: ( '[' ']' )*
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);

                if ( (LA70_0==LEFT_SQUARE) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1655:42: '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument4841); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument4843); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "[]";
            	    }

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
        }
        return name;
    }
    // $ANTLR end argument


    // $ANTLR start rhs_chunk
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1658:1: rhs_chunk[RuleDescr rule] : start= THEN (~ END )* loc= END ;
    public final void rhs_chunk(RuleDescr rule) throws RecognitionException {
        Token start=null;
        Token loc=null;

        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1664:10: (start= THEN (~ END )* loc= END )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1664:10: start= THEN (~ END )* loc= END
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            start=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk4887); if (failed) return ;
            if ( backtracking==0 ) {
              
              			location.setType( Location.LOCATION_RHS );
              		
            }
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1673:3: (~ END )*
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);

                if ( ((LA71_0>=ATTRIBUTES && LA71_0<=RIGHT_PAREN)||(LA71_0>=TEMPLATE && LA71_0<=79)) ) {
                    alt71=1;
                }


                switch (alt71) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1674:6: ~ END
            	    {
            	    if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=RIGHT_PAREN)||(input.LA(1)>=TEMPLATE && input.LA(1)<=79) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk4903);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop71;
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
            match(input,END,FOLLOW_END_in_rhs_chunk4940); if (failed) return ;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1705:1: name returns [String s] : (tok= ID | str= STRING ) ;
    public final String name() throws RecognitionException {
        String s = null;

        Token tok=null;
        Token str=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1707:2: ( (tok= ID | str= STRING ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1707:2: (tok= ID | str= STRING )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1707:2: (tok= ID | str= STRING )
            int alt72=2;
            int LA72_0 = input.LA(1);

            if ( (LA72_0==ID) ) {
                alt72=1;
            }
            else if ( (LA72_0==STRING) ) {
                alt72=2;
            }
            else {
                if (backtracking>0) {failed=true; return s;}
                NoViableAltException nvae =
                    new NoViableAltException("1707:2: (tok= ID | str= STRING )", 72, 0, input);

                throw nvae;
            }
            switch (alt72) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1708:6: tok= ID
                    {
                    tok=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_name4984); if (failed) return s;
                    if ( backtracking==0 ) {
                      
                      	        s = tok.getText();
                      	    
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1713:6: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_name5003); if (failed) return s;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1720:1: identifier returns [Token tok] : (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MEMBEROF | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END | t= IN ) ;
    public final Token identifier() throws RecognitionException {
        Token tok = null;

        Token t=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1722:2: ( (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MEMBEROF | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END | t= IN ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1722:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MEMBEROF | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END | t= IN )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1722:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MEMBEROF | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END | t= IN )
            int alt73=33;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt73=1;
                }
                break;
            case PACKAGE:
                {
                alt73=2;
                }
                break;
            case FUNCTION:
                {
                alt73=3;
                }
                break;
            case GLOBAL:
                {
                alt73=4;
                }
                break;
            case IMPORT:
                {
                alt73=5;
                }
                break;
            case RULE:
                {
                alt73=6;
                }
                break;
            case QUERY:
                {
                alt73=7;
                }
                break;
            case TEMPLATE:
                {
                alt73=8;
                }
                break;
            case ATTRIBUTES:
                {
                alt73=9;
                }
                break;
            case ENABLED:
                {
                alt73=10;
                }
                break;
            case SALIENCE:
                {
                alt73=11;
                }
                break;
            case DURATION:
                {
                alt73=12;
                }
                break;
            case FROM:
                {
                alt73=13;
                }
                break;
            case ACCUMULATE:
                {
                alt73=14;
                }
                break;
            case INIT:
                {
                alt73=15;
                }
                break;
            case ACTION:
                {
                alt73=16;
                }
                break;
            case RESULT:
                {
                alt73=17;
                }
                break;
            case COLLECT:
                {
                alt73=18;
                }
                break;
            case OR:
                {
                alt73=19;
                }
                break;
            case AND:
                {
                alt73=20;
                }
                break;
            case CONTAINS:
                {
                alt73=21;
                }
                break;
            case EXCLUDES:
                {
                alt73=22;
                }
                break;
            case MEMBEROF:
                {
                alt73=23;
                }
                break;
            case MATCHES:
                {
                alt73=24;
                }
                break;
            case NULL:
                {
                alt73=25;
                }
                break;
            case EXISTS:
                {
                alt73=26;
                }
                break;
            case NOT:
                {
                alt73=27;
                }
                break;
            case EVAL:
                {
                alt73=28;
                }
                break;
            case FORALL:
                {
                alt73=29;
                }
                break;
            case WHEN:
                {
                alt73=30;
                }
                break;
            case THEN:
                {
                alt73=31;
                }
                break;
            case END:
                {
                alt73=32;
                }
                break;
            case IN:
                {
                alt73=33;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return tok;}
                NoViableAltException nvae =
                    new NoViableAltException("1722:2: (t= ID | t= PACKAGE | t= FUNCTION | t= GLOBAL | t= IMPORT | t= RULE | t= QUERY | t= TEMPLATE | t= ATTRIBUTES | t= ENABLED | t= SALIENCE | t= DURATION | t= FROM | t= ACCUMULATE | t= INIT | t= ACTION | t= RESULT | t= COLLECT | t= OR | t= AND | t= CONTAINS | t= EXCLUDES | t= MEMBEROF | t= MATCHES | t= NULL | t= EXISTS | t= NOT | t= EVAL | t= FORALL | t= WHEN | t= THEN | t= END | t= IN )", 73, 0, input);

                throw nvae;
            }

            switch (alt73) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1722:10: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_identifier5041); if (failed) return tok;

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1723:4: t= PACKAGE
                    {
                    t=(Token)input.LT(1);
                    match(input,PACKAGE,FOLLOW_PACKAGE_in_identifier5054); if (failed) return tok;

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1724:4: t= FUNCTION
                    {
                    t=(Token)input.LT(1);
                    match(input,FUNCTION,FOLLOW_FUNCTION_in_identifier5061); if (failed) return tok;

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1725:4: t= GLOBAL
                    {
                    t=(Token)input.LT(1);
                    match(input,GLOBAL,FOLLOW_GLOBAL_in_identifier5068); if (failed) return tok;

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1726:4: t= IMPORT
                    {
                    t=(Token)input.LT(1);
                    match(input,IMPORT,FOLLOW_IMPORT_in_identifier5075); if (failed) return tok;

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1727:4: t= RULE
                    {
                    t=(Token)input.LT(1);
                    match(input,RULE,FOLLOW_RULE_in_identifier5084); if (failed) return tok;

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1728:4: t= QUERY
                    {
                    t=(Token)input.LT(1);
                    match(input,QUERY,FOLLOW_QUERY_in_identifier5091); if (failed) return tok;

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1729:17: t= TEMPLATE
                    {
                    t=(Token)input.LT(1);
                    match(input,TEMPLATE,FOLLOW_TEMPLATE_in_identifier5112); if (failed) return tok;

                    }
                    break;
                case 9 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1730:17: t= ATTRIBUTES
                    {
                    t=(Token)input.LT(1);
                    match(input,ATTRIBUTES,FOLLOW_ATTRIBUTES_in_identifier5140); if (failed) return tok;

                    }
                    break;
                case 10 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1731:17: t= ENABLED
                    {
                    t=(Token)input.LT(1);
                    match(input,ENABLED,FOLLOW_ENABLED_in_identifier5166); if (failed) return tok;

                    }
                    break;
                case 11 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1732:17: t= SALIENCE
                    {
                    t=(Token)input.LT(1);
                    match(input,SALIENCE,FOLLOW_SALIENCE_in_identifier5195); if (failed) return tok;

                    }
                    break;
                case 12 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1733:17: t= DURATION
                    {
                    t=(Token)input.LT(1);
                    match(input,DURATION,FOLLOW_DURATION_in_identifier5217); if (failed) return tok;

                    }
                    break;
                case 13 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1734:17: t= FROM
                    {
                    t=(Token)input.LT(1);
                    match(input,FROM,FOLLOW_FROM_in_identifier5239); if (failed) return tok;

                    }
                    break;
                case 14 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1735:17: t= ACCUMULATE
                    {
                    t=(Token)input.LT(1);
                    match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_identifier5268); if (failed) return tok;

                    }
                    break;
                case 15 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1736:17: t= INIT
                    {
                    t=(Token)input.LT(1);
                    match(input,INIT,FOLLOW_INIT_in_identifier5290); if (failed) return tok;

                    }
                    break;
                case 16 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1737:17: t= ACTION
                    {
                    t=(Token)input.LT(1);
                    match(input,ACTION,FOLLOW_ACTION_in_identifier5319); if (failed) return tok;

                    }
                    break;
                case 17 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1738:17: t= RESULT
                    {
                    t=(Token)input.LT(1);
                    match(input,RESULT,FOLLOW_RESULT_in_identifier5348); if (failed) return tok;

                    }
                    break;
                case 18 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1739:17: t= COLLECT
                    {
                    t=(Token)input.LT(1);
                    match(input,COLLECT,FOLLOW_COLLECT_in_identifier5377); if (failed) return tok;

                    }
                    break;
                case 19 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1740:17: t= OR
                    {
                    t=(Token)input.LT(1);
                    match(input,OR,FOLLOW_OR_in_identifier5406); if (failed) return tok;

                    }
                    break;
                case 20 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1741:17: t= AND
                    {
                    t=(Token)input.LT(1);
                    match(input,AND,FOLLOW_AND_in_identifier5435); if (failed) return tok;

                    }
                    break;
                case 21 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1742:17: t= CONTAINS
                    {
                    t=(Token)input.LT(1);
                    match(input,CONTAINS,FOLLOW_CONTAINS_in_identifier5464); if (failed) return tok;

                    }
                    break;
                case 22 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1743:17: t= EXCLUDES
                    {
                    t=(Token)input.LT(1);
                    match(input,EXCLUDES,FOLLOW_EXCLUDES_in_identifier5486); if (failed) return tok;

                    }
                    break;
                case 23 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1744:17: t= MEMBEROF
                    {
                    t=(Token)input.LT(1);
                    match(input,MEMBEROF,FOLLOW_MEMBEROF_in_identifier5508); if (failed) return tok;

                    }
                    break;
                case 24 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1745:17: t= MATCHES
                    {
                    t=(Token)input.LT(1);
                    match(input,MATCHES,FOLLOW_MATCHES_in_identifier5528); if (failed) return tok;

                    }
                    break;
                case 25 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1746:17: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_identifier5557); if (failed) return tok;

                    }
                    break;
                case 26 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1747:17: t= EXISTS
                    {
                    t=(Token)input.LT(1);
                    match(input,EXISTS,FOLLOW_EXISTS_in_identifier5586); if (failed) return tok;

                    }
                    break;
                case 27 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1748:17: t= NOT
                    {
                    t=(Token)input.LT(1);
                    match(input,NOT,FOLLOW_NOT_in_identifier5615); if (failed) return tok;

                    }
                    break;
                case 28 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1749:17: t= EVAL
                    {
                    t=(Token)input.LT(1);
                    match(input,EVAL,FOLLOW_EVAL_in_identifier5644); if (failed) return tok;

                    }
                    break;
                case 29 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1750:17: t= FORALL
                    {
                    t=(Token)input.LT(1);
                    match(input,FORALL,FOLLOW_FORALL_in_identifier5673); if (failed) return tok;

                    }
                    break;
                case 30 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1751:17: t= WHEN
                    {
                    t=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_identifier5711); if (failed) return tok;

                    }
                    break;
                case 31 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1752:17: t= THEN
                    {
                    t=(Token)input.LT(1);
                    match(input,THEN,FOLLOW_THEN_in_identifier5743); if (failed) return tok;

                    }
                    break;
                case 32 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1753:17: t= END
                    {
                    t=(Token)input.LT(1);
                    match(input,END,FOLLOW_END_in_identifier5772); if (failed) return tok;

                    }
                    break;
                case 33 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1754:11: t= IN
                    {
                    t=(Token)input.LT(1);
                    match(input,IN,FOLLOW_IN_in_identifier5791); if (failed) return tok;

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
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:208:4: ( function_import_statement )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:208:4: function_import_statement
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
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:209:4: ( import_statement )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:209:4: import_statement
        {
        pushFollow(FOLLOW_import_statement_in_synpred8162);
        import_statement();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred8

    // $ANTLR start synpred46
    public final void synpred46_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:780:4: ( paren_chunk[from] )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:780:4: paren_chunk[from]
        {
        pushFollow(FOLLOW_paren_chunk_in_synpred462003);
        paren_chunk(from);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred46

    // $ANTLR start synpred48
    public final void synpred48_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:816:6: ( LEFT_SQUARE )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:816:8: LEFT_SQUARE
        {
        match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_synpred482073); if (failed) return ;

        }
    }
    // $ANTLR end synpred48

    // $ANTLR start synpred49
    public final void synpred49_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:821:6: ( LEFT_PAREN )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:821:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred492107); if (failed) return ;

        }
    }
    // $ANTLR end synpred49

    // $ANTLR start synpred56
    public final void synpred56_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:940:6: ( ( OR | DOUBLE_PIPE ) fact )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:940:6: ( OR | DOUBLE_PIPE ) fact
        {
        if ( (input.LA(1)>=OR && input.LA(1)<=DOUBLE_PIPE) ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred562430);    throw mse;
        }

        pushFollow(FOLLOW_fact_in_synpred562448);
        fact();
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred56

    // $ANTLR start synpred61
    public final void synpred61_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1053:5: ( field_constraint[base] )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1053:5: field_constraint[base]
        {
        pushFollow(FOLLOW_field_constraint_in_synpred612738);
        field_constraint(base);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred61

    // $ANTLR start synpred66
    public final void synpred66_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1117:5: ( DOUBLE_PIPE and_restr_connective[or] )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1117:5: DOUBLE_PIPE and_restr_connective[or]
        {
        match(input,DOUBLE_PIPE,FOLLOW_DOUBLE_PIPE_in_synpred662884); if (failed) return ;
        pushFollow(FOLLOW_and_restr_connective_in_synpred662896);
        and_restr_connective(or);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred66

    // $ANTLR start synpred67
    public final void synpred67_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1139:5: ( DOUBLE_AMPER constraint_expression[and] )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1139:5: DOUBLE_AMPER constraint_expression[and]
        {
        match(input,DOUBLE_AMPER,FOLLOW_DOUBLE_AMPER_in_synpred672940); if (failed) return ;
        pushFollow(FOLLOW_constraint_expression_in_synpred672950);
        constraint_expression(and);
        _fsp--;
        if (failed) return ;

        }
    }
    // $ANTLR end synpred67

    // $ANTLR start synpred108
    public final void synpred108_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1510:14: ( ACCUMULATE )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1510:16: ACCUMULATE
        {
        match(input,ACCUMULATE,FOLLOW_ACCUMULATE_in_synpred1084187); if (failed) return ;

        }
    }
    // $ANTLR end synpred108

    // $ANTLR start synpred109
    public final void synpred109_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1511:14: ( COLLECT )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1511:16: COLLECT
        {
        match(input,COLLECT,FOLLOW_COLLECT_in_synpred1094216); if (failed) return ;

        }
    }
    // $ANTLR end synpred109

    // $ANTLR start synpred111
    public final void synpred111_fragment() throws RecognitionException {   
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1512:14: (~ ( ACCUMULATE | COLLECT ) )
        // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1512:16: ~ ( ACCUMULATE | COLLECT )
        {
        if ( (input.LA(1)>=ATTRIBUTES && input.LA(1)<=LOCK_ON_ACTIVE)||(input.LA(1)>=COMMA && input.LA(1)<=RESULT)||(input.LA(1)>=OR && input.LA(1)<=79) ) {
            input.consume();
            errorRecovery=false;failed=false;
        }
        else {
            if (backtracking>0) {failed=true; return ;}
            MismatchedSetException mse =
                new MismatchedSetException(null,input);
            recoverFromMismatchedSet(input,mse,FOLLOW_set_in_synpred1114246);    throw mse;
        }


        }
    }
    // $ANTLR end synpred111

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
    public final boolean synpred109() {
        backtracking++;
        int start = input.mark();
        try {
            synpred109_fragment(); // can never throw exception
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
    public final boolean synpred66() {
        backtracking++;
        int start = input.mark();
        try {
            synpred66_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred48() {
        backtracking++;
        int start = input.mark();
        try {
            synpred48_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred56() {
        backtracking++;
        int start = input.mark();
        try {
            synpred56_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred111() {
        backtracking++;
        int start = input.mark();
        try {
            synpred111_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public final boolean synpred67() {
        backtracking++;
        int start = input.mark();
        try {
            synpred67_fragment(); // can never throw exception
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
    public final boolean synpred61() {
        backtracking++;
        int start = input.mark();
        try {
            synpred61_fragment(); // can never throw exception
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


    protected DFA11 dfa11 = new DFA11(this);
    protected DFA12 dfa12 = new DFA12(this);
    static final String DFA11_eotS =
        "\6\uffff";
    static final String DFA11_eofS =
        "\6\uffff";
    static final String DFA11_minS =
        "\2\4\1\uffff\1\65\1\uffff\1\4";
    static final String DFA11_maxS =
        "\1\72\1\107\1\uffff\1\65\1\uffff\1\72";
    static final String DFA11_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\1\uffff";
    static final String DFA11_specialS =
        "\6\uffff}>";
    static final String[] DFA11_transitionS = {
            "\6\2\1\uffff\1\1\1\uffff\4\2\3\uffff\1\2\1\uffff\1\2\6\uffff"+
            "\1\2\2\uffff\1\2\1\uffff\5\2\2\uffff\7\2\1\uffff\1\2\4\uffff"+
            "\5\2",
            "\6\4\1\uffff\1\4\1\2\4\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4"+
            "\2\uffff\1\4\1\2\5\4\2\uffff\7\4\1\uffff\1\4\2\uffff\1\3\1\uffff"+
            "\5\4\14\uffff\1\4",
            "",
            "\1\5",
            "",
            "\6\4\1\uffff\1\4\1\2\4\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4"+
            "\2\uffff\1\4\1\2\5\4\2\uffff\7\4\1\uffff\1\4\2\uffff\1\3\1\uffff"+
            "\5\4"
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
            "\6\2\1\uffff\1\1\1\uffff\4\2\3\uffff\1\2\1\uffff\1\2\6\uffff"+
            "\1\2\2\uffff\1\2\1\uffff\5\2\2\uffff\7\2\1\uffff\1\2\4\uffff"+
            "\5\2",
            "\6\4\1\uffff\1\4\1\2\4\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4"+
            "\2\uffff\1\4\1\2\5\4\2\uffff\7\4\1\uffff\1\4\2\uffff\1\3\1\uffff"+
            "\5\4\14\uffff\1\4",
            "",
            "\1\5",
            "",
            "\6\4\1\uffff\1\4\1\2\4\4\3\uffff\1\4\1\uffff\1\4\6\uffff\1\4"+
            "\2\uffff\1\4\1\2\5\4\2\uffff\7\4\1\uffff\1\4\2\uffff\1\3\1\uffff"+
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
    public static final BitSet FOLLOW_prolog_in_compilation_unit64 = new BitSet(new long[]{0x000000000000C3C0L});
    public static final BitSet FOLLOW_statement_in_compilation_unit71 = new BitSet(new long[]{0x000000000000C3C2L});
    public static final BitSet FOLLOW_package_statement_in_prolog96 = new BitSet(new long[]{0x00000002FF5A0012L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_prolog111 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_prolog113 = new BitSet(new long[]{0x00000002FF5A0002L});
    public static final BitSet FOLLOW_COMMA_in_prolog122 = new BitSet(new long[]{0x00000000FF5A0000L});
    public static final BitSet FOLLOW_rule_attribute_in_prolog127 = new BitSet(new long[]{0x00000002FF5A0002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_package_statement238 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement242 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_import_statement277 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_import_name_in_import_statement300 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_function_import_statement329 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_FUNCTION_in_function_import_statement331 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement354 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_import_name385 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_71_in_import_name397 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_identifier_in_import_name401 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_72_in_import_name425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_global461 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_dotted_name_in_global472 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_identifier_in_global484 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_global486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function513 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_dotted_name_in_function518 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_identifier_in_function525 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function534 = new BitSet(new long[]{0x07C2FE7D2051FBF0L});
    public static final BitSet FOLLOW_dotted_name_in_function544 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_argument_in_function551 = new BitSet(new long[]{0x0000000200001000L});
    public static final BitSet FOLLOW_COMMA_in_function565 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_dotted_name_in_function570 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_argument_in_function577 = new BitSet(new long[]{0x0000000200001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function601 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_curly_chunk_in_function607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_query639 = new BitSet(new long[]{0x0000000000040800L});
    public static final BitSet FOLLOW_name_in_query643 = new BitSet(new long[]{0x03000A0000002C00L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_query656 = new BitSet(new long[]{0x0000000000001800L});
    public static final BitSet FOLLOW_ID_in_query680 = new BitSet(new long[]{0x0000000200001000L});
    public static final BitSet FOLLOW_COMMA_in_query693 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_ID_in_query697 = new BitSet(new long[]{0x0000000200001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_query734 = new BitSet(new long[]{0x03000A0000002C00L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query751 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_END_in_query768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_template798 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_identifier_in_template802 = new BitSet(new long[]{0x0000000000000800L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_template804 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_template_slot_in_template819 = new BitSet(new long[]{0x0000000000002800L});
    public static final BitSet FOLLOW_END_in_template836 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_template838 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot884 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_identifier_in_template_slot902 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule937 = new BitSet(new long[]{0x0000000000040800L});
    public static final BitSet FOLLOW_name_in_rule941 = new BitSet(new long[]{0x04000002FF5B0010L});
    public static final BitSet FOLLOW_rule_attributes_in_rule950 = new BitSet(new long[]{0x0400000000010000L});
    public static final BitSet FOLLOW_WHEN_in_rule959 = new BitSet(new long[]{0x07000A0000000C00L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_rule961 = new BitSet(new long[]{0x07000A0000000C00L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule979 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule1000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_rule_attributes1021 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_rule_attributes1023 = new BitSet(new long[]{0x00000002FF5A0002L});
    public static final BitSet FOLLOW_COMMA_in_rule_attributes1032 = new BitSet(new long[]{0x00000000FF5A0000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes1037 = new BitSet(new long[]{0x00000002FF5A0002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1088 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute1148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute1158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute1168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleflow_group_in_rule_attribute1178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lock_on_active_in_rule_attribute1188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dialect_in_rule_attribute1197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EFFECTIVE_in_date_effective1229 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_STRING_in_date_effective1233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DATE_EXPIRES_in_date_expires1266 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_STRING_in_date_expires1270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_enabled1305 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_BOOL_in_enabled1309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience1354 = new BitSet(new long[]{0x0000000000800400L});
    public static final BitSet FOLLOW_INT_in_salience1367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_salience1382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NO_LOOP_in_no_loop1450 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AUTO_FOCUS_in_auto_focus1531 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1535 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTIVATION_GROUP_in_activation_group1580 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_STRING_in_activation_group1584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULEFLOW_GROUP_in_ruleflow_group1616 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_STRING_in_ruleflow_group1620 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AGENDA_GROUP_in_agenda_group1652 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_duration1690 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_INT_in_duration1694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIALECT_in_dialect1726 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_STRING_in_dialect1730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LOCK_ON_ACTIVE_in_lock_on_active1807 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_BOOL_in_lock_on_active1811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1849 = new BitSet(new long[]{0x03000A0000000C02L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_pattern1914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_pattern1923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_from_source_in_from_statement1950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_identifier_in_from_source1992 = new BitSet(new long[]{0x0000000000000402L,0x0000000000000080L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source2003 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_expression_chain_in_from_source2017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_expression_chain2046 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_identifier_in_expression_chain2050 = new BitSet(new long[]{0x0010000000000402L,0x0000000000000080L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain2081 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain2115 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain2136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_accumulate_statement2177 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement2187 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_lhs_pattern_in_accumulate_statement2191 = new BitSet(new long[]{0x0000000600000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2193 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_INIT_in_accumulate_statement2203 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2214 = new BitSet(new long[]{0x0000000A00000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2217 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_ACTION_in_accumulate_statement2226 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2230 = new BitSet(new long[]{0x0000001200000000L});
    public static final BitSet FOLLOW_COMMA_in_accumulate_statement2233 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_RESULT_in_accumulate_statement2242 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement2246 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement2258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_collect_statement2301 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement2311 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_lhs_pattern_in_collect_statement2315 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement2319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding2353 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_fact_binding2355 = new BitSet(new long[]{0x0000000000000C00L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding2368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression2400 = new BitSet(new long[]{0x0000000000000C00L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2404 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression2407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression2418 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_set_in_fact_expression2430 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_fact_in_fact_expression2448 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_dotted_name_in_fact2509 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact2523 = new BitSet(new long[]{0x07C2FE7D2051FFF0L});
    public static final BitSet FOLLOW_constraints_in_fact2533 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact2546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints2566 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_COMMA_in_constraints2573 = new BitSet(new long[]{0x07C2FE7D2051EFF0L});
    public static final BitSet FOLLOW_constraint_in_constraints2582 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_or_constr_in_constraint2615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_constr_in_or_constr2638 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_constr2648 = new BitSet(new long[]{0x07C2FE7D2051EFF0L});
    public static final BitSet FOLLOW_and_constr_in_or_constr2658 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr2690 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_constr2700 = new BitSet(new long[]{0x07C2FE7D2051EFF0L});
    public static final BitSet FOLLOW_unary_constr_in_and_constr2710 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_field_constraint_in_unary_constr2738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_unary_constr2746 = new BitSet(new long[]{0x07C2FE7D2051EFF0L});
    public static final BitSet FOLLOW_or_constr_in_unary_constr2748 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_unary_constr2751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_unary_constr2757 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_predicate_in_unary_constr2759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_field_constraint2791 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_field_constraint2793 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_identifier_in_field_constraint2814 = new BitSet(new long[]{0x0000FC0000000402L,0x000000000000FE00L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constraint2828 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_field_constraint2843 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_predicate_in_field_constraint2845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective2874 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_or_restr_connective2884 = new BitSet(new long[]{0x0000FC0000000400L,0x000000000000FC00L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective2896 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective2930 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_and_restr_connective2940 = new BitSet(new long[]{0x0000FC0000000400L,0x000000000000FC00L});
    public static final BitSet FOLLOW_constraint_expression_in_and_restr_connective2950 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_compound_operator_in_constraint_expression2985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_operator_in_constraint_expression2992 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_constraint_expression2999 = new BitSet(new long[]{0x0000FC0000000400L,0x000000000000FC00L});
    public static final BitSet FOLLOW_or_restr_connective_in_constraint_expression3008 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_constraint_expression3014 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_simple_operator3045 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_75_in_simple_operator3053 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_76_in_simple_operator3061 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_77_in_simple_operator3069 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_78_in_simple_operator3077 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_79_in_simple_operator3085 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator3093 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3101 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_CONTAINS_in_simple_operator3105 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_EXCLUDES_in_simple_operator3113 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator3121 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3129 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_MATCHES_in_simple_operator3133 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator3141 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_NOT_in_simple_operator3149 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_MEMBEROF_in_simple_operator3153 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_expression_value_in_simple_operator3167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_compound_operator3196 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_NOT_in_compound_operator3208 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_IN_in_compound_operator3210 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_compound_operator3225 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3229 = new BitSet(new long[]{0x0000000200001000L});
    public static final BitSet FOLLOW_COMMA_in_compound_operator3241 = new BitSet(new long[]{0x0003000000A40C00L});
    public static final BitSet FOLLOW_expression_value_in_compound_operator3245 = new BitSet(new long[]{0x0000000200001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_compound_operator3261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_expression_value3289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_expression_value3302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_expression_value3322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_expression_value3336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint3379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint3390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint3403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint3414 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint3426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint3461 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_enum_constraint3467 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_identifier_in_enum_constraint3471 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate3513 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk3562 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk3578 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk3602 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk3639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk3690 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk3706 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk3730 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk3767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk3830 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_square_chunk3846 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk3870 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk3907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint3952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3980 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or3988 = new BitSet(new long[]{0x03000A0000000C00L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or4004 = new BitSet(new long[]{0x000000C000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and4040 = new BitSet(new long[]{0x0040010000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and4048 = new BitSet(new long[]{0x03000A0000000C00L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and4064 = new BitSet(new long[]{0x0040010000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary4101 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary4109 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary4117 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_unary4125 = new BitSet(new long[]{0x0080000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_FROM_in_lhs_unary4141 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary4196 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary4225 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary4260 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary4299 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary4307 = new BitSet(new long[]{0x03000A0000000C00L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_unary4311 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary4313 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary4323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_lhs_exist4347 = new BitSet(new long[]{0x0000000000000C00L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist4367 = new BitSet(new long[]{0x03000A0000000C00L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist4371 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist4403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_exist4453 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_lhs_not4507 = new BitSet(new long[]{0x0000000000000C00L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not4520 = new BitSet(new long[]{0x03000A0000000C00L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not4524 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not4557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_not4594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_lhs_eval4642 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval4653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_lhs_forall4682 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall4684 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall4688 = new BitSet(new long[]{0x0000000200000800L});
    public static final BitSet FOLLOW_COMMA_in_lhs_forall4702 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_lhs_pattern_in_lhs_forall4708 = new BitSet(new long[]{0x0000000200001800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall4723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name4754 = new BitSet(new long[]{0x0010000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_dotted_name4766 = new BitSet(new long[]{0x07C2FE7D2051EBF0L});
    public static final BitSet FOLLOW_identifier_in_dotted_name4770 = new BitSet(new long[]{0x0010000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name4792 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name4796 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_identifier_in_argument4835 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument4841 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument4843 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk4887 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk4903 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000FFFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk4940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_name4984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_name5003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_identifier5041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PACKAGE_in_identifier5054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_identifier5061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GLOBAL_in_identifier5068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_in_identifier5075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_identifier5084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_QUERY_in_identifier5091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TEMPLATE_in_identifier5112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ATTRIBUTES_in_identifier5140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ENABLED_in_identifier5166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_identifier5195 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DURATION_in_identifier5217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FROM_in_identifier5239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_identifier5268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INIT_in_identifier5290 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACTION_in_identifier5319 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RESULT_in_identifier5348 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_identifier5377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_identifier5406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_identifier5435 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CONTAINS_in_identifier5464 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXCLUDES_in_identifier5486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEMBEROF_in_identifier5508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MATCHES_in_identifier5528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_identifier5557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EXISTS_in_identifier5586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_identifier5615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EVAL_in_identifier5644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FORALL_in_identifier5673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_identifier5711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_identifier5743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_END_in_identifier5772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IN_in_identifier5791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_synpred7156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_synpred8162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_synpred462003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_synpred482073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred492107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred562430 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_fact_in_synpred562448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_field_constraint_in_synpred612738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_PIPE_in_synpred662884 = new BitSet(new long[]{0x0000FC0000000400L,0x000000000000FC00L});
    public static final BitSet FOLLOW_and_restr_connective_in_synpred662896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_AMPER_in_synpred672940 = new BitSet(new long[]{0x0000FC0000000400L,0x000000000000FC00L});
    public static final BitSet FOLLOW_constraint_expression_in_synpred672950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ACCUMULATE_in_synpred1084187 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLLECT_in_synpred1094216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred1114246 = new BitSet(new long[]{0x0000000000000002L});

}