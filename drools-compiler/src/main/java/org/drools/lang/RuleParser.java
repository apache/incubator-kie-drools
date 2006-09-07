// $ANTLR 3.0ea8 D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g 2006-09-06 10:48:01

	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.Map;	
	import java.util.HashMap;	
	import java.util.StringTokenizer;
	import org.drools.lang.descr.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class RuleParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "BOOL", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\';\'", "\'package\'", "\'import\'", "\'function\'", "\'.\'", "\'.*\'", "\'expander\'", "\'global\'", "\'(\'", "\',\'", "\')\'", "\'{\'", "\'}\'", "\'query\'", "\'end\'", "\'template\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'attributes\'", "\'salience\'", "\'no-loop\'", "\'auto-focus\'", "\'activation-group\'", "\'agenda-group\'", "\'duration\'", "\'from\'", "\'accumulate\'", "\'init\'", "\'action\'", "\'result\'", "\'null\'", "\'=>\'", "\'[\'", "\']\'", "\'or\'", "\'||\'", "\'&\'", "\'|\'", "\'->\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'use\'", "\'==\'", "\'=\'", "\'>\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'matches\'", "\'excludes\'"
    };
    public static final int BOOL=7;
    public static final int INT=6;
    public static final int WS=11;
    public static final int EOF=-1;
    public static final int MISC=10;
    public static final int STRING=8;
    public static final int EOL=4;
    public static final int FLOAT=9;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=12;
    public static final int MULTI_LINE_COMMENT=14;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=13;
    public static final int ID=5;
        public RuleParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }

    
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
        		CharStream charStream = new ANTLRStringStream( text  + " \n  then"); //need to then so it knows when to end... werd...
        		RuleParserLexer lexer = new RuleParserLexer( charStream );
        		TokenStream tokenStream = new CommonTokenStream( lexer );
        		RuleParser parser = new RuleParser( tokenStream );
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
            
            private String padConsequenceLine(int diff, String cons) {
            	for(int i = 0; i < diff; i++) {
            		cons = cons + '\n';
            	}
            	return cons;
            }
          



    // $ANTLR start opt_eol
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:289:1: opt_eol : ( (';'|EOL))* ;
    public void opt_eol() throws RecognitionException {   
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:290:17: ( ( (';'|EOL))* )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:290:17: ( (';'|EOL))*
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:290:17: ( (';'|EOL))*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);
                if ( LA1_0==EOL ) {
                    alt1=1;
                }
                else if ( LA1_0==15 ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:290:18: (';'|EOL)
            	    {
            	    if ( input.LA(1)==EOL||input.LA(1)==15 ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_opt_eol41);    throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
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
    // $ANTLR end opt_eol


    // $ANTLR start compilation_unit
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:293:1: compilation_unit : opt_eol prolog (r= rule | q= query | t= template | extra_statement )* ;
    public void compilation_unit() throws RecognitionException {   
        RuleDescr r = null;

        QueryDescr q = null;

        FactTemplateDescr t = null;


        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:294:17: ( opt_eol prolog (r= rule | q= query | t= template | extra_statement )* )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:294:17: opt_eol prolog (r= rule | q= query | t= template | extra_statement )*
            {
            following.push(FOLLOW_opt_eol_in_compilation_unit57);
            opt_eol();
            following.pop();

            following.push(FOLLOW_prolog_in_compilation_unit61);
            prolog();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:296:17: (r= rule | q= query | t= template | extra_statement )*
            loop2:
            do {
                int alt2=5;
                alt2 = dfa2.predict(input); 
                switch (alt2) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:296:25: r= rule
            	    {
            	    following.push(FOLLOW_rule_in_compilation_unit70);
            	    r=rule();
            	    following.pop();

            	    this.packageDescr.addRule( r ); 

            	    }
            	    break;
            	case 2 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:297:25: q= query
            	    {
            	    following.push(FOLLOW_query_in_compilation_unit83);
            	    q=query();
            	    following.pop();

            	    this.packageDescr.addRule( q ); 

            	    }
            	    break;
            	case 3 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:298:25: t= template
            	    {
            	    following.push(FOLLOW_template_in_compilation_unit93);
            	    t=template();
            	    following.pop();

            	    this.packageDescr.addFactTemplate ( t ); 

            	    }
            	    break;
            	case 4 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:299:25: extra_statement
            	    {
            	    following.push(FOLLOW_extra_statement_in_compilation_unit101);
            	    extra_statement();
            	    following.pop();


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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:303:1: prolog : opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol ;
    public void prolog() throws RecognitionException {   
        String name = null;


        
        		String packageName = "";
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:307:17: ( opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:307:17: opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_prolog125);
            opt_eol();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:308:17: (name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( LA3_0==16 ) {
                alt3=1;
            }
            else if ( LA3_0==-1||LA3_0==EOL||LA3_0==15||(LA3_0>=17 && LA3_0<=18)||(LA3_0>=21 && LA3_0<=22)||LA3_0==28||(LA3_0>=30 && LA3_0<=31) ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("308:17: (name= package_statement )?", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:308:19: name= package_statement
                    {
                    following.push(FOLLOW_package_statement_in_prolog133);
                    name=package_statement();
                    following.pop();

                     packageName = name; 

                    }
                    break;

            }

             
            			this.packageDescr = new PackageDescr( name ); 
            		
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:312:17: ( extra_statement | expander )*
            loop4:
            do {
                int alt4=3;
                alt4 = dfa4.predict(input); 
                switch (alt4) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:312:25: extra_statement
            	    {
            	    following.push(FOLLOW_extra_statement_in_prolog148);
            	    extra_statement();
            	    following.pop();


            	    }
            	    break;
            	case 2 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:313:25: expander
            	    {
            	    following.push(FOLLOW_expander_in_prolog154);
            	    expander();
            	    following.pop();


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_prolog166);
            opt_eol();
            following.pop();


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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:319:1: package_statement returns [String packageName] : 'package' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   
        String packageName;
        String name = null;


        
        		packageName = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:324:17: ( 'package' opt_eol name= dotted_name ( ';' )? opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:324:17: 'package' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,16,FOLLOW_16_in_package_statement190); 
            following.push(FOLLOW_opt_eol_in_package_statement192);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_package_statement196);
            name=dotted_name();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:324:52: ( ';' )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( LA5_0==15 ) {
                alt5=1;
            }
            else if ( LA5_0==-1||LA5_0==EOL||(LA5_0>=17 && LA5_0<=18)||(LA5_0>=21 && LA5_0<=22)||LA5_0==28||(LA5_0>=30 && LA5_0<=31) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("324:52: ( \';\' )?", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:324:52: ';'
                    {
                    match(input,15,FOLLOW_15_in_package_statement198); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_package_statement201);
            opt_eol();
            following.pop();

            
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:330:1: import_statement : 'import' opt_eol name= import_name ( ';' )? opt_eol ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:331:17: ( 'import' opt_eol name= import_name ( ';' )? opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:331:17: 'import' opt_eol name= import_name ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_import_statement217); 
            following.push(FOLLOW_opt_eol_in_import_statement219);
            opt_eol();
            following.pop();

            following.push(FOLLOW_import_name_in_import_statement223);
            name=import_name();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:331:51: ( ';' )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( LA6_0==15 ) {
                alt6=1;
            }
            else if ( LA6_0==-1||LA6_0==EOL||(LA6_0>=17 && LA6_0<=18)||(LA6_0>=21 && LA6_0<=22)||LA6_0==28||(LA6_0>=30 && LA6_0<=31) ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("331:51: ( \';\' )?", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:331:51: ';'
                    {
                    match(input,15,FOLLOW_15_in_import_statement225); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_import_statement228);
            opt_eol();
            following.pop();

            
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:338:1: function_import_statement : 'import' 'function' opt_eol name= import_name ( ';' )? opt_eol ;
    public void function_import_statement() throws RecognitionException {   
        String name = null;


        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:339:17: ( 'import' 'function' opt_eol name= import_name ( ';' )? opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:339:17: 'import' 'function' opt_eol name= import_name ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_function_import_statement244); 
            match(input,18,FOLLOW_18_in_function_import_statement246); 
            following.push(FOLLOW_opt_eol_in_function_import_statement248);
            opt_eol();
            following.pop();

            following.push(FOLLOW_import_name_in_function_import_statement252);
            name=import_name();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:339:62: ( ';' )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( LA7_0==15 ) {
                alt7=1;
            }
            else if ( LA7_0==-1||LA7_0==EOL||(LA7_0>=17 && LA7_0<=18)||(LA7_0>=21 && LA7_0<=22)||LA7_0==28||(LA7_0>=30 && LA7_0<=31) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("339:62: ( \';\' )?", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:339:62: ';'
                    {
                    match(input,15,FOLLOW_15_in_function_import_statement254); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_function_import_statement257);
            opt_eol();
            following.pop();

            
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:347:1: import_name returns [String name] : id= ID ( '.' id= ID )* (star= '.*' )? ;
    public String import_name() throws RecognitionException {   
        String name;
        Token id=null;
        Token star=null;

        
        		name = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:352:17: (id= ID ( '.' id= ID )* (star= '.*' )? )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:352:17: id= ID ( '.' id= ID )* (star= '.*' )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name289); 
             name=id.getText(); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:352:46: ( '.' id= ID )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);
                if ( LA8_0==19 ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:352:48: '.' id= ID
            	    {
            	    match(input,19,FOLLOW_19_in_import_name295); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_import_name299); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:352:99: (star= '.*' )?
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( LA9_0==20 ) {
                alt9=1;
            }
            else if ( LA9_0==-1||LA9_0==EOL||LA9_0==15||(LA9_0>=17 && LA9_0<=18)||(LA9_0>=21 && LA9_0<=22)||LA9_0==28||(LA9_0>=30 && LA9_0<=31) ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("352:99: (star= \'.*\' )?", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:352:100: star= '.*'
                    {
                    star=(Token)input.LT(1);
                    match(input,20,FOLLOW_20_in_import_name309); 
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


    // $ANTLR start expander
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:354:1: expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void expander() throws RecognitionException {   
        String name = null;


        
        		String config=null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:358:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:358:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {
            match(input,21,FOLLOW_21_in_expander329); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:358:28: (name= dotted_name )?
            int alt10=2;
            int LA10_0 = input.LA(1);
            if ( LA10_0==ID ) {
                alt10=1;
            }
            else if ( LA10_0==-1||LA10_0==EOL||LA10_0==15||(LA10_0>=17 && LA10_0<=18)||(LA10_0>=21 && LA10_0<=22)||LA10_0==28||(LA10_0>=30 && LA10_0<=31) ) {
                alt10=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("358:28: (name= dotted_name )?", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:358:29: name= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_expander334);
                    name=dotted_name();
                    following.pop();


                    }
                    break;

            }

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:358:48: ( ';' )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( LA11_0==15 ) {
                alt11=1;
            }
            else if ( LA11_0==-1||LA11_0==EOL||(LA11_0>=17 && LA11_0<=18)||(LA11_0>=21 && LA11_0<=22)||LA11_0==28||(LA11_0>=30 && LA11_0<=31) ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("358:48: ( \';\' )?", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:358:48: ';'
                    {
                    match(input,15,FOLLOW_15_in_expander338); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_expander341);
            opt_eol();
            following.pop();

            
            			if (expanderResolver == null) 
            				throw new IllegalArgumentException("Unable to use expander. Make sure a expander or dsl config is being passed to the parser. [ExpanderResolver was not set].");
            			if ( expander != null )
            				throw new IllegalArgumentException( "Only one 'expander' statement per file is allowed" );
            			expander = expanderResolver.get( name, config );
            		

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
    // $ANTLR end expander


    // $ANTLR start global
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:368:1: global : 'global' type= dotted_name id= ID ( ';' )? opt_eol ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;


        
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:372:17: ( 'global' type= dotted_name id= ID ( ';' )? opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:372:17: 'global' type= dotted_name id= ID ( ';' )? opt_eol
            {
            match(input,22,FOLLOW_22_in_global365); 
            following.push(FOLLOW_dotted_name_in_global369);
            type=dotted_name();
            following.pop();

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global373); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:372:49: ( ';' )?
            int alt12=2;
            int LA12_0 = input.LA(1);
            if ( LA12_0==15 ) {
                alt12=1;
            }
            else if ( LA12_0==-1||LA12_0==EOL||(LA12_0>=17 && LA12_0<=18)||(LA12_0>=21 && LA12_0<=22)||LA12_0==28||(LA12_0>=30 && LA12_0<=31) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("372:49: ( \';\' )?", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:372:49: ';'
                    {
                    match(input,15,FOLLOW_15_in_global375); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_global378);
            opt_eol();
            following.pop();

            
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:378:1: function : loc= 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol ;
    public void function() throws RecognitionException {   
        Token loc=null;
        Token name=null;
        String retType = null;

        String paramType = null;

        String paramName = null;

        String body = null;


        
        		FunctionDescr f = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:383:17: (loc= 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:383:17: loc= 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,18,FOLLOW_18_in_function404); 
            following.push(FOLLOW_opt_eol_in_function406);
            opt_eol();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:383:40: (retType= dotted_name )?
            int alt13=2;
            alt13 = dfa13.predict(input); 
            switch (alt13) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:383:41: retType= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_function411);
                    retType=dotted_name();
                    following.pop();


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_function415);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function419); 
            following.push(FOLLOW_opt_eol_in_function421);
            opt_eol();
            following.pop();

            
            			//System.err.println( "function :: " + name.getText() );
            			f = new FunctionDescr( name.getText(), retType );
            			f.setLocation(offset(loc.getLine()), loc.getCharPositionInLine());
            		
            match(input,23,FOLLOW_23_in_function430); 
            following.push(FOLLOW_opt_eol_in_function432);
            opt_eol();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:390:25: ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )?
            int alt17=2;
            int LA17_0 = input.LA(1);
            if ( (LA17_0>=EOL && LA17_0<=ID)||LA17_0==15 ) {
                alt17=1;
            }
            else if ( LA17_0==25 ) {
                alt17=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("390:25: ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( \',\' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )?", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:390:33: (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )*
                    {
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:390:33: (paramType= dotted_name )?
                    int alt14=2;
                    alt14 = dfa14.predict(input); 
                    switch (alt14) {
                        case 1 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:390:34: paramType= dotted_name
                            {
                            following.push(FOLLOW_dotted_name_in_function442);
                            paramType=dotted_name();
                            following.pop();


                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_function446);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_argument_name_in_function450);
                    paramName=argument_name();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_function452);
                    opt_eol();
                    following.pop();

                    
                    					f.addParameter( paramType, paramName );
                    				
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:394:33: ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);
                        if ( LA16_0==24 ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:394:41: ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol
                    	    {
                    	    match(input,24,FOLLOW_24_in_function466); 
                    	    following.push(FOLLOW_opt_eol_in_function468);
                    	    opt_eol();
                    	    following.pop();

                    	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:394:53: (paramType= dotted_name )?
                    	    int alt15=2;
                    	    alt15 = dfa15.predict(input); 
                    	    switch (alt15) {
                    	        case 1 :
                    	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:394:54: paramType= dotted_name
                    	            {
                    	            following.push(FOLLOW_dotted_name_in_function473);
                    	            paramType=dotted_name();
                    	            following.pop();


                    	            }
                    	            break;

                    	    }

                    	    following.push(FOLLOW_opt_eol_in_function477);
                    	    opt_eol();
                    	    following.pop();

                    	    following.push(FOLLOW_argument_name_in_function481);
                    	    paramName=argument_name();
                    	    following.pop();

                    	    following.push(FOLLOW_opt_eol_in_function483);
                    	    opt_eol();
                    	    following.pop();

                    	    
                    	    						f.addParameter( paramType, paramName );
                    	    					

                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,25,FOLLOW_25_in_function508); 
            following.push(FOLLOW_opt_eol_in_function512);
            opt_eol();
            following.pop();

            match(input,26,FOLLOW_26_in_function516); 
            following.push(FOLLOW_curly_chunk_in_function523);
            body=curly_chunk();
            following.pop();

            
            				f.setText( body );
            			
            match(input,27,FOLLOW_27_in_function532); 
            
            			packageDescr.addFunction( f );
            		
            following.push(FOLLOW_opt_eol_in_function540);
            opt_eol();
            following.pop();


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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:415:1: query returns [QueryDescr query] : opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query;
        Token loc=null;
        String queryName = null;


        
        		query = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:420:17: ( opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:420:17: opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_query564);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,28,FOLLOW_28_in_query570); 
            following.push(FOLLOW_word_in_query574);
            queryName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_query576);
            opt_eol();
            following.pop();

             
            			query = new QueryDescr( queryName, null ); 
            			query.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            			AndDescr lhs = new AndDescr(); query.setLhs( lhs ); 
            			lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
            int alt18=2;
            switch ( input.LA(1) ) {
            case 23:
                int LA18_1 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 1, input);

                    throw nvae;
                }
                break;
            case EOL:
                int LA18_2 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 2, input);

                    throw nvae;
                }
                break;
            case 29:
                int LA18_3 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 3, input);

                    throw nvae;
                }
                break;
            case 58:
                int LA18_4 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 4, input);

                    throw nvae;
                }
                break;
            case 59:
                int LA18_5 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 5, input);

                    throw nvae;
                }
                break;
            case 60:
                int LA18_6 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 6, input);

                    throw nvae;
                }
                break;
            case ID:
                int LA18_7 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 7, input);

                    throw nvae;
                }
                break;
            case 15:
                int LA18_8 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 8, input);

                    throw nvae;
                }
                break;
            case INT:
            case BOOL:
            case STRING:
            case FLOAT:
            case MISC:
            case WS:
            case SH_STYLE_SINGLE_LINE_COMMENT:
            case C_STYLE_SINGLE_LINE_COMMENT:
            case MULTI_LINE_COMMENT:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
                alt18=1;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:429:25: {...}? expander_lhs_block[lhs]
                    {
                    if ( !( expander != null ) ) {
                        throw new FailedPredicateException(input, "query", " expander != null ");
                    }
                    following.push(FOLLOW_expander_lhs_block_in_query592);
                    expander_lhs_block(lhs);
                    following.pop();


                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:430:27: normal_lhs_block[lhs]
                    {
                    following.push(FOLLOW_normal_lhs_block_in_query600);
                    normal_lhs_block(lhs);
                    following.pop();


                    }
                    break;

            }

            match(input,29,FOLLOW_29_in_query615); 
            following.push(FOLLOW_opt_eol_in_query617);
            opt_eol();
            following.pop();


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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:436:1: template returns [FactTemplateDescr template] : opt_eol loc= 'template' templateName= ID EOL (slot= template_slot )+ 'end' EOL ;
    public FactTemplateDescr template() throws RecognitionException {   
        FactTemplateDescr template;
        Token loc=null;
        Token templateName=null;
        FieldTemplateDescr slot = null;


        
        		template = null;		
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:441:17: ( opt_eol loc= 'template' templateName= ID EOL (slot= template_slot )+ 'end' EOL )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:441:17: opt_eol loc= 'template' templateName= ID EOL (slot= template_slot )+ 'end' EOL
            {
            following.push(FOLLOW_opt_eol_in_template641);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,30,FOLLOW_30_in_template647); 
            templateName=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template651); 
            match(input,EOL,FOLLOW_EOL_in_template653); 
            
            			template = new FactTemplateDescr(templateName.getText());
            			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
            		
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:447:17: (slot= template_slot )+
            int cnt19=0;
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);
                if ( LA19_0==ID ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:448:25: slot= template_slot
            	    {
            	    following.push(FOLLOW_template_slot_in_template668);
            	    slot=template_slot();
            	    following.pop();

            	    
            	    				template.addFieldTemplate(slot);
            	    			

            	    }
            	    break;

            	default :
            	    if ( cnt19 >= 1 ) break loop19;
                        EarlyExitException eee =
                            new EarlyExitException(19, input);
                        throw eee;
                }
                cnt19++;
            } while (true);

            match(input,29,FOLLOW_29_in_template683); 
            match(input,EOL,FOLLOW_EOL_in_template685); 

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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:456:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name name= ID (EOL|';');
    public FieldTemplateDescr template_slot() throws RecognitionException {   
        FieldTemplateDescr field;
        Token name=null;
        String fieldType = null;


        
        		field = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:462:18: (fieldType= dotted_name name= ID (EOL|';'))
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:462:18: fieldType= dotted_name name= ID (EOL|';')
            {
            following.push(FOLLOW_dotted_name_in_template_slot717);
            fieldType=dotted_name();
            following.pop();

            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template_slot721); 
            if ( input.LA(1)==EOL||input.LA(1)==15 ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_template_slot725);    throw mse;
            }

            
            			
            			
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:471:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )? )? 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule;
        Token loc=null;
        Token any=null;
        String ruleName = null;


        
        		rule = null;
        		String consequence = "";
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:477:17: ( opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )? )? 'end' opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:477:17: opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )? )? 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_rule760);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,31,FOLLOW_31_in_rule766); 
            following.push(FOLLOW_word_in_rule770);
            ruleName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_rule772);
            opt_eol();
            following.pop();

             
            			debug( "start rule: " + ruleName );
            			rule = new RuleDescr( ruleName, null ); 
            			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:484:17: ( rule_attributes[rule] )?
            int alt20=2;
            switch ( input.LA(1) ) {
            case 33:
            case 35:
                alt20=1;
                break;
            case EOL:
            case 15:
            case 24:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
                alt20=1;
                break;
            case 32:
                alt20=1;
                break;
            case 34:
                alt20=1;
                break;
            case 29:
                alt20=1;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("484:17: ( rule_attributes[rule] )?", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:484:25: rule_attributes[rule]
                    {
                    following.push(FOLLOW_rule_attributes_in_rule783);
                    rule_attributes(rule);
                    following.pop();


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule793);
            opt_eol();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:487:17: ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )? )?
            int alt28=2;
            int LA28_0 = input.LA(1);
            if ( LA28_0==EOL||LA28_0==15||LA28_0==32||LA28_0==34 ) {
                alt28=1;
            }
            else if ( LA28_0==29 ) {
                alt28=1;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("487:17: ( (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= \'then\' ( \':\' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )? )?", 28, 0, input);

                throw nvae;
            }
            switch (alt28) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:487:18: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )?
                    {
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:487:18: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);
                    if ( LA23_0==32 ) {
                        alt23=1;
                    }
                    else if ( LA23_0==EOL||LA23_0==15||LA23_0==29||LA23_0==34 ) {
                        alt23=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("487:18: (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?", 23, 0, input);

                        throw nvae;
                    }
                    switch (alt23) {
                        case 1 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:487:25: loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                            {
                            loc=(Token)input.LT(1);
                            match(input,32,FOLLOW_32_in_rule802); 
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:487:36: ( ':' )?
                            int alt21=2;
                            int LA21_0 = input.LA(1);
                            if ( LA21_0==33 ) {
                                int LA21_1 = input.LA(2);
                                if ( !( expander != null ) ) {
                                    alt21=1;
                                }
                                else if (  expander != null  ) {
                                    alt21=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("487:36: ( \':\' )?", 21, 1, input);

                                    throw nvae;
                                }
                            }
                            else if ( (LA21_0>=EOL && LA21_0<=32)||(LA21_0>=34 && LA21_0<=71) ) {
                                alt21=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("487:36: ( \':\' )?", 21, 0, input);

                                throw nvae;
                            }
                            switch (alt21) {
                                case 1 :
                                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:487:36: ':'
                                    {
                                    match(input,33,FOLLOW_33_in_rule804); 

                                    }
                                    break;

                            }

                            following.push(FOLLOW_opt_eol_in_rule807);
                            opt_eol();
                            following.pop();

                             
                            				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                            				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                            			
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                            int alt22=2;
                            switch ( input.LA(1) ) {
                            case 23:
                                int LA22_1 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 1, input);

                                    throw nvae;
                                }
                                break;
                            case EOL:
                                int LA22_2 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 2, input);

                                    throw nvae;
                                }
                                break;
                            case 15:
                                int LA22_3 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 3, input);

                                    throw nvae;
                                }
                                break;
                            case 34:
                                int LA22_4 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 4, input);

                                    throw nvae;
                                }
                                break;
                            case 29:
                                int LA22_5 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 5, input);

                                    throw nvae;
                                }
                                break;
                            case 58:
                                int LA22_6 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 6, input);

                                    throw nvae;
                                }
                                break;
                            case 59:
                                int LA22_7 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 7, input);

                                    throw nvae;
                                }
                                break;
                            case 60:
                                int LA22_8 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 8, input);

                                    throw nvae;
                                }
                                break;
                            case ID:
                                int LA22_9 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 9, input);

                                    throw nvae;
                                }
                                break;
                            case INT:
                            case BOOL:
                            case STRING:
                            case FLOAT:
                            case MISC:
                            case WS:
                            case SH_STYLE_SINGLE_LINE_COMMENT:
                            case C_STYLE_SINGLE_LINE_COMMENT:
                            case MULTI_LINE_COMMENT:
                            case 16:
                            case 17:
                            case 18:
                            case 19:
                            case 20:
                            case 21:
                            case 22:
                            case 24:
                            case 25:
                            case 26:
                            case 27:
                            case 28:
                            case 30:
                            case 31:
                            case 32:
                            case 33:
                            case 35:
                            case 36:
                            case 37:
                            case 38:
                            case 39:
                            case 40:
                            case 41:
                            case 42:
                            case 43:
                            case 44:
                            case 45:
                            case 46:
                            case 47:
                            case 48:
                            case 49:
                            case 50:
                            case 51:
                            case 52:
                            case 53:
                            case 54:
                            case 55:
                            case 56:
                            case 57:
                            case 61:
                            case 62:
                            case 63:
                            case 64:
                            case 65:
                            case 66:
                            case 67:
                            case 68:
                            case 69:
                            case 70:
                            case 71:
                                alt22=1;
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 0, input);

                                throw nvae;
                            }

                            switch (alt22) {
                                case 1 :
                                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:493:33: {...}? expander_lhs_block[lhs]
                                    {
                                    if ( !( expander != null ) ) {
                                        throw new FailedPredicateException(input, "rule", " expander != null ");
                                    }
                                    following.push(FOLLOW_expander_lhs_block_in_rule825);
                                    expander_lhs_block(lhs);
                                    following.pop();


                                    }
                                    break;
                                case 2 :
                                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:494:35: normal_lhs_block[lhs]
                                    {
                                    following.push(FOLLOW_normal_lhs_block_in_rule834);
                                    normal_lhs_block(lhs);
                                    following.pop();


                                    }
                                    break;

                            }


                            }
                            break;

                    }

                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:498:17: ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )?
                    int alt27=2;
                    int LA27_0 = input.LA(1);
                    if ( LA27_0==EOL||LA27_0==15||LA27_0==34 ) {
                        alt27=1;
                    }
                    else if ( LA27_0==29 ) {
                        alt27=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("498:17: ( opt_eol loc= \'then\' ( \':\' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )?", 27, 0, input);

                        throw nvae;
                    }
                    switch (alt27) {
                        case 1 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:498:19: opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )*
                            {
                            following.push(FOLLOW_opt_eol_in_rule857);
                            opt_eol();
                            following.pop();

                            loc=(Token)input.LT(1);
                            match(input,34,FOLLOW_34_in_rule861); 
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:498:38: ( ':' )?
                            int alt24=2;
                            int LA24_0 = input.LA(1);
                            if ( LA24_0==33 ) {
                                alt24=1;
                            }
                            else if ( (LA24_0>=EOL && LA24_0<=32)||(LA24_0>=34 && LA24_0<=71) ) {
                                alt24=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("498:38: ( \':\' )?", 24, 0, input);

                                throw nvae;
                            }
                            switch (alt24) {
                                case 1 :
                                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:498:38: ':'
                                    {
                                    match(input,33,FOLLOW_33_in_rule863); 

                                    }
                                    break;

                            }

                            following.push(FOLLOW_opt_eol_in_rule867);
                            opt_eol();
                            following.pop();

                            int prevLine = loc.getLine()+1; 
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:500:25: ( options {greedy=false; } : any= . ( EOL )* )*
                            loop26:
                            do {
                                int alt26=2;
                                int LA26_0 = input.LA(1);
                                if ( LA26_0==29 ) {
                                    alt26=2;
                                }
                                else if ( (LA26_0>=EOL && LA26_0<=28)||(LA26_0>=30 && LA26_0<=71) ) {
                                    alt26=1;
                                }


                                switch (alt26) {
                            	case 1 :
                            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:500:52: any= . ( EOL )*
                            	    {
                            	    any=(Token)input.LT(1);
                            	    matchAny(input); 
                            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:500:58: ( EOL )*
                            	    loop25:
                            	    do {
                            	        int alt25=2;
                            	        int LA25_0 = input.LA(1);
                            	        if ( LA25_0==EOL ) {
                            	            alt25=1;
                            	        }


                            	        switch (alt25) {
                            	    	case 1 :
                            	    	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:500:59: EOL
                            	    	    {
                            	    	    match(input,EOL,FOLLOW_EOL_in_rule894); 

                            	    	    }
                            	    	    break;

                            	    	default :
                            	    	    break loop25;
                            	        }
                            	    } while (true);

                            	    
                            	    					/*
                            	    					if (prevLine = 0) { prevLine = any.getLine() ;}
                            	    					
                            	    					int lineDif = any.getLine() - prevLine - 1;
                            	    					consequence = padConsequenceLines(lineDif, consequence);
                            	    					*/
                            	    					int diff = any.getLine() - prevLine;
                            	    					consequence = padConsequenceLine(diff, consequence);
                            	    					consequence = consequence + " " + any.getText();
                            	    					prevLine = any.getLine();
                            	    				

                            	    }
                            	    break;

                            	default :
                            	    break loop26;
                                }
                            } while (true);

                            
                            				if ( expander != null ) {
                            					String expanded = runThenExpander( consequence, offset(loc.getLine()) );
                            					rule.setConsequence( expanded );
                            				} else { 
                            					rule.setConsequence( consequence ); 
                            				}
                            				rule.setConsequenceLocation(offset(loc.getLine()), loc.getCharPositionInLine());
                            			

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,29,FOLLOW_29_in_rule924); 
            following.push(FOLLOW_opt_eol_in_rule926);
            opt_eol();
            following.pop();

            
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


    // $ANTLR start extra_statement
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:530:1: extra_statement : ( import_statement | function_import_statement | global | function ) ;
    public void extra_statement() throws RecognitionException {   
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:532:9: ( ( import_statement | function_import_statement | global | function ) )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:532:9: ( import_statement | function_import_statement | global | function )
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:532:9: ( import_statement | function_import_statement | global | function )
            int alt29=4;
            switch ( input.LA(1) ) {
            case 17:
                int LA29_1 = input.LA(2);
                if ( LA29_1==18 ) {
                    alt29=2;
                }
                else if ( (LA29_1>=EOL && LA29_1<=ID)||LA29_1==15 ) {
                    alt29=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("532:9: ( import_statement | function_import_statement | global | function )", 29, 1, input);

                    throw nvae;
                }
                break;
            case 22:
                alt29=3;
                break;
            case 18:
                alt29=4;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("532:9: ( import_statement | function_import_statement | global | function )", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:532:17: import_statement
                    {
                    following.push(FOLLOW_import_statement_in_extra_statement946);
                    import_statement();
                    following.pop();


                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:533:17: function_import_statement
                    {
                    following.push(FOLLOW_function_import_statement_in_extra_statement951);
                    function_import_statement();
                    following.pop();


                    }
                    break;
                case 3 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:534:17: global
                    {
                    following.push(FOLLOW_global_in_extra_statement956);
                    global();
                    following.pop();


                    }
                    break;
                case 4 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:535:17: function
                    {
                    following.push(FOLLOW_function_in_extra_statement961);
                    function();
                    following.pop();


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
    // $ANTLR end extra_statement


    // $ANTLR start rule_attributes
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:539:1: rule_attributes[RuleDescr rule] : ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:541:25: ( ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:541:25: ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )*
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:541:25: ( 'attributes' )?
            int alt30=2;
            int LA30_0 = input.LA(1);
            if ( LA30_0==35 ) {
                alt30=1;
            }
            else if ( LA30_0==EOL||LA30_0==15||LA30_0==24||LA30_0==29||(LA30_0>=32 && LA30_0<=34)||(LA30_0>=36 && LA30_0<=41) ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("541:25: ( \'attributes\' )?", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:541:25: 'attributes'
                    {
                    match(input,35,FOLLOW_35_in_rule_attributes980); 

                    }
                    break;

            }

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:541:39: ( ':' )?
            int alt31=2;
            int LA31_0 = input.LA(1);
            if ( LA31_0==33 ) {
                alt31=1;
            }
            else if ( LA31_0==EOL||LA31_0==15||LA31_0==24||LA31_0==29||LA31_0==32||LA31_0==34||(LA31_0>=36 && LA31_0<=41) ) {
                alt31=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("541:39: ( \':\' )?", 31, 0, input);

                throw nvae;
            }
            switch (alt31) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:541:39: ':'
                    {
                    match(input,33,FOLLOW_33_in_rule_attributes983); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule_attributes986);
            opt_eol();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:542:25: ( ( ',' )? a= rule_attribute opt_eol )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                if ( LA33_0==24||(LA33_0>=36 && LA33_0<=41) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:542:33: ( ',' )? a= rule_attribute opt_eol
            	    {
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:542:33: ( ',' )?
            	    int alt32=2;
            	    int LA32_0 = input.LA(1);
            	    if ( LA32_0==24 ) {
            	        alt32=1;
            	    }
            	    else if ( (LA32_0>=36 && LA32_0<=41) ) {
            	        alt32=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("542:33: ( \',\' )?", 32, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt32) {
            	        case 1 :
            	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:542:33: ','
            	            {
            	            match(input,24,FOLLOW_24_in_rule_attributes993); 

            	            }
            	            break;

            	    }

            	    following.push(FOLLOW_rule_attribute_in_rule_attributes998);
            	    a=rule_attribute();
            	    following.pop();

            	    following.push(FOLLOW_opt_eol_in_rule_attributes1000);
            	    opt_eol();
            	    following.pop();

            	    
            	    					rule.addAttribute( a );
            	    				

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
    // $ANTLR end rule_attributes


    // $ANTLR start rule_attribute
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:549:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d;
        AttributeDescr a = null;


        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:554:25: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus )
            int alt34=6;
            switch ( input.LA(1) ) {
            case 36:
                alt34=1;
                break;
            case 37:
                alt34=2;
                break;
            case 40:
                alt34=3;
                break;
            case 41:
                alt34=4;
                break;
            case 39:
                alt34=5;
                break;
            case 38:
                alt34=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("549:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus );", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:554:25: a= salience
                    {
                    following.push(FOLLOW_salience_in_rule_attribute1039);
                    a=salience();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:555:25: a= no_loop
                    {
                    following.push(FOLLOW_no_loop_in_rule_attribute1049);
                    a=no_loop();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 3 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:556:25: a= agenda_group
                    {
                    following.push(FOLLOW_agenda_group_in_rule_attribute1060);
                    a=agenda_group();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 4 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:557:25: a= duration
                    {
                    following.push(FOLLOW_duration_in_rule_attribute1073);
                    a=duration();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 5 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:558:25: a= activation_group
                    {
                    following.push(FOLLOW_activation_group_in_rule_attribute1087);
                    a=activation_group();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 6 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:559:25: a= auto_focus
                    {
                    following.push(FOLLOW_auto_focus_in_rule_attribute1098);
                    a=auto_focus();
                    following.pop();

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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:563:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:568:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:568:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,36,FOLLOW_36_in_salience1131); 
            following.push(FOLLOW_opt_eol_in_salience1133);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience1137); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:568:46: ( ';' )?
            int alt35=2;
            int LA35_0 = input.LA(1);
            if ( LA35_0==15 ) {
                alt35=1;
            }
            else if ( LA35_0==EOL||LA35_0==24||LA35_0==29||LA35_0==32||LA35_0==34||(LA35_0>=36 && LA35_0<=41) ) {
                alt35=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("568:46: ( \';\' )?", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:568:46: ';'
                    {
                    match(input,15,FOLLOW_15_in_salience1139); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_salience1142);
            opt_eol();
            following.pop();

            
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:575:1: no_loop returns [AttributeDescr d] : ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:580:17: ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) )
            int alt38=2;
            int LA38_0 = input.LA(1);
            if ( LA38_0==37 ) {
                int LA38_1 = input.LA(2);
                if ( LA38_1==BOOL ) {
                    alt38=2;
                }
                else if ( LA38_1==EOL||LA38_1==15||LA38_1==24||LA38_1==29||LA38_1==32||LA38_1==34||(LA38_1>=36 && LA38_1<=41) ) {
                    alt38=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("575:1: no_loop returns [AttributeDescr d] : ( (loc= \'no-loop\' opt_eol ( \';\' )? opt_eol ) | (loc= \'no-loop\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 38, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("575:1: no_loop returns [AttributeDescr d] : ( (loc= \'no-loop\' opt_eol ( \';\' )? opt_eol ) | (loc= \'no-loop\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:580:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                    {
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:580:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:581:25: loc= 'no-loop' opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,37,FOLLOW_37_in_no_loop1177); 
                    following.push(FOLLOW_opt_eol_in_no_loop1179);
                    opt_eol();
                    following.pop();

                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:581:47: ( ';' )?
                    int alt36=2;
                    int LA36_0 = input.LA(1);
                    if ( LA36_0==15 ) {
                        alt36=1;
                    }
                    else if ( LA36_0==EOL||LA36_0==24||LA36_0==29||LA36_0==32||LA36_0==34||(LA36_0>=36 && LA36_0<=41) ) {
                        alt36=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("581:47: ( \';\' )?", 36, 0, input);

                        throw nvae;
                    }
                    switch (alt36) {
                        case 1 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:581:47: ';'
                            {
                            match(input,15,FOLLOW_15_in_no_loop1181); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_no_loop1184);
                    opt_eol();
                    following.pop();

                    
                    				d = new AttributeDescr( "no-loop", "true" );
                    				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:588:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                    {
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:588:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:589:25: loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,37,FOLLOW_37_in_no_loop1209); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1213); 
                    following.push(FOLLOW_opt_eol_in_no_loop1215);
                    opt_eol();
                    following.pop();

                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:589:54: ( ';' )?
                    int alt37=2;
                    int LA37_0 = input.LA(1);
                    if ( LA37_0==15 ) {
                        alt37=1;
                    }
                    else if ( LA37_0==EOL||LA37_0==24||LA37_0==29||LA37_0==32||LA37_0==34||(LA37_0>=36 && LA37_0<=41) ) {
                        alt37=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("589:54: ( \';\' )?", 37, 0, input);

                        throw nvae;
                    }
                    switch (alt37) {
                        case 1 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:589:54: ';'
                            {
                            match(input,15,FOLLOW_15_in_no_loop1217); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_no_loop1220);
                    opt_eol();
                    following.pop();

                    
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:599:1: auto_focus returns [AttributeDescr d] : ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr auto_focus() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token t=null;

        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:604:17: ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) )
            int alt41=2;
            int LA41_0 = input.LA(1);
            if ( LA41_0==38 ) {
                int LA41_1 = input.LA(2);
                if ( LA41_1==BOOL ) {
                    alt41=2;
                }
                else if ( LA41_1==EOL||LA41_1==15||LA41_1==24||LA41_1==29||LA41_1==32||LA41_1==34||(LA41_1>=36 && LA41_1<=41) ) {
                    alt41=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("599:1: auto_focus returns [AttributeDescr d] : ( (loc= \'auto-focus\' opt_eol ( \';\' )? opt_eol ) | (loc= \'auto-focus\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 41, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("599:1: auto_focus returns [AttributeDescr d] : ( (loc= \'auto-focus\' opt_eol ( \';\' )? opt_eol ) | (loc= \'auto-focus\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:604:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                    {
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:604:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:605:25: loc= 'auto-focus' opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_auto_focus1266); 
                    following.push(FOLLOW_opt_eol_in_auto_focus1268);
                    opt_eol();
                    following.pop();

                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:605:50: ( ';' )?
                    int alt39=2;
                    int LA39_0 = input.LA(1);
                    if ( LA39_0==15 ) {
                        alt39=1;
                    }
                    else if ( LA39_0==EOL||LA39_0==24||LA39_0==29||LA39_0==32||LA39_0==34||(LA39_0>=36 && LA39_0<=41) ) {
                        alt39=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("605:50: ( \';\' )?", 39, 0, input);

                        throw nvae;
                    }
                    switch (alt39) {
                        case 1 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:605:50: ';'
                            {
                            match(input,15,FOLLOW_15_in_auto_focus1270); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_auto_focus1273);
                    opt_eol();
                    following.pop();

                    
                    				d = new AttributeDescr( "auto-focus", "true" );
                    				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:612:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                    {
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:612:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:613:25: loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_auto_focus1298); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1302); 
                    following.push(FOLLOW_opt_eol_in_auto_focus1304);
                    opt_eol();
                    following.pop();

                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:613:57: ( ';' )?
                    int alt40=2;
                    int LA40_0 = input.LA(1);
                    if ( LA40_0==15 ) {
                        alt40=1;
                    }
                    else if ( LA40_0==EOL||LA40_0==24||LA40_0==29||LA40_0==32||LA40_0==34||(LA40_0>=36 && LA40_0<=41) ) {
                        alt40=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("613:57: ( \';\' )?", 40, 0, input);

                        throw nvae;
                    }
                    switch (alt40) {
                        case 1 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:613:57: ';'
                            {
                            match(input,15,FOLLOW_15_in_auto_focus1306); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_auto_focus1309);
                    opt_eol();
                    following.pop();

                    
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:623:1: activation_group returns [AttributeDescr d] : loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr activation_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:628:17: (loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:628:17: loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,39,FOLLOW_39_in_activation_group1351); 
            following.push(FOLLOW_opt_eol_in_activation_group1353);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1357); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:628:60: ( ';' )?
            int alt42=2;
            int LA42_0 = input.LA(1);
            if ( LA42_0==15 ) {
                alt42=1;
            }
            else if ( LA42_0==EOL||LA42_0==24||LA42_0==29||LA42_0==32||LA42_0==34||(LA42_0>=36 && LA42_0<=41) ) {
                alt42=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("628:60: ( \';\' )?", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:628:60: ';'
                    {
                    match(input,15,FOLLOW_15_in_activation_group1359); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_activation_group1362);
            opt_eol();
            following.pop();

            
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:635:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr agenda_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;

        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:640:17: (loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:640:17: loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,40,FOLLOW_40_in_agenda_group1391); 
            following.push(FOLLOW_opt_eol_in_agenda_group1393);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1397); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:640:56: ( ';' )?
            int alt43=2;
            int LA43_0 = input.LA(1);
            if ( LA43_0==15 ) {
                alt43=1;
            }
            else if ( LA43_0==EOL||LA43_0==24||LA43_0==29||LA43_0==32||LA43_0==34||(LA43_0>=36 && LA43_0<=41) ) {
                alt43=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("640:56: ( \';\' )?", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:640:56: ';'
                    {
                    match(input,15,FOLLOW_15_in_agenda_group1399); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_agenda_group1402);
            opt_eol();
            following.pop();

            
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:648:1: duration returns [AttributeDescr d] : loc= 'duration' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr duration() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:653:17: (loc= 'duration' opt_eol i= INT ( ';' )? opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:653:17: loc= 'duration' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,41,FOLLOW_41_in_duration1434); 
            following.push(FOLLOW_opt_eol_in_duration1436);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1440); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:653:46: ( ';' )?
            int alt44=2;
            int LA44_0 = input.LA(1);
            if ( LA44_0==15 ) {
                alt44=1;
            }
            else if ( LA44_0==EOL||LA44_0==24||LA44_0==29||LA44_0==32||LA44_0==34||(LA44_0>=36 && LA44_0<=41) ) {
                alt44=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("653:46: ( \';\' )?", 44, 0, input);

                throw nvae;
            }
            switch (alt44) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:653:46: ';'
                    {
                    match(input,15,FOLLOW_15_in_duration1442); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_duration1445);
            opt_eol();
            following.pop();

            
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:661:1: normal_lhs_block[AndDescr descrs] : (d= lhs opt_eol )* opt_eol ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;


        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:663:17: ( (d= lhs opt_eol )* opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:663:17: (d= lhs opt_eol )* opt_eol
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:663:17: (d= lhs opt_eol )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);
                if ( LA45_0==ID||LA45_0==23||(LA45_0>=58 && LA45_0<=60) ) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:663:25: d= lhs opt_eol
            	    {
            	    following.push(FOLLOW_lhs_in_normal_lhs_block1471);
            	    d=lhs();
            	    following.pop();

            	    following.push(FOLLOW_opt_eol_in_normal_lhs_block1473);
            	    opt_eol();
            	    following.pop();

            	     descrs.addDescr( d ); 

            	    }
            	    break;

            	default :
            	    break loop45;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_normal_lhs_block1485);
            opt_eol();
            following.pop();


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


    // $ANTLR start expander_lhs_block
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:671:1: expander_lhs_block[AndDescr descrs] : ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )* ;
    public void expander_lhs_block(AndDescr descrs) throws RecognitionException {   
        Token loc=null;
        String text = null;


        
        		String lhsBlock = null;
        		String eol = System.getProperty( "line.separator" );
        		List constraints = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:678:17: ( ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )* )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:678:17: ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )*
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:678:17: ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )*
            loop47:
            do {
                int alt47=2;
                switch ( input.LA(1) ) {
                case 29:
                    alt47=2;
                    break;
                case EOL:
                    alt47=2;
                    break;
                case 34:
                    alt47=2;
                    break;
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 30:
                case 31:
                case 32:
                case 33:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    alt47=1;
                    break;
                case 15:
                    alt47=2;
                    break;

                }

                switch (alt47) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:679:25: text= paren_chunk loc= EOL ( EOL )*
            	    {
            	    following.push(FOLLOW_paren_chunk_in_expander_lhs_block1526);
            	    text=paren_chunk();
            	    following.pop();

            	    loc=(Token)input.LT(1);
            	    match(input,EOL,FOLLOW_EOL_in_expander_lhs_block1530); 
            	    
            	    				//only expand non null
            	    				if (text != null) {
            	    					if (text.trim().startsWith("-")) {
            	    						if (constraints == null) {
            	    							constraints = new ArrayList();
            	    						}
            	    						constraints.add(runWhenExpander( text, offset(loc.getLine())));
            	    					} else {
            	    						if (constraints != null) {
            	    							lhsBlock = applyConstraints(constraints, lhsBlock);
            	    							constraints = null;
            	    						}
            	    					
            	    					
            	    						if (lhsBlock == null) {					
            	    							lhsBlock = runWhenExpander( text, offset(loc.getLine()));
            	    						} else {
            	    							lhsBlock = lhsBlock + eol + runWhenExpander( text, offset(loc.getLine()));
            	    						}
            	    					}
            	    					text = null;
            	    				}
            	    			
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:705:17: ( EOL )*
            	    loop46:
            	    do {
            	        int alt46=2;
            	        int LA46_0 = input.LA(1);
            	        if ( LA46_0==EOL ) {
            	            alt46=1;
            	        }


            	        switch (alt46) {
            	    	case 1 :
            	    	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:705:18: EOL
            	    	    {
            	    	    match(input,EOL,FOLLOW_EOL_in_expander_lhs_block1545); 

            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop46;
            	        }
            	    } while (true);


            	    }
            	    break;

            	default :
            	    break loop47;
                }
            } while (true);

            	
            			//flush out any constraints left handing before the RHS
            			lhsBlock = applyConstraints(constraints, lhsBlock);
            			if (lhsBlock != null) {
            				reparseLhs(lhsBlock, descrs);
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
    // $ANTLR end expander_lhs_block


    // $ANTLR start lhs
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:719:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   
        PatternDescr d;
        PatternDescr l = null;


        
        		d=null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:723:17: (l= lhs_or )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:723:17: l= lhs_or
            {
            following.push(FOLLOW_lhs_or_in_lhs1587);
            l=lhs_or();
            following.pop();

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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:727:1: lhs_column returns [PatternDescr d] : (f= fact_binding | f= fact );
    public PatternDescr lhs_column() throws RecognitionException {   
        PatternDescr d;
        PatternDescr f = null;


        
        		d=null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:731:17: (f= fact_binding | f= fact )
            int alt48=2;
            alt48 = dfa48.predict(input); 
            switch (alt48) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:731:17: f= fact_binding
                    {
                    following.push(FOLLOW_fact_binding_in_lhs_column1615);
                    f=fact_binding();
                    following.pop();

                     d = f; 

                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:732:17: f= fact
                    {
                    following.push(FOLLOW_fact_in_lhs_column1624);
                    f=fact();
                    following.pop();

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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:735:1: from_statement returns [FromDescr d] : 'from' opt_eol ds= from_source ;
    public FromDescr from_statement() throws RecognitionException {   
        FromDescr d;
        DeclarativeInvokerDescr ds = null;


        
        		d=factory.createFrom();
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:740:17: ( 'from' opt_eol ds= from_source )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:740:17: 'from' opt_eol ds= from_source
            {
            match(input,42,FOLLOW_42_in_from_statement1652); 
            following.push(FOLLOW_opt_eol_in_from_statement1654);
            opt_eol();
            following.pop();

            following.push(FOLLOW_from_source_in_from_statement1658);
            ds=from_source();
            following.pop();

            
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:750:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID ) | (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) | (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) );
    public DeclarativeInvokerDescr from_source() throws RecognitionException {   
        DeclarativeInvokerDescr ds;
        Token var=null;
        Token field=null;
        Token method=null;
        Token functionName=null;
        ArrayList args = null;


        
        		ds = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:755:17: ( (var= ID '.' field= ID ) | (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) | (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) )
            int alt49=3;
            alt49 = dfa49.predict(input); 
            switch (alt49) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:755:17: (var= ID '.' field= ID )
                    {
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:755:17: (var= ID '.' field= ID )
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:755:18: var= ID '.' field= ID
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1702); 
                    match(input,19,FOLLOW_19_in_from_source1704); 
                    field=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1708); 
                    
                    			  FieldAccessDescr fa = new FieldAccessDescr(var.getText(), field.getText());	
                    			  fa.setLine(var.getLine());
                    			  ds = fa;
                    			 

                    }


                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:765:17: (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    {
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:765:17: (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:765:18: var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')'
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1735); 
                    match(input,19,FOLLOW_19_in_from_source1737); 
                    method=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1741); 
                    following.push(FOLLOW_opt_eol_in_from_source1743);
                    opt_eol();
                    following.pop();

                    match(input,23,FOLLOW_23_in_from_source1746); 
                    following.push(FOLLOW_opt_eol_in_from_source1748);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_argument_list_in_from_source1752);
                    args=argument_list();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_from_source1754);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_from_source1756); 
                    
                    			MethodAccessDescr mc = new MethodAccessDescr(var.getText(), method.getText());
                    			mc.setArguments(args);
                    			mc.setLine(var.getLine());
                    			ds = mc;
                    			

                    }


                    }
                    break;
                case 3 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:774:17: (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    {
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:774:17: (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:774:18: functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')'
                    {
                    functionName=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1778); 
                    following.push(FOLLOW_opt_eol_in_from_source1780);
                    opt_eol();
                    following.pop();

                    match(input,23,FOLLOW_23_in_from_source1782); 
                    following.push(FOLLOW_opt_eol_in_from_source1784);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_argument_list_in_from_source1788);
                    args=argument_list();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_from_source1790);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_from_source1792); 
                    
                    			FunctionCallDescr fc = new FunctionCallDescr(functionName.getText());
                    			fc.setLine(functionName.getLine());
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:787:1: accumulate_statement returns [AccumulateDescr d] : loc= 'from' opt_eol 'accumulate' opt_eol '(' opt_eol column= lhs_column opt_eol ',' opt_eol 'init' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'action' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'result' opt_eol '(' c= paren_chunk2 ')' opt_eol ')' ;
    public AccumulateDescr accumulate_statement() throws RecognitionException {   
        AccumulateDescr d;
        Token loc=null;
        PatternDescr column = null;

        String c = null;


        
        		d = factory.createAccumulate();
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:792:17: (loc= 'from' opt_eol 'accumulate' opt_eol '(' opt_eol column= lhs_column opt_eol ',' opt_eol 'init' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'action' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'result' opt_eol '(' c= paren_chunk2 ')' opt_eol ')' )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:792:17: loc= 'from' opt_eol 'accumulate' opt_eol '(' opt_eol column= lhs_column opt_eol ',' opt_eol 'init' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'action' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'result' opt_eol '(' c= paren_chunk2 ')' opt_eol ')'
            {
            loc=(Token)input.LT(1);
            match(input,42,FOLLOW_42_in_accumulate_statement1841); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1843);
            opt_eol();
            following.pop();

            match(input,43,FOLLOW_43_in_accumulate_statement1845); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1847);
            opt_eol();
            following.pop();

             
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            match(input,23,FOLLOW_23_in_accumulate_statement1857); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1859);
            opt_eol();
            following.pop();

            following.push(FOLLOW_lhs_column_in_accumulate_statement1863);
            column=lhs_column();
            following.pop();

            following.push(FOLLOW_opt_eol_in_accumulate_statement1865);
            opt_eol();
            following.pop();

            match(input,24,FOLLOW_24_in_accumulate_statement1867); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1869);
            opt_eol();
            following.pop();

            
            		        d.setSourceColumn( (ColumnDescr)column );
            		
            match(input,44,FOLLOW_44_in_accumulate_statement1877); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1879);
            opt_eol();
            following.pop();

            match(input,23,FOLLOW_23_in_accumulate_statement1881); 
            following.push(FOLLOW_paren_chunk2_in_accumulate_statement1885);
            c=paren_chunk2();
            following.pop();

            match(input,25,FOLLOW_25_in_accumulate_statement1887); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1889);
            opt_eol();
            following.pop();

            match(input,24,FOLLOW_24_in_accumulate_statement1891); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1893);
            opt_eol();
            following.pop();

            
            		        d.setInitCode( c );
            		
            match(input,45,FOLLOW_45_in_accumulate_statement1901); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1903);
            opt_eol();
            following.pop();

            match(input,23,FOLLOW_23_in_accumulate_statement1905); 
            following.push(FOLLOW_paren_chunk2_in_accumulate_statement1909);
            c=paren_chunk2();
            following.pop();

            match(input,25,FOLLOW_25_in_accumulate_statement1911); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1913);
            opt_eol();
            following.pop();

            match(input,24,FOLLOW_24_in_accumulate_statement1915); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1917);
            opt_eol();
            following.pop();

            
            		        d.setActionCode( c );
            		
            match(input,46,FOLLOW_46_in_accumulate_statement1925); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1927);
            opt_eol();
            following.pop();

            match(input,23,FOLLOW_23_in_accumulate_statement1929); 
            following.push(FOLLOW_paren_chunk2_in_accumulate_statement1933);
            c=paren_chunk2();
            following.pop();

            match(input,25,FOLLOW_25_in_accumulate_statement1935); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1937);
            opt_eol();
            following.pop();

            match(input,25,FOLLOW_25_in_accumulate_statement1939); 
            
            		        d.setResultCode( c );
            		

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


    // $ANTLR start argument_list
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:814:1: argument_list returns [ArrayList args] : (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )? ;
    public ArrayList argument_list() throws RecognitionException {   
        ArrayList args;
        ArgumentValueDescr param = null;


        
        		args = new ArrayList();
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:819:17: ( (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )? )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:819:17: (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )?
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:819:17: (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )?
            int alt51=2;
            int LA51_0 = input.LA(1);
            if ( (LA51_0>=ID && LA51_0<=FLOAT)||LA51_0==26||LA51_0==47||LA51_0==49 ) {
                alt51=1;
            }
            else if ( LA51_0==EOL||LA51_0==15||LA51_0==25 ) {
                alt51=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("819:17: (param= argument_value ( opt_eol \',\' opt_eol param= argument_value )* )?", 51, 0, input);

                throw nvae;
            }
            switch (alt51) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:819:18: param= argument_value ( opt_eol ',' opt_eol param= argument_value )*
                    {
                    following.push(FOLLOW_argument_value_in_argument_list1976);
                    param=argument_value();
                    following.pop();

                    
                    			if (param != null) {
                    				args.add(param);
                    			}
                    		
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:825:17: ( opt_eol ',' opt_eol param= argument_value )*
                    loop50:
                    do {
                        int alt50=2;
                        alt50 = dfa50.predict(input); 
                        switch (alt50) {
                    	case 1 :
                    	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:826:25: opt_eol ',' opt_eol param= argument_value
                    	    {
                    	    following.push(FOLLOW_opt_eol_in_argument_list1992);
                    	    opt_eol();
                    	    following.pop();

                    	    match(input,24,FOLLOW_24_in_argument_list1994); 
                    	    following.push(FOLLOW_opt_eol_in_argument_list1996);
                    	    opt_eol();
                    	    following.pop();

                    	    following.push(FOLLOW_argument_value_in_argument_list2000);
                    	    param=argument_value();
                    	    following.pop();

                    	    
                    	    				if (param != null) {
                    	    					args.add(param);
                    	    				}
                    	    			

                    	    }
                    	    break;

                    	default :
                    	    break loop50;
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:835:1: argument_value returns [ArgumentValueDescr value] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map | a= inline_array ) ;
    public ArgumentValueDescr argument_value() throws RecognitionException {   
        ArgumentValueDescr value;
        Token t=null;
        ArgumentValueDescr.MapDescr m = null;

        List a = null;


        
        		value = null;
        		String text = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:840:17: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map | a= inline_array ) )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:840:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map | a= inline_array )
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:840:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map | a= inline_array )
            int alt52=9;
            switch ( input.LA(1) ) {
            case STRING:
                alt52=1;
                break;
            case INT:
                alt52=2;
                break;
            case FLOAT:
                alt52=3;
                break;
            case BOOL:
                alt52=4;
                break;
            case ID:
                alt52=5;
                break;
            case 47:
                alt52=6;
                break;
            case 26:
                alt52=8;
                break;
            case 49:
                alt52=9;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("840:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= \'null\' | t= \'null\' | m= inline_map | a= inline_array )", 52, 0, input);

                throw nvae;
            }

            switch (alt52) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:840:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_argument_value2040); 
                     text = getString( t );  value=new ArgumentValueDescr(ArgumentValueDescr.STRING, text);

                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:841:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_argument_value2051); 
                     text = t.getText();  value=new ArgumentValueDescr(ArgumentValueDescr.INTEGRAL, text);

                    }
                    break;
                case 3 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:842:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_argument_value2064); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.DECIMAL, text); 

                    }
                    break;
                case 4 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:843:25: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_argument_value2075); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.BOOLEAN, text); 

                    }
                    break;
                case 5 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:844:25: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_argument_value2087); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.VARIABLE, text);

                    }
                    break;
                case 6 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:845:25: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,47,FOLLOW_47_in_argument_value2098); 
                     text = "null"; value=new ArgumentValueDescr(ArgumentValueDescr.NULL, text);

                    }
                    break;
                case 7 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:846:25: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,47,FOLLOW_47_in_argument_value2109); 
                     text = "null"; value=new ArgumentValueDescr(ArgumentValueDescr.NULL, text);

                    }
                    break;
                case 8 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:847:25: m= inline_map
                    {
                    following.push(FOLLOW_inline_map_in_argument_value2128);
                    m=inline_map();
                    following.pop();

                      value=new ArgumentValueDescr(ArgumentValueDescr.MAP, m.getKeyValuePairs() ); 

                    }
                    break;
                case 9 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:848:25: a= inline_array
                    {
                    following.push(FOLLOW_inline_array_in_argument_value2144);
                    a=inline_array();
                    following.pop();

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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:852:1: inline_map returns [ArgumentValueDescr.MapDescr mapDescr] : '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}' ;
    public ArgumentValueDescr.MapDescr inline_map() throws RecognitionException {   
        ArgumentValueDescr.MapDescr mapDescr;
        ArgumentValueDescr key = null;

        ArgumentValueDescr value = null;


        
                mapDescr = new ArgumentValueDescr.MapDescr();
            
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:856:8: ( '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}' )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:856:8: '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}'
            {
            match(input,26,FOLLOW_26_in_inline_map2184); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:857:12: (key= argument_value '=>' value= argument_value )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:857:14: key= argument_value '=>' value= argument_value
            {
            following.push(FOLLOW_argument_value_in_inline_map2202);
            key=argument_value();
            following.pop();

            match(input,48,FOLLOW_48_in_inline_map2204); 
            following.push(FOLLOW_argument_value_in_inline_map2208);
            value=argument_value();
            following.pop();

            
                             if ( key != null ) {
                                 mapDescr.add( new ArgumentValueDescr.KeyValuePairDescr( key, value ) );
                             }
                         

            }

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:864:12: ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )*
            loop55:
            do {
                int alt55=2;
                int LA55_0 = input.LA(1);
                if ( LA55_0==EOL||LA55_0==24 ) {
                    alt55=1;
                }


                switch (alt55) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:864:14: ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value
            	    {
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:864:14: ( EOL )?
            	    int alt53=2;
            	    int LA53_0 = input.LA(1);
            	    if ( LA53_0==EOL ) {
            	        alt53=1;
            	    }
            	    else if ( LA53_0==24 ) {
            	        alt53=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("864:14: ( EOL )?", 53, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt53) {
            	        case 1 :
            	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:864:15: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_map2251); 

            	            }
            	            break;

            	    }

            	    match(input,24,FOLLOW_24_in_inline_map2255); 
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:864:25: ( EOL )?
            	    int alt54=2;
            	    int LA54_0 = input.LA(1);
            	    if ( LA54_0==EOL ) {
            	        alt54=1;
            	    }
            	    else if ( (LA54_0>=ID && LA54_0<=FLOAT)||LA54_0==26||LA54_0==47||LA54_0==49 ) {
            	        alt54=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("864:25: ( EOL )?", 54, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt54) {
            	        case 1 :
            	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:864:26: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_map2258); 

            	            }
            	            break;

            	    }

            	    following.push(FOLLOW_argument_value_in_inline_map2264);
            	    key=argument_value();
            	    following.pop();

            	    match(input,48,FOLLOW_48_in_inline_map2266); 
            	    following.push(FOLLOW_argument_value_in_inline_map2270);
            	    value=argument_value();
            	    following.pop();

            	    
            	                     if ( key != null ) {
            	                         mapDescr.add( new ArgumentValueDescr.KeyValuePairDescr( key, value ) );
            	                     }
            	                 

            	    }
            	    break;

            	default :
            	    break loop55;
                }
            } while (true);

            match(input,27,FOLLOW_27_in_inline_map2306); 

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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:873:1: inline_array returns [List list] : '[' arg= argument_value ( ( EOL )? ',' ( EOL )? arg= argument_value )* ']' ;
    public List inline_array() throws RecognitionException {   
        List list;
        ArgumentValueDescr arg = null;


        
            	list = new ArrayList();
            
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:878:5: ( '[' arg= argument_value ( ( EOL )? ',' ( EOL )? arg= argument_value )* ']' )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:878:5: '[' arg= argument_value ( ( EOL )? ',' ( EOL )? arg= argument_value )* ']'
            {
            match(input,49,FOLLOW_49_in_inline_array2350); 
            following.push(FOLLOW_argument_value_in_inline_array2354);
            arg=argument_value();
            following.pop();

             list.add(arg); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:880:10: ( ( EOL )? ',' ( EOL )? arg= argument_value )*
            loop58:
            do {
                int alt58=2;
                int LA58_0 = input.LA(1);
                if ( LA58_0==EOL||LA58_0==24 ) {
                    alt58=1;
                }


                switch (alt58) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:880:12: ( EOL )? ',' ( EOL )? arg= argument_value
            	    {
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:880:12: ( EOL )?
            	    int alt56=2;
            	    int LA56_0 = input.LA(1);
            	    if ( LA56_0==EOL ) {
            	        alt56=1;
            	    }
            	    else if ( LA56_0==24 ) {
            	        alt56=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("880:12: ( EOL )?", 56, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt56) {
            	        case 1 :
            	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:880:12: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_array2372); 

            	            }
            	            break;

            	    }

            	    match(input,24,FOLLOW_24_in_inline_array2375); 
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:880:21: ( EOL )?
            	    int alt57=2;
            	    int LA57_0 = input.LA(1);
            	    if ( LA57_0==EOL ) {
            	        alt57=1;
            	    }
            	    else if ( (LA57_0>=ID && LA57_0<=FLOAT)||LA57_0==26||LA57_0==47||LA57_0==49 ) {
            	        alt57=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("880:21: ( EOL )?", 57, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt57) {
            	        case 1 :
            	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:880:21: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_array2377); 

            	            }
            	            break;

            	    }

            	    following.push(FOLLOW_argument_value_in_inline_array2382);
            	    arg=argument_value();
            	    following.pop();

            	     list.add(arg); 

            	    }
            	    break;

            	default :
            	    break loop58;
                }
            } while (true);

            match(input,50,FOLLOW_50_in_inline_array2395); 

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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:885:1: fact_binding returns [PatternDescr d] : id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()] ;
    public PatternDescr fact_binding() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        PatternDescr fe = null;


        
        		d=null;
        		boolean multi=false;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:891:17: (id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()] )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:891:17: id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding2438); 
            following.push(FOLLOW_opt_eol_in_fact_binding2448);
            opt_eol();
            following.pop();

            match(input,33,FOLLOW_33_in_fact_binding2450); 
            following.push(FOLLOW_opt_eol_in_fact_binding2452);
            opt_eol();
            following.pop();

            following.push(FOLLOW_fact_expression_in_fact_binding2456);
            fe=fact_expression(id.getText());
            following.pop();

            
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:899:2: fact_expression[String id] returns [PatternDescr pd] : ( '(' opt_eol fe= fact_expression[id] opt_eol ')' | f= fact opt_eol ( ('or'|'||') opt_eol f= fact )* );
    public PatternDescr fact_expression(String id) throws RecognitionException {   
        PatternDescr pd;
        PatternDescr fe = null;

        PatternDescr f = null;


        
         		pd = null;
         		boolean multi = false;
         	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:904:17: ( '(' opt_eol fe= fact_expression[id] opt_eol ')' | f= fact opt_eol ( ('or'|'||') opt_eol f= fact )* )
            int alt60=2;
            int LA60_0 = input.LA(1);
            if ( LA60_0==23 ) {
                alt60=1;
            }
            else if ( LA60_0==ID ) {
                alt60=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("899:2: fact_expression[String id] returns [PatternDescr pd] : ( \'(\' opt_eol fe= fact_expression[id] opt_eol \')\' | f= fact opt_eol ( (\'or\'|\'||\') opt_eol f= fact )* );", 60, 0, input);

                throw nvae;
            }
            switch (alt60) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:904:17: '(' opt_eol fe= fact_expression[id] opt_eol ')'
                    {
                    match(input,23,FOLLOW_23_in_fact_expression2488); 
                    following.push(FOLLOW_opt_eol_in_fact_expression2490);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_fact_expression_in_fact_expression2494);
                    fe=fact_expression(id);
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_fact_expression2496);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_fact_expression2498); 
                     pd=fe; 

                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:905:17: f= fact opt_eol ( ('or'|'||') opt_eol f= fact )*
                    {
                    following.push(FOLLOW_fact_in_fact_expression2509);
                    f=fact();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_fact_expression2511);
                    opt_eol();
                    following.pop();

                    
                     			((ColumnDescr)f).setIdentifier( id );
                     			pd = f;
                     		
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:910:17: ( ('or'|'||') opt_eol f= fact )*
                    loop59:
                    do {
                        int alt59=2;
                        int LA59_0 = input.LA(1);
                        if ( (LA59_0>=51 && LA59_0<=52) ) {
                            alt59=1;
                        }


                        switch (alt59) {
                    	case 1 :
                    	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:910:25: ('or'|'||') opt_eol f= fact
                    	    {
                    	    if ( (input.LA(1)>=51 && input.LA(1)<=52) ) {
                    	        input.consume();
                    	        errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression2524);    throw mse;
                    	    }

                    	    following.push(FOLLOW_opt_eol_in_fact_expression2529);
                    	    opt_eol();
                    	    following.pop();

                    	    	if ( ! multi ) {
                    	     					PatternDescr first = pd;
                    	     					pd = new OrDescr();
                    	     					((OrDescr)pd).addDescr( first );
                    	     					multi=true;
                    	     				}
                    	     			
                    	    following.push(FOLLOW_fact_in_fact_expression2543);
                    	    f=fact();
                    	    following.pop();

                    	    
                    	     				((ColumnDescr)f).setIdentifier( id );
                    	     				((OrDescr)pd).addDescr( f );
                    	     			

                    	    }
                    	    break;

                    	default :
                    	    break loop59;
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:926:1: fact returns [PatternDescr d] : id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol endLoc= ')' opt_eol ;
    public PatternDescr fact() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        Token endLoc=null;
        String id = null;

        List c = null;


        
        		d=null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:930:17: (id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol endLoc= ')' opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:930:17: id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol endLoc= ')' opt_eol
            {
            following.push(FOLLOW_dotted_name_in_fact2582);
            id=dotted_name();
            following.pop();

             
             			d = new ColumnDescr( id ); 
             		
            following.push(FOLLOW_opt_eol_in_fact2590);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,23,FOLLOW_23_in_fact2598); 
            
             				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
             			
            following.push(FOLLOW_opt_eol_in_fact2601);
            opt_eol();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:936:34: (c= constraints )?
            int alt61=2;
            alt61 = dfa61.predict(input); 
            switch (alt61) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:936:41: c= constraints
                    {
                    following.push(FOLLOW_constraints_in_fact2607);
                    c=constraints();
                    following.pop();

                    
                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_fact2626);
            opt_eol();
            following.pop();

            endLoc=(Token)input.LT(1);
            match(input,25,FOLLOW_25_in_fact2630); 
            following.push(FOLLOW_opt_eol_in_fact2632);
            opt_eol();
            following.pop();

            
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:950:1: constraints returns [List constraints] : opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol ;
    public List constraints() throws RecognitionException {   
        List constraints;
        
        		constraints = new ArrayList();
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:954:17: ( opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:954:17: opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraints2664);
            opt_eol();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:955:17: ( constraint[constraints] | predicate[constraints] )
            int alt62=2;
            int LA62_0 = input.LA(1);
            if ( LA62_0==EOL||LA62_0==15 ) {
                alt62=1;
            }
            else if ( LA62_0==ID ) {
                int LA62_2 = input.LA(2);
                if ( LA62_2==33 ) {
                    int LA62_3 = input.LA(3);
                    if ( LA62_3==ID ) {
                        int LA62_17 = input.LA(4);
                        if ( LA62_17==55 ) {
                            alt62=2;
                        }
                        else if ( LA62_17==EOL||LA62_17==15||(LA62_17>=24 && LA62_17<=25)||(LA62_17>=62 && LA62_17<=71) ) {
                            alt62=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("955:17: ( constraint[constraints] | predicate[constraints] )", 62, 17, input);

                            throw nvae;
                        }
                    }
                    else if ( LA62_3==EOL||LA62_3==15 ) {
                        alt62=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("955:17: ( constraint[constraints] | predicate[constraints] )", 62, 3, input);

                        throw nvae;
                    }
                }
                else if ( LA62_2==EOL||LA62_2==15||(LA62_2>=24 && LA62_2<=25)||(LA62_2>=62 && LA62_2<=71) ) {
                    alt62=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("955:17: ( constraint[constraints] | predicate[constraints] )", 62, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("955:17: ( constraint[constraints] | predicate[constraints] )", 62, 0, input);

                throw nvae;
            }
            switch (alt62) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:955:18: constraint[constraints]
                    {
                    following.push(FOLLOW_constraint_in_constraints2669);
                    constraint(constraints);
                    following.pop();


                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:955:42: predicate[constraints]
                    {
                    following.push(FOLLOW_predicate_in_constraints2672);
                    predicate(constraints);
                    following.pop();


                    }
                    break;

            }

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:956:17: ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )*
            loop64:
            do {
                int alt64=2;
                alt64 = dfa64.predict(input); 
                switch (alt64) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:956:19: opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] )
            	    {
            	    following.push(FOLLOW_opt_eol_in_constraints2680);
            	    opt_eol();
            	    following.pop();

            	    match(input,24,FOLLOW_24_in_constraints2682); 
            	    following.push(FOLLOW_opt_eol_in_constraints2684);
            	    opt_eol();
            	    following.pop();

            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:956:39: ( constraint[constraints] | predicate[constraints] )
            	    int alt63=2;
            	    int LA63_0 = input.LA(1);
            	    if ( LA63_0==EOL||LA63_0==15 ) {
            	        alt63=1;
            	    }
            	    else if ( LA63_0==ID ) {
            	        int LA63_2 = input.LA(2);
            	        if ( LA63_2==33 ) {
            	            int LA63_3 = input.LA(3);
            	            if ( LA63_3==ID ) {
            	                int LA63_17 = input.LA(4);
            	                if ( LA63_17==55 ) {
            	                    alt63=2;
            	                }
            	                else if ( LA63_17==EOL||LA63_17==15||(LA63_17>=24 && LA63_17<=25)||(LA63_17>=62 && LA63_17<=71) ) {
            	                    alt63=1;
            	                }
            	                else {
            	                    NoViableAltException nvae =
            	                        new NoViableAltException("956:39: ( constraint[constraints] | predicate[constraints] )", 63, 17, input);

            	                    throw nvae;
            	                }
            	            }
            	            else if ( LA63_3==EOL||LA63_3==15 ) {
            	                alt63=1;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("956:39: ( constraint[constraints] | predicate[constraints] )", 63, 3, input);

            	                throw nvae;
            	            }
            	        }
            	        else if ( LA63_2==EOL||LA63_2==15||(LA63_2>=24 && LA63_2<=25)||(LA63_2>=62 && LA63_2<=71) ) {
            	            alt63=1;
            	        }
            	        else {
            	            NoViableAltException nvae =
            	                new NoViableAltException("956:39: ( constraint[constraints] | predicate[constraints] )", 63, 2, input);

            	            throw nvae;
            	        }
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("956:39: ( constraint[constraints] | predicate[constraints] )", 63, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt63) {
            	        case 1 :
            	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:956:40: constraint[constraints]
            	            {
            	            following.push(FOLLOW_constraint_in_constraints2687);
            	            constraint(constraints);
            	            following.pop();


            	            }
            	            break;
            	        case 2 :
            	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:956:64: predicate[constraints]
            	            {
            	            following.push(FOLLOW_predicate_in_constraints2690);
            	            predicate(constraints);
            	            following.pop();


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop64;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_constraints2698);
            opt_eol();
            following.pop();


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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:960:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )? opt_eol ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token bvc=null;
        Token con=null;
        String op = null;

        String lc = null;

        String rvc = null;


        
        		PatternDescr d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:964:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )? opt_eol )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:964:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )? opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraint2717);
            opt_eol();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:965:17: (fb= ID opt_eol ':' opt_eol )?
            int alt65=2;
            alt65 = dfa65.predict(input); 
            switch (alt65) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:965:19: fb= ID opt_eol ':' opt_eol
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint2725); 
                    following.push(FOLLOW_opt_eol_in_constraint2727);
                    opt_eol();
                    following.pop();

                    match(input,33,FOLLOW_33_in_constraint2729); 
                    following.push(FOLLOW_opt_eol_in_constraint2731);
                    opt_eol();
                    following.pop();


                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint2741); 
            
            
            			if ( fb != null ) {
            				//System.err.println( "fb: " + fb.getText() );
            				//System.err.println( " f: " + f.getText() );
            				d = new FieldBindingDescr( f.getText(), fb.getText() );
            				//System.err.println( "fbd: " + d );
            				
            				d.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            				constraints.add( d );
            			} 
            			FieldConstraintDescr fc = new FieldConstraintDescr(f.getText());
            			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            									
            			
            		
            following.push(FOLLOW_opt_eol_in_constraint2755);
            opt_eol();
            following.pop();

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:983:33: (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )?
            int alt69=2;
            int LA69_0 = input.LA(1);
            if ( (LA69_0>=62 && LA69_0<=71) ) {
                alt69=1;
            }
            else if ( LA69_0==EOL||LA69_0==15||(LA69_0>=24 && LA69_0<=25) ) {
                alt69=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("983:33: (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= (\'&\'|\'|\')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )?", 69, 0, input);

                throw nvae;
            }
            switch (alt69) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:983:41: op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )*
                    {
                    following.push(FOLLOW_operator_in_constraint2761);
                    op=operator();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_constraint2763);
                    opt_eol();
                    following.pop();

                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:985:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    int alt66=4;
                    switch ( input.LA(1) ) {
                    case ID:
                        int LA66_1 = input.LA(2);
                        if ( LA66_1==19 ) {
                            alt66=2;
                        }
                        else if ( LA66_1==EOL||LA66_1==15||(LA66_1>=24 && LA66_1<=25)||(LA66_1>=53 && LA66_1<=54) ) {
                            alt66=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("985:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 66, 1, input);

                            throw nvae;
                        }
                        break;
                    case INT:
                    case BOOL:
                    case STRING:
                    case FLOAT:
                    case 47:
                        alt66=3;
                        break;
                    case 23:
                        alt66=4;
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("985:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 66, 0, input);

                        throw nvae;
                    }

                    switch (alt66) {
                        case 1 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:985:49: bvc= ID
                            {
                            bvc=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_constraint2781); 
                            
                            							
                            														
                            							
                            							VariableRestrictionDescr vd = new VariableRestrictionDescr(op, bvc.getText());
                            							fc.addRestriction(vd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;
                        case 2 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:996:49: lc= enum_constraint
                            {
                            following.push(FOLLOW_enum_constraint_in_constraint2806);
                            lc=enum_constraint();
                            following.pop();

                             
                            
                            							LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc, true);
                            							fc.addRestriction(lrd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;
                        case 3 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1005:49: lc= literal_constraint
                            {
                            following.push(FOLLOW_literal_constraint_in_constraint2838);
                            lc=literal_constraint();
                            following.pop();

                             
                            							
                            							LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc);
                            							fc.addRestriction(lrd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;
                        case 4 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1013:49: rvc= retval_constraint
                            {
                            following.push(FOLLOW_retval_constraint_in_constraint2858);
                            rvc=retval_constraint();
                            following.pop();

                             
                            							
                            							
                            
                            							ReturnValueRestrictionDescr rvd = new ReturnValueRestrictionDescr(op, rvc);							
                            							fc.addRestriction(rvd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;

                    }

                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1024:41: (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )*
                    loop68:
                    do {
                        int alt68=2;
                        int LA68_0 = input.LA(1);
                        if ( (LA68_0>=53 && LA68_0<=54) ) {
                            alt68=1;
                        }


                        switch (alt68) {
                    	case 1 :
                    	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1025:49: con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    	    {
                    	    con=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=53 && input.LA(1)<=54) ) {
                    	        input.consume();
                    	        errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint2893);    throw mse;
                    	    }

                    	    
                    	    							if (con.getText().equals("&") ) {								
                    	    								fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	    							} else {
                    	    								fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	    							}							
                    	    						
                    	    following.push(FOLLOW_operator_in_constraint2915);
                    	    op=operator();
                    	    following.pop();

                    	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1035:49: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    	    int alt67=4;
                    	    switch ( input.LA(1) ) {
                    	    case ID:
                    	        int LA67_1 = input.LA(2);
                    	        if ( LA67_1==19 ) {
                    	            alt67=2;
                    	        }
                    	        else if ( LA67_1==EOL||LA67_1==15||(LA67_1>=24 && LA67_1<=25)||(LA67_1>=53 && LA67_1<=54) ) {
                    	            alt67=1;
                    	        }
                    	        else {
                    	            NoViableAltException nvae =
                    	                new NoViableAltException("1035:49: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 67, 1, input);

                    	            throw nvae;
                    	        }
                    	        break;
                    	    case INT:
                    	    case BOOL:
                    	    case STRING:
                    	    case FLOAT:
                    	    case 47:
                    	        alt67=3;
                    	        break;
                    	    case 23:
                    	        alt67=4;
                    	        break;
                    	    default:
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("1035:49: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 67, 0, input);

                    	        throw nvae;
                    	    }

                    	    switch (alt67) {
                    	        case 1 :
                    	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1035:57: bvc= ID
                    	            {
                    	            bvc=(Token)input.LT(1);
                    	            match(input,ID,FOLLOW_ID_in_constraint2927); 
                    	            
                    	            								VariableRestrictionDescr vd = new VariableRestrictionDescr(op, bvc.getText());
                    	            								fc.addRestriction(vd);
                    	            							

                    	            }
                    	            break;
                    	        case 2 :
                    	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1041:57: lc= enum_constraint
                    	            {
                    	            following.push(FOLLOW_enum_constraint_in_constraint2955);
                    	            lc=enum_constraint();
                    	            following.pop();

                    	             
                    	            								LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc, true);
                    	            								fc.addRestriction(lrd);
                    	            								
                    	            							

                    	            }
                    	            break;
                    	        case 3 :
                    	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1048:57: lc= literal_constraint
                    	            {
                    	            following.push(FOLLOW_literal_constraint_in_constraint2990);
                    	            lc=literal_constraint();
                    	            following.pop();

                    	             
                    	            								LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc);
                    	            								fc.addRestriction(lrd);
                    	            								
                    	            							

                    	            }
                    	            break;
                    	        case 4 :
                    	            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1054:57: rvc= retval_constraint
                    	            {
                    	            following.push(FOLLOW_retval_constraint_in_constraint3012);
                    	            rvc=retval_constraint();
                    	            following.pop();

                    	             
                    	            								ReturnValueRestrictionDescr rvd = new ReturnValueRestrictionDescr(op, rvc);							
                    	            								fc.addRestriction(rvd);
                    	            								
                    	            							

                    	            }
                    	            break;

                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop68;
                        }
                    } while (true);


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_constraint3068);
            opt_eol();
            following.pop();


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


    // $ANTLR start literal_constraint
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1067:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) ;
    public String literal_constraint() throws RecognitionException {   
        String text;
        Token t=null;

        
        		text = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1071:17: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1071:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1071:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            int alt70=5;
            switch ( input.LA(1) ) {
            case STRING:
                alt70=1;
                break;
            case INT:
                alt70=2;
                break;
            case FLOAT:
                alt70=3;
                break;
            case BOOL:
                alt70=4;
                break;
            case 47:
                alt70=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1071:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= \'null\' )", 70, 0, input);

                throw nvae;
            }

            switch (alt70) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1071:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint3095); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1072:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint3106); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1073:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint3119); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1074:25: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint3130); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1075:25: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,47,FOLLOW_47_in_literal_constraint3142); 
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1079:1: enum_constraint returns [String text] : (cls= ID '.' en= ID ) ;
    public String enum_constraint() throws RecognitionException {   
        String text;
        Token cls=null;
        Token en=null;

        
        		text = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1083:17: ( (cls= ID '.' en= ID ) )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1083:17: (cls= ID '.' en= ID )
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1083:17: (cls= ID '.' en= ID )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1083:18: cls= ID '.' en= ID
            {
            cls=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint3173); 
            match(input,19,FOLLOW_19_in_enum_constraint3175); 
            en=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint3179); 

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


    // $ANTLR start retval_constraint
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1086:1: retval_constraint returns [String text] : '(' c= paren_chunk ')' ;
    public String retval_constraint() throws RecognitionException {   
        String text;
        String c = null;


        
        		text = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1091:17: ( '(' c= paren_chunk ')' )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1091:17: '(' c= paren_chunk ')'
            {
            match(input,23,FOLLOW_23_in_retval_constraint3208); 
            following.push(FOLLOW_paren_chunk_in_retval_constraint3213);
            c=paren_chunk();
            following.pop();

            match(input,25,FOLLOW_25_in_retval_constraint3216); 
             text = c; 

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


    // $ANTLR start predicate
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1094:1: predicate[List constraints] : decl= ID ':' field= ID '->' '(' text= paren_chunk ')' ;
    public void predicate(List constraints) throws RecognitionException {   
        Token decl=null;
        Token field=null;
        String text = null;


        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1096:17: (decl= ID ':' field= ID '->' '(' text= paren_chunk ')' )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1096:17: decl= ID ':' field= ID '->' '(' text= paren_chunk ')'
            {
            decl=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate3234); 
            match(input,33,FOLLOW_33_in_predicate3236); 
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate3240); 
            match(input,55,FOLLOW_55_in_predicate3242); 
            match(input,23,FOLLOW_23_in_predicate3244); 
            following.push(FOLLOW_paren_chunk_in_predicate3248);
            text=paren_chunk();
            following.pop();

            match(input,25,FOLLOW_25_in_predicate3250); 
            
            			PredicateDescr d = new PredicateDescr(field.getText(), decl.getText(), text );
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1103:1: paren_chunk returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* ;
    public String paren_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;


        
        		text = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1109:18: ( ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1109:18: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1109:18: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            loop71:
            do {
                int alt71=3;
                switch ( input.LA(1) ) {
                case EOL:
                    alt71=3;
                    break;
                case 25:
                    alt71=3;
                    break;
                case 23:
                    alt71=1;
                    break;
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 24:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    alt71=2;
                    break;

                }

                switch (alt71) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1110:25: '(' c= paren_chunk ')'
            	    {
            	    match(input,23,FOLLOW_23_in_paren_chunk3296); 
            	    following.push(FOLLOW_paren_chunk_in_paren_chunk3300);
            	    c=paren_chunk();
            	    following.pop();

            	    match(input,25,FOLLOW_25_in_paren_chunk3302); 
            	    
            	    				if ( c == null ) {
            	    					c = "";
            	    				}
            	    				if ( text == null ) {
            	    					text = "( " + c + " )";
            	    				} else {
            	    					text = text + " ( " + c + " )";
            	    				}
            	    			

            	    }
            	    break;
            	case 2 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1121:19: any= .
            	    {
            	    any=(Token)input.LT(1);
            	    matchAny(input); 
            	    
            	    				if ( text == null ) {
            	    					text = any.getText();
            	    				} else {
            	    					text = text + " " + any.getText(); 
            	    				} 
            	    			

            	    }
            	    break;

            	default :
            	    break loop71;
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
        return text;
    }
    // $ANTLR end paren_chunk


    // $ANTLR start paren_chunk2
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1133:1: paren_chunk2 returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )* ;
    public String paren_chunk2() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;


        
        		text = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1139:18: ( ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )* )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1139:18: ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )*
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1139:18: ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )*
            loop72:
            do {
                int alt72=3;
                switch ( input.LA(1) ) {
                case 25:
                    alt72=3;
                    break;
                case 23:
                    alt72=1;
                    break;
                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 24:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    alt72=2;
                    break;

                }

                switch (alt72) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1140:25: '(' c= paren_chunk2 ')'
            	    {
            	    match(input,23,FOLLOW_23_in_paren_chunk23373); 
            	    following.push(FOLLOW_paren_chunk2_in_paren_chunk23377);
            	    c=paren_chunk2();
            	    following.pop();

            	    match(input,25,FOLLOW_25_in_paren_chunk23379); 
            	    
            	    				if ( c == null ) {
            	    					c = "";
            	    				}
            	    				if ( text == null ) {
            	    					text = "( " + c + " )";
            	    				} else {
            	    					text = text + " ( " + c + " )";
            	    				}
            	    			

            	    }
            	    break;
            	case 2 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1151:19: any= .
            	    {
            	    any=(Token)input.LT(1);
            	    matchAny(input); 
            	    
            	    				if ( text == null ) {
            	    					text = any.getText();
            	    				} else {
            	    					text = text + " " + any.getText(); 
            	    				} 
            	    			

            	    }
            	    break;

            	default :
            	    break loop72;
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
        return text;
    }
    // $ANTLR end paren_chunk2


    // $ANTLR start curly_chunk
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1162:1: curly_chunk returns [String text] : ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* ;
    public String curly_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;


        
        		text = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1168:17: ( ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1168:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1168:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            loop73:
            do {
                int alt73=3;
                switch ( input.LA(1) ) {
                case 27:
                    alt73=3;
                    break;
                case 26:
                    alt73=1;
                    break;
                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    alt73=2;
                    break;

                }

                switch (alt73) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1169:25: '{' c= curly_chunk '}'
            	    {
            	    match(input,26,FOLLOW_26_in_curly_chunk3448); 
            	    following.push(FOLLOW_curly_chunk_in_curly_chunk3452);
            	    c=curly_chunk();
            	    following.pop();

            	    match(input,27,FOLLOW_27_in_curly_chunk3454); 
            	    
            	    				//System.err.println( "chunk [" + c + "]" );
            	    				if ( c == null ) {
            	    					c = "";
            	    				}
            	    				if ( text == null ) {
            	    					text = "{ " + c + " }";
            	    				} else {
            	    					text = text + " { " + c + " }";
            	    				}
            	    			

            	    }
            	    break;
            	case 2 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1181:19: any= .
            	    {
            	    any=(Token)input.LT(1);
            	    matchAny(input); 
            	    
            	    				//System.err.println( "any [" + any.getText() + "]" );
            	    				if ( text == null ) {
            	    					text = any.getText();
            	    				} else {
            	    					text = text + " " + any.getText(); 
            	    				} 
            	    			

            	    }
            	    break;

            	default :
            	    break loop73;
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
        return text;
    }
    // $ANTLR end curly_chunk


    // $ANTLR start lhs_or
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1193:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;


        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1198:17: (left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )* )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1198:17: left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )*
            {
             OrDescr or = null; 
            following.push(FOLLOW_lhs_and_in_lhs_or3512);
            left=lhs_and();
            following.pop();

            d = left; 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1200:17: ( ('or'|'||') opt_eol right= lhs_and )*
            loop74:
            do {
                int alt74=2;
                int LA74_0 = input.LA(1);
                if ( (LA74_0>=51 && LA74_0<=52) ) {
                    alt74=1;
                }


                switch (alt74) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1200:19: ('or'|'||') opt_eol right= lhs_and
            	    {
            	    if ( (input.LA(1)>=51 && input.LA(1)<=52) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or3521);    throw mse;
            	    }

            	    following.push(FOLLOW_opt_eol_in_lhs_or3526);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_lhs_and_in_lhs_or3533);
            	    right=lhs_and();
            	    following.pop();

            	    
            	    				if ( or == null ) {
            	    					or = new OrDescr();
            	    					or.addDescr( left );
            	    					d = or;
            	    				}
            	    				
            	    				or.addDescr( right );
            	    			

            	    }
            	    break;

            	default :
            	    break loop74;
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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1214:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;


        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1219:17: (left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )* )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1219:17: left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )*
            {
             AndDescr and = null; 
            following.push(FOLLOW_lhs_unary_in_lhs_and3573);
            left=lhs_unary();
            following.pop();

             d = left; 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1221:17: ( ('and'|'&&') opt_eol right= lhs_unary )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);
                if ( (LA75_0>=56 && LA75_0<=57) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1221:19: ('and'|'&&') opt_eol right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=56 && input.LA(1)<=57) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and3582);    throw mse;
            	    }

            	    following.push(FOLLOW_opt_eol_in_lhs_and3587);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_lhs_unary_in_lhs_and3594);
            	    right=lhs_unary();
            	    following.pop();

            	    
            	    				if ( and == null ) {
            	    					and = new AndDescr();
            	    					and.addDescr( left );
            	    					d = and;
            	    				}
            	    				
            	    				and.addDescr( right );
            	    			

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
        }
        return d;
    }
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1235:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) )? | '(' opt_eol u= lhs opt_eol ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   
        PatternDescr d;
        PatternDescr u = null;

        FromDescr fm = null;

        AccumulateDescr ac = null;


        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1239:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) )? | '(' opt_eol u= lhs opt_eol ')' ) )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1239:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) )? | '(' opt_eol u= lhs opt_eol ')' )
            {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1239:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) )? | '(' opt_eol u= lhs opt_eol ')' )
            int alt77=5;
            switch ( input.LA(1) ) {
            case 58:
                alt77=1;
                break;
            case 59:
                alt77=2;
                break;
            case 60:
                alt77=3;
                break;
            case ID:
                alt77=4;
                break;
            case 23:
                alt77=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1239:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) )? | \'(\' opt_eol u= lhs opt_eol \')\' )", 77, 0, input);

                throw nvae;
            }

            switch (alt77) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1239:25: u= lhs_exist
                    {
                    following.push(FOLLOW_lhs_exist_in_lhs_unary3632);
                    u=lhs_exist();
                    following.pop();

                    d = u;

                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1240:25: u= lhs_not
                    {
                    following.push(FOLLOW_lhs_not_in_lhs_unary3642);
                    u=lhs_not();
                    following.pop();

                    d = u;

                    }
                    break;
                case 3 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1241:25: u= lhs_eval
                    {
                    following.push(FOLLOW_lhs_eval_in_lhs_unary3652);
                    u=lhs_eval();
                    following.pop();

                    d = u;

                    }
                    break;
                case 4 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1242:25: u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) )?
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_unary3666);
                    u=lhs_column();
                    following.pop();

                    d=u;
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1243:27: ( (fm= from_statement ) | (ac= accumulate_statement ) )?
                    int alt76=3;
                    alt76 = dfa76.predict(input); 
                    switch (alt76) {
                        case 1 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1243:28: (fm= from_statement )
                            {
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1243:28: (fm= from_statement )
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1243:29: fm= from_statement
                            {
                            following.push(FOLLOW_from_statement_in_lhs_unary3687);
                            fm=from_statement();
                            following.pop();

                            fm.setColumn((ColumnDescr) u); d=fm;

                            }


                            }
                            break;
                        case 2 :
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1244:28: (ac= accumulate_statement )
                            {
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1244:28: (ac= accumulate_statement )
                            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1244:29: ac= accumulate_statement
                            {
                            following.push(FOLLOW_accumulate_statement_in_lhs_unary3709);
                            ac=accumulate_statement();
                            following.pop();

                            ac.setResultColumn((ColumnDescr) u); d=ac;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1245:25: '(' opt_eol u= lhs opt_eol ')'
                    {
                    match(input,23,FOLLOW_23_in_lhs_unary3721); 
                    following.push(FOLLOW_opt_eol_in_lhs_unary3723);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_lhs_in_lhs_unary3727);
                    u=lhs();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_lhs_unary3729);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_lhs_unary3731); 
                    d = u;

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
    // $ANTLR end lhs_unary


    // $ANTLR start lhs_exist
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1249:1: lhs_exist returns [PatternDescr d] : loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public PatternDescr lhs_exist() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        PatternDescr column = null;


        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1253:17: (loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1253:17: loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,58,FOLLOW_58_in_lhs_exist3762); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1253:30: ( '(' column= lhs_column ')' | column= lhs_column )
            int alt78=2;
            int LA78_0 = input.LA(1);
            if ( LA78_0==23 ) {
                alt78=1;
            }
            else if ( LA78_0==ID ) {
                alt78=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1253:30: ( \'(\' column= lhs_column \')\' | column= lhs_column )", 78, 0, input);

                throw nvae;
            }
            switch (alt78) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1253:31: '(' column= lhs_column ')'
                    {
                    match(input,23,FOLLOW_23_in_lhs_exist3765); 
                    following.push(FOLLOW_lhs_column_in_lhs_exist3769);
                    column=lhs_column();
                    following.pop();

                    match(input,25,FOLLOW_25_in_lhs_exist3771); 

                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1253:59: column= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_exist3777);
                    column=lhs_column();
                    following.pop();


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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1260:1: lhs_not returns [NotDescr d] : loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d;
        Token loc=null;
        PatternDescr column = null;


        
        		d = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1264:17: (loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1264:17: loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,59,FOLLOW_59_in_lhs_not3807); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1264:27: ( '(' column= lhs_column ')' | column= lhs_column )
            int alt79=2;
            int LA79_0 = input.LA(1);
            if ( LA79_0==23 ) {
                alt79=1;
            }
            else if ( LA79_0==ID ) {
                alt79=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1264:27: ( \'(\' column= lhs_column \')\' | column= lhs_column )", 79, 0, input);

                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1264:28: '(' column= lhs_column ')'
                    {
                    match(input,23,FOLLOW_23_in_lhs_not3810); 
                    following.push(FOLLOW_lhs_column_in_lhs_not3814);
                    column=lhs_column();
                    following.pop();

                    match(input,25,FOLLOW_25_in_lhs_not3817); 

                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1264:57: column= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_not3823);
                    column=lhs_column();
                    following.pop();


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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1271:1: lhs_eval returns [PatternDescr d] : 'eval' loc= '(' c= paren_chunk2 ')' ;
    public PatternDescr lhs_eval() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        String c = null;


        
        		d = null;
        		String text = "";
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1276:17: ( 'eval' loc= '(' c= paren_chunk2 ')' )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1276:17: 'eval' loc= '(' c= paren_chunk2 ')'
            {
            match(input,60,FOLLOW_60_in_lhs_eval3849); 
            loc=(Token)input.LT(1);
            match(input,23,FOLLOW_23_in_lhs_eval3853); 
            following.push(FOLLOW_paren_chunk2_in_lhs_eval3861);
            c=paren_chunk2();
            following.pop();

            match(input,25,FOLLOW_25_in_lhs_eval3865); 
             
            			checkTrailingSemicolon( c, offset(loc.getLine()) );
            			d = new EvalDescr( c ); 
            		

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
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1285:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ( '[' ']' )* ;
    public String dotted_name() throws RecognitionException {   
        String name;
        Token id=null;

        
        		name = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1290:17: (id= ID ( '.' id= ID )* ( '[' ']' )* )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1290:17: id= ID ( '.' id= ID )* ( '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name3897); 
             name=id.getText(); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1290:46: ( '.' id= ID )*
            loop80:
            do {
                int alt80=2;
                int LA80_0 = input.LA(1);
                if ( LA80_0==19 ) {
                    alt80=1;
                }


                switch (alt80) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1290:48: '.' id= ID
            	    {
            	    match(input,19,FOLLOW_19_in_dotted_name3903); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name3907); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop80;
                }
            } while (true);

            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1290:99: ( '[' ']' )*
            loop81:
            do {
                int alt81=2;
                int LA81_0 = input.LA(1);
                if ( LA81_0==49 ) {
                    alt81=1;
                }


                switch (alt81) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1290:101: '[' ']'
            	    {
            	    match(input,49,FOLLOW_49_in_dotted_name3916); 
            	    match(input,50,FOLLOW_50_in_dotted_name3918); 
            	     name = name + "[]";

            	    }
            	    break;

            	default :
            	    break loop81;
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


    // $ANTLR start argument_name
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1293:1: argument_name returns [String name] : id= ID ( '[' ']' )* ;
    public String argument_name() throws RecognitionException {   
        String name;
        Token id=null;

        
        		name = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1298:17: (id= ID ( '[' ']' )* )
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1298:17: id= ID ( '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument_name3948); 
             name=id.getText(); 
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1298:46: ( '[' ']' )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);
                if ( LA82_0==49 ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1298:48: '[' ']'
            	    {
            	    match(input,49,FOLLOW_49_in_argument_name3954); 
            	    match(input,50,FOLLOW_50_in_argument_name3956); 
            	     name = name + "[]";

            	    }
            	    break;

            	default :
            	    break loop82;
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
    // $ANTLR end argument_name


    // $ANTLR start word
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1302:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word;
        Token id=null;
        Token str=null;

        
        		word = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1306:17: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt83=11;
            switch ( input.LA(1) ) {
            case ID:
                alt83=1;
                break;
            case 17:
                alt83=2;
                break;
            case 61:
                alt83=3;
                break;
            case 31:
                alt83=4;
                break;
            case 28:
                alt83=5;
                break;
            case 36:
                alt83=6;
                break;
            case 37:
                alt83=7;
                break;
            case 32:
                alt83=8;
                break;
            case 34:
                alt83=9;
                break;
            case 29:
                alt83=10;
                break;
            case STRING:
                alt83=11;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1302:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'query\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 83, 0, input);

                throw nvae;
            }

            switch (alt83) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1306:17: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word3984); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1307:17: 'import'
                    {
                    match(input,17,FOLLOW_17_in_word3996); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1308:17: 'use'
                    {
                    match(input,61,FOLLOW_61_in_word4005); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1309:17: 'rule'
                    {
                    match(input,31,FOLLOW_31_in_word4017); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1310:17: 'query'
                    {
                    match(input,28,FOLLOW_28_in_word4028); 
                     word="query"; 

                    }
                    break;
                case 6 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1311:17: 'salience'
                    {
                    match(input,36,FOLLOW_36_in_word4038); 
                     word="salience"; 

                    }
                    break;
                case 7 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1312:17: 'no-loop'
                    {
                    match(input,37,FOLLOW_37_in_word4046); 
                     word="no-loop"; 

                    }
                    break;
                case 8 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1313:17: 'when'
                    {
                    match(input,32,FOLLOW_32_in_word4054); 
                     word="when"; 

                    }
                    break;
                case 9 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1314:17: 'then'
                    {
                    match(input,34,FOLLOW_34_in_word4065); 
                     word="then"; 

                    }
                    break;
                case 10 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1315:17: 'end'
                    {
                    match(input,29,FOLLOW_29_in_word4076); 
                     word="end"; 

                    }
                    break;
                case 11 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1316:17: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word4090); 
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


    // $ANTLR start operator
    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1319:1: operator returns [String str] : ( '==' | '=' | '>' | '>=' | '<' | '<=' | '!=' | 'contains' | 'matches' | 'excludes' );
    public String operator() throws RecognitionException {   
        String str;
        
        		str = null;
        	
        try {
            // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1325:17: ( '==' | '=' | '>' | '>=' | '<' | '<=' | '!=' | 'contains' | 'matches' | 'excludes' )
            int alt84=10;
            switch ( input.LA(1) ) {
            case 62:
                alt84=1;
                break;
            case 63:
                alt84=2;
                break;
            case 64:
                alt84=3;
                break;
            case 65:
                alt84=4;
                break;
            case 66:
                alt84=5;
                break;
            case 67:
                alt84=6;
                break;
            case 68:
                alt84=7;
                break;
            case 69:
                alt84=8;
                break;
            case 70:
                alt84=9;
                break;
            case 71:
                alt84=10;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1319:1: operator returns [String str] : ( \'==\' | \'=\' | \'>\' | \'>=\' | \'<\' | \'<=\' | \'!=\' | \'contains\' | \'matches\' | \'excludes\' );", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1325:17: '=='
                    {
                    match(input,62,FOLLOW_62_in_operator4119); 
                    str= "==";

                    }
                    break;
                case 2 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1326:18: '='
                    {
                    match(input,63,FOLLOW_63_in_operator4126); 
                    str="==";

                    }
                    break;
                case 3 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1327:18: '>'
                    {
                    match(input,64,FOLLOW_64_in_operator4133); 
                    str=">";

                    }
                    break;
                case 4 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1328:18: '>='
                    {
                    match(input,65,FOLLOW_65_in_operator4140); 
                    str=">=";

                    }
                    break;
                case 5 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1329:18: '<'
                    {
                    match(input,66,FOLLOW_66_in_operator4149); 
                    str="<";

                    }
                    break;
                case 6 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1330:18: '<='
                    {
                    match(input,67,FOLLOW_67_in_operator4156); 
                    str="<=";

                    }
                    break;
                case 7 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1331:18: '!='
                    {
                    match(input,68,FOLLOW_68_in_operator4163); 
                    str="!=";

                    }
                    break;
                case 8 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1332:18: 'contains'
                    {
                    match(input,69,FOLLOW_69_in_operator4170); 
                    str="contains";

                    }
                    break;
                case 9 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1333:18: 'matches'
                    {
                    match(input,70,FOLLOW_70_in_operator4177); 
                    str="matches";

                    }
                    break;
                case 10 :
                    // D:\workspace\jboss\jbossrules\drools-compiler\src\main\resources\org\drools\lang\drl.g:1334:18: 'excludes'
                    {
                    match(input,71,FOLLOW_71_in_operator4184); 
                    str="excludes";

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
        return str;
    }
    // $ANTLR end operator


    protected DFA2 dfa2 = new DFA2();protected DFA4 dfa4 = new DFA4();protected DFA13 dfa13 = new DFA13();protected DFA14 dfa14 = new DFA14();protected DFA15 dfa15 = new DFA15();protected DFA48 dfa48 = new DFA48();protected DFA49 dfa49 = new DFA49();protected DFA50 dfa50 = new DFA50();protected DFA61 dfa61 = new DFA61();protected DFA64 dfa64 = new DFA64();protected DFA65 dfa65 = new DFA65();protected DFA76 dfa76 = new DFA76();
    class DFA2 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=5;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s5 = new DFA.State() {{alt=3;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 31:
                    return s3;

                case EOL:
                case 15:
                    return s2;

                case 28:
                    return s4;

                case 30:
                    return s5;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s6 = new DFA.State() {{alt=4;}};
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case -1:
                    return s1;

                case EOL:
                case 15:
                    return s2;

                case 31:
                    return s3;

                case 28:
                    return s4;

                case 30:
                    return s5;

                case 17:
                case 18:
                case 22:
                    return s6;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA4 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=3;}};
        DFA.State s17 = new DFA.State() {{alt=1;}};
        DFA.State s16 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_16 = input.LA(1);
                if ( LA4_16==ID ) {return s17;}
                if ( LA4_16==EOL||LA4_16==15 ) {return s16;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 16, input);

                throw nvae;
            }
        };
        DFA.State s10 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_10 = input.LA(1);
                if ( LA4_10==EOL||LA4_10==15 ) {return s16;}
                if ( LA4_10==ID ) {return s17;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 10, input);

                throw nvae;
            }
        };
        DFA.State s12 = new DFA.State() {{alt=1;}};
        DFA.State s11 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_11 = input.LA(1);
                if ( LA4_11==ID ) {return s12;}
                if ( LA4_11==EOL||LA4_11==15 ) {return s11;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 11, input);

                throw nvae;
            }
        };
        DFA.State s5 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 18:
                    return s10;

                case EOL:
                case 15:
                    return s11;

                case ID:
                    return s12;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 5, input);

                    throw nvae;        }
            }
        };
        DFA.State s22 = new DFA.State() {{alt=1;}};
        DFA.State s33 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_33 = input.LA(1);
                if ( LA4_33==ID ) {return s22;}
                if ( LA4_33==49 ) {return s21;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 33, input);

                throw nvae;
            }
        };
        DFA.State s21 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_21 = input.LA(1);
                if ( LA4_21==50 ) {return s33;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 21, input);

                throw nvae;
            }
        };
        DFA.State s32 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 49:
                    return s21;

                case ID:
                    return s22;

                case 19:
                    return s20;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 32, input);

                    throw nvae;        }
            }
        };
        DFA.State s20 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_20 = input.LA(1);
                if ( LA4_20==ID ) {return s32;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 20, input);

                throw nvae;
            }
        };
        DFA.State s13 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 19:
                    return s20;

                case 49:
                    return s21;

                case ID:
                    return s22;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 13, input);

                    throw nvae;        }
            }
        };
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_6 = input.LA(1);
                if ( LA4_6==ID ) {return s13;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 6, input);

                throw nvae;
            }
        };
        DFA.State s120 = new DFA.State() {{alt=1;}};
        DFA.State s127 = new DFA.State() {{alt=1;}};
        DFA.State s128 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s127;

                case 26:
                    return s126;

                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s128;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 128, input);

                    throw nvae;        }
            }
        };
        DFA.State s126 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s127;

                case 26:
                    return s126;

                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s128;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 126, input);

                    throw nvae;        }
            }
        };
        DFA.State s121 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 26:
                    return s126;

                case 27:
                    return s127;

                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s128;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 121, input);

                    throw nvae;        }
            }
        };
        DFA.State s122 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s120;

                case 26:
                    return s121;

                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s122;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 122, input);

                    throw nvae;        }
            }
        };
        DFA.State s111 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s120;

                case 26:
                    return s121;

                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s122;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 111, input);

                    throw nvae;        }
            }
        };
        DFA.State s112 = new DFA.State() {{alt=1;}};
        DFA.State s113 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s112;

                case 26:
                    return s111;

                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s113;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 113, input);

                    throw nvae;        }
            }
        };
        DFA.State s93 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 26:
                    return s111;

                case 27:
                    return s112;

                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s113;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 93, input);

                    throw nvae;        }
            }
        };
        DFA.State s94 = new DFA.State() {{alt=1;}};
        DFA.State s95 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s94;

                case 26:
                    return s93;

                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s95;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 95, input);

                    throw nvae;        }
            }
        };
        DFA.State s73 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 26:
                    return s93;

                case 27:
                    return s94;

                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s95;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 73, input);

                    throw nvae;        }
            }
        };
        DFA.State s74 = new DFA.State() {{alt=1;}};
        DFA.State s75 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s74;

                case 26:
                    return s73;

                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s75;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 75, input);

                    throw nvae;        }
            }
        };
        DFA.State s59 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 26:
                    return s73;

                case 27:
                    return s74;

                case EOL:
                case ID:
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case MISC:
                case WS:
                case SH_STYLE_SINGLE_LINE_COMMENT:
                case C_STYLE_SINGLE_LINE_COMMENT:
                case MULTI_LINE_COMMENT:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s75;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 59, input);

                    throw nvae;        }
            }
        };
        DFA.State s58 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_58 = input.LA(1);
                if ( LA4_58==26 ) {return s59;}
                if ( LA4_58==EOL||LA4_58==15 ) {return s58;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 58, input);

                throw nvae;
            }
        };
        DFA.State s41 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_41 = input.LA(1);
                if ( LA4_41==EOL||LA4_41==15 ) {return s58;}
                if ( LA4_41==26 ) {return s59;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 41, input);

                throw nvae;
            }
        };
        DFA.State s66 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 24:
                    return s56;

                case 25:
                    return s41;

                case EOL:
                case 15:
                    return s66;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 66, input);

                    throw nvae;        }
            }
        };
        DFA.State s81 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s66;

                case 24:
                    return s56;

                case 25:
                    return s41;

                case 49:
                    return s65;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 81, input);

                    throw nvae;        }
            }
        };
        DFA.State s65 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_65 = input.LA(1);
                if ( LA4_65==50 ) {return s81;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 65, input);

                throw nvae;
            }
        };
        DFA.State s108 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s41;

                case 24:
                    return s56;

                case EOL:
                case 15:
                    return s108;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 108, input);

                    throw nvae;        }
            }
        };
        DFA.State s92 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 49:
                    return s65;

                case EOL:
                case 15:
                    return s108;

                case 25:
                    return s41;

                case 24:
                    return s56;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 92, input);

                    throw nvae;        }
            }
        };
        DFA.State s88 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s41;

                case 24:
                    return s56;

                case EOL:
                case 15:
                    return s88;

                case ID:
                    return s92;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 88, input);

                    throw nvae;        }
            }
        };
        DFA.State s70 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 49:
                    return s53;

                case EOL:
                case 15:
                    return s88;

                case 25:
                    return s41;

                case 24:
                    return s56;

                case 19:
                    return s20;

                case ID:
                    return s92;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 70, input);

                    throw nvae;        }
            }
        };
        DFA.State s69 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_69 = input.LA(1);
                if ( LA4_69==ID ) {return s70;}
                if ( LA4_69==EOL||LA4_69==15 ) {return s69;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 69, input);

                throw nvae;
            }
        };
        DFA.State s56 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_56 = input.LA(1);
                if ( LA4_56==EOL||LA4_56==15 ) {return s69;}
                if ( LA4_56==ID ) {return s70;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 56, input);

                throw nvae;
            }
        };
        DFA.State s55 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 49:
                    return s65;

                case EOL:
                case 15:
                    return s66;

                case 24:
                    return s56;

                case 25:
                    return s41;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 55, input);

                    throw nvae;        }
            }
        };
        DFA.State s54 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 24:
                    return s56;

                case 25:
                    return s41;

                case EOL:
                case 15:
                    return s54;

                case ID:
                    return s55;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 54, input);

                    throw nvae;        }
            }
        };
        DFA.State s60 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s54;

                case ID:
                    return s55;

                case 49:
                    return s53;

                case 24:
                    return s56;

                case 25:
                    return s41;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 60, input);

                    throw nvae;        }
            }
        };
        DFA.State s53 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_53 = input.LA(1);
                if ( LA4_53==50 ) {return s60;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 53, input);

                throw nvae;
            }
        };
        DFA.State s40 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 19:
                    return s20;

                case 49:
                    return s53;

                case EOL:
                case 15:
                    return s54;

                case ID:
                    return s55;

                case 24:
                    return s56;

                case 25:
                    return s41;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 40, input);

                    throw nvae;        }
            }
        };
        DFA.State s39 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s40;

                case EOL:
                case 15:
                    return s39;

                case 25:
                    return s41;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 39, input);

                    throw nvae;        }
            }
        };
        DFA.State s29 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s39;

                case ID:
                    return s40;

                case 25:
                    return s41;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 29, input);

                    throw nvae;        }
            }
        };
        DFA.State s37 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_37 = input.LA(1);
                if ( LA4_37==23 ) {return s29;}
                if ( LA4_37==EOL||LA4_37==15 ) {return s37;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 37, input);

                throw nvae;
            }
        };
        DFA.State s28 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_28 = input.LA(1);
                if ( LA4_28==EOL||LA4_28==15 ) {return s37;}
                if ( LA4_28==23 ) {return s29;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 28, input);

                throw nvae;
            }
        };
        DFA.State s27 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s28;

                case EOL:
                case 15:
                    return s27;

                case 23:
                    return s29;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 27, input);

                    throw nvae;        }
            }
        };
        DFA.State s15 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 19:
                    return s20;

                case 49:
                    return s21;

                case EOL:
                case 15:
                    return s27;

                case ID:
                    return s28;

                case 23:
                    return s29;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 15, input);

                    throw nvae;        }
            }
        };
        DFA.State s14 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_14 = input.LA(1);
                if ( LA4_14==ID ) {return s15;}
                if ( LA4_14==EOL||LA4_14==15 ) {return s14;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 14, input);

                throw nvae;
            }
        };
        DFA.State s7 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_7 = input.LA(1);
                if ( LA4_7==EOL||LA4_7==15 ) {return s14;}
                if ( LA4_7==ID ) {return s15;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 7, input);

                throw nvae;
            }
        };
        DFA.State s9 = new DFA.State() {{alt=2;}};
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case -1:
                case EOL:
                case 15:
                case 28:
                case 30:
                case 31:
                    return s1;

                case 17:
                    return s5;

                case 22:
                    return s6;

                case 18:
                    return s7;

                case 21:
                    return s9;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA13 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s5 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s3;

                case EOL:
                case 15:
                    return s5;

                case 23:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 5, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                case 19:
                case 49:
                    return s3;

                case EOL:
                case 15:
                    return s5;

                case 23:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA13_0 = input.LA(1);
                if ( LA13_0==ID ) {return s1;}
                if ( LA13_0==EOL||LA13_0==15 ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 13, 0, input);

                throw nvae;
            }
        };

    }class DFA14 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s7 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 24:
                case 25:
                    return s2;

                case EOL:
                case 15:
                    return s4;

                case ID:
                    return s7;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 4, input);

                    throw nvae;        }
            }
        };
        DFA.State s9 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s4;

                case 24:
                case 25:
                    return s2;

                case 49:
                    return s3;

                case ID:
                    return s7;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 9, input);

                    throw nvae;        }
            }
        };
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA14_3 = input.LA(1);
                if ( LA14_3==50 ) {return s9;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 14, 3, input);

                throw nvae;
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 49:
                    return s3;

                case EOL:
                case 15:
                    return s4;

                case 24:
                case 25:
                    return s2;

                case ID:
                case 19:
                    return s7;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA14_0 = input.LA(1);
                if ( LA14_0==ID ) {return s1;}
                if ( LA14_0==EOL||LA14_0==15 ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 14, 0, input);

                throw nvae;
            }
        };

    }class DFA15 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s5 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 24:
                case 25:
                    return s2;

                case EOL:
                case 15:
                    return s5;

                case ID:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 5, input);

                    throw nvae;        }
            }
        };
        DFA.State s9 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s5;

                case 24:
                case 25:
                    return s2;

                case 49:
                    return s4;

                case ID:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 9, input);

                    throw nvae;        }
            }
        };
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA15_4 = input.LA(1);
                if ( LA15_4==50 ) {return s9;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 15, 4, input);

                throw nvae;
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                case 19:
                    return s3;

                case 49:
                    return s4;

                case EOL:
                case 15:
                    return s5;

                case 24:
                case 25:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA15_0 = input.LA(1);
                if ( LA15_0==ID ) {return s1;}
                if ( LA15_0==EOL||LA15_0==15 ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
        };

    }class DFA48 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 33:
                    return s3;

                case EOL:
                case 15:
                    return s2;

                case 23:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s2;

                case 33:
                    return s3;

                case 19:
                case 23:
                case 49:
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA48_0 = input.LA(1);
                if ( LA48_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 48, 0, input);

                throw nvae;
            }
        };

    }class DFA49 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s8 = new DFA.State() {{alt=1;}};
        DFA.State s32 = new DFA.State() {{alt=2;}};
        DFA.State s56 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                case 33:
                    return s8;

                case EOL:
                case 15:
                    return s56;

                case 24:
                case 25:
                    return s32;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 49, 56, input);

                    throw nvae;        }
            }
        };
        DFA.State s30 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 19:
                case 23:
                case 33:
                case 49:
                    return s8;

                case EOL:
                case 15:
                    return s56;

                case 24:
                case 25:
                    return s32;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 49, 30, input);

                    throw nvae;        }
            }
        };
        DFA.State s26 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                case 58:
                case 59:
                case 60:
                    return s8;

                case ID:
                    return s30;

                case EOL:
                case 15:
                    return s26;

                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case 25:
                case 26:
                case 47:
                case 49:
                    return s32;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 49, 26, input);

                    throw nvae;        }
            }
        };
        DFA.State s7 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s26;

                case 23:
                case 58:
                case 59:
                case 60:
                    return s8;

                case ID:
                    return s30;

                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case 25:
                case 26:
                case 47:
                case 49:
                    return s32;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 49, 7, input);

                    throw nvae;        }
            }
        };
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                case 25:
                case 29:
                case 34:
                case 58:
                case 59:
                case 60:
                    return s8;

                case EOL:
                case 15:
                    return s6;

                case 23:
                    return s7;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 49, 6, input);

                    throw nvae;        }
            }
        };
        DFA.State s5 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s6;

                case 23:
                    return s7;

                case ID:
                case 25:
                case 29:
                case 34:
                case 51:
                case 52:
                case 56:
                case 57:
                case 58:
                case 59:
                case 60:
                    return s8;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 49, 5, input);

                    throw nvae;        }
            }
        };
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA49_2 = input.LA(1);
                if ( LA49_2==ID ) {return s5;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 49, 2, input);

                throw nvae;
            }
        };
        DFA.State s3 = new DFA.State() {{alt=3;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA49_1 = input.LA(1);
                if ( LA49_1==19 ) {return s2;}
                if ( LA49_1==EOL||LA49_1==15||LA49_1==23 ) {return s3;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 49, 1, input);

                throw nvae;
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA49_0 = input.LA(1);
                if ( LA49_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 49, 0, input);

                throw nvae;
            }
        };

    }class DFA50 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s2;

                case EOL:
                case 15:
                    return s1;

                case 24:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s1;

                case 25:
                    return s2;

                case 24:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA61 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s1;

                case ID:
                    return s2;

                case 25:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 61, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s1;

                case ID:
                    return s2;

                case 25:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 61, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA64 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 24:
                    return s3;

                case EOL:
                case 15:
                    return s1;

                case 25:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s1;

                case 25:
                    return s2;

                case 24:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 64, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA65 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s15 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 24:
                case 25:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s3;

                case EOL:
                case 15:
                    return s2;

                case 33:
                    return s15;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 65, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s2;

                case 24:
                case 25:
                case 62:
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                    return s3;

                case 33:
                    return s15;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 65, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA65_0 = input.LA(1);
                if ( LA65_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 65, 0, input);

                throw nvae;
            }
        };

    }class DFA76 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s15 = new DFA.State() {{alt=1;}};
        DFA.State s14 = new DFA.State() {{alt=2;}};
        DFA.State s13 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s15;

                case EOL:
                case 15:
                    return s13;

                case 43:
                    return s14;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 76, 13, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s13;

                case 43:
                    return s14;

                case ID:
                    return s15;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 76, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s2 = new DFA.State() {{alt=3;}};
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA76_0 = input.LA(1);
                if ( LA76_0==42 ) {return s1;}
                if ( (LA76_0>=EOL && LA76_0<=ID)||LA76_0==15||LA76_0==23||LA76_0==25||LA76_0==29||LA76_0==34||(LA76_0>=51 && LA76_0<=52)||(LA76_0>=56 && LA76_0<=60) ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 76, 0, input);

                throw nvae;
            }
        };

    }


    public static final BitSet FOLLOW_set_in_opt_eol41 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_compilation_unit57 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit61 = new BitSet(new long[]{0x0000000000468012L});
    public static final BitSet FOLLOW_rule_in_compilation_unit70 = new BitSet(new long[]{0x0000000000468012L});
    public static final BitSet FOLLOW_query_in_compilation_unit83 = new BitSet(new long[]{0x0000000000468012L});
    public static final BitSet FOLLOW_template_in_compilation_unit93 = new BitSet(new long[]{0x0000000000468012L});
    public static final BitSet FOLLOW_extra_statement_in_compilation_unit101 = new BitSet(new long[]{0x0000000000468012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog125 = new BitSet(new long[]{0x0000000000678012L});
    public static final BitSet FOLLOW_package_statement_in_prolog133 = new BitSet(new long[]{0x0000000000668012L});
    public static final BitSet FOLLOW_extra_statement_in_prolog148 = new BitSet(new long[]{0x0000000000668012L});
    public static final BitSet FOLLOW_expander_in_prolog154 = new BitSet(new long[]{0x0000000000668012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog166 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_package_statement190 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement192 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement196 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_package_statement198 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement201 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_import_statement217 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement219 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_import_name_in_import_statement223 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_import_statement225 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_function_import_statement244 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_function_import_statement246 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function_import_statement248 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_import_name_in_function_import_statement252 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_function_import_statement254 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function_import_statement257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_import_name289 = new BitSet(new long[]{0x0000000000180002L});
    public static final BitSet FOLLOW_19_in_import_name295 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_import_name299 = new BitSet(new long[]{0x0000000000180002L});
    public static final BitSet FOLLOW_20_in_import_name309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_expander329 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_dotted_name_in_expander334 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_expander338 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_expander341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_global365 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_global369 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_global373 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_global375 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_global378 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_function404 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function406 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_dotted_name_in_function411 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function415 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function419 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function421 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_function430 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function432 = new BitSet(new long[]{0x0000000002008032L});
    public static final BitSet FOLLOW_dotted_name_in_function442 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function446 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_argument_name_in_function450 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function452 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_24_in_function466 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function468 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_dotted_name_in_function473 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function477 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_argument_name_in_function481 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function483 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_25_in_function508 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function512 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_function516 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_curly_chunk_in_function523 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_function532 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_query564 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_query570 = new BitSet(new long[]{0x20000035B0020120L});
    public static final BitSet FOLLOW_word_in_query574 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_query576 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_expander_lhs_block_in_query592 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query600 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_query615 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_query617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_template641 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_template647 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_template651 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_template653 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_template_slot_in_template668 = new BitSet(new long[]{0x0000000020000020L});
    public static final BitSet FOLLOW_29_in_template683 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_template685 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot717 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_template_slot721 = new BitSet(new long[]{0x0000000000008010L});
    public static final BitSet FOLLOW_set_in_template_slot725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_rule760 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_rule766 = new BitSet(new long[]{0x20000035B0020120L});
    public static final BitSet FOLLOW_word_in_rule770 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule772 = new BitSet(new long[]{0x0000000A00008012L});
    public static final BitSet FOLLOW_rule_attributes_in_rule783 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule793 = new BitSet(new long[]{0x0000000120008012L});
    public static final BitSet FOLLOW_32_in_rule802 = new BitSet(new long[]{0x0000000200008012L});
    public static final BitSet FOLLOW_33_in_rule804 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule807 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_expander_lhs_block_in_rule825 = new BitSet(new long[]{0x0000000020008012L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule834 = new BitSet(new long[]{0x0000000020008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule857 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_rule861 = new BitSet(new long[]{0x0000000200008012L});
    public static final BitSet FOLLOW_33_in_rule863 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule867 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000000000FFL});
    public static final BitSet FOLLOW_EOL_in_rule894 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000000000FFL});
    public static final BitSet FOLLOW_29_in_rule924 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_extra_statement946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_extra_statement951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_extra_statement956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_extra_statement961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_rule_attributes980 = new BitSet(new long[]{0x0000000200008012L});
    public static final BitSet FOLLOW_33_in_rule_attributes983 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes986 = new BitSet(new long[]{0x000003F001000002L});
    public static final BitSet FOLLOW_24_in_rule_attributes993 = new BitSet(new long[]{0x000003F000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes998 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes1000 = new BitSet(new long[]{0x000003F001000002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1073 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_salience1131 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_salience1133 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_salience1137 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_salience1139 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_salience1142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_no_loop1177 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop1179 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_no_loop1181 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop1184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_no_loop1209 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1213 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop1215 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_no_loop1217 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop1220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_auto_focus1266 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1268 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_auto_focus1270 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1273 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_auto_focus1298 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1302 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1304 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_auto_focus1306 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_activation_group1351 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_activation_group1353 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_activation_group1357 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_activation_group1359 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_activation_group1362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_agenda_group1391 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_agenda_group1393 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1397 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_agenda_group1399 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_agenda_group1402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_duration1434 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_duration1436 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_duration1440 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_duration1442 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_duration1445 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1471 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_normal_lhs_block1473 = new BitSet(new long[]{0x1C00000000808032L});
    public static final BitSet FOLLOW_opt_eol_in_normal_lhs_block1485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expander_lhs_block1526 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block1530 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block1545 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_lhs_or_in_lhs1587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_from_statement1652 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_statement1654 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_from_source_in_from_statement1658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1702 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_from_source1704 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_from_source1708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1735 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_from_source1737 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_from_source1741 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1743 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_from_source1746 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1748 = new BitSet(new long[]{0x00028000040003E2L});
    public static final BitSet FOLLOW_argument_list_in_from_source1752 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1754 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_from_source1756 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1778 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1780 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_from_source1782 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1784 = new BitSet(new long[]{0x00028000040003E2L});
    public static final BitSet FOLLOW_argument_list_in_from_source1788 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1790 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_from_source1792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_accumulate_statement1841 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1843 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_accumulate_statement1845 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1847 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_accumulate_statement1857 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1859 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_accumulate_statement1863 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1865 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_accumulate_statement1867 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1869 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_accumulate_statement1877 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1879 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_accumulate_statement1881 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_paren_chunk2_in_accumulate_statement1885 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_accumulate_statement1887 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1889 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_accumulate_statement1891 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1893 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_accumulate_statement1901 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1903 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_accumulate_statement1905 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_paren_chunk2_in_accumulate_statement1909 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_accumulate_statement1911 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1913 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_accumulate_statement1915 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1917 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_accumulate_statement1925 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1927 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_accumulate_statement1929 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_paren_chunk2_in_accumulate_statement1933 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_accumulate_statement1935 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1937 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_accumulate_statement1939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_argument_value_in_argument_list1976 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_argument_list1992 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_argument_list1994 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_argument_list1996 = new BitSet(new long[]{0x00028000040003E0L});
    public static final BitSet FOLLOW_argument_value_in_argument_list2000 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_STRING_in_argument_value2040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_argument_value2051 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_argument_value2064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_argument_value2075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument_value2087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_argument_value2098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_argument_value2109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inline_map_in_argument_value2128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inline_array_in_argument_value2144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_inline_map2184 = new BitSet(new long[]{0x00028000040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2202 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_48_in_inline_map2204 = new BitSet(new long[]{0x00028000040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2208 = new BitSet(new long[]{0x0000000009000010L});
    public static final BitSet FOLLOW_EOL_in_inline_map2251 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_inline_map2255 = new BitSet(new long[]{0x00028000040003F0L});
    public static final BitSet FOLLOW_EOL_in_inline_map2258 = new BitSet(new long[]{0x00028000040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2264 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_48_in_inline_map2266 = new BitSet(new long[]{0x00028000040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2270 = new BitSet(new long[]{0x0000000009000010L});
    public static final BitSet FOLLOW_27_in_inline_map2306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_inline_array2350 = new BitSet(new long[]{0x00028000040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_array2354 = new BitSet(new long[]{0x0004000001000010L});
    public static final BitSet FOLLOW_EOL_in_inline_array2372 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_inline_array2375 = new BitSet(new long[]{0x00028000040003F0L});
    public static final BitSet FOLLOW_EOL_in_inline_array2377 = new BitSet(new long[]{0x00028000040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_array2382 = new BitSet(new long[]{0x0004000001000010L});
    public static final BitSet FOLLOW_50_in_inline_array2395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding2438 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding2448 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_fact_binding2450 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding2452 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding2456 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_fact_expression2488 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2490 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2494 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2496 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_fact_expression2498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression2509 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2511 = new BitSet(new long[]{0x0018000000000002L});
    public static final BitSet FOLLOW_set_in_fact_expression2524 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2529 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_expression2543 = new BitSet(new long[]{0x0018000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_fact2582 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2590 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_fact2598 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2601 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_constraints_in_fact2607 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2626 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_fact2630 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2664 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_constraint_in_constraints2669 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_predicate_in_constraints2672 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2680 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_constraints2682 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2684 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_constraint_in_constraints2687 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_predicate_in_constraints2690 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2717 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint2725 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2727 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_constraint2729 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2731 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint2741 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2755 = new BitSet(new long[]{0xC000000000008012L,0x00000000000000FFL});
    public static final BitSet FOLLOW_operator_in_constraint2761 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2763 = new BitSet(new long[]{0x00008000008003E0L});
    public static final BitSet FOLLOW_ID_in_constraint2781 = new BitSet(new long[]{0x0060000000008012L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint2806 = new BitSet(new long[]{0x0060000000008012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint2838 = new BitSet(new long[]{0x0060000000008012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint2858 = new BitSet(new long[]{0x0060000000008012L});
    public static final BitSet FOLLOW_set_in_constraint2893 = new BitSet(new long[]{0xC000000000000000L,0x00000000000000FFL});
    public static final BitSet FOLLOW_operator_in_constraint2915 = new BitSet(new long[]{0x00008000008003E0L});
    public static final BitSet FOLLOW_ID_in_constraint2927 = new BitSet(new long[]{0x0060000000008012L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint2955 = new BitSet(new long[]{0x0060000000008012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint2990 = new BitSet(new long[]{0x0060000000008012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint3012 = new BitSet(new long[]{0x0060000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint3068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint3095 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint3106 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint3119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint3130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_literal_constraint3142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint3173 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_enum_constraint3175 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_enum_constraint3179 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_retval_constraint3208 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint3213 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_retval_constraint3216 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_predicate3234 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_predicate3236 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_predicate3240 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_predicate3242 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_predicate3244 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_paren_chunk_in_predicate3248 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_predicate3250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_paren_chunk3296 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk3300 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_paren_chunk3302 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_23_in_paren_chunk23373 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_paren_chunk2_in_paren_chunk23377 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_paren_chunk23379 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_26_in_curly_chunk3448 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk3452 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_curly_chunk3454 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3512 = new BitSet(new long[]{0x0018000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or3521 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_or3526 = new BitSet(new long[]{0x1C00000000800020L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3533 = new BitSet(new long[]{0x0018000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3573 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and3582 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_and3587 = new BitSet(new long[]{0x1C00000000800020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3594 = new BitSet(new long[]{0x0300000000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary3632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary3642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary3652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary3666 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary3687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary3709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_lhs_unary3721 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_unary3723 = new BitSet(new long[]{0x1C00000000800020L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary3727 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_unary3729 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_unary3731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_lhs_exist3762 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_23_in_lhs_exist3765 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3769 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_exist3771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_lhs_not3807 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_23_in_lhs_not3810 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3814 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_not3817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_lhs_eval3849 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_lhs_eval3853 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000000FFL});
    public static final BitSet FOLLOW_paren_chunk2_in_lhs_eval3861 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_eval3865 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name3897 = new BitSet(new long[]{0x0002000000080002L});
    public static final BitSet FOLLOW_19_in_dotted_name3903 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_dotted_name3907 = new BitSet(new long[]{0x0002000000080002L});
    public static final BitSet FOLLOW_49_in_dotted_name3916 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_dotted_name3918 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_ID_in_argument_name3948 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_49_in_argument_name3954 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_argument_name3956 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_ID_in_word3984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_word3996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_word4005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_word4017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_word4028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_word4038 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_word4046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_word4054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_word4065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_word4076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word4090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_operator4119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_operator4126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_operator4133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_operator4140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_operator4149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_operator4156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_operator4163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_operator4170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_operator4177 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_operator4184 = new BitSet(new long[]{0x0000000000000002L});

}