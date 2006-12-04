// $ANTLR 3.0b5 D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g 2006-12-04 11:20:24

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "CURLY_CHUNK", "RHS", "INT", "BOOL", "STRING", "FLOAT", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_SQUARE", "RIGHT_SQUARE", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "NO_CURLY", "MULTI_LINE_COMMENT", "IGNORE", "';'", "'package'", "'import'", "'function'", "'.'", "'.*'", "'global'", "','", "'query'", "'end'", "'template'", "'rule'", "'when'", "':'", "'attributes'", "'salience'", "'no-loop'", "'auto-focus'", "'activation-group'", "'agenda-group'", "'duration'", "'from'", "'accumulate'", "'init'", "'action'", "'result'", "'collect'", "'or'", "'||'", "'&'", "'|'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='", "'contains'", "'matches'", "'excludes'", "'null'", "'->'", "'and'", "'&&'", "'exists'", "'not'", "'eval'", "'use'"
    };
    public static final int LEFT_PAREN=11;
    public static final int BOOL=8;
    public static final int HexDigit=18;
    public static final int CURLY_CHUNK=5;
    public static final int WS=16;
    public static final int STRING=9;
    public static final int FLOAT=10;
    public static final int NO_CURLY=23;
    public static final int UnicodeEscape=19;
    public static final int EscapeSequence=17;
    public static final int INT=7;
    public static final int EOF=-1;
    public static final int IGNORE=25;
    public static final int RHS=6;
    public static final int EOL=15;
    public static final int LEFT_SQUARE=13;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=21;
    public static final int OctalEscape=20;
    public static final int MULTI_LINE_COMMENT=24;
    public static final int RIGHT_PAREN=12;
    public static final int RIGHT_SQUARE=14;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=22;
    public static final int ID=4;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[135+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g"; }

    
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:282:1: opt_semicolon : ( ( ';' )=> ';' )? ;
    public void opt_semicolon() throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:283:4: ( ( ( ';' )=> ';' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:283:4: ( ( ';' )=> ';' )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:283:4: ( ( ';' )=> ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);
            if ( (LA1_0==26) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ';' )=> ';'
                    {
                    match(input,26,FOLLOW_26_in_opt_semicolon46); if (failed) return ;

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:286:1: compilation_unit : prolog ( ( statement )=> statement )* ;
    public void compilation_unit() throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:287:4: ( prolog ( ( statement )=> statement )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:287:4: prolog ( ( statement )=> statement )*
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit58);
            prolog();
            _fsp--;
            if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:288:3: ( ( statement )=> statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( ((LA2_0>=28 && LA2_0<=29)||LA2_0==32||LA2_0==34||(LA2_0>=36 && LA2_0<=37)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:288:5: ( statement )=> statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit65);
            	    statement();
            	    _fsp--;
            	    if (failed) return ;

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
        return ;
    }
    // $ANTLR end compilation_unit


    // $ANTLR start prolog
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:291:1: prolog : ( ( package_statement )=>name= package_statement )? ;
    public void prolog() throws RecognitionException {   
        String name = null;


        
        		String packageName = "";
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:295:4: ( ( ( package_statement )=>name= package_statement )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:295:4: ( ( package_statement )=>name= package_statement )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:295:4: ( ( package_statement )=>name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0==27) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:295:6: ( package_statement )=>name= package_statement
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:301:1: statement : ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) ;
    public void statement() throws RecognitionException {   
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:303:2: ( ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:303:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:303:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
            int alt4=7;
            switch ( input.LA(1) ) {
            case 28:
                int LA4_1 = input.LA(2);
                if ( (LA4_1==29) ) {
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
            case 32:
                alt4=3;
                break;
            case 29:
                alt4=4;
                break;
            case 36:
                alt4=5;
                break;
            case 37:
                alt4=6;
                break;
            case 34:
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:303:4: ( import_statement )=> import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement114);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:304:10: ( function_import_statement )=> function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement125);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:305:4: ( global )=> global
                    {
                    pushFollow(FOLLOW_global_in_statement130);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:306:4: ( function )=> function
                    {
                    pushFollow(FOLLOW_function_in_statement135);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:10: ( template )=>t= template
                    {
                    pushFollow(FOLLOW_template_in_statement148);
                    t=template();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      this.packageDescr.addFactTemplate( t ); 
                    }

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:308:4: ( rule )=>r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement157);
                    r=rule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      this.packageDescr.addRule( r ); 
                    }

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:309:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement167);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:313:1: package_statement returns [String packageName] : 'package' name= dotted_name opt_semicolon ;
    public String package_statement() throws RecognitionException {   
        String packageName = null;

        String name = null;


        
        		packageName = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:318:3: ( 'package' name= dotted_name opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:318:3: 'package' name= dotted_name opt_semicolon
            {
            match(input,27,FOLLOW_27_in_package_statement195); if (failed) return packageName;
            pushFollow(FOLLOW_dotted_name_in_package_statement199);
            name=dotted_name();
            _fsp--;
            if (failed) return packageName;
            pushFollow(FOLLOW_opt_semicolon_in_package_statement201);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:325:1: import_statement : 'import' name= import_name opt_semicolon ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:326:4: ( 'import' name= import_name opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:326:4: 'import' name= import_name opt_semicolon
            {
            match(input,28,FOLLOW_28_in_import_statement218); if (failed) return ;
            pushFollow(FOLLOW_import_name_in_import_statement222);
            name=import_name();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_import_statement224);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:333:1: function_import_statement : 'import' 'function' name= import_name opt_semicolon ;
    public void function_import_statement() throws RecognitionException {   
        String name = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:334:4: ( 'import' 'function' name= import_name opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:334:4: 'import' 'function' name= import_name opt_semicolon
            {
            match(input,28,FOLLOW_28_in_function_import_statement240); if (failed) return ;
            match(input,29,FOLLOW_29_in_function_import_statement242); if (failed) return ;
            pushFollow(FOLLOW_import_name_in_function_import_statement246);
            name=import_name();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement248);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:342:1: import_name returns [String name] : id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '.*' )=>star= '.*' )? ;
    public String import_name() throws RecognitionException {   
        String name = null;

        Token id=null;
        Token star=null;

        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:347:3: (id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '.*' )=>star= '.*' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:347:3: id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '.*' )=>star= '.*' )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name280); if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:347:32: ( ( '.' ID )=> '.' id= ID )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);
                if ( (LA5_0==30) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:347:34: ( '.' ID )=> '.' id= ID
            	    {
            	    match(input,30,FOLLOW_30_in_import_name286); if (failed) return name;
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_import_name290); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "." + id.getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:347:85: ( ( '.*' )=>star= '.*' )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0==31) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:347:86: ( '.*' )=>star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,31,FOLLOW_31_in_import_name300); if (failed) return name;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:351:1: global : 'global' type= dotted_name id= ID opt_semicolon ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;


        
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:355:3: ( 'global' type= dotted_name id= ID opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:355:3: 'global' type= dotted_name id= ID opt_semicolon
            {
            match(input,32,FOLLOW_32_in_global324); if (failed) return ;
            pushFollow(FOLLOW_dotted_name_in_global328);
            type=dotted_name();
            _fsp--;
            if (failed) return ;
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global332); if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_global334);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:362:1: function : loc= 'function' ( ( dotted_name )=>retType= dotted_name )? name= ID '(' ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )? ')' body= CURLY_CHUNK ;
    public void function() throws RecognitionException {   
        Token loc=null;
        Token name=null;
        Token body=null;
        String retType = null;

        String paramType = null;

        String paramName = null;


        
        		FunctionDescr f = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:367:3: (loc= 'function' ( ( dotted_name )=>retType= dotted_name )? name= ID '(' ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )? ')' body= CURLY_CHUNK )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:367:3: loc= 'function' ( ( dotted_name )=>retType= dotted_name )? name= ID '(' ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )? ')' body= CURLY_CHUNK
            {
            loc=(Token)input.LT(1);
            match(input,29,FOLLOW_29_in_function361); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:367:18: ( ( dotted_name )=>retType= dotted_name )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( (LA7_0==ID) ) {
                int LA7_1 = input.LA(2);
                if ( (LA7_1==ID||LA7_1==LEFT_SQUARE||LA7_1==30) ) {
                    alt7=1;
                }
            }
            switch (alt7) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:367:19: ( dotted_name )=>retType= dotted_name
                    {
                    pushFollow(FOLLOW_dotted_name_in_function366);
                    retType=dotted_name();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function372); if (failed) return ;
            if ( backtracking==0 ) {
              
              			//System.err.println( "function :: " + name.getText() );
              			f = new FunctionDescr( name.getText(), retType );
              			f.setLocation(offset(loc.getLine()), loc.getCharPositionInLine());
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function381); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:374:4: ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( (LA11_0==ID) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:374:6: ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )*
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:374:6: ( ( dotted_name )=>paramType= dotted_name )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:374:7: ( dotted_name )=>paramType= dotted_name
                            {
                            pushFollow(FOLLOW_dotted_name_in_function391);
                            paramType=dotted_name();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function397);
                    paramName=argument();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      					f.addParameter( paramType, paramName );
                      				
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:378:5: ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);
                        if ( (LA10_0==33) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:378:7: ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument
                    	    {
                    	    match(input,33,FOLLOW_33_in_function411); if (failed) return ;
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:378:11: ( ( dotted_name )=>paramType= dotted_name )?
                    	    int alt9=2;
                    	    alt9 = dfa9.predict(input);
                    	    switch (alt9) {
                    	        case 1 :
                    	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:378:12: ( dotted_name )=>paramType= dotted_name
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function416);
                    	            paramType=dotted_name();
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function422);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function446); if (failed) return ;
            body=(Token)input.LT(1);
            match(input,CURLY_CHUNK,FOLLOW_CURLY_CHUNK_in_function452); if (failed) return ;
            if ( backtracking==0 ) {
              
              			//strip out '{','}'
              			String bodys = body.getText();
              			bodys = bodys.substring(1,bodys.length()-1);
              			f.setText( bodys );
              
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:398:1: query returns [QueryDescr query] : loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end' ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query = null;

        Token loc=null;
        String queryName = null;


        
        		query = null;
        		AndDescr lhs = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:404:3: (loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:404:3: loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end'
            {
            loc=(Token)input.LT(1);
            match(input,34,FOLLOW_34_in_query483); if (failed) return query;
            pushFollow(FOLLOW_word_in_query487);
            queryName=word();
            _fsp--;
            if (failed) return query;
            if ( backtracking==0 ) {
               
              			query = new QueryDescr( queryName, null ); 
              			query.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			lhs = new AndDescr(); query.setLhs( lhs ); 
              			lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:3: ( normal_lhs_block[lhs] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:413:4: normal_lhs_block[lhs]
            {
            pushFollow(FOLLOW_normal_lhs_block_in_query501);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;

            }

            match(input,35,FOLLOW_35_in_query516); if (failed) return query;

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:420:1: template returns [FactTemplateDescr template] : loc= 'template' templateName= ID opt_semicolon ( ( template_slot )=>slot= template_slot )+ 'end' opt_semicolon ;
    public FactTemplateDescr template() throws RecognitionException {   
        FactTemplateDescr template = null;

        Token loc=null;
        Token templateName=null;
        FieldTemplateDescr slot = null;


        
        		template = null;		
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:425:3: (loc= 'template' templateName= ID opt_semicolon ( ( template_slot )=>slot= template_slot )+ 'end' opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:425:3: loc= 'template' templateName= ID opt_semicolon ( ( template_slot )=>slot= template_slot )+ 'end' opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,36,FOLLOW_36_in_template542); if (failed) return template;
            templateName=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template546); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template548);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {
              
              			template = new FactTemplateDescr(templateName.getText());
              			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:430:3: ( ( template_slot )=>slot= template_slot )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:431:4: ( template_slot )=>slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template563);
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

            match(input,35,FOLLOW_35_in_template578); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template580);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:439:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name name= ID opt_semicolon ;
    public FieldTemplateDescr template_slot() throws RecognitionException {   
        FieldTemplateDescr field = null;

        Token name=null;
        String fieldType = null;


        
        		field = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:4: (fieldType= dotted_name name= ID opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:445:4: fieldType= dotted_name name= ID opt_semicolon
            {
            pushFollow(FOLLOW_dotted_name_in_template_slot612);
            fieldType=dotted_name();
            _fsp--;
            if (failed) return field;
            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template_slot616); if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot618);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:454:1: rule returns [RuleDescr rule] : loc= 'rule' ruleName= word rule_attributes[rule] ( ( 'when' ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= 'when' ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs= RHS ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule = null;

        Token loc=null;
        Token rhs=null;
        String ruleName = null;


        
        		rule = null;
        		String consequence = "";
        		AndDescr lhs = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:461:3: (loc= 'rule' ruleName= word rule_attributes[rule] ( ( 'when' ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= 'when' ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs= RHS )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:461:3: loc= 'rule' ruleName= word rule_attributes[rule] ( ( 'when' ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= 'when' ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs= RHS
            {
            loc=(Token)input.LT(1);
            match(input,37,FOLLOW_37_in_rule649); if (failed) return rule;
            pushFollow(FOLLOW_word_in_rule653);
            ruleName=word();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               
              			debug( "start rule: " + ruleName );
              			rule = new RuleDescr( ruleName, null ); 
              			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            pushFollow(FOLLOW_rule_attributes_in_rule662);
            rule_attributes(rule);
            _fsp--;
            if (failed) return rule;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:468:3: ( ( 'when' ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= 'when' ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0==38) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:468:5: ( 'when' ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= 'when' ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_rule671); if (failed) return rule;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:468:16: ( ( ':' )=> ':' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    if ( (LA13_0==39) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ':' )=> ':'
                            {
                            match(input,39,FOLLOW_39_in_rule673); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				lhs = new AndDescr(); rule.setLhs( lhs ); 
                      				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      			
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:473:4: ( normal_lhs_block[lhs] )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:475:5: normal_lhs_block[lhs]
                    {
                    pushFollow(FOLLOW_normal_lhs_block_in_rule691);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }


                    }
                    break;

            }

            rhs=(Token)input.LT(1);
            match(input,RHS,FOLLOW_RHS_in_rule714); if (failed) return rule;
            if ( backtracking==0 ) {
              
              				consequence = rhs.getText();
              				//strip out "then", "end"
              				consequence = consequence.substring(4,consequence.length()-3);
              				
              				if ( expander != null ) {
              					String expanded = runThenExpander( consequence, offset(rhs.getLine()) );
              					rule.setConsequence( expanded );
              				} else { 
              					rule.setConsequence( consequence ); 
              				}
              				rule.setConsequenceLocation(offset(rhs.getLine()), rhs.getCharPositionInLine());
              				debug( "end rule: " + ruleName );
              			
            }

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:498:1: rule_attributes[RuleDescr rule] : ( ( 'attributes' ':' )=> 'attributes' ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:500:4: ( ( ( 'attributes' ':' )=> 'attributes' ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:500:4: ( ( 'attributes' ':' )=> 'attributes' ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:500:4: ( ( 'attributes' ':' )=> 'attributes' ':' )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( (LA15_0==40) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:500:5: ( 'attributes' ':' )=> 'attributes' ':'
                    {
                    match(input,40,FOLLOW_40_in_rule_attributes739); if (failed) return ;
                    match(input,39,FOLLOW_39_in_rule_attributes741); if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:501:4: ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                if ( (LA17_0==33||(LA17_0>=41 && LA17_0<=46)) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:501:6: ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute
            	    {
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:501:6: ( ( ',' )=> ',' )?
            	    int alt16=2;
            	    int LA16_0 = input.LA(1);
            	    if ( (LA16_0==33) ) {
            	        alt16=1;
            	    }
            	    switch (alt16) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ',' )=> ','
            	            {
            	            match(input,33,FOLLOW_33_in_rule_attributes750); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes755);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:510:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | a= auto_focus );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d = null;

        AttributeDescr a = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:515:4: ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | a= auto_focus )
            int alt18=6;
            switch ( input.LA(1) ) {
            case 41:
                alt18=1;
                break;
            case 42:
                alt18=2;
                break;
            case 45:
                alt18=3;
                break;
            case 46:
                alt18=4;
                break;
            case 44:
                alt18=5;
                break;
            case 43:
                alt18=6;
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("510:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | a= auto_focus );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:515:4: ( salience )=>a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute796);
                    a=salience();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:516:5: ( no_loop )=>a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute806);
                    a=no_loop();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:517:5: ( agenda_group )=>a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute817);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:518:5: ( duration )=>a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute830);
                    a=duration();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:519:5: ( activation_group )=>a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute844);
                    a=activation_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:520:5: a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute855);
                    a=auto_focus();
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


    // $ANTLR start salience
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:525:1: salience returns [AttributeDescr d ] : loc= 'salience' i= INT opt_semicolon ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:530:3: (loc= 'salience' i= INT opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:530:3: loc= 'salience' i= INT opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,41,FOLLOW_41_in_salience889); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience893); if (failed) return d;
            pushFollow(FOLLOW_opt_semicolon_in_salience895);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:537:1: no_loop returns [AttributeDescr d] : ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:542:3: ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) )
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( (LA19_0==42) ) {
                int LA19_1 = input.LA(2);
                if ( (LA19_1==BOOL) ) {
                    alt19=2;
                }
                else if ( (LA19_1==EOF||LA19_1==RHS||LA19_1==26||LA19_1==33||LA19_1==38||(LA19_1>=41 && LA19_1<=46)) ) {
                    alt19=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("537:1: no_loop returns [AttributeDescr d] : ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );", 19, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("537:1: no_loop returns [AttributeDescr d] : ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:542:3: ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:542:3: (loc= 'no-loop' opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:543:4: loc= 'no-loop' opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,42,FOLLOW_42_in_no_loop930); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_no_loop932);
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:550:3: (loc= 'no-loop' t= BOOL opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:550:3: (loc= 'no-loop' t= BOOL opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:551:4: loc= 'no-loop' t= BOOL opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,42,FOLLOW_42_in_no_loop957); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop961); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_no_loop963);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:561:1: auto_focus returns [AttributeDescr d] : ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );
    public AttributeDescr auto_focus() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:566:3: ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) )
            int alt20=2;
            int LA20_0 = input.LA(1);
            if ( (LA20_0==43) ) {
                int LA20_1 = input.LA(2);
                if ( (LA20_1==BOOL) ) {
                    alt20=2;
                }
                else if ( (LA20_1==EOF||LA20_1==RHS||LA20_1==26||LA20_1==33||LA20_1==38||(LA20_1>=41 && LA20_1<=46)) ) {
                    alt20=1;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("561:1: auto_focus returns [AttributeDescr d] : ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );", 20, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("561:1: auto_focus returns [AttributeDescr d] : ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:566:3: ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:566:3: (loc= 'auto-focus' opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:567:4: loc= 'auto-focus' opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_auto_focus1009); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_auto_focus1011);
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:574:3: (loc= 'auto-focus' t= BOOL opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:574:3: (loc= 'auto-focus' t= BOOL opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:575:4: loc= 'auto-focus' t= BOOL opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_auto_focus1036); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1040); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_auto_focus1042);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:585:1: activation_group returns [AttributeDescr d] : loc= 'activation-group' name= STRING opt_semicolon ;
    public AttributeDescr activation_group() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:590:3: (loc= 'activation-group' name= STRING opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:590:3: loc= 'activation-group' name= STRING opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,44,FOLLOW_44_in_activation_group1084); if (failed) return d;
            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1088); if (failed) return d;
            pushFollow(FOLLOW_opt_semicolon_in_activation_group1090);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:597:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' name= STRING opt_semicolon ;
    public AttributeDescr agenda_group() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:602:3: (loc= 'agenda-group' name= STRING opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:602:3: loc= 'agenda-group' name= STRING opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,45,FOLLOW_45_in_agenda_group1119); if (failed) return d;
            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1123); if (failed) return d;
            pushFollow(FOLLOW_opt_semicolon_in_agenda_group1125);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:610:1: duration returns [AttributeDescr d] : loc= 'duration' i= INT ;
    public AttributeDescr duration() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:615:3: (loc= 'duration' i= INT )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:615:3: loc= 'duration' i= INT
            {
            loc=(Token)input.LT(1);
            match(input,46,FOLLOW_46_in_duration1157); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1161); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:623:1: normal_lhs_block[AndDescr descrs] : ( ( lhs )=>d= lhs )* ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        BaseDescr d = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:625:3: ( ( ( lhs )=>d= lhs )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:625:3: ( ( lhs )=>d= lhs )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:625:3: ( ( lhs )=>d= lhs )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);
                if ( (LA21_0==ID||LA21_0==LEFT_PAREN||(LA21_0>=70 && LA21_0<=72)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:625:5: ( lhs )=>d= lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1187);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:1: lhs returns [BaseDescr d] : l= lhs_or ;
    public BaseDescr lhs() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr l = null;


        
        		d=null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:683:4: (l= lhs_or )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:683:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1225);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:687:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );
    public BaseDescr lhs_column() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr f = null;


        
        		d=null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:691:4: ( ( fact_binding )=>f= fact_binding | f= fact )
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( (LA22_0==ID) ) {
                int LA22_1 = input.LA(2);
                if ( (LA22_1==39) ) {
                    alt22=1;
                }
                else if ( (LA22_1==LEFT_PAREN||LA22_1==LEFT_SQUARE||LA22_1==30) ) {
                    alt22=2;
                }
                else {
                    if (backtracking>0) {failed=true; return d;}
                    NoViableAltException nvae =
                        new NoViableAltException("687:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );", 22, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("687:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:691:4: ( fact_binding )=>f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_column1253);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = f; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:692:4: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_column1262);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:695:1: from_statement returns [FromDescr d] : 'from' ds= from_source ;
    public FromDescr from_statement() throws RecognitionException {   
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;


        
        		d=factory.createFrom();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:700:2: ( 'from' ds= from_source )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:700:2: 'from' ds= from_source
            {
            match(input,47,FOLLOW_47_in_from_statement1287); if (failed) return d;
            pushFollow(FOLLOW_from_source_in_from_statement1291);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:710:1: from_source returns [DeclarativeInvokerDescr ds] : ( ( ( ( ID LEFT_PAREN )=> ID paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? ) )=> ( ( ID LEFT_PAREN )=>functionName= ID args= paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? ) | (var= ID ( ( expression_chain[ad] )=> expression_chain[ad] )? ) );
    public DeclarativeInvokerDescr from_source() throws RecognitionException {   
        DeclarativeInvokerDescr ds = null;

        Token functionName=null;
        Token var=null;
        String args = null;


        
        		ds = null;
        		AccessorDescr ad = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:716:3: ( ( ( ( ID LEFT_PAREN )=> ID paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? ) )=> ( ( ID LEFT_PAREN )=>functionName= ID args= paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? ) | (var= ID ( ( expression_chain[ad] )=> expression_chain[ad] )? ) )
            int alt25=2;
            int LA25_0 = input.LA(1);
            if ( (LA25_0==ID) ) {
                if ( (synpred34()) ) {
                    alt25=1;
                }
                else if ( (true) ) {
                    alt25=2;
                }
                else {
                    if (backtracking>0) {failed=true; return ds;}
                    NoViableAltException nvae =
                        new NoViableAltException("710:1: from_source returns [DeclarativeInvokerDescr ds] : ( ( ( ( ID LEFT_PAREN )=> ID paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? ) )=> ( ( ID LEFT_PAREN )=>functionName= ID args= paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? ) | (var= ID ( ( expression_chain[ad] )=> expression_chain[ad] )? ) );", 25, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ds;}
                NoViableAltException nvae =
                    new NoViableAltException("710:1: from_source returns [DeclarativeInvokerDescr ds] : ( ( ( ( ID LEFT_PAREN )=> ID paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? ) )=> ( ( ID LEFT_PAREN )=>functionName= ID args= paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? ) | (var= ID ( ( expression_chain[ad] )=> expression_chain[ad] )? ) );", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:716:3: ( ( ( ID LEFT_PAREN )=> ID paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? ) )=> ( ( ID LEFT_PAREN )=>functionName= ID args= paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:716:3: ( ( ID LEFT_PAREN )=>functionName= ID args= paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:716:5: ( ID LEFT_PAREN )=>functionName= ID args= paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )?
                    {
                    functionName=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1343); if (failed) return ds;
                    pushFollow(FOLLOW_paren_chunk_in_from_source1347);
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:726:6: ( ( expression_chain[ad] )=> expression_chain[ad] )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    if ( (LA23_0==30) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[ad] )=> expression_chain[ad]
                            {
                            pushFollow(FOLLOW_expression_chain_in_from_source1369);
                            expression_chain(ad);
                            _fsp--;
                            if (failed) return ds;

                            }
                            break;

                    }


                    }


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:729:3: (var= ID ( ( expression_chain[ad] )=> expression_chain[ad] )? )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:729:3: (var= ID ( ( expression_chain[ad] )=> expression_chain[ad] )? )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:729:7: var= ID ( ( expression_chain[ad] )=> expression_chain[ad] )?
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1389); if (failed) return ds;
                    if ( backtracking==0 ) {
                      
                      			ad = new AccessorDescr(var.getText());	
                      			ad.setLocation( offset(var.getLine()), var.getCharPositionInLine() );
                      			ds = ad;
                      		    
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:735:7: ( ( expression_chain[ad] )=> expression_chain[ad] )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);
                    if ( (LA24_0==30) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[ad] )=> expression_chain[ad]
                            {
                            pushFollow(FOLLOW_expression_chain_in_from_source1406);
                            expression_chain(ad);
                            _fsp--;
                            if (failed) return ds;

                            }
                            break;

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
        return ds;
    }
    // $ANTLR end from_source


    // $ANTLR start expression_chain
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:739:1: expression_chain[AccessorDescr as] : ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? ) ;
    public void expression_chain(AccessorDescr as) throws RecognitionException {   
        Token field=null;
        String sqarg = null;

        String paarg = null;


        
          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:745:2: ( ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:745:2: ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:745:2: ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:745:4: '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )?
            {
            match(input,30,FOLLOW_30_in_expression_chain1437); if (failed) return ;
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_expression_chain1441); if (failed) return ;
            if ( backtracking==0 ) {
              
              	        fa = new FieldAccessDescr(field.getText());	
              		fa.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
              	    
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:750:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?
            int alt26=3;
            int LA26_0 = input.LA(1);
            if ( (LA26_0==LEFT_SQUARE) ) {
                alt26=1;
            }
            else if ( (LA26_0==LEFT_PAREN) ) {
                if ( (synpred37()) ) {
                    alt26=2;
                }
            }
            switch (alt26) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:751:6: ( LEFT_SQUARE )=>sqarg= square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain1472);
                    sqarg=square_chunk();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      	          fa.setArgument( sqarg );	
                      	      
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:756:6: ( LEFT_PAREN )=>paarg= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain1505);
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:769:4: ( ( expression_chain[as] )=> expression_chain[as] )?
            int alt27=2;
            int LA27_0 = input.LA(1);
            if ( (LA27_0==30) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[as] )=> expression_chain[as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1525);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:773:1: accumulate_statement returns [AccumulateDescr d] : loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')' ;
    public AccumulateDescr accumulate_statement() throws RecognitionException {   
        AccumulateDescr d = null;

        Token loc=null;
        BaseDescr column = null;

        String text = null;


        
        		d = factory.createAccumulate();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:778:10: (loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:778:10: loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')'
            {
            loc=(Token)input.LT(1);
            match(input,47,FOLLOW_47_in_accumulate_statement1566); if (failed) return d;
            match(input,48,FOLLOW_48_in_accumulate_statement1568); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement1578); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_accumulate_statement1582);
            column=lhs_column();
            _fsp--;
            if (failed) return d;
            match(input,33,FOLLOW_33_in_accumulate_statement1584); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setSourceColumn( (ColumnDescr)column );
              		
            }
            match(input,49,FOLLOW_49_in_accumulate_statement1593); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1597);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            match(input,33,FOLLOW_33_in_accumulate_statement1599); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setInitCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,50,FOLLOW_50_in_accumulate_statement1608); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1612);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            match(input,33,FOLLOW_33_in_accumulate_statement1614); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setActionCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,51,FOLLOW_51_in_accumulate_statement1623); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1627);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement1629); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:800:1: collect_statement returns [CollectDescr d] : loc= 'from' 'collect' '(' column= lhs_column ')' ;
    public CollectDescr collect_statement() throws RecognitionException {   
        CollectDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = factory.createCollect();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:805:10: (loc= 'from' 'collect' '(' column= lhs_column ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:805:10: loc= 'from' 'collect' '(' column= lhs_column ')'
            {
            loc=(Token)input.LT(1);
            match(input,47,FOLLOW_47_in_collect_statement1672); if (failed) return d;
            match(input,52,FOLLOW_52_in_collect_statement1674); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement1684); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_collect_statement1688);
            column=lhs_column();
            _fsp--;
            if (failed) return d;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement1690); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:886:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public BaseDescr fact_binding() throws RecognitionException {   
        BaseDescr d = null;

        Token id=null;
        BaseDescr fe = null;


        
        		d=null;
        		boolean multi=false;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:892:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:892:4: id= ID ':' fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding1725); if (failed) return d;
            match(input,39,FOLLOW_39_in_fact_binding1735); if (failed) return d;
            pushFollow(FOLLOW_fact_expression_in_fact_binding1739);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:900:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact );
    public BaseDescr fact_expression(String id) throws RecognitionException {   
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:905:5: ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact )
            int alt28=2;
            int LA28_0 = input.LA(1);
            if ( (LA28_0==LEFT_PAREN) ) {
                alt28=1;
            }
            else if ( (LA28_0==ID) ) {
                alt28=2;
            }
            else {
                if (backtracking>0) {failed=true; return pd;}
                NoViableAltException nvae =
                    new NoViableAltException("900:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact );", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:905:5: ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression1771); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_paren_in_fact_expression1775);
                    fe=fact_expression_in_paren(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression1778); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:906:6: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression1789);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:914:2: fact_expression_in_paren[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )* );
    public BaseDescr fact_expression_in_paren(String id) throws RecognitionException {   
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:919:5: ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )* )
            int alt30=2;
            int LA30_0 = input.LA(1);
            if ( (LA30_0==LEFT_PAREN) ) {
                alt30=1;
            }
            else if ( (LA30_0==ID) ) {
                alt30=2;
            }
            else {
                if (backtracking>0) {failed=true; return pd;}
                NoViableAltException nvae =
                    new NoViableAltException("914:2: fact_expression_in_paren[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )* );", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:919:5: ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression_in_paren1820); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_paren_in_fact_expression_in_paren1824);
                    fe=fact_expression_in_paren(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression_in_paren1826); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:920:6: f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )*
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression_in_paren1837);
                    f=fact();
                    _fsp--;
                    if (failed) return pd;
                    if ( backtracking==0 ) {
                      
                       			((ColumnDescr)f).setIdentifier( id );
                       			pd = f;
                       		
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:925:4: ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )*
                    loop29:
                    do {
                        int alt29=2;
                        int LA29_0 = input.LA(1);
                        if ( ((LA29_0>=53 && LA29_0<=54)) ) {
                            alt29=1;
                        }


                        switch (alt29) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:925:6: ( ('or'|'||') fact )=> ('or'|'||')f= fact
                    	    {
                    	    if ( (input.LA(1)>=53 && input.LA(1)<=54) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return pd;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression_in_paren1850);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      	if ( ! multi ) {
                    	       					BaseDescr first = pd;
                    	       					pd = new OrDescr();
                    	       					((OrDescr)pd).addDescr( first );
                    	       					multi=true;
                    	       				}
                    	       			
                    	    }
                    	    pushFollow(FOLLOW_fact_in_fact_expression_in_paren1867);
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
                    	    break loop29;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:941:1: fact returns [BaseDescr d] : id= dotted_name loc= '(' ( ( constraints )=>c= constraints )? endLoc= ')' ;
    public BaseDescr fact() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        Token endLoc=null;
        String id = null;

        List c = null;


        
        		d=null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:945:5: (id= dotted_name loc= '(' ( ( constraints )=>c= constraints )? endLoc= ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:945:5: id= dotted_name loc= '(' ( ( constraints )=>c= constraints )? endLoc= ')'
            {
            pushFollow(FOLLOW_dotted_name_in_fact1906);
            id=dotted_name();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               			d = new ColumnDescr( id ); 
               		
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact1919); if (failed) return d;
            if ( backtracking==0 ) {
              
               				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
               			
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:952:4: ( ( constraints )=>c= constraints )?
            int alt31=2;
            int LA31_0 = input.LA(1);
            if ( (LA31_0==ID) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:952:6: ( constraints )=>c= constraints
                    {
                    pushFollow(FOLLOW_constraints_in_fact1931);
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
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact1952); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:966:1: constraints returns [List constraints] : ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) ( ( ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )=> ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )* ;
    public List constraints() throws RecognitionException {   
        List constraints = null;

        
        		constraints = new ArrayList();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:970:4: ( ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) ( ( ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )=> ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:970:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) ( ( ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )=> ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:970:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )
            int alt32=2;
            int LA32_0 = input.LA(1);
            if ( (LA32_0==ID) ) {
                int LA32_1 = input.LA(2);
                if ( (LA32_1==39) ) {
                    int LA32_2 = input.LA(3);
                    if ( (LA32_2==ID) ) {
                        int LA32_4 = input.LA(4);
                        if ( (LA32_4==67) ) {
                            alt32=2;
                        }
                        else if ( (LA32_4==EOF||LA32_4==RIGHT_PAREN||LA32_4==33||(LA32_4>=57 && LA32_4<=65)) ) {
                            alt32=1;
                        }
                        else {
                            if (backtracking>0) {failed=true; return constraints;}
                            NoViableAltException nvae =
                                new NoViableAltException("970:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 32, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        if (backtracking>0) {failed=true; return constraints;}
                        NoViableAltException nvae =
                            new NoViableAltException("970:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 32, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA32_1==EOF||LA32_1==RIGHT_PAREN||LA32_1==33||(LA32_1>=57 && LA32_1<=65)) ) {
                    alt32=1;
                }
                else {
                    if (backtracking>0) {failed=true; return constraints;}
                    NoViableAltException nvae =
                        new NoViableAltException("970:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 32, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return constraints;}
                NoViableAltException nvae =
                    new NoViableAltException("970:4: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 32, 0, input);

                throw nvae;
            }
            switch (alt32) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:970:5: ( constraint[constraints] )=> constraint[constraints]
                    {
                    pushFollow(FOLLOW_constraint_in_constraints1982);
                    constraint(constraints);
                    _fsp--;
                    if (failed) return constraints;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:970:29: predicate[constraints]
                    {
                    pushFollow(FOLLOW_predicate_in_constraints1985);
                    predicate(constraints);
                    _fsp--;
                    if (failed) return constraints;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:971:3: ( ( ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )=> ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);
                if ( (LA34_0==33) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:971:5: ( ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] ) )=> ',' ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )
            	    {
            	    match(input,33,FOLLOW_33_in_constraints1993); if (failed) return constraints;
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:971:9: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )
            	    int alt33=2;
            	    int LA33_0 = input.LA(1);
            	    if ( (LA33_0==ID) ) {
            	        int LA33_1 = input.LA(2);
            	        if ( (LA33_1==39) ) {
            	            int LA33_2 = input.LA(3);
            	            if ( (LA33_2==ID) ) {
            	                int LA33_4 = input.LA(4);
            	                if ( (LA33_4==67) ) {
            	                    alt33=2;
            	                }
            	                else if ( (LA33_4==EOF||LA33_4==RIGHT_PAREN||LA33_4==33||(LA33_4>=57 && LA33_4<=65)) ) {
            	                    alt33=1;
            	                }
            	                else {
            	                    if (backtracking>0) {failed=true; return constraints;}
            	                    NoViableAltException nvae =
            	                        new NoViableAltException("971:9: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 33, 4, input);

            	                    throw nvae;
            	                }
            	            }
            	            else {
            	                if (backtracking>0) {failed=true; return constraints;}
            	                NoViableAltException nvae =
            	                    new NoViableAltException("971:9: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 33, 2, input);

            	                throw nvae;
            	            }
            	        }
            	        else if ( (LA33_1==EOF||LA33_1==RIGHT_PAREN||LA33_1==33||(LA33_1>=57 && LA33_1<=65)) ) {
            	            alt33=1;
            	        }
            	        else {
            	            if (backtracking>0) {failed=true; return constraints;}
            	            NoViableAltException nvae =
            	                new NoViableAltException("971:9: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 33, 1, input);

            	            throw nvae;
            	        }
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return constraints;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("971:9: ( ( constraint[constraints] )=> constraint[constraints] | predicate[constraints] )", 33, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt33) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:971:10: ( constraint[constraints] )=> constraint[constraints]
            	            {
            	            pushFollow(FOLLOW_constraint_in_constraints1996);
            	            constraint(constraints);
            	            _fsp--;
            	            if (failed) return constraints;

            	            }
            	            break;
            	        case 2 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:971:34: predicate[constraints]
            	            {
            	            pushFollow(FOLLOW_predicate_in_constraints1999);
            	            predicate(constraints);
            	            _fsp--;
            	            if (failed) return constraints;

            	            }
            	            break;

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
        return constraints;
    }
    // $ANTLR end constraints


    // $ANTLR start constraint
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:974:1: constraint[List constraints] : ( ( ID ':' )=>fb= ID ':' )? f= ID ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* )=>rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )? ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token con=null;
        RestrictionDescr rd = null;


        
        		BaseDescr d = null;
        		FieldConstraintDescr fc = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:980:3: ( ( ( ID ':' )=>fb= ID ':' )? f= ID ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* )=>rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:980:3: ( ( ID ':' )=>fb= ID ':' )? f= ID ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* )=>rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:980:3: ( ( ID ':' )=>fb= ID ':' )?
            int alt35=2;
            int LA35_0 = input.LA(1);
            if ( (LA35_0==ID) ) {
                int LA35_1 = input.LA(2);
                if ( (LA35_1==39) ) {
                    alt35=1;
                }
            }
            switch (alt35) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:980:5: ( ID ':' )=>fb= ID ':'
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint2028); if (failed) return ;
                    match(input,39,FOLLOW_39_in_constraint2030); if (failed) return ;

                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint2040); if (failed) return ;
            if ( backtracking==0 ) {
              
              			if ( fb != null ) {
              				d = new FieldBindingDescr( f.getText(), fb.getText() );
              				d.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              				constraints.add( d );
              			} 
              			fc = new FieldConstraintDescr(f.getText());
              			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:991:3: ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* )=>rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )?
            int alt37=2;
            int LA37_0 = input.LA(1);
            if ( ((LA37_0>=57 && LA37_0<=65)) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:992:4: ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* )=>rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                    {
                    pushFollow(FOLLOW_constraint_expression_in_constraint2056);
                    rd=constraint_expression();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      				fc.addRestriction(rd);
                      				constraints.add(fc);
                      			
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:997:4: ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                    loop36:
                    do {
                        int alt36=2;
                        int LA36_0 = input.LA(1);
                        if ( ((LA36_0>=55 && LA36_0<=56)) ) {
                            alt36=1;
                        }


                        switch (alt36) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:998:5: ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression
                    	    {
                    	    con=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=55 && input.LA(1)<=56) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return ;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint2075);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      
                    	      					if (con.getText().equals("&") ) {								
                    	      						fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	      					} else {
                    	      						fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	      					}							
                    	      				
                    	    }
                    	    pushFollow(FOLLOW_constraint_expression_in_constraint2092);
                    	    rd=constraint_expression();
                    	    _fsp--;
                    	    if (failed) return ;
                    	    if ( backtracking==0 ) {
                    	      
                    	      					fc.addRestriction(rd);
                    	      				
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1014:1: constraint_expression returns [RestrictionDescr rd] : op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) ;
    public RestrictionDescr constraint_expression() throws RecognitionException {   
        RestrictionDescr rd = null;

        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1016:3: (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1016:3: op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
            {
            op=(Token)input.LT(1);
            if ( (input.LA(1)>=57 && input.LA(1)<=65) ) {
                input.consume();
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return rd;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint_expression2144);    throw mse;
            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
            int alt38=4;
            switch ( input.LA(1) ) {
            case ID:
                int LA38_1 = input.LA(2);
                if ( (LA38_1==30) ) {
                    alt38=2;
                }
                else if ( (LA38_1==EOF||LA38_1==RIGHT_PAREN||LA38_1==33||(LA38_1>=55 && LA38_1<=56)) ) {
                    alt38=1;
                }
                else {
                    if (backtracking>0) {failed=true; return rd;}
                    NoViableAltException nvae =
                        new NoViableAltException("1026:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )", 38, 1, input);

                    throw nvae;
                }
                break;
            case INT:
            case BOOL:
            case STRING:
            case FLOAT:
            case 66:
                alt38=3;
                break;
            case LEFT_PAREN:
                alt38=4;
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("1026:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:5: ( ID )=>bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint_expression2211); if (failed) return rd;
                    if ( backtracking==0 ) {
                      
                      				rd = new VariableRestrictionDescr(op.getText(), bvc.getText());
                      			
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1031:4: ( enum_constraint )=>lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_constraint_expression2227);
                    lc=enum_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc, true);
                      			
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1036:4: ( literal_constraint )=>lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_constraint_expression2250);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc);
                      			
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1040:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_constraint_expression2264);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1047:1: literal_constraint returns [String text] : ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' ) ;
    public String literal_constraint() throws RecognitionException {   
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1051:4: ( ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1051:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1051:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' )
            int alt39=5;
            switch ( input.LA(1) ) {
            case STRING:
                alt39=1;
                break;
            case INT:
                alt39=2;
                break;
            case FLOAT:
                alt39=3;
                break;
            case BOOL:
                alt39=4;
                break;
            case 66:
                alt39=5;
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("1051:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' )", 39, 0, input);

                throw nvae;
            }

            switch (alt39) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1051:6: ( STRING )=>t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint2303); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1052:5: ( INT )=>t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint2314); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1053:5: ( FLOAT )=>t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint2327); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1054:5: ( BOOL )=>t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint2338); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1055:5: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,66,FOLLOW_66_in_literal_constraint2350); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1059:1: enum_constraint returns [String text] : (cls= ID '.' en= ID ) ;
    public String enum_constraint() throws RecognitionException {   
        String text = null;

        Token cls=null;
        Token en=null;

        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1063:4: ( (cls= ID '.' en= ID ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1063:4: (cls= ID '.' en= ID )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1063:4: (cls= ID '.' en= ID )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1063:5: cls= ID '.' en= ID
            {
            cls=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2381); if (failed) return text;
            match(input,30,FOLLOW_30_in_enum_constraint2383); if (failed) return text;
            en=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2387); if (failed) return text;

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1067:1: predicate[List constraints] : decl= ID ':' field= ID '->' text= paren_chunk ;
    public void predicate(List constraints) throws RecognitionException {   
        Token decl=null;
        Token field=null;
        String text = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1069:3: (decl= ID ':' field= ID '->' text= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1069:3: decl= ID ':' field= ID '->' text= paren_chunk
            {
            decl=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2409); if (failed) return ;
            match(input,39,FOLLOW_39_in_predicate2411); if (failed) return ;
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2415); if (failed) return ;
            match(input,67,FOLLOW_67_in_predicate2417); if (failed) return ;
            pushFollow(FOLLOW_paren_chunk_in_predicate2421);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1077:1: paren_chunk returns [String text] : loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN ;
    public String paren_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1083:10: (loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1083:10: loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk2468); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
               
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1093:3: ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )*
            loop40:
            do {
                int alt40=3;
                int LA40_0 = input.LA(1);
                if ( ((LA40_0>=ID && LA40_0<=FLOAT)||(LA40_0>=LEFT_SQUARE && LA40_0<=73)) ) {
                    alt40=1;
                }
                else if ( (LA40_0==LEFT_PAREN) ) {
                    alt40=2;
                }


                switch (alt40) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1094:4: (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN)
            	    {
            	    if ( (input.LA(1)>=ID && input.LA(1)<=FLOAT)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=73) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk2484);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1099:4: ( paren_chunk )=>chunk= paren_chunk
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk2508);
            	    chunk=paren_chunk();
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
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk2544); if (failed) return text;
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


    // $ANTLR start square_chunk
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1118:1: square_chunk returns [String text] : loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE ;
    public String square_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1124:10: (loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1124:10: loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk2605); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
               
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1134:3: ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )*
            loop41:
            do {
                int alt41=3;
                int LA41_0 = input.LA(1);
                if ( ((LA41_0>=ID && LA41_0<=RIGHT_PAREN)||(LA41_0>=EOL && LA41_0<=73)) ) {
                    alt41=1;
                }
                else if ( (LA41_0==LEFT_SQUARE) ) {
                    alt41=2;
                }


                switch (alt41) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1135:4: (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE)
            	    {
            	    if ( (input.LA(1)>=ID && input.LA(1)<=RIGHT_PAREN)||(input.LA(1)>=EOL && input.LA(1)<=73) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk2621);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1140:4: ( square_chunk )=>chunk= square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk2645);
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
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk2681); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1159:1: retval_constraint returns [String text] : c= paren_chunk ;
    public String retval_constraint() throws RecognitionException {   
        String text = null;

        String c = null;


        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1164:3: (c= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1164:3: c= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint2726);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1172:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )* ;
    public BaseDescr lhs_or() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		OrDescr or = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1178:3: (left= lhs_and ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1178:3: left= lhs_and ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )*
            {
            pushFollow(FOLLOW_lhs_and_in_lhs_or2759);
            left=lhs_and();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              d = left; 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1179:3: ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);
                if ( ((LA42_0>=53 && LA42_0<=54)) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1179:5: ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and
            	    {
            	    if ( (input.LA(1)>=53 && input.LA(1)<=54) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or2768);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_and_in_lhs_or2778);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1193:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )* ;
    public BaseDescr lhs_and() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		AndDescr and = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1199:3: (left= lhs_unary ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1199:3: left= lhs_unary ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )*
            {
            pushFollow(FOLLOW_lhs_unary_in_lhs_and2814);
            left=lhs_unary();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = left; 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1200:3: ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                if ( ((LA43_0>=68 && LA43_0<=69)) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1200:5: ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=68 && input.LA(1)<=69) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and2823);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2833);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1214:1: lhs_unary returns [BaseDescr d] : ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon ;
    public BaseDescr lhs_unary() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr u = null;

        FromDescr fm = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1218:4: ( ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1218:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1218:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | '(' u= lhs ')' )
            int alt45=5;
            switch ( input.LA(1) ) {
            case 70:
                alt45=1;
                break;
            case 71:
                alt45=2;
                break;
            case 72:
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
                    new NoViableAltException("1218:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | '(' u= lhs ')' )", 45, 0, input);

                throw nvae;
            }

            switch (alt45) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1218:6: ( lhs_exist )=>u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary2870);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1219:5: ( lhs_not )=>u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary2878);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1220:5: ( lhs_eval )=>u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2886);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1221:5: ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )?
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_unary2894);
                    u=lhs_column();
                    _fsp--;
                    if (failed) return d;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1221:18: ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )?
                    int alt44=4;
                    int LA44_0 = input.LA(1);
                    if ( (LA44_0==47) ) {
                        switch ( input.LA(2) ) {
                            case 52:
                                alt44=3;
                                break;
                            case 48:
                                alt44=2;
                                break;
                            case ID:
                                alt44=1;
                                break;
                        }

                    }
                    switch (alt44) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1222:14: ( ( from_statement ) )=> (fm= from_statement )
                            {
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1222:14: (fm= from_statement )
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1222:15: fm= from_statement
                            {
                            pushFollow(FOLLOW_from_statement_in_lhs_unary2914);
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
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:14: ( ( accumulate_statement ) )=> (ac= accumulate_statement )
                            {
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:14: (ac= accumulate_statement )
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1223:15: ac= accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_lhs_unary2936);
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
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1224:14: ( ( collect_statement ) )=> (cs= collect_statement )
                            {
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1224:14: (cs= collect_statement )
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1224:15: cs= collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_lhs_unary2957);
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1226:5: '(' u= lhs ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary2980); if (failed) return d;
                    pushFollow(FOLLOW_lhs_in_lhs_unary2984);
                    u=lhs();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary2986); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               d = u; 
            }
            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary2996);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1231:1: lhs_exist returns [BaseDescr d] : loc= 'exists' ( ( '(' lhs_column ')' )=> '(' column= lhs_column ')' | column= lhs_column ) ;
    public BaseDescr lhs_exist() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:4: (loc= 'exists' ( ( '(' lhs_column ')' )=> '(' column= lhs_column ')' | column= lhs_column ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:4: loc= 'exists' ( ( '(' lhs_column ')' )=> '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,70,FOLLOW_70_in_lhs_exist3020); if (failed) return d;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:17: ( ( '(' lhs_column ')' )=> '(' column= lhs_column ')' | column= lhs_column )
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
                    new NoViableAltException("1235:17: ( ( '(' lhs_column ')' )=> '(' column= lhs_column ')' | column= lhs_column )", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:18: ( '(' lhs_column ')' )=> '(' column= lhs_column ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist3023); if (failed) return d;
                    pushFollow(FOLLOW_lhs_column_in_lhs_exist3027);
                    column=lhs_column();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist3029); if (failed) return d;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:46: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_exist3035);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1242:1: lhs_not returns [NotDescr d] : loc= 'not' ( ( '(' lhs_column ')' )=> '(' column= lhs_column ')' | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1246:4: (loc= 'not' ( ( '(' lhs_column ')' )=> '(' column= lhs_column ')' | column= lhs_column ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1246:4: loc= 'not' ( ( '(' lhs_column ')' )=> '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,71,FOLLOW_71_in_lhs_not3065); if (failed) return d;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1246:14: ( ( '(' lhs_column ')' )=> '(' column= lhs_column ')' | column= lhs_column )
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
                    new NoViableAltException("1246:14: ( ( '(' lhs_column ')' )=> '(' column= lhs_column ')' | column= lhs_column )", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1246:15: ( '(' lhs_column ')' )=> '(' column= lhs_column ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not3068); if (failed) return d;
                    pushFollow(FOLLOW_lhs_column_in_lhs_not3072);
                    column=lhs_column();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not3075); if (failed) return d;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1246:44: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_not3081);
                    column=lhs_column();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
              
              			d = new NotDescr( (ColumnDescr) column ); 
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1253:1: lhs_eval returns [BaseDescr d] : loc= 'eval' c= paren_chunk ;
    public BaseDescr lhs_eval() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        String c = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1257:4: (loc= 'eval' c= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1257:4: loc= 'eval' c= paren_chunk
            {
            loc=(Token)input.LT(1);
            match(input,72,FOLLOW_72_in_lhs_eval3109); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval3113);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1265:1: dotted_name returns [String name] : id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '[' ']' )=> '[' ']' )* ;
    public String dotted_name() throws RecognitionException {   
        String name = null;

        Token id=null;

        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1270:3: (id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '[' ']' )=> '[' ']' )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1270:3: id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '[' ']' )=> '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name3144); if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1270:32: ( ( '.' ID )=> '.' id= ID )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);
                if ( (LA48_0==30) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1270:34: ( '.' ID )=> '.' id= ID
            	    {
            	    match(input,30,FOLLOW_30_in_dotted_name3150); if (failed) return name;
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name3154); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "." + id.getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1270:85: ( ( '[' ']' )=> '[' ']' )*
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);
                if ( (LA49_0==LEFT_SQUARE) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1270:87: ( '[' ']' )=> '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name3163); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name3165); if (failed) return name;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1273:1: argument returns [String name] : id= ID ( ( '[' ']' )=> '[' ']' )* ;
    public String argument() throws RecognitionException {   
        String name = null;

        Token id=null;

        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1278:3: (id= ID ( ( '[' ']' )=> '[' ']' )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1278:3: id= ID ( ( '[' ']' )=> '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument3195); if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1278:32: ( ( '[' ']' )=> '[' ']' )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);
                if ( (LA50_0==LEFT_SQUARE) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1278:34: ( '[' ']' )=> '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument3201); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument3203); if (failed) return name;
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


    // $ANTLR start word
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1282:1: word returns [String word] : ( ( ID )=>id= ID | ( 'import' )=> 'import' | ( 'use' )=> 'use' | ( 'rule' )=> 'rule' | ( 'query' )=> 'query' | ( 'salience' )=> 'salience' | ( 'no-loop' )=> 'no-loop' | ( 'when' )=> 'when' | ( 'then' )=> 'then' | ( 'end' )=> 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word = null;

        Token id=null;
        Token str=null;

        
        		word = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1286:4: ( ( ID )=>id= ID | ( 'import' )=> 'import' | ( 'use' )=> 'use' | ( 'rule' )=> 'rule' | ( 'query' )=> 'query' | ( 'salience' )=> 'salience' | ( 'no-loop' )=> 'no-loop' | ( 'when' )=> 'when' | ( 'then' )=> 'then' | ( 'end' )=> 'end' | str= STRING )
            int alt51=11;
            switch ( input.LA(1) ) {
            case ID:
                alt51=1;
                break;
            case 28:
                alt51=2;
                break;
            case 73:
                alt51=3;
                break;
            case 37:
                alt51=4;
                break;
            case 34:
                alt51=5;
                break;
            case 41:
                alt51=6;
                break;
            case 42:
                alt51=7;
                break;
            case 38:
                alt51=8;
                break;
            case RHS:
                alt51=9;
                break;
            case 35:
                alt51=10;
                break;
            case STRING:
                alt51=11;
                break;
            default:
                if (backtracking>0) {failed=true; return word;}
                NoViableAltException nvae =
                    new NoViableAltException("1282:1: word returns [String word] : ( ( ID )=>id= ID | ( 'import' )=> 'import' | ( 'use' )=> 'use' | ( 'rule' )=> 'rule' | ( 'query' )=> 'query' | ( 'salience' )=> 'salience' | ( 'no-loop' )=> 'no-loop' | ( 'when' )=> 'when' | ( 'then' )=> 'then' | ( 'end' )=> 'end' | str= STRING );", 51, 0, input);

                throw nvae;
            }

            switch (alt51) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1286:4: ( ID )=>id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word3231); if (failed) return word;
                    if ( backtracking==0 ) {
                       word=id.getText(); 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1287:4: ( 'import' )=> 'import'
                    {
                    match(input,28,FOLLOW_28_in_word3243); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="import"; 
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1288:4: ( 'use' )=> 'use'
                    {
                    match(input,73,FOLLOW_73_in_word3252); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="use"; 
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1289:4: ( 'rule' )=> 'rule'
                    {
                    match(input,37,FOLLOW_37_in_word3264); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="rule"; 
                    }

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1290:4: ( 'query' )=> 'query'
                    {
                    match(input,34,FOLLOW_34_in_word3275); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="query"; 
                    }

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1291:4: ( 'salience' )=> 'salience'
                    {
                    match(input,41,FOLLOW_41_in_word3285); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="salience"; 
                    }

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1292:5: ( 'no-loop' )=> 'no-loop'
                    {
                    match(input,42,FOLLOW_42_in_word3293); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="no-loop"; 
                    }

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1293:4: ( 'when' )=> 'when'
                    {
                    match(input,38,FOLLOW_38_in_word3301); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="when"; 
                    }

                    }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1294:4: ( 'then' )=> 'then'
                    {
                    match(input,RHS,FOLLOW_RHS_in_word3312); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="then"; 
                    }

                    }
                    break;
                case 10 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1295:4: ( 'end' )=> 'end'
                    {
                    match(input,35,FOLLOW_35_in_word3323); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="end"; 
                    }

                    }
                    break;
                case 11 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1296:4: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word3337); if (failed) return word;
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

    // $ANTLR start synpred34
    public void synpred34_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:716:3: ( ( ( ID LEFT_PAREN )=> ID paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? ) )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:716:3: ( ( ID LEFT_PAREN )=> ID paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? )
        {
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:716:3: ( ( ID LEFT_PAREN )=> ID paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )? )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:716:5: ( ID LEFT_PAREN )=> ID paren_chunk ( ( expression_chain[ad] )=> expression_chain[ad] )?
        {
        match(input,ID,FOLLOW_ID_in_synpred341343); if (failed) return ;
        pushFollow(FOLLOW_paren_chunk_in_synpred341347);
        paren_chunk();
        _fsp--;
        if (failed) return ;
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:726:6: ( ( expression_chain[ad] )=> expression_chain[ad] )?
        int alt58=2;
        int LA58_0 = input.LA(1);
        if ( (LA58_0==30) ) {
            alt58=1;
        }
        switch (alt58) {
            case 1 :
                // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[ad] )=> expression_chain[ad]
                {
                pushFollow(FOLLOW_expression_chain_in_synpred341369);
                expression_chain(null);
                _fsp--;
                if (failed) return ;

                }
                break;

        }


        }


        }
    }
    // $ANTLR end synpred34

    // $ANTLR start synpred37
    public void synpred37_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:756:6: ( LEFT_PAREN )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:756:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred371497); if (failed) return ;

        }
    }
    // $ANTLR end synpred37

    public boolean synpred37() {
        backtracking++;
        int start = input.mark();
        try {
            synpred37_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !failed;
        input.rewind(start);
        backtracking--;
        failed=false;
        return success;
    }
    public boolean synpred34() {
        backtracking++;
        int start = input.mark();
        try {
            synpred34_fragment(); // can never throw exception
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
        "\2\4\1\uffff\1\16\1\uffff\1\4";
    public static final String DFA8_maxS =
        "\1\4\1\41\1\uffff\1\16\1\uffff\1\41";
    public static final String DFA8_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    public static final String DFA8_specialS =
        "\6\uffff}>";
    public static final String[] DFA8_transition = {
        "\1\1",
        "\1\2\7\uffff\1\4\1\3\20\uffff\1\2\2\uffff\1\4",
        "",
        "\1\5",
        "",
        "\1\2\7\uffff\1\4\1\3\23\uffff\1\4"
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
        "\2\4\1\uffff\1\16\1\uffff\1\4";
    public static final String DFA9_maxS =
        "\1\4\1\41\1\uffff\1\16\1\uffff\1\41";
    public static final String DFA9_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    public static final String DFA9_specialS =
        "\6\uffff}>";
    public static final String[] DFA9_transition = {
        "\1\1",
        "\1\2\7\uffff\1\4\1\3\20\uffff\1\2\2\uffff\1\4",
        "",
        "\1\5",
        "",
        "\1\2\7\uffff\1\4\1\3\23\uffff\1\4"
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
 

    public static final BitSet FOLLOW_26_in_opt_semicolon46 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit58 = new BitSet(new long[]{0x0000003530000002L});
    public static final BitSet FOLLOW_statement_in_compilation_unit65 = new BitSet(new long[]{0x0000003530000002L});
    public static final BitSet FOLLOW_package_statement_in_prolog90 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement114 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement167 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_package_statement195 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement199 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_import_statement218 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_import_name_in_import_statement222 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_function_import_statement240 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_function_import_statement242 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement246 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name280 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_30_in_import_name286 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_import_name290 = new BitSet(new long[]{0x00000000C0000002L});
    public static final BitSet FOLLOW_31_in_import_name300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_global324 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_global328 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_global332 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_global334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_function361 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_function366 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_function372 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function381 = new BitSet(new long[]{0x0000000000001010L});
    public static final BitSet FOLLOW_dotted_name_in_function391 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_argument_in_function397 = new BitSet(new long[]{0x0000000200001000L});
    public static final BitSet FOLLOW_33_in_function411 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_function416 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_argument_in_function422 = new BitSet(new long[]{0x0000000200001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function446 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CURLY_CHUNK_in_function452 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_query483 = new BitSet(new long[]{0x0000066C10000250L,0x0000000000000200L});
    public static final BitSet FOLLOW_word_in_query487 = new BitSet(new long[]{0x0000000800000810L,0x00000000000001C0L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query501 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_query516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_template542 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_template546 = new BitSet(new long[]{0x0000000004000010L});
    public static final BitSet FOLLOW_opt_semicolon_in_template548 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_template_slot_in_template563 = new BitSet(new long[]{0x0000000800000010L});
    public static final BitSet FOLLOW_35_in_template578 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template580 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot612 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_template_slot616 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot618 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_rule649 = new BitSet(new long[]{0x0000066C10000250L,0x0000000000000200L});
    public static final BitSet FOLLOW_word_in_rule653 = new BitSet(new long[]{0x00007F4200000040L});
    public static final BitSet FOLLOW_rule_attributes_in_rule662 = new BitSet(new long[]{0x0000004000000040L});
    public static final BitSet FOLLOW_38_in_rule671 = new BitSet(new long[]{0x0000008000000850L,0x00000000000001C0L});
    public static final BitSet FOLLOW_39_in_rule673 = new BitSet(new long[]{0x0000000000000850L,0x00000000000001C0L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule691 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RHS_in_rule714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule_attributes739 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_rule_attributes741 = new BitSet(new long[]{0x00007E0200000002L});
    public static final BitSet FOLLOW_33_in_rule_attributes750 = new BitSet(new long[]{0x00007E0000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes755 = new BitSet(new long[]{0x00007E0200000002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_salience889 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_INT_in_salience893 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_salience895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_no_loop930 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_no_loop932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_no_loop957 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_no_loop961 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_no_loop963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_auto_focus1009 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_auto_focus1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_auto_focus1036 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1040 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_auto_focus1042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_activation_group1084 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_STRING_in_activation_group1088 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_activation_group1090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_agenda_group1119 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1123 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_agenda_group1125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_duration1157 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_INT_in_duration1161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1187 = new BitSet(new long[]{0x0000000000000812L,0x00000000000001C0L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_from_statement1287 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_from_source_in_from_statement1291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1343 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source1347 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_expression_chain_in_from_source1369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1389 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_expression_chain_in_from_source1406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_expression_chain1437 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_expression_chain1441 = new BitSet(new long[]{0x0000000040002802L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain1472 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain1505 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_accumulate_statement1566 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_48_in_accumulate_statement1568 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement1578 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_accumulate_statement1582 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_accumulate_statement1584 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_accumulate_statement1593 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1597 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_accumulate_statement1599 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_accumulate_statement1608 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1612 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_accumulate_statement1614 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_accumulate_statement1623 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1627 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement1629 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_collect_statement1672 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_collect_statement1674 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement1684 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_collect_statement1688 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement1690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding1725 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_fact_binding1735 = new BitSet(new long[]{0x0000000000000810L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding1739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression1771 = new BitSet(new long[]{0x0000000000000810L});
    public static final BitSet FOLLOW_fact_expression_in_paren_in_fact_expression1775 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression1778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression1789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression_in_paren1820 = new BitSet(new long[]{0x0000000000000810L});
    public static final BitSet FOLLOW_fact_expression_in_paren_in_fact_expression_in_paren1824 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression_in_paren1826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression_in_paren1837 = new BitSet(new long[]{0x0060000000000002L});
    public static final BitSet FOLLOW_set_in_fact_expression_in_paren1850 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_fact_in_fact_expression_in_paren1867 = new BitSet(new long[]{0x0060000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_fact1906 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact1919 = new BitSet(new long[]{0x0000000000001010L});
    public static final BitSet FOLLOW_constraints_in_fact1931 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact1952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints1982 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_predicate_in_constraints1985 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_33_in_constraints1993 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constraint_in_constraints1996 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_predicate_in_constraints1999 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_ID_in_constraint2028 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_constraint2030 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_constraint2040 = new BitSet(new long[]{0xFE00000000000002L,0x0000000000000003L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2056 = new BitSet(new long[]{0x0180000000000002L});
    public static final BitSet FOLLOW_set_in_constraint2075 = new BitSet(new long[]{0xFE00000000000000L,0x0000000000000003L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2092 = new BitSet(new long[]{0x0180000000000002L});
    public static final BitSet FOLLOW_set_in_constraint_expression2144 = new BitSet(new long[]{0x0000000000000F90L,0x0000000000000004L});
    public static final BitSet FOLLOW_ID_in_constraint_expression2211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint_expression2227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint_expression2250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint_expression2264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint2303 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint2314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_literal_constraint2350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2381 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_enum_constraint2383 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_predicate2409 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_predicate2411 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_predicate2415 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_67_in_predicate2417 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate2421 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk2468 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000000003FFL});
    public static final BitSet FOLLOW_set_in_paren_chunk2484 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000000003FFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk2508 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000000003FFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk2544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk2605 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000000003FFL});
    public static final BitSet FOLLOW_set_in_square_chunk2621 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000000003FFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk2645 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000000003FFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk2681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint2726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2759 = new BitSet(new long[]{0x0060000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or2768 = new BitSet(new long[]{0x0000000000000810L,0x00000000000001C0L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2778 = new BitSet(new long[]{0x0060000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2814 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000030L});
    public static final BitSet FOLLOW_set_in_lhs_and2823 = new BitSet(new long[]{0x0000000000000810L,0x00000000000001C0L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2833 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000030L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2870 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2878 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2886 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary2894 = new BitSet(new long[]{0x0000800004000002L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary2914 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary2936 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary2957 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary2980 = new BitSet(new long[]{0x0000000000000810L,0x00000000000001C0L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary2984 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary2986 = new BitSet(new long[]{0x0000000004000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary2996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_lhs_exist3020 = new BitSet(new long[]{0x0000000000000810L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist3023 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3027 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist3029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_lhs_not3065 = new BitSet(new long[]{0x0000000000000810L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not3068 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3072 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not3075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_lhs_eval3109 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval3113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name3144 = new BitSet(new long[]{0x0000000040002002L});
    public static final BitSet FOLLOW_30_in_dotted_name3150 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_dotted_name3154 = new BitSet(new long[]{0x0000000040002002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name3163 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name3165 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_ID_in_argument3195 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument3201 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument3203 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_ID_in_word3231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_word3243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_word3252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_word3264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_word3275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_word3285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_word3293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_word3301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RHS_in_word3312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_word3323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word3337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred341343 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_paren_chunk_in_synpred341347 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_expression_chain_in_synpred341369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred371497 = new BitSet(new long[]{0x0000000000000002L});

}