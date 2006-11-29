// $ANTLR 3.0b5 D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g 2006-11-29 09:26:13

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

public class DRLParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "CURLY_CHUNK", "RHS", "INT", "BOOL", "STRING", "FLOAT", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "NO_CURLY", "LEFT_PAREN", "RIGHT_PAREN", "NO_PAREN", "MULTI_LINE_COMMENT", "IGNORE", "';'", "'package'", "'import'", "'function'", "'.'", "'.*'", "'global'", "','", "'query'", "'end'", "'template'", "'rule'", "'when'", "':'", "'attributes'", "'salience'", "'no-loop'", "'auto-focus'", "'activation-group'", "'agenda-group'", "'duration'", "'from'", "'['", "']'", "'accumulate'", "'init'", "'action'", "'result'", "'collect'", "'null'", "'=>'", "'}'", "'or'", "'||'", "'&'", "'|'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='", "'contains'", "'matches'", "'excludes'", "'->'", "'and'", "'&&'", "'exists'", "'not'", "'eval'", "'use'"
    };
    public static final int LEFT_PAREN=20;
    public static final int BOOL=8;
    public static final int HexDigit=14;
    public static final int CURLY_CHUNK=5;
    public static final int WS=12;
    public static final int STRING=9;
    public static final int FLOAT=10;
    public static final int NO_CURLY=19;
    public static final int UnicodeEscape=15;
    public static final int EscapeSequence=13;
    public static final int INT=7;
    public static final int EOF=-1;
    public static final int IGNORE=24;
    public static final int RHS=6;
    public static final int EOL=11;
    public static final int OctalEscape=16;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=17;
    public static final int MULTI_LINE_COMMENT=23;
    public static final int RIGHT_PAREN=21;
    public static final int NO_PAREN=22;
    public static final int ID=4;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=18;

        public DRLParser(TokenStream input) {
            super(input);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:281:1: opt_semicolon : ( ';' )? ;
    public void opt_semicolon() throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:282:4: ( ( ';' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:282:4: ( ';' )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:282:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);
            if ( (LA1_0==25) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:282:4: ';'
                    {
                    match(input,25,FOLLOW_25_in_opt_semicolon39); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:285:1: compilation_unit : prolog ( statement )* ;
    public void compilation_unit() throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:286:4: ( prolog ( statement )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:286:4: prolog ( statement )*
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit51);
            prolog();
            _fsp--;

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:287:3: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( ((LA2_0>=27 && LA2_0<=28)||LA2_0==31||LA2_0==33||(LA2_0>=35 && LA2_0<=36)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:287:5: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit58);
            	    statement();
            	    _fsp--;


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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:290:1: prolog : (name= package_statement )? ;
    public void prolog() throws RecognitionException {   
        String name = null;


        
        		String packageName = "";
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:294:4: ( (name= package_statement )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:294:4: (name= package_statement )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:294:4: (name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0==26) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:294:6: name= package_statement
                    {
                    pushFollow(FOLLOW_package_statement_in_prolog83);
                    name=package_statement();
                    _fsp--;

                     packageName = name; 

                    }
                    break;

            }

             
            			this.packageDescr = new PackageDescr( name ); 
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:300:1: statement : ( import_statement | function_import_statement | global | function | t= template | r= rule | q= query ) ;
    public void statement() throws RecognitionException {   
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:302:2: ( ( import_statement | function_import_statement | global | function | t= template | r= rule | q= query ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:302:2: ( import_statement | function_import_statement | global | function | t= template | r= rule | q= query )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:302:2: ( import_statement | function_import_statement | global | function | t= template | r= rule | q= query )
            int alt4=7;
            switch ( input.LA(1) ) {
            case 27:
                int LA4_1 = input.LA(2);
                if ( (LA4_1==28) ) {
                    alt4=2;
                }
                else if ( (LA4_1==ID) ) {
                    alt4=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("302:2: ( import_statement | function_import_statement | global | function | t= template | r= rule | q= query )", 4, 1, input);

                    throw nvae;
                }
                break;
            case 31:
                alt4=3;
                break;
            case 28:
                alt4=4;
                break;
            case 35:
                alt4=5;
                break;
            case 36:
                alt4=6;
                break;
            case 33:
                alt4=7;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("302:2: ( import_statement | function_import_statement | global | function | t= template | r= rule | q= query )", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:302:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement107);
                    import_statement();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:303:10: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement118);
                    function_import_statement();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:304:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement123);
                    global();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:305:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement128);
                    function();
                    _fsp--;


                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:306:10: t= template
                    {
                    pushFollow(FOLLOW_template_in_statement141);
                    t=template();
                    _fsp--;

                    this.packageDescr.addFactTemplate( t ); 

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:4: r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement150);
                    r=rule();
                    _fsp--;

                    this.packageDescr.addRule( r ); 

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:308:4: q= query
                    {
                    pushFollow(FOLLOW_query_in_statement160);
                    q=query();
                    _fsp--;

                    this.packageDescr.addRule( q ); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:312:1: package_statement returns [String packageName] : 'package' name= dotted_name opt_semicolon ;
    public String package_statement() throws RecognitionException {   
        String packageName = null;

        String name = null;


        
        		packageName = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:317:3: ( 'package' name= dotted_name opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:317:3: 'package' name= dotted_name opt_semicolon
            {
            match(input,26,FOLLOW_26_in_package_statement188); 
            pushFollow(FOLLOW_dotted_name_in_package_statement192);
            name=dotted_name();
            _fsp--;

            pushFollow(FOLLOW_opt_semicolon_in_package_statement194);
            opt_semicolon();
            _fsp--;

            
            			packageName = name;
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:324:1: import_statement : 'import' name= import_name opt_semicolon ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:325:4: ( 'import' name= import_name opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:325:4: 'import' name= import_name opt_semicolon
            {
            match(input,27,FOLLOW_27_in_import_statement211); 
            pushFollow(FOLLOW_import_name_in_import_statement215);
            name=import_name();
            _fsp--;

            pushFollow(FOLLOW_opt_semicolon_in_import_statement217);
            opt_semicolon();
            _fsp--;

            
            			if (packageDescr != null) 
            				packageDescr.addImport( name );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:332:1: function_import_statement : 'import' 'function' name= import_name opt_semicolon ;
    public void function_import_statement() throws RecognitionException {   
        String name = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:333:4: ( 'import' 'function' name= import_name opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:333:4: 'import' 'function' name= import_name opt_semicolon
            {
            match(input,27,FOLLOW_27_in_function_import_statement233); 
            match(input,28,FOLLOW_28_in_function_import_statement235); 
            pushFollow(FOLLOW_import_name_in_function_import_statement239);
            name=import_name();
            _fsp--;

            pushFollow(FOLLOW_opt_semicolon_in_function_import_statement241);
            opt_semicolon();
            _fsp--;

            
            			if (packageDescr != null) 
            				packageDescr.addFunctionImport( name );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:341:1: import_name returns [String name] : id= ID ( '.' id= ID )* (star= '.*' )? ;
    public String import_name() throws RecognitionException {   
        String name = null;

        Token id=null;
        Token star=null;

        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:3: (id= ID ( '.' id= ID )* (star= '.*' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:3: id= ID ( '.' id= ID )* (star= '.*' )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name273); 
             name=id.getText(); 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:32: ( '.' id= ID )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);
                if ( (LA5_0==29) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:34: '.' id= ID
            	    {
            	    match(input,29,FOLLOW_29_in_import_name279); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_import_name283); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:85: (star= '.*' )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0==30) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:86: star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,30,FOLLOW_30_in_import_name293); 
                     name = name + star.getText(); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:350:1: global : 'global' type= dotted_name id= ID opt_semicolon ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;


        
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:354:3: ( 'global' type= dotted_name id= ID opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:354:3: 'global' type= dotted_name id= ID opt_semicolon
            {
            match(input,31,FOLLOW_31_in_global317); 
            pushFollow(FOLLOW_dotted_name_in_global321);
            type=dotted_name();
            _fsp--;

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global325); 
            pushFollow(FOLLOW_opt_semicolon_in_global327);
            opt_semicolon();
            _fsp--;

            
            			packageDescr.addGlobal( id.getText(), type );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:361:1: function : loc= 'function' (retType= dotted_name )? name= ID '(' ( (paramType= dotted_name )? paramName= argument ( ',' (paramType= dotted_name )? paramName= argument )* )? ')' body= CURLY_CHUNK ;
    public void function() throws RecognitionException {   
        Token loc=null;
        Token name=null;
        Token body=null;
        String retType = null;

        String paramType = null;

        String paramName = null;


        
        		FunctionDescr f = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:366:3: (loc= 'function' (retType= dotted_name )? name= ID '(' ( (paramType= dotted_name )? paramName= argument ( ',' (paramType= dotted_name )? paramName= argument )* )? ')' body= CURLY_CHUNK )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:366:3: loc= 'function' (retType= dotted_name )? name= ID '(' ( (paramType= dotted_name )? paramName= argument ( ',' (paramType= dotted_name )? paramName= argument )* )? ')' body= CURLY_CHUNK
            {
            loc=(Token)input.LT(1);
            match(input,28,FOLLOW_28_in_function354); 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:366:18: (retType= dotted_name )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( (LA7_0==ID) ) {
                int LA7_1 = input.LA(2);
                if ( (LA7_1==ID||LA7_1==29||LA7_1==47) ) {
                    alt7=1;
                }
            }
            switch (alt7) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:366:19: retType= dotted_name
                    {
                    pushFollow(FOLLOW_dotted_name_in_function359);
                    retType=dotted_name();
                    _fsp--;


                    }
                    break;

            }

            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function365); 
            
            			//System.err.println( "function :: " + name.getText() );
            			f = new FunctionDescr( name.getText(), retType );
            			f.setLocation(offset(loc.getLine()), loc.getCharPositionInLine());
            		
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function374); 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:373:4: ( (paramType= dotted_name )? paramName= argument ( ',' (paramType= dotted_name )? paramName= argument )* )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( (LA11_0==ID) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:373:6: (paramType= dotted_name )? paramName= argument ( ',' (paramType= dotted_name )? paramName= argument )*
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:373:6: (paramType= dotted_name )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:373:7: paramType= dotted_name
                            {
                            pushFollow(FOLLOW_dotted_name_in_function384);
                            paramType=dotted_name();
                            _fsp--;


                            }
                            break;

                    }

                    pushFollow(FOLLOW_argument_in_function390);
                    paramName=argument();
                    _fsp--;

                    
                    					f.addParameter( paramType, paramName );
                    				
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:377:5: ( ',' (paramType= dotted_name )? paramName= argument )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);
                        if ( (LA10_0==32) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:377:7: ',' (paramType= dotted_name )? paramName= argument
                    	    {
                    	    match(input,32,FOLLOW_32_in_function404); 
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:377:11: (paramType= dotted_name )?
                    	    int alt9=2;
                    	    alt9 = dfa9.predict(input);
                    	    switch (alt9) {
                    	        case 1 :
                    	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:377:12: paramType= dotted_name
                    	            {
                    	            pushFollow(FOLLOW_dotted_name_in_function409);
                    	            paramType=dotted_name();
                    	            _fsp--;


                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_argument_in_function415);
                    	    paramName=argument();
                    	    _fsp--;

                    	    
                    	    						f.addParameter( paramType, paramName );
                    	    					

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function439); 
            body=(Token)input.LT(1);
            match(input,CURLY_CHUNK,FOLLOW_CURLY_CHUNK_in_function445); 
            
            			//strip out '{','}'
            			String bodys = body.getText();
            			bodys = bodys.substring(1,bodys.length()-1);
            			f.setText( bodys );
            
            			packageDescr.addFunction( f );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:397:1: query returns [QueryDescr query] : loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end' ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query = null;

        Token loc=null;
        String queryName = null;


        
        		query = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:402:3: (loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:402:3: loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end'
            {
            loc=(Token)input.LT(1);
            match(input,33,FOLLOW_33_in_query476); 
            pushFollow(FOLLOW_word_in_query480);
            queryName=word();
            _fsp--;

             
            			query = new QueryDescr( queryName, null ); 
            			query.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            			AndDescr lhs = new AndDescr(); query.setLhs( lhs ); 
            			lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:409:3: ( normal_lhs_block[lhs] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:4: normal_lhs_block[lhs]
            {
            pushFollow(FOLLOW_normal_lhs_block_in_query494);
            normal_lhs_block(lhs);
            _fsp--;


            }

            match(input,34,FOLLOW_34_in_query509); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:418:1: template returns [FactTemplateDescr template] : loc= 'template' templateName= ID opt_semicolon (slot= template_slot )+ 'end' opt_semicolon ;
    public FactTemplateDescr template() throws RecognitionException {   
        FactTemplateDescr template = null;

        Token loc=null;
        Token templateName=null;
        FieldTemplateDescr slot = null;


        
        		template = null;		
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:423:3: (loc= 'template' templateName= ID opt_semicolon (slot= template_slot )+ 'end' opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:423:3: loc= 'template' templateName= ID opt_semicolon (slot= template_slot )+ 'end' opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,35,FOLLOW_35_in_template535); 
            templateName=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template539); 
            pushFollow(FOLLOW_opt_semicolon_in_template541);
            opt_semicolon();
            _fsp--;

            
            			template = new FactTemplateDescr(templateName.getText());
            			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
            		
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:428:3: (slot= template_slot )+
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
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:429:4: slot= template_slot
            	    {
            	    pushFollow(FOLLOW_template_slot_in_template556);
            	    slot=template_slot();
            	    _fsp--;

            	    
            	    				template.addFieldTemplate(slot);
            	    			

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);

            match(input,34,FOLLOW_34_in_template571); 
            pushFollow(FOLLOW_opt_semicolon_in_template573);
            opt_semicolon();
            _fsp--;


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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:437:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name name= ID opt_semicolon ;
    public FieldTemplateDescr template_slot() throws RecognitionException {   
        FieldTemplateDescr field = null;

        Token name=null;
        String fieldType = null;


        
        		field = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:443:4: (fieldType= dotted_name name= ID opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:443:4: fieldType= dotted_name name= ID opt_semicolon
            {
            pushFollow(FOLLOW_dotted_name_in_template_slot605);
            fieldType=dotted_name();
            _fsp--;

            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template_slot609); 
            pushFollow(FOLLOW_opt_semicolon_in_template_slot611);
            opt_semicolon();
            _fsp--;

            
            			
            			
            			field = new FieldTemplateDescr(name.getText(), fieldType);
            			field.setLocation( offset(name.getLine()), name.getCharPositionInLine() );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:452:1: rule returns [RuleDescr rule] : loc= 'rule' ruleName= word rule_attributes[rule] (loc= 'when' ( ':' )? ( normal_lhs_block[lhs] ) )? rhs= RHS ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule = null;

        Token loc=null;
        Token rhs=null;
        String ruleName = null;


        
        		rule = null;
        		String consequence = "";
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:458:3: (loc= 'rule' ruleName= word rule_attributes[rule] (loc= 'when' ( ':' )? ( normal_lhs_block[lhs] ) )? rhs= RHS )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:458:3: loc= 'rule' ruleName= word rule_attributes[rule] (loc= 'when' ( ':' )? ( normal_lhs_block[lhs] ) )? rhs= RHS
            {
            loc=(Token)input.LT(1);
            match(input,36,FOLLOW_36_in_rule642); 
            pushFollow(FOLLOW_word_in_rule646);
            ruleName=word();
            _fsp--;

             
            			debug( "start rule: " + ruleName );
            			rule = new RuleDescr( ruleName, null ); 
            			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            pushFollow(FOLLOW_rule_attributes_in_rule655);
            rule_attributes(rule);
            _fsp--;

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:3: (loc= 'when' ( ':' )? ( normal_lhs_block[lhs] ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0==37) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:5: loc= 'when' ( ':' )? ( normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,37,FOLLOW_37_in_rule664); 
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:16: ( ':' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    if ( (LA13_0==38) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:16: ':'
                            {
                            match(input,38,FOLLOW_38_in_rule666); 

                            }
                            break;

                    }

                     
                    				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                    				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:470:4: ( normal_lhs_block[lhs] )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:472:5: normal_lhs_block[lhs]
                    {
                    pushFollow(FOLLOW_normal_lhs_block_in_rule684);
                    normal_lhs_block(lhs);
                    _fsp--;


                    }


                    }
                    break;

            }

            rhs=(Token)input.LT(1);
            match(input,RHS,FOLLOW_RHS_in_rule707); 
            
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:495:1: rule_attributes[RuleDescr rule] : ( 'attributes' ':' )? ( ( ',' )? a= rule_attribute )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:497:4: ( ( 'attributes' ':' )? ( ( ',' )? a= rule_attribute )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:497:4: ( 'attributes' ':' )? ( ( ',' )? a= rule_attribute )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:497:4: ( 'attributes' ':' )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( (LA15_0==39) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:497:5: 'attributes' ':'
                    {
                    match(input,39,FOLLOW_39_in_rule_attributes732); 
                    match(input,38,FOLLOW_38_in_rule_attributes734); 

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:498:4: ( ( ',' )? a= rule_attribute )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                if ( (LA17_0==32||(LA17_0>=40 && LA17_0<=45)) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:498:6: ( ',' )? a= rule_attribute
            	    {
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:498:6: ( ',' )?
            	    int alt16=2;
            	    int LA16_0 = input.LA(1);
            	    if ( (LA16_0==32) ) {
            	        alt16=1;
            	    }
            	    switch (alt16) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:498:6: ','
            	            {
            	            match(input,32,FOLLOW_32_in_rule_attributes743); 

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_rule_attribute_in_rule_attributes748);
            	    a=rule_attribute();
            	    _fsp--;

            	    
            	    					rule.addAttribute( a );
            	    				

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:507:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d = null;

        AttributeDescr a = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:512:4: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus )
            int alt18=6;
            switch ( input.LA(1) ) {
            case 40:
                alt18=1;
                break;
            case 41:
                alt18=2;
                break;
            case 44:
                alt18=3;
                break;
            case 45:
                alt18=4;
                break;
            case 43:
                alt18=5;
                break;
            case 42:
                alt18=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("507:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:512:4: a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute789);
                    a=salience();
                    _fsp--;

                     d = a; 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:513:5: a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute799);
                    a=no_loop();
                    _fsp--;

                     d = a; 

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:514:5: a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute810);
                    a=agenda_group();
                    _fsp--;

                     d = a; 

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:515:5: a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute823);
                    a=duration();
                    _fsp--;

                     d = a; 

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:516:5: a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute837);
                    a=activation_group();
                    _fsp--;

                     d = a; 

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:517:5: a= auto_focus
                    {
                    pushFollow(FOLLOW_auto_focus_in_rule_attribute848);
                    a=auto_focus();
                    _fsp--;

                     d = a; 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:522:1: salience returns [AttributeDescr d ] : loc= 'salience' i= INT opt_semicolon ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:527:3: (loc= 'salience' i= INT opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:527:3: loc= 'salience' i= INT opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,40,FOLLOW_40_in_salience882); 
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience886); 
            pushFollow(FOLLOW_opt_semicolon_in_salience888);
            opt_semicolon();
            _fsp--;

            
            			d = new AttributeDescr( "salience", i.getText() );
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:534:1: no_loop returns [AttributeDescr d] : ( (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:539:3: ( (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) )
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( (LA19_0==41) ) {
                int LA19_1 = input.LA(2);
                if ( (LA19_1==BOOL) ) {
                    alt19=2;
                }
                else if ( (LA19_1==RHS||LA19_1==25||LA19_1==32||LA19_1==37||(LA19_1>=40 && LA19_1<=45)) ) {
                    alt19=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("534:1: no_loop returns [AttributeDescr d] : ( (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );", 19, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("534:1: no_loop returns [AttributeDescr d] : ( (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );", 19, 0, input);

                throw nvae;
            }
            switch (alt19) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:539:3: (loc= 'no-loop' opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:539:3: (loc= 'no-loop' opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:540:4: loc= 'no-loop' opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,41,FOLLOW_41_in_no_loop923); 
                    pushFollow(FOLLOW_opt_semicolon_in_no_loop925);
                    opt_semicolon();
                    _fsp--;

                    
                    				d = new AttributeDescr( "no-loop", "true" );
                    				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:547:3: (loc= 'no-loop' t= BOOL opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:547:3: (loc= 'no-loop' t= BOOL opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:548:4: loc= 'no-loop' t= BOOL opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,41,FOLLOW_41_in_no_loop950); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop954); 
                    pushFollow(FOLLOW_opt_semicolon_in_no_loop956);
                    opt_semicolon();
                    _fsp--;

                    
                    				d = new AttributeDescr( "no-loop", t.getText() );
                    				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:558:1: auto_focus returns [AttributeDescr d] : ( (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );
    public AttributeDescr auto_focus() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:563:3: ( (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) )
            int alt20=2;
            int LA20_0 = input.LA(1);
            if ( (LA20_0==42) ) {
                int LA20_1 = input.LA(2);
                if ( (LA20_1==BOOL) ) {
                    alt20=2;
                }
                else if ( (LA20_1==RHS||LA20_1==25||LA20_1==32||LA20_1==37||(LA20_1>=40 && LA20_1<=45)) ) {
                    alt20=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("558:1: auto_focus returns [AttributeDescr d] : ( (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );", 20, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("558:1: auto_focus returns [AttributeDescr d] : ( (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:563:3: (loc= 'auto-focus' opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:563:3: (loc= 'auto-focus' opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:564:4: loc= 'auto-focus' opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,42,FOLLOW_42_in_auto_focus1002); 
                    pushFollow(FOLLOW_opt_semicolon_in_auto_focus1004);
                    opt_semicolon();
                    _fsp--;

                    
                    				d = new AttributeDescr( "auto-focus", "true" );
                    				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:571:3: (loc= 'auto-focus' t= BOOL opt_semicolon )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:571:3: (loc= 'auto-focus' t= BOOL opt_semicolon )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:572:4: loc= 'auto-focus' t= BOOL opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,42,FOLLOW_42_in_auto_focus1029); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1033); 
                    pushFollow(FOLLOW_opt_semicolon_in_auto_focus1035);
                    opt_semicolon();
                    _fsp--;

                    
                    				d = new AttributeDescr( "auto-focus", t.getText() );
                    				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:582:1: activation_group returns [AttributeDescr d] : loc= 'activation-group' name= STRING opt_semicolon ;
    public AttributeDescr activation_group() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:587:3: (loc= 'activation-group' name= STRING opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:587:3: loc= 'activation-group' name= STRING opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,43,FOLLOW_43_in_activation_group1077); 
            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1081); 
            pushFollow(FOLLOW_opt_semicolon_in_activation_group1083);
            opt_semicolon();
            _fsp--;

            
            			d = new AttributeDescr( "activation-group", getString( name ) );
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:594:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' name= STRING opt_semicolon ;
    public AttributeDescr agenda_group() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:599:3: (loc= 'agenda-group' name= STRING opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:599:3: loc= 'agenda-group' name= STRING opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,44,FOLLOW_44_in_agenda_group1112); 
            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1116); 
            pushFollow(FOLLOW_opt_semicolon_in_agenda_group1118);
            opt_semicolon();
            _fsp--;

            
            			d = new AttributeDescr( "agenda-group", getString( name ) );
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:607:1: duration returns [AttributeDescr d] : loc= 'duration' i= INT ;
    public AttributeDescr duration() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:612:3: (loc= 'duration' i= INT )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:612:3: loc= 'duration' i= INT
            {
            loc=(Token)input.LT(1);
            match(input,45,FOLLOW_45_in_duration1150); 
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1154); 
            
            			d = new AttributeDescr( "duration", i.getText() );
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:620:1: normal_lhs_block[AndDescr descrs] : (d= lhs )* ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        BaseDescr d = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:622:3: ( (d= lhs )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:622:3: (d= lhs )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:622:3: (d= lhs )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);
                if ( (LA21_0==ID||LA21_0==LEFT_PAREN||(LA21_0>=73 && LA21_0<=75)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:622:5: d= lhs
            	    {
            	    pushFollow(FOLLOW_lhs_in_normal_lhs_block1180);
            	    d=lhs();
            	    _fsp--;

            	     descrs.addDescr( d ); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:676:1: lhs returns [BaseDescr d] : l= lhs_or ;
    public BaseDescr lhs() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr l = null;


        
        		d=null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:4: (l= lhs_or )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:4: l= lhs_or
            {
            pushFollow(FOLLOW_lhs_or_in_lhs1218);
            l=lhs_or();
            _fsp--;

             d = l; 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:684:1: lhs_column returns [BaseDescr d] : (f= fact_binding | f= fact );
    public BaseDescr lhs_column() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr f = null;


        
        		d=null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:688:4: (f= fact_binding | f= fact )
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( (LA22_0==ID) ) {
                int LA22_1 = input.LA(2);
                if ( (LA22_1==38) ) {
                    alt22=1;
                }
                else if ( (LA22_1==LEFT_PAREN||LA22_1==29||LA22_1==47) ) {
                    alt22=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("684:1: lhs_column returns [BaseDescr d] : (f= fact_binding | f= fact );", 22, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("684:1: lhs_column returns [BaseDescr d] : (f= fact_binding | f= fact );", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:688:4: f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_column1246);
                    f=fact_binding();
                    _fsp--;

                     d = f; 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:689:4: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_lhs_column1255);
                    f=fact();
                    _fsp--;

                     d = f; 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:692:1: from_statement returns [FromDescr d] : 'from' ds= from_source ;
    public FromDescr from_statement() throws RecognitionException {   
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;


        
        		d=factory.createFrom();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:697:4: ( 'from' ds= from_source )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:697:4: 'from' ds= from_source
            {
            match(input,46,FOLLOW_46_in_from_statement1283); 
            pushFollow(FOLLOW_from_source_in_from_statement1287);
            ds=from_source();
            _fsp--;

            
             			d.setDataSource(ds);
             		
             		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID ( '[' arg= argument_value ']' )? ) | (var= ID '.' method= ID '(' args= argument_list ')' ) | (functionName= ID '(' args= argument_list ')' ) );
    public DeclarativeInvokerDescr from_source() throws RecognitionException {   
        DeclarativeInvokerDescr ds = null;

        Token var=null;
        Token field=null;
        Token method=null;
        Token functionName=null;
        ArgumentValueDescr arg = null;

        ArrayList args = null;


        
        		ds = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:3: ( (var= ID '.' field= ID ( '[' arg= argument_value ']' )? ) | (var= ID '.' method= ID '(' args= argument_list ')' ) | (functionName= ID '(' args= argument_list ')' ) )
            int alt24=3;
            int LA24_0 = input.LA(1);
            if ( (LA24_0==ID) ) {
                int LA24_1 = input.LA(2);
                if ( (LA24_1==LEFT_PAREN) ) {
                    alt24=3;
                }
                else if ( (LA24_1==29) ) {
                    int LA24_3 = input.LA(3);
                    if ( (LA24_3==ID) ) {
                        int LA24_4 = input.LA(4);
                        if ( (LA24_4==LEFT_PAREN) ) {
                            switch ( input.LA(5) ) {
                            case CURLY_CHUNK:
                            case INT:
                            case BOOL:
                            case STRING:
                            case FLOAT:
                            case RIGHT_PAREN:
                            case 47:
                            case 54:
                                alt24=2;
                                break;
                            case ID:
                                int LA24_8 = input.LA(6);
                                if ( (LA24_8==LEFT_PAREN||LA24_8==29||LA24_8==38||LA24_8==47) ) {
                                    alt24=1;
                                }
                                else if ( (LA24_8==RIGHT_PAREN||LA24_8==32) ) {
                                    alt24=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID ( '[' arg= argument_value ']' )? ) | (var= ID '.' method= ID '(' args= argument_list ')' ) | (functionName= ID '(' args= argument_list ')' ) );", 24, 8, input);

                                    throw nvae;
                                }
                                break;
                            case LEFT_PAREN:
                            case 73:
                            case 74:
                            case 75:
                                alt24=1;
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID ( '[' arg= argument_value ']' )? ) | (var= ID '.' method= ID '(' args= argument_list ')' ) | (functionName= ID '(' args= argument_list ')' ) );", 24, 5, input);

                                throw nvae;
                            }

                        }
                        else if ( (LA24_4==ID||LA24_4==RHS||LA24_4==RIGHT_PAREN||LA24_4==25||LA24_4==34||LA24_4==47||(LA24_4>=57 && LA24_4<=58)||(LA24_4>=71 && LA24_4<=75)) ) {
                            alt24=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID ( '[' arg= argument_value ']' )? ) | (var= ID '.' method= ID '(' args= argument_list ')' ) | (functionName= ID '(' args= argument_list ')' ) );", 24, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID ( '[' arg= argument_value ']' )? ) | (var= ID '.' method= ID '(' args= argument_list ')' ) | (functionName= ID '(' args= argument_list ')' ) );", 24, 3, input);

                        throw nvae;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID ( '[' arg= argument_value ']' )? ) | (var= ID '.' method= ID '(' args= argument_list ')' ) | (functionName= ID '(' args= argument_list ')' ) );", 24, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID ( '[' arg= argument_value ']' )? ) | (var= ID '.' method= ID '(' args= argument_list ')' ) | (functionName= ID '(' args= argument_list ')' ) );", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:3: (var= ID '.' field= ID ( '[' arg= argument_value ']' )? )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:3: (var= ID '.' field= ID ( '[' arg= argument_value ']' )? )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:4: var= ID '.' field= ID ( '[' arg= argument_value ']' )?
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1331); 
                    match(input,29,FOLLOW_29_in_from_source1333); 
                    field=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1337); 
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:25: ( '[' arg= argument_value ']' )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    if ( (LA23_0==47) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:26: '[' arg= argument_value ']'
                            {
                            match(input,47,FOLLOW_47_in_from_source1341); 
                            pushFollow(FOLLOW_argument_value_in_from_source1345);
                            arg=argument_value();
                            _fsp--;

                            match(input,48,FOLLOW_48_in_from_source1347); 

                            }
                            break;

                    }

                    
                              		 FieldAccessDescr fa;
                    			  if ( arg == null )   {
                    				  fa = new FieldAccessDescr(var.getText(), field.getText());	
                    			  } else {
                    				  fa = new FieldAccessDescr(var.getText(), field.getText(), arg);				  
                    			  }
                    			  fa.setLocation( offset(var.getLine()), var.getCharPositionInLine() );
                    			  ds = fa;
                    			 

                    }


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:727:3: (var= ID '.' method= ID '(' args= argument_list ')' )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:727:3: (var= ID '.' method= ID '(' args= argument_list ')' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:727:4: var= ID '.' method= ID '(' args= argument_list ')'
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1376); 
                    match(input,29,FOLLOW_29_in_from_source1378); 
                    method=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1382); 
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_from_source1384); 
                    pushFollow(FOLLOW_argument_list_in_from_source1388);
                    args=argument_list();
                    _fsp--;

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_from_source1390); 
                    
                    			  MethodAccessDescr ma = new MethodAccessDescr(var.getText(), method.getText());	
                    			  ma.setLocation( offset(var.getLine()), var.getCharPositionInLine() );
                    			  ma.setArguments(args);
                    			  ds = ma;
                    			

                    }


                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:736:3: (functionName= ID '(' args= argument_list ')' )
                    {
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:736:3: (functionName= ID '(' args= argument_list ')' )
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:736:4: functionName= ID '(' args= argument_list ')'
                    {
                    functionName=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1412); 
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_from_source1414); 
                    pushFollow(FOLLOW_argument_list_in_from_source1418);
                    args=argument_list();
                    _fsp--;

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_from_source1420); 
                    
                    			FunctionCallDescr fc = new FunctionCallDescr(functionName.getText());
                    			fc.setLocation( offset(functionName.getLine()), functionName.getCharPositionInLine() );			
                    			fc.setArguments(args);
                    			ds = fc;
                    			

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


    // $ANTLR start accumulate_statement
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:749:1: accumulate_statement returns [AccumulateDescr d] : loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')' ;
    public AccumulateDescr accumulate_statement() throws RecognitionException {   
        AccumulateDescr d = null;

        Token loc=null;
        BaseDescr column = null;

        String text = null;


        
        		d = factory.createAccumulate();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:754:10: (loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:754:10: loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')'
            {
            loc=(Token)input.LT(1);
            match(input,46,FOLLOW_46_in_accumulate_statement1469); 
            match(input,49,FOLLOW_49_in_accumulate_statement1471); 
             
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_accumulate_statement1481); 
            pushFollow(FOLLOW_lhs_column_in_accumulate_statement1485);
            column=lhs_column();
            _fsp--;

            match(input,32,FOLLOW_32_in_accumulate_statement1487); 
            
            		        d.setSourceColumn( (ColumnDescr)column );
            		
            match(input,50,FOLLOW_50_in_accumulate_statement1496); 
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1500);
            text=paren_chunk();
            _fsp--;

            match(input,32,FOLLOW_32_in_accumulate_statement1502); 
            
            		        d.setInitCode( text.substring(1, text.length()-1) );
            		
            match(input,51,FOLLOW_51_in_accumulate_statement1511); 
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1515);
            text=paren_chunk();
            _fsp--;

            match(input,32,FOLLOW_32_in_accumulate_statement1517); 
            
            		        d.setActionCode( text.substring(1, text.length()-1) );
            		
            match(input,52,FOLLOW_52_in_accumulate_statement1526); 
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1530);
            text=paren_chunk();
            _fsp--;

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_accumulate_statement1532); 
            
            		        d.setResultCode( text.substring(1, text.length()-1) );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:776:1: collect_statement returns [CollectDescr d] : loc= 'from' 'collect' '(' column= lhs_column ')' ;
    public CollectDescr collect_statement() throws RecognitionException {   
        CollectDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = factory.createCollect();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:781:10: (loc= 'from' 'collect' '(' column= lhs_column ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:781:10: loc= 'from' 'collect' '(' column= lhs_column ')'
            {
            loc=(Token)input.LT(1);
            match(input,46,FOLLOW_46_in_collect_statement1575); 
            match(input,53,FOLLOW_53_in_collect_statement1577); 
             
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_collect_statement1587); 
            pushFollow(FOLLOW_lhs_column_in_collect_statement1591);
            column=lhs_column();
            _fsp--;

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_collect_statement1593); 
            
            		        d.setSourceColumn( (ColumnDescr)column );
            		

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


    // $ANTLR start argument_list
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:791:1: argument_list returns [ArrayList args] : (param= argument_value ( ',' param= argument_value )* )? ;
    public ArrayList argument_list() throws RecognitionException {   
        ArrayList args = null;

        ArgumentValueDescr param = null;


        
        		args = new ArrayList();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:796:3: ( (param= argument_value ( ',' param= argument_value )* )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:796:3: (param= argument_value ( ',' param= argument_value )* )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:796:3: (param= argument_value ( ',' param= argument_value )* )?
            int alt26=2;
            int LA26_0 = input.LA(1);
            if ( ((LA26_0>=ID && LA26_0<=CURLY_CHUNK)||(LA26_0>=INT && LA26_0<=FLOAT)||LA26_0==47||LA26_0==54) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:796:4: param= argument_value ( ',' param= argument_value )*
                    {
                    pushFollow(FOLLOW_argument_value_in_argument_list1626);
                    param=argument_value();
                    _fsp--;

                    
                    			if (param != null) {
                    				args.add(param);
                    			}
                    		
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:802:3: ( ',' param= argument_value )*
                    loop25:
                    do {
                        int alt25=2;
                        int LA25_0 = input.LA(1);
                        if ( (LA25_0==32) ) {
                            alt25=1;
                        }


                        switch (alt25) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:803:4: ',' param= argument_value
                    	    {
                    	    match(input,32,FOLLOW_32_in_argument_list1642); 
                    	    pushFollow(FOLLOW_argument_value_in_argument_list1646);
                    	    param=argument_value();
                    	    _fsp--;

                    	    
                    	    				if (param != null) {
                    	    					args.add(param);
                    	    				}
                    	    			

                    	    }
                    	    break;

                    	default :
                    	    break loop25;
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
        return args;
    }
    // $ANTLR end argument_list


    // $ANTLR start argument_value
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:812:1: argument_value returns [ArgumentValueDescr value] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | m= inline_map | a= inline_array ) ;
    public ArgumentValueDescr argument_value() throws RecognitionException {   
        ArgumentValueDescr value = null;

        Token t=null;
        ArgumentValueDescr.MapDescr m = null;

        List a = null;


        
        		value = null;
        		String text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:817:4: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | m= inline_map | a= inline_array ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:817:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | m= inline_map | a= inline_array )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:817:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | m= inline_map | a= inline_array )
            int alt27=8;
            switch ( input.LA(1) ) {
            case STRING:
                alt27=1;
                break;
            case INT:
                alt27=2;
                break;
            case FLOAT:
                alt27=3;
                break;
            case BOOL:
                alt27=4;
                break;
            case ID:
                alt27=5;
                break;
            case 54:
                alt27=6;
                break;
            case CURLY_CHUNK:
                alt27=7;
                break;
            case 47:
                alt27=8;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("817:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | m= inline_map | a= inline_array )", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:817:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_argument_value1686); 
                     text = getString( t );  value=new ArgumentValueDescr(ArgumentValueDescr.STRING, text);

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:818:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_argument_value1697); 
                     text = t.getText();  value=new ArgumentValueDescr(ArgumentValueDescr.INTEGRAL, text);

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:819:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_argument_value1710); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.DECIMAL, text); 

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:820:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_argument_value1721); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.BOOLEAN, text); 

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:821:5: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_argument_value1733); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.VARIABLE, text);

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:822:5: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,54,FOLLOW_54_in_argument_value1744); 
                     text = "null"; value=new ArgumentValueDescr(ArgumentValueDescr.NULL, text);

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:823:11: m= inline_map
                    {
                    pushFollow(FOLLOW_inline_map_in_argument_value1761);
                    m=inline_map();
                    _fsp--;

                      value=new ArgumentValueDescr(ArgumentValueDescr.MAP, m.getKeyValuePairs() ); 

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:824:11: a= inline_array
                    {
                    pushFollow(FOLLOW_inline_array_in_argument_value1777);
                    a=inline_array();
                    _fsp--;

                     value = new ArgumentValueDescr(ArgumentValueDescr.LIST, a ); 

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
        return value;
    }
    // $ANTLR end argument_value


    // $ANTLR start inline_map
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:828:1: inline_map returns [ArgumentValueDescr.MapDescr mapDescr] : '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}' ;
    public ArgumentValueDescr.MapDescr inline_map() throws RecognitionException {   
        ArgumentValueDescr.MapDescr mapDescr = null;

        ArgumentValueDescr key = null;

        ArgumentValueDescr value = null;


        
                mapDescr = new ArgumentValueDescr.MapDescr();
            
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:832:8: ( '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:832:8: '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}'
            {
            match(input,CURLY_CHUNK,FOLLOW_CURLY_CHUNK_in_inline_map1817); 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:833:12: (key= argument_value '=>' value= argument_value )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:833:14: key= argument_value '=>' value= argument_value
            {
            pushFollow(FOLLOW_argument_value_in_inline_map1835);
            key=argument_value();
            _fsp--;

            match(input,55,FOLLOW_55_in_inline_map1837); 
            pushFollow(FOLLOW_argument_value_in_inline_map1841);
            value=argument_value();
            _fsp--;

            
                             if ( key != null ) {
                                 mapDescr.add( new ArgumentValueDescr.KeyValuePairDescr( key, value ) );
                             }
                         

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:840:12: ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )*
            loop30:
            do {
                int alt30=2;
                int LA30_0 = input.LA(1);
                if ( (LA30_0==EOL||LA30_0==32) ) {
                    alt30=1;
                }


                switch (alt30) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:840:14: ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value
            	    {
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:840:14: ( EOL )?
            	    int alt28=2;
            	    int LA28_0 = input.LA(1);
            	    if ( (LA28_0==EOL) ) {
            	        alt28=1;
            	    }
            	    switch (alt28) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:840:15: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_map1884); 

            	            }
            	            break;

            	    }

            	    match(input,32,FOLLOW_32_in_inline_map1888); 
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:840:25: ( EOL )?
            	    int alt29=2;
            	    int LA29_0 = input.LA(1);
            	    if ( (LA29_0==EOL) ) {
            	        alt29=1;
            	    }
            	    switch (alt29) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:840:26: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_map1891); 

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_argument_value_in_inline_map1897);
            	    key=argument_value();
            	    _fsp--;

            	    match(input,55,FOLLOW_55_in_inline_map1899); 
            	    pushFollow(FOLLOW_argument_value_in_inline_map1903);
            	    value=argument_value();
            	    _fsp--;

            	    
            	                     if ( key != null ) {
            	                         mapDescr.add( new ArgumentValueDescr.KeyValuePairDescr( key, value ) );
            	                     }
            	                 

            	    }
            	    break;

            	default :
            	    break loop30;
                }
            } while (true);

            match(input,56,FOLLOW_56_in_inline_map1939); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return mapDescr;
    }
    // $ANTLR end inline_map


    // $ANTLR start inline_array
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:849:1: inline_array returns [List list] : '[' arg= argument_value ( ( EOL )? ',' ( EOL )? arg= argument_value )* ']' ;
    public List inline_array() throws RecognitionException {   
        List list = null;

        ArgumentValueDescr arg = null;


        
            	list = new ArrayList();
            
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:854:5: ( '[' arg= argument_value ( ( EOL )? ',' ( EOL )? arg= argument_value )* ']' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:854:5: '[' arg= argument_value ( ( EOL )? ',' ( EOL )? arg= argument_value )* ']'
            {
            match(input,47,FOLLOW_47_in_inline_array1983); 
            pushFollow(FOLLOW_argument_value_in_inline_array1987);
            arg=argument_value();
            _fsp--;

             list.add(arg); 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:856:8: ( ( EOL )? ',' ( EOL )? arg= argument_value )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                if ( (LA33_0==EOL||LA33_0==32) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:856:10: ( EOL )? ',' ( EOL )? arg= argument_value
            	    {
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:856:10: ( EOL )?
            	    int alt31=2;
            	    int LA31_0 = input.LA(1);
            	    if ( (LA31_0==EOL) ) {
            	        alt31=1;
            	    }
            	    switch (alt31) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:856:10: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_array2005); 

            	            }
            	            break;

            	    }

            	    match(input,32,FOLLOW_32_in_inline_array2008); 
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:856:19: ( EOL )?
            	    int alt32=2;
            	    int LA32_0 = input.LA(1);
            	    if ( (LA32_0==EOL) ) {
            	        alt32=1;
            	    }
            	    switch (alt32) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:856:19: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_array2010); 

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_argument_value_in_inline_array2015);
            	    arg=argument_value();
            	    _fsp--;

            	     list.add(arg); 

            	    }
            	    break;

            	default :
            	    break loop33;
                }
            } while (true);

            match(input,48,FOLLOW_48_in_inline_array2028); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return list;
    }
    // $ANTLR end inline_array


    // $ANTLR start fact_binding
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:862:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public BaseDescr fact_binding() throws RecognitionException {   
        BaseDescr d = null;

        Token id=null;
        BaseDescr fe = null;


        
        		d=null;
        		boolean multi=false;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:868:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:868:4: id= ID ':' fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding2072); 
            match(input,38,FOLLOW_38_in_fact_binding2082); 
            pushFollow(FOLLOW_fact_expression_in_fact_binding2086);
            fe=fact_expression(id.getText());
            _fsp--;

            
             			d=fe;
             		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:876:2: fact_expression[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression_in_paren[id] ')' | f= fact );
    public BaseDescr fact_expression(String id) throws RecognitionException {   
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:881:5: ( '(' fe= fact_expression_in_paren[id] ')' | f= fact )
            int alt34=2;
            int LA34_0 = input.LA(1);
            if ( (LA34_0==LEFT_PAREN) ) {
                alt34=1;
            }
            else if ( (LA34_0==ID) ) {
                alt34=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("876:2: fact_expression[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression_in_paren[id] ')' | f= fact );", 34, 0, input);

                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:881:5: '(' fe= fact_expression_in_paren[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression2118); 
                    pushFollow(FOLLOW_fact_expression_in_paren_in_fact_expression2122);
                    fe=fact_expression_in_paren(id);
                    _fsp--;

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression2125); 
                     pd=fe; 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:882:6: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression2136);
                    f=fact();
                    _fsp--;

                    
                     			((ColumnDescr)f).setIdentifier( id );
                     			pd = f;
                     		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:890:2: fact_expression_in_paren[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ('or'|'||')f= fact )* );
    public BaseDescr fact_expression_in_paren(String id) throws RecognitionException {   
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:895:5: ( '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ('or'|'||')f= fact )* )
            int alt36=2;
            int LA36_0 = input.LA(1);
            if ( (LA36_0==LEFT_PAREN) ) {
                alt36=1;
            }
            else if ( (LA36_0==ID) ) {
                alt36=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("890:2: fact_expression_in_paren[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ('or'|'||')f= fact )* );", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:895:5: '(' fe= fact_expression_in_paren[id] ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact_expression_in_paren2167); 
                    pushFollow(FOLLOW_fact_expression_in_paren_in_fact_expression_in_paren2171);
                    fe=fact_expression_in_paren(id);
                    _fsp--;

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact_expression_in_paren2173); 
                     pd=fe; 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:896:6: f= fact ( ('or'|'||')f= fact )*
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression_in_paren2184);
                    f=fact();
                    _fsp--;

                    
                     			((ColumnDescr)f).setIdentifier( id );
                     			pd = f;
                     		
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:901:4: ( ('or'|'||')f= fact )*
                    loop35:
                    do {
                        int alt35=2;
                        int LA35_0 = input.LA(1);
                        if ( ((LA35_0>=57 && LA35_0<=58)) ) {
                            alt35=1;
                        }


                        switch (alt35) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:901:6: ('or'|'||')f= fact
                    	    {
                    	    if ( (input.LA(1)>=57 && input.LA(1)<=58) ) {
                    	        input.consume();
                    	        errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression_in_paren2197);    throw mse;
                    	    }

                    	    	if ( ! multi ) {
                    	     					BaseDescr first = pd;
                    	     					pd = new OrDescr();
                    	     					((OrDescr)pd).addDescr( first );
                    	     					multi=true;
                    	     				}
                    	     			
                    	    pushFollow(FOLLOW_fact_in_fact_expression_in_paren2214);
                    	    f=fact();
                    	    _fsp--;

                    	    
                    	     				((ColumnDescr)f).setIdentifier( id );
                    	     				((OrDescr)pd).addDescr( f );
                    	     			

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
    // $ANTLR end fact_expression_in_paren


    // $ANTLR start fact
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:917:1: fact returns [BaseDescr d] : id= dotted_name loc= '(' (c= constraints )? endLoc= ')' ;
    public BaseDescr fact() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        Token endLoc=null;
        String id = null;

        List c = null;


        
        		d=null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:921:5: (id= dotted_name loc= '(' (c= constraints )? endLoc= ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:921:5: id= dotted_name loc= '(' (c= constraints )? endLoc= ')'
            {
            pushFollow(FOLLOW_dotted_name_in_fact2253);
            id=dotted_name();
            _fsp--;

             
             			d = new ColumnDescr( id ); 
             		
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_fact2266); 
            
             				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
             			
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:927:7: (c= constraints )?
            int alt37=2;
            int LA37_0 = input.LA(1);
            if ( (LA37_0==ID) ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:927:9: c= constraints
                    {
                    pushFollow(FOLLOW_constraints_in_fact2274);
                    c=constraints();
                    _fsp--;

                    
                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (BaseDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            endLoc=(Token)input.LT(1);
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_fact2295); 
            
             					d.setEndLocation( offset(endLoc.getLine()), endLoc.getCharPositionInLine() );	
             				

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:941:1: constraints returns [List constraints] : ( constraint[constraints] | predicate[constraints] ) ( ',' ( constraint[constraints] | predicate[constraints] ) )* ;
    public List constraints() throws RecognitionException {   
        List constraints = null;

        
        		constraints = new ArrayList();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:945:4: ( ( constraint[constraints] | predicate[constraints] ) ( ',' ( constraint[constraints] | predicate[constraints] ) )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:945:4: ( constraint[constraints] | predicate[constraints] ) ( ',' ( constraint[constraints] | predicate[constraints] ) )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:945:4: ( constraint[constraints] | predicate[constraints] )
            int alt38=2;
            int LA38_0 = input.LA(1);
            if ( (LA38_0==ID) ) {
                int LA38_1 = input.LA(2);
                if ( (LA38_1==38) ) {
                    int LA38_2 = input.LA(3);
                    if ( (LA38_2==ID) ) {
                        int LA38_4 = input.LA(4);
                        if ( (LA38_4==70) ) {
                            alt38=2;
                        }
                        else if ( (LA38_4==RIGHT_PAREN||LA38_4==32||(LA38_4>=61 && LA38_4<=69)) ) {
                            alt38=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("945:4: ( constraint[constraints] | predicate[constraints] )", 38, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("945:4: ( constraint[constraints] | predicate[constraints] )", 38, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA38_1==RIGHT_PAREN||LA38_1==32||(LA38_1>=61 && LA38_1<=69)) ) {
                    alt38=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("945:4: ( constraint[constraints] | predicate[constraints] )", 38, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("945:4: ( constraint[constraints] | predicate[constraints] )", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:945:5: constraint[constraints]
                    {
                    pushFollow(FOLLOW_constraint_in_constraints2328);
                    constraint(constraints);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:945:29: predicate[constraints]
                    {
                    pushFollow(FOLLOW_predicate_in_constraints2331);
                    predicate(constraints);
                    _fsp--;


                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:946:3: ( ',' ( constraint[constraints] | predicate[constraints] ) )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);
                if ( (LA40_0==32) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:946:5: ',' ( constraint[constraints] | predicate[constraints] )
            	    {
            	    match(input,32,FOLLOW_32_in_constraints2339); 
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:946:9: ( constraint[constraints] | predicate[constraints] )
            	    int alt39=2;
            	    int LA39_0 = input.LA(1);
            	    if ( (LA39_0==ID) ) {
            	        int LA39_1 = input.LA(2);
            	        if ( (LA39_1==38) ) {
            	            int LA39_2 = input.LA(3);
            	            if ( (LA39_2==ID) ) {
            	                int LA39_4 = input.LA(4);
            	                if ( (LA39_4==70) ) {
            	                    alt39=2;
            	                }
            	                else if ( (LA39_4==RIGHT_PAREN||LA39_4==32||(LA39_4>=61 && LA39_4<=69)) ) {
            	                    alt39=1;
            	                }
            	                else {
            	                    NoViableAltException nvae =
            	                        new NoViableAltException("946:9: ( constraint[constraints] | predicate[constraints] )", 39, 4, input);

            	                    throw nvae;
            	                }
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("946:9: ( constraint[constraints] | predicate[constraints] )", 39, 2, input);

            	                throw nvae;
            	            }
            	        }
            	        else if ( (LA39_1==RIGHT_PAREN||LA39_1==32||(LA39_1>=61 && LA39_1<=69)) ) {
            	            alt39=1;
            	        }
            	        else {
            	            NoViableAltException nvae =
            	                new NoViableAltException("946:9: ( constraint[constraints] | predicate[constraints] )", 39, 1, input);

            	            throw nvae;
            	        }
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("946:9: ( constraint[constraints] | predicate[constraints] )", 39, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt39) {
            	        case 1 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:946:10: constraint[constraints]
            	            {
            	            pushFollow(FOLLOW_constraint_in_constraints2342);
            	            constraint(constraints);
            	            _fsp--;


            	            }
            	            break;
            	        case 2 :
            	            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:946:34: predicate[constraints]
            	            {
            	            pushFollow(FOLLOW_predicate_in_constraints2345);
            	            predicate(constraints);
            	            _fsp--;


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop40;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:949:1: constraint[List constraints] : (fb= ID ':' )? f= ID (rd= constraint_expression (con= ('&'|'|')rd= constraint_expression )* )? ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token con=null;
        RestrictionDescr rd = null;


        
        		BaseDescr d = null;
        		FieldConstraintDescr fc = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:955:3: ( (fb= ID ':' )? f= ID (rd= constraint_expression (con= ('&'|'|')rd= constraint_expression )* )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:955:3: (fb= ID ':' )? f= ID (rd= constraint_expression (con= ('&'|'|')rd= constraint_expression )* )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:955:3: (fb= ID ':' )?
            int alt41=2;
            int LA41_0 = input.LA(1);
            if ( (LA41_0==ID) ) {
                int LA41_1 = input.LA(2);
                if ( (LA41_1==38) ) {
                    alt41=1;
                }
            }
            switch (alt41) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:955:5: fb= ID ':'
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint2374); 
                    match(input,38,FOLLOW_38_in_constraint2376); 

                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint2386); 
            
            			if ( fb != null ) {
            				d = new FieldBindingDescr( f.getText(), fb.getText() );
            				d.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            				constraints.add( d );
            			} 
            			fc = new FieldConstraintDescr(f.getText());
            			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            		
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:966:3: (rd= constraint_expression (con= ('&'|'|')rd= constraint_expression )* )?
            int alt43=2;
            int LA43_0 = input.LA(1);
            if ( ((LA43_0>=61 && LA43_0<=69)) ) {
                alt43=1;
            }
            switch (alt43) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:967:4: rd= constraint_expression (con= ('&'|'|')rd= constraint_expression )*
                    {
                    pushFollow(FOLLOW_constraint_expression_in_constraint2402);
                    rd=constraint_expression();
                    _fsp--;

                    
                    				fc.addRestriction(rd);
                    				constraints.add(fc);
                    			
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:972:4: (con= ('&'|'|')rd= constraint_expression )*
                    loop42:
                    do {
                        int alt42=2;
                        int LA42_0 = input.LA(1);
                        if ( ((LA42_0>=59 && LA42_0<=60)) ) {
                            alt42=1;
                        }


                        switch (alt42) {
                    	case 1 :
                    	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:973:5: con= ('&'|'|')rd= constraint_expression
                    	    {
                    	    con=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=59 && input.LA(1)<=60) ) {
                    	        input.consume();
                    	        errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint2421);    throw mse;
                    	    }

                    	    
                    	    					if (con.getText().equals("&") ) {								
                    	    						fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	    					} else {
                    	    						fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	    					}							
                    	    				
                    	    pushFollow(FOLLOW_constraint_expression_in_constraint2438);
                    	    rd=constraint_expression();
                    	    _fsp--;

                    	    
                    	    					fc.addRestriction(rd);
                    	    				

                    	    }
                    	    break;

                    	default :
                    	    break loop42;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:989:1: constraint_expression returns [RestrictionDescr rd] : op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) ;
    public RestrictionDescr constraint_expression() throws RecognitionException {   
        RestrictionDescr rd = null;

        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:991:3: (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:991:3: op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            {
            op=(Token)input.LT(1);
            if ( (input.LA(1)>=61 && input.LA(1)<=69) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint_expression2490);    throw mse;
            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1001:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            int alt44=4;
            switch ( input.LA(1) ) {
            case ID:
                int LA44_1 = input.LA(2);
                if ( (LA44_1==29) ) {
                    alt44=2;
                }
                else if ( (LA44_1==RIGHT_PAREN||LA44_1==32||(LA44_1>=59 && LA44_1<=60)) ) {
                    alt44=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("1001:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 44, 1, input);

                    throw nvae;
                }
                break;
            case INT:
            case BOOL:
            case STRING:
            case FLOAT:
            case 54:
                alt44=3;
                break;
            case LEFT_PAREN:
                alt44=4;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1001:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 44, 0, input);

                throw nvae;
            }

            switch (alt44) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1001:5: bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint_expression2557); 
                    
                    				rd = new VariableRestrictionDescr(op.getText(), bvc.getText());
                    			

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1006:4: lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_constraint_expression2573);
                    lc=enum_constraint();
                    _fsp--;

                     
                    				rd  = new LiteralRestrictionDescr(op.getText(), lc, true);
                    			

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1011:4: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_constraint_expression2596);
                    lc=literal_constraint();
                    _fsp--;

                     
                    				rd  = new LiteralRestrictionDescr(op.getText(), lc);
                    			

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1015:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_constraint_expression2610);
                    rvc=retval_constraint();
                    _fsp--;

                     
                    				rd = new ReturnValueRestrictionDescr(op.getText(), rvc);							
                    			

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1022:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) ;
    public String literal_constraint() throws RecognitionException {   
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:4: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            int alt45=5;
            switch ( input.LA(1) ) {
            case STRING:
                alt45=1;
                break;
            case INT:
                alt45=2;
                break;
            case FLOAT:
                alt45=3;
                break;
            case BOOL:
                alt45=4;
                break;
            case 54:
                alt45=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1026:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )", 45, 0, input);

                throw nvae;
            }

            switch (alt45) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1026:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint2649); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1027:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint2660); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1028:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint2673); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1029:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint2684); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1030:5: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,54,FOLLOW_54_in_literal_constraint2696); 
                     text = null; 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1034:1: enum_constraint returns [String text] : (cls= ID '.' en= ID ) ;
    public String enum_constraint() throws RecognitionException {   
        String text = null;

        Token cls=null;
        Token en=null;

        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1038:4: ( (cls= ID '.' en= ID ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1038:4: (cls= ID '.' en= ID )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1038:4: (cls= ID '.' en= ID )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1038:5: cls= ID '.' en= ID
            {
            cls=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2727); 
            match(input,29,FOLLOW_29_in_enum_constraint2729); 
            en=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2733); 

            }

             text = cls.getText() + "." + en.getText(); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1042:1: predicate[List constraints] : decl= ID ':' field= ID '->' text= paren_chunk ;
    public void predicate(List constraints) throws RecognitionException {   
        Token decl=null;
        Token field=null;
        String text = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1044:3: (decl= ID ':' field= ID '->' text= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1044:3: decl= ID ':' field= ID '->' text= paren_chunk
            {
            decl=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2755); 
            match(input,38,FOLLOW_38_in_predicate2757); 
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2761); 
            match(input,70,FOLLOW_70_in_predicate2763); 
            pushFollow(FOLLOW_paren_chunk_in_predicate2767);
            text=paren_chunk();
            _fsp--;

            
            		        String body = text.substring(1, text.length()-1);
            			PredicateDescr d = new PredicateDescr(field.getText(), decl.getText(), body );
            			constraints.add( d );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1052:1: paren_chunk returns [String text] : loc= '(' ')' ;
    public String paren_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;

        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1054:10: (loc= '(' ')' )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1054:10: loc= '(' ')'
            {
            
            		    ((CommonTokenStream)input).setTokenTypeChannel(WS, Token.DEFAULT_CHANNEL);
            	        
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_paren_chunk2801); 
            
            		    int parenCounter = 1;
            		    StringBuffer buf = new StringBuffer();
            		    buf.append(loc.getText());
            
                                do {
                                    Token nextToken = input.LT(1);
                                    buf.append( nextToken.getText() );
                                    
                                    int nextTokenId = nextToken.getType();
                                    if( nextTokenId == RIGHT_PAREN ) {
                                        parenCounter--;
                                    } else if( nextTokenId == LEFT_PAREN ) {
                                        parenCounter++;
                                    }
                                    if( parenCounter == 0 ) {
                                        break;
                                    }
                                    input.consume();
            		    } while( true );
            		    text = buf.toString();
            		    ((CommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
            		
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_paren_chunk2824); 

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


    // $ANTLR start retval_constraint
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1084:1: retval_constraint returns [String text] : c= paren_chunk ;
    public String retval_constraint() throws RecognitionException {   
        String text = null;

        String c = null;


        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1089:3: (c= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1089:3: c= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint2851);
            c=paren_chunk();
            _fsp--;

             text = c.substring(1, c.length()-1); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1097:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public BaseDescr lhs_or() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1102:3: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1102:3: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {
             OrDescr or = null; 
            pushFollow(FOLLOW_lhs_and_in_lhs_or2888);
            left=lhs_and();
            _fsp--;

            d = left; 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1104:3: ( ('or'|'||')right= lhs_and )*
            loop46:
            do {
                int alt46=2;
                int LA46_0 = input.LA(1);
                if ( ((LA46_0>=57 && LA46_0<=58)) ) {
                    alt46=1;
                }


                switch (alt46) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1104:5: ('or'|'||')right= lhs_and
            	    {
            	    if ( (input.LA(1)>=57 && input.LA(1)<=58) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or2897);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_and_in_lhs_or2907);
            	    right=lhs_and();
            	    _fsp--;

            	    
            	    				if ( or == null ) {
            	    					or = new OrDescr();
            	    					or.addDescr( left );
            	    					d = or;
            	    				}
            	    				
            	    				or.addDescr( right );
            	    			

            	    }
            	    break;

            	default :
            	    break loop46;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1118:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public BaseDescr lhs_and() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1123:3: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1123:3: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {
             AndDescr and = null; 
            pushFollow(FOLLOW_lhs_unary_in_lhs_and2947);
            left=lhs_unary();
            _fsp--;

             d = left; 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1125:3: ( ('and'|'&&')right= lhs_unary )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);
                if ( ((LA47_0>=71 && LA47_0<=72)) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1125:5: ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=71 && input.LA(1)<=72) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and2956);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2966);
            	    right=lhs_unary();
            	    _fsp--;

            	    
            	    				if ( and == null ) {
            	    					and = new AndDescr();
            	    					and.addDescr( left );
            	    					d = and;
            	    				}
            	    				
            	    				and.addDescr( right );
            	    			

            	    }
            	    break;

            	default :
            	    break loop47;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1139:1: lhs_unary returns [BaseDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon ;
    public BaseDescr lhs_unary() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr u = null;

        FromDescr fm = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1143:4: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1143:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1143:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' u= lhs ')' )
            int alt49=5;
            switch ( input.LA(1) ) {
            case 73:
                alt49=1;
                break;
            case 74:
                alt49=2;
                break;
            case 75:
                alt49=3;
                break;
            case ID:
                alt49=4;
                break;
            case LEFT_PAREN:
                alt49=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1143:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' u= lhs ')' )", 49, 0, input);

                throw nvae;
            }

            switch (alt49) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1143:6: u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary3003);
                    u=lhs_exist();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1144:5: u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary3011);
                    u=lhs_not();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1145:5: u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary3019);
                    u=lhs_eval();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1146:5: u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )?
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_unary3027);
                    u=lhs_column();
                    _fsp--;

                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1146:18: ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )?
                    int alt48=4;
                    int LA48_0 = input.LA(1);
                    if ( (LA48_0==46) ) {
                        switch ( input.LA(2) ) {
                            case 49:
                                alt48=2;
                                break;
                            case 53:
                                alt48=3;
                                break;
                            case ID:
                                alt48=1;
                                break;
                        }

                    }
                    switch (alt48) {
                        case 1 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1147:14: (fm= from_statement )
                            {
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1147:14: (fm= from_statement )
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1147:15: fm= from_statement
                            {
                            pushFollow(FOLLOW_from_statement_in_lhs_unary3047);
                            fm=from_statement();
                            _fsp--;

                            fm.setColumn((ColumnDescr) u); u=fm;

                            }


                            }
                            break;
                        case 2 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1148:14: (ac= accumulate_statement )
                            {
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1148:14: (ac= accumulate_statement )
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1148:15: ac= accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_lhs_unary3069);
                            ac=accumulate_statement();
                            _fsp--;

                            ac.setResultColumn((ColumnDescr) u); u=ac;

                            }


                            }
                            break;
                        case 3 :
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1149:14: (cs= collect_statement )
                            {
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1149:14: (cs= collect_statement )
                            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1149:15: cs= collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_lhs_unary3090);
                            cs=collect_statement();
                            _fsp--;

                            cs.setResultColumn((ColumnDescr) u); u=cs;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1151:5: '(' u= lhs ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_unary3113); 
                    pushFollow(FOLLOW_lhs_in_lhs_unary3117);
                    u=lhs();
                    _fsp--;

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_unary3119); 

                    }
                    break;

            }

             d = u; 
            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary3129);
            opt_semicolon();
            _fsp--;


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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1156:1: lhs_exist returns [BaseDescr d] : loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public BaseDescr lhs_exist() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1160:4: (loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1160:4: loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,73,FOLLOW_73_in_lhs_exist3153); 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1160:17: ( '(' column= lhs_column ')' | column= lhs_column )
            int alt50=2;
            int LA50_0 = input.LA(1);
            if ( (LA50_0==LEFT_PAREN) ) {
                alt50=1;
            }
            else if ( (LA50_0==ID) ) {
                alt50=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1160:17: ( '(' column= lhs_column ')' | column= lhs_column )", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1160:18: '(' column= lhs_column ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_exist3156); 
                    pushFollow(FOLLOW_lhs_column_in_lhs_exist3160);
                    column=lhs_column();
                    _fsp--;

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_exist3162); 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1160:46: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_exist3168);
                    column=lhs_column();
                    _fsp--;


                    }
                    break;

            }

             
            			d = new ExistsDescr( (ColumnDescr) column ); 
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1167:1: lhs_not returns [NotDescr d] : loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1171:4: (loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1171:4: loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,74,FOLLOW_74_in_lhs_not3198); 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1171:14: ( '(' column= lhs_column ')' | column= lhs_column )
            int alt51=2;
            int LA51_0 = input.LA(1);
            if ( (LA51_0==LEFT_PAREN) ) {
                alt51=1;
            }
            else if ( (LA51_0==ID) ) {
                alt51=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1171:14: ( '(' column= lhs_column ')' | column= lhs_column )", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1171:15: '(' column= lhs_column ')'
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lhs_not3201); 
                    pushFollow(FOLLOW_lhs_column_in_lhs_not3205);
                    column=lhs_column();
                    _fsp--;

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lhs_not3208); 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1171:44: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_not3214);
                    column=lhs_column();
                    _fsp--;


                    }
                    break;

            }

            
            			d = new NotDescr( (ColumnDescr) column ); 
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1178:1: lhs_eval returns [BaseDescr d] : loc= 'eval' c= paren_chunk ;
    public BaseDescr lhs_eval() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        String c = null;


        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1182:4: (loc= 'eval' c= paren_chunk )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1182:4: loc= 'eval' c= paren_chunk
            {
            loc=(Token)input.LT(1);
            match(input,75,FOLLOW_75_in_lhs_eval3242); 
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval3246);
            c=paren_chunk();
            _fsp--;

             
            		        String body = c.substring(1, c.length()-1);
            			checkTrailingSemicolon( body, offset(loc.getLine()) );
            			d = new EvalDescr( body ); 
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1190:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ( '[' ']' )* ;
    public String dotted_name() throws RecognitionException {   
        String name = null;

        Token id=null;

        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1195:3: (id= ID ( '.' id= ID )* ( '[' ']' )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1195:3: id= ID ( '.' id= ID )* ( '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name3277); 
             name=id.getText(); 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1195:32: ( '.' id= ID )*
            loop52:
            do {
                int alt52=2;
                int LA52_0 = input.LA(1);
                if ( (LA52_0==29) ) {
                    alt52=1;
                }


                switch (alt52) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1195:34: '.' id= ID
            	    {
            	    match(input,29,FOLLOW_29_in_dotted_name3283); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name3287); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop52;
                }
            } while (true);

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1195:85: ( '[' ']' )*
            loop53:
            do {
                int alt53=2;
                int LA53_0 = input.LA(1);
                if ( (LA53_0==47) ) {
                    alt53=1;
                }


                switch (alt53) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1195:87: '[' ']'
            	    {
            	    match(input,47,FOLLOW_47_in_dotted_name3296); 
            	    match(input,48,FOLLOW_48_in_dotted_name3298); 
            	     name = name + "[]";

            	    }
            	    break;

            	default :
            	    break loop53;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1198:1: argument returns [String name] : id= ID ( '[' ']' )* ;
    public String argument() throws RecognitionException {   
        String name = null;

        Token id=null;

        
        		name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1203:3: (id= ID ( '[' ']' )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1203:3: id= ID ( '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument3328); 
             name=id.getText(); 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1203:32: ( '[' ']' )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);
                if ( (LA54_0==47) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1203:34: '[' ']'
            	    {
            	    match(input,47,FOLLOW_47_in_argument3334); 
            	    match(input,48,FOLLOW_48_in_argument3336); 
            	     name = name + "[]";

            	    }
            	    break;

            	default :
            	    break loop54;
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1207:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word = null;

        Token id=null;
        Token str=null;

        
        		word = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1211:4: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt55=11;
            switch ( input.LA(1) ) {
            case ID:
                alt55=1;
                break;
            case 27:
                alt55=2;
                break;
            case 76:
                alt55=3;
                break;
            case 36:
                alt55=4;
                break;
            case 33:
                alt55=5;
                break;
            case 40:
                alt55=6;
                break;
            case 41:
                alt55=7;
                break;
            case 37:
                alt55=8;
                break;
            case RHS:
                alt55=9;
                break;
            case 34:
                alt55=10;
                break;
            case STRING:
                alt55=11;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1207:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );", 55, 0, input);

                throw nvae;
            }

            switch (alt55) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1211:4: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word3364); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1212:4: 'import'
                    {
                    match(input,27,FOLLOW_27_in_word3376); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1213:4: 'use'
                    {
                    match(input,76,FOLLOW_76_in_word3385); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1214:4: 'rule'
                    {
                    match(input,36,FOLLOW_36_in_word3397); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1215:4: 'query'
                    {
                    match(input,33,FOLLOW_33_in_word3408); 
                     word="query"; 

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1216:4: 'salience'
                    {
                    match(input,40,FOLLOW_40_in_word3418); 
                     word="salience"; 

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1217:5: 'no-loop'
                    {
                    match(input,41,FOLLOW_41_in_word3426); 
                     word="no-loop"; 

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1218:4: 'when'
                    {
                    match(input,37,FOLLOW_37_in_word3434); 
                     word="when"; 

                    }
                    break;
                case 9 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1219:4: 'then'
                    {
                    match(input,RHS,FOLLOW_RHS_in_word3445); 
                     word="then"; 

                    }
                    break;
                case 10 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1220:4: 'end'
                    {
                    match(input,34,FOLLOW_34_in_word3456); 
                     word="end"; 

                    }
                    break;
                case 11 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1221:4: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word3470); 
                     word=getString(str);

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


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA9 dfa9 = new DFA9(this);
    public static final String DFA8_eotS =
        "\6\uffff";
    public static final String DFA8_eofS =
        "\6\uffff";
    public static final String DFA8_minS =
        "\2\4\1\uffff\1\60\1\uffff\1\4";
    public static final String DFA8_maxS =
        "\1\4\1\57\1\uffff\1\60\1\uffff\1\57";
    public static final String DFA8_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    public static final String DFA8_specialS =
        "\6\uffff}>";
    public static final String[] DFA8_transition = {
        "\1\1",
        "\1\2\20\uffff\1\4\7\uffff\1\2\2\uffff\1\4\16\uffff\1\3",
        "",
        "\1\5",
        "",
        "\1\2\20\uffff\1\4\12\uffff\1\4\16\uffff\1\3"
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
            return "373:6: (paramType= dotted_name )?";
        }
    }
    public static final String DFA9_eotS =
        "\6\uffff";
    public static final String DFA9_eofS =
        "\6\uffff";
    public static final String DFA9_minS =
        "\2\4\1\uffff\1\60\1\uffff\1\4";
    public static final String DFA9_maxS =
        "\1\4\1\57\1\uffff\1\60\1\uffff\1\57";
    public static final String DFA9_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    public static final String DFA9_specialS =
        "\6\uffff}>";
    public static final String[] DFA9_transition = {
        "\1\1",
        "\1\2\20\uffff\1\4\7\uffff\1\2\2\uffff\1\4\16\uffff\1\3",
        "",
        "\1\5",
        "",
        "\1\2\20\uffff\1\4\12\uffff\1\4\16\uffff\1\3"
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
            return "377:11: (paramType= dotted_name )?";
        }
    }
 

    public static final BitSet FOLLOW_25_in_opt_semicolon39 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit51 = new BitSet(new long[]{0x0000001A98000002L});
    public static final BitSet FOLLOW_statement_in_compilation_unit58 = new BitSet(new long[]{0x0000001A98000002L});
    public static final BitSet FOLLOW_package_statement_in_prolog83 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_package_statement188 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement192 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_import_statement211 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_import_name_in_import_statement215 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_function_import_statement233 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_function_import_statement235 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement239 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name273 = new BitSet(new long[]{0x0000000060000002L});
    public static final BitSet FOLLOW_29_in_import_name279 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_import_name283 = new BitSet(new long[]{0x0000000060000002L});
    public static final BitSet FOLLOW_30_in_import_name293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_global317 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_global321 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_global325 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_global327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_function354 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_function359 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_function365 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function374 = new BitSet(new long[]{0x0000000000200010L});
    public static final BitSet FOLLOW_dotted_name_in_function384 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_argument_in_function390 = new BitSet(new long[]{0x0000000100200000L});
    public static final BitSet FOLLOW_32_in_function404 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_function409 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_argument_in_function415 = new BitSet(new long[]{0x0000000100200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function439 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CURLY_CHUNK_in_function445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_query476 = new BitSet(new long[]{0x0000033608000250L,0x0000000000001000L});
    public static final BitSet FOLLOW_word_in_query480 = new BitSet(new long[]{0x0000000400100010L,0x0000000000000E00L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query494 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_query509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_template535 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_template539 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_opt_semicolon_in_template541 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_template_slot_in_template556 = new BitSet(new long[]{0x0000000400000010L});
    public static final BitSet FOLLOW_34_in_template571 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot605 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_template_slot609 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_rule642 = new BitSet(new long[]{0x0000033608000250L,0x0000000000001000L});
    public static final BitSet FOLLOW_word_in_rule646 = new BitSet(new long[]{0x00003FA100000040L});
    public static final BitSet FOLLOW_rule_attributes_in_rule655 = new BitSet(new long[]{0x0000002000000040L});
    public static final BitSet FOLLOW_37_in_rule664 = new BitSet(new long[]{0x0000004000100050L,0x0000000000000E00L});
    public static final BitSet FOLLOW_38_in_rule666 = new BitSet(new long[]{0x0000000000100050L,0x0000000000000E00L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule684 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RHS_in_rule707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_rule_attributes732 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_rule_attributes734 = new BitSet(new long[]{0x00003F0100000002L});
    public static final BitSet FOLLOW_32_in_rule_attributes743 = new BitSet(new long[]{0x00003F0000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes748 = new BitSet(new long[]{0x00003F0100000002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_salience882 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_INT_in_salience886 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_salience888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_no_loop923 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_no_loop925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_no_loop950 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_no_loop954 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_no_loop956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_auto_focus1002 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_auto_focus1004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_auto_focus1029 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1033 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_auto_focus1035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_activation_group1077 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_STRING_in_activation_group1081 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_activation_group1083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_agenda_group1112 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1116 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_agenda_group1118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_duration1150 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_INT_in_duration1154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1180 = new BitSet(new long[]{0x0000000000100012L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_from_statement1283 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_from_source_in_from_statement1287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1331 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_from_source1333 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_from_source1337 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_47_in_from_source1341 = new BitSet(new long[]{0x00408000000007B0L});
    public static final BitSet FOLLOW_argument_value_in_from_source1345 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_48_in_from_source1347 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1376 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_from_source1378 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_from_source1382 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_from_source1384 = new BitSet(new long[]{0x00408000002007B0L});
    public static final BitSet FOLLOW_argument_list_in_from_source1388 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_from_source1390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1412 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_from_source1414 = new BitSet(new long[]{0x00408000002007B0L});
    public static final BitSet FOLLOW_argument_list_in_from_source1418 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_from_source1420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_accumulate_statement1469 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_accumulate_statement1471 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_accumulate_statement1481 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_accumulate_statement1485 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_accumulate_statement1487 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_accumulate_statement1496 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1500 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_accumulate_statement1502 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_accumulate_statement1511 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1515 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_accumulate_statement1517 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_accumulate_statement1526 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1530 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_accumulate_statement1532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_collect_statement1575 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_53_in_collect_statement1577 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_collect_statement1587 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_collect_statement1591 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_collect_statement1593 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_argument_value_in_argument_list1626 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_32_in_argument_list1642 = new BitSet(new long[]{0x00408000000007B0L});
    public static final BitSet FOLLOW_argument_value_in_argument_list1646 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_STRING_in_argument_value1686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_argument_value1697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_argument_value1710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_argument_value1721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument_value1733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_argument_value1744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inline_map_in_argument_value1761 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inline_array_in_argument_value1777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CURLY_CHUNK_in_inline_map1817 = new BitSet(new long[]{0x00408000000007B0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map1835 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_inline_map1837 = new BitSet(new long[]{0x00408000000007B0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map1841 = new BitSet(new long[]{0x0100000100000800L});
    public static final BitSet FOLLOW_EOL_in_inline_map1884 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_inline_map1888 = new BitSet(new long[]{0x0040800000000FB0L});
    public static final BitSet FOLLOW_EOL_in_inline_map1891 = new BitSet(new long[]{0x00408000000007B0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map1897 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_inline_map1899 = new BitSet(new long[]{0x00408000000007B0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map1903 = new BitSet(new long[]{0x0100000100000800L});
    public static final BitSet FOLLOW_56_in_inline_map1939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_inline_array1983 = new BitSet(new long[]{0x00408000000007B0L});
    public static final BitSet FOLLOW_argument_value_in_inline_array1987 = new BitSet(new long[]{0x0001000100000800L});
    public static final BitSet FOLLOW_EOL_in_inline_array2005 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_inline_array2008 = new BitSet(new long[]{0x0040800000000FB0L});
    public static final BitSet FOLLOW_EOL_in_inline_array2010 = new BitSet(new long[]{0x00408000000007B0L});
    public static final BitSet FOLLOW_argument_value_in_inline_array2015 = new BitSet(new long[]{0x0001000100000800L});
    public static final BitSet FOLLOW_48_in_inline_array2028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding2072 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_fact_binding2082 = new BitSet(new long[]{0x0000000000100010L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding2086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression2118 = new BitSet(new long[]{0x0000000000100010L});
    public static final BitSet FOLLOW_fact_expression_in_paren_in_fact_expression2122 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression2125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression2136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact_expression_in_paren2167 = new BitSet(new long[]{0x0000000000100010L});
    public static final BitSet FOLLOW_fact_expression_in_paren_in_fact_expression_in_paren2171 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact_expression_in_paren2173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression_in_paren2184 = new BitSet(new long[]{0x0600000000000002L});
    public static final BitSet FOLLOW_set_in_fact_expression_in_paren2197 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_fact_in_fact_expression_in_paren2214 = new BitSet(new long[]{0x0600000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_fact2253 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_fact2266 = new BitSet(new long[]{0x0000000000200010L});
    public static final BitSet FOLLOW_constraints_in_fact2274 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_fact2295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints2328 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_predicate_in_constraints2331 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_32_in_constraints2339 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constraint_in_constraints2342 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_predicate_in_constraints2345 = new BitSet(new long[]{0x0000000100000002L});
    public static final BitSet FOLLOW_ID_in_constraint2374 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_constraint2376 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_constraint2386 = new BitSet(new long[]{0xE000000000000002L,0x000000000000003FL});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2402 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_set_in_constraint2421 = new BitSet(new long[]{0xE000000000000000L,0x000000000000003FL});
    public static final BitSet FOLLOW_constraint_expression_in_constraint2438 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_set_in_constraint_expression2490 = new BitSet(new long[]{0x0040000000100790L});
    public static final BitSet FOLLOW_ID_in_constraint_expression2557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint_expression2573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint_expression2596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint_expression2610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint2649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint2660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2673 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2684 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_literal_constraint2696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2727 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_enum_constraint2729 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2733 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_predicate2755 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_predicate2757 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_predicate2761 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_predicate2763 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate2767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_paren_chunk2801 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_paren_chunk2824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint2851 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2888 = new BitSet(new long[]{0x0600000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or2897 = new BitSet(new long[]{0x0000000000100010L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2907 = new BitSet(new long[]{0x0600000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2947 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_set_in_lhs_and2956 = new BitSet(new long[]{0x0000000000100010L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2966 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary3003 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary3011 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary3019 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary3027 = new BitSet(new long[]{0x0000400002000002L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary3047 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary3069 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary3090 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_unary3113 = new BitSet(new long[]{0x0000000000100010L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary3117 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_unary3119 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary3129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_lhs_exist3153 = new BitSet(new long[]{0x0000000000100010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_exist3156 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3160 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_exist3162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3168 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_lhs_not3198 = new BitSet(new long[]{0x0000000000100010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lhs_not3201 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3205 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lhs_not3208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_lhs_eval3242 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval3246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name3277 = new BitSet(new long[]{0x0000800020000002L});
    public static final BitSet FOLLOW_29_in_dotted_name3283 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_dotted_name3287 = new BitSet(new long[]{0x0000800020000002L});
    public static final BitSet FOLLOW_47_in_dotted_name3296 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_48_in_dotted_name3298 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_ID_in_argument3328 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_47_in_argument3334 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_48_in_argument3336 = new BitSet(new long[]{0x0000800000000002L});
    public static final BitSet FOLLOW_ID_in_word3364 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_word3376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_word3385 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_word3397 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_word3408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_word3418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_word3426 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_word3434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RHS_in_word3445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_word3456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word3470 = new BitSet(new long[]{0x0000000000000002L});

}
