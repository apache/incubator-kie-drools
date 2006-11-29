// $ANTLR 3.0b5 D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g 2006-11-29 21:06:30

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "ID", "CURLY_CHUNK", "RHS", "INT", "BOOL", "STRING", "FLOAT", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "NO_CURLY", "LEFT_PAREN", "RIGHT_PAREN", "LEFT_SQUARE", "RIGHT_SQUARE", "NO_PAREN", "MULTI_LINE_COMMENT", "IGNORE", "';'", "'package'", "'import'", "'function'", "'.'", "'.*'", "'global'", "','", "'query'", "'end'", "'template'", "'rule'", "'when'", "':'", "'attributes'", "'salience'", "'no-loop'", "'auto-focus'", "'activation-group'", "'agenda-group'", "'duration'", "'from'", "'accumulate'", "'init'", "'action'", "'result'", "'collect'", "'or'", "'||'", "'&'", "'|'", "'=='", "'>'", "'>='", "'<'", "'<='", "'!='", "'contains'", "'matches'", "'excludes'", "'null'", "'->'", "'['", "']'", "'and'", "'&&'", "'exists'", "'not'", "'eval'", "'use'"
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
    public static final int IGNORE=26;
    public static final int RHS=6;
    public static final int EOL=11;
    public static final int LEFT_SQUARE=22;
    public static final int OctalEscape=16;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=17;
    public static final int MULTI_LINE_COMMENT=25;
    public static final int RIGHT_PAREN=21;
    public static final int RIGHT_SQUARE=23;
    public static final int NO_PAREN=24;
    public static final int ID=4;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=18;

        public DRLParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g"; }

    
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:281:1: opt_semicolon : ( ';' )? ;
    public void opt_semicolon() throws RecognitionException {   
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:282:4: ( ( ';' )? )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:282:4: ( ';' )?
            {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:282:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);
            if ( (LA1_0==27) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:282:4: ';'
                    {
                    match(input,27,FOLLOW_27_in_opt_semicolon39); 

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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:285:1: compilation_unit : prolog ( statement )* ;
    public void compilation_unit() throws RecognitionException {   
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:286:4: ( prolog ( statement )* )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:286:4: prolog ( statement )*
            {
            pushFollow(FOLLOW_prolog_in_compilation_unit51);
            prolog();
            _fsp--;

            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:287:3: ( statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( ((LA2_0>=29 && LA2_0<=30)||LA2_0==33||LA2_0==35||(LA2_0>=37 && LA2_0<=38)) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:287:5: statement
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:290:1: prolog : (name= package_statement )? ;
    public void prolog() throws RecognitionException {   
        String name = null;


        
        		String packageName = "";
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:294:4: ( (name= package_statement )? )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:294:4: (name= package_statement )?
            {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:294:4: (name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0==28) ) {
                alt3=1;
            }
            switch (alt3) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:294:6: name= package_statement
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:300:1: statement : ( import_statement | function_import_statement | global | function | t= template | r= rule | q= query ) ;
    public void statement() throws RecognitionException {   
        FactTemplateDescr t = null;

        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:302:2: ( ( import_statement | function_import_statement | global | function | t= template | r= rule | q= query ) )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:302:2: ( import_statement | function_import_statement | global | function | t= template | r= rule | q= query )
            {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:302:2: ( import_statement | function_import_statement | global | function | t= template | r= rule | q= query )
            int alt4=7;
            switch ( input.LA(1) ) {
            case 29:
                int LA4_1 = input.LA(2);
                if ( (LA4_1==30) ) {
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
            case 33:
                alt4=3;
                break;
            case 30:
                alt4=4;
                break;
            case 37:
                alt4=5;
                break;
            case 38:
                alt4=6;
                break;
            case 35:
                alt4=7;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("302:2: ( import_statement | function_import_statement | global | function | t= template | r= rule | q= query )", 4, 0, input);

                throw nvae;
            }

            switch (alt4) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:302:4: import_statement
                    {
                    pushFollow(FOLLOW_import_statement_in_statement107);
                    import_statement();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:303:10: function_import_statement
                    {
                    pushFollow(FOLLOW_function_import_statement_in_statement118);
                    function_import_statement();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:304:4: global
                    {
                    pushFollow(FOLLOW_global_in_statement123);
                    global();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:305:4: function
                    {
                    pushFollow(FOLLOW_function_in_statement128);
                    function();
                    _fsp--;


                    }
                    break;
                case 5 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:306:10: t= template
                    {
                    pushFollow(FOLLOW_template_in_statement141);
                    t=template();
                    _fsp--;

                    this.packageDescr.addFactTemplate( t ); 

                    }
                    break;
                case 6 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:307:4: r= rule
                    {
                    pushFollow(FOLLOW_rule_in_statement150);
                    r=rule();
                    _fsp--;

                    this.packageDescr.addRule( r ); 

                    }
                    break;
                case 7 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:308:4: q= query
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:312:1: package_statement returns [String packageName] : 'package' name= dotted_name opt_semicolon ;
    public String package_statement() throws RecognitionException {   
        String packageName = null;

        String name = null;


        
        		packageName = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:317:3: ( 'package' name= dotted_name opt_semicolon )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:317:3: 'package' name= dotted_name opt_semicolon
            {
            match(input,28,FOLLOW_28_in_package_statement188); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:324:1: import_statement : 'import' name= import_name opt_semicolon ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:325:4: ( 'import' name= import_name opt_semicolon )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:325:4: 'import' name= import_name opt_semicolon
            {
            match(input,29,FOLLOW_29_in_import_statement211); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:332:1: function_import_statement : 'import' 'function' name= import_name opt_semicolon ;
    public void function_import_statement() throws RecognitionException {   
        String name = null;


        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:333:4: ( 'import' 'function' name= import_name opt_semicolon )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:333:4: 'import' 'function' name= import_name opt_semicolon
            {
            match(input,29,FOLLOW_29_in_function_import_statement233); 
            match(input,30,FOLLOW_30_in_function_import_statement235); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:341:1: import_name returns [String name] : id= ID ( '.' id= ID )* (star= '.*' )? ;
    public String import_name() throws RecognitionException {   
        String name = null;

        Token id=null;
        Token star=null;

        
        		name = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:3: (id= ID ( '.' id= ID )* (star= '.*' )? )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:3: id= ID ( '.' id= ID )* (star= '.*' )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name273); 
             name=id.getText(); 
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:32: ( '.' id= ID )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);
                if ( (LA5_0==31) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:34: '.' id= ID
            	    {
            	    match(input,31,FOLLOW_31_in_import_name279); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_import_name283); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:85: (star= '.*' )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( (LA6_0==32) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:346:86: star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,32,FOLLOW_32_in_import_name293); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:350:1: global : 'global' type= dotted_name id= ID opt_semicolon ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;


        
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:354:3: ( 'global' type= dotted_name id= ID opt_semicolon )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:354:3: 'global' type= dotted_name id= ID opt_semicolon
            {
            match(input,33,FOLLOW_33_in_global317); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:361:1: function : loc= 'function' (retType= dotted_name )? name= ID '(' ( (paramType= dotted_name )? paramName= argument ( ',' (paramType= dotted_name )? paramName= argument )* )? ')' body= CURLY_CHUNK ;
    public void function() throws RecognitionException {   
        Token loc=null;
        Token name=null;
        Token body=null;
        String retType = null;

        String paramType = null;

        String paramName = null;


        
        		FunctionDescr f = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:366:3: (loc= 'function' (retType= dotted_name )? name= ID '(' ( (paramType= dotted_name )? paramName= argument ( ',' (paramType= dotted_name )? paramName= argument )* )? ')' body= CURLY_CHUNK )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:366:3: loc= 'function' (retType= dotted_name )? name= ID '(' ( (paramType= dotted_name )? paramName= argument ( ',' (paramType= dotted_name )? paramName= argument )* )? ')' body= CURLY_CHUNK
            {
            loc=(Token)input.LT(1);
            match(input,30,FOLLOW_30_in_function354); 
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:366:18: (retType= dotted_name )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( (LA7_0==ID) ) {
                int LA7_1 = input.LA(2);
                if ( (LA7_1==ID||LA7_1==31||LA7_1==69) ) {
                    alt7=1;
                }
            }
            switch (alt7) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:366:19: retType= dotted_name
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
            		
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_function374); 
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:373:4: ( (paramType= dotted_name )? paramName= argument ( ',' (paramType= dotted_name )? paramName= argument )* )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( (LA11_0==ID) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:373:6: (paramType= dotted_name )? paramName= argument ( ',' (paramType= dotted_name )? paramName= argument )*
                    {
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:373:6: (paramType= dotted_name )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:373:7: paramType= dotted_name
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
                    				
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:377:5: ( ',' (paramType= dotted_name )? paramName= argument )*
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);
                        if ( (LA10_0==34) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:377:7: ',' (paramType= dotted_name )? paramName= argument
                    	    {
                    	    match(input,34,FOLLOW_34_in_function404); 
                    	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:377:11: (paramType= dotted_name )?
                    	    int alt9=2;
                    	    alt9 = dfa9.predict(input);
                    	    switch (alt9) {
                    	        case 1 :
                    	            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:377:12: paramType= dotted_name
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

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_function439); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:397:1: query returns [QueryDescr query] : loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end' ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query = null;

        Token loc=null;
        String queryName = null;


        
        		query = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:402:3: (loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end' )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:402:3: loc= 'query' queryName= word ( normal_lhs_block[lhs] ) 'end'
            {
            loc=(Token)input.LT(1);
            match(input,35,FOLLOW_35_in_query476); 
            pushFollow(FOLLOW_word_in_query480);
            queryName=word();
            _fsp--;

             
            			query = new QueryDescr( queryName, null ); 
            			query.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            			AndDescr lhs = new AndDescr(); query.setLhs( lhs ); 
            			lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:409:3: ( normal_lhs_block[lhs] )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:411:4: normal_lhs_block[lhs]
            {
            pushFollow(FOLLOW_normal_lhs_block_in_query494);
            normal_lhs_block(lhs);
            _fsp--;


            }

            match(input,36,FOLLOW_36_in_query509); 

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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:418:1: template returns [FactTemplateDescr template] : loc= 'template' templateName= ID opt_semicolon (slot= template_slot )+ 'end' opt_semicolon ;
    public FactTemplateDescr template() throws RecognitionException {   
        FactTemplateDescr template = null;

        Token loc=null;
        Token templateName=null;
        FieldTemplateDescr slot = null;


        
        		template = null;		
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:423:3: (loc= 'template' templateName= ID opt_semicolon (slot= template_slot )+ 'end' opt_semicolon )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:423:3: loc= 'template' templateName= ID opt_semicolon (slot= template_slot )+ 'end' opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,37,FOLLOW_37_in_template535); 
            templateName=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template539); 
            pushFollow(FOLLOW_opt_semicolon_in_template541);
            opt_semicolon();
            _fsp--;

            
            			template = new FactTemplateDescr(templateName.getText());
            			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
            		
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:428:3: (slot= template_slot )+
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
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:429:4: slot= template_slot
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

            match(input,36,FOLLOW_36_in_template571); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:437:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name name= ID opt_semicolon ;
    public FieldTemplateDescr template_slot() throws RecognitionException {   
        FieldTemplateDescr field = null;

        Token name=null;
        String fieldType = null;


        
        		field = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:443:4: (fieldType= dotted_name name= ID opt_semicolon )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:443:4: fieldType= dotted_name name= ID opt_semicolon
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:452:1: rule returns [RuleDescr rule] : loc= 'rule' ruleName= word rule_attributes[rule] (loc= 'when' ( ':' )? ( normal_lhs_block[lhs] ) )? rhs= RHS ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule = null;

        Token loc=null;
        Token rhs=null;
        String ruleName = null;


        
        		rule = null;
        		String consequence = "";
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:458:3: (loc= 'rule' ruleName= word rule_attributes[rule] (loc= 'when' ( ':' )? ( normal_lhs_block[lhs] ) )? rhs= RHS )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:458:3: loc= 'rule' ruleName= word rule_attributes[rule] (loc= 'when' ( ':' )? ( normal_lhs_block[lhs] ) )? rhs= RHS
            {
            loc=(Token)input.LT(1);
            match(input,38,FOLLOW_38_in_rule642); 
            pushFollow(FOLLOW_word_in_rule646);
            ruleName=word();
            _fsp--;

             
            			debug( "start rule: " + ruleName );
            			rule = new RuleDescr( ruleName, null ); 
            			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            pushFollow(FOLLOW_rule_attributes_in_rule655);
            rule_attributes(rule);
            _fsp--;

            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:3: (loc= 'when' ( ':' )? ( normal_lhs_block[lhs] ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0==39) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:5: loc= 'when' ( ':' )? ( normal_lhs_block[lhs] )
                    {
                    loc=(Token)input.LT(1);
                    match(input,39,FOLLOW_39_in_rule664); 
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:16: ( ':' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);
                    if ( (LA13_0==40) ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:465:16: ':'
                            {
                            match(input,40,FOLLOW_40_in_rule666); 

                            }
                            break;

                    }

                     
                    				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                    				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:470:4: ( normal_lhs_block[lhs] )
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:472:5: normal_lhs_block[lhs]
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:495:1: rule_attributes[RuleDescr rule] : ( 'attributes' ':' )? ( ( ',' )? a= rule_attribute )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:497:4: ( ( 'attributes' ':' )? ( ( ',' )? a= rule_attribute )* )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:497:4: ( 'attributes' ':' )? ( ( ',' )? a= rule_attribute )*
            {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:497:4: ( 'attributes' ':' )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( (LA15_0==41) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:497:5: 'attributes' ':'
                    {
                    match(input,41,FOLLOW_41_in_rule_attributes732); 
                    match(input,40,FOLLOW_40_in_rule_attributes734); 

                    }
                    break;

            }

            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:498:4: ( ( ',' )? a= rule_attribute )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                if ( (LA17_0==34||(LA17_0>=42 && LA17_0<=47)) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:498:6: ( ',' )? a= rule_attribute
            	    {
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:498:6: ( ',' )?
            	    int alt16=2;
            	    int LA16_0 = input.LA(1);
            	    if ( (LA16_0==34) ) {
            	        alt16=1;
            	    }
            	    switch (alt16) {
            	        case 1 :
            	            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:498:6: ','
            	            {
            	            match(input,34,FOLLOW_34_in_rule_attributes743); 

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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:507:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d = null;

        AttributeDescr a = null;


        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:512:4: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus )
            int alt18=6;
            switch ( input.LA(1) ) {
            case 42:
                alt18=1;
                break;
            case 43:
                alt18=2;
                break;
            case 46:
                alt18=3;
                break;
            case 47:
                alt18=4;
                break;
            case 45:
                alt18=5;
                break;
            case 44:
                alt18=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("507:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus );", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:512:4: a= salience
                    {
                    pushFollow(FOLLOW_salience_in_rule_attribute789);
                    a=salience();
                    _fsp--;

                     d = a; 

                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:513:5: a= no_loop
                    {
                    pushFollow(FOLLOW_no_loop_in_rule_attribute799);
                    a=no_loop();
                    _fsp--;

                     d = a; 

                    }
                    break;
                case 3 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:514:5: a= agenda_group
                    {
                    pushFollow(FOLLOW_agenda_group_in_rule_attribute810);
                    a=agenda_group();
                    _fsp--;

                     d = a; 

                    }
                    break;
                case 4 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:515:5: a= duration
                    {
                    pushFollow(FOLLOW_duration_in_rule_attribute823);
                    a=duration();
                    _fsp--;

                     d = a; 

                    }
                    break;
                case 5 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:516:5: a= activation_group
                    {
                    pushFollow(FOLLOW_activation_group_in_rule_attribute837);
                    a=activation_group();
                    _fsp--;

                     d = a; 

                    }
                    break;
                case 6 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:517:5: a= auto_focus
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:522:1: salience returns [AttributeDescr d ] : loc= 'salience' i= INT opt_semicolon ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:527:3: (loc= 'salience' i= INT opt_semicolon )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:527:3: loc= 'salience' i= INT opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,42,FOLLOW_42_in_salience882); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:534:1: no_loop returns [AttributeDescr d] : ( (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) );
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:539:3: ( (loc= 'no-loop' opt_semicolon ) | (loc= 'no-loop' t= BOOL opt_semicolon ) )
            int alt19=2;
            int LA19_0 = input.LA(1);
            if ( (LA19_0==43) ) {
                int LA19_1 = input.LA(2);
                if ( (LA19_1==BOOL) ) {
                    alt19=2;
                }
                else if ( (LA19_1==RHS||LA19_1==27||LA19_1==34||LA19_1==39||(LA19_1>=42 && LA19_1<=47)) ) {
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
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:539:3: (loc= 'no-loop' opt_semicolon )
                    {
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:539:3: (loc= 'no-loop' opt_semicolon )
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:540:4: loc= 'no-loop' opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_no_loop923); 
                    pushFollow(FOLLOW_opt_semicolon_in_no_loop925);
                    opt_semicolon();
                    _fsp--;

                    
                    				d = new AttributeDescr( "no-loop", "true" );
                    				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:547:3: (loc= 'no-loop' t= BOOL opt_semicolon )
                    {
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:547:3: (loc= 'no-loop' t= BOOL opt_semicolon )
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:548:4: loc= 'no-loop' t= BOOL opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_no_loop950); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:558:1: auto_focus returns [AttributeDescr d] : ( (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) );
    public AttributeDescr auto_focus() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:563:3: ( (loc= 'auto-focus' opt_semicolon ) | (loc= 'auto-focus' t= BOOL opt_semicolon ) )
            int alt20=2;
            int LA20_0 = input.LA(1);
            if ( (LA20_0==44) ) {
                int LA20_1 = input.LA(2);
                if ( (LA20_1==BOOL) ) {
                    alt20=2;
                }
                else if ( (LA20_1==RHS||LA20_1==27||LA20_1==34||LA20_1==39||(LA20_1>=42 && LA20_1<=47)) ) {
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
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:563:3: (loc= 'auto-focus' opt_semicolon )
                    {
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:563:3: (loc= 'auto-focus' opt_semicolon )
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:564:4: loc= 'auto-focus' opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_auto_focus1002); 
                    pushFollow(FOLLOW_opt_semicolon_in_auto_focus1004);
                    opt_semicolon();
                    _fsp--;

                    
                    				d = new AttributeDescr( "auto-focus", "true" );
                    				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:571:3: (loc= 'auto-focus' t= BOOL opt_semicolon )
                    {
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:571:3: (loc= 'auto-focus' t= BOOL opt_semicolon )
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:572:4: loc= 'auto-focus' t= BOOL opt_semicolon
                    {
                    loc=(Token)input.LT(1);
                    match(input,44,FOLLOW_44_in_auto_focus1029); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:582:1: activation_group returns [AttributeDescr d] : loc= 'activation-group' name= STRING opt_semicolon ;
    public AttributeDescr activation_group() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:587:3: (loc= 'activation-group' name= STRING opt_semicolon )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:587:3: loc= 'activation-group' name= STRING opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,45,FOLLOW_45_in_activation_group1077); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:594:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' name= STRING opt_semicolon ;
    public AttributeDescr agenda_group() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:599:3: (loc= 'agenda-group' name= STRING opt_semicolon )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:599:3: loc= 'agenda-group' name= STRING opt_semicolon
            {
            loc=(Token)input.LT(1);
            match(input,46,FOLLOW_46_in_agenda_group1112); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:607:1: duration returns [AttributeDescr d] : loc= 'duration' i= INT ;
    public AttributeDescr duration() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:612:3: (loc= 'duration' i= INT )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:612:3: loc= 'duration' i= INT
            {
            loc=(Token)input.LT(1);
            match(input,47,FOLLOW_47_in_duration1150); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:620:1: normal_lhs_block[AndDescr descrs] : (d= lhs )* ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        BaseDescr d = null;


        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:622:3: ( (d= lhs )* )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:622:3: (d= lhs )*
            {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:622:3: (d= lhs )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);
                if ( (LA21_0==ID||LA21_0==LEFT_SQUARE||(LA21_0>=73 && LA21_0<=75)) ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:622:5: d= lhs
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:676:1: lhs returns [BaseDescr d] : l= lhs_or ;
    public BaseDescr lhs() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr l = null;


        
        		d=null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:4: (l= lhs_or )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:680:4: l= lhs_or
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:684:1: lhs_column returns [BaseDescr d] : (f= fact_binding | f= fact );
    public BaseDescr lhs_column() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr f = null;


        
        		d=null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:688:4: (f= fact_binding | f= fact )
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( (LA22_0==ID) ) {
                int LA22_1 = input.LA(2);
                if ( (LA22_1==40) ) {
                    alt22=1;
                }
                else if ( (LA22_1==LEFT_SQUARE||LA22_1==31||LA22_1==69) ) {
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
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:688:4: f= fact_binding
                    {
                    pushFollow(FOLLOW_fact_binding_in_lhs_column1246);
                    f=fact_binding();
                    _fsp--;

                     d = f; 

                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:689:4: f= fact
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:692:1: from_statement returns [FromDescr d] : 'from' ds= from_source ;
    public FromDescr from_statement() throws RecognitionException {   
        FromDescr d = null;

        DeclarativeInvokerDescr ds = null;


        
        		d=factory.createFrom();
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:697:4: ( 'from' ds= from_source )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:697:4: 'from' ds= from_source
            {
            match(input,48,FOLLOW_48_in_from_statement1283); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID (arg= square_chunk )? ) | (var= ID '.' method= ID '(' arg= paren_chunk ')' ) | (functionName= ID '(' args= paren_chunk ')' ) );
    public DeclarativeInvokerDescr from_source() throws RecognitionException {   
        DeclarativeInvokerDescr ds = null;

        Token var=null;
        Token field=null;
        Token method=null;
        Token functionName=null;
        String arg = null;

        String args = null;


        
        		ds = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:3: ( (var= ID '.' field= ID (arg= square_chunk )? ) | (var= ID '.' method= ID '(' arg= paren_chunk ')' ) | (functionName= ID '(' args= paren_chunk ')' ) )
            int alt24=3;
            int LA24_0 = input.LA(1);
            if ( (LA24_0==ID) ) {
                int LA24_1 = input.LA(2);
                if ( (LA24_1==31) ) {
                    int LA24_2 = input.LA(3);
                    if ( (LA24_2==ID) ) {
                        int LA24_4 = input.LA(4);
                        if ( (LA24_4==LEFT_SQUARE) ) {
                            int LA24_5 = input.LA(5);
                            if ( (LA24_5==LEFT_SQUARE) ) {
                                int LA24_7 = input.LA(6);
                                if ( (LA24_7==RIGHT_SQUARE) ) {
                                    alt24=2;
                                }
                                else if ( (LA24_7==ID||LA24_7==LEFT_SQUARE||(LA24_7>=73 && LA24_7<=75)) ) {
                                    alt24=1;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID (arg= square_chunk )? ) | (var= ID '.' method= ID '(' arg= paren_chunk ')' ) | (functionName= ID '(' args= paren_chunk ')' ) );", 24, 7, input);

                                    throw nvae;
                                }
                            }
                            else if ( (LA24_5==ID||(LA24_5>=73 && LA24_5<=75)) ) {
                                alt24=1;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID (arg= square_chunk )? ) | (var= ID '.' method= ID '(' arg= paren_chunk ')' ) | (functionName= ID '(' args= paren_chunk ')' ) );", 24, 5, input);

                                throw nvae;
                            }
                        }
                        else if ( (LA24_4==ID||LA24_4==RHS||LA24_4==RIGHT_SQUARE||LA24_4==27||LA24_4==36||(LA24_4>=54 && LA24_4<=55)||LA24_4==69||(LA24_4>=71 && LA24_4<=75)) ) {
                            alt24=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID (arg= square_chunk )? ) | (var= ID '.' method= ID '(' arg= paren_chunk ')' ) | (functionName= ID '(' args= paren_chunk ')' ) );", 24, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID (arg= square_chunk )? ) | (var= ID '.' method= ID '(' arg= paren_chunk ')' ) | (functionName= ID '(' args= paren_chunk ')' ) );", 24, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA24_1==LEFT_SQUARE) ) {
                    alt24=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID (arg= square_chunk )? ) | (var= ID '.' method= ID '(' arg= paren_chunk ')' ) | (functionName= ID '(' args= paren_chunk ')' ) );", 24, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("707:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID (arg= square_chunk )? ) | (var= ID '.' method= ID '(' arg= paren_chunk ')' ) | (functionName= ID '(' args= paren_chunk ')' ) );", 24, 0, input);

                throw nvae;
            }
            switch (alt24) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:3: (var= ID '.' field= ID (arg= square_chunk )? )
                    {
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:3: (var= ID '.' field= ID (arg= square_chunk )? )
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:4: var= ID '.' field= ID (arg= square_chunk )?
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1331); 
                    match(input,31,FOLLOW_31_in_from_source1333); 
                    field=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1337); 
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:25: (arg= square_chunk )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    if ( (LA23_0==69) ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:712:27: arg= square_chunk
                            {
                            pushFollow(FOLLOW_square_chunk_in_from_source1344);
                            arg=square_chunk();
                            _fsp--;


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
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:727:3: (var= ID '.' method= ID '(' arg= paren_chunk ')' )
                    {
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:727:3: (var= ID '.' method= ID '(' arg= paren_chunk ')' )
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:727:4: var= ID '.' method= ID '(' arg= paren_chunk ')'
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1374); 
                    match(input,31,FOLLOW_31_in_from_source1376); 
                    method=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1380); 
                    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_from_source1382); 
                    pushFollow(FOLLOW_paren_chunk_in_from_source1386);
                    arg=paren_chunk();
                    _fsp--;

                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_from_source1388); 
                    
                    			  MethodAccessDescr ma = new MethodAccessDescr(var.getText(), method.getText());	
                    			  ma.setLocation( offset(var.getLine()), var.getCharPositionInLine() );
                    			  ma.setArguments(args);
                    			  ds = ma;
                    			

                    }


                    }
                    break;
                case 3 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:736:3: (functionName= ID '(' args= paren_chunk ')' )
                    {
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:736:3: (functionName= ID '(' args= paren_chunk ')' )
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:736:4: functionName= ID '(' args= paren_chunk ')'
                    {
                    functionName=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1410); 
                    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_from_source1412); 
                    pushFollow(FOLLOW_paren_chunk_in_from_source1416);
                    args=paren_chunk();
                    _fsp--;

                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_from_source1418); 
                    
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:749:1: accumulate_statement returns [AccumulateDescr d] : loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')' ;
    public AccumulateDescr accumulate_statement() throws RecognitionException {   
        AccumulateDescr d = null;

        Token loc=null;
        BaseDescr column = null;

        String text = null;


        
        		d = factory.createAccumulate();
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:754:10: (loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')' )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:754:10: loc= 'from' 'accumulate' '(' column= lhs_column ',' 'init' text= paren_chunk ',' 'action' text= paren_chunk ',' 'result' text= paren_chunk ')'
            {
            loc=(Token)input.LT(1);
            match(input,48,FOLLOW_48_in_accumulate_statement1467); 
            match(input,49,FOLLOW_49_in_accumulate_statement1469); 
             
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_accumulate_statement1479); 
            pushFollow(FOLLOW_lhs_column_in_accumulate_statement1483);
            column=lhs_column();
            _fsp--;

            match(input,34,FOLLOW_34_in_accumulate_statement1485); 
            
            		        d.setSourceColumn( (ColumnDescr)column );
            		
            match(input,50,FOLLOW_50_in_accumulate_statement1494); 
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1498);
            text=paren_chunk();
            _fsp--;

            match(input,34,FOLLOW_34_in_accumulate_statement1500); 
            
            		        d.setInitCode( text.substring(1, text.length()-1) );
            		
            match(input,51,FOLLOW_51_in_accumulate_statement1509); 
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1513);
            text=paren_chunk();
            _fsp--;

            match(input,34,FOLLOW_34_in_accumulate_statement1515); 
            
            		        d.setActionCode( text.substring(1, text.length()-1) );
            		
            match(input,52,FOLLOW_52_in_accumulate_statement1524); 
            pushFollow(FOLLOW_paren_chunk_in_accumulate_statement1528);
            text=paren_chunk();
            _fsp--;

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_accumulate_statement1530); 
            
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:776:1: collect_statement returns [CollectDescr d] : loc= 'from' 'collect' '(' column= lhs_column ')' ;
    public CollectDescr collect_statement() throws RecognitionException {   
        CollectDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = factory.createCollect();
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:781:10: (loc= 'from' 'collect' '(' column= lhs_column ')' )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:781:10: loc= 'from' 'collect' '(' column= lhs_column ')'
            {
            loc=(Token)input.LT(1);
            match(input,48,FOLLOW_48_in_collect_statement1573); 
            match(input,53,FOLLOW_53_in_collect_statement1575); 
             
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_collect_statement1585); 
            pushFollow(FOLLOW_lhs_column_in_collect_statement1589);
            column=lhs_column();
            _fsp--;

            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_collect_statement1591); 
            
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


    // $ANTLR start fact_binding
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:827:1: fact_binding returns [BaseDescr d] : id= ID ':' fe= fact_expression[id.getText()] ;
    public BaseDescr fact_binding() throws RecognitionException {   
        BaseDescr d = null;

        Token id=null;
        BaseDescr fe = null;


        
        		d=null;
        		boolean multi=false;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:833:4: (id= ID ':' fe= fact_expression[id.getText()] )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:833:4: id= ID ':' fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding1627); 
            match(input,40,FOLLOW_40_in_fact_binding1637); 
            pushFollow(FOLLOW_fact_expression_in_fact_binding1641);
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:841:2: fact_expression[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression_in_paren[id] ')' | f= fact );
    public BaseDescr fact_expression(String id) throws RecognitionException {   
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:846:5: ( '(' fe= fact_expression_in_paren[id] ')' | f= fact )
            int alt25=2;
            int LA25_0 = input.LA(1);
            if ( (LA25_0==LEFT_SQUARE) ) {
                alt25=1;
            }
            else if ( (LA25_0==ID) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("841:2: fact_expression[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression_in_paren[id] ')' | f= fact );", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:846:5: '(' fe= fact_expression_in_paren[id] ')'
                    {
                    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_fact_expression1673); 
                    pushFollow(FOLLOW_fact_expression_in_paren_in_fact_expression1677);
                    fe=fact_expression_in_paren(id);
                    _fsp--;

                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_fact_expression1680); 
                     pd=fe; 

                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:847:6: f= fact
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression1691);
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:855:2: fact_expression_in_paren[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ('or'|'||')f= fact )* );
    public BaseDescr fact_expression_in_paren(String id) throws RecognitionException {   
        BaseDescr pd = null;

        BaseDescr fe = null;

        BaseDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:860:5: ( '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ('or'|'||')f= fact )* )
            int alt27=2;
            int LA27_0 = input.LA(1);
            if ( (LA27_0==LEFT_SQUARE) ) {
                alt27=1;
            }
            else if ( (LA27_0==ID) ) {
                alt27=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("855:2: fact_expression_in_paren[String id] returns [BaseDescr pd] : ( '(' fe= fact_expression_in_paren[id] ')' | f= fact ( ('or'|'||')f= fact )* );", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:860:5: '(' fe= fact_expression_in_paren[id] ')'
                    {
                    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_fact_expression_in_paren1722); 
                    pushFollow(FOLLOW_fact_expression_in_paren_in_fact_expression_in_paren1726);
                    fe=fact_expression_in_paren(id);
                    _fsp--;

                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_fact_expression_in_paren1728); 
                     pd=fe; 

                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:861:6: f= fact ( ('or'|'||')f= fact )*
                    {
                    pushFollow(FOLLOW_fact_in_fact_expression_in_paren1739);
                    f=fact();
                    _fsp--;

                    
                     			((ColumnDescr)f).setIdentifier( id );
                     			pd = f;
                     		
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:866:4: ( ('or'|'||')f= fact )*
                    loop26:
                    do {
                        int alt26=2;
                        int LA26_0 = input.LA(1);
                        if ( ((LA26_0>=54 && LA26_0<=55)) ) {
                            alt26=1;
                        }


                        switch (alt26) {
                    	case 1 :
                    	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:866:6: ('or'|'||')f= fact
                    	    {
                    	    if ( (input.LA(1)>=54 && input.LA(1)<=55) ) {
                    	        input.consume();
                    	        errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression_in_paren1752);    throw mse;
                    	    }

                    	    	if ( ! multi ) {
                    	     					BaseDescr first = pd;
                    	     					pd = new OrDescr();
                    	     					((OrDescr)pd).addDescr( first );
                    	     					multi=true;
                    	     				}
                    	     			
                    	    pushFollow(FOLLOW_fact_in_fact_expression_in_paren1769);
                    	    f=fact();
                    	    _fsp--;

                    	    
                    	     				((ColumnDescr)f).setIdentifier( id );
                    	     				((OrDescr)pd).addDescr( f );
                    	     			

                    	    }
                    	    break;

                    	default :
                    	    break loop26;
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:882:1: fact returns [BaseDescr d] : id= dotted_name loc= '(' (c= constraints )? endLoc= ')' ;
    public BaseDescr fact() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        Token endLoc=null;
        String id = null;

        List c = null;


        
        		d=null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:886:5: (id= dotted_name loc= '(' (c= constraints )? endLoc= ')' )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:886:5: id= dotted_name loc= '(' (c= constraints )? endLoc= ')'
            {
            pushFollow(FOLLOW_dotted_name_in_fact1808);
            id=dotted_name();
            _fsp--;

             
             			d = new ColumnDescr( id ); 
             		
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_fact1821); 
            
             				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
             			
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:892:7: (c= constraints )?
            int alt28=2;
            int LA28_0 = input.LA(1);
            if ( (LA28_0==ID) ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:892:9: c= constraints
                    {
                    pushFollow(FOLLOW_constraints_in_fact1829);
                    c=constraints();
                    _fsp--;

                    
                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (BaseDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            endLoc=(Token)input.LT(1);
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_fact1850); 
            
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:906:1: constraints returns [List constraints] : ( constraint[constraints] | predicate[constraints] ) ( ',' ( constraint[constraints] | predicate[constraints] ) )* ;
    public List constraints() throws RecognitionException {   
        List constraints = null;

        
        		constraints = new ArrayList();
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:910:4: ( ( constraint[constraints] | predicate[constraints] ) ( ',' ( constraint[constraints] | predicate[constraints] ) )* )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:910:4: ( constraint[constraints] | predicate[constraints] ) ( ',' ( constraint[constraints] | predicate[constraints] ) )*
            {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:910:4: ( constraint[constraints] | predicate[constraints] )
            int alt29=2;
            int LA29_0 = input.LA(1);
            if ( (LA29_0==ID) ) {
                int LA29_1 = input.LA(2);
                if ( (LA29_1==40) ) {
                    int LA29_2 = input.LA(3);
                    if ( (LA29_2==ID) ) {
                        int LA29_4 = input.LA(4);
                        if ( (LA29_4==68) ) {
                            alt29=2;
                        }
                        else if ( (LA29_4==RIGHT_SQUARE||LA29_4==34||(LA29_4>=58 && LA29_4<=66)) ) {
                            alt29=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("910:4: ( constraint[constraints] | predicate[constraints] )", 29, 4, input);

                            throw nvae;
                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("910:4: ( constraint[constraints] | predicate[constraints] )", 29, 2, input);

                        throw nvae;
                    }
                }
                else if ( (LA29_1==RIGHT_SQUARE||LA29_1==34||(LA29_1>=58 && LA29_1<=66)) ) {
                    alt29=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("910:4: ( constraint[constraints] | predicate[constraints] )", 29, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("910:4: ( constraint[constraints] | predicate[constraints] )", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:910:5: constraint[constraints]
                    {
                    pushFollow(FOLLOW_constraint_in_constraints1883);
                    constraint(constraints);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:910:29: predicate[constraints]
                    {
                    pushFollow(FOLLOW_predicate_in_constraints1886);
                    predicate(constraints);
                    _fsp--;


                    }
                    break;

            }

            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:911:3: ( ',' ( constraint[constraints] | predicate[constraints] ) )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);
                if ( (LA31_0==34) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:911:5: ',' ( constraint[constraints] | predicate[constraints] )
            	    {
            	    match(input,34,FOLLOW_34_in_constraints1894); 
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:911:9: ( constraint[constraints] | predicate[constraints] )
            	    int alt30=2;
            	    int LA30_0 = input.LA(1);
            	    if ( (LA30_0==ID) ) {
            	        int LA30_1 = input.LA(2);
            	        if ( (LA30_1==40) ) {
            	            int LA30_2 = input.LA(3);
            	            if ( (LA30_2==ID) ) {
            	                int LA30_4 = input.LA(4);
            	                if ( (LA30_4==68) ) {
            	                    alt30=2;
            	                }
            	                else if ( (LA30_4==RIGHT_SQUARE||LA30_4==34||(LA30_4>=58 && LA30_4<=66)) ) {
            	                    alt30=1;
            	                }
            	                else {
            	                    NoViableAltException nvae =
            	                        new NoViableAltException("911:9: ( constraint[constraints] | predicate[constraints] )", 30, 4, input);

            	                    throw nvae;
            	                }
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("911:9: ( constraint[constraints] | predicate[constraints] )", 30, 2, input);

            	                throw nvae;
            	            }
            	        }
            	        else if ( (LA30_1==RIGHT_SQUARE||LA30_1==34||(LA30_1>=58 && LA30_1<=66)) ) {
            	            alt30=1;
            	        }
            	        else {
            	            NoViableAltException nvae =
            	                new NoViableAltException("911:9: ( constraint[constraints] | predicate[constraints] )", 30, 1, input);

            	            throw nvae;
            	        }
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("911:9: ( constraint[constraints] | predicate[constraints] )", 30, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt30) {
            	        case 1 :
            	            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:911:10: constraint[constraints]
            	            {
            	            pushFollow(FOLLOW_constraint_in_constraints1897);
            	            constraint(constraints);
            	            _fsp--;


            	            }
            	            break;
            	        case 2 :
            	            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:911:34: predicate[constraints]
            	            {
            	            pushFollow(FOLLOW_predicate_in_constraints1900);
            	            predicate(constraints);
            	            _fsp--;


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop31;
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:914:1: constraint[List constraints] : (fb= ID ':' )? f= ID (rd= constraint_expression (con= ('&'|'|')rd= constraint_expression )* )? ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token con=null;
        RestrictionDescr rd = null;


        
        		BaseDescr d = null;
        		FieldConstraintDescr fc = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:920:3: ( (fb= ID ':' )? f= ID (rd= constraint_expression (con= ('&'|'|')rd= constraint_expression )* )? )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:920:3: (fb= ID ':' )? f= ID (rd= constraint_expression (con= ('&'|'|')rd= constraint_expression )* )?
            {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:920:3: (fb= ID ':' )?
            int alt32=2;
            int LA32_0 = input.LA(1);
            if ( (LA32_0==ID) ) {
                int LA32_1 = input.LA(2);
                if ( (LA32_1==40) ) {
                    alt32=1;
                }
            }
            switch (alt32) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:920:5: fb= ID ':'
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1929); 
                    match(input,40,FOLLOW_40_in_constraint1931); 

                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint1941); 
            
            			if ( fb != null ) {
            				d = new FieldBindingDescr( f.getText(), fb.getText() );
            				d.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            				constraints.add( d );
            			} 
            			fc = new FieldConstraintDescr(f.getText());
            			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            		
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:931:3: (rd= constraint_expression (con= ('&'|'|')rd= constraint_expression )* )?
            int alt34=2;
            int LA34_0 = input.LA(1);
            if ( ((LA34_0>=58 && LA34_0<=66)) ) {
                alt34=1;
            }
            switch (alt34) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:932:4: rd= constraint_expression (con= ('&'|'|')rd= constraint_expression )*
                    {
                    pushFollow(FOLLOW_constraint_expression_in_constraint1957);
                    rd=constraint_expression();
                    _fsp--;

                    
                    				fc.addRestriction(rd);
                    				constraints.add(fc);
                    			
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:937:4: (con= ('&'|'|')rd= constraint_expression )*
                    loop33:
                    do {
                        int alt33=2;
                        int LA33_0 = input.LA(1);
                        if ( ((LA33_0>=56 && LA33_0<=57)) ) {
                            alt33=1;
                        }


                        switch (alt33) {
                    	case 1 :
                    	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:938:5: con= ('&'|'|')rd= constraint_expression
                    	    {
                    	    con=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=56 && input.LA(1)<=57) ) {
                    	        input.consume();
                    	        errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint1976);    throw mse;
                    	    }

                    	    
                    	    					if (con.getText().equals("&") ) {								
                    	    						fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	    					} else {
                    	    						fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	    					}							
                    	    				
                    	    pushFollow(FOLLOW_constraint_expression_in_constraint1993);
                    	    rd=constraint_expression();
                    	    _fsp--;

                    	    
                    	    					fc.addRestriction(rd);
                    	    				

                    	    }
                    	    break;

                    	default :
                    	    break loop33;
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:954:1: constraint_expression returns [RestrictionDescr rd] : op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) ;
    public RestrictionDescr constraint_expression() throws RecognitionException {   
        RestrictionDescr rd = null;

        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;


        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:956:3: (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:956:3: op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches'|'excludes') (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            {
            op=(Token)input.LT(1);
            if ( (input.LA(1)>=58 && input.LA(1)<=66) ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint_expression2045);    throw mse;
            }

            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:966:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
            int alt35=4;
            switch ( input.LA(1) ) {
            case ID:
                int LA35_1 = input.LA(2);
                if ( (LA35_1==31) ) {
                    alt35=2;
                }
                else if ( (LA35_1==RIGHT_SQUARE||LA35_1==34||(LA35_1>=56 && LA35_1<=57)) ) {
                    alt35=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("966:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 35, 1, input);

                    throw nvae;
                }
                break;
            case INT:
            case BOOL:
            case STRING:
            case FLOAT:
            case 67:
                alt35=3;
                break;
            case LEFT_SQUARE:
                alt35=4;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("966:3: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 35, 0, input);

                throw nvae;
            }

            switch (alt35) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:966:5: bvc= ID
                    {
                    bvc=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint_expression2112); 
                    
                    				rd = new VariableRestrictionDescr(op.getText(), bvc.getText());
                    			

                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:971:4: lc= enum_constraint
                    {
                    pushFollow(FOLLOW_enum_constraint_in_constraint_expression2128);
                    lc=enum_constraint();
                    _fsp--;

                     
                    				rd  = new LiteralRestrictionDescr(op.getText(), lc, true);
                    			

                    }
                    break;
                case 3 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:976:4: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_constraint_expression2151);
                    lc=literal_constraint();
                    _fsp--;

                     
                    				rd  = new LiteralRestrictionDescr(op.getText(), lc);
                    			

                    }
                    break;
                case 4 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:980:5: rvc= retval_constraint
                    {
                    pushFollow(FOLLOW_retval_constraint_in_constraint_expression2165);
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:987:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) ;
    public String literal_constraint() throws RecognitionException {   
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:991:4: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:991:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:991:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            int alt36=5;
            switch ( input.LA(1) ) {
            case STRING:
                alt36=1;
                break;
            case INT:
                alt36=2;
                break;
            case FLOAT:
                alt36=3;
                break;
            case BOOL:
                alt36=4;
                break;
            case 67:
                alt36=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("991:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )", 36, 0, input);

                throw nvae;
            }

            switch (alt36) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:991:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint2204); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:992:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint2215); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:993:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint2228); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:994:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint2239); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:995:5: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,67,FOLLOW_67_in_literal_constraint2251); 
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:999:1: enum_constraint returns [String text] : (cls= ID '.' en= ID ) ;
    public String enum_constraint() throws RecognitionException {   
        String text = null;

        Token cls=null;
        Token en=null;

        
        		text = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1003:4: ( (cls= ID '.' en= ID ) )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1003:4: (cls= ID '.' en= ID )
            {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1003:4: (cls= ID '.' en= ID )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1003:5: cls= ID '.' en= ID
            {
            cls=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2282); 
            match(input,31,FOLLOW_31_in_enum_constraint2284); 
            en=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2288); 

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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1007:1: predicate[List constraints] : decl= ID ':' field= ID '->' text= paren_chunk ;
    public void predicate(List constraints) throws RecognitionException {   
        Token decl=null;
        Token field=null;
        String text = null;


        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1009:3: (decl= ID ':' field= ID '->' text= paren_chunk )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1009:3: decl= ID ':' field= ID '->' text= paren_chunk
            {
            decl=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2310); 
            match(input,40,FOLLOW_40_in_predicate2312); 
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2316); 
            match(input,68,FOLLOW_68_in_predicate2318); 
            pushFollow(FOLLOW_paren_chunk_in_predicate2322);
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1017:1: paren_chunk returns [String text] : loc= '(' ')' ;
    public String paren_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;

        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1019:10: (loc= '(' ')' )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1019:10: loc= '(' ')'
            {
            
            		    ((CommonTokenStream)input).setTokenTypeChannel(WS, Token.DEFAULT_CHANNEL);
            	        
            loc=(Token)input.LT(1);
            match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_paren_chunk2356); 
            
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
            		
            match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_paren_chunk2379); 

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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1050:1: square_chunk returns [String text] : loc= '[' ']' ;
    public String square_chunk() throws RecognitionException {   
        String text = null;

        Token loc=null;

        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1052:10: (loc= '[' ']' )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1052:10: loc= '[' ']'
            {
            
            		    ((CommonTokenStream)input).setTokenTypeChannel(WS, Token.DEFAULT_CHANNEL);
            	        
            loc=(Token)input.LT(1);
            match(input,69,FOLLOW_69_in_square_chunk2411); 
            
            		    int parenCounter = 1;
            		    StringBuffer buf = new StringBuffer();
            		    buf.append(loc.getText());
            
                                do {
                                    Token nextToken = input.LT(1);
                                    buf.append( nextToken.getText() );
                                    
                                    int nextTokenId = nextToken.getType();
                                    if( nextTokenId == RIGHT_SQUARE ) {
                                        parenCounter--;
                                    } else if( nextTokenId == LEFT_SQUARE ) {
                                        parenCounter++;
                                    }
                                    if( parenCounter == 0 ) {
                                        break;
                                    }
                                    input.consume();
            		    } while( true );
            		    text = buf.toString();
            		    ((CommonTokenStream)input).setTokenTypeChannel(WS, Token.HIDDEN_CHANNEL);
            		
            match(input,70,FOLLOW_70_in_square_chunk2434); 

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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1082:1: retval_constraint returns [String text] : c= paren_chunk ;
    public String retval_constraint() throws RecognitionException {   
        String text = null;

        String c = null;


        
        		text = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1087:3: (c= paren_chunk )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1087:3: c= paren_chunk
            {
            pushFollow(FOLLOW_paren_chunk_in_retval_constraint2462);
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1095:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ('or'|'||')right= lhs_and )* ;
    public BaseDescr lhs_or() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1100:3: (left= lhs_and ( ('or'|'||')right= lhs_and )* )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1100:3: left= lhs_and ( ('or'|'||')right= lhs_and )*
            {
             OrDescr or = null; 
            pushFollow(FOLLOW_lhs_and_in_lhs_or2499);
            left=lhs_and();
            _fsp--;

            d = left; 
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1102:3: ( ('or'|'||')right= lhs_and )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);
                if ( ((LA37_0>=54 && LA37_0<=55)) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1102:5: ('or'|'||')right= lhs_and
            	    {
            	    if ( (input.LA(1)>=54 && input.LA(1)<=55) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or2508);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_and_in_lhs_or2518);
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
            	    break loop37;
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1116:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ('and'|'&&')right= lhs_unary )* ;
    public BaseDescr lhs_and() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr left = null;

        BaseDescr right = null;


        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1121:3: (left= lhs_unary ( ('and'|'&&')right= lhs_unary )* )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1121:3: left= lhs_unary ( ('and'|'&&')right= lhs_unary )*
            {
             AndDescr and = null; 
            pushFollow(FOLLOW_lhs_unary_in_lhs_and2558);
            left=lhs_unary();
            _fsp--;

             d = left; 
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1123:3: ( ('and'|'&&')right= lhs_unary )*
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);
                if ( ((LA38_0>=71 && LA38_0<=72)) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1123:5: ('and'|'&&')right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=71 && input.LA(1)<=72) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and2567);    throw mse;
            	    }

            	    pushFollow(FOLLOW_lhs_unary_in_lhs_and2577);
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
            	    break loop38;
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1137:1: lhs_unary returns [BaseDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon ;
    public BaseDescr lhs_unary() throws RecognitionException {   
        BaseDescr d = null;

        BaseDescr u = null;

        FromDescr fm = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;


        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1141:4: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1141:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' u= lhs ')' ) opt_semicolon
            {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1141:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' u= lhs ')' )
            int alt40=5;
            switch ( input.LA(1) ) {
            case 73:
                alt40=1;
                break;
            case 74:
                alt40=2;
                break;
            case 75:
                alt40=3;
                break;
            case ID:
                alt40=4;
                break;
            case LEFT_SQUARE:
                alt40=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1141:4: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' u= lhs ')' )", 40, 0, input);

                throw nvae;
            }

            switch (alt40) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1141:6: u= lhs_exist
                    {
                    pushFollow(FOLLOW_lhs_exist_in_lhs_unary2614);
                    u=lhs_exist();
                    _fsp--;


                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1142:5: u= lhs_not
                    {
                    pushFollow(FOLLOW_lhs_not_in_lhs_unary2622);
                    u=lhs_not();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1143:5: u= lhs_eval
                    {
                    pushFollow(FOLLOW_lhs_eval_in_lhs_unary2630);
                    u=lhs_eval();
                    _fsp--;


                    }
                    break;
                case 4 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1144:5: u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )?
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_unary2638);
                    u=lhs_column();
                    _fsp--;

                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1144:18: ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )?
                    int alt39=4;
                    int LA39_0 = input.LA(1);
                    if ( (LA39_0==48) ) {
                        switch ( input.LA(2) ) {
                            case 53:
                                alt39=3;
                                break;
                            case 49:
                                alt39=2;
                                break;
                            case ID:
                                alt39=1;
                                break;
                        }

                    }
                    switch (alt39) {
                        case 1 :
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1145:14: (fm= from_statement )
                            {
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1145:14: (fm= from_statement )
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1145:15: fm= from_statement
                            {
                            pushFollow(FOLLOW_from_statement_in_lhs_unary2658);
                            fm=from_statement();
                            _fsp--;

                            fm.setColumn((ColumnDescr) u); u=fm;

                            }


                            }
                            break;
                        case 2 :
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1146:14: (ac= accumulate_statement )
                            {
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1146:14: (ac= accumulate_statement )
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1146:15: ac= accumulate_statement
                            {
                            pushFollow(FOLLOW_accumulate_statement_in_lhs_unary2680);
                            ac=accumulate_statement();
                            _fsp--;

                            ac.setResultColumn((ColumnDescr) u); u=ac;

                            }


                            }
                            break;
                        case 3 :
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1147:14: (cs= collect_statement )
                            {
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1147:14: (cs= collect_statement )
                            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1147:15: cs= collect_statement
                            {
                            pushFollow(FOLLOW_collect_statement_in_lhs_unary2701);
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
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1149:5: '(' u= lhs ')'
                    {
                    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_lhs_unary2724); 
                    pushFollow(FOLLOW_lhs_in_lhs_unary2728);
                    u=lhs();
                    _fsp--;

                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_lhs_unary2730); 

                    }
                    break;

            }

             d = u; 
            pushFollow(FOLLOW_opt_semicolon_in_lhs_unary2740);
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1154:1: lhs_exist returns [BaseDescr d] : loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public BaseDescr lhs_exist() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1158:4: (loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1158:4: loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,73,FOLLOW_73_in_lhs_exist2764); 
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1158:17: ( '(' column= lhs_column ')' | column= lhs_column )
            int alt41=2;
            int LA41_0 = input.LA(1);
            if ( (LA41_0==LEFT_SQUARE) ) {
                alt41=1;
            }
            else if ( (LA41_0==ID) ) {
                alt41=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1158:17: ( '(' column= lhs_column ')' | column= lhs_column )", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1158:18: '(' column= lhs_column ')'
                    {
                    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_lhs_exist2767); 
                    pushFollow(FOLLOW_lhs_column_in_lhs_exist2771);
                    column=lhs_column();
                    _fsp--;

                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_lhs_exist2773); 

                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1158:46: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_exist2779);
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1165:1: lhs_not returns [NotDescr d] : loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d = null;

        Token loc=null;
        BaseDescr column = null;


        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1169:4: (loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1169:4: loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,74,FOLLOW_74_in_lhs_not2809); 
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1169:14: ( '(' column= lhs_column ')' | column= lhs_column )
            int alt42=2;
            int LA42_0 = input.LA(1);
            if ( (LA42_0==LEFT_SQUARE) ) {
                alt42=1;
            }
            else if ( (LA42_0==ID) ) {
                alt42=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1169:14: ( '(' column= lhs_column ')' | column= lhs_column )", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1169:15: '(' column= lhs_column ')'
                    {
                    match(input,LEFT_SQUARE,FOLLOW_LEFT_SQUARE_in_lhs_not2812); 
                    pushFollow(FOLLOW_lhs_column_in_lhs_not2816);
                    column=lhs_column();
                    _fsp--;

                    match(input,RIGHT_SQUARE,FOLLOW_RIGHT_SQUARE_in_lhs_not2819); 

                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1169:44: column= lhs_column
                    {
                    pushFollow(FOLLOW_lhs_column_in_lhs_not2825);
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1176:1: lhs_eval returns [BaseDescr d] : loc= 'eval' c= paren_chunk ;
    public BaseDescr lhs_eval() throws RecognitionException {   
        BaseDescr d = null;

        Token loc=null;
        String c = null;


        
        		d = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1180:4: (loc= 'eval' c= paren_chunk )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1180:4: loc= 'eval' c= paren_chunk
            {
            loc=(Token)input.LT(1);
            match(input,75,FOLLOW_75_in_lhs_eval2853); 
            pushFollow(FOLLOW_paren_chunk_in_lhs_eval2857);
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1188:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ( '[' ']' )* ;
    public String dotted_name() throws RecognitionException {   
        String name = null;

        Token id=null;

        
        		name = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1193:3: (id= ID ( '.' id= ID )* ( '[' ']' )* )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1193:3: id= ID ( '.' id= ID )* ( '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name2888); 
             name=id.getText(); 
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1193:32: ( '.' id= ID )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                if ( (LA43_0==31) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1193:34: '.' id= ID
            	    {
            	    match(input,31,FOLLOW_31_in_dotted_name2894); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name2898); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);

            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1193:85: ( '[' ']' )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);
                if ( (LA44_0==69) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1193:87: '[' ']'
            	    {
            	    match(input,69,FOLLOW_69_in_dotted_name2907); 
            	    match(input,70,FOLLOW_70_in_dotted_name2909); 
            	     name = name + "[]";

            	    }
            	    break;

            	default :
            	    break loop44;
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1196:1: argument returns [String name] : id= ID ( '[' ']' )* ;
    public String argument() throws RecognitionException {   
        String name = null;

        Token id=null;

        
        		name = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1201:3: (id= ID ( '[' ']' )* )
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1201:3: id= ID ( '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument2939); 
             name=id.getText(); 
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1201:32: ( '[' ']' )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);
                if ( (LA45_0==69) ) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1201:34: '[' ']'
            	    {
            	    match(input,69,FOLLOW_69_in_argument2945); 
            	    match(input,70,FOLLOW_70_in_argument2947); 
            	     name = name + "[]";

            	    }
            	    break;

            	default :
            	    break loop45;
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
    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1205:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word = null;

        Token id=null;
        Token str=null;

        
        		word = null;
        	
        try {
            // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1209:4: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt46=11;
            switch ( input.LA(1) ) {
            case ID:
                alt46=1;
                break;
            case 29:
                alt46=2;
                break;
            case 76:
                alt46=3;
                break;
            case 38:
                alt46=4;
                break;
            case 35:
                alt46=5;
                break;
            case 42:
                alt46=6;
                break;
            case 43:
                alt46=7;
                break;
            case 39:
                alt46=8;
                break;
            case RHS:
                alt46=9;
                break;
            case 36:
                alt46=10;
                break;
            case STRING:
                alt46=11;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1205:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );", 46, 0, input);

                throw nvae;
            }

            switch (alt46) {
                case 1 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1209:4: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word2975); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1210:4: 'import'
                    {
                    match(input,29,FOLLOW_29_in_word2987); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1211:4: 'use'
                    {
                    match(input,76,FOLLOW_76_in_word2996); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1212:4: 'rule'
                    {
                    match(input,38,FOLLOW_38_in_word3008); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1213:4: 'query'
                    {
                    match(input,35,FOLLOW_35_in_word3019); 
                     word="query"; 

                    }
                    break;
                case 6 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1214:4: 'salience'
                    {
                    match(input,42,FOLLOW_42_in_word3029); 
                     word="salience"; 

                    }
                    break;
                case 7 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1215:5: 'no-loop'
                    {
                    match(input,43,FOLLOW_43_in_word3037); 
                     word="no-loop"; 

                    }
                    break;
                case 8 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1216:4: 'when'
                    {
                    match(input,39,FOLLOW_39_in_word3045); 
                     word="when"; 

                    }
                    break;
                case 9 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1217:4: 'then'
                    {
                    match(input,RHS,FOLLOW_RHS_in_word3056); 
                     word="then"; 

                    }
                    break;
                case 10 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1218:4: 'end'
                    {
                    match(input,36,FOLLOW_36_in_word3067); 
                     word="end"; 

                    }
                    break;
                case 11 :
                    // D:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\lang\\DRL.g:1219:4: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word3081); 
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
        "\2\4\1\uffff\1\106\1\uffff\1\4";
    public static final String DFA8_maxS =
        "\1\4\1\105\1\uffff\1\106\1\uffff\1\105";
    public static final String DFA8_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    public static final String DFA8_specialS =
        "\6\uffff}>";
    public static final String[] DFA8_transition = {
        "\1\1",
        "\1\2\22\uffff\1\4\7\uffff\1\2\2\uffff\1\4\42\uffff\1\3",
        "",
        "\1\5",
        "",
        "\1\2\22\uffff\1\4\12\uffff\1\4\42\uffff\1\3"
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
        "\2\4\1\uffff\1\106\1\uffff\1\4";
    public static final String DFA9_maxS =
        "\1\4\1\105\1\uffff\1\106\1\uffff\1\105";
    public static final String DFA9_acceptS =
        "\2\uffff\1\1\1\uffff\1\2\1\uffff";
    public static final String DFA9_specialS =
        "\6\uffff}>";
    public static final String[] DFA9_transition = {
        "\1\1",
        "\1\2\22\uffff\1\4\7\uffff\1\2\2\uffff\1\4\42\uffff\1\3",
        "",
        "\1\5",
        "",
        "\1\2\22\uffff\1\4\12\uffff\1\4\42\uffff\1\3"
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
 

    public static final BitSet FOLLOW_27_in_opt_semicolon39 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit51 = new BitSet(new long[]{0x0000006A60000002L});
    public static final BitSet FOLLOW_statement_in_compilation_unit58 = new BitSet(new long[]{0x0000006A60000002L});
    public static final BitSet FOLLOW_package_statement_in_prolog83 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_statement107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_statement118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_statement123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_statement128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_template_in_statement141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rule_in_statement150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_query_in_statement160 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_package_statement188 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement192 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_package_statement194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_import_statement211 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_import_name_in_import_statement215 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_import_statement217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_function_import_statement233 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_function_import_statement235 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement239 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_function_import_statement241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name273 = new BitSet(new long[]{0x0000000180000002L});
    public static final BitSet FOLLOW_31_in_import_name279 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_import_name283 = new BitSet(new long[]{0x0000000180000002L});
    public static final BitSet FOLLOW_32_in_import_name293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_global317 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_global321 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_global325 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_global327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_function354 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_function359 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_function365 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_function374 = new BitSet(new long[]{0x0000000000800010L});
    public static final BitSet FOLLOW_dotted_name_in_function384 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_argument_in_function390 = new BitSet(new long[]{0x0000000400800000L});
    public static final BitSet FOLLOW_34_in_function404 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_dotted_name_in_function409 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_argument_in_function415 = new BitSet(new long[]{0x0000000400800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_function439 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_CURLY_CHUNK_in_function445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_query476 = new BitSet(new long[]{0x00000CD820000250L,0x0000000000001000L});
    public static final BitSet FOLLOW_word_in_query480 = new BitSet(new long[]{0x0000001000400010L,0x0000000000000E00L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query494 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_query509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_template535 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_template539 = new BitSet(new long[]{0x0000000008000010L});
    public static final BitSet FOLLOW_opt_semicolon_in_template541 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_template_slot_in_template556 = new BitSet(new long[]{0x0000001000000010L});
    public static final BitSet FOLLOW_36_in_template571 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot605 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_template_slot609 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_template_slot611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_rule642 = new BitSet(new long[]{0x00000CD820000250L,0x0000000000001000L});
    public static final BitSet FOLLOW_word_in_rule646 = new BitSet(new long[]{0x0000FE8400000040L});
    public static final BitSet FOLLOW_rule_attributes_in_rule655 = new BitSet(new long[]{0x0000008000000040L});
    public static final BitSet FOLLOW_39_in_rule664 = new BitSet(new long[]{0x0000010000400050L,0x0000000000000E00L});
    public static final BitSet FOLLOW_40_in_rule666 = new BitSet(new long[]{0x0000000000400050L,0x0000000000000E00L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule684 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RHS_in_rule707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_rule_attributes732 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_rule_attributes734 = new BitSet(new long[]{0x0000FC0400000002L});
    public static final BitSet FOLLOW_34_in_rule_attributes743 = new BitSet(new long[]{0x0000FC0000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes748 = new BitSet(new long[]{0x0000FC0400000002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute799 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_salience882 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_INT_in_salience886 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_salience888 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_no_loop923 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_no_loop925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_no_loop950 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_no_loop954 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_no_loop956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_auto_focus1002 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_auto_focus1004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_auto_focus1029 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1033 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_auto_focus1035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_activation_group1077 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_STRING_in_activation_group1081 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_activation_group1083 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_agenda_group1112 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1116 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_agenda_group1118 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_duration1150 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_INT_in_duration1154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1180 = new BitSet(new long[]{0x0000000000400012L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_from_statement1283 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_from_source_in_from_statement1287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1331 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_from_source1333 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_from_source1337 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_square_chunk_in_from_source1344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1374 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_from_source1376 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_from_source1380 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_from_source1382 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source1386 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_from_source1388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1410 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_from_source1412 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_from_source1416 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_from_source1418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_accumulate_statement1467 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_accumulate_statement1469 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_accumulate_statement1479 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_accumulate_statement1483 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_accumulate_statement1485 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_accumulate_statement1494 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1498 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_accumulate_statement1500 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_accumulate_statement1509 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1513 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_accumulate_statement1515 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_accumulate_statement1524 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_accumulate_statement1528 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_accumulate_statement1530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_collect_statement1573 = new BitSet(new long[]{0x0020000000000000L});
    public static final BitSet FOLLOW_53_in_collect_statement1575 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_collect_statement1585 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_collect_statement1589 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_collect_statement1591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding1627 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_fact_binding1637 = new BitSet(new long[]{0x0000000000400010L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding1641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_fact_expression1673 = new BitSet(new long[]{0x0000000000400010L});
    public static final BitSet FOLLOW_fact_expression_in_paren_in_fact_expression1677 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_fact_expression1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression1691 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_fact_expression_in_paren1722 = new BitSet(new long[]{0x0000000000400010L});
    public static final BitSet FOLLOW_fact_expression_in_paren_in_fact_expression_in_paren1726 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_fact_expression_in_paren1728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression_in_paren1739 = new BitSet(new long[]{0x00C0000000000002L});
    public static final BitSet FOLLOW_set_in_fact_expression_in_paren1752 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_fact_in_fact_expression_in_paren1769 = new BitSet(new long[]{0x00C0000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_fact1808 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_fact1821 = new BitSet(new long[]{0x0000000000800010L});
    public static final BitSet FOLLOW_constraints_in_fact1829 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_fact1850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constraint_in_constraints1883 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_predicate_in_constraints1886 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_constraints1894 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_constraint_in_constraints1897 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_predicate_in_constraints1900 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_ID_in_constraint1929 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_constraint1931 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_constraint1941 = new BitSet(new long[]{0xFC00000000000002L,0x0000000000000007L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint1957 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_set_in_constraint1976 = new BitSet(new long[]{0xFC00000000000000L,0x0000000000000007L});
    public static final BitSet FOLLOW_constraint_expression_in_constraint1993 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_set_in_constraint_expression2045 = new BitSet(new long[]{0x0000000000400790L,0x0000000000000008L});
    public static final BitSet FOLLOW_ID_in_constraint_expression2112 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint_expression2128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint_expression2151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint_expression2165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint2204 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint2215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_literal_constraint2251 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2282 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_enum_constraint2284 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_predicate2310 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_predicate2312 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_predicate2316 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_68_in_predicate2318 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate2322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_paren_chunk2356 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_paren_chunk2379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_square_chunk2411 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_square_chunk2434 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint2462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2499 = new BitSet(new long[]{0x00C0000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or2508 = new BitSet(new long[]{0x0000000000400010L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2518 = new BitSet(new long[]{0x00C0000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2558 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_set_in_lhs_and2567 = new BitSet(new long[]{0x0000000000400010L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2577 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2614 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2622 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2630 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary2638 = new BitSet(new long[]{0x0001000008000002L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary2658 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary2680 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary2701 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_lhs_unary2724 = new BitSet(new long[]{0x0000000000400010L,0x0000000000000E00L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary2728 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_lhs_unary2730 = new BitSet(new long[]{0x0000000008000002L});
    public static final BitSet FOLLOW_opt_semicolon_in_lhs_unary2740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_lhs_exist2764 = new BitSet(new long[]{0x0000000000400010L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_lhs_exist2767 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist2771 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_lhs_exist2773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist2779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_74_in_lhs_not2809 = new BitSet(new long[]{0x0000000000400010L});
    public static final BitSet FOLLOW_LEFT_SQUARE_in_lhs_not2812 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not2816 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_RIGHT_SQUARE_in_lhs_not2819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not2825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_lhs_eval2853 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2857 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name2888 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_31_in_dotted_name2894 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ID_in_dotted_name2898 = new BitSet(new long[]{0x0000000080000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_dotted_name2907 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_dotted_name2909 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_argument2939 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_69_in_argument2945 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_argument2947 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_word2975 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_word2987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_76_in_word2996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_word3008 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_word3019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_word3029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_word3037 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_word3045 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RHS_in_word3056 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_word3067 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word3081 = new BitSet(new long[]{0x0000000000000002L});

}