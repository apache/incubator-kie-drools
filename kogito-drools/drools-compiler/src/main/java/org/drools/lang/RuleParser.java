// $ANTLR 3.0ea8 /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g 2006-04-22 21:27:15

	package org.drools.lang;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.StringTokenizer;
	import org.drools.lang.descr.*;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class RuleParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "BOOL", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\';\'", "\'package\'", "\'import\'", "\'expander\'", "\'global\'", "\'function\'", "\'(\'", "\',\'", "\')\'", "\'{\'", "\'}\'", "\'query\'", "\'end\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'attributes\'", "\'salience\'", "\'no-loop\'", "\'auto-focus\'", "\'xor-group\'", "\'agenda-group\'", "\'duration\'", "\'or\'", "\'==\'", "\'>\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'matches\'", "\'null\'", "\'.\'", "\'->\'", "\'||\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'use\'"
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
    	
    	public void setSource(String source) {
    		this.source = source;
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

            /** Reparse the results of the expansion */
        	private void reparseLhs(String text, AndDescr descrs) throws RecognitionException {
        		CharStream charStream = new ANTLRStringStream( text );
        		RuleParserLexer lexer = new RuleParserLexer( charStream );
        		TokenStream tokenStream = new CommonTokenStream( lexer );
        		RuleParser parser = new RuleParser( tokenStream );
        		parser.normal_lhs_block(descrs);
                
                    if (parser.hasErrors()) {
    		        //add the offset of the error 
            	        for ( Iterator iter = parser.getErrors().iterator(); iter.hasNext(); ) {
                    	    RecognitionException err = (RecognitionException) iter.next();
                        	err.line = err.line + descrs.getLine();
                    	}
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
                            //System.err.print("[SPURIOUS] ");
                            return;
                    }
                    errorRecovery = true;

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
                    } else if (e instanceof ExpanderException) {
    			message.append(" " + e.getMessage());
    		}
                   	return message.toString();
            }   



    // $ANTLR start opt_eol
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:226:1: opt_eol : ( (';'|EOL))* ;
    public void opt_eol() throws RecognitionException {   
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:227:17: ( ( (';'|EOL))* )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:227:17: ( (';'|EOL))*
            {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:227:17: ( (';'|EOL))*
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
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:227:18: (';'|EOL)
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:230:1: compilation_unit : opt_eol prolog (r= rule | q= query | extra_statement )* ;
    public void compilation_unit() throws RecognitionException {   
        RuleDescr r = null;

        QueryDescr q = null;


        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:231:17: ( opt_eol prolog (r= rule | q= query | extra_statement )* )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:231:17: opt_eol prolog (r= rule | q= query | extra_statement )*
            {
            following.push(FOLLOW_opt_eol_in_compilation_unit57);
            opt_eol();
            following.pop();

            following.push(FOLLOW_prolog_in_compilation_unit61);
            prolog();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:233:17: (r= rule | q= query | extra_statement )*
            loop2:
            do {
                int alt2=4;
                alt2 = dfa2.predict(input); 
                switch (alt2) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:233:25: r= rule
            	    {
            	    following.push(FOLLOW_rule_in_compilation_unit70);
            	    r=rule();
            	    following.pop();

            	    this.packageDescr.addRule( r ); 

            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:234:25: q= query
            	    {
            	    following.push(FOLLOW_query_in_compilation_unit83);
            	    q=query();
            	    following.pop();

            	    this.packageDescr.addRule( q ); 

            	    }
            	    break;
            	case 3 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:235:25: extra_statement
            	    {
            	    following.push(FOLLOW_extra_statement_in_compilation_unit91);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:239:1: prolog : opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol ;
    public void prolog() throws RecognitionException {   
        String name = null;



        		String packageName = "";
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:243:17: ( opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:243:17: opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_prolog115);
            opt_eol();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:244:17: (name= package_statement )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( LA3_0==16 ) {
                alt3=1;
            }
            else if ( LA3_0==-1||LA3_0==EOL||LA3_0==15||(LA3_0>=17 && LA3_0<=20)||LA3_0==26||LA3_0==28 ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("244:17: (name= package_statement )?", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:244:19: name= package_statement
                    {
                    following.push(FOLLOW_package_statement_in_prolog123);
                    name=package_statement();
                    following.pop();

                     packageName = name; 

                    }
                    break;

            }

             
            			this.packageDescr = new PackageDescr( name ); 
            		
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:248:17: ( extra_statement | expander )*
            loop4:
            do {
                int alt4=3;
                alt4 = dfa4.predict(input); 
                switch (alt4) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:248:25: extra_statement
            	    {
            	    following.push(FOLLOW_extra_statement_in_prolog138);
            	    extra_statement();
            	    following.pop();


            	    }
            	    break;
            	case 2 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:249:25: expander
            	    {
            	    following.push(FOLLOW_expander_in_prolog144);
            	    expander();
            	    following.pop();


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_prolog156);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:255:1: package_statement returns [String packageName] : 'package' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   
        String packageName;
        String name = null;



        		packageName = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:260:17: ( 'package' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:260:17: 'package' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,16,FOLLOW_16_in_package_statement180); 
            following.push(FOLLOW_opt_eol_in_package_statement182);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_package_statement186);
            name=dotted_name();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:260:52: ( ';' )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( LA5_0==15 ) {
                alt5=1;
            }
            else if ( LA5_0==-1||LA5_0==EOL||(LA5_0>=17 && LA5_0<=20)||LA5_0==26||LA5_0==28 ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("260:52: ( \';\' )?", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:260:52: ';'
                    {
                    match(input,15,FOLLOW_15_in_package_statement188); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_package_statement191);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:266:1: import_statement : 'import' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:267:17: ( 'import' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:267:17: 'import' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_import_statement207); 
            following.push(FOLLOW_opt_eol_in_import_statement209);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_import_statement213);
            name=dotted_name();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:267:51: ( ';' )?
            int alt6=2;
            int LA6_0 = input.LA(1);
            if ( LA6_0==15 ) {
                alt6=1;
            }
            else if ( LA6_0==-1||LA6_0==EOL||(LA6_0>=17 && LA6_0<=20)||LA6_0==26||LA6_0==28 ) {
                alt6=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("267:51: ( \';\' )?", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:267:51: ';'
                    {
                    match(input,15,FOLLOW_15_in_import_statement215); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_import_statement218);
            opt_eol();
            following.pop();


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


    // $ANTLR start expander
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:273:1: expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void expander() throws RecognitionException {   
        String name = null;



        		String config=null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:277:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:277:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {
            match(input,18,FOLLOW_18_in_expander240); 
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:277:28: (name= dotted_name )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( LA7_0==ID ) {
                alt7=1;
            }
            else if ( LA7_0==-1||LA7_0==EOL||LA7_0==15||(LA7_0>=17 && LA7_0<=20)||LA7_0==26||LA7_0==28 ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("277:28: (name= dotted_name )?", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:277:29: name= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_expander245);
                    name=dotted_name();
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:277:48: ( ';' )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            if ( LA8_0==15 ) {
                alt8=1;
            }
            else if ( LA8_0==-1||LA8_0==EOL||(LA8_0>=17 && LA8_0<=20)||LA8_0==26||LA8_0==28 ) {
                alt8=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("277:48: ( \';\' )?", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:277:48: ';'
                    {
                    match(input,15,FOLLOW_15_in_expander249); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_expander252);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:287:1: global : 'global' type= dotted_name id= ID ( ';' )? opt_eol ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;



        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:291:17: ( 'global' type= dotted_name id= ID ( ';' )? opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:291:17: 'global' type= dotted_name id= ID ( ';' )? opt_eol
            {
            match(input,19,FOLLOW_19_in_global276); 
            following.push(FOLLOW_dotted_name_in_global280);
            type=dotted_name();
            following.pop();

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global284); 
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:291:49: ( ';' )?
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( LA9_0==15 ) {
                alt9=1;
            }
            else if ( LA9_0==-1||LA9_0==EOL||(LA9_0>=17 && LA9_0<=20)||LA9_0==26||LA9_0==28 ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("291:49: ( \';\' )?", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:291:49: ';'
                    {
                    match(input,15,FOLLOW_15_in_global286); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_global289);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:297:1: function : 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol ;
    public void function() throws RecognitionException {   
        Token name=null;
        Token paramName=null;
        String retType = null;

        String paramType = null;

        String body = null;



        		FunctionDescr f = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:302:17: ( 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:302:17: 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol
            {
            match(input,20,FOLLOW_20_in_function313); 
            following.push(FOLLOW_opt_eol_in_function315);
            opt_eol();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:302:36: (retType= dotted_name )?
            int alt10=2;
            alt10 = dfa10.predict(input); 
            switch (alt10) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:302:37: retType= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_function320);
                    retType=dotted_name();
                    following.pop();


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_function324);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function328); 
            following.push(FOLLOW_opt_eol_in_function330);
            opt_eol();
            following.pop();


            			//System.err.println( "function :: " + name.getText() );
            			f = new FunctionDescr( name.getText(), retType );
            		
            match(input,21,FOLLOW_21_in_function339); 
            following.push(FOLLOW_opt_eol_in_function341);
            opt_eol();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:308:25: ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0>=EOL && LA14_0<=ID)||LA14_0==15 ) {
                alt14=1;
            }
            else if ( LA14_0==23 ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("308:25: ( (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( \',\' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )* )?", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:308:33: (paramType= dotted_name )? opt_eol paramName= ID opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )*
                    {
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:308:33: (paramType= dotted_name )?
                    int alt11=2;
                    alt11 = dfa11.predict(input); 
                    switch (alt11) {
                        case 1 :
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:308:34: paramType= dotted_name
                            {
                            following.push(FOLLOW_dotted_name_in_function351);
                            paramType=dotted_name();
                            following.pop();


                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_function355);
                    opt_eol();
                    following.pop();

                    paramName=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_function359); 
                    following.push(FOLLOW_opt_eol_in_function361);
                    opt_eol();
                    following.pop();


                    					f.addParameter( paramType, paramName.getText() );
                    				
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:312:33: ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol )*
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);
                        if ( LA13_0==22 ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:312:41: ',' opt_eol (paramType= dotted_name )? opt_eol paramName= ID opt_eol
                    	    {
                    	    match(input,22,FOLLOW_22_in_function375); 
                    	    following.push(FOLLOW_opt_eol_in_function377);
                    	    opt_eol();
                    	    following.pop();

                    	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:312:53: (paramType= dotted_name )?
                    	    int alt12=2;
                    	    alt12 = dfa12.predict(input); 
                    	    switch (alt12) {
                    	        case 1 :
                    	            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:312:54: paramType= dotted_name
                    	            {
                    	            following.push(FOLLOW_dotted_name_in_function382);
                    	            paramType=dotted_name();
                    	            following.pop();


                    	            }
                    	            break;

                    	    }

                    	    following.push(FOLLOW_opt_eol_in_function386);
                    	    opt_eol();
                    	    following.pop();

                    	    paramName=(Token)input.LT(1);
                    	    match(input,ID,FOLLOW_ID_in_function390); 
                    	    following.push(FOLLOW_opt_eol_in_function392);
                    	    opt_eol();
                    	    following.pop();


                    	    						f.addParameter( paramType, paramName.getText() );
                    	    					

                    	    }
                    	    break;

                    	default :
                    	    break loop13;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,23,FOLLOW_23_in_function417); 
            following.push(FOLLOW_opt_eol_in_function421);
            opt_eol();
            following.pop();

            match(input,24,FOLLOW_24_in_function425); 
            following.push(FOLLOW_curly_chunk_in_function432);
            body=curly_chunk();
            following.pop();


            				f.setText( body );
            			
            match(input,25,FOLLOW_25_in_function441); 

            			packageDescr.addFunction( f );
            		
            following.push(FOLLOW_opt_eol_in_function449);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:333:1: query returns [QueryDescr query] : opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query;
        Token loc=null;
        String queryName = null;



        		query = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:338:17: ( opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:338:17: opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_query473);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,26,FOLLOW_26_in_query479); 
            following.push(FOLLOW_word_in_query483);
            queryName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_query485);
            opt_eol();
            following.pop();

             
            			query = new QueryDescr( queryName, null ); 
            			query.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            			AndDescr lhs = new AndDescr(); query.setLhs( lhs ); 
            			lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:346:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
            int alt15=2;
            switch ( input.LA(1) ) {
            case 21:
                int LA15_1 = input.LA(2);
                if (  expander != null  ) {
                    alt15=1;
                }
                else if ( true ) {
                    alt15=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("346:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 1, input);

                    throw nvae;
                }
                break;
            case EOL:
                int LA15_2 = input.LA(2);
                if (  expander != null  ) {
                    alt15=1;
                }
                else if ( true ) {
                    alt15=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("346:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 2, input);

                    throw nvae;
                }
                break;
            case 27:
                int LA15_3 = input.LA(2);
                if (  expander != null  ) {
                    alt15=1;
                }
                else if ( true ) {
                    alt15=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("346:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 3, input);

                    throw nvae;
                }
                break;
            case 54:
                int LA15_4 = input.LA(2);
                if (  expander != null  ) {
                    alt15=1;
                }
                else if ( true ) {
                    alt15=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("346:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 4, input);

                    throw nvae;
                }
                break;
            case 55:
                int LA15_5 = input.LA(2);
                if (  expander != null  ) {
                    alt15=1;
                }
                else if ( true ) {
                    alt15=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("346:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 5, input);

                    throw nvae;
                }
                break;
            case 56:
                int LA15_6 = input.LA(2);
                if (  expander != null  ) {
                    alt15=1;
                }
                else if ( true ) {
                    alt15=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("346:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 6, input);

                    throw nvae;
                }
                break;
            case ID:
                int LA15_7 = input.LA(2);
                if (  expander != null  ) {
                    alt15=1;
                }
                else if ( true ) {
                    alt15=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("346:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 7, input);

                    throw nvae;
                }
                break;
            case 15:
                int LA15_8 = input.LA(2);
                if (  expander != null  ) {
                    alt15=1;
                }
                else if ( true ) {
                    alt15=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("346:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 8, input);

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
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
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
            case 57:
                alt15=1;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("346:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:25: {...}? expander_lhs_block[lhs]
                    {
                    if ( !( expander != null ) ) {
                        throw new FailedPredicateException(input, "query", " expander != null ");
                    }
                    following.push(FOLLOW_expander_lhs_block_in_query501);
                    expander_lhs_block(lhs);
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:348:27: normal_lhs_block[lhs]
                    {
                    following.push(FOLLOW_normal_lhs_block_in_query509);
                    normal_lhs_block(lhs);
                    following.pop();


                    }
                    break;

            }

            match(input,27,FOLLOW_27_in_query524); 
            following.push(FOLLOW_opt_eol_in_query526);
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


    // $ANTLR start rule
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:354:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )? 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule;
        Token loc=null;
        Token any=null;
        String ruleName = null;



        		rule = null;
        		String consequence = "";
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:360:17: ( opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )? 'end' opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:360:17: opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )? 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_rule549);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,28,FOLLOW_28_in_rule555); 
            following.push(FOLLOW_word_in_rule559);
            ruleName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_rule561);
            opt_eol();
            following.pop();

             
            			rule = new RuleDescr( ruleName, null ); 
            			rule.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:366:17: ( rule_attributes[rule] )?
            int alt16=2;
            switch ( input.LA(1) ) {
            case 30:
            case 32:
                alt16=1;
                break;
            case EOL:
            case 15:
            case 22:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
                alt16=1;
                break;
            case 29:
                alt16=1;
                break;
            case 31:
                alt16=1;
                break;
            case 27:
                alt16=1;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("366:17: ( rule_attributes[rule] )?", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:366:25: rule_attributes[rule]
                    {
                    following.push(FOLLOW_rule_attributes_in_rule572);
                    rule_attributes(rule);
                    following.pop();


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule582);
            opt_eol();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:369:17: ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )?
            int alt23=2;
            int LA23_0 = input.LA(1);
            if ( LA23_0==EOL||LA23_0==15||LA23_0==29||LA23_0==31 ) {
                alt23=1;
            }
            else if ( LA23_0==27 ) {
                alt23=1;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("369:17: ( (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= \'then\' ( \':\' )? opt_eol ( options {greedy=false; } : any= . )* )? )?", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:369:18: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )?
                    {
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:369:18: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?
                    int alt19=2;
                    int LA19_0 = input.LA(1);
                    if ( LA19_0==29 ) {
                        alt19=1;
                    }
                    else if ( LA19_0==EOL||LA19_0==15||LA19_0==27||LA19_0==31 ) {
                        alt19=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("369:18: (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?", 19, 0, input);

                        throw nvae;
                    }
                    switch (alt19) {
                        case 1 :
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:369:25: loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                            {
                            loc=(Token)input.LT(1);
                            match(input,29,FOLLOW_29_in_rule591); 
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:369:36: ( ':' )?
                            int alt17=2;
                            int LA17_0 = input.LA(1);
                            if ( LA17_0==30 ) {
                                int LA17_1 = input.LA(2);
                                if ( !( expander != null ) ) {
                                    alt17=1;
                                }
                                else if (  expander != null  ) {
                                    alt17=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("369:36: ( \':\' )?", 17, 1, input);

                                    throw nvae;
                                }
                            }
                            else if ( (LA17_0>=EOL && LA17_0<=29)||(LA17_0>=31 && LA17_0<=57) ) {
                                alt17=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("369:36: ( \':\' )?", 17, 0, input);

                                throw nvae;
                            }
                            switch (alt17) {
                                case 1 :
                                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:369:36: ':'
                                    {
                                    match(input,30,FOLLOW_30_in_rule593); 

                                    }
                                    break;

                            }

                            following.push(FOLLOW_opt_eol_in_rule596);
                            opt_eol();
                            following.pop();

                             
                            				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                            				lhs.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                            			
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:374:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                            int alt18=2;
                            switch ( input.LA(1) ) {
                            case 21:
                                int LA18_1 = input.LA(2);
                                if (  expander != null  ) {
                                    alt18=1;
                                }
                                else if ( true ) {
                                    alt18=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("374:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 1, input);

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
                                        new NoViableAltException("374:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 2, input);

                                    throw nvae;
                                }
                                break;
                            case 15:
                                int LA18_3 = input.LA(2);
                                if (  expander != null  ) {
                                    alt18=1;
                                }
                                else if ( true ) {
                                    alt18=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("374:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 3, input);

                                    throw nvae;
                                }
                                break;
                            case 31:
                                int LA18_4 = input.LA(2);
                                if (  expander != null  ) {
                                    alt18=1;
                                }
                                else if ( true ) {
                                    alt18=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("374:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 4, input);

                                    throw nvae;
                                }
                                break;
                            case 27:
                                int LA18_5 = input.LA(2);
                                if (  expander != null  ) {
                                    alt18=1;
                                }
                                else if ( true ) {
                                    alt18=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("374:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 5, input);

                                    throw nvae;
                                }
                                break;
                            case 54:
                                int LA18_6 = input.LA(2);
                                if (  expander != null  ) {
                                    alt18=1;
                                }
                                else if ( true ) {
                                    alt18=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("374:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 6, input);

                                    throw nvae;
                                }
                                break;
                            case 55:
                                int LA18_7 = input.LA(2);
                                if (  expander != null  ) {
                                    alt18=1;
                                }
                                else if ( true ) {
                                    alt18=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("374:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 7, input);

                                    throw nvae;
                                }
                                break;
                            case 56:
                                int LA18_8 = input.LA(2);
                                if (  expander != null  ) {
                                    alt18=1;
                                }
                                else if ( true ) {
                                    alt18=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("374:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 8, input);

                                    throw nvae;
                                }
                                break;
                            case ID:
                                int LA18_9 = input.LA(2);
                                if (  expander != null  ) {
                                    alt18=1;
                                }
                                else if ( true ) {
                                    alt18=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("374:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 9, input);

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
                            case 22:
                            case 23:
                            case 24:
                            case 25:
                            case 26:
                            case 28:
                            case 29:
                            case 30:
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
                            case 57:
                                alt18=1;
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("374:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 0, input);

                                throw nvae;
                            }

                            switch (alt18) {
                                case 1 :
                                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:375:33: {...}? expander_lhs_block[lhs]
                                    {
                                    if ( !( expander != null ) ) {
                                        throw new FailedPredicateException(input, "rule", " expander != null ");
                                    }
                                    following.push(FOLLOW_expander_lhs_block_in_rule614);
                                    expander_lhs_block(lhs);
                                    following.pop();


                                    }
                                    break;
                                case 2 :
                                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:376:35: normal_lhs_block[lhs]
                                    {
                                    following.push(FOLLOW_normal_lhs_block_in_rule623);
                                    normal_lhs_block(lhs);
                                    following.pop();


                                    }
                                    break;

                            }


                            }
                            break;

                    }

                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:380:17: ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);
                    if ( LA22_0==EOL||LA22_0==15||LA22_0==31 ) {
                        alt22=1;
                    }
                    else if ( LA22_0==27 ) {
                        alt22=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("380:17: ( opt_eol loc= \'then\' ( \':\' )? opt_eol ( options {greedy=false; } : any= . )* )?", 22, 0, input);

                        throw nvae;
                    }
                    switch (alt22) {
                        case 1 :
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:380:19: opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )*
                            {
                            following.push(FOLLOW_opt_eol_in_rule646);
                            opt_eol();
                            following.pop();

                            loc=(Token)input.LT(1);
                            match(input,31,FOLLOW_31_in_rule650); 
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:380:38: ( ':' )?
                            int alt20=2;
                            int LA20_0 = input.LA(1);
                            if ( LA20_0==30 ) {
                                alt20=1;
                            }
                            else if ( (LA20_0>=EOL && LA20_0<=29)||(LA20_0>=31 && LA20_0<=57) ) {
                                alt20=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("380:38: ( \':\' )?", 20, 0, input);

                                throw nvae;
                            }
                            switch (alt20) {
                                case 1 :
                                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:380:38: ':'
                                    {
                                    match(input,30,FOLLOW_30_in_rule652); 

                                    }
                                    break;

                            }

                            following.push(FOLLOW_opt_eol_in_rule656);
                            opt_eol();
                            following.pop();

                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:381:25: ( options {greedy=false; } : any= . )*
                            loop21:
                            do {
                                int alt21=2;
                                int LA21_0 = input.LA(1);
                                if ( LA21_0==27 ) {
                                    alt21=2;
                                }
                                else if ( (LA21_0>=EOL && LA21_0<=26)||(LA21_0>=28 && LA21_0<=57) ) {
                                    alt21=1;
                                }


                                switch (alt21) {
                            	case 1 :
                            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:381:52: any= .
                            	    {
                            	    any=(Token)input.LT(1);
                            	    matchAny(input); 

                            	    					consequence = consequence + " " + any.getText();
                            	    				

                            	    }
                            	    break;

                            	default :
                            	    break loop21;
                                }
                            } while (true);


                            				if ( expander != null ) {
                            					String expanded = runThenExpander( consequence, loc.getLine() );
                            					rule.setConsequence( expanded );
                            				} else { 
                            					rule.setConsequence( consequence ); 
                            				}
                            				rule.setConsequenceLocation(loc.getLine(), loc.getCharPositionInLine());
                            			

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,27,FOLLOW_27_in_rule702); 
            following.push(FOLLOW_opt_eol_in_rule704);
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
        return rule;
    }
    // $ANTLR end rule


    // $ANTLR start extra_statement
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:399:1: extra_statement : ( import_statement | global | function ) ;
    public void extra_statement() throws RecognitionException {   
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:401:9: ( ( import_statement | global | function ) )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:401:9: ( import_statement | global | function )
            {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:401:9: ( import_statement | global | function )
            int alt24=3;
            switch ( input.LA(1) ) {
            case 17:
                alt24=1;
                break;
            case 19:
                alt24=2;
                break;
            case 20:
                alt24=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("401:9: ( import_statement | global | function )", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:401:17: import_statement
                    {
                    following.push(FOLLOW_import_statement_in_extra_statement720);
                    import_statement();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:402:17: global
                    {
                    following.push(FOLLOW_global_in_extra_statement725);
                    global();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:403:17: function
                    {
                    following.push(FOLLOW_function_in_extra_statement730);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:407:1: rule_attributes[RuleDescr rule] : ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:409:25: ( ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:409:25: ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )*
            {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:409:25: ( 'attributes' )?
            int alt25=2;
            int LA25_0 = input.LA(1);
            if ( LA25_0==32 ) {
                alt25=1;
            }
            else if ( LA25_0==EOL||LA25_0==15||LA25_0==22||LA25_0==27||(LA25_0>=29 && LA25_0<=31)||(LA25_0>=33 && LA25_0<=38) ) {
                alt25=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("409:25: ( \'attributes\' )?", 25, 0, input);

                throw nvae;
            }
            switch (alt25) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:409:25: 'attributes'
                    {
                    match(input,32,FOLLOW_32_in_rule_attributes749); 

                    }
                    break;

            }

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:409:39: ( ':' )?
            int alt26=2;
            int LA26_0 = input.LA(1);
            if ( LA26_0==30 ) {
                alt26=1;
            }
            else if ( LA26_0==EOL||LA26_0==15||LA26_0==22||LA26_0==27||LA26_0==29||LA26_0==31||(LA26_0>=33 && LA26_0<=38) ) {
                alt26=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("409:39: ( \':\' )?", 26, 0, input);

                throw nvae;
            }
            switch (alt26) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:409:39: ':'
                    {
                    match(input,30,FOLLOW_30_in_rule_attributes752); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule_attributes755);
            opt_eol();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:410:25: ( ( ',' )? a= rule_attribute opt_eol )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);
                if ( LA28_0==22||(LA28_0>=33 && LA28_0<=38) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:410:33: ( ',' )? a= rule_attribute opt_eol
            	    {
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:410:33: ( ',' )?
            	    int alt27=2;
            	    int LA27_0 = input.LA(1);
            	    if ( LA27_0==22 ) {
            	        alt27=1;
            	    }
            	    else if ( (LA27_0>=33 && LA27_0<=38) ) {
            	        alt27=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("410:33: ( \',\' )?", 27, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt27) {
            	        case 1 :
            	            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:410:33: ','
            	            {
            	            match(input,22,FOLLOW_22_in_rule_attributes762); 

            	            }
            	            break;

            	    }

            	    following.push(FOLLOW_rule_attribute_in_rule_attributes767);
            	    a=rule_attribute();
            	    following.pop();

            	    following.push(FOLLOW_opt_eol_in_rule_attributes769);
            	    opt_eol();
            	    following.pop();


            	    					rule.addAttribute( a );
            	    				

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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:417:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= xor_group | a= auto_focus );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d;
        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:422:25: (a= salience | a= no_loop | a= agenda_group | a= duration | a= xor_group | a= auto_focus )
            int alt29=6;
            switch ( input.LA(1) ) {
            case 33:
                alt29=1;
                break;
            case 34:
                alt29=2;
                break;
            case 37:
                alt29=3;
                break;
            case 38:
                alt29=4;
                break;
            case 36:
                alt29=5;
                break;
            case 35:
                alt29=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("417:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= xor_group | a= auto_focus );", 29, 0, input);

                throw nvae;
            }

            switch (alt29) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:422:25: a= salience
                    {
                    following.push(FOLLOW_salience_in_rule_attribute808);
                    a=salience();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:423:25: a= no_loop
                    {
                    following.push(FOLLOW_no_loop_in_rule_attribute818);
                    a=no_loop();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 3 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:424:25: a= agenda_group
                    {
                    following.push(FOLLOW_agenda_group_in_rule_attribute829);
                    a=agenda_group();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 4 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:425:25: a= duration
                    {
                    following.push(FOLLOW_duration_in_rule_attribute842);
                    a=duration();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 5 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:426:25: a= xor_group
                    {
                    following.push(FOLLOW_xor_group_in_rule_attribute856);
                    a=xor_group();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 6 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:427:25: a= auto_focus
                    {
                    following.push(FOLLOW_auto_focus_in_rule_attribute867);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:431:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:436:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:436:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,33,FOLLOW_33_in_salience900); 
            following.push(FOLLOW_opt_eol_in_salience902);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience906); 
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:436:46: ( ';' )?
            int alt30=2;
            int LA30_0 = input.LA(1);
            if ( LA30_0==15 ) {
                alt30=1;
            }
            else if ( LA30_0==EOL||LA30_0==22||LA30_0==27||LA30_0==29||LA30_0==31||(LA30_0>=33 && LA30_0<=38) ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("436:46: ( \';\' )?", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:436:46: ';'
                    {
                    match(input,15,FOLLOW_15_in_salience908); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_salience911);
            opt_eol();
            following.pop();


            			d = new AttributeDescr( "salience", i.getText() );
            			d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		

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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:443:1: no_loop returns [AttributeDescr d] : ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:448:17: ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) )
            int alt33=2;
            int LA33_0 = input.LA(1);
            if ( LA33_0==34 ) {
                int LA33_1 = input.LA(2);
                if ( LA33_1==BOOL ) {
                    alt33=2;
                }
                else if ( LA33_1==EOL||LA33_1==15||LA33_1==22||LA33_1==27||LA33_1==29||LA33_1==31||(LA33_1>=33 && LA33_1<=38) ) {
                    alt33=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("443:1: no_loop returns [AttributeDescr d] : ( (loc= \'no-loop\' opt_eol ( \';\' )? opt_eol ) | (loc= \'no-loop\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 33, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("443:1: no_loop returns [AttributeDescr d] : ( (loc= \'no-loop\' opt_eol ( \';\' )? opt_eol ) | (loc= \'no-loop\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:448:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                    {
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:448:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:449:25: loc= 'no-loop' opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,34,FOLLOW_34_in_no_loop946); 
                    following.push(FOLLOW_opt_eol_in_no_loop948);
                    opt_eol();
                    following.pop();

                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:449:47: ( ';' )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);
                    if ( LA31_0==15 ) {
                        alt31=1;
                    }
                    else if ( LA31_0==EOL||LA31_0==22||LA31_0==27||LA31_0==29||LA31_0==31||(LA31_0>=33 && LA31_0<=38) ) {
                        alt31=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("449:47: ( \';\' )?", 31, 0, input);

                        throw nvae;
                    }
                    switch (alt31) {
                        case 1 :
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:449:47: ';'
                            {
                            match(input,15,FOLLOW_15_in_no_loop950); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_no_loop953);
                    opt_eol();
                    following.pop();


                    				d = new AttributeDescr( "no-loop", "true" );
                    				d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:456:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                    {
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:456:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:457:25: loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,34,FOLLOW_34_in_no_loop978); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop982); 
                    following.push(FOLLOW_opt_eol_in_no_loop984);
                    opt_eol();
                    following.pop();

                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:457:54: ( ';' )?
                    int alt32=2;
                    int LA32_0 = input.LA(1);
                    if ( LA32_0==15 ) {
                        alt32=1;
                    }
                    else if ( LA32_0==EOL||LA32_0==22||LA32_0==27||LA32_0==29||LA32_0==31||(LA32_0>=33 && LA32_0<=38) ) {
                        alt32=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("457:54: ( \';\' )?", 32, 0, input);

                        throw nvae;
                    }
                    switch (alt32) {
                        case 1 :
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:457:54: ';'
                            {
                            match(input,15,FOLLOW_15_in_no_loop986); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_no_loop989);
                    opt_eol();
                    following.pop();


                    				d = new AttributeDescr( "no-loop", t.getText() );
                    				d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			

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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:467:1: auto_focus returns [AttributeDescr d] : ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr auto_focus() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:472:17: ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) )
            int alt36=2;
            int LA36_0 = input.LA(1);
            if ( LA36_0==35 ) {
                int LA36_1 = input.LA(2);
                if ( LA36_1==BOOL ) {
                    alt36=2;
                }
                else if ( LA36_1==EOL||LA36_1==15||LA36_1==22||LA36_1==27||LA36_1==29||LA36_1==31||(LA36_1>=33 && LA36_1<=38) ) {
                    alt36=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("467:1: auto_focus returns [AttributeDescr d] : ( (loc= \'auto-focus\' opt_eol ( \';\' )? opt_eol ) | (loc= \'auto-focus\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 36, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("467:1: auto_focus returns [AttributeDescr d] : ( (loc= \'auto-focus\' opt_eol ( \';\' )? opt_eol ) | (loc= \'auto-focus\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:472:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                    {
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:472:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:473:25: loc= 'auto-focus' opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,35,FOLLOW_35_in_auto_focus1035); 
                    following.push(FOLLOW_opt_eol_in_auto_focus1037);
                    opt_eol();
                    following.pop();

                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:473:50: ( ';' )?
                    int alt34=2;
                    int LA34_0 = input.LA(1);
                    if ( LA34_0==15 ) {
                        alt34=1;
                    }
                    else if ( LA34_0==EOL||LA34_0==22||LA34_0==27||LA34_0==29||LA34_0==31||(LA34_0>=33 && LA34_0<=38) ) {
                        alt34=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("473:50: ( \';\' )?", 34, 0, input);

                        throw nvae;
                    }
                    switch (alt34) {
                        case 1 :
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:473:50: ';'
                            {
                            match(input,15,FOLLOW_15_in_auto_focus1039); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_auto_focus1042);
                    opt_eol();
                    following.pop();


                    				d = new AttributeDescr( "auto-focus", "true" );
                    				d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:480:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                    {
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:480:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:481:25: loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,35,FOLLOW_35_in_auto_focus1067); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1071); 
                    following.push(FOLLOW_opt_eol_in_auto_focus1073);
                    opt_eol();
                    following.pop();

                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:481:57: ( ';' )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);
                    if ( LA35_0==15 ) {
                        alt35=1;
                    }
                    else if ( LA35_0==EOL||LA35_0==22||LA35_0==27||LA35_0==29||LA35_0==31||(LA35_0>=33 && LA35_0<=38) ) {
                        alt35=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("481:57: ( \';\' )?", 35, 0, input);

                        throw nvae;
                    }
                    switch (alt35) {
                        case 1 :
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:481:57: ';'
                            {
                            match(input,15,FOLLOW_15_in_auto_focus1075); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_auto_focus1078);
                    opt_eol();
                    following.pop();


                    				d = new AttributeDescr( "auto-focus", t.getText() );
                    				d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
                    			

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


    // $ANTLR start xor_group
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:491:1: xor_group returns [AttributeDescr d] : loc= 'xor-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr xor_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;


        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:496:17: (loc= 'xor-group' opt_eol name= STRING ( ';' )? opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:496:17: loc= 'xor-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,36,FOLLOW_36_in_xor_group1120); 
            following.push(FOLLOW_opt_eol_in_xor_group1122);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_xor_group1126); 
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:496:53: ( ';' )?
            int alt37=2;
            int LA37_0 = input.LA(1);
            if ( LA37_0==15 ) {
                alt37=1;
            }
            else if ( LA37_0==EOL||LA37_0==22||LA37_0==27||LA37_0==29||LA37_0==31||(LA37_0>=33 && LA37_0<=38) ) {
                alt37=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("496:53: ( \';\' )?", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:496:53: ';'
                    {
                    match(input,15,FOLLOW_15_in_xor_group1128); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_xor_group1131);
            opt_eol();
            following.pop();


            			d = new AttributeDescr( "xor-group", getString( name ) );
            			d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		

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
    // $ANTLR end xor_group


    // $ANTLR start agenda_group
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:503:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr agenda_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;


        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:508:17: (loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:508:17: loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,37,FOLLOW_37_in_agenda_group1160); 
            following.push(FOLLOW_opt_eol_in_agenda_group1162);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1166); 
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:508:56: ( ';' )?
            int alt38=2;
            int LA38_0 = input.LA(1);
            if ( LA38_0==15 ) {
                alt38=1;
            }
            else if ( LA38_0==EOL||LA38_0==22||LA38_0==27||LA38_0==29||LA38_0==31||(LA38_0>=33 && LA38_0<=38) ) {
                alt38=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("508:56: ( \';\' )?", 38, 0, input);

                throw nvae;
            }
            switch (alt38) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:508:56: ';'
                    {
                    match(input,15,FOLLOW_15_in_agenda_group1168); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_agenda_group1171);
            opt_eol();
            following.pop();


            			d = new AttributeDescr( "agenda-group", getString( name ) );
            			d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		

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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:516:1: duration returns [AttributeDescr d] : loc= 'duration' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr duration() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:521:17: (loc= 'duration' opt_eol i= INT ( ';' )? opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:521:17: loc= 'duration' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,38,FOLLOW_38_in_duration1203); 
            following.push(FOLLOW_opt_eol_in_duration1205);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1209); 
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:521:46: ( ';' )?
            int alt39=2;
            int LA39_0 = input.LA(1);
            if ( LA39_0==15 ) {
                alt39=1;
            }
            else if ( LA39_0==EOL||LA39_0==22||LA39_0==27||LA39_0==29||LA39_0==31||(LA39_0>=33 && LA39_0<=38) ) {
                alt39=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("521:46: ( \';\' )?", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:521:46: ';'
                    {
                    match(input,15,FOLLOW_15_in_duration1211); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_duration1214);
            opt_eol();
            following.pop();


            			d = new AttributeDescr( "duration", i.getText() );
            			d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		

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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:529:1: normal_lhs_block[AndDescr descrs] : (d= lhs opt_eol )* opt_eol ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;


        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:531:17: ( (d= lhs opt_eol )* opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:531:17: (d= lhs opt_eol )* opt_eol
            {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:531:17: (d= lhs opt_eol )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);
                if ( LA40_0==ID||LA40_0==21||(LA40_0>=54 && LA40_0<=56) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:531:25: d= lhs opt_eol
            	    {
            	    following.push(FOLLOW_lhs_in_normal_lhs_block1240);
            	    d=lhs();
            	    following.pop();

            	    following.push(FOLLOW_opt_eol_in_normal_lhs_block1242);
            	    opt_eol();
            	    following.pop();

            	     descrs.addDescr( d ); 

            	    }
            	    break;

            	default :
            	    break loop40;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_normal_lhs_block1254);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:539:1: expander_lhs_block[AndDescr descrs] : ( options {greedy=false; } : text= paren_chunk loc= EOL )* ;
    public void expander_lhs_block(AndDescr descrs) throws RecognitionException {   
        Token loc=null;
        String text = null;



        		String lhsBlock = null;
        		String eol = System.getProperty( "line.separator" );
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:545:17: ( ( options {greedy=false; } : text= paren_chunk loc= EOL )* )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:545:17: ( options {greedy=false; } : text= paren_chunk loc= EOL )*
            {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:545:17: ( options {greedy=false; } : text= paren_chunk loc= EOL )*
            loop41:
            do {
                int alt41=2;
                switch ( input.LA(1) ) {
                case 27:
                    alt41=2;
                    break;
                case EOL:
                    alt41=2;
                    break;
                case 31:
                    alt41=2;
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
                case 28:
                case 29:
                case 30:
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
                    alt41=1;
                    break;
                case 15:
                    alt41=2;
                    break;

                }

                switch (alt41) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:546:25: text= paren_chunk loc= EOL
            	    {
            	    following.push(FOLLOW_paren_chunk_in_expander_lhs_block1295);
            	    text=paren_chunk();
            	    following.pop();

            	    loc=(Token)input.LT(1);
            	    match(input,EOL,FOLLOW_EOL_in_expander_lhs_block1299); 

            	    				//only expand non null
            	    				if (text != null) {
            	    					if (lhsBlock == null) {					
            	    						lhsBlock = runWhenExpander( text, loc.getLine());
            	    					} else {
            	    						lhsBlock = lhsBlock + eol + runWhenExpander( text, loc.getLine());
            	    					}
            	    					text = null;
            	    				}
            	    			

            	    }
            	    break;

            	default :
            	    break loop41;
                }
            } while (true);


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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:571:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   
        PatternDescr d;
        PatternDescr l = null;



        		d=null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:575:17: (l= lhs_or )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:575:17: l= lhs_or
            {
            following.push(FOLLOW_lhs_or_in_lhs1351);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:579:1: lhs_column returns [PatternDescr d] : (f= fact_binding | f= fact );
    public PatternDescr lhs_column() throws RecognitionException {   
        PatternDescr d;
        PatternDescr f = null;



        		d=null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:583:17: (f= fact_binding | f= fact )
            int alt42=2;
            alt42 = dfa42.predict(input); 
            switch (alt42) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:583:17: f= fact_binding
                    {
                    following.push(FOLLOW_fact_binding_in_lhs_column1379);
                    f=fact_binding();
                    following.pop();

                     d = f; 

                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:584:17: f= fact
                    {
                    following.push(FOLLOW_fact_in_lhs_column1388);
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


    // $ANTLR start fact_binding
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:587:1: fact_binding returns [PatternDescr d] : id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* ;
    public PatternDescr fact_binding() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        PatternDescr f = null;



        		d=null;
        		boolean multi=false;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:593:17: (id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )* )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:593:17: id= ID opt_eol ':' opt_eol f= fact opt_eol ( 'or' f= fact )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding1420); 
            following.push(FOLLOW_opt_eol_in_fact_binding1430);
            opt_eol();
            following.pop();

            match(input,30,FOLLOW_30_in_fact_binding1432); 
            following.push(FOLLOW_opt_eol_in_fact_binding1434);
            opt_eol();
            following.pop();

            following.push(FOLLOW_fact_in_fact_binding1442);
            f=fact();
            following.pop();

            following.push(FOLLOW_opt_eol_in_fact_binding1444);
            opt_eol();
            following.pop();


             			((ColumnDescr)f).setIdentifier( id.getText() );
             			d = f;
             		
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:601:17: ( 'or' f= fact )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);
                if ( LA43_0==39 ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:601:25: 'or' f= fact
            	    {
            	    match(input,39,FOLLOW_39_in_fact_binding1456); 
            	    	if ( ! multi ) {
            	     					PatternDescr first = d;
            	     					d = new OrDescr();
            	     					((OrDescr)d).addDescr( first );
            	     					multi=true;
            	     				}
            	     			
            	    following.push(FOLLOW_fact_in_fact_binding1470);
            	    f=fact();
            	    following.pop();


            	     				((ColumnDescr)f).setIdentifier( id.getText() );
            	     				((OrDescr)d).addDescr( f );
            	     			

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
    // $ANTLR end fact_binding


    // $ANTLR start fact
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:617:1: fact returns [PatternDescr d] : id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol ')' opt_eol ;
    public PatternDescr fact() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        String id = null;

        List c = null;



        		d=null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:621:17: (id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol ')' opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:621:17: id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol ')' opt_eol
            {
            following.push(FOLLOW_dotted_name_in_fact1510);
            id=dotted_name();
            following.pop();

             
             			d = new ColumnDescr( id ); 
             		
            following.push(FOLLOW_opt_eol_in_fact1518);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,21,FOLLOW_21_in_fact1526); 

             				d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
             			
            following.push(FOLLOW_opt_eol_in_fact1529);
            opt_eol();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:627:34: (c= constraints )?
            int alt44=2;
            alt44 = dfa44.predict(input); 
            switch (alt44) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:627:41: c= constraints
                    {
                    following.push(FOLLOW_constraints_in_fact1535);
                    c=constraints();
                    following.pop();


                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_fact1554);
            opt_eol();
            following.pop();

            match(input,23,FOLLOW_23_in_fact1556); 
            following.push(FOLLOW_opt_eol_in_fact1558);
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
        return d;
    }
    // $ANTLR end fact


    // $ANTLR start constraints
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:638:1: constraints returns [List constraints] : opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol ;
    public List constraints() throws RecognitionException {   
        List constraints;

        		constraints = new ArrayList();
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:642:17: ( opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:642:17: opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraints1583);
            opt_eol();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:643:17: ( constraint[constraints] | predicate[constraints] )
            int alt45=2;
            int LA45_0 = input.LA(1);
            if ( LA45_0==EOL||LA45_0==15 ) {
                alt45=1;
            }
            else if ( LA45_0==ID ) {
                int LA45_2 = input.LA(2);
                if ( LA45_2==30 ) {
                    int LA45_3 = input.LA(3);
                    if ( LA45_3==ID ) {
                        int LA45_8 = input.LA(4);
                        if ( LA45_8==50 ) {
                            alt45=2;
                        }
                        else if ( LA45_8==EOL||LA45_8==15||(LA45_8>=22 && LA45_8<=23)||(LA45_8>=40 && LA45_8<=47) ) {
                            alt45=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("643:17: ( constraint[constraints] | predicate[constraints] )", 45, 8, input);

                            throw nvae;
                        }
                    }
                    else if ( LA45_3==EOL||LA45_3==15 ) {
                        alt45=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("643:17: ( constraint[constraints] | predicate[constraints] )", 45, 3, input);

                        throw nvae;
                    }
                }
                else if ( LA45_2==EOL||LA45_2==15||(LA45_2>=22 && LA45_2<=23)||(LA45_2>=40 && LA45_2<=47) ) {
                    alt45=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("643:17: ( constraint[constraints] | predicate[constraints] )", 45, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("643:17: ( constraint[constraints] | predicate[constraints] )", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:643:18: constraint[constraints]
                    {
                    following.push(FOLLOW_constraint_in_constraints1588);
                    constraint(constraints);
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:643:42: predicate[constraints]
                    {
                    following.push(FOLLOW_predicate_in_constraints1591);
                    predicate(constraints);
                    following.pop();


                    }
                    break;

            }

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:644:17: ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )*
            loop47:
            do {
                int alt47=2;
                alt47 = dfa47.predict(input); 
                switch (alt47) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:644:19: opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] )
            	    {
            	    following.push(FOLLOW_opt_eol_in_constraints1599);
            	    opt_eol();
            	    following.pop();

            	    match(input,22,FOLLOW_22_in_constraints1601); 
            	    following.push(FOLLOW_opt_eol_in_constraints1603);
            	    opt_eol();
            	    following.pop();

            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:644:39: ( constraint[constraints] | predicate[constraints] )
            	    int alt46=2;
            	    int LA46_0 = input.LA(1);
            	    if ( LA46_0==EOL||LA46_0==15 ) {
            	        alt46=1;
            	    }
            	    else if ( LA46_0==ID ) {
            	        int LA46_2 = input.LA(2);
            	        if ( LA46_2==30 ) {
            	            int LA46_3 = input.LA(3);
            	            if ( LA46_3==ID ) {
            	                int LA46_8 = input.LA(4);
            	                if ( LA46_8==50 ) {
            	                    alt46=2;
            	                }
            	                else if ( LA46_8==EOL||LA46_8==15||(LA46_8>=22 && LA46_8<=23)||(LA46_8>=40 && LA46_8<=47) ) {
            	                    alt46=1;
            	                }
            	                else {
            	                    NoViableAltException nvae =
            	                        new NoViableAltException("644:39: ( constraint[constraints] | predicate[constraints] )", 46, 8, input);

            	                    throw nvae;
            	                }
            	            }
            	            else if ( LA46_3==EOL||LA46_3==15 ) {
            	                alt46=1;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("644:39: ( constraint[constraints] | predicate[constraints] )", 46, 3, input);

            	                throw nvae;
            	            }
            	        }
            	        else if ( LA46_2==EOL||LA46_2==15||(LA46_2>=22 && LA46_2<=23)||(LA46_2>=40 && LA46_2<=47) ) {
            	            alt46=1;
            	        }
            	        else {
            	            NoViableAltException nvae =
            	                new NoViableAltException("644:39: ( constraint[constraints] | predicate[constraints] )", 46, 2, input);

            	            throw nvae;
            	        }
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("644:39: ( constraint[constraints] | predicate[constraints] )", 46, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt46) {
            	        case 1 :
            	            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:644:40: constraint[constraints]
            	            {
            	            following.push(FOLLOW_constraint_in_constraints1606);
            	            constraint(constraints);
            	            following.pop();


            	            }
            	            break;
            	        case 2 :
            	            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:644:64: predicate[constraints]
            	            {
            	            following.push(FOLLOW_predicate_in_constraints1609);
            	            predicate(constraints);
            	            following.pop();


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop47;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_constraints1617);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:648:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )? opt_eol ;
    public void constraint(List constraints) throws RecognitionException {   
        Token fb=null;
        Token f=null;
        Token op=null;
        Token bvc=null;
        String lc = null;

        String rvc = null;



        		PatternDescr d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:652:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )? opt_eol )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:652:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )? opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraint1636);
            opt_eol();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:653:17: (fb= ID opt_eol ':' opt_eol )?
            int alt48=2;
            alt48 = dfa48.predict(input); 
            switch (alt48) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:653:19: fb= ID opt_eol ':' opt_eol
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint1644); 
                    following.push(FOLLOW_opt_eol_in_constraint1646);
                    opt_eol();
                    following.pop();

                    match(input,30,FOLLOW_30_in_constraint1648); 
                    following.push(FOLLOW_opt_eol_in_constraint1650);
                    opt_eol();
                    following.pop();


                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint1660); 

            			if ( fb != null ) {
            				//System.err.println( "fb: " + fb.getText() );
            				//System.err.println( " f: " + f.getText() );
            				d = new FieldBindingDescr( f.getText(), fb.getText() );
            				//System.err.println( "fbd: " + d );
            				
            				d.setLocation( f.getLine(), f.getCharPositionInLine() );
            				constraints.add( d );
            			} 
            		
            following.push(FOLLOW_opt_eol_in_constraint1670);
            opt_eol();
            following.pop();

            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:666:33: (op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )?
            int alt50=2;
            int LA50_0 = input.LA(1);
            if ( (LA50_0>=40 && LA50_0<=47) ) {
                alt50=1;
            }
            else if ( LA50_0==EOL||LA50_0==15||(LA50_0>=22 && LA50_0<=23) ) {
                alt50=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("666:33: (op= (\'==\'|\'>\'|\'>=\'|\'<\'|\'<=\'|\'!=\'|\'contains\'|\'matches\') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )?", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:666:41: op= ('=='|'>'|'>='|'<'|'<='|'!='|'contains'|'matches') opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    {
                    op=(Token)input.LT(1);
                    if ( (input.LA(1)>=40 && input.LA(1)<=47) ) {
                        input.consume();
                        errorRecovery=false;
                    }
                    else {
                        MismatchedSetException mse =
                            new MismatchedSetException(null,input);
                        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint1678);    throw mse;
                    }

                    following.push(FOLLOW_opt_eol_in_constraint1750);
                    opt_eol();
                    following.pop();

                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:676:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    int alt49=4;
                    switch ( input.LA(1) ) {
                    case ID:
                        int LA49_1 = input.LA(2);
                        if ( LA49_1==49 ) {
                            alt49=2;
                        }
                        else if ( LA49_1==EOL||LA49_1==15||(LA49_1>=22 && LA49_1<=23) ) {
                            alt49=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("676:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 49, 1, input);

                            throw nvae;
                        }
                        break;
                    case INT:
                    case BOOL:
                    case STRING:
                    case FLOAT:
                    case 48:
                        alt49=3;
                        break;
                    case 21:
                        alt49=4;
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("676:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 49, 0, input);

                        throw nvae;
                    }

                    switch (alt49) {
                        case 1 :
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:676:49: bvc= ID
                            {
                            bvc=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_constraint1768); 

                            							d = new BoundVariableDescr( f.getText(), op.getText(), bvc.getText() );
                            							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                            							constraints.add( d );
                            						

                            }
                            break;
                        case 2 :
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:683:49: lc= enum_constraint
                            {
                            following.push(FOLLOW_enum_constraint_in_constraint1793);
                            lc=enum_constraint();
                            following.pop();

                             
                            							d = new LiteralDescr( f.getText(), op.getText(), lc, true ); 
                            							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                            							constraints.add( d );
                            						

                            }
                            break;
                        case 3 :
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:690:49: lc= literal_constraint
                            {
                            following.push(FOLLOW_literal_constraint_in_constraint1825);
                            lc=literal_constraint();
                            following.pop();

                             
                            							d = new LiteralDescr( f.getText(), op.getText(), lc ); 
                            							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                            							constraints.add( d );
                            						

                            }
                            break;
                        case 4 :
                            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:696:49: rvc= retval_constraint
                            {
                            following.push(FOLLOW_retval_constraint_in_constraint1845);
                            rvc=retval_constraint();
                            following.pop();

                             
                            							d = new ReturnValueDescr( f.getText(), op.getText(), rvc ); 
                            							d.setLocation( f.getLine(), f.getCharPositionInLine() );
                            							constraints.add( d );
                            						

                            }
                            break;

                    }


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_constraint1878);
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:707:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) ;
    public String literal_constraint() throws RecognitionException {   
        String text;
        Token t=null;


        		text = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:711:17: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:711:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:711:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            int alt51=5;
            switch ( input.LA(1) ) {
            case STRING:
                alt51=1;
                break;
            case INT:
                alt51=2;
                break;
            case FLOAT:
                alt51=3;
                break;
            case BOOL:
                alt51=4;
                break;
            case 48:
                alt51=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("711:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= \'null\' )", 51, 0, input);

                throw nvae;
            }

            switch (alt51) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:711:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint1905); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:712:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint1916); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:713:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint1929); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:714:25: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint1940); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:715:25: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,48,FOLLOW_48_in_literal_constraint1952); 
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:719:1: enum_constraint returns [String text] : (cls= ID '.' en= ID ) ;
    public String enum_constraint() throws RecognitionException {   
        String text;
        Token cls=null;
        Token en=null;


        		text = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:723:17: ( (cls= ID '.' en= ID ) )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:723:17: (cls= ID '.' en= ID )
            {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:723:17: (cls= ID '.' en= ID )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:723:18: cls= ID '.' en= ID
            {
            cls=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint1983); 
            match(input,49,FOLLOW_49_in_enum_constraint1985); 
            en=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint1989); 

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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:726:1: retval_constraint returns [String text] : '(' c= paren_chunk ')' ;
    public String retval_constraint() throws RecognitionException {   
        String text;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:731:17: ( '(' c= paren_chunk ')' )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:731:17: '(' c= paren_chunk ')'
            {
            match(input,21,FOLLOW_21_in_retval_constraint2018); 
            following.push(FOLLOW_paren_chunk_in_retval_constraint2022);
            c=paren_chunk();
            following.pop();

            match(input,23,FOLLOW_23_in_retval_constraint2024); 
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:734:1: predicate[List constraints] : decl= ID ':' field= ID '->' '(' text= paren_chunk ')' ;
    public void predicate(List constraints) throws RecognitionException {   
        Token decl=null;
        Token field=null;
        String text = null;


        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:736:17: (decl= ID ':' field= ID '->' '(' text= paren_chunk ')' )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:736:17: decl= ID ':' field= ID '->' '(' text= paren_chunk ')'
            {
            decl=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2042); 
            match(input,30,FOLLOW_30_in_predicate2044); 
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2048); 
            match(input,50,FOLLOW_50_in_predicate2050); 
            match(input,21,FOLLOW_21_in_predicate2052); 
            following.push(FOLLOW_paren_chunk_in_predicate2056);
            text=paren_chunk();
            following.pop();

            match(input,23,FOLLOW_23_in_predicate2058); 

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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:743:1: paren_chunk returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* ;
    public String paren_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:749:18: ( ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:749:18: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:749:18: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            loop52:
            do {
                int alt52=3;
                switch ( input.LA(1) ) {
                case EOL:
                    alt52=3;
                    break;
                case 23:
                    alt52=3;
                    break;
                case 21:
                    alt52=1;
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
                case 22:
                case 24:
                case 25:
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
                    alt52=2;
                    break;

                }

                switch (alt52) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:750:25: '(' c= paren_chunk ')'
            	    {
            	    match(input,21,FOLLOW_21_in_paren_chunk2104); 
            	    following.push(FOLLOW_paren_chunk_in_paren_chunk2108);
            	    c=paren_chunk();
            	    following.pop();

            	    match(input,23,FOLLOW_23_in_paren_chunk2110); 

            	    				//System.err.println( "chunk [" + c + "]" );
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
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:762:19: any= .
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
        return text;
    }
    // $ANTLR end paren_chunk


    // $ANTLR start curly_chunk
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:774:1: curly_chunk returns [String text] : ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* ;
    public String curly_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:780:17: ( ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:780:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:780:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            loop53:
            do {
                int alt53=3;
                switch ( input.LA(1) ) {
                case 25:
                    alt53=3;
                    break;
                case 24:
                    alt53=1;
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
                    alt53=2;
                    break;

                }

                switch (alt53) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:781:25: '{' c= curly_chunk '}'
            	    {
            	    match(input,24,FOLLOW_24_in_curly_chunk2179); 
            	    following.push(FOLLOW_curly_chunk_in_curly_chunk2183);
            	    c=curly_chunk();
            	    following.pop();

            	    match(input,25,FOLLOW_25_in_curly_chunk2185); 

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
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:793:19: any= .
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
        return text;
    }
    // $ANTLR end curly_chunk


    // $ANTLR start lhs_or
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:805:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:810:17: (left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )* )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:810:17: left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )*
            {
             OrDescr or = null; 
            following.push(FOLLOW_lhs_and_in_lhs_or2243);
            left=lhs_and();
            following.pop();

            d = left; 
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:812:17: ( ('or'|'||') opt_eol right= lhs_and )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);
                if ( LA54_0==39||LA54_0==51 ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:812:19: ('or'|'||') opt_eol right= lhs_and
            	    {
            	    if ( input.LA(1)==39||input.LA(1)==51 ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or2252);    throw mse;
            	    }

            	    following.push(FOLLOW_opt_eol_in_lhs_or2257);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_lhs_and_in_lhs_or2264);
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
        return d;
    }
    // $ANTLR end lhs_or


    // $ANTLR start lhs_and
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:826:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:831:17: (left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )* )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:831:17: left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )*
            {
             AndDescr and = null; 
            following.push(FOLLOW_lhs_unary_in_lhs_and2304);
            left=lhs_unary();
            following.pop();

             d = left; 
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:833:17: ( ('and'|'&&') opt_eol right= lhs_unary )*
            loop55:
            do {
                int alt55=2;
                int LA55_0 = input.LA(1);
                if ( (LA55_0>=52 && LA55_0<=53) ) {
                    alt55=1;
                }


                switch (alt55) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:833:19: ('and'|'&&') opt_eol right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=52 && input.LA(1)<=53) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and2313);    throw mse;
            	    }

            	    following.push(FOLLOW_opt_eol_in_lhs_and2318);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_lhs_unary_in_lhs_and2325);
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
            	    break loop55;
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:847:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   
        PatternDescr d;
        PatternDescr u = null;



        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:851:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' ) )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:851:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:851:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | '(' u= lhs ')' )
            int alt56=5;
            switch ( input.LA(1) ) {
            case 54:
                alt56=1;
                break;
            case 55:
                alt56=2;
                break;
            case 56:
                alt56=3;
                break;
            case ID:
                alt56=4;
                break;
            case 21:
                alt56=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("851:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column | \'(\' u= lhs \')\' )", 56, 0, input);

                throw nvae;
            }

            switch (alt56) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:851:25: u= lhs_exist
                    {
                    following.push(FOLLOW_lhs_exist_in_lhs_unary2363);
                    u=lhs_exist();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:852:25: u= lhs_not
                    {
                    following.push(FOLLOW_lhs_not_in_lhs_unary2371);
                    u=lhs_not();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:853:25: u= lhs_eval
                    {
                    following.push(FOLLOW_lhs_eval_in_lhs_unary2379);
                    u=lhs_eval();
                    following.pop();


                    }
                    break;
                case 4 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:854:25: u= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_unary2387);
                    u=lhs_column();
                    following.pop();


                    }
                    break;
                case 5 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:855:25: '(' u= lhs ')'
                    {
                    match(input,21,FOLLOW_21_in_lhs_unary2393); 
                    following.push(FOLLOW_lhs_in_lhs_unary2397);
                    u=lhs();
                    following.pop();

                    match(input,23,FOLLOW_23_in_lhs_unary2399); 

                    }
                    break;

            }

             d = u; 

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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:859:1: lhs_exist returns [PatternDescr d] : loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public PatternDescr lhs_exist() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:863:17: (loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:863:17: loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,54,FOLLOW_54_in_lhs_exist2429); 
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:863:30: ( '(' column= lhs_column ')' | column= lhs_column )
            int alt57=2;
            int LA57_0 = input.LA(1);
            if ( LA57_0==21 ) {
                alt57=1;
            }
            else if ( LA57_0==ID ) {
                alt57=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("863:30: ( \'(\' column= lhs_column \')\' | column= lhs_column )", 57, 0, input);

                throw nvae;
            }
            switch (alt57) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:863:31: '(' column= lhs_column ')'
                    {
                    match(input,21,FOLLOW_21_in_lhs_exist2432); 
                    following.push(FOLLOW_lhs_column_in_lhs_exist2436);
                    column=lhs_column();
                    following.pop();

                    match(input,23,FOLLOW_23_in_lhs_exist2438); 

                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:863:59: column= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_exist2444);
                    column=lhs_column();
                    following.pop();


                    }
                    break;

            }

             
            			d = new ExistsDescr( (ColumnDescr) column ); 
            			d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		

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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:870:1: lhs_not returns [NotDescr d] : loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:874:17: (loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:874:17: loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,55,FOLLOW_55_in_lhs_not2474); 
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:874:27: ( '(' column= lhs_column ')' | column= lhs_column )
            int alt58=2;
            int LA58_0 = input.LA(1);
            if ( LA58_0==21 ) {
                alt58=1;
            }
            else if ( LA58_0==ID ) {
                alt58=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("874:27: ( \'(\' column= lhs_column \')\' | column= lhs_column )", 58, 0, input);

                throw nvae;
            }
            switch (alt58) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:874:28: '(' column= lhs_column ')'
                    {
                    match(input,21,FOLLOW_21_in_lhs_not2477); 
                    following.push(FOLLOW_lhs_column_in_lhs_not2481);
                    column=lhs_column();
                    following.pop();

                    match(input,23,FOLLOW_23_in_lhs_not2484); 

                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:874:57: column= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_not2490);
                    column=lhs_column();
                    following.pop();


                    }
                    break;

            }


            			d = new NotDescr( (ColumnDescr) column ); 
            			d.setLocation( loc.getLine(), loc.getCharPositionInLine() );
            		

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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:881:1: lhs_eval returns [PatternDescr d] : 'eval' '(' opt_eol c= paren_chunk ')' ;
    public PatternDescr lhs_eval() throws RecognitionException {   
        PatternDescr d;
        String c = null;



        		d = null;
        		String text = "";
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:886:17: ( 'eval' '(' opt_eol c= paren_chunk ')' )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:886:17: 'eval' '(' opt_eol c= paren_chunk ')'
            {
            match(input,56,FOLLOW_56_in_lhs_eval2516); 
            match(input,21,FOLLOW_21_in_lhs_eval2518); 
            following.push(FOLLOW_opt_eol_in_lhs_eval2520);
            opt_eol();
            following.pop();

            following.push(FOLLOW_paren_chunk_in_lhs_eval2524);
            c=paren_chunk();
            following.pop();

            match(input,23,FOLLOW_23_in_lhs_eval2526); 
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
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:890:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ;
    public String dotted_name() throws RecognitionException {   
        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:895:17: (id= ID ( '.' id= ID )* )
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:895:17: id= ID ( '.' id= ID )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name2558); 
             name=id.getText(); 
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:895:46: ( '.' id= ID )*
            loop59:
            do {
                int alt59=2;
                int LA59_0 = input.LA(1);
                if ( LA59_0==49 ) {
                    alt59=1;
                }


                switch (alt59) {
            	case 1 :
            	    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:895:48: '.' id= ID
            	    {
            	    match(input,49,FOLLOW_49_in_dotted_name2564); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name2568); 
            	     name = name + "." + id.getText(); 

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
        return name;
    }
    // $ANTLR end dotted_name


    // $ANTLR start word
    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:899:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word;
        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:903:17: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt60=11;
            switch ( input.LA(1) ) {
            case ID:
                alt60=1;
                break;
            case 17:
                alt60=2;
                break;
            case 57:
                alt60=3;
                break;
            case 28:
                alt60=4;
                break;
            case 26:
                alt60=5;
                break;
            case 33:
                alt60=6;
                break;
            case 34:
                alt60=7;
                break;
            case 29:
                alt60=8;
                break;
            case 31:
                alt60=9;
                break;
            case 27:
                alt60=10;
                break;
            case STRING:
                alt60=11;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("899:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'query\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 60, 0, input);

                throw nvae;
            }

            switch (alt60) {
                case 1 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:903:17: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word2598); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:904:17: 'import'
                    {
                    match(input,17,FOLLOW_17_in_word2610); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:905:17: 'use'
                    {
                    match(input,57,FOLLOW_57_in_word2619); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:906:17: 'rule'
                    {
                    match(input,28,FOLLOW_28_in_word2631); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:907:17: 'query'
                    {
                    match(input,26,FOLLOW_26_in_word2642); 
                     word="query"; 

                    }
                    break;
                case 6 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:908:17: 'salience'
                    {
                    match(input,33,FOLLOW_33_in_word2652); 
                     word="salience"; 

                    }
                    break;
                case 7 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:909:17: 'no-loop'
                    {
                    match(input,34,FOLLOW_34_in_word2660); 
                     word="no-loop"; 

                    }
                    break;
                case 8 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:910:17: 'when'
                    {
                    match(input,29,FOLLOW_29_in_word2668); 
                     word="when"; 

                    }
                    break;
                case 9 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:911:17: 'then'
                    {
                    match(input,31,FOLLOW_31_in_word2679); 
                     word="then"; 

                    }
                    break;
                case 10 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:912:17: 'end'
                    {
                    match(input,27,FOLLOW_27_in_word2690); 
                     word="end"; 

                    }
                    break;
                case 11 :
                    // /Users/bob/checkouts/jbossrules/drools-compiler/src/main/resources/org/drools/lang/drl.g:913:17: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word2704); 
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


    protected DFA2 dfa2 = new DFA2();protected DFA4 dfa4 = new DFA4();protected DFA10 dfa10 = new DFA10();protected DFA11 dfa11 = new DFA11();protected DFA12 dfa12 = new DFA12();protected DFA42 dfa42 = new DFA42();protected DFA44 dfa44 = new DFA44();protected DFA47 dfa47 = new DFA47();protected DFA48 dfa48 = new DFA48();
    class DFA2 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s1 = new DFA.State() {{alt=4;}};
        DFA.State s4 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 26:
                    return s4;

                case EOL:
                case 15:
                    return s2;

                case 28:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 2, 2, input);

                    throw nvae;        }
            }
        };
        DFA.State s5 = new DFA.State() {{alt=3;}};
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case -1:
                    return s1;

                case EOL:
                case 15:
                    return s2;

                case 28:
                    return s3;

                case 26:
                    return s4;

                case 17:
                case 19:
                case 20:
                    return s5;

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
        DFA.State s10 = new DFA.State() {{alt=1;}};
        DFA.State s9 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_9 = input.LA(1);
                if ( LA4_9==ID ) {return s10;}
                if ( LA4_9==EOL||LA4_9==15 ) {return s9;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 9, input);

                throw nvae;
            }
        };
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_4 = input.LA(1);
                if ( LA4_4==EOL||LA4_4==15 ) {return s9;}
                if ( LA4_4==ID ) {return s10;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 4, input);

                throw nvae;
            }
        };
        DFA.State s17 = new DFA.State() {{alt=1;}};
        DFA.State s24 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_24 = input.LA(1);
                if ( LA4_24==ID ) {return s17;}
                if ( LA4_24==49 ) {return s16;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 24, input);

                throw nvae;
            }
        };
        DFA.State s16 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_16 = input.LA(1);
                if ( LA4_16==ID ) {return s24;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 16, input);

                throw nvae;
            }
        };
        DFA.State s11 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_11 = input.LA(1);
                if ( LA4_11==49 ) {return s16;}
                if ( LA4_11==ID ) {return s17;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 11, input);

                throw nvae;
            }
        };
        DFA.State s5 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_5 = input.LA(1);
                if ( LA4_5==ID ) {return s11;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 5, input);

                throw nvae;
            }
        };
        DFA.State s71 = new DFA.State() {{alt=1;}};
        DFA.State s84 = new DFA.State() {{alt=1;}};
        DFA.State s93 = new DFA.State() {{alt=1;}};
        DFA.State s99 = new DFA.State() {{alt=1;}};
        DFA.State s101 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s99;

                case 24:
                    return s100;

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
                    return s101;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 101, input);

                    throw nvae;        }
            }
        };
        DFA.State s100 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s99;

                case 24:
                    return s100;

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
                    return s101;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 100, input);

                    throw nvae;        }
            }
        };
        DFA.State s94 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s99;

                case 24:
                    return s100;

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
                    return s101;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 94, input);

                    throw nvae;        }
            }
        };
        DFA.State s95 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s93;

                case 24:
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
                    return s95;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 95, input);

                    throw nvae;        }
            }
        };
        DFA.State s85 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s93;

                case 24:
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
                    return s95;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 85, input);

                    throw nvae;        }
            }
        };
        DFA.State s86 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s84;

                case 24:
                    return s85;

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
                    return s86;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 86, input);

                    throw nvae;        }
            }
        };
        DFA.State s72 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s84;

                case 24:
                    return s85;

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
                    return s86;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 72, input);

                    throw nvae;        }
            }
        };
        DFA.State s73 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s71;

                case 24:
                    return s72;

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
                    return s73;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 73, input);

                    throw nvae;        }
            }
        };
        DFA.State s58 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s71;

                case 24:
                    return s72;

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
                    return s73;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 58, input);

                    throw nvae;        }
            }
        };
        DFA.State s59 = new DFA.State() {{alt=1;}};
        DFA.State s60 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s59;

                case 24:
                    return s58;

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
                    return s60;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 60, input);

                    throw nvae;        }
            }
        };
        DFA.State s46 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 24:
                    return s58;

                case 25:
                    return s59;

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
                    return s60;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 46, input);

                    throw nvae;        }
            }
        };
        DFA.State s45 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_45 = input.LA(1);
                if ( LA4_45==24 ) {return s46;}
                if ( LA4_45==EOL||LA4_45==15 ) {return s45;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 45, input);

                throw nvae;
            }
        };
        DFA.State s32 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_32 = input.LA(1);
                if ( LA4_32==EOL||LA4_32==15 ) {return s45;}
                if ( LA4_32==24 ) {return s46;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 32, input);

                throw nvae;
            }
        };
        DFA.State s81 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                    return s32;

                case 22:
                    return s43;

                case EOL:
                case 15:
                    return s81;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 81, input);

                    throw nvae;        }
            }
        };
        DFA.State s68 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s81;

                case 23:
                    return s32;

                case 22:
                    return s43;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 68, input);

                    throw nvae;        }
            }
        };
        DFA.State s67 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                    return s32;

                case 22:
                    return s43;

                case EOL:
                case 15:
                    return s67;

                case ID:
                    return s68;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 67, input);

                    throw nvae;        }
            }
        };
        DFA.State s55 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 49:
                    return s16;

                case EOL:
                case 15:
                    return s67;

                case ID:
                    return s68;

                case 23:
                    return s32;

                case 22:
                    return s43;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 55, input);

                    throw nvae;        }
            }
        };
        DFA.State s54 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_54 = input.LA(1);
                if ( LA4_54==ID ) {return s55;}
                if ( LA4_54==EOL||LA4_54==15 ) {return s54;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 54, input);

                throw nvae;
            }
        };
        DFA.State s43 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_43 = input.LA(1);
                if ( LA4_43==EOL||LA4_43==15 ) {return s54;}
                if ( LA4_43==ID ) {return s55;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 43, input);

                throw nvae;
            }
        };
        DFA.State s51 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 22:
                    return s43;

                case 23:
                    return s32;

                case EOL:
                case 15:
                    return s51;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 51, input);

                    throw nvae;        }
            }
        };
        DFA.State s42 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s51;

                case 22:
                    return s43;

                case 23:
                    return s32;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 42, input);

                    throw nvae;        }
            }
        };
        DFA.State s41 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 22:
                    return s43;

                case 23:
                    return s32;

                case EOL:
                case 15:
                    return s41;

                case ID:
                    return s42;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 41, input);

                    throw nvae;        }
            }
        };
        DFA.State s31 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 49:
                    return s16;

                case EOL:
                case 15:
                    return s41;

                case ID:
                    return s42;

                case 22:
                    return s43;

                case 23:
                    return s32;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 31, input);

                    throw nvae;        }
            }
        };
        DFA.State s30 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s31;

                case EOL:
                case 15:
                    return s30;

                case 23:
                    return s32;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 30, input);

                    throw nvae;        }
            }
        };
        DFA.State s23 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s30;

                case ID:
                    return s31;

                case 23:
                    return s32;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 23, input);

                    throw nvae;        }
            }
        };
        DFA.State s28 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_28 = input.LA(1);
                if ( LA4_28==21 ) {return s23;}
                if ( LA4_28==EOL||LA4_28==15 ) {return s28;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 28, input);

                throw nvae;
            }
        };
        DFA.State s22 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_22 = input.LA(1);
                if ( LA4_22==EOL||LA4_22==15 ) {return s28;}
                if ( LA4_22==21 ) {return s23;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 22, input);

                throw nvae;
            }
        };
        DFA.State s21 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s22;

                case EOL:
                case 15:
                    return s21;

                case 21:
                    return s23;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 21, input);

                    throw nvae;        }
            }
        };
        DFA.State s13 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 49:
                    return s16;

                case EOL:
                case 15:
                    return s21;

                case ID:
                    return s22;

                case 21:
                    return s23;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 13, input);

                    throw nvae;        }
            }
        };
        DFA.State s12 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_12 = input.LA(1);
                if ( LA4_12==ID ) {return s13;}
                if ( LA4_12==EOL||LA4_12==15 ) {return s12;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 12, input);

                throw nvae;
            }
        };
        DFA.State s6 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_6 = input.LA(1);
                if ( LA4_6==EOL||LA4_6==15 ) {return s12;}
                if ( LA4_6==ID ) {return s13;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 6, input);

                throw nvae;
            }
        };
        DFA.State s8 = new DFA.State() {{alt=2;}};
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case -1:
                case EOL:
                case 15:
                case 26:
                case 28:
                    return s1;

                case 17:
                    return s4;

                case 19:
                    return s5;

                case 20:
                    return s6;

                case 18:
                    return s8;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA10 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s3;

                case EOL:
                case 15:
                    return s4;

                case 21:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 4, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                case 49:
                    return s3;

                case EOL:
                case 15:
                    return s4;

                case 21:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 10, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA10_0 = input.LA(1);
                if ( LA10_0==ID ) {return s1;}
                if ( LA10_0==EOL||LA10_0==15 ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
        };

    }class DFA11 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s3;

                case EOL:
                case 15:
                    return s4;

                case 22:
                case 23:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 4, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                case 49:
                    return s3;

                case EOL:
                case 15:
                    return s4;

                case 22:
                case 23:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 11, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA11_0 = input.LA(1);
                if ( LA11_0==ID ) {return s1;}
                if ( LA11_0==EOL||LA11_0==15 ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
        };

    }class DFA12 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s6 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s6;

                case EOL:
                case 15:
                    return s3;

                case 22:
                case 23:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 3, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s3;

                case 22:
                case 23:
                    return s2;

                case ID:
                case 49:
                    return s6;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 12, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA12_0 = input.LA(1);
                if ( LA12_0==ID ) {return s1;}
                if ( LA12_0==EOL||LA12_0==15 ) {return s2;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }
        };

    }class DFA42 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s5 = new DFA.State() {{alt=1;}};
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 30:
                    return s5;

                case EOL:
                case 15:
                    return s3;

                case 21:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 42, 3, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 21:
                case 49:
                    return s2;

                case EOL:
                case 15:
                    return s3;

                case 30:
                    return s5;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 42, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA42_0 = input.LA(1);
                if ( LA42_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
        };

    }class DFA44 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 23:
                    return s3;

                case EOL:
                case 15:
                    return s1;

                case ID:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 44, 1, input);

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

                case 23:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 44, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA47 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 22:
                    return s3;

                case EOL:
                case 15:
                    return s1;

                case 23:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s1;

                case 23:
                    return s2;

                case 22:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA48 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s6 = new DFA.State() {{alt=1;}};
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 30:
                    return s6;

                case EOL:
                case 15:
                    return s2;

                case 22:
                case 23:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                    return s3;

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

                case 22:
                case 23:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                    return s3;

                case 30:
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

    }


    public static final BitSet FOLLOW_set_in_opt_eol41 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_compilation_unit57 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_prolog_in_compilation_unit61 = new BitSet(new long[]{0x00000000001A8012L});
    public static final BitSet FOLLOW_rule_in_compilation_unit70 = new BitSet(new long[]{0x00000000001A8012L});
    public static final BitSet FOLLOW_query_in_compilation_unit83 = new BitSet(new long[]{0x00000000001A8012L});
    public static final BitSet FOLLOW_extra_statement_in_compilation_unit91 = new BitSet(new long[]{0x00000000001A8012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog115 = new BitSet(new long[]{0x00000000001F8012L});
    public static final BitSet FOLLOW_package_statement_in_prolog123 = new BitSet(new long[]{0x00000000001E8012L});
    public static final BitSet FOLLOW_extra_statement_in_prolog138 = new BitSet(new long[]{0x00000000001E8012L});
    public static final BitSet FOLLOW_expander_in_prolog144 = new BitSet(new long[]{0x00000000001E8012L});
    public static final BitSet FOLLOW_opt_eol_in_prolog156 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_package_statement180 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement182 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_package_statement186 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_package_statement188 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_package_statement191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_import_statement207 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement209 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_import_statement213 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_import_statement215 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_import_statement218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_expander240 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_dotted_name_in_expander245 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_expander249 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_expander252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_global276 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_dotted_name_in_global280 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_global284 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_global286 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_global289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_function313 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function315 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_dotted_name_in_function320 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function324 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function328 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function330 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_function339 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function341 = new BitSet(new long[]{0x0000000000808032L});
    public static final BitSet FOLLOW_dotted_name_in_function351 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function355 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function359 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function361 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_22_in_function375 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function377 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_dotted_name_in_function382 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function386 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function390 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function392 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_23_in_function417 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function421 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_function425 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_curly_chunk_in_function432 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_function441 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function449 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_query473 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_query479 = new BitSet(new long[]{0x02000006BC020120L});
    public static final BitSet FOLLOW_word_in_query483 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_query485 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_query501 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query509 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_query524 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_query526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_rule549 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_rule555 = new BitSet(new long[]{0x02000006BC020120L});
    public static final BitSet FOLLOW_word_in_rule559 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule561 = new BitSet(new long[]{0x0000000140008012L});
    public static final BitSet FOLLOW_rule_attributes_in_rule572 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule582 = new BitSet(new long[]{0x0000000028008012L});
    public static final BitSet FOLLOW_29_in_rule591 = new BitSet(new long[]{0x0000000040008012L});
    public static final BitSet FOLLOW_30_in_rule593 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule596 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_expander_lhs_block_in_rule614 = new BitSet(new long[]{0x0000000008008012L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule623 = new BitSet(new long[]{0x0000000008008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule646 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_rule650 = new BitSet(new long[]{0x0000000040008012L});
    public static final BitSet FOLLOW_30_in_rule652 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule656 = new BitSet(new long[]{0x03FFFFFFFFFFFFF0L});
    public static final BitSet FOLLOW_27_in_rule702 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_extra_statement720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_extra_statement725 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_extra_statement730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_rule_attributes749 = new BitSet(new long[]{0x0000000040008012L});
    public static final BitSet FOLLOW_30_in_rule_attributes752 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes755 = new BitSet(new long[]{0x0000007E00400002L});
    public static final BitSet FOLLOW_22_in_rule_attributes762 = new BitSet(new long[]{0x0000007E00000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes767 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes769 = new BitSet(new long[]{0x0000007E00400002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_xor_group_in_rule_attribute856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_salience900 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_salience902 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_salience906 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_salience908 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_salience911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_no_loop946 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop948 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_no_loop950 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop953 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_no_loop978 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_BOOL_in_no_loop982 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop984 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_no_loop986 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_auto_focus1035 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1037 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_auto_focus1039 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_auto_focus1067 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1071 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1073 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_auto_focus1075 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_xor_group1120 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_xor_group1122 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_xor_group1126 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_xor_group1128 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_xor_group1131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_agenda_group1160 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_agenda_group1162 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1166 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_agenda_group1168 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_agenda_group1171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_duration1203 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_duration1205 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_duration1209 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_duration1211 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_duration1214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1240 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_normal_lhs_block1242 = new BitSet(new long[]{0x01C0000000208032L});
    public static final BitSet FOLLOW_opt_eol_in_normal_lhs_block1254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expander_lhs_block1295 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block1299 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_or_in_lhs1351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1388 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding1420 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1430 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_fact_binding1432 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1434 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding1442 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding1444 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_39_in_fact_binding1456 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_binding1470 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_dotted_name_in_fact1510 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1518 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_fact1526 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1529 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_constraints_in_fact1535 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1554 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_fact1556 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact1558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1583 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_constraint_in_constraints1588 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_predicate_in_constraints1591 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1599 = new BitSet(new long[]{0x0000000000400000L});
    public static final BitSet FOLLOW_22_in_constraints1601 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1603 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_constraint_in_constraints1606 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_predicate_in_constraints1609 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints1617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1636 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1644 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1646 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_constraint1648 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1650 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint1660 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1670 = new BitSet(new long[]{0x0000FF0000008012L});
    public static final BitSet FOLLOW_set_in_constraint1678 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1750 = new BitSet(new long[]{0x00010000002003E0L});
    public static final BitSet FOLLOW_ID_in_constraint1768 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint1793 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint1825 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint1845 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint1878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint1905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint1916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint1929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint1940 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_literal_constraint1952 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint1983 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_enum_constraint1985 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_enum_constraint1989 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_retval_constraint2018 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint2022 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_retval_constraint2024 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_predicate2042 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_predicate2044 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_predicate2048 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_predicate2050 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_predicate2052 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_predicate2056 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_predicate2058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_paren_chunk2104 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk2108 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_paren_chunk2110 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_24_in_curly_chunk2179 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk2183 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_curly_chunk2185 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2243 = new BitSet(new long[]{0x0008008000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or2252 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_or2257 = new BitSet(new long[]{0x01C0000000200020L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or2264 = new BitSet(new long[]{0x0008008000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2304 = new BitSet(new long[]{0x0030000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and2313 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_and2318 = new BitSet(new long[]{0x01C0000000200020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and2325 = new BitSet(new long[]{0x0030000000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary2363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary2371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary2379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary2387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_lhs_unary2393 = new BitSet(new long[]{0x01C0000000200020L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary2397 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_lhs_unary2399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_lhs_exist2429 = new BitSet(new long[]{0x0000000000200020L});
    public static final BitSet FOLLOW_21_in_lhs_exist2432 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist2436 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_lhs_exist2438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist2444 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_55_in_lhs_not2474 = new BitSet(new long[]{0x0000000000200020L});
    public static final BitSet FOLLOW_21_in_lhs_not2477 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not2481 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_lhs_not2484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not2490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_lhs_eval2516 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_21_in_lhs_eval2518 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_eval2520 = new BitSet(new long[]{0x03FFFFFFFFFFFFF2L});
    public static final BitSet FOLLOW_paren_chunk_in_lhs_eval2524 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_lhs_eval2526 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name2558 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_49_in_dotted_name2564 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_dotted_name2568 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_ID_in_word2598 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_word2610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_word2619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_word2631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_word2642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_word2652 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_word2660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_word2668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_word2679 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_word2690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word2704 = new BitSet(new long[]{0x0000000000000002L});

}