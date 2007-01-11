// $ANTLR 3.0b5 /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g 2007-01-11 21:47:58

	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.Map;	
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "RULE", "WHEN", "STRING", "BOOL", "INT", "FLOAT", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "END", "THEN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "'package'", "'import'", "'function'", "'.'", "'.*'", "'global'", "','", "'query'", "'template'", "':'", "'attributes'", "'date-effective'", "'date-expires'", "'enabled'", "'salience'", "'no-loop'", "'auto-focus'", "'activation-group'", "'agenda-group'", "'duration'", "'from'", "'accumulate'", "'init'", "'action'", "'result'", "'collect'", "'or'", "'||'", "'&'", "'|'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='", "'contains'", "'matches'", "'excludes'", "'null'", "'->'", "'and'", "'&&'", "'exists'", "'not'", "'eval'", "'use'"
    };
    public static final int LEFT_PAREN=11;
    public static final int RIGHT_CURLY=14;
    public static final int BOOL=8;
    public static final int END=17;
    public static final int HexDigit=22;
    public static final int WHEN=6;
    public static final int WS=20;
    public static final int MISC=28;
    public static final int STRING=7;
    public static final int FLOAT=10;
    public static final int THEN=18;
    public static final int RULE=5;
    public static final int UnicodeEscape=23;
    public static final int EscapeSequence=21;
    public static final int INT=9;
    public static final int EOF=-1;
    public static final int EOL=19;
    public static final int LEFT_SQUARE=15;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=25;
    public static final int OctalEscape=24;
    public static final int MULTI_LINE_COMMENT=27;
    public static final int RIGHT_PAREN=12;
    public static final int LEFT_CURLY=13;
    public static final int RIGHT_SQUARE=16;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=26;
    public static final int ID=4;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[144+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "/home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g"; }


    	private ExpanderResolver expanderResolver;
    	private Expander expander;
    	private boolean expanderDebug = false;
    	private PackageDescr packageDescr;
    	private List errors = new ArrayList();
    	private String source = "unknown";
    	private int lineOffset = 0;
    	private DescrFactory factory = new DescrFactory();
    	private boolean parserDebug = false;
    	
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

    	/**
    	 * This may be set to enable debuggin of DSLs/expanders.
    	 * If set to true, expander stuff will be sent to the Std out.
    	 */	
    	public void setExpanderDebug(boolean status) {
    		expanderDebug = status;
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
    	
    	public void setExpanderResolver(ExpanderResolver expanderResolver) {
    		this.expanderResolver = expanderResolver;
    	}
    	
    	public ExpanderResolver getExpanderResolver() {
    		return expanderResolver;
    	}
    	
    	/** Expand the LHS */
    	private String runWhenExpander(String text, int line) throws RecognitionException {
    		String expanded = text.trim();
    		if (expanded.startsWith(">")) {
    			expanded = expanded.substring(1);  //escape !!
    		} else {
    			try {
    				expanded = expander.expand( "when", text );			
    			} catch (Exception e) {
    				this.errors.add(new ExpanderException("Unable to expand: " + text + ". Due to " + e.getMessage(), line));
    				return "";
    			}
    		}
    		if (expanderDebug) {
    			System.out.println("Expanding LHS: " + text + " ----> " + expanded + " --> from line: " + line);
    		}
    		return expanded;	
    		
    	}
    	
        	/** This will apply a list of constraints to an LHS block */
        	private String applyConstraints(List constraints, String block) {
        		//apply the constraints as a comma seperated list inside the previous block
        		//the block will end in something like "foo()" and the constraint patterns will be put in the ()
        		if (constraints == null) {
        			return block;
        		}
        		StringBuffer list = new StringBuffer();    		
        		for (Iterator iter = constraints.iterator(); iter.hasNext();) {
    				String con = (String) iter.next();
    				list.append("\n\t\t");
    				list.append(con);
    				if (iter.hasNext()) {
    					list.append(",");					
    				}			
    			}
        		if (block.endsWith("()")) {
        			return block.substring(0, block.length() - 2) + "(" + list.toString() + ")";
        		} else {
        			return block + "(" + list.toString() + ")";
        		}
        	}  	

            /** Reparse the results of the expansion */
        	private void reparseLhs(String text, AndDescr descrs) throws RecognitionException {
        		CharStream charStream = new ANTLRStringStream( text );
        		DRLLexer lexer = new DRLLexer( charStream );
        		TokenStream tokenStream = new CommonTokenStream( lexer );
        		DRLParser parser = new DRLParser( tokenStream );
        		parser.setLineOffset( descrs.getLine() );
        		parser.normal_lhs_block(descrs);
                
                    if (parser.hasErrors()) {
        			this.errors.addAll(parser.getErrors());
        		}
    		if (expanderDebug) {
    			System.out.println("Reparsing LHS: " + text + " --> successful:" + !parser.hasErrors());
    		}    		
        		
        	}
    	
    	/** Expand a line on the RHS */
    	private String runThenExpander(String text, int startLine) {
    		//System.err.println( "expand THEN [" + text + "]" );
    		StringTokenizer lines = new StringTokenizer( text, "\n\r" );

    		StringBuffer expanded = new StringBuffer();
    		
    		String eol = System.getProperty( "line.separator" );
    				
    		while ( lines.hasMoreTokens() ) {
    			startLine++;
    			String line = lines.nextToken();
    			line = line.trim();
    			if ( line.length() > 0 ) {
    				if ( line.startsWith( ">" ) ) {
    					expanded.append( line.substring( 1 ) );
    					expanded.append( eol );
    				} else {
    					try {
    						expanded.append( expander.expand( "then", line ) );
    						expanded.append( eol );
    					} catch (Exception e) {
    						this.errors.add(new ExpanderException("Unable to expand: " + line + ". Due to " + e.getMessage(), startLine));			
    					}
    				}
    			}
    		}
    		
    		if (expanderDebug) {
    			System.out.println("Expanding RHS: " + text + " ----> " + expanded.toString() + " --> from line starting: " + startLine);
    		}		
    		
    		return expanded.toString();
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
                                                               mtne.foundNode+
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:282:1: opt_semicolon : ( ( ';' )=> ';' )? ;
    public void opt_semicolon() throws RecognitionException {   
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:283:4: ( ( ( ';' )=> ';' )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:283:4: ( ( ';' )=> ';' )?
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:283:4: ( ( ';' )=> ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);
            if ( (LA1_0==29) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ( ';' )=> ';'
                    {
                    match(input,29,FOLLOW_29_in_opt_semicolon46); if (failed) return ;

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:286:1: compilation_unit : prolog ( ( statement )=> statement )+ ;
    public void compilation_unit() throws RecognitionException {   
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:287:4: ( prolog ( ( statement )=> statement )+ )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:287:4: prolog ( ( statement )=> statement )+
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit58);
            prolog();
            _fsp--;
            if (failed) return ;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:288:3: ( ( statement )=> statement )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( (LA2_0==RULE||(LA2_0>=31 && LA2_0<=32)||LA2_0==35||(LA2_0>=37 && LA2_0<=38)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:288:5: ( statement )=> statement
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:291:1: prolog : ( ( package_statement )=>name= package_statement )? ;
    public void prolog() throws RecognitionException {   
        String name = null;



        		String packageName = "";
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:295:4: ( ( ( package_statement )=>name= package_statement )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:295:4: ( ( package_statement )=>name= package_statement )?
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:295:4: ( ( package_statement )=>name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0==30) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:295:6: ( package_statement )=>name= package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_prolog90);
                    name=package_statement();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       packageName = name; 
                    }

                    }
                    break;

            }

            if ( backtracking==0 ) {
               
              			this.packageDescr = new PackageDescr( name ); 
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:301:1: statement : ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) ;
    public void statement() throws RecognitionException {   
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:303:2: ( ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:303:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:303:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
            int alt4=7;
            switch ( input.LA(1) ) {
            case 31:
                int LA4_1 = input.LA(2);
                if ( (LA4_1==32) ) {
                    alt4=2;
                }
                else if ( (LA4_1==ID) ) {
                    alt4=1;
                }
                else {
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("303:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )", 4, 1, input);

                    throw nvae;
                }
                break;
            case 35:
                alt4=3;
                break;
            case 32:
                alt4=4;
                break;
            case 38:
                alt4=5;
                break;
            case RULE:
                alt4=6;
                break;
            case 37:
                alt4=7;
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("303:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:303:4: ( import_statement )=> import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement114);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:304:10: ( function_import_statement )=> function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement126);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:305:4: ( global )=> global
                    {
                    pushFollow(FOLLOW_global_in_statement132);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:306:4: ( function )=> function
                    {
                    pushFollow(FOLLOW_function_in_statement138);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:307:10: ( template )=>t= template
                    {
                    pushFollow(FOLLOW_template_in_statement152);
                    t=template();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      this.packageDescr.addFactTemplate( t ); 
                    }

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:308:4: ( rule )=>r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement161);
                    r=rule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      this.packageDescr.addRule( r ); 
                    }

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:309:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement171);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:313:1: package_statement returns [String packageName] : 'package' name= dotted_name opt_semicolon ;
    public String package_statement() throws RecognitionException {   
        String packageName = null;

        String name = null;



        		packageName = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:318:3: ( 'package' name= dotted_name opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:318:3: 'package' name= dotted_name opt_semicolon
            {
            match(input,30,FOLLOW_30_in_package_statement200); if (failed) return packageName;
            pushFollow(FOLLOW_dotted_name_in_package_statement204);
            name=dotted_name();
            _fsp--;
            if (failed) return packageName;
            pushFollow(FOLLOW_opt_semicolon_in_package_statement206);
            opt_semicolon();
            _fsp--;
            if (failed) return packageName;
            if ( backtracking==0 ) {

              			packageName = name;
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:325:1: import_statement : 'import' name= import_name opt_semicolon ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:326:4: ( 'import' name= import_name opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:326:4: 'import' name= import_name opt_semicolon
            {
            match(input,31,FOLLOW_31_in_import_statement223); if (failed) return ;
            pushFollow(FOLLOW_import_name_in_import_statement227);
            name=import_name();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_import_statement229);
            opt_semicolon();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			if (packageDescr != null) 
              				packageDescr.addImport( name );
              		
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
    // $ANTLR end import_statement


    // $ANTLR start function_import_statement
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:333:1: function_import_statement : 'import' 'function' name= import_name opt_semicolon ;
    public void function_import_statement() throws RecognitionException {   
        String name = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:334:4: ( 'import' 'function' name= import_name opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:334:4: 'import' 'function' name= import_name opt_semicolon
            {
            match(input,31,FOLLOW_31_in_function_import_statement245); if (failed) return ;
            match(input,32,FOLLOW_32_in_function_import_statement247); if (failed) return ;
            pushFollow(FOLLOW_import_name_in_function_import_statement251);
            name=import_name();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement253);
            opt_semicolon();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			if (packageDescr != null) 
              				packageDescr.addFunctionImport( name );
              		
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
    // $ANTLR end function_import_statement


    // $ANTLR start import_name
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:342:1: import_name returns [String name] : id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '.*' )=>star= '.*' )? ;
    public String import_name() throws RecognitionException {   
        String name = null;

        Token id=null;
        Token star=null;


        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:347:3: (id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '.*' )=>star= '.*' )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:347:3: id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '.*' )=>star= '.*' )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name285); if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:347:32: ( ( '.' ID )=> '.' id= ID )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);
                if ( (LA5_0==33) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:347:34: ( '.' ID )=> '.' id= ID
            	    {
            	    match(input,33,FOLLOW_33_in_import_name291); if (failed) return name;
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_import_name295); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "." + id.getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:347:85: ( ( '.*' )=>star= '.*' )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0==34) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:347:86: ( '.*' )=>star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,34,FOLLOW_34_in_import_name305); if (failed) return name;
                    if ( backtracking==0 ) {
                       name = name + star.getText(); 
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:351:1: global : 'global' type= dotted_name id= ID opt_semicolon ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;



        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:355:3: ( 'global' type= dotted_name id= ID opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:355:3: 'global' type= dotted_name id= ID opt_semicolon
            {
            match(input,35,FOLLOW_35_in_global329); if (failed) return ;
            pushFollow(FOLLOW_dotted_name_in_global333);
            type=dotted_name();
            _fsp--;
            if (failed) return ;
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global337); if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_global339);
            opt_semicolon();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			packageDescr.addGlobal( id.getText(), type );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:362:1: function : loc= 'function' ( ( dotted_name )=>retType= dotted_name )? name= ID '(' ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )? ')' body= curly_chunk ;
    public void function() throws RecognitionException {   
        Token loc=null;
        Token name=null;
        String retType = null;

        String paramType = null;

        String paramName = null;

        String body = null;



        		FunctionDescr f = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:3: (loc= 'function' ( ( dotted_name )=>retType= dotted_name )? name= ID '(' ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )? ')' body= curly_chunk )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:3: loc= 'function' ( ( dotted_name )=>retType= dotted_name )? name= ID '(' ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )? ')' body= curly_chunk
            {
            loc=(Token)input.LT(1);
            match(input,32,FOLLOW_32_in_function366); if (failed) return ;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:18: ( ( dotted_name )=>retType= dotted_name )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( (LA7_0==ID) ) {
                int LA7_1 = input.LA(2);
                if ( (LA7_1==ID||LA7_1==LEFT_SQUARE||LA7_1==33) ) {
                    alt7=1;
                }
            }
            switch (alt7) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:367:19: ( dotted_name )=>retType= dotted_name
                    {
                    pushFollow(FOLLOW_dotted_name_in_function371);
                    retType=dotted_name();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function377); if (failed) return ;
            if ( backtracking==0 ) {

              			//System.err.println( "function :: " + name.getText() );
              			f = new FunctionDescr( name.getText(), retType );
              			f.setLocation(offset(loc.getLine()), loc.getCharPositionInLine());
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function386); if (failed) return ;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:374:4: ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( (LA11_0==ID) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:374:6: ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )*
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:374:6: ( ( dotted_name )=>paramType= dotted_name )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:374:7: ( dotted_name )=>paramType= dotted_name
                            {
                            pushFollow(FOLLOW_dotted_name_in_function396);
                            paramType=dotted_name();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function402);
                    paramName=argument();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      					f.addParameter( paramType, paramName );
                      				
                    }
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:378:5: ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);
                        if ( (LA10_0==36) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:378:7: ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument
                    	    {
                    	    match(input,36,FOLLOW_36_in_function416); if (failed) return ;
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:378:11: ( ( dotted_name )=>paramType= dotted_name )?
                    	    int alt9=2;
                    	    alt9 = dfa9.predict(input);
                    	    switch (alt9) {
                    	        case 1 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:378:12: ( dotted_name )=>paramType= dotted_name
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function421);
                    	            paramType=dotted_name();
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function427);
                    	    paramName=argument();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {

                    	      						f.addParameter( paramType, paramName );
                    	      					
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function451); if (failed) return ;
            pushFollow(FOLLOW_curly_chunk_in_function457);
            body=curly_chunk();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              			//strip out '{','}'
              			f.setText( body.substring( 1, body.length()-1 ) );

              			packageDescr.addFunction( f );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:396:1: query returns [QueryDescr query] : loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end' ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query = null;

        Token loc=null;
        String queryName = null;



        		query = null;
        		AndDescr lhs = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:402:3: (loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:402:3: loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end'
            {
            loc=(Token)input.LT(1);
            match(input,37,FOLLOW_37_in_query488); if (failed) return query;
            pushFollow(FOLLOW_word_in_query492);
            queryName=word();
            _fsp--;
            if (failed) return query;
            if ( backtracking==0 ) {
               
              			query = new QueryDescr( queryName, null ); 
              			query.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			lhs = new AndDescr(); query.setLhs( lhs ); 
              			lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:409:3: ( normal_lhs_block[lhs] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:411:4: normal_lhs_block[lhs]
            {
            pushFollow(FOLLOW_normal_lhs_block_in_query506);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;

            }

            match(input,END,FOLLOW_END_in_query521); if (failed) return query;

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:418:1: template returns [FactTemplateDescr template] : loc= 'template' templateName= ID opt_semicolon ( ( template_slot )=>slot= template_slot )+ 'end' opt_semicolon ;
    public FactTemplateDescr template() throws RecognitionException {   
        FactTemplateDescr template = null;

        Token loc=null;
        Token templateName=null;
        FieldTemplateDescr slot = null;



        		template = null;		
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:423:3: (loc= 'template' templateName= ID opt_semicolon ( ( template_slot )=>slot= template_slot )+ 'end' opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:423:3: loc= 'template' templateName= ID opt_semicolon ( ( template_slot )=>slot= template_slot )+ 'end' opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,38,FOLLOW_38_in_template547); if (failed) return template;
            templateName=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template551); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template553);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {

              			template = new FactTemplateDescr(templateName.getText());
              			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:428:3: ( ( template_slot )=>slot= template_slot )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);
                if ( (LA12_0==ID) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:429:4: ( template_slot )=>slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template568);
            	    slot=template_slot();
            	    _fsp--;
            	    if (failed) return template;
            	    if ( backtracking==0 ) {

            	      				template.addFieldTemplate(slot);
            	      			
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
            	    if (backtracking>0) {failed=true; return template;}
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);

            match(input,END,FOLLOW_END_in_template583); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template585);
            opt_semicolon();
            _fsp--;
            if (failed) return template;

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:437:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name name= ID opt_semicolon ;
    public FieldTemplateDescr template_slot() throws RecognitionException {   
        FieldTemplateDescr field = null;

        Token name=null;
        String fieldType = null;



        		field = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:443:4: (fieldType= dotted_name name= ID opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:443:4: fieldType= dotted_name name= ID opt_semicolon
            {
            pushFollow(FOLLOW_dotted_name_in_template_slot617);
            fieldType=dotted_name();
            _fsp--;
            if (failed) return field;
            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template_slot621); if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot623);
            opt_semicolon();
            _fsp--;
            if (failed) return field;
            if ( backtracking==0 ) {

              			
              			
              			field = new FieldTemplateDescr(name.getText(), fieldType);
              			field.setLocation( offset(name.getLine()), name.getCharPositionInLine() );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:452:1: rule returns [RuleDescr rule] : loc= RULE ruleName= word rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule = null;

        Token loc=null;
        String ruleName = null;



        		rule = null;
        		String consequence = "";
        		AndDescr lhs = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:459:3: (loc= RULE ruleName= word rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:459:3: loc= RULE ruleName= word rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule]
            {
            loc=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule654); if (failed) return rule;
            pushFollow(FOLLOW_word_in_rule658);
            ruleName=word();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               
              			debug( "start rule: " + ruleName );
              			rule = new RuleDescr( ruleName, null ); 
              			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            pushFollow(FOLLOW_rule_attributes_in_rule667);
            rule_attributes(rule);
            _fsp--;
            if (failed) return rule;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:466:3: ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0==WHEN) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:466:5: ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule676); if (failed) return rule;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:466:14: ( ( ':' )=> ':' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    if ( (LA13_0==39) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ( ':' )=> ':'
                            {
                            match(input,39,FOLLOW_39_in_rule678); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				lhs = new AndDescr(); rule.setLhs( lhs ); 
                      				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      			
                    }
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:471:4: ( normal_lhs_block[lhs] )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:473:5: normal_lhs_block[lhs]
                    {
                    pushFollow(FOLLOW_normal_lhs_block_in_rule696);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule717);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:482:1: rule_attributes[RuleDescr rule] : ( ( 'attributes' ':' )=> 'attributes' ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:4: ( ( ( 'attributes' ':' )=> 'attributes' ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:4: ( ( 'attributes' ':' )=> 'attributes' ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:4: ( ( 'attributes' ':' )=> 'attributes' ':' )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( (LA15_0==40) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:484:5: ( 'attributes' ':' )=> 'attributes' ':'
                    {
                    match(input,40,FOLLOW_40_in_rule_attributes738); if (failed) return ;
                    match(input,39,FOLLOW_39_in_rule_attributes740); if (failed) return ;

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:4: ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                if ( (LA17_0==36||(LA17_0>=41 && LA17_0<=49)) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:6: ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute
            	    {
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:485:6: ( ( ',' )=> ',' )?
            	    int alt16=2;
            	    int LA16_0 = input.LA(1);
            	    if ( (LA16_0==36) ) {
            	        alt16=1;
            	    }
            	    switch (alt16) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ( ',' )=> ','
            	            {
            	            match(input,36,FOLLOW_36_in_rule_attributes749); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes754);
            	    a=rule_attribute();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {

            	      					rule.addAttribute( a );
            	      				
            	    }

            	    }
            	    break;

            	default :
            	    break loop17;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:494:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | a= enabled );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d = null;

        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:499:4: ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | a= enabled )
            int alt18=9;
            switch ( input.LA(1) ) {
            case 44:
                alt18=1;
                break;
            case 45:
                alt18=2;
                break;
            case 48:
                alt18=3;
                break;
            case 49:
                alt18=4;
                break;
            case 47:
                alt18=5;
                break;
            case 46:
                alt18=6;
                break;
            case 41:
                alt18=7;
                break;
            case 42:
                alt18=8;
                break;
            case 43:
                alt18=9;
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("494:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | a= enabled );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:499:4: ( salience )=>a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute795);
                    a=salience();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:500:5: ( no_loop )=>a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute805);
                    a=no_loop();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:501:5: ( agenda_group )=>a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute816);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:502:5: ( duration )=>a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute829);
                    a=duration();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:503:5: ( activation_group )=>a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute843);
                    a=activation_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:504:5: ( auto_focus )=>a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute854);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:505:29: ( date_effective )=>a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute889);
                    a=date_effective();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 8 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:506:5: ( date_expires )=>a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute899);
                    a=date_expires();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 9 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:507:29: a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute933);
                    a=enabled();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d=a;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:511:1: date_effective returns [AttributeDescr d] : loc= 'date-effective' val= STRING ;
    public AttributeDescr date_effective() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:516:3: (loc= 'date-effective' val= STRING )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:516:3: loc= 'date-effective' val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,41,FOLLOW_41_in_date_effective965); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective969); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "date-effective", getString( val ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:524:1: date_expires returns [AttributeDescr d] : loc= 'date-expires' val= STRING ;
    public AttributeDescr date_expires() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:529:3: (loc= 'date-expires' val= STRING )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:529:3: loc= 'date-expires' val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,42,FOLLOW_42_in_date_expires1000); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1004); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "date-expires", getString( val ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:538:1: enabled returns [AttributeDescr d] : loc= 'enabled' t= BOOL opt_semicolon ;
    public AttributeDescr enabled() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:4: (loc= 'enabled' t= BOOL opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:543:4: loc= 'enabled' t= BOOL opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,43,FOLLOW_43_in_enabled1037); if (failed) return d;
            t=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1041); if (failed) return d;
            pushFollow(FOLLOW_opt_semicolon_in_enabled1043);
            opt_semicolon();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              				d = new AttributeDescr( "enabled", t.getText() );
              				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:554:1: salience returns [AttributeDescr d ] : loc= 'salience' i= INT opt_semicolon ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:559:3: (loc= 'salience' i= INT opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:559:3: loc= 'salience' i= INT opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,44,FOLLOW_44_in_salience1085); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience1089); if (failed) return d;
            pushFollow(FOLLOW_opt_semicolon_in_salience1091);
            opt_semicolon();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "salience", i.getText() );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:566:1: no_loop returns [AttributeDescr d] : ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:3: ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) )
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( (LA19_0==45) ) {
                int LA19_1 = input.LA(2);
                if ( (LA19_1==BOOL) ) {
                    alt19=2;
                }
                else if ( (LA19_1==EOF||LA19_1==WHEN||LA19_1==THEN||LA19_1==29||LA19_1==36||(LA19_1>=41 && LA19_1<=49)) ) {
                    alt19=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("566:1: no_loop returns [AttributeDescr d] : ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );", 19, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("566:1: no_loop returns [AttributeDescr d] : ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:3: ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:571:3: (loc= 'no-loop' opt_semicolon )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:572:4: loc= 'no-loop' opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,45,FOLLOW_45_in_no_loop1126); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_no_loop1128);
                    opt_semicolon();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      				d = new AttributeDescr( "no-loop", "true" );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:579:3: (loc= 'no-loop' t= BOOL opt_semicolon )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:579:3: (loc= 'no-loop' t= BOOL opt_semicolon )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:580:4: loc= 'no-loop' t= BOOL opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,45,FOLLOW_45_in_no_loop1153); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1157); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_no_loop1159);
                    opt_semicolon();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      				d = new AttributeDescr( "no-loop", t.getText() );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      			
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:590:1: auto_focus returns [AttributeDescr d] : ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );
    public AttributeDescr auto_focus() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:595:3: ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) )
            int alt20=2;
            int LA20_0 = input.LA(1);
            if ( (LA20_0==46) ) {
                int LA20_1 = input.LA(2);
                if ( (LA20_1==BOOL) ) {
                    alt20=2;
                }
                else if ( (LA20_1==EOF||LA20_1==WHEN||LA20_1==THEN||LA20_1==29||LA20_1==36||(LA20_1>=41 && LA20_1<=49)) ) {
                    alt20=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("590:1: auto_focus returns [AttributeDescr d] : ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );", 20, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("590:1: auto_focus returns [AttributeDescr d] : ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:595:3: ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:595:3: (loc= 'auto-focus' opt_semicolon )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:596:4: loc= 'auto-focus' opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,46,FOLLOW_46_in_auto_focus1205); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_auto_focus1207);
                    opt_semicolon();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      				d = new AttributeDescr( "auto-focus", "true" );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:603:3: (loc= 'auto-focus' t= BOOL opt_semicolon )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:603:3: (loc= 'auto-focus' t= BOOL opt_semicolon )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:604:4: loc= 'auto-focus' t= BOOL opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,46,FOLLOW_46_in_auto_focus1232); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1236); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_auto_focus1238);
                    opt_semicolon();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      				d = new AttributeDescr( "auto-focus", t.getText() );
                      				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      			
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:614:1: activation_group returns [AttributeDescr d] : loc= 'activation-group' name= STRING opt_semicolon ;
    public AttributeDescr activation_group() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token name=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:619:3: (loc= 'activation-group' name= STRING opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:619:3: loc= 'activation-group' name= STRING opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,47,FOLLOW_47_in_activation_group1280); if (failed) return d;
            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1284); if (failed) return d;
            pushFollow(FOLLOW_opt_semicolon_in_activation_group1286);
            opt_semicolon();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "activation-group", getString( name ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
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


    // $ANTLR start agenda_group
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:626:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' name= STRING opt_semicolon ;
    public AttributeDescr agenda_group() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token name=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:631:3: (loc= 'agenda-group' name= STRING opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:631:3: loc= 'agenda-group' name= STRING opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,48,FOLLOW_48_in_agenda_group1315); if (failed) return d;
            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1319); if (failed) return d;
            pushFollow(FOLLOW_opt_semicolon_in_agenda_group1321);
            opt_semicolon();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "agenda-group", getString( name ) );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:639:1: duration returns [AttributeDescr d] : loc= 'duration' i= INT ;
    public AttributeDescr duration() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:644:3: (loc= 'duration' i= INT )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:644:3: loc= 'duration' i= INT
            {
            loc=(Token)input.LT(1);
            match(input,49,FOLLOW_49_in_duration1353); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1357); if (failed) return d;
            if ( backtracking==0 ) {

              			d = new AttributeDescr( "duration", i.getText() );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
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


    // $ANTLR start normal_lhs_block
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:652:1: normal_lhs_block[AndDescr descrs] : ( ( lhs )=>d= lhs )* ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        BaseDescr d = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:654:3: ( ( ( lhs )=>d= lhs )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:654:3: ( ( lhs )=>d= lhs )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:654:3: ( ( lhs )=>d= lhs )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);
                if ( (LA21_0==ID||LA21_0==LEFT_PAREN||(LA21_0>=73 && LA21_0<=75)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:654:5: ( lhs )=>d= lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1383);
            	    d=lhs();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       descrs.addDescr( d ); 
            	    }

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
        return ;
    }
    // $ANTLR end normal_lhs_block


    // $ANTLR start lhs
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:708:1: lhs returns [BaseDescr d] : l= lhs_or ;
    public BaseDescr lhs() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr l = null;



        		d=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:4: (l= lhs_or )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:712:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1421);
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


    // $ANTLR start lhs_column
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:716:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );
    public BaseDescr lhs_column() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr f = null;



        		d=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:720:4: ( ( fact_binding )=>f= fact_binding | f= fact )
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( (LA22_0==ID) ) {
                int LA22_1 = input.LA(2);
                if ( (LA22_1==39) ) {
                    alt22=1;
                }
                else if ( (LA22_1==LEFT_PAREN||LA22_1==LEFT_SQUARE||LA22_1==33) ) {
                    alt22=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("716:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );", 22, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("716:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:720:4: ( fact_binding )=>f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_column1449);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = f; 
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:721:4: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_column1458);
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
    // $ANTLR end lhs_column


    // $ANTLR start from_statement
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:724:1: from_statement returns [FromDescr d] : 'from' ds= from_source ;
    public FromDescr from_statement() throws RecognitionException {   
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;



        		d=factory.createFrom();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:729:2: ( 'from' ds= from_source )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:729:2: 'from' ds= from_source
            {
            match(input,50,FOLLOW_50_in_from_statement1483); if (failed) return d;
            pushFollow(FOLLOW_from_source_in_from_statement1487);
            ds=from_source();
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:739:1: from_source returns [DeclarativeInvokerDescr ds] : ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) ) ( ( expression_chain[ad] )=> expression_chain[ad] )? ;
    public DeclarativeInvokerDescr from_source() throws RecognitionException {   
        DeclarativeInvokerDescr ds = null;

        Token functionName=null;
        Token var=null;
        String args = null;



        		ds = null;
        		AccessorDescr ad = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:3: ( ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) ) ( ( expression_chain[ad] )=> expression_chain[ad] )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:3: ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) ) ( ( expression_chain[ad] )=> expression_chain[ad] )?
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:3: ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) )
            int alt23=2;
            int LA23_0 = input.LA(1);
            if ( (LA23_0==ID) ) {
                if ( (synpred35()) ) {
                    alt23=1;
                }
                else if ( (true) ) {
                    alt23=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("745:3: ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) )", 23, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ds;}
                NoViableAltException nvae =
                    new NoViableAltException("745:3: ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) )", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:4: ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:4: (functionName= ID args= paren_chunk )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:6: functionName= ID args= paren_chunk
                    {
                    functionName=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1530); if (failed) return ds;
                    pushFollow(FOLLOW_paren_chunk_in_from_source1534);
                    args=paren_chunk();
                    _fsp--;
                    if (failed) return ds;
                    if ( backtracking==0 ) {

                       				ad = new AccessorDescr();	
                      				ad.setLocation( offset(functionName.getLine()), functionName.getCharPositionInLine() );
                      				ds = ad;
                      				FunctionCallDescr fc = new FunctionCallDescr(functionName.getText());
                      				fc.setLocation( offset(functionName.getLine()), functionName.getCharPositionInLine() );			
                      				fc.setArguments(args);
                      				ad.addInvoker(fc);
                      			
                    }

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:757:3: (var= ID )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:757:3: (var= ID )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:757:7: var= ID
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1567); if (failed) return ds;
                    if ( backtracking==0 ) {

                      			ad = new AccessorDescr(var.getText());	
                      			ad.setLocation( offset(var.getLine()), var.getCharPositionInLine() );
                      			ds = ad;
                      		    
                    }

                    }


                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:765:3: ( ( expression_chain[ad] )=> expression_chain[ad] )?
            int alt24=2;
            int LA24_0 = input.LA(1);
            if ( (LA24_0==33) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ( expression_chain[ad] )=> expression_chain[ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source1590);
                    expression_chain(ad);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:768:1: expression_chain[AccessorDescr as] : ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? ) ;
    public void expression_chain(AccessorDescr as) throws RecognitionException {   
        Token field=null;
        String sqarg = null;

        String paarg = null;



          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:774:2: ( ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:774:2: ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:774:2: ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:774:4: '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )?
            {
            match(input,33,FOLLOW_33_in_expression_chain1615); if (failed) return ;
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_expression_chain1619); if (failed) return ;
            if ( backtracking==0 ) {

              	        fa = new FieldAccessDescr(field.getText());	
              		fa.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
              	    
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:779:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?
            int alt25=3;
            int LA25_0 = input.LA(1);
            if ( (LA25_0==LEFT_SQUARE) ) {
                alt25=1;
            }
            else if ( (LA25_0==LEFT_PAREN) ) {
                if ( (synpred38()) ) {
                    alt25=2;
                }
            }
            switch (alt25) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:780:6: ( LEFT_SQUARE )=>sqarg= square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain1650);
                    sqarg=square_chunk();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      	          fa.setArgument( sqarg );	
                      	      
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:6: ( LEFT_PAREN )=>paarg= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain1683);
                    paarg=paren_chunk();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      	    	  ma = new MethodAccessDescr( field.getText(), paarg );	
                      		  ma.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
                      		
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:798:4: ( ( expression_chain[as] )=> expression_chain[as] )?
            int alt26=2;
            int LA26_0 = input.LA(1);
            if ( (LA26_0==33) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:0:0: ( expression_chain[as] )=> expression_chain[as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1703);
                    expression_chain(as);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:802:1: accumulate_statement returns [AccumulateDescr d] : loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')' ;
    public AccumulateDescr accumulate_statement() throws RecognitionException {   
        AccumulateDescr d = null;

        Token loc=null;
        BaseDescr column = null;

        String text = null;



        		d = factory.createAccumulate();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:807:10: (loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:807:10: loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')'
            {
            loc=(Token)input.LT(1);
            match(input,50,FOLLOW_50_in_accumulate_statement1744); if (failed) return d;
            match(input,51,FOLLOW_51_in_accumulate_statement1746); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement1756); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_accumulate_statement1760);
            column=lhs_column();
            _fsp--;
            if (failed) return d;
            match(input,36,FOLLOW_36_in_accumulate_statement1762); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setSourceColumn( (ColumnDescr)column );
              		
            }
            match(input,52,FOLLOW_52_in_accumulate_statement1771); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1775);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            match(input,36,FOLLOW_36_in_accumulate_statement1777); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setInitCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,53,FOLLOW_53_in_accumulate_statement1786); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1790);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            match(input,36,FOLLOW_36_in_accumulate_statement1792); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setActionCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,54,FOLLOW_54_in_accumulate_statement1801); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1805);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement1807); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setResultCode( text.substring(1, text.length()-1) );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:829:1: collect_statement returns [CollectDescr d] : loc= 'from' 'collect' '(' column= lhs_column ')' ;
    public CollectDescr collect_statement() throws RecognitionException {   
        CollectDescr d = null;

        Token loc=null;
        BaseDescr column = null;



        		d = factory.createCollect();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:834:10: (loc= 'from' 'collect' '(' column= lhs_column ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:834:10: loc= 'from' 'collect' '(' column= lhs_column ')'
            {
            loc=(Token)input.LT(1);
            match(input,50,FOLLOW_50_in_collect_statement1850); if (failed) return d;
            match(input,55,FOLLOW_55_in_collect_statement1852); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement1862); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_collect_statement1866);
            column=lhs_column();
            _fsp--;
            if (failed) return d;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement1868); if (failed) return d;
            if ( backtracking==0 ) {

              		        d.setSourceColumn( (ColumnDescr)column );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:915:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public BaseDescr fact_binding() throws RecognitionException {   
        BaseDescr d = null;

        Token id=null;
        BaseDescr fe = null;



        		d=null;
        		boolean multi=false;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:921:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:921:4: id= ID ':' fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding1903); if (failed) return d;
            match(input,39,FOLLOW_39_in_fact_binding1913); if (failed) return d;
            pushFollow(FOLLOW_fact_expression_in_fact_binding1917);
            fe=fact_expression(id.getText());
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {

               			d=fe;
               		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:929:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact );
    public BaseDescr fact_expression(String id) throws RecognitionException {   
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;



         		pd = null;
         		boolean multi = false;
         	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:934:5: ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact )
            int alt27=2;
            int LA27_0 = input.LA(1);
            if ( (LA27_0==LEFT_PAREN) ) {
                alt27=1;
            }
            else if ( (LA27_0==ID) ) {
                alt27=2;
            }
            else {
                if (backtracking>0) {failed=true; return pd;}
                NoViableAltException nvae =
                    new NoViableAltException("929:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact );", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:934:5: ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression1949); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_paren_in_fact_expression1953);
                    fe=fact_expression_in_paren(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression1956); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:935:6: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression1967);
                    f=fact();
                    _fsp--;
                    if (failed) return pd;
                    if ( backtracking==0 ) {

                       			((ColumnDescr)f).setIdentifier( id );
                       			pd = f;
                       		
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
        return pd;
    }
    // $ANTLR end fact_expression


    // $ANTLR start fact_expression_in_paren
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:943:2: fact_expression_in_paren[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )* );
    public BaseDescr fact_expression_in_paren(String id) throws RecognitionException {   
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;



         		pd = null;
         		boolean multi = false;
         	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:948:5: ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )* )
            int alt29=2;
            int LA29_0 = input.LA(1);
            if ( (LA29_0==LEFT_PAREN) ) {
                alt29=1;
            }
            else if ( (LA29_0==ID) ) {
                alt29=2;
            }
            else {
                if (backtracking>0) {failed=true; return pd;}
                NoViableAltException nvae =
                    new NoViableAltException("943:2: fact_expression_in_paren[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )* );", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:948:5: ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression_in_paren1998); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_paren_in_fact_expression_in_paren2002);
                    fe=fact_expression_in_paren(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression_in_paren2004); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:949:6: f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )*
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression_in_paren2015);
                    f=fact();
                    _fsp--;
                    if (failed) return pd;
                    if ( backtracking==0 ) {

                       			((ColumnDescr)f).setIdentifier( id );
                       			pd = f;
                       		
                    }
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:954:4: ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);
                        if ( ((LA28_0>=56 && LA28_0<=57)) ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:954:6: ( ('or'|'||') fact )=> ('or'|'||')f= fact
                    	    {
                    	    if ( (input.LA(1)>=56 && input.LA(1)<=57) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return pd;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression_in_paren2028);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      	if ( ! multi ) {
                    	       					BaseDescr first = pd;
                    	       					pd = new OrDescr();
                    	       					((OrDescr)pd).addDescr( first );
                    	       					multi=true;
                    	       				}
                    	       			
                    	    }
                    	    pushFollow(FOLLOW_fact_in_fact_expression_in_paren2045);
                    	    f=fact();
                    	    _fsp--;
                    	    if (failed) return pd;
                    	    if ( backtracking==0 ) {

                    	       				((ColumnDescr)f).setIdentifier( id );
                    	       				((OrDescr)pd).addDescr( f );
                    	       			
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop28;
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
    // $ANTLR end fact_expression_in_paren


    // $ANTLR start fact
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:970:1: fact returns [BaseDescr d] : id= dotted_name loc= '(' ( ( constraints )=>c= constraints )? endLoc= ')' ;
    public BaseDescr fact() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        Token endLoc=null;
        String id = null;

        List c = null;



        		d=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:974:5: (id= dotted_name loc= '(' ( ( constraints )=>c= constraints )? endLoc= ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:974:5: id= dotted_name loc= '(' ( ( constraints )=>c= constraints )? endLoc= ')'
            {
            pushFollow(FOLLOW_dotted_name_in_fact2084);
            id=dotted_name();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               			d = new ColumnDescr( id ); 
               		
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact2097); if (failed) return d;
            if ( backtracking==0 ) {

               				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
               			
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:981:4: ( ( constraints )=>c= constraints )?
            int alt30=2;
            int LA30_0 = input.LA(1);
            if ( (LA30_0==ID) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:981:6: ( constraints )=>c= constraints
                    {
                    pushFollow(FOLLOW_constraints_in_fact2109);
                    c=constraints();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {

                      		 		for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                       					((ColumnDescr)d).addDescr( (BaseDescr) cIter.next() );
                       				}
                       			
                    }

                    }
                    break;

            }

            endLoc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact2130); if (failed) return d;
            if ( backtracking==0 ) {

              			d.setEndLocation( offset(endLoc.getLine()), endLoc.getCharPositionInLine() );	
               		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:995:1: constraints returns [List constraints] : ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) ( ( ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )=> ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )* ;
    public List constraints() throws RecognitionException {   
        List constraints = null;


        		constraints = new ArrayList();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:4: ( ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) ( ( ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )=> ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) ( ( ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )=> ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )
            int alt31=2;
            int LA31_0 = input.LA(1);
            if ( (LA31_0==ID) ) {
                int LA31_1 = input.LA(2);
                if ( (LA31_1==39) ) {
                    int LA31_2 = input.LA(3);
                    if ( (LA31_2==ID) ) {
                        int LA31_4 = input.LA(4);
                        if ( (LA31_4==70) ) {
                            alt31=2;
                        }
                        else if ( (LA31_4==EOF||LA31_4==RIGHT_PAREN||LA31_4==36||(LA31_4>=60 && LA31_4<=68)) ) {
                            alt31=1;
                        }
                        else {
                            if (backtracking>0) {failed=true; return constraints;}
                            NoViableAltException nvae =
                                new NoViableAltException("999:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 31, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return constraints;}
                        NoViableAltException nvae =
                            new NoViableAltException("999:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 31, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA31_1==EOF||LA31_1==RIGHT_PAREN||LA31_1==36||(LA31_1>=60 && LA31_1<=68)) ) {
                    alt31=1;
                }
                else {
                    if (backtracking>0) {failed=true; return constraints;}
                    NoViableAltException nvae =
                        new NoViableAltException("999:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 31, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return constraints;}
                NoViableAltException nvae =
                    new NoViableAltException("999:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:5: ( constraint[constraints] )=> constraint[constraints]
                    {
                    pushFollow(FOLLOW_constraint_in_constraints2160);
                    constraint(constraints);
                    _fsp--;
                    if (failed) return constraints;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:999:29: predicate[constraints]
                    {
                    pushFollow(FOLLOW_predicate_in_constraints2163);
                    predicate(constraints);
                    _fsp--;
                    if (failed) return constraints;

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1000:3: ( ( ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )=> ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                if ( (LA33_0==36) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1000:5: ( ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )=> ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )
            	    {
            	    match(input,36,FOLLOW_36_in_constraints2171); if (failed) return constraints;
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1000:9: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )
            	    int alt32=2;
            	    int LA32_0 = input.LA(1);
            	    if ( (LA32_0==ID) ) {
            	        int LA32_1 = input.LA(2);
            	        if ( (LA32_1==39) ) {
            	            int LA32_2 = input.LA(3);
            	            if ( (LA32_2==ID) ) {
            	                int LA32_4 = input.LA(4);
            	                if ( (LA32_4==70) ) {
            	                    alt32=2;
            	                }
            	                else if ( (LA32_4==EOF||LA32_4==RIGHT_PAREN||LA32_4==36||(LA32_4>=60 && LA32_4<=68)) ) {
            	                    alt32=1;
            	                }
            	                else {
            	                    if (backtracking>0) {failed=true; return constraints;}
            	                    NoViableAltException nvae =
            	                        new NoViableAltException("1000:9: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 32, 4, input);

            	                    throw nvae;
            	                }
            	            }
            	            else {
            	                if (backtracking>0) {failed=true; return constraints;}
            	                NoViableAltException nvae =
            	                    new NoViableAltException("1000:9: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 32, 2, input);

            	                throw nvae;
            	            }
            	        }
            	        else if ( (LA32_1==EOF||LA32_1==RIGHT_PAREN||LA32_1==36||(LA32_1>=60 && LA32_1<=68)) ) {
            	            alt32=1;
            	        }
            	        else {
            	            if (backtracking>0) {failed=true; return constraints;}
            	            NoViableAltException nvae =
            	                new NoViableAltException("1000:9: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 32, 1, input);

            	            throw nvae;
            	        }
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return constraints;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("1000:9: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 32, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt32) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1000:10: ( constraint[constraints] )=> constraint[constraints]
            	            {
            	            pushFollow(FOLLOW_constraint_in_constraints2174);
            	            constraint(constraints);
            	            _fsp--;
            	            if (failed) return constraints;

            	            }
            	            break;
            	        case 2 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1000:34: predicate[constraints]
            	            {
            	            pushFollow(FOLLOW_predicate_in_constraints2177);
            	            predicate(constraints);
            	            _fsp--;
            	            if (failed) return constraints;

            	            }
            	            break;

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
        return constraints;
    }
    // $ANTLR end constraints


    // $ANTLR start constraint
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1003:1: constraint[List constraints] : ( ( ID ':' )=>fb= ID ':' )? f= ID ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* )=>rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )? ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token con=null;
        RestrictionDescr rd = null;



        		BaseDescr d = null;
        		FieldConstraintDescr fc = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1009:3: ( ( ( ID ':' )=>fb= ID ':' )? f= ID ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* )=>rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1009:3: ( ( ID ':' )=>fb= ID ':' )? f= ID ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* )=>rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )?
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1009:3: ( ( ID ':' )=>fb= ID ':' )?
            int alt34=2;
            int LA34_0 = input.LA(1);
            if ( (LA34_0==ID) ) {
                int LA34_1 = input.LA(2);
                if ( (LA34_1==39) ) {
                    alt34=1;
                }
            }
            switch (alt34) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1009:5: ( ID ':' )=>fb= ID ':'
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint2206); if (failed) return ;
                    match(input,39,FOLLOW_39_in_constraint2208); if (failed) return ;

                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint2218); if (failed) return ;
            if ( backtracking==0 ) {

              			if ( fb != null ) {
              				d = new FieldBindingDescr( f.getText(), fb.getText() );
              				d.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              				constraints.add( d );
              			} 
              			fc = new FieldConstraintDescr(f.getText());
              			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1020:3: ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* )=>rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )?
            int alt36=2;
            int LA36_0 = input.LA(1);
            if ( ((LA36_0>=60 && LA36_0<=68)) ) {
                alt36=1;
            }
            switch (alt36) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1021:4: ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* )=>rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                    {
                    pushFollow(FOLLOW_constraint_expression_in_constraint2234);
                    rd=constraint_expression();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {

                      				fc.addRestriction(rd);
                      				constraints.add(fc);
                      			
                    }
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1026:4: ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);
                        if ( ((LA35_0>=58 && LA35_0<=59)) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1027:5: ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression
                    	    {
                    	    con=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=58 && input.LA(1)<=59) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return ;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint2253);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {

                    	      					if (con.getText().equals("&") ) {								
                    	      						fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	      					} else {
                    	      						fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	      					}							
                    	      				
                    	    }
                    	    pushFollow(FOLLOW_constraint_expression_in_constraint2270);
                    	    rd=constraint_expression();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {

                    	      					fc.addRestriction(rd);
                    	      				
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1043:1: constraint_expression returns [RestrictionDescr rd] : op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) ;
    public RestrictionDescr constraint_expression() throws RecognitionException {   
        RestrictionDescr rd = null;

        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1045:3: (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1045:3: op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
            {
            op=(Token)input.LT(1);
            if ( (input.LA(1)>=60 && input.LA(1)<=68) ) {
                input.consume();
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return rd;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint_expression2322);    throw mse;
            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1055:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
            int alt37=4;
            switch ( input.LA(1) ) {
            case ID:
                int LA37_1 = input.LA(2);
                if ( (LA37_1==33) ) {
                    alt37=2;
                }
                else if ( (LA37_1==EOF||LA37_1==RIGHT_PAREN||LA37_1==36||(LA37_1>=58 && LA37_1<=59)) ) {
                    alt37=1;
                }
                else {
                    if (backtracking>0) {failed=true; return rd;}
                    NoViableAltException nvae =
                        new NoViableAltException("1055:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )", 37, 1, input);

                    throw nvae;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case 69:
                alt37=3;
                break;
            case LEFT_PAREN:
                alt37=4;
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("1055:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1055:5: ( ID )=>bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint_expression2389); if (failed) return rd;
                    if ( backtracking==0 ) {

                      				rd = new VariableRestrictionDescr(op.getText(), bvc.getText());
                      			
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1060:4: ( enum_constraint )=>lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_constraint_expression2405);
                    lc=enum_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc, true);
                      			
                    }

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1065:4: ( literal_constraint )=>lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_constraint_expression2428);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc);
                      			
                    }

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1069:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_constraint_expression2442);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1076:1: literal_constraint returns [String text] : ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' ) ;
    public String literal_constraint() throws RecognitionException {   
        String text = null;

        Token t=null;


        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1080:4: ( ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1080:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1080:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' )
            int alt38=5;
            switch ( input.LA(1) ) {
            case STRING:
                alt38=1;
                break;
            case INT:
                alt38=2;
                break;
            case FLOAT:
                alt38=3;
                break;
            case BOOL:
                alt38=4;
                break;
            case 69:
                alt38=5;
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("1080:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' )", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1080:6: ( STRING )=>t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint2481); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1081:5: ( INT )=>t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint2492); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1082:5: ( FLOAT )=>t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint2505); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1083:5: ( BOOL )=>t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint2516); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1084:5: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,69,FOLLOW_69_in_literal_constraint2528); if (failed) return text;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1088:1: enum_constraint returns [String text] : (cls= ID '.' en= ID ) ;
    public String enum_constraint() throws RecognitionException {   
        String text = null;

        Token cls=null;
        Token en=null;


        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1092:4: ( (cls= ID '.' en= ID ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1092:4: (cls= ID '.' en= ID )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1092:4: (cls= ID '.' en= ID )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1092:5: cls= ID '.' en= ID
            {
            cls=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2559); if (failed) return text;
            match(input,33,FOLLOW_33_in_enum_constraint2561); if (failed) return text;
            en=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2565); if (failed) return text;

            }

            if ( backtracking==0 ) {
               text = cls.getText() + "." + en.getText(); 
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
    // $ANTLR end enum_constraint


    // $ANTLR start predicate
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1096:1: predicate[List constraints] : decl= ID ':' field= ID '->' text= paren_chunk ;
    public void predicate(List constraints) throws RecognitionException {   
        Token decl=null;
        Token field=null;
        String text = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1098:3: (decl= ID ':' field= ID '->' text= paren_chunk )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1098:3: decl= ID ':' field= ID '->' text= paren_chunk
            {
            decl=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2587); if (failed) return ;
            match(input,39,FOLLOW_39_in_predicate2589); if (failed) return ;
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2593); if (failed) return ;
            match(input,70,FOLLOW_70_in_predicate2595); if (failed) return ;
            pushFollow(FOLLOW_paren_chunk_in_predicate2599);
            text=paren_chunk();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {

              		        String body = text.substring(1, text.length()-1);
              			PredicateDescr d = new PredicateDescr(field.getText(), decl.getText(), body );
              			constraints.add( d );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1106:1: paren_chunk returns [String text] : loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN ;
    public String paren_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1112:10: (loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1112:10: loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk2646); if (failed) return text;
            if ( backtracking==0 ) {

              		    buf.append( loc.getText());
               
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1122:3: ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )*
            loop39:
            do {
                int alt39=3;
                int LA39_0 = input.LA(1);
                if ( ((LA39_0>=ID && LA39_0<=FLOAT)||(LA39_0>=LEFT_CURLY && LA39_0<=76)) ) {
                    alt39=1;
                }
                else if ( (LA39_0==LEFT_PAREN) ) {
                    alt39=2;
                }


                switch (alt39) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1123:4: (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN)
            	    {
            	    if ( (input.LA(1)>=ID && input.LA(1)<=FLOAT)||(input.LA(1)>=LEFT_CURLY && input.LA(1)<=76) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk2662);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1128:4: ( paren_chunk )=>chunk= paren_chunk
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk2686);
            	    chunk=paren_chunk();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop39;
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
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk2722); if (failed) return text;
            if ( backtracking==0 ) {

                                  buf.append( loc.getText() );
              		    text = buf.toString();
                              
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1147:1: curly_chunk returns [String text] : loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk )=>chunk= curly_chunk )* loc= RIGHT_CURLY ;
    public String curly_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1153:3: (loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk )=>chunk= curly_chunk )* loc= RIGHT_CURLY )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1153:3: loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk )=>chunk= curly_chunk )* loc= RIGHT_CURLY
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk2772); if (failed) return text;
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              		    
              		    buf.append( loc.getText() );
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1161:3: ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk )=>chunk= curly_chunk )*
            loop40:
            do {
                int alt40=3;
                int LA40_0 = input.LA(1);
                if ( ((LA40_0>=ID && LA40_0<=RIGHT_PAREN)||(LA40_0>=LEFT_SQUARE && LA40_0<=76)) ) {
                    alt40=1;
                }
                else if ( (LA40_0==LEFT_CURLY) ) {
                    alt40=2;
                }


                switch (alt40) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1162:4: (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY)
            	    {
            	    if ( (input.LA(1)>=ID && input.LA(1)<=RIGHT_PAREN)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=76) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk2788);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1167:4: ( curly_chunk )=>chunk= curly_chunk
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk2812);
            	    chunk=curly_chunk();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop40;
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
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk2848); if (failed) return text;
            if ( backtracking==0 ) {

                                  buf.append( loc.getText() );
              		    text = buf.toString();
                              
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1186:1: square_chunk returns [String text] : loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE ;
    public String square_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;
        String chunk = null;



                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1192:10: (loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1192:10: loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk2909); if (failed) return text;
            if ( backtracking==0 ) {

              		    buf.append( loc.getText());
               
              		
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1202:3: ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )*
            loop41:
            do {
                int alt41=3;
                int LA41_0 = input.LA(1);
                if ( ((LA41_0>=ID && LA41_0<=RIGHT_CURLY)||(LA41_0>=END && LA41_0<=76)) ) {
                    alt41=1;
                }
                else if ( (LA41_0==LEFT_SQUARE) ) {
                    alt41=2;
                }


                switch (alt41) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1203:4: (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE)
            	    {
            	    if ( (input.LA(1)>=ID && input.LA(1)<=RIGHT_CURLY)||(input.LA(1)>=END && input.LA(1)<=76) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk2925);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1208:4: ( square_chunk )=>chunk= square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk2949);
            	    chunk=square_chunk();
            	    _fsp--;
            	    if (failed) return text;
            	    if ( backtracking==0 ) {

            	      			    buf.append( chunk );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop41;
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
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk2985); if (failed) return text;
            if ( backtracking==0 ) {

                                  buf.append( loc.getText() );
              		    text = buf.toString();
                              
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1227:1: retval_constraint returns [String text] : c= paren_chunk ;
    public String retval_constraint() throws RecognitionException {   
        String text = null;

        String c = null;



        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1232:3: (c= paren_chunk )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1232:3: c= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint3030);
            c=paren_chunk();
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1240:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )* ;
    public BaseDescr lhs_or() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		OrDescr or = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1246:3: (left= lhs_and ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1246:3: left= lhs_and ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )*
            {
            pushFollow(FOLLOW_lhs_and_in_lhs_or3063);
            left=lhs_and();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              d = left; 
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1247:3: ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);
                if ( ((LA42_0>=56 && LA42_0<=57)) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1247:5: ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and
            	    {
            	    if ( (input.LA(1)>=56 && input.LA(1)<=57) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or3072);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_and_in_lhs_or3082);
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
            	    break loop42;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1261:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )* ;
    public BaseDescr lhs_and() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        		AndDescr and = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1267:3: (left= lhs_unary ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1267:3: left= lhs_unary ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )*
            {
            pushFollow(FOLLOW_lhs_unary_in_lhs_and3118);
            left=lhs_unary();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = left; 
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1268:3: ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                if ( ((LA43_0>=71 && LA43_0<=72)) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1268:5: ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=71 && input.LA(1)<=72) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and3127);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_unary_in_lhs_and3137);
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
            	    break loop43;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1282:1: lhs_unary returns [BaseDescr d] : ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon ;
    public BaseDescr lhs_unary() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr u = null;

        FromDescr fm = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1286:4: ( ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1286:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1286:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | '(' u= lhs ')' )
            int alt45=5;
            switch ( input.LA(1) ) {
            case 73:
                alt45=1;
                break;
            case 74:
                alt45=2;
                break;
            case 75:
                alt45=3;
                break;
            case ID:
                alt45=4;
                break;
            case LEFT_PAREN:
                alt45=5;
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1286:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | '(' u= lhs ')' )", 45, 0, input);

                throw nvae;
            }

            switch (alt45) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1286:6: ( lhs_exist )=>u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary3174);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1287:5: ( lhs_not )=>u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary3182);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1288:5: ( lhs_eval )=>u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary3190);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1289:5: ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )?
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_unary3198);
                    u=lhs_column();
                    _fsp--;
                    if (failed) return d;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1289:18: ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )?
                    int alt44=4;
                    int LA44_0 = input.LA(1);
                    if ( (LA44_0==50) ) {
                        switch ( input.LA(2) ) {
                            case 51:
                                alt44=2;
                                break;
                            case 55:
                                alt44=3;
                                break;
                            case ID:
                                alt44=1;
                                break;
                        }

                    }
                    switch (alt44) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1290:14: ( ( from_statement ) )=> (fm= from_statement )
                            {
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1290:14: (fm= from_statement )
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1290:15: fm= from_statement
                            {
                            pushFollow(FOLLOW_from_statement_in_lhs_unary3218);
                            fm=from_statement();
                            _fsp--;
                            if (failed) return d;
                            if ( backtracking==0 ) {
                              fm.setColumn((ColumnDescr) u); u=fm;
                            }

                            }


                            }
                            break;
                        case 2 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1291:14: ( ( accumulate_statement ) )=> (ac= accumulate_statement )
                            {
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1291:14: (ac= accumulate_statement )
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1291:15: ac= accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_lhs_unary3240);
                            ac=accumulate_statement();
                            _fsp--;
                            if (failed) return d;
                            if ( backtracking==0 ) {
                              ac.setResultColumn((ColumnDescr) u); u=ac;
                            }

                            }


                            }
                            break;
                        case 3 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1292:14: ( ( collect_statement ) )=> (cs= collect_statement )
                            {
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1292:14: (cs= collect_statement )
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1292:15: cs= collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_lhs_unary3261);
                            cs=collect_statement();
                            _fsp--;
                            if (failed) return d;
                            if ( backtracking==0 ) {
                              cs.setResultColumn((ColumnDescr) u); u=cs;
                            }

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1294:5: '(' u= lhs ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary3284); if (failed) return d;
                    pushFollow(FOLLOW_lhs_in_lhs_unary3288);
                    u=lhs();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary3290); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               d = u; 
            }
            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary3300);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1299:1: lhs_exist returns [BaseDescr d] : loc= 'exists' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) ;
    public BaseDescr lhs_exist() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        BaseDescr column = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:4: (loc= 'exists' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:4: loc= 'exists' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,73,FOLLOW_73_in_lhs_exist3324); if (failed) return d;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:17: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
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
                    new NoViableAltException("1303:17: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:18: ( '(' lhs_or ')' )=> '(' column= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist3327); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist3331);
                    column=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist3333); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1303:42: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_exist3339);
                    column=lhs_column();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               
              			d = new ExistsDescr( (ColumnDescr) column ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1310:1: lhs_not returns [NotDescr d] : loc= 'not' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d = null;

        Token loc=null;
        BaseDescr column = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1314:4: (loc= 'not' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1314:4: loc= 'not' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,74,FOLLOW_74_in_lhs_not3369); if (failed) return d;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1314:14: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
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
                    new NoViableAltException("1314:14: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1314:15: ( '(' lhs_or ')' )=> '(' column= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not3372); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not3376);
                    column=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not3379); if (failed) return d;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1314:40: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_not3385);
                    column=lhs_column();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {

              			d = new NotDescr( column ); 
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1321:1: lhs_eval returns [BaseDescr d] : loc= 'eval' c= paren_chunk ;
    public BaseDescr lhs_eval() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        String c = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1325:4: (loc= 'eval' c= paren_chunk )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1325:4: loc= 'eval' c= paren_chunk
            {
            loc=(Token)input.LT(1);
            match(input,75,FOLLOW_75_in_lhs_eval3413); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval3417);
            c=paren_chunk();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
              		        String body = c.substring(1, c.length()-1);
              			checkTrailingSemicolon( body, offset(loc.getLine()) );
              			d = new EvalDescr( body ); 
              		
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


    // $ANTLR start dotted_name
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1333:1: dotted_name returns [String name] : id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '[' ']' )=> '[' ']' )* ;
    public String dotted_name() throws RecognitionException {   
        String name = null;

        Token id=null;


        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1338:3: (id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '[' ']' )=> '[' ']' )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1338:3: id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '[' ']' )=> '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name3448); if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1338:32: ( ( '.' ID )=> '.' id= ID )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);
                if ( (LA48_0==33) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1338:34: ( '.' ID )=> '.' id= ID
            	    {
            	    match(input,33,FOLLOW_33_in_dotted_name3454); if (failed) return name;
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name3458); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "." + id.getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1338:85: ( ( '[' ']' )=> '[' ']' )*
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);
                if ( (LA49_0==LEFT_SQUARE) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1338:87: ( '[' ']' )=> '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name3467); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name3469); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "[]";
            	    }

            	    }
            	    break;

            	default :
            	    break loop49;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1341:1: argument returns [String name] : id= ID ( ( '[' ']' )=> '[' ']' )* ;
    public String argument() throws RecognitionException {   
        String name = null;

        Token id=null;


        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1346:3: (id= ID ( ( '[' ']' )=> '[' ']' )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1346:3: id= ID ( ( '[' ']' )=> '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument3499); if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1346:32: ( ( '[' ']' )=> '[' ']' )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);
                if ( (LA50_0==LEFT_SQUARE) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1346:34: ( '[' ']' )=> '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument3505); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument3507); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "[]";
            	    }

            	    }
            	    break;

            	default :
            	    break loop50;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1349:1: rhs_chunk[RuleDescr rule] : start= 'then' ( (~ END )=>~ END )* END ;
    public void rhs_chunk(RuleDescr rule) throws RecognitionException {   
        Token start=null;


                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1355:10: (start= 'then' ( (~ END )=>~ END )* END )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1355:10: start= 'then' ( (~ END )=>~ END )* END
            {
            if ( backtracking==0 ) {

              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            start=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk3551); if (failed) return ;
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1361:3: ( (~ END )=>~ END )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);
                if ( ((LA51_0>=ID && LA51_0<=RIGHT_SQUARE)||(LA51_0>=THEN && LA51_0<=76)) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1362:6: (~ END )=>~ END
            	    {
            	    if ( (input.LA(1)>=ID && input.LA(1)<=RIGHT_SQUARE)||(input.LA(1)>=THEN && input.LA(1)<=76) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk3563);    throw mse;
            	    }

            	    if ( backtracking==0 ) {

            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;

            	default :
            	    break loop51;
                }
            } while (true);

            if ( backtracking==0 ) {

              		    if( channel != null ) {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, channel.intValue());
              		    } else {
              			    ((SwitchingCommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
              		    }
              		
            }
            match(input,END,FOLLOW_END_in_rhs_chunk3598); if (failed) return ;
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


    // $ANTLR start word
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1392:1: word returns [String word] : ( ( ID )=>id= ID | ( 'import' )=> 'import' | ( 'use' )=> 'use' | ( RULE )=> RULE | ( 'query' )=> 'query' | ( 'salience' )=> 'salience' | ( 'no-loop' )=> 'no-loop' | ( WHEN )=> WHEN | ( THEN )=> THEN | ( END )=> END | str= STRING );
    public String word() throws RecognitionException {   
        String word = null;

        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1396:4: ( ( ID )=>id= ID | ( 'import' )=> 'import' | ( 'use' )=> 'use' | ( RULE )=> RULE | ( 'query' )=> 'query' | ( 'salience' )=> 'salience' | ( 'no-loop' )=> 'no-loop' | ( WHEN )=> WHEN | ( THEN )=> THEN | ( END )=> END | str= STRING )
            int alt52=11;
            switch ( input.LA(1) ) {
            case ID:
                alt52=1;
                break;
            case 31:
                alt52=2;
                break;
            case 76:
                alt52=3;
                break;
            case RULE:
                alt52=4;
                break;
            case 37:
                alt52=5;
                break;
            case 44:
                alt52=6;
                break;
            case 45:
                alt52=7;
                break;
            case WHEN:
                alt52=8;
                break;
            case THEN:
                alt52=9;
                break;
            case END:
                alt52=10;
                break;
            case STRING:
                alt52=11;
                break;
            default:
                if (backtracking>0) {failed=true; return word;}
                NoViableAltException nvae =
                    new NoViableAltException("1392:1: word returns [String word] : ( ( ID )=>id= ID | ( 'import' )=> 'import' | ( 'use' )=> 'use' | ( RULE )=> RULE | ( 'query' )=> 'query' | ( 'salience' )=> 'salience' | ( 'no-loop' )=> 'no-loop' | ( WHEN )=> WHEN | ( THEN )=> THEN | ( END )=> END | str= STRING );", 52, 0, input);

                throw nvae;
            }

            switch (alt52) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1396:4: ( ID )=>id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word3640); if (failed) return word;
                    if ( backtracking==0 ) {
                       word=id.getText(); 
                    }

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1397:4: ( 'import' )=> 'import'
                    {
                    match(input,31,FOLLOW_31_in_word3652); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="import"; 
                    }

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1398:4: ( 'use' )=> 'use'
                    {
                    match(input,76,FOLLOW_76_in_word3661); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="use"; 
                    }

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1399:4: ( RULE )=> RULE
                    {
                    match(input,RULE,FOLLOW_RULE_in_word3673); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="rule"; 
                    }

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1400:4: ( 'query' )=> 'query'
                    {
                    match(input,37,FOLLOW_37_in_word3686); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="query"; 
                    }

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1401:4: ( 'salience' )=> 'salience'
                    {
                    match(input,44,FOLLOW_44_in_word3696); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="salience"; 
                    }

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1402:5: ( 'no-loop' )=> 'no-loop'
                    {
                    match(input,45,FOLLOW_45_in_word3704); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="no-loop"; 
                    }

                    }
                    break;
                case 8 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1403:4: ( WHEN )=> WHEN
                    {
                    match(input,WHEN,FOLLOW_WHEN_in_word3712); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="when"; 
                    }

                    }
                    break;
                case 9 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1404:4: ( THEN )=> THEN
                    {
                    match(input,THEN,FOLLOW_THEN_in_word3725); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="then"; 
                    }

                    }
                    break;
                case 10 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1405:4: ( END )=> END
                    {
                    match(input,END,FOLLOW_END_in_word3738); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="end"; 
                    }

                    }
                    break;
                case 11 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:1406:4: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word3754); if (failed) return word;
                    if ( backtracking==0 ) {
                       word=getString(str);
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
        return word;
    }
    // $ANTLR end word

    // $ANTLR start synpred35
    public void synpred35_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:4: ( ( ID paren_chunk ) )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:4: ( ID paren_chunk )
        {
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:4: ( ID paren_chunk )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:745:6: ID paren_chunk
        {
        match(input,ID,FOLLOW_ID_in_synpred351530); if (failed) return ;
        pushFollow(FOLLOW_paren_chunk_in_synpred351534);
        paren_chunk();
        _fsp--;
        if (failed) return ;

        }


        }
    }
    // $ANTLR end synpred35

    // $ANTLR start synpred38
    public void synpred38_fragment() throws RecognitionException {   
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:6: ( LEFT_PAREN )
        // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/DRL.g:785:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred381675); if (failed) return ;

        }
    }
    // $ANTLR end synpred38

    public boolean synpred35() {
        backtracking++;
        int start = input.mark();
        try {
            synpred35_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public boolean synpred38() {
        backtracking++;
        int start = input.mark();
        try {
            synpred38_fragment(); // can never throw exception
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
    public static final String DFA8_eotS =
        "\6\uffff";
    public static final String DFA8_eofS =
        "\6\uffff";
    public static final String DFA8_minS =
        "\2\4\1\uffff\1\20\1\uffff\1\4";
    public static final String DFA8_maxS =
        "\1\4\1\44\1\uffff\1\20\1\uffff\1\44";
    public static final String DFA8_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    public static final String DFA8_specialS =
        "\6\uffff}>";
    public static final String[] DFA8_transition = {
        "\1\1",
        "\1\2\7\uffff\1\4\2\uffff\1\3\21\uffff\1\2\2\uffff\1\4",
        "",
        "\1\5",
        "",
        "\1\2\7\uffff\1\4\2\uffff\1\3\24\uffff\1\4"
    };

    class DFA8 extends DFA {
        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA.unpackEncodedString(DFA8_eotS);
            this.eof = DFA.unpackEncodedString(DFA8_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
            this.accept = DFA.unpackEncodedString(DFA8_acceptS);
            this.special = DFA.unpackEncodedString(DFA8_specialS);
            int numStates = DFA8_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA8_transition[i]);
            }
        }
        public String getDescription() {
            return "374:6: ( ( dotted_name )=>paramType= dotted_name )?";
        }
    }
    public static final String DFA9_eotS =
        "\6\uffff";
    public static final String DFA9_eofS =
        "\6\uffff";
    public static final String DFA9_minS =
        "\2\4\1\20\2\uffff\1\4";
    public static final String DFA9_maxS =
        "\1\4\1\44\1\20\2\uffff\1\44";
    public static final String DFA9_acceptS =
        "\3\uffff\1\2\1\1\1\uffff";
    public static final String DFA9_specialS =
        "\6\uffff}>";
    public static final String[] DFA9_transition = {
        "\1\1",
        "\1\4\7\uffff\1\3\2\uffff\1\2\21\uffff\1\4\2\uffff\1\3",
        "\1\5",
        "",
        "",
        "\1\4\7\uffff\1\3\2\uffff\1\2\24\uffff\1\3"
    };

    class DFA9 extends DFA {
        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA.unpackEncodedString(DFA9_eotS);
            this.eof = DFA.unpackEncodedString(DFA9_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
            this.accept = DFA.unpackEncodedString(DFA9_acceptS);
            this.special = DFA.unpackEncodedString(DFA9_specialS);
            int numStates = DFA9_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA9_transition[i]);
            }
        }
        public String getDescription() {
            return "378:11: ( ( dotted_name )=>paramType= dotted_name )?";
        }
    }
 

    public static final BitSet FOLLOW_29_in_opt_semicolon46 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit58 = new BitSet(new long[]{0x0000006980000020L});
    public static final BitSet FOLLOW_statement_in_compilation_unit65 = new BitSet(new long[]{0x0000006980000022L});
    public static final BitSet FOLLOW_package_statement_in_prolog90 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement138 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_package_statement200 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement204 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_import_statement223 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_import_name_in_import_statement227 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_function_import_statement245 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_function_import_statement247 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement251 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name285 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_33_in_import_name291 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_import_name295 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_34_in_import_name305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_global329 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_global333 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_global337 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_global339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_function366 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_function371 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_function377 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function386 = new BitSet(new long[]{0x0000000000001010L});
    public static final BitSet FOLLOW_dotted_name_in_function396 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_argument_in_function402 = new BitSet(new long[]{0x0000001000001000L});
    public static final BitSet FOLLOW_36_in_function416 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_function421 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_argument_in_function427 = new BitSet(new long[]{0x0000001000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function451 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_curly_chunk_in_function457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_query488 = new BitSet(new long[]{0x00003020800600F0L,0x0000000000001000L});
    public static final BitSet FOLLOW_word_in_query492 = new BitSet(new long[]{0x0000000000020810L,0x0000000000000E00L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query506 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_END_in_query521 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_template547 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_template551 = new BitSet(new long[]{0x0000000020000010L});
    public static final BitSet FOLLOW_opt_semicolon_in_template553 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_template_slot_in_template568 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_END_in_template583 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot617 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_template_slot621 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule654 = new BitSet(new long[]{0x00003020800600F0L,0x0000000000001000L});
    public static final BitSet FOLLOW_word_in_rule658 = new BitSet(new long[]{0x0003FF1000040040L});
    public static final BitSet FOLLOW_rule_attributes_in_rule667 = new BitSet(new long[]{0x0000000000040040L});
    public static final BitSet FOLLOW_WHEN_in_rule676 = new BitSet(new long[]{0x0000008000040810L,0x0000000000000E00L});
    public static final BitSet FOLLOW_39_in_rule678 = new BitSet(new long[]{0x0000000000040810L,0x0000000000000E00L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule696 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule_attributes738 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_rule_attributes740 = new BitSet(new long[]{0x0003FE1000000002L});
    public static final BitSet FOLLOW_36_in_rule_attributes749 = new BitSet(new long[]{0x0003FE0000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes754 = new BitSet(new long[]{0x0003FE1000000002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute795 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute899 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_date_effective965 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_date_effective969 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_date_expires1000 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_date_expires1004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_enabled1037 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_enabled1041 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_enabled1043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_salience1085 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_INT_in_salience1089 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_salience1091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_no_loop1126 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_no_loop1128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_no_loop1153 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1157 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_no_loop1159 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_auto_focus1205 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_auto_focus1207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_auto_focus1232 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1236 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_auto_focus1238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_activation_group1280 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_activation_group1284 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_activation_group1286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_agenda_group1315 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1319 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_agenda_group1321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_duration1353 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_INT_in_duration1357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1383 = new BitSet(new long[]{0x0000000000000812L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1458 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_from_statement1483 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_from_source_in_from_statement1487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1530 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source1534 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_ID_in_from_source1567 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_expression_chain_in_from_source1590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_expression_chain1615 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_expression_chain1619 = new BitSet(new long[]{0x0000000200008802L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain1650 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain1683 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_accumulate_statement1744 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_accumulate_statement1746 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement1756 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_accumulate_statement1760 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_accumulate_statement1762 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_accumulate_statement1771 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1775 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_accumulate_statement1777 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_53_in_accumulate_statement1786 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1790 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_accumulate_statement1792 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_54_in_accumulate_statement1801 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1805 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement1807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_collect_statement1850 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_collect_statement1852 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement1862 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_collect_statement1866 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement1868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding1903 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_fact_binding1913 = new BitSet(new long[]{0x0000000000000810L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding1917 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression1949 = new BitSet(new long[]{0x0000000000000810L});
    public static final BitSet FOLLOW_fact_expression_in_paren_in_fact_expression1953 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression1956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression1967 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression_in_paren1998 = new BitSet(new long[]{0x0000000000000810L});
    public static final BitSet FOLLOW_fact_expression_in_paren_in_fact_expression_in_paren2002 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression_in_paren2004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression_in_paren2015 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_set_in_fact_expression_in_paren2028 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_fact_in_fact_expression_in_paren2045 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_fact2084 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact2097 = new BitSet(new long[]{0x0000000000001010L});
    public static final BitSet FOLLOW_constraints_in_fact2109 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact2130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints2160 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_predicate_in_constraints2163 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_constraints2171 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constraint_in_constraints2174 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_predicate_in_constraints2177 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_ID_in_constraint2206 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_constraint2208 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_constraint2218 = new BitSet(new long[]{0xF000000000000002L,0x000000000000001FL});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2234 = new BitSet(new long[]{0x0C00000000000002L});
    public static final BitSet FOLLOW_set_in_constraint2253 = new BitSet(new long[]{0xF000000000000000L,0x000000000000001FL});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2270 = new BitSet(new long[]{0x0C00000000000002L});
    public static final BitSet FOLLOW_set_in_constraint_expression2322 = new BitSet(new long[]{0x0000000000000F90L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint_expression2389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint_expression2405 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint_expression2428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint_expression2442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint2481 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint2492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_literal_constraint2528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2559 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_enum_constraint2561 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2565 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_predicate2587 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_predicate2589 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_predicate2593 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_predicate2595 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate2599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk2646 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk2662 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk2686 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk2722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk2772 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk2788 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk2812 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk2848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk2909 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_set_in_square_chunk2925 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk2949 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk2985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint3030 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3063 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or3072 = new BitSet(new long[]{0x0000000000000810L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3082 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3118 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_set_in_lhs_and3127 = new BitSet(new long[]{0x0000000000000810L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3137 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary3174 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary3182 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary3190 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary3198 = new BitSet(new long[]{0x0004000020000002L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary3218 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary3240 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary3261 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary3284 = new BitSet(new long[]{0x0000000000000810L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary3288 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary3290 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary3300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_lhs_exist3324 = new BitSet(new long[]{0x0000000000000810L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist3327 = new BitSet(new long[]{0x0000000000000810L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist3331 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist3333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3339 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_lhs_not3369 = new BitSet(new long[]{0x0000000000000810L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not3372 = new BitSet(new long[]{0x0000000000000810L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not3376 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not3379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_lhs_eval3413 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval3417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name3448 = new BitSet(new long[]{0x0000000200008002L});
    public static final BitSet FOLLOW_33_in_dotted_name3454 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_dotted_name3458 = new BitSet(new long[]{0x0000000200008002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name3467 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name3469 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ID_in_argument3499 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument3505 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument3507 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk3551 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk3563 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000001FFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk3598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_word3640 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_word3652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_word3661 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_word3673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_word3686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_word3696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_word3704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_word3712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_word3725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_END_in_word3738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word3754 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred351530 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_synpred351534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred381675 = new BitSet(new long[]{0x0000000000000002L});

}