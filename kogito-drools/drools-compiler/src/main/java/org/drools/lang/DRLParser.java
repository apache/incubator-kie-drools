// $ANTLR 3.0b5 D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g 2007-01-23 16:40:48

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "RULE", "WHEN", "STRING", "BOOL", "INT", "LEFT_PAREN", "RIGHT_PAREN", "FLOAT", "LEFT_CURLY", "RIGHT_CURLY", "LEFT_SQUARE", "RIGHT_SQUARE", "END", "THEN", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "MISC", "';'", "'package'", "'import'", "'function'", "'.'", "'.*'", "'global'", "','", "'query'", "'template'", "':'", "'attributes'", "'date-effective'", "'date-expires'", "'enabled'", "'salience'", "'no-loop'", "'auto-focus'", "'activation-group'", "'agenda-group'", "'duration'", "'from'", "'accumulate'", "'init'", "'action'", "'result'", "'collect'", "'or'", "'||'", "'&'", "'|'", "'->'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='", "'contains'", "'matches'", "'excludes'", "'null'", "'and'", "'&&'", "'exists'", "'not'", "'eval'", "'forall'", "'use'"
    };
    public static final int LEFT_PAREN=10;
    public static final int RIGHT_CURLY=14;
    public static final int BOOL=8;
    public static final int END=17;
    public static final int HexDigit=22;
    public static final int WHEN=6;
    public static final int WS=20;
    public static final int MISC=28;
    public static final int STRING=7;
    public static final int FLOAT=12;
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
    public static final int RIGHT_PAREN=11;
    public static final int LEFT_CURLY=13;
    public static final int RIGHT_SQUARE=16;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=26;
    public static final int ID=4;

        public DRLParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[149+1];
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:263:1: opt_semicolon : ( ( ';' )=> ';' )? ;
    public void opt_semicolon() throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:264:4: ( ( ( ';' )=> ';' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:264:4: ( ( ';' )=> ';' )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:264:4: ( ( ';' )=> ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);
            if ( (LA1_0==29) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ';' )=> ';'
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:267:1: compilation_unit : prolog ( ( statement )=> statement )+ ;
    public void compilation_unit() throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:268:4: ( prolog ( ( statement )=> statement )+ )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:268:4: prolog ( ( statement )=> statement )+
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit58);
            prolog();
            _fsp--;
            if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:269:3: ( ( statement )=> statement )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:269:5: ( statement )=> statement
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:272:1: prolog : ( ( package_statement )=>name= package_statement )? ;
    public void prolog() throws RecognitionException {   
        String name = null;


        
        		String packageName = "";
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:276:4: ( ( ( package_statement )=>name= package_statement )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:276:4: ( ( package_statement )=>name= package_statement )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:276:4: ( ( package_statement )=>name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0==30) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:276:6: ( package_statement )=>name= package_statement
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
               
              			this.packageDescr = new PackageDescr( packageName ); 
              		
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:282:1: statement : ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) ;
    public void statement() throws RecognitionException {   
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:284:2: ( ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:284:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:284:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )
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
                        new NoViableAltException("284:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )", 4, 1, input);

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
                    new NoViableAltException("284:2: ( ( import_statement )=> import_statement | ( function_import_statement )=> function_import_statement | ( global )=> global | ( function )=> function | ( template )=>t= template | ( rule )=>r= rule | q= query )", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:284:4: ( import_statement )=> import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement114);
                    import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:285:10: ( function_import_statement )=> function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement126);
                    function_import_statement();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:286:4: ( global )=> global
                    {
                    pushFollow(FOLLOW_global_in_statement132);
                    global();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:287:4: ( function )=> function
                    {
                    pushFollow(FOLLOW_function_in_statement138);
                    function();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:288:10: ( template )=>t= template
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:289:4: ( rule )=>r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement161);
                    r=rule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       if( r != null ) this.packageDescr.addRule( r ); 
                    }

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:290:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement173);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:294:1: package_statement returns [String packageName] : 'package' name= dotted_name opt_semicolon ;
    public String package_statement() throws RecognitionException {   
        String packageName = null;

        String name = null;


        
        		packageName = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:299:3: ( 'package' name= dotted_name opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:299:3: 'package' name= dotted_name opt_semicolon
            {
            match(input,30,FOLLOW_30_in_package_statement202); if (failed) return packageName;
            pushFollow(FOLLOW_dotted_name_in_package_statement206);
            name=dotted_name();
            _fsp--;
            if (failed) return packageName;
            pushFollow(FOLLOW_opt_semicolon_in_package_statement208);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:306:1: import_statement : 'import' name= import_name opt_semicolon ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:4: ( 'import' name= import_name opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:4: 'import' name= import_name opt_semicolon
            {
            match(input,31,FOLLOW_31_in_import_statement225); if (failed) return ;
            pushFollow(FOLLOW_import_name_in_import_statement229);
            name=import_name();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_import_statement231);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:314:1: function_import_statement : 'import' 'function' name= import_name opt_semicolon ;
    public void function_import_statement() throws RecognitionException {   
        String name = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:315:4: ( 'import' 'function' name= import_name opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:315:4: 'import' 'function' name= import_name opt_semicolon
            {
            match(input,31,FOLLOW_31_in_function_import_statement247); if (failed) return ;
            match(input,32,FOLLOW_32_in_function_import_statement249); if (failed) return ;
            pushFollow(FOLLOW_import_name_in_function_import_statement253);
            name=import_name();
            _fsp--;
            if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement255);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:323:1: import_name returns [String name] : id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '.*' )=>star= '.*' )? ;
    public String import_name() throws RecognitionException {   
        String name = null;

        Token id=null;
        Token star=null;

        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:328:3: (id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '.*' )=>star= '.*' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:328:3: id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '.*' )=>star= '.*' )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name287); if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:328:32: ( ( '.' ID )=> '.' id= ID )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);
                if ( (LA5_0==33) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:328:34: ( '.' ID )=> '.' id= ID
            	    {
            	    match(input,33,FOLLOW_33_in_import_name293); if (failed) return name;
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_import_name297); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "." + id.getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:328:85: ( ( '.*' )=>star= '.*' )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0==34) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:328:86: ( '.*' )=>star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,34,FOLLOW_34_in_import_name307); if (failed) return name;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:332:1: global : 'global' type= dotted_name id= ID opt_semicolon ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;


        
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:336:3: ( 'global' type= dotted_name id= ID opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:336:3: 'global' type= dotted_name id= ID opt_semicolon
            {
            match(input,35,FOLLOW_35_in_global331); if (failed) return ;
            pushFollow(FOLLOW_dotted_name_in_global335);
            type=dotted_name();
            _fsp--;
            if (failed) return ;
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global339); if (failed) return ;
            pushFollow(FOLLOW_opt_semicolon_in_global341);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:343:1: function : loc= 'function' ( ( dotted_name )=>retType= dotted_name )? name= ID '(' ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )? ')' body= curly_chunk ;
    public void function() throws RecognitionException {   
        Token loc=null;
        Token name=null;
        String retType = null;

        String paramType = null;

        String paramName = null;

        String body = null;


        
        		FunctionDescr f = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:348:3: (loc= 'function' ( ( dotted_name )=>retType= dotted_name )? name= ID '(' ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )? ')' body= curly_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:348:3: loc= 'function' ( ( dotted_name )=>retType= dotted_name )? name= ID '(' ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )? ')' body= curly_chunk
            {
            loc=(Token)input.LT(1);
            match(input,32,FOLLOW_32_in_function368); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:348:18: ( ( dotted_name )=>retType= dotted_name )?
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:348:19: ( dotted_name )=>retType= dotted_name
                    {
                    pushFollow(FOLLOW_dotted_name_in_function373);
                    retType=dotted_name();
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function379); if (failed) return ;
            if ( backtracking==0 ) {
              
              			//System.err.println( "function :: " + name.getText() );
              			f = new FunctionDescr( name.getText(), retType );
              			f.setLocation(offset(loc.getLine()), loc.getCharPositionInLine());
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function388); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:355:4: ( ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )* )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( (LA11_0==ID) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:355:6: ( ( ( dotted_name )=> dotted_name )? argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=> dotted_name )? argument )* )=> ( ( dotted_name )=>paramType= dotted_name )? paramName= argument ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )*
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:355:6: ( ( dotted_name )=>paramType= dotted_name )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:355:7: ( dotted_name )=>paramType= dotted_name
                            {
                            pushFollow(FOLLOW_dotted_name_in_function398);
                            paramType=dotted_name();
                            _fsp--;
                            if (failed) return ;

                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function404);
                    paramName=argument();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      					f.addParameter( paramType, paramName );
                      				
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:359:5: ( ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);
                        if ( (LA10_0==36) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:359:7: ( ',' ( ( dotted_name )=> dotted_name )? argument )=> ',' ( ( dotted_name )=>paramType= dotted_name )? paramName= argument
                    	    {
                    	    match(input,36,FOLLOW_36_in_function418); if (failed) return ;
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:359:11: ( ( dotted_name )=>paramType= dotted_name )?
                    	    int alt9=2;
                    	    alt9 = dfa9.predict(input);
                    	    switch (alt9) {
                    	        case 1 :
                    	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:359:12: ( dotted_name )=>paramType= dotted_name
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function423);
                    	            paramType=dotted_name();
                    	            _fsp--;
                    	            if (failed) return ;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function429);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function453); if (failed) return ;
            pushFollow(FOLLOW_curly_chunk_in_function459);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:377:1: query returns [QueryDescr query] : loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end' ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query = null;

        Token loc=null;
        String queryName = null;


        
        		query = null;
        		AndDescr lhs = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:383:3: (loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:383:3: loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end'
            {
            loc=(Token)input.LT(1);
            match(input,37,FOLLOW_37_in_query490); if (failed) return query;
            pushFollow(FOLLOW_word_in_query494);
            queryName=word();
            _fsp--;
            if (failed) return query;
            if ( backtracking==0 ) {
               
              			query = new QueryDescr( queryName, null ); 
              			query.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			lhs = new AndDescr(); query.setLhs( lhs ); 
              			lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:390:3: ( normal_lhs_block[lhs] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:392:4: normal_lhs_block[lhs]
            {
            pushFollow(FOLLOW_normal_lhs_block_in_query508);
            normal_lhs_block(lhs);
            _fsp--;
            if (failed) return query;

            }

            match(input,END,FOLLOW_END_in_query523); if (failed) return query;

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:399:1: template returns [FactTemplateDescr template] : loc= 'template' templateName= ID opt_semicolon ( ( template_slot )=>slot= template_slot )+ 'end' opt_semicolon ;
    public FactTemplateDescr template() throws RecognitionException {   
        FactTemplateDescr template = null;

        Token loc=null;
        Token templateName=null;
        FieldTemplateDescr slot = null;


        
        		template = null;		
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:404:3: (loc= 'template' templateName= ID opt_semicolon ( ( template_slot )=>slot= template_slot )+ 'end' opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:404:3: loc= 'template' templateName= ID opt_semicolon ( ( template_slot )=>slot= template_slot )+ 'end' opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,38,FOLLOW_38_in_template549); if (failed) return template;
            templateName=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template553); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template555);
            opt_semicolon();
            _fsp--;
            if (failed) return template;
            if ( backtracking==0 ) {
              
              			template = new FactTemplateDescr(templateName.getText());
              			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:409:3: ( ( template_slot )=>slot= template_slot )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:410:4: ( template_slot )=>slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template570);
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

            match(input,END,FOLLOW_END_in_template585); if (failed) return template;
            pushFollow(FOLLOW_opt_semicolon_in_template587);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:418:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name name= ID opt_semicolon ;
    public FieldTemplateDescr template_slot() throws RecognitionException {   
        FieldTemplateDescr field = null;

        Token name=null;
        String fieldType = null;


        
        		field = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:424:4: (fieldType= dotted_name name= ID opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:424:4: fieldType= dotted_name name= ID opt_semicolon
            {
            pushFollow(FOLLOW_dotted_name_in_template_slot619);
            fieldType=dotted_name();
            _fsp--;
            if (failed) return field;
            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template_slot623); if (failed) return field;
            pushFollow(FOLLOW_opt_semicolon_in_template_slot625);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:433:1: rule returns [RuleDescr rule] : loc= RULE ruleName= word rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule = null;

        Token loc=null;
        String ruleName = null;


        
        		rule = null;
        		String consequence = "";
        		AndDescr lhs = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:440:3: (loc= RULE ruleName= word rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:440:3: loc= RULE ruleName= word rule_attributes[rule] ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )? rhs_chunk[rule]
            {
            loc=(Token)input.LT(1);
            match(input,RULE,FOLLOW_RULE_in_rule656); if (failed) return rule;
            pushFollow(FOLLOW_word_in_rule660);
            ruleName=word();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               
              			debug( "start rule: " + ruleName );
              			rule = new RuleDescr( ruleName, null ); 
              			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			rule.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              		
            }
            pushFollow(FOLLOW_rule_attributes_in_rule669);
            rule_attributes(rule);
            _fsp--;
            if (failed) return rule;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:448:3: ( ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0==WHEN) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:448:5: ( WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] ) )=>loc= WHEN ( ( ':' )=> ':' )? ( normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,WHEN,FOLLOW_WHEN_in_rule678); if (failed) return rule;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:448:14: ( ( ':' )=> ':' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    if ( (LA13_0==39) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ':' )=> ':'
                            {
                            match(input,39,FOLLOW_39_in_rule680); if (failed) return rule;

                            }
                            break;

                    }

                    if ( backtracking==0 ) {
                       
                      				lhs = new AndDescr(); rule.setLhs( lhs ); 
                      				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                      			
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:453:4: ( normal_lhs_block[lhs] )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:455:5: normal_lhs_block[lhs]
                    {
                    pushFollow(FOLLOW_normal_lhs_block_in_rule698);
                    normal_lhs_block(lhs);
                    _fsp--;
                    if (failed) return rule;

                    }


                    }
                    break;

            }

            pushFollow(FOLLOW_rhs_chunk_in_rule719);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:464:1: rule_attributes[RuleDescr rule] : ( ( 'attributes' ':' )=> 'attributes' ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:466:4: ( ( ( 'attributes' ':' )=> 'attributes' ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:466:4: ( ( 'attributes' ':' )=> 'attributes' ':' )? ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:466:4: ( ( 'attributes' ':' )=> 'attributes' ':' )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( (LA15_0==40) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:466:5: ( 'attributes' ':' )=> 'attributes' ':'
                    {
                    match(input,40,FOLLOW_40_in_rule_attributes740); if (failed) return ;
                    match(input,39,FOLLOW_39_in_rule_attributes742); if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:467:4: ( ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                if ( (LA17_0==36||(LA17_0>=41 && LA17_0<=49)) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:467:6: ( ( ( ',' )=> ',' )? rule_attribute )=> ( ( ',' )=> ',' )? a= rule_attribute
            	    {
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:467:6: ( ( ',' )=> ',' )?
            	    int alt16=2;
            	    int LA16_0 = input.LA(1);
            	    if ( (LA16_0==36) ) {
            	        alt16=1;
            	    }
            	    switch (alt16) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( ',' )=> ','
            	            {
            	            match(input,36,FOLLOW_36_in_rule_attributes751); if (failed) return ;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes756);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:476:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | a= enabled );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d = null;

        AttributeDescr a = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:481:4: ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | a= enabled )
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
                    new NoViableAltException("476:1: rule_attribute returns [AttributeDescr d] : ( ( salience )=>a= salience | ( no_loop )=>a= no_loop | ( agenda_group )=>a= agenda_group | ( duration )=>a= duration | ( activation_group )=>a= activation_group | ( auto_focus )=>a= auto_focus | ( date_effective )=>a= date_effective | ( date_expires )=>a= date_expires | a= enabled );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:481:4: ( salience )=>a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute797);
                    a=salience();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:482:5: ( no_loop )=>a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute807);
                    a=no_loop();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:483:5: ( agenda_group )=>a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute818);
                    a=agenda_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:484:5: ( duration )=>a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute831);
                    a=duration();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:485:5: ( activation_group )=>a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute845);
                    a=activation_group();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:486:5: ( auto_focus )=>a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute856);
                    a=auto_focus();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = a; 
                    }

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:487:29: ( date_effective )=>a= date_effective
                    {
                    pushFollow(FOLLOW_date_effective_in_rule_attribute891);
                    a=date_effective();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:488:5: ( date_expires )=>a= date_expires
                    {
                    pushFollow(FOLLOW_date_expires_in_rule_attribute901);
                    a=date_expires();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                      d = a; 
                    }

                    }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:489:29: a= enabled
                    {
                    pushFollow(FOLLOW_enabled_in_rule_attribute935);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:493:1: date_effective returns [AttributeDescr d] : loc= 'date-effective' val= STRING ;
    public AttributeDescr date_effective() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:498:3: (loc= 'date-effective' val= STRING )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:498:3: loc= 'date-effective' val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,41,FOLLOW_41_in_date_effective967); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_effective971); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:506:1: date_expires returns [AttributeDescr d] : loc= 'date-expires' val= STRING ;
    public AttributeDescr date_expires() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token val=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:511:3: (loc= 'date-expires' val= STRING )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:511:3: loc= 'date-expires' val= STRING
            {
            loc=(Token)input.LT(1);
            match(input,42,FOLLOW_42_in_date_expires1002); if (failed) return d;
            val=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_date_expires1006); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:520:1: enabled returns [AttributeDescr d] : loc= 'enabled' t= BOOL opt_semicolon ;
    public AttributeDescr enabled() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:525:4: (loc= 'enabled' t= BOOL opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:525:4: loc= 'enabled' t= BOOL opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,43,FOLLOW_43_in_enabled1039); if (failed) return d;
            t=(Token)input.LT(1);
            match(input,BOOL,FOLLOW_BOOL_in_enabled1043); if (failed) return d;
            pushFollow(FOLLOW_opt_semicolon_in_enabled1045);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:536:1: salience returns [AttributeDescr d ] : loc= 'salience' i= INT opt_semicolon ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:541:3: (loc= 'salience' i= INT opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:541:3: loc= 'salience' i= INT opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,44,FOLLOW_44_in_salience1087); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience1091); if (failed) return d;
            pushFollow(FOLLOW_opt_semicolon_in_salience1093);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:548:1: no_loop returns [AttributeDescr d] : ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:553:3: ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) )
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
                        new NoViableAltException("548:1: no_loop returns [AttributeDescr d] : ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );", 19, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("548:1: no_loop returns [AttributeDescr d] : ( ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:553:3: ( ( 'no-loop' opt_semicolon ) )=> (loc= 'no-loop' opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:553:3: (loc= 'no-loop' opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:554:4: loc= 'no-loop' opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,45,FOLLOW_45_in_no_loop1128); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_no_loop1130);
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:561:3: (loc= 'no-loop' t= BOOL opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:561:3: (loc= 'no-loop' t= BOOL opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:562:4: loc= 'no-loop' t= BOOL opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,45,FOLLOW_45_in_no_loop1155); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1159); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_no_loop1161);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:572:1: auto_focus returns [AttributeDescr d] : ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );
    public AttributeDescr auto_focus() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:577:3: ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) )
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
                        new NoViableAltException("572:1: auto_focus returns [AttributeDescr d] : ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );", 20, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("572:1: auto_focus returns [AttributeDescr d] : ( ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:577:3: ( ( 'auto-focus' opt_semicolon ) )=> (loc= 'auto-focus' opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:577:3: (loc= 'auto-focus' opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:578:4: loc= 'auto-focus' opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,46,FOLLOW_46_in_auto_focus1207); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_auto_focus1209);
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:585:3: (loc= 'auto-focus' t= BOOL opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:585:3: (loc= 'auto-focus' t= BOOL opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:586:4: loc= 'auto-focus' t= BOOL opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,46,FOLLOW_46_in_auto_focus1234); if (failed) return d;
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1238); if (failed) return d;
                    pushFollow(FOLLOW_opt_semicolon_in_auto_focus1240);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:596:1: activation_group returns [AttributeDescr d] : loc= 'activation-group' name= STRING opt_semicolon ;
    public AttributeDescr activation_group() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:601:3: (loc= 'activation-group' name= STRING opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:601:3: loc= 'activation-group' name= STRING opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,47,FOLLOW_47_in_activation_group1282); if (failed) return d;
            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1286); if (failed) return d;
            pushFollow(FOLLOW_opt_semicolon_in_activation_group1288);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:608:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' name= STRING opt_semicolon ;
    public AttributeDescr agenda_group() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:613:3: (loc= 'agenda-group' name= STRING opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:613:3: loc= 'agenda-group' name= STRING opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,48,FOLLOW_48_in_agenda_group1317); if (failed) return d;
            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1321); if (failed) return d;
            pushFollow(FOLLOW_opt_semicolon_in_agenda_group1323);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:621:1: duration returns [AttributeDescr d] : loc= 'duration' i= INT ;
    public AttributeDescr duration() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:626:3: (loc= 'duration' i= INT )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:626:3: loc= 'duration' i= INT
            {
            loc=(Token)input.LT(1);
            match(input,49,FOLLOW_49_in_duration1355); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1359); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:634:1: normal_lhs_block[AndDescr descrs] : ( ( lhs )=>d= lhs )* ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        BaseDescr d = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:636:3: ( ( ( lhs )=>d= lhs )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:636:3: ( ( lhs )=>d= lhs )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:636:3: ( ( lhs )=>d= lhs )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);
                if ( (LA21_0==ID||LA21_0==LEFT_PAREN||(LA21_0>=73 && LA21_0<=76)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:636:5: ( lhs )=>d= lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1385);
            	    d=lhs();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       if(d != null) descrs.addDescr( d ); 
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:642:1: lhs returns [BaseDescr d] : l= lhs_or ;
    public BaseDescr lhs() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr l = null;


        
        		d=null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:646:4: (l= lhs_or )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:646:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1420);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:650:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );
    public BaseDescr lhs_column() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr f = null;


        
        		d=null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:654:4: ( ( fact_binding )=>f= fact_binding | f= fact )
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
                        new NoViableAltException("650:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );", 22, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("650:1: lhs_column returns [BaseDescr d] : ( ( fact_binding )=>f= fact_binding | f= fact );", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:654:4: ( fact_binding )=>f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_column1448);
                    f=fact_binding();
                    _fsp--;
                    if (failed) return d;
                    if ( backtracking==0 ) {
                       d = f; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:655:4: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_column1457);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:658:1: from_statement returns [FromDescr d] : 'from' ds= from_source ;
    public FromDescr from_statement() throws RecognitionException {   
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;


        
        		d=factory.createFrom();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:663:2: ( 'from' ds= from_source )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:663:2: 'from' ds= from_source
            {
            match(input,50,FOLLOW_50_in_from_statement1482); if (failed) return d;
            pushFollow(FOLLOW_from_source_in_from_statement1486);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:673:1: from_source returns [DeclarativeInvokerDescr ds] : ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) ) ( ( expression_chain[ad] )=> expression_chain[ad] )? ;
    public DeclarativeInvokerDescr from_source() throws RecognitionException {   
        DeclarativeInvokerDescr ds = null;

        Token functionName=null;
        Token var=null;
        String args = null;


        
        		ds = null;
        		AccessorDescr ad = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:3: ( ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) ) ( ( expression_chain[ad] )=> expression_chain[ad] )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:3: ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) ) ( ( expression_chain[ad] )=> expression_chain[ad] )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:3: ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) )
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
                        new NoViableAltException("679:3: ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) )", 23, 1, input);

                    throw nvae;
                }
            }
            else {
                if (backtracking>0) {failed=true; return ds;}
                NoViableAltException nvae =
                    new NoViableAltException("679:3: ( ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk ) | (var= ID ) )", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:4: ( ( ID paren_chunk ) )=> (functionName= ID args= paren_chunk )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:4: (functionName= ID args= paren_chunk )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:6: functionName= ID args= paren_chunk
                    {
                    functionName=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1529); if (failed) return ds;
                    pushFollow(FOLLOW_paren_chunk_in_from_source1533);
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:691:3: (var= ID )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:691:3: (var= ID )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:691:7: var= ID
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1566); if (failed) return ds;
                    if ( backtracking==0 ) {
                      
                      			ad = new AccessorDescr(var.getText());	
                      			ad.setLocation( offset(var.getLine()), var.getCharPositionInLine() );
                      			ds = ad;
                      		    
                    }

                    }


                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:699:3: ( ( expression_chain[ad] )=> expression_chain[ad] )?
            int alt24=2;
            int LA24_0 = input.LA(1);
            if ( (LA24_0==33) ) {
                alt24=1;
            }
            switch (alt24) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[ad] )=> expression_chain[ad]
                    {
                    pushFollow(FOLLOW_expression_chain_in_from_source1589);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:702:1: expression_chain[AccessorDescr as] : ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? ) ;
    public void expression_chain(AccessorDescr as) throws RecognitionException {   
        Token field=null;
        String sqarg = null;

        String paarg = null;


        
          		FieldAccessDescr fa = null;
        	    	MethodAccessDescr ma = null;	
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:708:2: ( ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:708:2: ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:708:2: ( '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:708:4: '.' field= ID ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )? ( ( expression_chain[as] )=> expression_chain[as] )?
            {
            match(input,33,FOLLOW_33_in_expression_chain1614); if (failed) return ;
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_expression_chain1618); if (failed) return ;
            if ( backtracking==0 ) {
              
              	        fa = new FieldAccessDescr(field.getText());	
              		fa.setLocation( offset(field.getLine()), field.getCharPositionInLine() );
              	    
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:713:4: ( ( LEFT_SQUARE )=>sqarg= square_chunk | ( LEFT_PAREN )=>paarg= paren_chunk )?
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:714:6: ( LEFT_SQUARE )=>sqarg= square_chunk
                    {
                    pushFollow(FOLLOW_square_chunk_in_expression_chain1649);
                    sqarg=square_chunk();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      	          fa.setArgument( sqarg );	
                      	      
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:719:6: ( LEFT_PAREN )=>paarg= paren_chunk
                    {
                    pushFollow(FOLLOW_paren_chunk_in_expression_chain1682);
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
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:732:4: ( ( expression_chain[as] )=> expression_chain[as] )?
            int alt26=2;
            int LA26_0 = input.LA(1);
            if ( (LA26_0==33) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:0:0: ( expression_chain[as] )=> expression_chain[as]
                    {
                    pushFollow(FOLLOW_expression_chain_in_expression_chain1702);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:736:1: accumulate_statement returns [AccumulateDescr d] : loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')' ;
    public AccumulateDescr accumulate_statement() throws RecognitionException {   
        AccumulateDescr d = null;

        Token loc=null;
        BaseDescr column = null;

        String text = null;


        
        		d = factory.createAccumulate();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:741:10: (loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:741:10: loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')'
            {
            loc=(Token)input.LT(1);
            match(input,50,FOLLOW_50_in_accumulate_statement1743); if (failed) return d;
            match(input,51,FOLLOW_51_in_accumulate_statement1745); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement1755); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_accumulate_statement1759);
            column=lhs_column();
            _fsp--;
            if (failed) return d;
            match(input,36,FOLLOW_36_in_accumulate_statement1761); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setSourceColumn( (ColumnDescr)column );
              		
            }
            match(input,52,FOLLOW_52_in_accumulate_statement1770); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1774);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            match(input,36,FOLLOW_36_in_accumulate_statement1776); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setInitCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,53,FOLLOW_53_in_accumulate_statement1785); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1789);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            match(input,36,FOLLOW_36_in_accumulate_statement1791); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        d.setActionCode( text.substring(1, text.length()-1) );
              		
            }
            match(input,54,FOLLOW_54_in_accumulate_statement1800); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1804);
            text=paren_chunk();
            _fsp--;
            if (failed) return d;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement1806); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:763:1: collect_statement returns [CollectDescr d] : loc= 'from' 'collect' '(' column= lhs_column ')' ;
    public CollectDescr collect_statement() throws RecognitionException {   
        CollectDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = factory.createCollect();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:768:10: (loc= 'from' 'collect' '(' column= lhs_column ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:768:10: loc= 'from' 'collect' '(' column= lhs_column ')'
            {
            loc=(Token)input.LT(1);
            match(input,50,FOLLOW_50_in_collect_statement1849); if (failed) return d;
            match(input,55,FOLLOW_55_in_collect_statement1851); if (failed) return d;
            if ( backtracking==0 ) {
               
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement1861); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_collect_statement1865);
            column=lhs_column();
            _fsp--;
            if (failed) return d;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement1867); if (failed) return d;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:849:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public BaseDescr fact_binding() throws RecognitionException {   
        BaseDescr d = null;

        Token id=null;
        BaseDescr fe = null;


        
        		d=null;
        		boolean multi=false;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:855:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:855:4: id= ID ':' fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding1902); if (failed) return d;
            match(input,39,FOLLOW_39_in_fact_binding1912); if (failed) return d;
            pushFollow(FOLLOW_fact_expression_in_fact_binding1916);
            fe=fact_expression(id.getText());
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:866:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact );
    public BaseDescr fact_expression(String id) throws RecognitionException {   
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:871:5: ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact )
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
                    new NoViableAltException("866:2: fact_expression[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact );", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:871:5: ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression1948); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_paren_in_fact_expression1952);
                    fe=fact_expression_in_paren(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression1955); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:872:6: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression1966);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:880:2: fact_expression_in_paren[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )* );
    public BaseDescr fact_expression_in_paren(String id) throws RecognitionException {   
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:885:5: ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )* )
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
                    new NoViableAltException("880:2: fact_expression_in_paren[String id] returns [BaseDescr pd] : ( ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )* );", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:885:5: ( '(' fact_expression_in_paren[id] ')' )=> '(' fe= fact_expression_in_paren[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression_in_paren1997); if (failed) return pd;
                    pushFollow(FOLLOW_fact_expression_in_paren_in_fact_expression_in_paren2001);
                    fe=fact_expression_in_paren(id);
                    _fsp--;
                    if (failed) return pd;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression_in_paren2003); if (failed) return pd;
                    if ( backtracking==0 ) {
                       pd=fe; 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:886:6: f= fact ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )*
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression_in_paren2014);
                    f=fact();
                    _fsp--;
                    if (failed) return pd;
                    if ( backtracking==0 ) {
                      
                       			((ColumnDescr)f).setIdentifier( id );
                       			pd = f;
                       		
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:891:4: ( ( ('or'|'||') fact )=> ('or'|'||')f= fact )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);
                        if ( ((LA28_0>=56 && LA28_0<=57)) ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:891:6: ( ('or'|'||') fact )=> ('or'|'||')f= fact
                    	    {
                    	    if ( (input.LA(1)>=56 && input.LA(1)<=57) ) {
                    	        input.consume();
                    	        errorRecovery=false;failed=false;
                    	    }
                    	    else {
                    	        if (backtracking>0) {failed=true; return pd;}
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression_in_paren2027);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      	if ( ! multi ) {
                    	       					BaseDescr first = pd;
                    	       					pd = new OrDescr();
                    	       					((OrDescr)pd).addDescr( first );
                    	       					multi=true;
                    	       				}
                    	       			
                    	    }
                    	    pushFollow(FOLLOW_fact_in_fact_expression_in_paren2044);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:907:1: fact returns [BaseDescr d] : id= dotted_name loc= LEFT_PAREN ( ( constraints[(ColumnDescr) d] )=> constraints[(ColumnDescr) d] )? endLoc= RIGHT_PAREN ;
    public BaseDescr fact() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        Token endLoc=null;
        String id = null;


        
        		d=null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:911:5: (id= dotted_name loc= LEFT_PAREN ( ( constraints[(ColumnDescr) d] )=> constraints[(ColumnDescr) d] )? endLoc= RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:911:5: id= dotted_name loc= LEFT_PAREN ( ( constraints[(ColumnDescr) d] )=> constraints[(ColumnDescr) d] )? endLoc= RIGHT_PAREN
            {
            pushFollow(FOLLOW_dotted_name_in_fact2083);
            id=dotted_name();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               
               			d = new ColumnDescr( id ); 
               		
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact2096); if (failed) return d;
            if ( backtracking==0 ) {
              
               				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
               			        d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
               			
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:919:4: ( ( constraints[(ColumnDescr) d] )=> constraints[(ColumnDescr) d] )?
            int alt30=2;
            int LA30_0 = input.LA(1);
            if ( (LA30_0==ID||LA30_0==LEFT_PAREN) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:919:6: ( constraints[(ColumnDescr) d] )=> constraints[(ColumnDescr) d]
                    {
                    pushFollow(FOLLOW_constraints_in_fact2106);
                    constraints((ColumnDescr) d);
                    _fsp--;
                    if (failed) return d;

                    }
                    break;

            }

            endLoc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact2119); if (failed) return d;
            if ( backtracking==0 ) {
              
              		        if( endLoc.getType() == RIGHT_PAREN ) {
              				d.setEndLocation( offset(endLoc.getLine()), endLoc.getCharPositionInLine() );	
              				d.setEndCharacter( ((CommonToken)endLoc).getStopIndex() );
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:930:1: constraints[ColumnDescr column] : ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )* ;
    public void constraints(ColumnDescr column) throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:931:4: ( ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:931:4: ( ( constraint[column] )=> constraint[column] | predicate[column] ) ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:931:4: ( ( constraint[column] )=> constraint[column] | predicate[column] )
            int alt31=2;
            int LA31_0 = input.LA(1);
            if ( (LA31_0==ID) ) {
                alt31=1;
            }
            else if ( (LA31_0==LEFT_PAREN) ) {
                alt31=2;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("931:4: ( ( constraint[column] )=> constraint[column] | predicate[column] )", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:931:5: ( constraint[column] )=> constraint[column]
                    {
                    pushFollow(FOLLOW_constraint_in_constraints2140);
                    constraint(column);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:931:24: predicate[column]
                    {
                    pushFollow(FOLLOW_predicate_in_constraints2143);
                    predicate(column);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:932:3: ( ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                if ( (LA33_0==36) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:932:5: ( ',' ( ( constraint[column] )=> constraint[column] | predicate[column] ) )=> ',' ( ( constraint[column] )=> constraint[column] | predicate[column] )
            	    {
            	    match(input,36,FOLLOW_36_in_constraints2151); if (failed) return ;
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:932:9: ( ( constraint[column] )=> constraint[column] | predicate[column] )
            	    int alt32=2;
            	    int LA32_0 = input.LA(1);
            	    if ( (LA32_0==ID) ) {
            	        alt32=1;
            	    }
            	    else if ( (LA32_0==LEFT_PAREN) ) {
            	        alt32=2;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("932:9: ( ( constraint[column] )=> constraint[column] | predicate[column] )", 32, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt32) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:932:10: ( constraint[column] )=> constraint[column]
            	            {
            	            pushFollow(FOLLOW_constraint_in_constraints2154);
            	            constraint(column);
            	            _fsp--;
            	            if (failed) return ;

            	            }
            	            break;
            	        case 2 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:932:29: predicate[column]
            	            {
            	            pushFollow(FOLLOW_predicate_in_constraints2157);
            	            predicate(column);
            	            _fsp--;
            	            if (failed) return ;

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
        return ;
    }
    // $ANTLR end constraints


    // $ANTLR start constraint
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:935:1: constraint[ColumnDescr column] : ( ( ID ':' )=>fb= ID ':' )? f= ID ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )? ;
    public void constraint(ColumnDescr column) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token con=null;
        RestrictionDescr rd = null;


        
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:941:3: ( ( ( ID ':' )=>fb= ID ':' )? f= ID ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:941:3: ( ( ID ':' )=>fb= ID ':' )? f= ID ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:941:3: ( ( ID ':' )=>fb= ID ':' )?
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:941:5: ( ID ':' )=>fb= ID ':'
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint2186); if (failed) return ;
                    match(input,39,FOLLOW_39_in_constraint2188); if (failed) return ;
                    if ( backtracking==0 ) {
                       
                      			fbd = new FieldBindingDescr();
                      			fbd.setIdentifier( fb.getText() );
                      			fbd.setLocation( offset(fb.getLine()), fb.getCharPositionInLine() );
                      			fbd.setStartCharacter( ((CommonToken)fb).getStartIndex() );
                      			column.addDescr( fbd );
                      
                      		    
                    }

                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint2209); if (failed) return ;
            if ( backtracking==0 ) {
              
              			if ( fb != null ) {
              			    fbd.setFieldName( f.getText() );
               			    fbd.setEndCharacter( ((CommonToken)f).getStopIndex() );
              			} 
              			fc = new FieldConstraintDescr(f.getText());
              			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:960:3: ( ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* ) | ( '->' predicate[column] )=> '->' predicate[column] )?
            int alt36=3;
            int LA36_0 = input.LA(1);
            if ( ((LA36_0>=61 && LA36_0<=69)) ) {
                alt36=1;
            }
            else if ( (LA36_0==60) ) {
                alt36=2;
            }
            switch (alt36) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:961:4: ( ( constraint_expression ( ( ('&'|'|') constraint_expression )=> ('&'|'|') constraint_expression )* ) )=> (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:961:4: (rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )* )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:961:6: rd= constraint_expression ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                    {
                    pushFollow(FOLLOW_constraint_expression_in_constraint2227);
                    rd=constraint_expression();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                      					fc.addRestriction(rd);
                      					column.addDescr(fc);
                      				
                    }
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:966:5: ( ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);
                        if ( ((LA35_0>=58 && LA35_0<=59)) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:967:6: ( ('&'|'|') constraint_expression )=>con= ('&'|'|')rd= constraint_expression
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
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint2249);    throw mse;
                    	    }

                    	    if ( backtracking==0 ) {
                    	      
                    	      						if (con.getText().equals("&") ) {								
                    	      							fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	      						} else {
                    	      							fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	      						}							
                    	      					
                    	    }
                    	    pushFollow(FOLLOW_constraint_expression_in_constraint2268);
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


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:982:4: ( '->' predicate[column] )=> '->' predicate[column]
                    {
                    match(input,60,FOLLOW_60_in_constraint2296); if (failed) return ;
                    pushFollow(FOLLOW_predicate_in_constraint2298);
                    predicate(column);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:986:1: constraint_expression returns [RestrictionDescr rd] : op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) ;
    public RestrictionDescr constraint_expression() throws RecognitionException {   
        RestrictionDescr rd = null;

        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:988:3: (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:988:3: op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
            {
            op=(Token)input.LT(1);
            if ( (input.LA(1)>=61 && input.LA(1)<=69) ) {
                input.consume();
                errorRecovery=false;failed=false;
            }
            else {
                if (backtracking>0) {failed=true; return rd;}
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint_expression2335);    throw mse;
            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:998:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )
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
                        new NoViableAltException("998:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )", 37, 1, input);

                    throw nvae;
                }
                break;
            case STRING:
            case BOOL:
            case INT:
            case FLOAT:
            case 70:
                alt37=3;
                break;
            case LEFT_PAREN:
                alt37=4;
                break;
            default:
                if (backtracking>0) {failed=true; return rd;}
                NoViableAltException nvae =
                    new NoViableAltException("998:3: ( ( ID )=>bvc= ID | ( enum_constraint )=>lc= enum_constraint | ( literal_constraint )=>lc= literal_constraint | rvc= retval_constraint )", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:998:5: ( ID )=>bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint_expression2402); if (failed) return rd;
                    if ( backtracking==0 ) {
                      
                      				rd = new VariableRestrictionDescr(op.getText(), bvc.getText());
                      			
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1003:4: ( enum_constraint )=>lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_constraint_expression2418);
                    lc=enum_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc, true);
                      			
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1008:4: ( literal_constraint )=>lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_constraint_expression2441);
                    lc=literal_constraint();
                    _fsp--;
                    if (failed) return rd;
                    if ( backtracking==0 ) {
                       
                      				rd  = new LiteralRestrictionDescr(op.getText(), lc);
                      			
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1012:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_constraint_expression2455);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1019:1: literal_constraint returns [String text] : ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' ) ;
    public String literal_constraint() throws RecognitionException {   
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1023:4: ( ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1023:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1023:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' )
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
            case 70:
                alt38=5;
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("1023:4: ( ( STRING )=>t= STRING | ( INT )=>t= INT | ( FLOAT )=>t= FLOAT | ( BOOL )=>t= BOOL | t= 'null' )", 38, 0, input);

                throw nvae;
            }

            switch (alt38) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1023:6: ( STRING )=>t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint2494); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1024:5: ( INT )=>t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint2505); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1025:5: ( FLOAT )=>t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint2518); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:5: ( BOOL )=>t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint2529); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1027:5: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,70,FOLLOW_70_in_literal_constraint2541); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1031:1: enum_constraint returns [String text] : (cls= ID '.' en= ID ) ;
    public String enum_constraint() throws RecognitionException {   
        String text = null;

        Token cls=null;
        Token en=null;

        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1035:4: ( (cls= ID '.' en= ID ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1035:4: (cls= ID '.' en= ID )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1035:4: (cls= ID '.' en= ID )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1035:5: cls= ID '.' en= ID
            {
            cls=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2572); if (failed) return text;
            match(input,33,FOLLOW_33_in_enum_constraint2574); if (failed) return text;
            en=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2578); if (failed) return text;

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1039:1: predicate[ColumnDescr column] : text= paren_chunk ;
    public void predicate(ColumnDescr column) throws RecognitionException {   
        String text = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1041:3: (text= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1041:3: text= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_predicate2600);
            text=paren_chunk();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              
              		        String body = text.substring(1, text.length()-1);
              			PredicateDescr d = new PredicateDescr( body );
              			column.addDescr( d );
              		
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1049:1: paren_chunk returns [String text] : loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN ;
    public String paren_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1055:10: (loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1055:10: loc= LEFT_PAREN ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )* loc= RIGHT_PAREN
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk2647); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
               
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1065:3: ( (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN) | ( paren_chunk )=>chunk= paren_chunk )*
            loop39:
            do {
                int alt39=3;
                int LA39_0 = input.LA(1);
                if ( ((LA39_0>=ID && LA39_0<=INT)||(LA39_0>=FLOAT && LA39_0<=77)) ) {
                    alt39=1;
                }
                else if ( (LA39_0==LEFT_PAREN) ) {
                    alt39=2;
                }


                switch (alt39) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1066:4: (~ (LEFT_PAREN|RIGHT_PAREN))=>~ (LEFT_PAREN|RIGHT_PAREN)
            	    {
            	    if ( (input.LA(1)>=ID && input.LA(1)<=INT)||(input.LA(1)>=FLOAT && input.LA(1)<=77) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_paren_chunk2663);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1071:4: ( paren_chunk )=>chunk= paren_chunk
            	    {
            	    pushFollow(FOLLOW_paren_chunk_in_paren_chunk2687);
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
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk2723); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1090:1: curly_chunk returns [String text] : loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk )=>chunk= curly_chunk )* loc= RIGHT_CURLY ;
    public String curly_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1096:3: (loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk )=>chunk= curly_chunk )* loc= RIGHT_CURLY )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1096:3: loc= LEFT_CURLY ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk )=>chunk= curly_chunk )* loc= RIGHT_CURLY
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_CURLY,FOLLOW_LEFT_CURLY_in_curly_chunk2773); if (failed) return text;
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              		    
              		    buf.append( loc.getText() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1104:3: ( (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY) | ( curly_chunk )=>chunk= curly_chunk )*
            loop40:
            do {
                int alt40=3;
                int LA40_0 = input.LA(1);
                if ( ((LA40_0>=ID && LA40_0<=FLOAT)||(LA40_0>=LEFT_SQUARE && LA40_0<=77)) ) {
                    alt40=1;
                }
                else if ( (LA40_0==LEFT_CURLY) ) {
                    alt40=2;
                }


                switch (alt40) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1105:4: (~ (LEFT_CURLY|RIGHT_CURLY))=>~ (LEFT_CURLY|RIGHT_CURLY)
            	    {
            	    if ( (input.LA(1)>=ID && input.LA(1)<=FLOAT)||(input.LA(1)>=LEFT_SQUARE && input.LA(1)<=77) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_curly_chunk2789);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1110:4: ( curly_chunk )=>chunk= curly_chunk
            	    {
            	    pushFollow(FOLLOW_curly_chunk_in_curly_chunk2813);
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
            match(input,RIGHT_CURLY,FOLLOW_RIGHT_CURLY_in_curly_chunk2849); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1129:1: square_chunk returns [String text] : loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE ;
    public String square_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;
        String chunk = null;


        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1135:10: (loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1135:10: loc= LEFT_SQUARE ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )* loc= RIGHT_SQUARE
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_square_chunk2910); if (failed) return text;
            if ( backtracking==0 ) {
              
              		    buf.append( loc.getText());
               
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1145:3: ( (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE) | ( square_chunk )=>chunk= square_chunk )*
            loop41:
            do {
                int alt41=3;
                int LA41_0 = input.LA(1);
                if ( ((LA41_0>=ID && LA41_0<=RIGHT_CURLY)||(LA41_0>=END && LA41_0<=77)) ) {
                    alt41=1;
                }
                else if ( (LA41_0==LEFT_SQUARE) ) {
                    alt41=2;
                }


                switch (alt41) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1146:4: (~ (LEFT_SQUARE|RIGHT_SQUARE))=>~ (LEFT_SQUARE|RIGHT_SQUARE)
            	    {
            	    if ( (input.LA(1)>=ID && input.LA(1)<=RIGHT_CURLY)||(input.LA(1)>=END && input.LA(1)<=77) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return text;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_square_chunk2926);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
            	    }

            	    }
            	    break;
            	case 2 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1151:4: ( square_chunk )=>chunk= square_chunk
            	    {
            	    pushFollow(FOLLOW_square_chunk_in_square_chunk2950);
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
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_square_chunk2986); if (failed) return text;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1170:1: retval_constraint returns [String text] : c= paren_chunk ;
    public String retval_constraint() throws RecognitionException {   
        String text = null;

        String c = null;


        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1175:3: (c= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1175:3: c= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint3031);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1183:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )* ;
    public BaseDescr lhs_or() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		OrDescr or = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1189:3: (left= lhs_and ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1189:3: left= lhs_and ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )*
            {
            pushFollow(FOLLOW_lhs_and_in_lhs_or3064);
            left=lhs_and();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              d = left; 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1190:3: ( ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);
                if ( ((LA42_0>=56 && LA42_0<=57)) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1190:5: ( ('or'|'||') lhs_and )=> ('or'|'||')right= lhs_and
            	    {
            	    if ( (input.LA(1)>=56 && input.LA(1)<=57) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or3073);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_and_in_lhs_or3083);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1204:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )* ;
    public BaseDescr lhs_and() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        		AndDescr and = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1210:3: (left= lhs_unary ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1210:3: left= lhs_unary ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )*
            {
            pushFollow(FOLLOW_lhs_unary_in_lhs_and3119);
            left=lhs_unary();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
               d = left; 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1211:3: ( ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                if ( ((LA43_0>=71 && LA43_0<=72)) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1211:5: ( ('and'|'&&') lhs_unary )=> ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=71 && input.LA(1)<=72) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return d;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and3128);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_unary_in_lhs_and3138);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1225:1: lhs_unary returns [BaseDescr d] : ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs ')' ) opt_semicolon ;
    public BaseDescr lhs_unary() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr u = null;

        FromDescr fm = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:4: ( ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs ')' ) opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs ')' ) opt_semicolon
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs ')' )
            int alt45=6;
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
            case 76:
                alt45=5;
                break;
            case LEFT_PAREN:
                alt45=6;
                break;
            default:
                if (backtracking>0) {failed=true; return d;}
                NoViableAltException nvae =
                    new NoViableAltException("1229:4: ( ( lhs_exist )=>u= lhs_exist | ( lhs_not )=>u= lhs_not | ( lhs_eval )=>u= lhs_eval | ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )? | ( lhs_forall )=>u= lhs_forall | '(' u= lhs ')' )", 45, 0, input);

                throw nvae;
            }

            switch (alt45) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1229:6: ( lhs_exist )=>u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary3175);
                    u=lhs_exist();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1230:5: ( lhs_not )=>u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary3183);
                    u=lhs_not();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1231:5: ( lhs_eval )=>u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary3191);
                    u=lhs_eval();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1232:5: ( lhs_column ( ( ( from_statement ) )=> ( from_statement ) | ( ( accumulate_statement ) )=> ( accumulate_statement ) | ( ( collect_statement ) )=> ( collect_statement ) )? )=>u= lhs_column ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )?
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_unary3199);
                    u=lhs_column();
                    _fsp--;
                    if (failed) return d;
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1232:18: ( ( ( from_statement ) )=> (fm= from_statement ) | ( ( accumulate_statement ) )=> (ac= accumulate_statement ) | ( ( collect_statement ) )=> (cs= collect_statement ) )?
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
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1233:14: ( ( from_statement ) )=> (fm= from_statement )
                            {
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1233:14: (fm= from_statement )
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1233:15: fm= from_statement
                            {
                            pushFollow(FOLLOW_from_statement_in_lhs_unary3219);
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
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1234:14: ( ( accumulate_statement ) )=> (ac= accumulate_statement )
                            {
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1234:14: (ac= accumulate_statement )
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1234:15: ac= accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_lhs_unary3241);
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
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:14: ( ( collect_statement ) )=> (cs= collect_statement )
                            {
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:14: (cs= collect_statement )
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1235:15: cs= collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_lhs_unary3262);
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
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1237:5: ( lhs_forall )=>u= lhs_forall
                    {
                    pushFollow(FOLLOW_lhs_forall_in_lhs_unary3287);
                    u=lhs_forall();
                    _fsp--;
                    if (failed) return d;

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1238:5: '(' u= lhs ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary3295); if (failed) return d;
                    pushFollow(FOLLOW_lhs_in_lhs_unary3299);
                    u=lhs();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary3301); if (failed) return d;

                    }
                    break;

            }

            if ( backtracking==0 ) {
               d = u; 
            }
            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary3311);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1243:1: lhs_exist returns [BaseDescr d] : loc= 'exists' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) ;
    public BaseDescr lhs_exist() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1247:4: (loc= 'exists' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1247:4: loc= 'exists' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,73,FOLLOW_73_in_lhs_exist3335); if (failed) return d;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1247:17: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
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
                    new NoViableAltException("1247:17: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )", 46, 0, input);

                throw nvae;
            }
            switch (alt46) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1247:18: ( '(' lhs_or ')' )=> '(' column= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist3338); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_exist3342);
                    column=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist3344); if (failed) return d;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1247:42: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_exist3350);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1254:1: lhs_not returns [NotDescr d] : loc= 'not' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1258:4: (loc= 'not' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1258:4: loc= 'not' ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,74,FOLLOW_74_in_lhs_not3380); if (failed) return d;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1258:14: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )
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
                    new NoViableAltException("1258:14: ( ( '(' lhs_or ')' )=> '(' column= lhs_or ')' | column= lhs_column )", 47, 0, input);

                throw nvae;
            }
            switch (alt47) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1258:15: ( '(' lhs_or ')' )=> '(' column= lhs_or ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not3383); if (failed) return d;
                    pushFollow(FOLLOW_lhs_or_in_lhs_not3387);
                    column=lhs_or();
                    _fsp--;
                    if (failed) return d;
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not3390); if (failed) return d;

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1258:40: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_not3396);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1265:1: lhs_eval returns [BaseDescr d] : loc= 'eval' c= paren_chunk ;
    public BaseDescr lhs_eval() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        String c = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1269:4: (loc= 'eval' c= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1269:4: loc= 'eval' c= paren_chunk
            {
            loc=(Token)input.LT(1);
            match(input,75,FOLLOW_75_in_lhs_eval3424); if (failed) return d;
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval3428);
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


    // $ANTLR start lhs_forall
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1277:1: lhs_forall returns [ForallDescr d] : loc= 'forall' '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ ')' ;
    public ForallDescr lhs_forall() throws RecognitionException {   
        ForallDescr d = null;

        Token loc=null;
        BaseDescr base = null;

        BaseDescr column = null;


        
        		d = factory.createForall();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1281:4: (loc= 'forall' '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1281:4: loc= 'forall' '(' base= lhs_column ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+ ')'
            {
            loc=(Token)input.LT(1);
            match(input,76,FOLLOW_76_in_lhs_forall3456); if (failed) return d;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_forall3458); if (failed) return d;
            pushFollow(FOLLOW_lhs_column_in_lhs_forall3462);
            base=lhs_column();
            _fsp--;
            if (failed) return d;
            if ( backtracking==0 ) {
              
              		        // adding the base column
              		        d.addDescr( base );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              		
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1287:3: ( ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column )+
            int cnt49=0;
            loop49:
            do {
                int alt49=2;
                int LA49_0 = input.LA(1);
                if ( (LA49_0==ID||LA49_0==36) ) {
                    alt49=1;
                }


                switch (alt49) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1287:5: ( ( ( ',' )=> ',' )? lhs_column )=> ( ( ',' )=> ',' )? column= lhs_column
            	    {
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1287:5: ( ( ',' )=> ',' )?
            	    int alt48=2;
            	    int LA48_0 = input.LA(1);
            	    if ( (LA48_0==36) ) {
            	        alt48=1;
            	    }
            	    switch (alt48) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1287:6: ( ',' )=> ','
            	            {
            	            match(input,36,FOLLOW_36_in_lhs_forall3476); if (failed) return d;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_lhs_column_in_lhs_forall3482);
            	    column=lhs_column();
            	    _fsp--;
            	    if (failed) return d;
            	    if ( backtracking==0 ) {
            	      
            	      		        // adding additional columns
            	      			d.addDescr( column );
            	      		
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt49 >= 1 ) break loop49;
            	    if (backtracking>0) {failed=true; return d;}
                        EarlyExitException eee =
                            new EarlyExitException(49, input);
                        throw eee;
                }
                cnt49++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_forall3495); if (failed) return d;

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1296:1: dotted_name returns [String name] : id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '[' ']' )=> '[' ']' )* ;
    public String dotted_name() throws RecognitionException {   
        String name = null;

        Token id=null;

        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1301:3: (id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '[' ']' )=> '[' ']' )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1301:3: id= ID ( ( '.' ID )=> '.' id= ID )* ( ( '[' ']' )=> '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name3521); if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1301:32: ( ( '.' ID )=> '.' id= ID )*
            loop50:
            do {
                int alt50=2;
                int LA50_0 = input.LA(1);
                if ( (LA50_0==33) ) {
                    alt50=1;
                }


                switch (alt50) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1301:34: ( '.' ID )=> '.' id= ID
            	    {
            	    match(input,33,FOLLOW_33_in_dotted_name3527); if (failed) return name;
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name3531); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "." + id.getText(); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop50;
                }
            } while (true);

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1301:85: ( ( '[' ']' )=> '[' ']' )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);
                if ( (LA51_0==LEFT_SQUARE) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1301:87: ( '[' ']' )=> '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_dotted_name3540); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_dotted_name3542); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "[]";
            	    }

            	    }
            	    break;

            	default :
            	    break loop51;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1304:1: argument returns [String name] : id= ID ( ( '[' ']' )=> '[' ']' )* ;
    public String argument() throws RecognitionException {   
        String name = null;

        Token id=null;

        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1309:3: (id= ID ( ( '[' ']' )=> '[' ']' )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1309:3: id= ID ( ( '[' ']' )=> '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument3572); if (failed) return name;
            if ( backtracking==0 ) {
               name=id.getText(); 
            }
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1309:32: ( ( '[' ']' )=> '[' ']' )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);
                if ( (LA52_0==LEFT_SQUARE) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1309:34: ( '[' ']' )=> '[' ']'
            	    {
            	    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_argument3578); if (failed) return name;
            	    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_argument3580); if (failed) return name;
            	    if ( backtracking==0 ) {
            	       name = name + "[]";
            	    }

            	    }
            	    break;

            	default :
            	    break loop52;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1312:1: rhs_chunk[RuleDescr rule] : start= 'then' ( (~ END )=>~ END )* loc= END ;
    public void rhs_chunk(RuleDescr rule) throws RecognitionException {   
        Token start=null;
        Token loc=null;

        
                   StringBuffer buf = null;
                   Integer channel = null;
                
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1318:10: (start= 'then' ( (~ END )=>~ END )* loc= END )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1318:10: start= 'then' ( (~ END )=>~ END )* loc= END
            {
            if ( backtracking==0 ) {
              
              	            channel = ((SwitchingCommonTokenStream)input).getTokenTypeChannel( WS ); 
              		    ((SwitchingCommonTokenStream)input).setTokenTypeChannel( WS, Token.DEFAULT_CHANNEL );
              		    buf = new StringBuffer();
              	        
            }
            start=(Token)input.LT(1);
            match(input,THEN,FOLLOW_THEN_in_rhs_chunk3624); if (failed) return ;
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1324:3: ( (~ END )=>~ END )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);
                if ( ((LA53_0>=ID && LA53_0<=RIGHT_SQUARE)||(LA53_0>=THEN && LA53_0<=77)) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1325:6: (~ END )=>~ END
            	    {
            	    if ( (input.LA(1)>=ID && input.LA(1)<=RIGHT_SQUARE)||(input.LA(1)>=THEN && input.LA(1)<=77) ) {
            	        input.consume();
            	        errorRecovery=false;failed=false;
            	    }
            	    else {
            	        if (backtracking>0) {failed=true; return ;}
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_rhs_chunk3636);    throw mse;
            	    }

            	    if ( backtracking==0 ) {
            	      
            	      			    buf.append( input.LT(-1).getText() );
            	      			  
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
            match(input,END,FOLLOW_END_in_rhs_chunk3673); if (failed) return ;
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


    // $ANTLR start word
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1356:1: word returns [String word] : ( ( ID )=>id= ID | ( 'import' )=> 'import' | ( 'use' )=> 'use' | ( RULE )=> RULE | ( 'query' )=> 'query' | ( 'salience' )=> 'salience' | ( 'no-loop' )=> 'no-loop' | ( WHEN )=> WHEN | ( THEN )=> THEN | ( END )=> END | str= STRING );
    public String word() throws RecognitionException {   
        String word = null;

        Token id=null;
        Token str=null;

        
        		word = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1360:4: ( ( ID )=>id= ID | ( 'import' )=> 'import' | ( 'use' )=> 'use' | ( RULE )=> RULE | ( 'query' )=> 'query' | ( 'salience' )=> 'salience' | ( 'no-loop' )=> 'no-loop' | ( WHEN )=> WHEN | ( THEN )=> THEN | ( END )=> END | str= STRING )
            int alt54=11;
            switch ( input.LA(1) ) {
            case ID:
                alt54=1;
                break;
            case 31:
                alt54=2;
                break;
            case 77:
                alt54=3;
                break;
            case RULE:
                alt54=4;
                break;
            case 37:
                alt54=5;
                break;
            case 44:
                alt54=6;
                break;
            case 45:
                alt54=7;
                break;
            case WHEN:
                alt54=8;
                break;
            case THEN:
                alt54=9;
                break;
            case END:
                alt54=10;
                break;
            case STRING:
                alt54=11;
                break;
            default:
                if (backtracking>0) {failed=true; return word;}
                NoViableAltException nvae =
                    new NoViableAltException("1356:1: word returns [String word] : ( ( ID )=>id= ID | ( 'import' )=> 'import' | ( 'use' )=> 'use' | ( RULE )=> RULE | ( 'query' )=> 'query' | ( 'salience' )=> 'salience' | ( 'no-loop' )=> 'no-loop' | ( WHEN )=> WHEN | ( THEN )=> THEN | ( END )=> END | str= STRING );", 54, 0, input);

                throw nvae;
            }

            switch (alt54) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1360:4: ( ID )=>id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word3715); if (failed) return word;
                    if ( backtracking==0 ) {
                       word=id.getText(); 
                    }

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1361:4: ( 'import' )=> 'import'
                    {
                    match(input,31,FOLLOW_31_in_word3727); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="import"; 
                    }

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1362:4: ( 'use' )=> 'use'
                    {
                    match(input,77,FOLLOW_77_in_word3736); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="use"; 
                    }

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1363:4: ( RULE )=> RULE
                    {
                    match(input,RULE,FOLLOW_RULE_in_word3748); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="rule"; 
                    }

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1364:4: ( 'query' )=> 'query'
                    {
                    match(input,37,FOLLOW_37_in_word3761); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="query"; 
                    }

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1365:4: ( 'salience' )=> 'salience'
                    {
                    match(input,44,FOLLOW_44_in_word3771); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="salience"; 
                    }

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1366:5: ( 'no-loop' )=> 'no-loop'
                    {
                    match(input,45,FOLLOW_45_in_word3779); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="no-loop"; 
                    }

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1367:4: ( WHEN )=> WHEN
                    {
                    match(input,WHEN,FOLLOW_WHEN_in_word3787); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="when"; 
                    }

                    }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1368:4: ( THEN )=> THEN
                    {
                    match(input,THEN,FOLLOW_THEN_in_word3800); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="then"; 
                    }

                    }
                    break;
                case 10 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1369:4: ( END )=> END
                    {
                    match(input,END,FOLLOW_END_in_word3813); if (failed) return word;
                    if ( backtracking==0 ) {
                       word="end"; 
                    }

                    }
                    break;
                case 11 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1370:4: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word3829); if (failed) return word;
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
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:4: ( ( ID paren_chunk ) )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:4: ( ID paren_chunk )
        {
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:4: ( ID paren_chunk )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:679:6: ID paren_chunk
        {
        match(input,ID,FOLLOW_ID_in_synpred351529); if (failed) return ;
        pushFollow(FOLLOW_paren_chunk_in_synpred351533);
        paren_chunk();
        _fsp--;
        if (failed) return ;

        }


        }
    }
    // $ANTLR end synpred35

    // $ANTLR start synpred38
    public void synpred38_fragment() throws RecognitionException {   
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:719:6: ( LEFT_PAREN )
        // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:719:8: LEFT_PAREN
        {
        match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_synpred381674); if (failed) return ;

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
        "\1\2\6\uffff\1\4\3\uffff\1\3\21\uffff\1\2\2\uffff\1\4",
        "",
        "\1\5",
        "",
        "\1\2\6\uffff\1\4\3\uffff\1\3\24\uffff\1\4"
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
            return "355:6: ( ( dotted_name )=>paramType= dotted_name )?";
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
        "\1\4\6\uffff\1\3\3\uffff\1\2\21\uffff\1\4\2\uffff\1\3",
        "\1\5",
        "",
        "",
        "\1\4\6\uffff\1\3\3\uffff\1\2\24\uffff\1\3"
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
            return "359:11: ( ( dotted_name )=>paramType= dotted_name )?";
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
    public static final BitSet FOLLOW_query_in_statement173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_package_statement202 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement206 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_import_statement225 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_import_name_in_import_statement229 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_function_import_statement247 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_function_import_statement249 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement253 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name287 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_33_in_import_name293 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_import_name297 = new BitSet(new long[]{0x0000000600000002L});
    public static final BitSet FOLLOW_34_in_import_name307 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_global331 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_global335 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_global339 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_global341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_function368 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_function373 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_function379 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function388 = new BitSet(new long[]{0x0000000000000810L});
    public static final BitSet FOLLOW_dotted_name_in_function398 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_argument_in_function404 = new BitSet(new long[]{0x0000001000000800L});
    public static final BitSet FOLLOW_36_in_function418 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_function423 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_argument_in_function429 = new BitSet(new long[]{0x0000001000000800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function453 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_curly_chunk_in_function459 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_query490 = new BitSet(new long[]{0x00003020800600F0L,0x0000000000002000L});
    public static final BitSet FOLLOW_word_in_query494 = new BitSet(new long[]{0x0000000000020410L,0x0000000000001E00L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query508 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_END_in_query523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_template549 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_template553 = new BitSet(new long[]{0x0000000020000010L});
    public static final BitSet FOLLOW_opt_semicolon_in_template555 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_template_slot_in_template570 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_END_in_template585 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot619 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_template_slot623 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_rule656 = new BitSet(new long[]{0x00003020800600F0L,0x0000000000002000L});
    public static final BitSet FOLLOW_word_in_rule660 = new BitSet(new long[]{0x0003FF1000040040L});
    public static final BitSet FOLLOW_rule_attributes_in_rule669 = new BitSet(new long[]{0x0000000000040040L});
    public static final BitSet FOLLOW_WHEN_in_rule678 = new BitSet(new long[]{0x0000008000040410L,0x0000000000001E00L});
    public static final BitSet FOLLOW_39_in_rule680 = new BitSet(new long[]{0x0000000000040410L,0x0000000000001E00L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule698 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_rhs_chunk_in_rule719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_rule_attributes740 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_rule_attributes742 = new BitSet(new long[]{0x0003FE1000000002L});
    public static final BitSet FOLLOW_36_in_rule_attributes751 = new BitSet(new long[]{0x0003FE0000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes756 = new BitSet(new long[]{0x0003FE1000000002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute845 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_effective_in_rule_attribute891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_date_expires_in_rule_attribute901 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enabled_in_rule_attribute935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_date_effective967 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_date_effective971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_date_expires1002 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_date_expires1006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_enabled1039 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_enabled1043 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_enabled1045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_salience1087 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_INT_in_salience1091 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_salience1093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_no_loop1128 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_no_loop1130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_no_loop1155 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1159 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_no_loop1161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_auto_focus1207 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_auto_focus1209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_auto_focus1234 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1238 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_auto_focus1240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_activation_group1282 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_activation_group1286 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_activation_group1288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_agenda_group1317 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1321 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_agenda_group1323 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_duration1355 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_INT_in_duration1359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1385 = new BitSet(new long[]{0x0000000000000412L,0x0000000000001E00L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_from_statement1482 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_from_source_in_from_statement1486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1529 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source1533 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_ID_in_from_source1566 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_expression_chain_in_from_source1589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_expression_chain1614 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_expression_chain1618 = new BitSet(new long[]{0x0000000200008402L});
    public static final BitSet FOLLOW_square_chunk_in_expression_chain1649 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expression_chain1682 = new BitSet(new long[]{0x0000000200000002L});
    public static final BitSet FOLLOW_expression_chain_in_expression_chain1702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_accumulate_statement1743 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_accumulate_statement1745 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement1755 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_accumulate_statement1759 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_accumulate_statement1761 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_accumulate_statement1770 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1774 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_accumulate_statement1776 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_53_in_accumulate_statement1785 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1789 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_accumulate_statement1791 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_54_in_accumulate_statement1800 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1804 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement1806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_collect_statement1849 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_collect_statement1851 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement1861 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_collect_statement1865 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement1867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding1902 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_fact_binding1912 = new BitSet(new long[]{0x0000000000000410L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding1916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression1948 = new BitSet(new long[]{0x0000000000000410L});
    public static final BitSet FOLLOW_fact_expression_in_paren_in_fact_expression1952 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression1955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression1966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression_in_paren1997 = new BitSet(new long[]{0x0000000000000410L});
    public static final BitSet FOLLOW_fact_expression_in_paren_in_fact_expression_in_paren2001 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression_in_paren2003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression_in_paren2014 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_set_in_fact_expression_in_paren2027 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_fact_in_fact_expression_in_paren2044 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_fact2083 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact2096 = new BitSet(new long[]{0x0000000000000C10L});
    public static final BitSet FOLLOW_constraints_in_fact2106 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact2119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints2140 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_predicate_in_constraints2143 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_36_in_constraints2151 = new BitSet(new long[]{0x0000000000000410L});
    public static final BitSet FOLLOW_constraint_in_constraints2154 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_predicate_in_constraints2157 = new BitSet(new long[]{0x0000001000000002L});
    public static final BitSet FOLLOW_ID_in_constraint2186 = new BitSet(new long[]{0x0000008000000000L});
    public static final BitSet FOLLOW_39_in_constraint2188 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_constraint2209 = new BitSet(new long[]{0xF000000000000002L,0x000000000000003FL});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2227 = new BitSet(new long[]{0x0C00000000000002L});
    public static final BitSet FOLLOW_set_in_constraint2249 = new BitSet(new long[]{0xE000000000000000L,0x000000000000003FL});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2268 = new BitSet(new long[]{0x0C00000000000002L});
    public static final BitSet FOLLOW_60_in_constraint2296 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_predicate_in_constraint2298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_constraint_expression2335 = new BitSet(new long[]{0x0000000000001790L,0x0000000000000040L});
    public static final BitSet FOLLOW_ID_in_constraint_expression2402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint_expression2418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint_expression2441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint_expression2455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint2494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint2505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2518 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_literal_constraint2541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2572 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_enum_constraint2574 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate2600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk2647 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_set_in_paren_chunk2663 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk2687 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk2723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_CURLY_in_curly_chunk2773 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_set_in_curly_chunk2789 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk2813 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_RIGHT_CURLY_in_curly_chunk2849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_square_chunk2910 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_set_in_square_chunk2926 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_square_chunk_in_square_chunk2950 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_square_chunk2986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint3031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3064 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or3073 = new BitSet(new long[]{0x0000000000000410L,0x0000000000001E00L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3083 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3119 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_set_in_lhs_and3128 = new BitSet(new long[]{0x0000000000000410L,0x0000000000001E00L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3138 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary3175 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary3183 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary3191 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary3199 = new BitSet(new long[]{0x0004000020000002L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary3219 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary3241 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary3262 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_lhs_forall_in_lhs_unary3287 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary3295 = new BitSet(new long[]{0x0000000000000410L,0x0000000000001E00L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary3299 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary3301 = new BitSet(new long[]{0x0000000020000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary3311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_lhs_exist3335 = new BitSet(new long[]{0x0000000000000410L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist3338 = new BitSet(new long[]{0x0000000000000410L,0x0000000000001E00L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_exist3342 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist3344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_lhs_not3380 = new BitSet(new long[]{0x0000000000000410L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not3383 = new BitSet(new long[]{0x0000000000000410L,0x0000000000001E00L});
    public static final BitSet FOLLOW_lhs_or_in_lhs_not3387 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not3390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_lhs_eval3424 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval3428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_lhs_forall3456 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_forall3458 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_forall3462 = new BitSet(new long[]{0x0000001000000010L});
    public static final BitSet FOLLOW_36_in_lhs_forall3476 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_forall3482 = new BitSet(new long[]{0x0000001000000810L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_forall3495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name3521 = new BitSet(new long[]{0x0000000200008002L});
    public static final BitSet FOLLOW_33_in_dotted_name3527 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_dotted_name3531 = new BitSet(new long[]{0x0000000200008002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_dotted_name3540 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_dotted_name3542 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_ID_in_argument3572 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_argument3578 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_argument3580 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_THEN_in_rhs_chunk3624 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_set_in_rhs_chunk3636 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x0000000000003FFFL});
    public static final BitSet FOLLOW_END_in_rhs_chunk3673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_word3715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_word3727 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_word3736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_in_word3748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_word3761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_word3771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_word3779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHEN_in_word3787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_THEN_in_word3800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_END_in_word3813 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word3829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_synpred351529 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_paren_chunk_in_synpred351533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_synpred381674 = new BitSet(new long[]{0x0000000000000002L});

}