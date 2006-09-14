// $ANTLR 3.0ea8 /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g 2006-09-14 21:52:37

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "BOOL", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\';\'", "\'package\'", "\'import\'", "\'function\'", "\'.\'", "\'.*\'", "\'expander\'", "\'global\'", "\'(\'", "\',\'", "\')\'", "\'{\'", "\'}\'", "\'query\'", "\'end\'", "\'template\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'attributes\'", "\'salience\'", "\'no-loop\'", "\'auto-focus\'", "\'activation-group\'", "\'agenda-group\'", "\'duration\'", "\'from\'", "\'[\'", "\']\'", "\'accumulate\'", "\'init\'", "\'action\'", "\'result\'", "\'collect\'", "\'null\'", "\'=>\'", "\'or\'", "\'||\'", "\'&\'", "\'|\'", "\'->\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'use\'", "\'==\'", "\'=\'", "\'>\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'matches\'", "\'excludes\'"
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:289:1: opt_eol : ( (';'|EOL))* ;
    public void opt_eol() throws RecognitionException {   
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:290:17: ( ( (';'|EOL))* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:290:17: ( (';'|EOL))*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:290:17: ( (';'|EOL))*
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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:290:18: (';'|EOL)
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:293:1: compilation_unit : opt_eol prolog (r= rule | q= query | t= template | extra_statement )* ;
    public void compilation_unit() throws RecognitionException {   
        RuleDescr r = null;

        QueryDescr q = null;

        FactTemplateDescr t = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:294:17: ( opt_eol prolog (r= rule | q= query | t= template | extra_statement )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:294:17: opt_eol prolog (r= rule | q= query | t= template | extra_statement )*
            {
            following.push(FOLLOW_opt_eol_in_compilation_unit57);
            opt_eol();
            following.pop();

            following.push(FOLLOW_prolog_in_compilation_unit61);
            prolog();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:296:17: (r= rule | q= query | t= template | extra_statement )*
            loop2:
            do {
                int alt2=5;
                alt2 = dfa2.predict(input); 
                switch (alt2) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:296:25: r= rule
            	    {
            	    following.push(FOLLOW_rule_in_compilation_unit70);
            	    r=rule();
            	    following.pop();

            	    this.packageDescr.addRule( r ); 

            	    }
            	    break;
            	case 2 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:297:25: q= query
            	    {
            	    following.push(FOLLOW_query_in_compilation_unit83);
            	    q=query();
            	    following.pop();

            	    this.packageDescr.addRule( q ); 

            	    }
            	    break;
            	case 3 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:298:25: t= template
            	    {
            	    following.push(FOLLOW_template_in_compilation_unit93);
            	    t=template();
            	    following.pop();

            	    this.packageDescr.addFactTemplate ( t ); 

            	    }
            	    break;
            	case 4 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:299:25: extra_statement
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:303:1: prolog : opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol ;
    public void prolog() throws RecognitionException {   
        String name = null;



        		String packageName = "";
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:307:17: ( opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:307:17: opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_prolog125);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:308:17: (name= package_statement )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:308:19: name= package_statement
                    {
                    following.push(FOLLOW_package_statement_in_prolog133);
                    name=package_statement();
                    following.pop();

                     packageName = name; 

                    }
                    break;

            }

             
            			this.packageDescr = new PackageDescr( name ); 
            		
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:312:17: ( extra_statement | expander )*
            loop4:
            do {
                int alt4=3;
                alt4 = dfa4.predict(input); 
                switch (alt4) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:312:25: extra_statement
            	    {
            	    following.push(FOLLOW_extra_statement_in_prolog148);
            	    extra_statement();
            	    following.pop();


            	    }
            	    break;
            	case 2 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:313:25: expander
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:319:1: package_statement returns [String packageName] : 'package' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   
        String packageName;
        String name = null;



        		packageName = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:324:17: ( 'package' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:324:17: 'package' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,16,FOLLOW_16_in_package_statement190); 
            following.push(FOLLOW_opt_eol_in_package_statement192);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_package_statement196);
            name=dotted_name();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:324:52: ( ';' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:324:52: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:330:1: import_statement : 'import' opt_eol name= import_name ( ';' )? opt_eol ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:331:17: ( 'import' opt_eol name= import_name ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:331:17: 'import' opt_eol name= import_name ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_import_statement217); 
            following.push(FOLLOW_opt_eol_in_import_statement219);
            opt_eol();
            following.pop();

            following.push(FOLLOW_import_name_in_import_statement223);
            name=import_name();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:331:51: ( ';' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:331:51: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:338:1: function_import_statement : 'import' 'function' opt_eol name= import_name ( ';' )? opt_eol ;
    public void function_import_statement() throws RecognitionException {   
        String name = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:339:17: ( 'import' 'function' opt_eol name= import_name ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:339:17: 'import' 'function' opt_eol name= import_name ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_function_import_statement244); 
            match(input,18,FOLLOW_18_in_function_import_statement246); 
            following.push(FOLLOW_opt_eol_in_function_import_statement248);
            opt_eol();
            following.pop();

            following.push(FOLLOW_import_name_in_function_import_statement252);
            name=import_name();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:339:62: ( ';' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:339:62: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:1: import_name returns [String name] : id= ID ( '.' id= ID )* (star= '.*' )? ;
    public String import_name() throws RecognitionException {   
        String name;
        Token id=null;
        Token star=null;


        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:352:17: (id= ID ( '.' id= ID )* (star= '.*' )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:352:17: id= ID ( '.' id= ID )* (star= '.*' )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name289); 
             name=id.getText(); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:352:46: ( '.' id= ID )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);
                if ( LA8_0==19 ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:352:48: '.' id= ID
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

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:352:99: (star= '.*' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:352:100: star= '.*'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:354:1: expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void expander() throws RecognitionException {   
        String name = null;



        		String config=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:358:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:358:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {
            match(input,21,FOLLOW_21_in_expander329); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:358:28: (name= dotted_name )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:358:29: name= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_expander334);
                    name=dotted_name();
                    following.pop();


                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:358:48: ( ';' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:358:48: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:368:1: global : 'global' type= dotted_name id= ID ( ';' )? opt_eol ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;



        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:372:17: ( 'global' type= dotted_name id= ID ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:372:17: 'global' type= dotted_name id= ID ( ';' )? opt_eol
            {
            match(input,22,FOLLOW_22_in_global365); 
            following.push(FOLLOW_dotted_name_in_global369);
            type=dotted_name();
            following.pop();

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global373); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:372:49: ( ';' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:372:49: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:378:1: function : loc= 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol ;
    public void function() throws RecognitionException {   
        Token loc=null;
        Token name=null;
        String retType = null;

        String paramType = null;

        String paramName = null;

        String body = null;



        		FunctionDescr f = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:383:17: (loc= 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:383:17: loc= 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,18,FOLLOW_18_in_function404); 
            following.push(FOLLOW_opt_eol_in_function406);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:383:40: (retType= dotted_name )?
            int alt13=2;
            alt13 = dfa13.predict(input); 
            switch (alt13) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:383:41: retType= dotted_name
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

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:390:25: ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:390:33: (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )*
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:390:33: (paramType= dotted_name )?
                    int alt14=2;
                    alt14 = dfa14.predict(input); 
                    switch (alt14) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:390:34: paramType= dotted_name
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
                    				
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:394:33: ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);
                        if ( LA16_0==24 ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:394:41: ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol
                    	    {
                    	    match(input,24,FOLLOW_24_in_function466); 
                    	    following.push(FOLLOW_opt_eol_in_function468);
                    	    opt_eol();
                    	    following.pop();

                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:394:53: (paramType= dotted_name )?
                    	    int alt15=2;
                    	    alt15 = dfa15.predict(input); 
                    	    switch (alt15) {
                    	        case 1 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:394:54: paramType= dotted_name
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:415:1: query returns [QueryDescr query] : opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query;
        Token loc=null;
        String queryName = null;



        		query = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:420:17: ( opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:420:17: opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol
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
            		
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
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
            case 59:
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
            case 60:
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
            case 61:
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
            case 58:
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
            case 72:
                alt18=1;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("428:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:429:25: {...}? expander_lhs_block[lhs]
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:430:27: normal_lhs_block[lhs]
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:436:1: template returns [FactTemplateDescr template] : opt_eol loc= 'template' templateName= ID EOL (slot= template_slot )+ 'end' EOL ;
    public FactTemplateDescr template() throws RecognitionException {   
        FactTemplateDescr template;
        Token loc=null;
        Token templateName=null;
        FieldTemplateDescr slot = null;



        		template = null;		
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:441:17: ( opt_eol loc= 'template' templateName= ID EOL (slot= template_slot )+ 'end' EOL )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:441:17: opt_eol loc= 'template' templateName= ID EOL (slot= template_slot )+ 'end' EOL
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
            		
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:447:17: (slot= template_slot )+
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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:448:25: slot= template_slot
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:456:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name name= ID (EOL|';');
    public FieldTemplateDescr template_slot() throws RecognitionException {   
        FieldTemplateDescr field;
        Token name=null;
        String fieldType = null;



        		field = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:462:18: (fieldType= dotted_name name= ID (EOL|';'))
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:462:18: fieldType= dotted_name name= ID (EOL|';')
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:471:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )? )? 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule;
        Token loc=null;
        Token any=null;
        String ruleName = null;



        		rule = null;
        		String consequence = "";
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:477:17: ( opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )? )? 'end' opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:477:17: opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )? )? 'end' opt_eol
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
            		
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:484:17: ( rule_attributes[rule] )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:484:25: rule_attributes[rule]
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

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:487:17: ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )? )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:487:18: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )?
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:487:18: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?
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
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:487:25: loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                            {
                            loc=(Token)input.LT(1);
                            match(input,32,FOLLOW_32_in_rule802); 
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:487:36: ( ':' )?
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
                            else if ( (LA21_0>=EOL && LA21_0<=32)||(LA21_0>=34 && LA21_0<=72) ) {
                                alt21=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("487:36: ( \':\' )?", 21, 0, input);

                                throw nvae;
                            }
                            switch (alt21) {
                                case 1 :
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:487:36: ':'
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
                            			
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
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
                            case 59:
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
                            case 60:
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
                            case 61:
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
                            case 58:
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
                            case 72:
                                alt22=1;
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("492:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 0, input);

                                throw nvae;
                            }

                            switch (alt22) {
                                case 1 :
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:493:33: {...}? expander_lhs_block[lhs]
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
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:494:35: normal_lhs_block[lhs]
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

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:498:17: ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )* )?
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
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:498:19: opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . ( EOL )* )*
                            {
                            following.push(FOLLOW_opt_eol_in_rule857);
                            opt_eol();
                            following.pop();

                            loc=(Token)input.LT(1);
                            match(input,34,FOLLOW_34_in_rule861); 
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:498:38: ( ':' )?
                            int alt24=2;
                            int LA24_0 = input.LA(1);
                            if ( LA24_0==33 ) {
                                alt24=1;
                            }
                            else if ( (LA24_0>=EOL && LA24_0<=32)||(LA24_0>=34 && LA24_0<=72) ) {
                                alt24=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("498:38: ( \':\' )?", 24, 0, input);

                                throw nvae;
                            }
                            switch (alt24) {
                                case 1 :
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:498:38: ':'
                                    {
                                    match(input,33,FOLLOW_33_in_rule863); 

                                    }
                                    break;

                            }

                            following.push(FOLLOW_opt_eol_in_rule867);
                            opt_eol();
                            following.pop();

                            int prevLine = loc.getLine()+1; 
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:500:25: ( options {greedy=false; } : any= . ( EOL )* )*
                            loop26:
                            do {
                                int alt26=2;
                                int LA26_0 = input.LA(1);
                                if ( LA26_0==29 ) {
                                    alt26=2;
                                }
                                else if ( (LA26_0>=EOL && LA26_0<=28)||(LA26_0>=30 && LA26_0<=72) ) {
                                    alt26=1;
                                }


                                switch (alt26) {
                            	case 1 :
                            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:500:52: any= . ( EOL )*
                            	    {
                            	    any=(Token)input.LT(1);
                            	    matchAny(input); 
                            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:500:58: ( EOL )*
                            	    loop25:
                            	    do {
                            	        int alt25=2;
                            	        int LA25_0 = input.LA(1);
                            	        if ( LA25_0==EOL ) {
                            	            alt25=1;
                            	        }


                            	        switch (alt25) {
                            	    	case 1 :
                            	    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:500:59: EOL
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:530:1: extra_statement : ( import_statement | function_import_statement | global | function ) ;
    public void extra_statement() throws RecognitionException {   
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:532:9: ( ( import_statement | function_import_statement | global | function ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:532:9: ( import_statement | function_import_statement | global | function )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:532:9: ( import_statement | function_import_statement | global | function )
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:532:17: import_statement
                    {
                    following.push(FOLLOW_import_statement_in_extra_statement946);
                    import_statement();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:533:17: function_import_statement
                    {
                    following.push(FOLLOW_function_import_statement_in_extra_statement951);
                    function_import_statement();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:534:17: global
                    {
                    following.push(FOLLOW_global_in_extra_statement956);
                    global();
                    following.pop();


                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:535:17: function
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:539:1: rule_attributes[RuleDescr rule] : ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:541:25: ( ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:541:25: ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:541:25: ( 'attributes' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:541:25: 'attributes'
                    {
                    match(input,35,FOLLOW_35_in_rule_attributes980); 

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:541:39: ( ':' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:541:39: ':'
                    {
                    match(input,33,FOLLOW_33_in_rule_attributes983); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule_attributes986);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:542:25: ( ( ',' )? a= rule_attribute opt_eol )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);
                if ( LA33_0==24||(LA33_0>=36 && LA33_0<=41) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:542:33: ( ',' )? a= rule_attribute opt_eol
            	    {
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:542:33: ( ',' )?
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
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:542:33: ','
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:549:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d;
        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:554:25: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus )
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:554:25: a= salience
                    {
                    following.push(FOLLOW_salience_in_rule_attribute1039);
                    a=salience();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:555:25: a= no_loop
                    {
                    following.push(FOLLOW_no_loop_in_rule_attribute1049);
                    a=no_loop();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:556:25: a= agenda_group
                    {
                    following.push(FOLLOW_agenda_group_in_rule_attribute1060);
                    a=agenda_group();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:557:25: a= duration
                    {
                    following.push(FOLLOW_duration_in_rule_attribute1073);
                    a=duration();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:558:25: a= activation_group
                    {
                    following.push(FOLLOW_activation_group_in_rule_attribute1087);
                    a=activation_group();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:559:25: a= auto_focus
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:563:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:568:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:568:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,36,FOLLOW_36_in_salience1131); 
            following.push(FOLLOW_opt_eol_in_salience1133);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience1137); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:568:46: ( ';' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:568:46: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:575:1: no_loop returns [AttributeDescr d] : ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:580:17: ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) )
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:580:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:580:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:581:25: loc= 'no-loop' opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,37,FOLLOW_37_in_no_loop1177); 
                    following.push(FOLLOW_opt_eol_in_no_loop1179);
                    opt_eol();
                    following.pop();

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:581:47: ( ';' )?
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
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:581:47: ';'
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:588:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:588:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:589:25: loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,37,FOLLOW_37_in_no_loop1209); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1213); 
                    following.push(FOLLOW_opt_eol_in_no_loop1215);
                    opt_eol();
                    following.pop();

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:589:54: ( ';' )?
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
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:589:54: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:599:1: auto_focus returns [AttributeDescr d] : ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr auto_focus() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:604:17: ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) )
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:604:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:604:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:605:25: loc= 'auto-focus' opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_auto_focus1266); 
                    following.push(FOLLOW_opt_eol_in_auto_focus1268);
                    opt_eol();
                    following.pop();

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:605:50: ( ';' )?
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
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:605:50: ';'
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:612:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:612:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:613:25: loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_auto_focus1298); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1302); 
                    following.push(FOLLOW_opt_eol_in_auto_focus1304);
                    opt_eol();
                    following.pop();

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:613:57: ( ';' )?
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
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:613:57: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:623:1: activation_group returns [AttributeDescr d] : loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr activation_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:628:17: (loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:628:17: loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,39,FOLLOW_39_in_activation_group1351); 
            following.push(FOLLOW_opt_eol_in_activation_group1353);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1357); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:628:60: ( ';' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:628:60: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:635:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr agenda_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:640:17: (loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:640:17: loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,40,FOLLOW_40_in_agenda_group1391); 
            following.push(FOLLOW_opt_eol_in_agenda_group1393);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1397); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:640:56: ( ';' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:640:56: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:648:1: duration returns [AttributeDescr d] : loc= 'duration' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr duration() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:653:17: (loc= 'duration' opt_eol i= INT ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:653:17: loc= 'duration' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,41,FOLLOW_41_in_duration1434); 
            following.push(FOLLOW_opt_eol_in_duration1436);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1440); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:653:46: ( ';' )?
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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:653:46: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:661:1: normal_lhs_block[AndDescr descrs] : (d= lhs opt_eol )* opt_eol ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        BaseDescr d = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:663:17: ( (d= lhs opt_eol )* opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:663:17: (d= lhs opt_eol )* opt_eol
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:663:17: (d= lhs opt_eol )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);
                if ( LA45_0==ID||LA45_0==23||(LA45_0>=59 && LA45_0<=61) ) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:663:25: d= lhs opt_eol
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:671:1: expander_lhs_block[AndDescr descrs] : ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )* ;
    public void expander_lhs_block(AndDescr descrs) throws RecognitionException {   
        Token loc=null;
        String text = null;



        		String lhsBlock = null;
        		String eol = System.getProperty( "line.separator" );
        		List constraints = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:678:17: ( ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:678:17: ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:678:17: ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )*
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
                case 72:
                    alt47=1;
                    break;
                case 15:
                    alt47=2;
                    break;

                }

                switch (alt47) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:679:25: text= paren_chunk loc= EOL ( EOL )*
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
            	    			
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:705:17: ( EOL )*
            	    loop46:
            	    do {
            	        int alt46=2;
            	        int LA46_0 = input.LA(1);
            	        if ( LA46_0==EOL ) {
            	            alt46=1;
            	        }


            	        switch (alt46) {
            	    	case 1 :
            	    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:705:18: EOL
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:719:1: lhs returns [BaseDescr d] : l= lhs_or ;
    public BaseDescr lhs() throws RecognitionException {   
        BaseDescr d;
        BaseDescr l = null;



        		d=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:723:17: (l= lhs_or )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:723:17: l= lhs_or
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:727:1: lhs_column returns [BaseDescr d] : (f= fact_binding | f= fact );
    public BaseDescr lhs_column() throws RecognitionException {   
        BaseDescr d;
        BaseDescr f = null;



        		d=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:731:17: (f= fact_binding | f= fact )
            int alt48=2;
            alt48 = dfa48.predict(input); 
            switch (alt48) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:731:17: f= fact_binding
                    {
                    following.push(FOLLOW_fact_binding_in_lhs_column1615);
                    f=fact_binding();
                    following.pop();

                     d = f; 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:732:17: f= fact
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:735:1: from_statement returns [FromDescr d] : 'from' opt_eol ds= from_source ;
    public FromDescr from_statement() throws RecognitionException {   
        FromDescr d;
        DeclarativeInvokerDescr ds = null;



        		d=factory.createFrom();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:740:17: ( 'from' opt_eol ds= from_source )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:740:17: 'from' opt_eol ds= from_source
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:750:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID ( '[' arg= argument_value ']' )? ) | (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) | (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) );
    public DeclarativeInvokerDescr from_source() throws RecognitionException {   
        DeclarativeInvokerDescr ds;
        Token var=null;
        Token field=null;
        Token method=null;
        Token functionName=null;
        ArgumentValueDescr arg = null;

        ArrayList args = null;



        		ds = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:755:17: ( (var= ID '.' field= ID ( '[' arg= argument_value ']' )? ) | (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) | (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) )
            int alt50=3;
            alt50 = dfa50.predict(input); 
            switch (alt50) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:755:17: (var= ID '.' field= ID ( '[' arg= argument_value ']' )? )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:755:17: (var= ID '.' field= ID ( '[' arg= argument_value ']' )? )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:755:18: var= ID '.' field= ID ( '[' arg= argument_value ']' )?
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1702); 
                    match(input,19,FOLLOW_19_in_from_source1704); 
                    field=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1708); 
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:755:39: ( '[' arg= argument_value ']' )?
                    int alt49=2;
                    int LA49_0 = input.LA(1);
                    if ( LA49_0==43 ) {
                        alt49=1;
                    }
                    else if ( (LA49_0>=EOL && LA49_0<=ID)||LA49_0==15||LA49_0==23||LA49_0==25||LA49_0==29||LA49_0==34||(LA49_0>=52 && LA49_0<=53)||(LA49_0>=57 && LA49_0<=61) ) {
                        alt49=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("755:39: ( \'[\' arg= argument_value \']\' )?", 49, 0, input);

                        throw nvae;
                    }
                    switch (alt49) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:755:40: '[' arg= argument_value ']'
                            {
                            match(input,43,FOLLOW_43_in_from_source1712); 
                            following.push(FOLLOW_argument_value_in_from_source1716);
                            arg=argument_value();
                            following.pop();

                            match(input,44,FOLLOW_44_in_from_source1718); 

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
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:770:17: (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:770:17: (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:770:18: var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')'
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1747); 
                    match(input,19,FOLLOW_19_in_from_source1749); 
                    method=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1753); 
                    following.push(FOLLOW_opt_eol_in_from_source1755);
                    opt_eol();
                    following.pop();

                    match(input,23,FOLLOW_23_in_from_source1758); 
                    following.push(FOLLOW_opt_eol_in_from_source1760);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_argument_list_in_from_source1764);
                    args=argument_list();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_from_source1766);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_from_source1768); 

                    			  MethodAccessDescr ma = new MethodAccessDescr(var.getText(), method.getText());	
                    			  ma.setLocation( offset(var.getLine()), var.getCharPositionInLine() );
                    			  ma.setArguments(args);
                    			  ds = ma;
                    			

                    }


                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:779:17: (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:779:17: (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:779:18: functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')'
                    {
                    functionName=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1790); 
                    following.push(FOLLOW_opt_eol_in_from_source1792);
                    opt_eol();
                    following.pop();

                    match(input,23,FOLLOW_23_in_from_source1794); 
                    following.push(FOLLOW_opt_eol_in_from_source1796);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_argument_list_in_from_source1800);
                    args=argument_list();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_from_source1802);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_from_source1804); 

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:792:1: accumulate_statement returns [AccumulateDescr d] : loc= 'from' opt_eol 'accumulate' opt_eol '(' opt_eol column= lhs_column opt_eol ',' opt_eol 'init' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'action' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'result' opt_eol '(' c= paren_chunk2 ')' opt_eol ')' ;
    public AccumulateDescr accumulate_statement() throws RecognitionException {   
        AccumulateDescr d;
        Token loc=null;
        BaseDescr column = null;

        String c = null;



        		d = factory.createAccumulate();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:797:17: (loc= 'from' opt_eol 'accumulate' opt_eol '(' opt_eol column= lhs_column opt_eol ',' opt_eol 'init' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'action' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'result' opt_eol '(' c= paren_chunk2 ')' opt_eol ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:797:17: loc= 'from' opt_eol 'accumulate' opt_eol '(' opt_eol column= lhs_column opt_eol ',' opt_eol 'init' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'action' opt_eol '(' c= paren_chunk2 ')' opt_eol ',' opt_eol 'result' opt_eol '(' c= paren_chunk2 ')' opt_eol ')'
            {
            loc=(Token)input.LT(1);
            match(input,42,FOLLOW_42_in_accumulate_statement1853); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1855);
            opt_eol();
            following.pop();

            match(input,45,FOLLOW_45_in_accumulate_statement1857); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1859);
            opt_eol();
            following.pop();

             
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            match(input,23,FOLLOW_23_in_accumulate_statement1869); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1871);
            opt_eol();
            following.pop();

            following.push(FOLLOW_lhs_column_in_accumulate_statement1875);
            column=lhs_column();
            following.pop();

            following.push(FOLLOW_opt_eol_in_accumulate_statement1877);
            opt_eol();
            following.pop();

            match(input,24,FOLLOW_24_in_accumulate_statement1879); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1881);
            opt_eol();
            following.pop();


            		        d.setSourceColumn( (ColumnDescr)column );
            		
            match(input,46,FOLLOW_46_in_accumulate_statement1889); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1891);
            opt_eol();
            following.pop();

            match(input,23,FOLLOW_23_in_accumulate_statement1893); 
            following.push(FOLLOW_paren_chunk2_in_accumulate_statement1897);
            c=paren_chunk2();
            following.pop();

            match(input,25,FOLLOW_25_in_accumulate_statement1899); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1901);
            opt_eol();
            following.pop();

            match(input,24,FOLLOW_24_in_accumulate_statement1903); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1905);
            opt_eol();
            following.pop();


            		        d.setInitCode( c );
            		
            match(input,47,FOLLOW_47_in_accumulate_statement1913); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1915);
            opt_eol();
            following.pop();

            match(input,23,FOLLOW_23_in_accumulate_statement1917); 
            following.push(FOLLOW_paren_chunk2_in_accumulate_statement1921);
            c=paren_chunk2();
            following.pop();

            match(input,25,FOLLOW_25_in_accumulate_statement1923); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1925);
            opt_eol();
            following.pop();

            match(input,24,FOLLOW_24_in_accumulate_statement1927); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1929);
            opt_eol();
            following.pop();


            		        d.setActionCode( c );
            		
            match(input,48,FOLLOW_48_in_accumulate_statement1937); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1939);
            opt_eol();
            following.pop();

            match(input,23,FOLLOW_23_in_accumulate_statement1941); 
            following.push(FOLLOW_paren_chunk2_in_accumulate_statement1945);
            c=paren_chunk2();
            following.pop();

            match(input,25,FOLLOW_25_in_accumulate_statement1947); 
            following.push(FOLLOW_opt_eol_in_accumulate_statement1949);
            opt_eol();
            following.pop();

            match(input,25,FOLLOW_25_in_accumulate_statement1951); 

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


    // $ANTLR start collect_statement
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:819:1: collect_statement returns [CollectDescr d] : loc= 'from' opt_eol 'collect' opt_eol '(' opt_eol column= lhs_column opt_eol ')' ;
    public CollectDescr collect_statement() throws RecognitionException {   
        CollectDescr d;
        Token loc=null;
        BaseDescr column = null;



        		d = factory.createCollect();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:824:17: (loc= 'from' opt_eol 'collect' opt_eol '(' opt_eol column= lhs_column opt_eol ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:824:17: loc= 'from' opt_eol 'collect' opt_eol '(' opt_eol column= lhs_column opt_eol ')'
            {
            loc=(Token)input.LT(1);
            match(input,42,FOLLOW_42_in_collect_statement1994); 
            following.push(FOLLOW_opt_eol_in_collect_statement1996);
            opt_eol();
            following.pop();

            match(input,49,FOLLOW_49_in_collect_statement1998); 
            following.push(FOLLOW_opt_eol_in_collect_statement2000);
            opt_eol();
            following.pop();

             
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            match(input,23,FOLLOW_23_in_collect_statement2010); 
            following.push(FOLLOW_opt_eol_in_collect_statement2012);
            opt_eol();
            following.pop();

            following.push(FOLLOW_lhs_column_in_collect_statement2016);
            column=lhs_column();
            following.pop();

            following.push(FOLLOW_opt_eol_in_collect_statement2018);
            opt_eol();
            following.pop();

            match(input,25,FOLLOW_25_in_collect_statement2020); 

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:834:1: argument_list returns [ArrayList args] : (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )? ;
    public ArrayList argument_list() throws RecognitionException {   
        ArrayList args;
        ArgumentValueDescr param = null;



        		args = new ArrayList();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:839:17: ( (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:839:17: (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )?
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:839:17: (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )?
            int alt52=2;
            int LA52_0 = input.LA(1);
            if ( (LA52_0>=ID && LA52_0<=FLOAT)||LA52_0==26||LA52_0==43||LA52_0==50 ) {
                alt52=1;
            }
            else if ( LA52_0==EOL||LA52_0==15||LA52_0==25 ) {
                alt52=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("839:17: (param= argument_value ( opt_eol \',\' opt_eol param= argument_value )* )?", 52, 0, input);

                throw nvae;
            }
            switch (alt52) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:839:18: param= argument_value ( opt_eol ',' opt_eol param= argument_value )*
                    {
                    following.push(FOLLOW_argument_value_in_argument_list2053);
                    param=argument_value();
                    following.pop();


                    			if (param != null) {
                    				args.add(param);
                    			}
                    		
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:845:17: ( opt_eol ',' opt_eol param= argument_value )*
                    loop51:
                    do {
                        int alt51=2;
                        alt51 = dfa51.predict(input); 
                        switch (alt51) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:846:25: opt_eol ',' opt_eol param= argument_value
                    	    {
                    	    following.push(FOLLOW_opt_eol_in_argument_list2069);
                    	    opt_eol();
                    	    following.pop();

                    	    match(input,24,FOLLOW_24_in_argument_list2071); 
                    	    following.push(FOLLOW_opt_eol_in_argument_list2073);
                    	    opt_eol();
                    	    following.pop();

                    	    following.push(FOLLOW_argument_value_in_argument_list2077);
                    	    param=argument_value();
                    	    following.pop();


                    	    				if (param != null) {
                    	    					args.add(param);
                    	    				}
                    	    			

                    	    }
                    	    break;

                    	default :
                    	    break loop51;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:855:1: argument_value returns [ArgumentValueDescr value] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map | a= inline_array ) ;
    public ArgumentValueDescr argument_value() throws RecognitionException {   
        ArgumentValueDescr value;
        Token t=null;
        ArgumentValueDescr.MapDescr m = null;

        List a = null;



        		value = null;
        		String text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:860:17: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map | a= inline_array ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:860:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map | a= inline_array )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:860:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map | a= inline_array )
            int alt53=9;
            switch ( input.LA(1) ) {
            case STRING:
                alt53=1;
                break;
            case INT:
                alt53=2;
                break;
            case FLOAT:
                alt53=3;
                break;
            case BOOL:
                alt53=4;
                break;
            case ID:
                alt53=5;
                break;
            case 50:
                alt53=6;
                break;
            case 26:
                alt53=8;
                break;
            case 43:
                alt53=9;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("860:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= \'null\' | t= \'null\' | m= inline_map | a= inline_array )", 53, 0, input);

                throw nvae;
            }

            switch (alt53) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:860:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_argument_value2117); 
                     text = getString( t );  value=new ArgumentValueDescr(ArgumentValueDescr.STRING, text);

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:861:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_argument_value2128); 
                     text = t.getText();  value=new ArgumentValueDescr(ArgumentValueDescr.INTEGRAL, text);

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:862:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_argument_value2141); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.DECIMAL, text); 

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:863:25: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_argument_value2152); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.BOOLEAN, text); 

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:864:25: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_argument_value2164); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.VARIABLE, text);

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:865:25: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,50,FOLLOW_50_in_argument_value2175); 
                     text = "null"; value=new ArgumentValueDescr(ArgumentValueDescr.NULL, text);

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:866:25: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,50,FOLLOW_50_in_argument_value2186); 
                     text = "null"; value=new ArgumentValueDescr(ArgumentValueDescr.NULL, text);

                    }
                    break;
                case 8 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:867:25: m= inline_map
                    {
                    following.push(FOLLOW_inline_map_in_argument_value2205);
                    m=inline_map();
                    following.pop();

                      value=new ArgumentValueDescr(ArgumentValueDescr.MAP, m.getKeyValuePairs() ); 

                    }
                    break;
                case 9 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:868:25: a= inline_array
                    {
                    following.push(FOLLOW_inline_array_in_argument_value2221);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:872:1: inline_map returns [ArgumentValueDescr.MapDescr mapDescr] : '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}' ;
    public ArgumentValueDescr.MapDescr inline_map() throws RecognitionException {   
        ArgumentValueDescr.MapDescr mapDescr;
        ArgumentValueDescr key = null;

        ArgumentValueDescr value = null;



                mapDescr = new ArgumentValueDescr.MapDescr();
            
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:876:8: ( '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:876:8: '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}'
            {
            match(input,26,FOLLOW_26_in_inline_map2261); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:877:12: (key= argument_value '=>' value= argument_value )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:877:14: key= argument_value '=>' value= argument_value
            {
            following.push(FOLLOW_argument_value_in_inline_map2279);
            key=argument_value();
            following.pop();

            match(input,51,FOLLOW_51_in_inline_map2281); 
            following.push(FOLLOW_argument_value_in_inline_map2285);
            value=argument_value();
            following.pop();


                             if ( key != null ) {
                                 mapDescr.add( new ArgumentValueDescr.KeyValuePairDescr( key, value ) );
                             }
                         

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:884:12: ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )*
            loop56:
            do {
                int alt56=2;
                int LA56_0 = input.LA(1);
                if ( LA56_0==EOL||LA56_0==24 ) {
                    alt56=1;
                }


                switch (alt56) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:884:14: ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value
            	    {
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:884:14: ( EOL )?
            	    int alt54=2;
            	    int LA54_0 = input.LA(1);
            	    if ( LA54_0==EOL ) {
            	        alt54=1;
            	    }
            	    else if ( LA54_0==24 ) {
            	        alt54=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("884:14: ( EOL )?", 54, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt54) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:884:15: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_map2328); 

            	            }
            	            break;

            	    }

            	    match(input,24,FOLLOW_24_in_inline_map2332); 
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:884:25: ( EOL )?
            	    int alt55=2;
            	    int LA55_0 = input.LA(1);
            	    if ( LA55_0==EOL ) {
            	        alt55=1;
            	    }
            	    else if ( (LA55_0>=ID && LA55_0<=FLOAT)||LA55_0==26||LA55_0==43||LA55_0==50 ) {
            	        alt55=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("884:25: ( EOL )?", 55, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt55) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:884:26: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_map2335); 

            	            }
            	            break;

            	    }

            	    following.push(FOLLOW_argument_value_in_inline_map2341);
            	    key=argument_value();
            	    following.pop();

            	    match(input,51,FOLLOW_51_in_inline_map2343); 
            	    following.push(FOLLOW_argument_value_in_inline_map2347);
            	    value=argument_value();
            	    following.pop();


            	                     if ( key != null ) {
            	                         mapDescr.add( new ArgumentValueDescr.KeyValuePairDescr( key, value ) );
            	                     }
            	                 

            	    }
            	    break;

            	default :
            	    break loop56;
                }
            } while (true);

            match(input,27,FOLLOW_27_in_inline_map2383); 

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:893:1: inline_array returns [List list] : '[' arg= argument_value ( ( EOL )? ',' ( EOL )? arg= argument_value )* ']' ;
    public List inline_array() throws RecognitionException {   
        List list;
        ArgumentValueDescr arg = null;



            	list = new ArrayList();
            
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:898:5: ( '[' arg= argument_value ( ( EOL )? ',' ( EOL )? arg= argument_value )* ']' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:898:5: '[' arg= argument_value ( ( EOL )? ',' ( EOL )? arg= argument_value )* ']'
            {
            match(input,43,FOLLOW_43_in_inline_array2427); 
            following.push(FOLLOW_argument_value_in_inline_array2431);
            arg=argument_value();
            following.pop();

             list.add(arg); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:900:10: ( ( EOL )? ',' ( EOL )? arg= argument_value )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);
                if ( LA59_0==EOL||LA59_0==24 ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:900:12: ( EOL )? ',' ( EOL )? arg= argument_value
            	    {
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:900:12: ( EOL )?
            	    int alt57=2;
            	    int LA57_0 = input.LA(1);
            	    if ( LA57_0==EOL ) {
            	        alt57=1;
            	    }
            	    else if ( LA57_0==24 ) {
            	        alt57=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("900:12: ( EOL )?", 57, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt57) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:900:12: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_array2449); 

            	            }
            	            break;

            	    }

            	    match(input,24,FOLLOW_24_in_inline_array2452); 
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:900:21: ( EOL )?
            	    int alt58=2;
            	    int LA58_0 = input.LA(1);
            	    if ( LA58_0==EOL ) {
            	        alt58=1;
            	    }
            	    else if ( (LA58_0>=ID && LA58_0<=FLOAT)||LA58_0==26||LA58_0==43||LA58_0==50 ) {
            	        alt58=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("900:21: ( EOL )?", 58, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt58) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:900:21: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_array2454); 

            	            }
            	            break;

            	    }

            	    following.push(FOLLOW_argument_value_in_inline_array2459);
            	    arg=argument_value();
            	    following.pop();

            	     list.add(arg); 

            	    }
            	    break;

            	default :
            	    break loop59;
                }
            } while (true);

            match(input,44,FOLLOW_44_in_inline_array2472); 

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:905:1: fact_binding returns [BaseDescr d] : id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()] ;
    public BaseDescr fact_binding() throws RecognitionException {   
        BaseDescr d;
        Token id=null;
        BaseDescr fe = null;



        		d=null;
        		boolean multi=false;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:911:17: (id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:911:17: id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding2515); 
            following.push(FOLLOW_opt_eol_in_fact_binding2525);
            opt_eol();
            following.pop();

            match(input,33,FOLLOW_33_in_fact_binding2527); 
            following.push(FOLLOW_opt_eol_in_fact_binding2529);
            opt_eol();
            following.pop();

            following.push(FOLLOW_fact_expression_in_fact_binding2533);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:919:2: fact_expression[String id] returns [BaseDescr pd] : ( '(' opt_eol fe= fact_expression[id] opt_eol ')' | f= fact opt_eol ( ('or'|'||') opt_eol f= fact )* );
    public BaseDescr fact_expression(String id) throws RecognitionException {   
        BaseDescr pd;
        BaseDescr fe = null;

        BaseDescr f = null;



         		pd = null;
         		boolean multi = false;
         	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:924:17: ( '(' opt_eol fe= fact_expression[id] opt_eol ')' | f= fact opt_eol ( ('or'|'||') opt_eol f= fact )* )
            int alt61=2;
            int LA61_0 = input.LA(1);
            if ( LA61_0==23 ) {
                alt61=1;
            }
            else if ( LA61_0==ID ) {
                alt61=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("919:2: fact_expression[String id] returns [BaseDescr pd] : ( \'(\' opt_eol fe= fact_expression[id] opt_eol \')\' | f= fact opt_eol ( (\'or\'|\'||\') opt_eol f= fact )* );", 61, 0, input);

                throw nvae;
            }
            switch (alt61) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:924:17: '(' opt_eol fe= fact_expression[id] opt_eol ')'
                    {
                    match(input,23,FOLLOW_23_in_fact_expression2565); 
                    following.push(FOLLOW_opt_eol_in_fact_expression2567);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_fact_expression_in_fact_expression2571);
                    fe=fact_expression(id);
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_fact_expression2573);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_fact_expression2575); 
                     pd=fe; 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:925:17: f= fact opt_eol ( ('or'|'||') opt_eol f= fact )*
                    {
                    following.push(FOLLOW_fact_in_fact_expression2586);
                    f=fact();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_fact_expression2588);
                    opt_eol();
                    following.pop();


                     			((ColumnDescr)f).setIdentifier( id );
                     			pd = f;
                     		
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:930:17: ( ('or'|'||') opt_eol f= fact )*
                    loop60:
                    do {
                        int alt60=2;
                        int LA60_0 = input.LA(1);
                        if ( (LA60_0>=52 && LA60_0<=53) ) {
                            alt60=1;
                        }


                        switch (alt60) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:930:25: ('or'|'||') opt_eol f= fact
                    	    {
                    	    if ( (input.LA(1)>=52 && input.LA(1)<=53) ) {
                    	        input.consume();
                    	        errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression2601);    throw mse;
                    	    }

                    	    following.push(FOLLOW_opt_eol_in_fact_expression2606);
                    	    opt_eol();
                    	    following.pop();

                    	    	if ( ! multi ) {
                    	     					BaseDescr first = pd;
                    	     					pd = new OrDescr();
                    	     					((OrDescr)pd).addDescr( first );
                    	     					multi=true;
                    	     				}
                    	     			
                    	    following.push(FOLLOW_fact_in_fact_expression2620);
                    	    f=fact();
                    	    following.pop();


                    	     				((ColumnDescr)f).setIdentifier( id );
                    	     				((OrDescr)pd).addDescr( f );
                    	     			

                    	    }
                    	    break;

                    	default :
                    	    break loop60;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:946:1: fact returns [BaseDescr d] : id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol endLoc= ')' opt_eol ;
    public BaseDescr fact() throws RecognitionException {   
        BaseDescr d;
        Token loc=null;
        Token endLoc=null;
        String id = null;

        List c = null;



        		d=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:950:17: (id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol endLoc= ')' opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:950:17: id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol endLoc= ')' opt_eol
            {
            following.push(FOLLOW_dotted_name_in_fact2659);
            id=dotted_name();
            following.pop();

             
             			d = new ColumnDescr( id ); 
             		
            following.push(FOLLOW_opt_eol_in_fact2667);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,23,FOLLOW_23_in_fact2675); 

             				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
             			
            following.push(FOLLOW_opt_eol_in_fact2678);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:956:34: (c= constraints )?
            int alt62=2;
            alt62 = dfa62.predict(input); 
            switch (alt62) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:956:41: c= constraints
                    {
                    following.push(FOLLOW_constraints_in_fact2684);
                    c=constraints();
                    following.pop();


                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (BaseDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_fact2703);
            opt_eol();
            following.pop();

            endLoc=(Token)input.LT(1);
            match(input,25,FOLLOW_25_in_fact2707); 
            following.push(FOLLOW_opt_eol_in_fact2709);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:970:1: constraints returns [List constraints] : opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol ;
    public List constraints() throws RecognitionException {   
        List constraints;

        		constraints = new ArrayList();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:974:17: ( opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:974:17: opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraints2741);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:975:17: ( constraint[constraints] | predicate[constraints] )
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
                        if ( LA63_17==56 ) {
                            alt63=2;
                        }
                        else if ( LA63_17==EOL||LA63_17==15||(LA63_17>=24 && LA63_17<=25)||(LA63_17>=63 && LA63_17<=72) ) {
                            alt63=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("975:17: ( constraint[constraints] | predicate[constraints] )", 63, 17, input);

                            throw nvae;
                        }
                    }
                    else if ( LA63_3==EOL||LA63_3==15 ) {
                        alt63=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("975:17: ( constraint[constraints] | predicate[constraints] )", 63, 3, input);

                        throw nvae;
                    }
                }
                else if ( LA63_2==EOL||LA63_2==15||(LA63_2>=24 && LA63_2<=25)||(LA63_2>=63 && LA63_2<=72) ) {
                    alt63=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("975:17: ( constraint[constraints] | predicate[constraints] )", 63, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("975:17: ( constraint[constraints] | predicate[constraints] )", 63, 0, input);

                throw nvae;
            }
            switch (alt63) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:975:18: constraint[constraints]
                    {
                    following.push(FOLLOW_constraint_in_constraints2746);
                    constraint(constraints);
                    following.pop();


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:975:42: predicate[constraints]
                    {
                    following.push(FOLLOW_predicate_in_constraints2749);
                    predicate(constraints);
                    following.pop();


                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:976:17: ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )*
            loop65:
            do {
                int alt65=2;
                alt65 = dfa65.predict(input); 
                switch (alt65) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:976:19: opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] )
            	    {
            	    following.push(FOLLOW_opt_eol_in_constraints2757);
            	    opt_eol();
            	    following.pop();

            	    match(input,24,FOLLOW_24_in_constraints2759); 
            	    following.push(FOLLOW_opt_eol_in_constraints2761);
            	    opt_eol();
            	    following.pop();

            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:976:39: ( constraint[constraints] | predicate[constraints] )
            	    int alt64=2;
            	    int LA64_0 = input.LA(1);
            	    if ( LA64_0==EOL||LA64_0==15 ) {
            	        alt64=1;
            	    }
            	    else if ( LA64_0==ID ) {
            	        int LA64_2 = input.LA(2);
            	        if ( LA64_2==33 ) {
            	            int LA64_3 = input.LA(3);
            	            if ( LA64_3==ID ) {
            	                int LA64_17 = input.LA(4);
            	                if ( LA64_17==56 ) {
            	                    alt64=2;
            	                }
            	                else if ( LA64_17==EOL||LA64_17==15||(LA64_17>=24 && LA64_17<=25)||(LA64_17>=63 && LA64_17<=72) ) {
            	                    alt64=1;
            	                }
            	                else {
            	                    NoViableAltException nvae =
            	                        new NoViableAltException("976:39: ( constraint[constraints] | predicate[constraints] )", 64, 17, input);

            	                    throw nvae;
            	                }
            	            }
            	            else if ( LA64_3==EOL||LA64_3==15 ) {
            	                alt64=1;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("976:39: ( constraint[constraints] | predicate[constraints] )", 64, 3, input);

            	                throw nvae;
            	            }
            	        }
            	        else if ( LA64_2==EOL||LA64_2==15||(LA64_2>=24 && LA64_2<=25)||(LA64_2>=63 && LA64_2<=72) ) {
            	            alt64=1;
            	        }
            	        else {
            	            NoViableAltException nvae =
            	                new NoViableAltException("976:39: ( constraint[constraints] | predicate[constraints] )", 64, 2, input);

            	            throw nvae;
            	        }
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("976:39: ( constraint[constraints] | predicate[constraints] )", 64, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt64) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:976:40: constraint[constraints]
            	            {
            	            following.push(FOLLOW_constraint_in_constraints2764);
            	            constraint(constraints);
            	            following.pop();


            	            }
            	            break;
            	        case 2 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:976:64: predicate[constraints]
            	            {
            	            following.push(FOLLOW_predicate_in_constraints2767);
            	            predicate(constraints);
            	            following.pop();


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop65;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_constraints2775);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:980:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )? opt_eol ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token bvc=null;
        Token con=null;
        String op = null;

        String lc = null;

        String rvc = null;



        		BaseDescr d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:984:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:984:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )? opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraint2794);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:985:17: (fb= ID opt_eol ':' opt_eol )?
            int alt66=2;
            alt66 = dfa66.predict(input); 
            switch (alt66) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:985:19: fb= ID opt_eol ':' opt_eol
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint2802); 
                    following.push(FOLLOW_opt_eol_in_constraint2804);
                    opt_eol();
                    following.pop();

                    match(input,33,FOLLOW_33_in_constraint2806); 
                    following.push(FOLLOW_opt_eol_in_constraint2808);
                    opt_eol();
                    following.pop();


                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint2818); 


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
            									
            			
            		
            following.push(FOLLOW_opt_eol_in_constraint2832);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1003:33: (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )?
            int alt70=2;
            int LA70_0 = input.LA(1);
            if ( (LA70_0>=63 && LA70_0<=72) ) {
                alt70=1;
            }
            else if ( LA70_0==EOL||LA70_0==15||(LA70_0>=24 && LA70_0<=25) ) {
                alt70=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1003:33: (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= (\'&\'|\'|\')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )?", 70, 0, input);

                throw nvae;
            }
            switch (alt70) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1003:41: op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )*
                    {
                    following.push(FOLLOW_operator_in_constraint2838);
                    op=operator();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_constraint2840);
                    opt_eol();
                    following.pop();

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1005:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    int alt67=4;
                    switch ( input.LA(1) ) {
                    case ID:
                        int LA67_1 = input.LA(2);
                        if ( LA67_1==19 ) {
                            alt67=2;
                        }
                        else if ( LA67_1==EOL||LA67_1==15||(LA67_1>=24 && LA67_1<=25)||(LA67_1>=54 && LA67_1<=55) ) {
                            alt67=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("1005:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 67, 1, input);

                            throw nvae;
                        }
                        break;
                    case INT:
                    case BOOL:
                    case STRING:
                    case FLOAT:
                    case 50:
                        alt67=3;
                        break;
                    case 23:
                        alt67=4;
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("1005:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 67, 0, input);

                        throw nvae;
                    }

                    switch (alt67) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1005:49: bvc= ID
                            {
                            bvc=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_constraint2858); 

                            							
                            														
                            							
                            							VariableRestrictionDescr vd = new VariableRestrictionDescr(op, bvc.getText());
                            							fc.addRestriction(vd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;
                        case 2 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1016:49: lc= enum_constraint
                            {
                            following.push(FOLLOW_enum_constraint_in_constraint2883);
                            lc=enum_constraint();
                            following.pop();

                             

                            							LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc, true);
                            							fc.addRestriction(lrd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;
                        case 3 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1025:49: lc= literal_constraint
                            {
                            following.push(FOLLOW_literal_constraint_in_constraint2915);
                            lc=literal_constraint();
                            following.pop();

                             
                            							
                            							LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc);
                            							fc.addRestriction(lrd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;
                        case 4 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1033:49: rvc= retval_constraint
                            {
                            following.push(FOLLOW_retval_constraint_in_constraint2935);
                            rvc=retval_constraint();
                            following.pop();

                             
                            							
                            							

                            							ReturnValueRestrictionDescr rvd = new ReturnValueRestrictionDescr(op, rvc);							
                            							fc.addRestriction(rvd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;

                    }

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1044:41: (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )*
                    loop69:
                    do {
                        int alt69=2;
                        int LA69_0 = input.LA(1);
                        if ( (LA69_0>=54 && LA69_0<=55) ) {
                            alt69=1;
                        }


                        switch (alt69) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1045:49: con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    	    {
                    	    con=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=54 && input.LA(1)<=55) ) {
                    	        input.consume();
                    	        errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint2970);    throw mse;
                    	    }


                    	    							if (con.getText().equals("&") ) {								
                    	    								fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	    							} else {
                    	    								fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	    							}							
                    	    						
                    	    following.push(FOLLOW_operator_in_constraint2992);
                    	    op=operator();
                    	    following.pop();

                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1055:49: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    	    int alt68=4;
                    	    switch ( input.LA(1) ) {
                    	    case ID:
                    	        int LA68_1 = input.LA(2);
                    	        if ( LA68_1==19 ) {
                    	            alt68=2;
                    	        }
                    	        else if ( LA68_1==EOL||LA68_1==15||(LA68_1>=24 && LA68_1<=25)||(LA68_1>=54 && LA68_1<=55) ) {
                    	            alt68=1;
                    	        }
                    	        else {
                    	            NoViableAltException nvae =
                    	                new NoViableAltException("1055:49: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 68, 1, input);

                    	            throw nvae;
                    	        }
                    	        break;
                    	    case INT:
                    	    case BOOL:
                    	    case STRING:
                    	    case FLOAT:
                    	    case 50:
                    	        alt68=3;
                    	        break;
                    	    case 23:
                    	        alt68=4;
                    	        break;
                    	    default:
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("1055:49: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 68, 0, input);

                    	        throw nvae;
                    	    }

                    	    switch (alt68) {
                    	        case 1 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1055:57: bvc= ID
                    	            {
                    	            bvc=(Token)input.LT(1);
                    	            match(input,ID,FOLLOW_ID_in_constraint3004); 

                    	            								VariableRestrictionDescr vd = new VariableRestrictionDescr(op, bvc.getText());
                    	            								fc.addRestriction(vd);
                    	            							

                    	            }
                    	            break;
                    	        case 2 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1061:57: lc= enum_constraint
                    	            {
                    	            following.push(FOLLOW_enum_constraint_in_constraint3032);
                    	            lc=enum_constraint();
                    	            following.pop();

                    	             
                    	            								LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc, true);
                    	            								fc.addRestriction(lrd);
                    	            								
                    	            							

                    	            }
                    	            break;
                    	        case 3 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1068:57: lc= literal_constraint
                    	            {
                    	            following.push(FOLLOW_literal_constraint_in_constraint3067);
                    	            lc=literal_constraint();
                    	            following.pop();

                    	             
                    	            								LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc);
                    	            								fc.addRestriction(lrd);
                    	            								
                    	            							

                    	            }
                    	            break;
                    	        case 4 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1074:57: rvc= retval_constraint
                    	            {
                    	            following.push(FOLLOW_retval_constraint_in_constraint3089);
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
                    	    break loop69;
                        }
                    } while (true);


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_constraint3145);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1087:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) ;
    public String literal_constraint() throws RecognitionException {   
        String text;
        Token t=null;


        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1091:17: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1091:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1091:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            int alt71=5;
            switch ( input.LA(1) ) {
            case STRING:
                alt71=1;
                break;
            case INT:
                alt71=2;
                break;
            case FLOAT:
                alt71=3;
                break;
            case BOOL:
                alt71=4;
                break;
            case 50:
                alt71=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1091:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= \'null\' )", 71, 0, input);

                throw nvae;
            }

            switch (alt71) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1091:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint3172); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1092:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint3183); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1093:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint3196); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1094:25: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint3207); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1095:25: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,50,FOLLOW_50_in_literal_constraint3219); 
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1099:1: enum_constraint returns [String text] : (cls= ID '.' en= ID ) ;
    public String enum_constraint() throws RecognitionException {   
        String text;
        Token cls=null;
        Token en=null;


        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1103:17: ( (cls= ID '.' en= ID ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1103:17: (cls= ID '.' en= ID )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1103:17: (cls= ID '.' en= ID )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1103:18: cls= ID '.' en= ID
            {
            cls=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint3250); 
            match(input,19,FOLLOW_19_in_enum_constraint3252); 
            en=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint3256); 

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1106:1: retval_constraint returns [String text] : '(' c= paren_chunk ')' ;
    public String retval_constraint() throws RecognitionException {   
        String text;
        String c = null;



        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1111:17: ( '(' c= paren_chunk ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1111:17: '(' c= paren_chunk ')'
            {
            match(input,23,FOLLOW_23_in_retval_constraint3285); 
            following.push(FOLLOW_paren_chunk_in_retval_constraint3290);
            c=paren_chunk();
            following.pop();

            match(input,25,FOLLOW_25_in_retval_constraint3293); 
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1114:1: predicate[List constraints] : decl= ID ':' field= ID '->' '(' text= paren_chunk ')' ;
    public void predicate(List constraints) throws RecognitionException {   
        Token decl=null;
        Token field=null;
        String text = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1116:17: (decl= ID ':' field= ID '->' '(' text= paren_chunk ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1116:17: decl= ID ':' field= ID '->' '(' text= paren_chunk ')'
            {
            decl=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate3311); 
            match(input,33,FOLLOW_33_in_predicate3313); 
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate3317); 
            match(input,56,FOLLOW_56_in_predicate3319); 
            match(input,23,FOLLOW_23_in_predicate3321); 
            following.push(FOLLOW_paren_chunk_in_predicate3325);
            text=paren_chunk();
            following.pop();

            match(input,25,FOLLOW_25_in_predicate3327); 

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1123:1: paren_chunk returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* ;
    public String paren_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1129:18: ( ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1129:18: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1129:18: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            loop72:
            do {
                int alt72=3;
                switch ( input.LA(1) ) {
                case EOL:
                    alt72=3;
                    break;
                case 25:
                    alt72=3;
                    break;
                case 23:
                    alt72=1;
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
                case 72:
                    alt72=2;
                    break;

                }

                switch (alt72) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1130:25: '(' c= paren_chunk ')'
            	    {
            	    match(input,23,FOLLOW_23_in_paren_chunk3373); 
            	    following.push(FOLLOW_paren_chunk_in_paren_chunk3377);
            	    c=paren_chunk();
            	    following.pop();

            	    match(input,25,FOLLOW_25_in_paren_chunk3379); 

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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1141:19: any= .
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
    // $ANTLR end paren_chunk


    // $ANTLR start paren_chunk2
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1153:1: paren_chunk2 returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )* ;
    public String paren_chunk2() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1159:18: ( ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1159:18: ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1159:18: ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )*
            loop73:
            do {
                int alt73=3;
                switch ( input.LA(1) ) {
                case 25:
                    alt73=3;
                    break;
                case 23:
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
                case 72:
                    alt73=2;
                    break;

                }

                switch (alt73) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1160:25: '(' c= paren_chunk2 ')'
            	    {
            	    match(input,23,FOLLOW_23_in_paren_chunk23450); 
            	    following.push(FOLLOW_paren_chunk2_in_paren_chunk23454);
            	    c=paren_chunk2();
            	    following.pop();

            	    match(input,25,FOLLOW_25_in_paren_chunk23456); 

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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1171:19: any= .
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
    // $ANTLR end paren_chunk2


    // $ANTLR start curly_chunk
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1182:1: curly_chunk returns [String text] : ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* ;
    public String curly_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1188:17: ( ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1188:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1188:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            loop74:
            do {
                int alt74=3;
                switch ( input.LA(1) ) {
                case 27:
                    alt74=3;
                    break;
                case 26:
                    alt74=1;
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
                case 72:
                    alt74=2;
                    break;

                }

                switch (alt74) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1189:25: '{' c= curly_chunk '}'
            	    {
            	    match(input,26,FOLLOW_26_in_curly_chunk3525); 
            	    following.push(FOLLOW_curly_chunk_in_curly_chunk3529);
            	    c=curly_chunk();
            	    following.pop();

            	    match(input,27,FOLLOW_27_in_curly_chunk3531); 

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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1201:19: any= .
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
        return text;
    }
    // $ANTLR end curly_chunk


    // $ANTLR start lhs_or
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1213:1: lhs_or returns [BaseDescr d] : left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )* ;
    public BaseDescr lhs_or() throws RecognitionException {   
        BaseDescr d;
        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1218:17: (left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1218:17: left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )*
            {
             OrDescr or = null; 
            following.push(FOLLOW_lhs_and_in_lhs_or3589);
            left=lhs_and();
            following.pop();

            d = left; 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1220:17: ( ('or'|'||') opt_eol right= lhs_and )*
            loop75:
            do {
                int alt75=2;
                int LA75_0 = input.LA(1);
                if ( (LA75_0>=52 && LA75_0<=53) ) {
                    alt75=1;
                }


                switch (alt75) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1220:19: ('or'|'||') opt_eol right= lhs_and
            	    {
            	    if ( (input.LA(1)>=52 && input.LA(1)<=53) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or3598);    throw mse;
            	    }

            	    following.push(FOLLOW_opt_eol_in_lhs_or3603);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_lhs_and_in_lhs_or3610);
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
    // $ANTLR end lhs_or


    // $ANTLR start lhs_and
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1234:1: lhs_and returns [BaseDescr d] : left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )* ;
    public BaseDescr lhs_and() throws RecognitionException {   
        BaseDescr d;
        BaseDescr left = null;

        BaseDescr right = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1239:17: (left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1239:17: left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )*
            {
             AndDescr and = null; 
            following.push(FOLLOW_lhs_unary_in_lhs_and3650);
            left=lhs_unary();
            following.pop();

             d = left; 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1241:17: ( ('and'|'&&') opt_eol right= lhs_unary )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);
                if ( (LA76_0>=57 && LA76_0<=58) ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1241:19: ('and'|'&&') opt_eol right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=57 && input.LA(1)<=58) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and3659);    throw mse;
            	    }

            	    following.push(FOLLOW_opt_eol_in_lhs_and3664);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_lhs_unary_in_lhs_and3671);
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
        return d;
    }
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1255:1: lhs_unary returns [BaseDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' opt_eol u= lhs opt_eol ')' ) ;
    public BaseDescr lhs_unary() throws RecognitionException {   
        BaseDescr d;
        BaseDescr u = null;

        FromDescr fm = null;

        AccumulateDescr ac = null;

        CollectDescr cs = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1259:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' opt_eol u= lhs opt_eol ')' ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1259:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' opt_eol u= lhs opt_eol ')' )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1259:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | '(' opt_eol u= lhs opt_eol ')' )
            int alt78=5;
            switch ( input.LA(1) ) {
            case 59:
                alt78=1;
                break;
            case 60:
                alt78=2;
                break;
            case 61:
                alt78=3;
                break;
            case ID:
                alt78=4;
                break;
            case 23:
                alt78=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1259:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )? | \'(\' opt_eol u= lhs opt_eol \')\' )", 78, 0, input);

                throw nvae;
            }

            switch (alt78) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1259:25: u= lhs_exist
                    {
                    following.push(FOLLOW_lhs_exist_in_lhs_unary3709);
                    u=lhs_exist();
                    following.pop();

                    d = u;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1260:25: u= lhs_not
                    {
                    following.push(FOLLOW_lhs_not_in_lhs_unary3719);
                    u=lhs_not();
                    following.pop();

                    d = u;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1261:25: u= lhs_eval
                    {
                    following.push(FOLLOW_lhs_eval_in_lhs_unary3729);
                    u=lhs_eval();
                    following.pop();

                    d = u;

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1262:25: u= lhs_column ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )?
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_unary3743);
                    u=lhs_column();
                    following.pop();

                    d=u;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1263:27: ( (fm= from_statement ) | (ac= accumulate_statement ) | (cs= collect_statement ) )?
                    int alt77=4;
                    alt77 = dfa77.predict(input); 
                    switch (alt77) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1263:28: (fm= from_statement )
                            {
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1263:28: (fm= from_statement )
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1263:29: fm= from_statement
                            {
                            following.push(FOLLOW_from_statement_in_lhs_unary3764);
                            fm=from_statement();
                            following.pop();

                            fm.setColumn((ColumnDescr) u); d=fm;

                            }


                            }
                            break;
                        case 2 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1264:28: (ac= accumulate_statement )
                            {
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1264:28: (ac= accumulate_statement )
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1264:29: ac= accumulate_statement
                            {
                            following.push(FOLLOW_accumulate_statement_in_lhs_unary3786);
                            ac=accumulate_statement();
                            following.pop();

                            ac.setResultColumn((ColumnDescr) u); d=ac;

                            }


                            }
                            break;
                        case 3 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1265:28: (cs= collect_statement )
                            {
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1265:28: (cs= collect_statement )
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1265:29: cs= collect_statement
                            {
                            following.push(FOLLOW_collect_statement_in_lhs_unary3807);
                            cs=collect_statement();
                            following.pop();

                            cs.setResultColumn((ColumnDescr) u); d=cs;

                            }


                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1266:25: '(' opt_eol u= lhs opt_eol ')'
                    {
                    match(input,23,FOLLOW_23_in_lhs_unary3819); 
                    following.push(FOLLOW_opt_eol_in_lhs_unary3821);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_lhs_in_lhs_unary3825);
                    u=lhs();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_lhs_unary3827);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_lhs_unary3829); 
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1270:1: lhs_exist returns [BaseDescr d] : loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public BaseDescr lhs_exist() throws RecognitionException {   
        BaseDescr d;
        Token loc=null;
        BaseDescr column = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1274:17: (loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1274:17: loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,59,FOLLOW_59_in_lhs_exist3860); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1274:30: ( '(' column= lhs_column ')' | column= lhs_column )
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
                    new NoViableAltException("1274:30: ( \'(\' column= lhs_column \')\' | column= lhs_column )", 79, 0, input);

                throw nvae;
            }
            switch (alt79) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1274:31: '(' column= lhs_column ')'
                    {
                    match(input,23,FOLLOW_23_in_lhs_exist3863); 
                    following.push(FOLLOW_lhs_column_in_lhs_exist3867);
                    column=lhs_column();
                    following.pop();

                    match(input,25,FOLLOW_25_in_lhs_exist3869); 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1274:59: column= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_exist3875);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1281:1: lhs_not returns [NotDescr d] : loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d;
        Token loc=null;
        BaseDescr column = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1285:17: (loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1285:17: loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,60,FOLLOW_60_in_lhs_not3905); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1285:27: ( '(' column= lhs_column ')' | column= lhs_column )
            int alt80=2;
            int LA80_0 = input.LA(1);
            if ( LA80_0==23 ) {
                alt80=1;
            }
            else if ( LA80_0==ID ) {
                alt80=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1285:27: ( \'(\' column= lhs_column \')\' | column= lhs_column )", 80, 0, input);

                throw nvae;
            }
            switch (alt80) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1285:28: '(' column= lhs_column ')'
                    {
                    match(input,23,FOLLOW_23_in_lhs_not3908); 
                    following.push(FOLLOW_lhs_column_in_lhs_not3912);
                    column=lhs_column();
                    following.pop();

                    match(input,25,FOLLOW_25_in_lhs_not3915); 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1285:57: column= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_not3921);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1292:1: lhs_eval returns [BaseDescr d] : 'eval' loc= '(' c= paren_chunk2 ')' ;
    public BaseDescr lhs_eval() throws RecognitionException {   
        BaseDescr d;
        Token loc=null;
        String c = null;



        		d = null;
        		String text = "";
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1297:17: ( 'eval' loc= '(' c= paren_chunk2 ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1297:17: 'eval' loc= '(' c= paren_chunk2 ')'
            {
            match(input,61,FOLLOW_61_in_lhs_eval3947); 
            loc=(Token)input.LT(1);
            match(input,23,FOLLOW_23_in_lhs_eval3951); 
            following.push(FOLLOW_paren_chunk2_in_lhs_eval3959);
            c=paren_chunk2();
            following.pop();

            match(input,25,FOLLOW_25_in_lhs_eval3963); 
             
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1306:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ( '[' ']' )* ;
    public String dotted_name() throws RecognitionException {   
        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1311:17: (id= ID ( '.' id= ID )* ( '[' ']' )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1311:17: id= ID ( '.' id= ID )* ( '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name3995); 
             name=id.getText(); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1311:46: ( '.' id= ID )*
            loop81:
            do {
                int alt81=2;
                int LA81_0 = input.LA(1);
                if ( LA81_0==19 ) {
                    alt81=1;
                }


                switch (alt81) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1311:48: '.' id= ID
            	    {
            	    match(input,19,FOLLOW_19_in_dotted_name4001); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name4005); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop81;
                }
            } while (true);

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1311:99: ( '[' ']' )*
            loop82:
            do {
                int alt82=2;
                int LA82_0 = input.LA(1);
                if ( LA82_0==43 ) {
                    alt82=1;
                }


                switch (alt82) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1311:101: '[' ']'
            	    {
            	    match(input,43,FOLLOW_43_in_dotted_name4014); 
            	    match(input,44,FOLLOW_44_in_dotted_name4016); 
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
    // $ANTLR end dotted_name


    // $ANTLR start argument_name
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1314:1: argument_name returns [String name] : id= ID ( '[' ']' )* ;
    public String argument_name() throws RecognitionException {   
        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1319:17: (id= ID ( '[' ']' )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1319:17: id= ID ( '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument_name4046); 
             name=id.getText(); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1319:46: ( '[' ']' )*
            loop83:
            do {
                int alt83=2;
                int LA83_0 = input.LA(1);
                if ( LA83_0==43 ) {
                    alt83=1;
                }


                switch (alt83) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1319:48: '[' ']'
            	    {
            	    match(input,43,FOLLOW_43_in_argument_name4052); 
            	    match(input,44,FOLLOW_44_in_argument_name4054); 
            	     name = name + "[]";

            	    }
            	    break;

            	default :
            	    break loop83;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1323:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word;
        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1327:17: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt84=11;
            switch ( input.LA(1) ) {
            case ID:
                alt84=1;
                break;
            case 17:
                alt84=2;
                break;
            case 62:
                alt84=3;
                break;
            case 31:
                alt84=4;
                break;
            case 28:
                alt84=5;
                break;
            case 36:
                alt84=6;
                break;
            case 37:
                alt84=7;
                break;
            case 32:
                alt84=8;
                break;
            case 34:
                alt84=9;
                break;
            case 29:
                alt84=10;
                break;
            case STRING:
                alt84=11;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1323:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'query\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 84, 0, input);

                throw nvae;
            }

            switch (alt84) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1327:17: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word4082); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1328:17: 'import'
                    {
                    match(input,17,FOLLOW_17_in_word4094); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1329:17: 'use'
                    {
                    match(input,62,FOLLOW_62_in_word4103); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1330:17: 'rule'
                    {
                    match(input,31,FOLLOW_31_in_word4115); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1331:17: 'query'
                    {
                    match(input,28,FOLLOW_28_in_word4126); 
                     word="query"; 

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1332:17: 'salience'
                    {
                    match(input,36,FOLLOW_36_in_word4136); 
                     word="salience"; 

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1333:17: 'no-loop'
                    {
                    match(input,37,FOLLOW_37_in_word4144); 
                     word="no-loop"; 

                    }
                    break;
                case 8 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1334:17: 'when'
                    {
                    match(input,32,FOLLOW_32_in_word4152); 
                     word="when"; 

                    }
                    break;
                case 9 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1335:17: 'then'
                    {
                    match(input,34,FOLLOW_34_in_word4163); 
                     word="then"; 

                    }
                    break;
                case 10 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1336:17: 'end'
                    {
                    match(input,29,FOLLOW_29_in_word4174); 
                     word="end"; 

                    }
                    break;
                case 11 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1337:17: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word4188); 
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1340:1: operator returns [String str] : ( '==' | '=' | '>' | '>=' | '<' | '<=' | '!=' | 'contains' | 'matches' | 'excludes' );
    public String operator() throws RecognitionException {   
        String str;

        		str = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1346:17: ( '==' | '=' | '>' | '>=' | '<' | '<=' | '!=' | 'contains' | 'matches' | 'excludes' )
            int alt85=10;
            switch ( input.LA(1) ) {
            case 63:
                alt85=1;
                break;
            case 64:
                alt85=2;
                break;
            case 65:
                alt85=3;
                break;
            case 66:
                alt85=4;
                break;
            case 67:
                alt85=5;
                break;
            case 68:
                alt85=6;
                break;
            case 69:
                alt85=7;
                break;
            case 70:
                alt85=8;
                break;
            case 71:
                alt85=9;
                break;
            case 72:
                alt85=10;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1340:1: operator returns [String str] : ( \'==\' | \'=\' | \'>\' | \'>=\' | \'<\' | \'<=\' | \'!=\' | \'contains\' | \'matches\' | \'excludes\' );", 85, 0, input);

                throw nvae;
            }

            switch (alt85) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1346:17: '=='
                    {
                    match(input,63,FOLLOW_63_in_operator4217); 
                    str= "==";

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1347:18: '='
                    {
                    match(input,64,FOLLOW_64_in_operator4224); 
                    str="==";

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1348:18: '>'
                    {
                    match(input,65,FOLLOW_65_in_operator4231); 
                    str=">";

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1349:18: '>='
                    {
                    match(input,66,FOLLOW_66_in_operator4238); 
                    str=">=";

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1350:18: '<'
                    {
                    match(input,67,FOLLOW_67_in_operator4247); 
                    str="<";

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1351:18: '<='
                    {
                    match(input,68,FOLLOW_68_in_operator4254); 
                    str="<=";

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1352:18: '!='
                    {
                    match(input,69,FOLLOW_69_in_operator4261); 
                    str="!=";

                    }
                    break;
                case 8 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1353:18: 'contains'
                    {
                    match(input,70,FOLLOW_70_in_operator4268); 
                    str="contains";

                    }
                    break;
                case 9 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1354:18: 'matches'
                    {
                    match(input,71,FOLLOW_71_in_operator4275); 
                    str="matches";

                    }
                    break;
                case 10 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1355:18: 'excludes'
                    {
                    match(input,72,FOLLOW_72_in_operator4282); 
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


    protected DFA2 dfa2 = new DFA2();protected DFA4 dfa4 = new DFA4();protected DFA13 dfa13 = new DFA13();protected DFA14 dfa14 = new DFA14();protected DFA15 dfa15 = new DFA15();protected DFA48 dfa48 = new DFA48();protected DFA50 dfa50 = new DFA50();protected DFA51 dfa51 = new DFA51();protected DFA62 dfa62 = new DFA62();protected DFA65 dfa65 = new DFA65();protected DFA66 dfa66 = new DFA66();protected DFA77 dfa77 = new DFA77();
    class DFA2 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=5;}};
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s5 = new DFA.State() {{alt=3;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 28:
                    return s4;

                case EOL:
                case 15:
                    return s2;

                case 31:
                    return s3;

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
                if ( LA4_33==43 ) {return s21;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 33, input);

                throw nvae;
            }
        };
        DFA.State s21 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_21 = input.LA(1);
                if ( LA4_21==44 ) {return s33;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 21, input);

                throw nvae;
            }
        };
        DFA.State s32 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 43:
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

                case 43:
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
        DFA.State s93 = new DFA.State() {{alt=1;}};
        DFA.State s111 = new DFA.State() {{alt=1;}};
        DFA.State s120 = new DFA.State() {{alt=1;}};
        DFA.State s126 = new DFA.State() {{alt=1;}};
        DFA.State s128 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s126;

                case 26:
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
                case 72:
                    return s128;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 128, input);

                    throw nvae;        }
            }
        };
        DFA.State s127 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s126;

                case 26:
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
                case 72:
                    return s128;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 127, input);

                    throw nvae;        }
            }
        };
        DFA.State s121 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s126;

                case 26:
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
                case 72:
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
                case 72:
                    return s122;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 122, input);

                    throw nvae;        }
            }
        };
        DFA.State s112 = new DFA.State() {
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
                case 72:
                    return s122;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 112, input);

                    throw nvae;        }
            }
        };
        DFA.State s113 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s111;

                case 26:
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
                case 72:
                    return s113;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 113, input);

                    throw nvae;        }
            }
        };
        DFA.State s94 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s111;

                case 26:
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
                case 72:
                    return s113;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 94, input);

                    throw nvae;        }
            }
        };
        DFA.State s95 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 27:
                    return s93;

                case 26:
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
                case 72:
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
                case 27:
                    return s93;

                case 26:
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
                case 72:
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
                case 72:
                    return s75;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 75, input);

                    throw nvae;        }
            }
        };
        DFA.State s57 = new DFA.State() {
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
                case 72:
                    return s75;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 57, input);

                    throw nvae;        }
            }
        };
        DFA.State s56 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_56 = input.LA(1);
                if ( LA4_56==26 ) {return s57;}
                if ( LA4_56==EOL||LA4_56==15 ) {return s56;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 56, input);

                throw nvae;
            }
        };
        DFA.State s39 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_39 = input.LA(1);
                if ( LA4_39==EOL||LA4_39==15 ) {return s56;}
                if ( LA4_39==26 ) {return s57;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 39, input);

                throw nvae;
            }
        };
        DFA.State s104 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s39;

                case 24:
                    return s52;

                case EOL:
                case 15:
                    return s104;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 104, input);

                    throw nvae;        }
            }
        };
        DFA.State s88 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 43:
                    return s67;

                case EOL:
                case 15:
                    return s104;

                case 25:
                    return s39;

                case 24:
                    return s52;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 88, input);

                    throw nvae;        }
            }
        };
        DFA.State s84 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s39;

                case 24:
                    return s52;

                case EOL:
                case 15:
                    return s84;

                case ID:
                    return s88;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 84, input);

                    throw nvae;        }
            }
        };
        DFA.State s66 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 43:
                    return s50;

                case EOL:
                case 15:
                    return s84;

                case 25:
                    return s39;

                case 24:
                    return s52;

                case 19:
                    return s20;

                case ID:
                    return s88;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 66, input);

                    throw nvae;        }
            }
        };
        DFA.State s65 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_65 = input.LA(1);
                if ( LA4_65==ID ) {return s66;}
                if ( LA4_65==EOL||LA4_65==15 ) {return s65;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 65, input);

                throw nvae;
            }
        };
        DFA.State s52 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_52 = input.LA(1);
                if ( LA4_52==EOL||LA4_52==15 ) {return s65;}
                if ( LA4_52==ID ) {return s66;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 52, input);

                throw nvae;
            }
        };
        DFA.State s68 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 24:
                    return s52;

                case 25:
                    return s39;

                case EOL:
                case 15:
                    return s68;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 68, input);

                    throw nvae;        }
            }
        };
        DFA.State s89 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s68;

                case 24:
                    return s52;

                case 25:
                    return s39;

                case 43:
                    return s67;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 89, input);

                    throw nvae;        }
            }
        };
        DFA.State s67 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_67 = input.LA(1);
                if ( LA4_67==44 ) {return s89;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 67, input);

                throw nvae;
            }
        };
        DFA.State s55 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 43:
                    return s67;

                case EOL:
                case 15:
                    return s68;

                case 24:
                    return s52;

                case 25:
                    return s39;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 55, input);

                    throw nvae;        }
            }
        };
        DFA.State s51 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s55;

                case EOL:
                case 15:
                    return s51;

                case 24:
                    return s52;

                case 25:
                    return s39;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 51, input);

                    throw nvae;        }
            }
        };
        DFA.State s60 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s51;

                case 24:
                    return s52;

                case 25:
                    return s39;

                case 43:
                    return s50;

                case ID:
                    return s55;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 60, input);

                    throw nvae;        }
            }
        };
        DFA.State s50 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_50 = input.LA(1);
                if ( LA4_50==44 ) {return s60;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 50, input);

                throw nvae;
            }
        };
        DFA.State s38 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 43:
                    return s50;

                case EOL:
                case 15:
                    return s51;

                case 24:
                    return s52;

                case 25:
                    return s39;

                case 19:
                    return s20;

                case ID:
                    return s55;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 38, input);

                    throw nvae;        }
            }
        };
        DFA.State s37 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s38;

                case EOL:
                case 15:
                    return s37;

                case 25:
                    return s39;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 37, input);

                    throw nvae;        }
            }
        };
        DFA.State s26 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s37;

                case ID:
                    return s38;

                case 25:
                    return s39;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 26, input);

                    throw nvae;        }
            }
        };
        DFA.State s40 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_40 = input.LA(1);
                if ( LA4_40==23 ) {return s26;}
                if ( LA4_40==EOL||LA4_40==15 ) {return s40;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 40, input);

                throw nvae;
            }
        };
        DFA.State s29 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_29 = input.LA(1);
                if ( LA4_29==EOL||LA4_29==15 ) {return s40;}
                if ( LA4_29==23 ) {return s26;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 29, input);

                throw nvae;
            }
        };
        DFA.State s25 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                    return s26;

                case EOL:
                case 15:
                    return s25;

                case ID:
                    return s29;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 25, input);

                    throw nvae;        }
            }
        };
        DFA.State s15 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s25;

                case 23:
                    return s26;

                case 19:
                    return s20;

                case 43:
                    return s21;

                case ID:
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
                case 43:
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

                case 24:
                case 25:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 5, input);

                    throw nvae;        }
            }
        };
        DFA.State s9 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s5;

                case ID:
                    return s3;

                case 43:
                    return s4;

                case 24:
                case 25:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 9, input);

                    throw nvae;        }
            }
        };
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA14_4 = input.LA(1);
                if ( LA14_4==44 ) {return s9;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 14, 4, input);

                throw nvae;
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                case 19:
                    return s3;

                case 43:
                    return s4;

                case EOL:
                case 15:
                    return s5;

                case 24:
                case 25:
                    return s2;

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

                case ID:
                    return s3;

                case 43:
                    return s4;

                case 24:
                case 25:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 9, input);

                    throw nvae;        }
            }
        };
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA15_4 = input.LA(1);
                if ( LA15_4==44 ) {return s9;}

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

                case 43:
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
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s6 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                    return s2;

                case EOL:
                case 15:
                    return s4;

                case 33:
                    return s6;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 4, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 19:
                case 23:
                case 43:
                    return s2;

                case EOL:
                case 15:
                    return s4;

                case 33:
                    return s6;

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

    }class DFA50 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s8 = new DFA.State() {{alt=1;}};
        DFA.State s33 = new DFA.State() {{alt=2;}};
        DFA.State s55 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                case 33:
                    return s8;

                case EOL:
                case 15:
                    return s55;

                case 24:
                case 25:
                    return s33;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 55, input);

                    throw nvae;        }
            }
        };
        DFA.State s31 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s55;

                case 24:
                case 25:
                    return s33;

                case 19:
                case 23:
                case 33:
                case 43:
                    return s8;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 31, input);

                    throw nvae;        }
            }
        };
        DFA.State s27 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                case 59:
                case 60:
                case 61:
                    return s8;

                case ID:
                    return s31;

                case EOL:
                case 15:
                    return s27;

                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case 25:
                case 26:
                case 43:
                case 50:
                    return s33;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 27, input);

                    throw nvae;        }
            }
        };
        DFA.State s7 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s27;

                case 23:
                case 59:
                case 60:
                case 61:
                    return s8;

                case ID:
                    return s31;

                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case 25:
                case 26:
                case 43:
                case 50:
                    return s33;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 7, input);

                    throw nvae;        }
            }
        };
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                    return s7;

                case EOL:
                case 15:
                    return s6;

                case ID:
                case 25:
                case 29:
                case 34:
                case 59:
                case 60:
                case 61:
                    return s8;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 6, input);

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
                case 43:
                case 52:
                case 53:
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                    return s8;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 5, input);

                    throw nvae;        }
            }
        };
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA50_2 = input.LA(1);
                if ( LA50_2==ID ) {return s5;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 50, 2, input);

                throw nvae;
            }
        };
        DFA.State s3 = new DFA.State() {{alt=3;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA50_1 = input.LA(1);
                if ( LA50_1==19 ) {return s2;}
                if ( LA50_1==EOL||LA50_1==15||LA50_1==23 ) {return s3;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 50, 1, input);

                throw nvae;
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA50_0 = input.LA(1);
                if ( LA50_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }
        };

    }class DFA51 extends DFA {
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
                        new NoViableAltException("", 51, 1, input);

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
                        new NoViableAltException("", 51, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA62 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s2;

                case EOL:
                case 15:
                    return s1;

                case 25:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 62, 1, input);

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
                        new NoViableAltException("", 62, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA65 extends DFA {
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
                        new NoViableAltException("", 65, 1, input);

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
                        new NoViableAltException("", 65, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA66 extends DFA {
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
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                    return s3;

                case EOL:
                case 15:
                    return s2;

                case 33:
                    return s15;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 66, 2, input);

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
                case 63:
                case 64:
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70:
                case 71:
                case 72:
                    return s3;

                case 33:
                    return s15;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 66, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA66_0 = input.LA(1);
                if ( LA66_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 66, 0, input);

                throw nvae;
            }
        };

    }class DFA77 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s15 = new DFA.State() {{alt=1;}};
        DFA.State s14 = new DFA.State() {{alt=2;}};
        DFA.State s16 = new DFA.State() {{alt=3;}};
        DFA.State s13 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s15;

                case EOL:
                case 15:
                    return s13;

                case 45:
                    return s14;

                case 49:
                    return s16;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 77, 13, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s13;

                case 45:
                    return s14;

                case ID:
                    return s15;

                case 49:
                    return s16;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 77, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s2 = new DFA.State() {{alt=4;}};
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA77_0 = input.LA(1);
                if ( LA77_0==42 ) {return s1;}
                if ( (LA77_0>=EOL && LA77_0<=ID)||LA77_0==15||LA77_0==23||LA77_0==25||LA77_0==29||LA77_0==34||(LA77_0>=52 && LA77_0<=53)||(LA77_0>=57 && LA77_0<=61) ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 77, 0, input);

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
    public static final BitSet FOLLOW_26_in_function516 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_curly_chunk_in_function523 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_function532 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function540 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_query564 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_query570 = new BitSet(new long[]{0x40000035B0020120L});
    public static final BitSet FOLLOW_word_in_query574 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_query576 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
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
    public static final BitSet FOLLOW_31_in_rule766 = new BitSet(new long[]{0x40000035B0020120L});
    public static final BitSet FOLLOW_word_in_rule770 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule772 = new BitSet(new long[]{0x0000000A00008012L});
    public static final BitSet FOLLOW_rule_attributes_in_rule783 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule793 = new BitSet(new long[]{0x0000000120008012L});
    public static final BitSet FOLLOW_32_in_rule802 = new BitSet(new long[]{0x0000000200008012L});
    public static final BitSet FOLLOW_33_in_rule804 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule807 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_expander_lhs_block_in_rule825 = new BitSet(new long[]{0x0000000020008012L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule834 = new BitSet(new long[]{0x0000000020008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule857 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_rule861 = new BitSet(new long[]{0x0000000200008012L});
    public static final BitSet FOLLOW_33_in_rule863 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule867 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000000001FFL});
    public static final BitSet FOLLOW_EOL_in_rule894 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000000001FFL});
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
    public static final BitSet FOLLOW_opt_eol_in_normal_lhs_block1473 = new BitSet(new long[]{0x3800000000808032L});
    public static final BitSet FOLLOW_opt_eol_in_normal_lhs_block1485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expander_lhs_block1526 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block1530 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block1545 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_lhs_or_in_lhs1587 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_from_statement1652 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_statement1654 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_from_source_in_from_statement1658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1702 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_from_source1704 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_from_source1708 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_from_source1712 = new BitSet(new long[]{0x00040800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_from_source1716 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_from_source1718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1747 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_from_source1749 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_from_source1753 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1755 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_from_source1758 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1760 = new BitSet(new long[]{0x00040800040003E2L});
    public static final BitSet FOLLOW_argument_list_in_from_source1764 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1766 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_from_source1768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1790 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1792 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_from_source1794 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1796 = new BitSet(new long[]{0x00040800040003E2L});
    public static final BitSet FOLLOW_argument_list_in_from_source1800 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1802 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_from_source1804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_accumulate_statement1853 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1855 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_accumulate_statement1857 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1859 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_accumulate_statement1869 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1871 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_accumulate_statement1875 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1877 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_accumulate_statement1879 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1881 = new BitSet(new long[]{0x0000400000000000L});
    public static final BitSet FOLLOW_46_in_accumulate_statement1889 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1891 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_accumulate_statement1893 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_paren_chunk2_in_accumulate_statement1897 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_accumulate_statement1899 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1901 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_accumulate_statement1903 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1905 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_accumulate_statement1913 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1915 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_accumulate_statement1917 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_paren_chunk2_in_accumulate_statement1921 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_accumulate_statement1923 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1925 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_accumulate_statement1927 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1929 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_48_in_accumulate_statement1937 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1939 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_accumulate_statement1941 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_paren_chunk2_in_accumulate_statement1945 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_accumulate_statement1947 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_accumulate_statement1949 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_accumulate_statement1951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_collect_statement1994 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_collect_statement1996 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_collect_statement1998 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_collect_statement2000 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_collect_statement2010 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_collect_statement2012 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_collect_statement2016 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_collect_statement2018 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_collect_statement2020 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_argument_value_in_argument_list2053 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_argument_list2069 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_argument_list2071 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_argument_list2073 = new BitSet(new long[]{0x00040800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_argument_list2077 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_STRING_in_argument_value2117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_argument_value2128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_argument_value2141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_argument_value2152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument_value2164 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_argument_value2175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_argument_value2186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inline_map_in_argument_value2205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inline_array_in_argument_value2221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_inline_map2261 = new BitSet(new long[]{0x00040800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2279 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_inline_map2281 = new BitSet(new long[]{0x00040800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2285 = new BitSet(new long[]{0x0000000009000010L});
    public static final BitSet FOLLOW_EOL_in_inline_map2328 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_inline_map2332 = new BitSet(new long[]{0x00040800040003F0L});
    public static final BitSet FOLLOW_EOL_in_inline_map2335 = new BitSet(new long[]{0x00040800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2341 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_inline_map2343 = new BitSet(new long[]{0x00040800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2347 = new BitSet(new long[]{0x0000000009000010L});
    public static final BitSet FOLLOW_27_in_inline_map2383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_inline_array2427 = new BitSet(new long[]{0x00040800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_array2431 = new BitSet(new long[]{0x0000100001000010L});
    public static final BitSet FOLLOW_EOL_in_inline_array2449 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_inline_array2452 = new BitSet(new long[]{0x00040800040003F0L});
    public static final BitSet FOLLOW_EOL_in_inline_array2454 = new BitSet(new long[]{0x00040800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_array2459 = new BitSet(new long[]{0x0000100001000010L});
    public static final BitSet FOLLOW_44_in_inline_array2472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding2515 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding2525 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_fact_binding2527 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding2529 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding2533 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_fact_expression2565 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2567 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2571 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2573 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_fact_expression2575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression2586 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2588 = new BitSet(new long[]{0x0030000000000002L});
    public static final BitSet FOLLOW_set_in_fact_expression2601 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2606 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_expression2620 = new BitSet(new long[]{0x0030000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_fact2659 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2667 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_fact2675 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2678 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_constraints_in_fact2684 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2703 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_fact2707 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2741 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_constraint_in_constraints2746 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_predicate_in_constraints2749 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2757 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_constraints2759 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2761 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_constraint_in_constraints2764 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_predicate_in_constraints2767 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2775 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2794 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint2802 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2804 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_constraint2806 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2808 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint2818 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2832 = new BitSet(new long[]{0x8000000000008012L,0x00000000000001FFL});
    public static final BitSet FOLLOW_operator_in_constraint2838 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2840 = new BitSet(new long[]{0x00040000008003E0L});
    public static final BitSet FOLLOW_ID_in_constraint2858 = new BitSet(new long[]{0x00C0000000008012L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint2883 = new BitSet(new long[]{0x00C0000000008012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint2915 = new BitSet(new long[]{0x00C0000000008012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint2935 = new BitSet(new long[]{0x00C0000000008012L});
    public static final BitSet FOLLOW_set_in_constraint2970 = new BitSet(new long[]{0x8000000000000000L,0x00000000000001FFL});
    public static final BitSet FOLLOW_operator_in_constraint2992 = new BitSet(new long[]{0x00040000008003E0L});
    public static final BitSet FOLLOW_ID_in_constraint3004 = new BitSet(new long[]{0x00C0000000008012L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint3032 = new BitSet(new long[]{0x00C0000000008012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint3067 = new BitSet(new long[]{0x00C0000000008012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint3089 = new BitSet(new long[]{0x00C0000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint3145 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint3172 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint3183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint3196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint3207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_50_in_literal_constraint3219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint3250 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_enum_constraint3252 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_enum_constraint3256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_retval_constraint3285 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint3290 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_retval_constraint3293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_predicate3311 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_predicate3313 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_predicate3317 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_predicate3319 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_predicate3321 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_paren_chunk_in_predicate3325 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_predicate3327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_paren_chunk3373 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk3377 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_paren_chunk3379 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_23_in_paren_chunk23450 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_paren_chunk2_in_paren_chunk23454 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_paren_chunk23456 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_26_in_curly_chunk3525 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk3529 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_curly_chunk3531 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3589 = new BitSet(new long[]{0x0030000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or3598 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_or3603 = new BitSet(new long[]{0x3800000000800020L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3610 = new BitSet(new long[]{0x0030000000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3650 = new BitSet(new long[]{0x0600000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and3659 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_and3664 = new BitSet(new long[]{0x3800000000800020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3671 = new BitSet(new long[]{0x0600000000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary3709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary3719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary3729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary3743 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary3764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_accumulate_statement_in_lhs_unary3786 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_collect_statement_in_lhs_unary3807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_lhs_unary3819 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_unary3821 = new BitSet(new long[]{0x3800000000800020L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary3825 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_unary3827 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_unary3829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_lhs_exist3860 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_23_in_lhs_exist3863 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3867 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_exist3869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_lhs_not3905 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_23_in_lhs_not3908 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3912 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_not3915 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_lhs_eval3947 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_lhs_eval3951 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x00000000000001FFL});
    public static final BitSet FOLLOW_paren_chunk2_in_lhs_eval3959 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_eval3963 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name3995 = new BitSet(new long[]{0x0000080000080002L});
    public static final BitSet FOLLOW_19_in_dotted_name4001 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_dotted_name4005 = new BitSet(new long[]{0x0000080000080002L});
    public static final BitSet FOLLOW_43_in_dotted_name4014 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_dotted_name4016 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_ID_in_argument_name4046 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_43_in_argument_name4052 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_argument_name4054 = new BitSet(new long[]{0x0000080000000002L});
    public static final BitSet FOLLOW_ID_in_word4082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_word4094 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_word4103 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_word4115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_word4126 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_word4136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_word4144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_word4152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_word4163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_word4174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word4188 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_operator4217 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_operator4224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_operator4231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_operator4238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_operator4247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_68_in_operator4254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_69_in_operator4261 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_operator4268 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_operator4275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_operator4282 = new BitSet(new long[]{0x0000000000000002L});

}