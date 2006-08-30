// $ANTLR 3.0ea8 /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g 2006-08-30 17:14:27

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "EOL", "ID", "INT", "BOOL", "STRING", "FLOAT", "MISC", "WS", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "MULTI_LINE_COMMENT", "\';\'", "\'package\'", "\'import\'", "\'function\'", "\'.\'", "\'.*\'", "\'expander\'", "\'global\'", "\'(\'", "\',\'", "\')\'", "\'{\'", "\'}\'", "\'query\'", "\'end\'", "\'template\'", "\'rule\'", "\'when\'", "\':\'", "\'then\'", "\'attributes\'", "\'salience\'", "\'no-loop\'", "\'auto-focus\'", "\'activation-group\'", "\'agenda-group\'", "\'duration\'", "\'from\'", "\'null\'", "\'=>\'", "\'or\'", "\'||\'", "\'&\'", "\'|\'", "\'->\'", "\'and\'", "\'&&\'", "\'exists\'", "\'not\'", "\'eval\'", "\'[\'", "\']\'", "\'use\'", "\'==\'", "\'=\'", "\'>\'", "\'>=\'", "\'<\'", "\'<=\'", "\'!=\'", "\'contains\'", "\'matches\'", "\'excludes\'"
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
          



    // $ANTLR start opt_eol
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:282:1: opt_eol : ( (';'|EOL))* ;
    public void opt_eol() throws RecognitionException {   
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:283:17: ( ( (';'|EOL))* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:283:17: ( (';'|EOL))*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:283:17: ( (';'|EOL))*
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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:283:18: (';'|EOL)
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:286:1: compilation_unit : opt_eol prolog (r= rule | q= query | t= template | extra_statement )* ;
    public void compilation_unit() throws RecognitionException {   
        RuleDescr r = null;

        QueryDescr q = null;

        FactTemplateDescr t = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:287:17: ( opt_eol prolog (r= rule | q= query | t= template | extra_statement )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:287:17: opt_eol prolog (r= rule | q= query | t= template | extra_statement )*
            {
            following.push(FOLLOW_opt_eol_in_compilation_unit57);
            opt_eol();
            following.pop();

            following.push(FOLLOW_prolog_in_compilation_unit61);
            prolog();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:289:17: (r= rule | q= query | t= template | extra_statement )*
            loop2:
            do {
                int alt2=5;
                alt2 = dfa2.predict(input); 
                switch (alt2) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:289:25: r= rule
            	    {
            	    following.push(FOLLOW_rule_in_compilation_unit70);
            	    r=rule();
            	    following.pop();

            	    this.packageDescr.addRule( r ); 

            	    }
            	    break;
            	case 2 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:290:25: q= query
            	    {
            	    following.push(FOLLOW_query_in_compilation_unit83);
            	    q=query();
            	    following.pop();

            	    this.packageDescr.addRule( q ); 

            	    }
            	    break;
            	case 3 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:291:25: t= template
            	    {
            	    following.push(FOLLOW_template_in_compilation_unit93);
            	    t=template();
            	    following.pop();

            	    this.packageDescr.addFactTemplate ( t ); 

            	    }
            	    break;
            	case 4 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:292:25: extra_statement
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:296:1: prolog : opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol ;
    public void prolog() throws RecognitionException {   
        String name = null;



        		String packageName = "";
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:300:17: ( opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:300:17: opt_eol (name= package_statement )? ( extra_statement | expander )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_prolog125);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:301:17: (name= package_statement )?
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
                    new NoViableAltException("301:17: (name= package_statement )?", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:301:19: name= package_statement
                    {
                    following.push(FOLLOW_package_statement_in_prolog133);
                    name=package_statement();
                    following.pop();

                     packageName = name; 

                    }
                    break;

            }

             
            			this.packageDescr = new PackageDescr( name ); 
            		
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:305:17: ( extra_statement | expander )*
            loop4:
            do {
                int alt4=3;
                alt4 = dfa4.predict(input); 
                switch (alt4) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:305:25: extra_statement
            	    {
            	    following.push(FOLLOW_extra_statement_in_prolog148);
            	    extra_statement();
            	    following.pop();


            	    }
            	    break;
            	case 2 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:306:25: expander
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:312:1: package_statement returns [String packageName] : 'package' opt_eol name= dotted_name ( ';' )? opt_eol ;
    public String package_statement() throws RecognitionException {   
        String packageName;
        String name = null;



        		packageName = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:317:17: ( 'package' opt_eol name= dotted_name ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:317:17: 'package' opt_eol name= dotted_name ( ';' )? opt_eol
            {
            match(input,16,FOLLOW_16_in_package_statement190); 
            following.push(FOLLOW_opt_eol_in_package_statement192);
            opt_eol();
            following.pop();

            following.push(FOLLOW_dotted_name_in_package_statement196);
            name=dotted_name();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:317:52: ( ';' )?
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
                    new NoViableAltException("317:52: ( \';\' )?", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:317:52: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:323:1: import_statement : 'import' opt_eol name= import_name ( ';' )? opt_eol ;
    public void import_statement() throws RecognitionException {   
        String name = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:324:17: ( 'import' opt_eol name= import_name ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:324:17: 'import' opt_eol name= import_name ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_import_statement217); 
            following.push(FOLLOW_opt_eol_in_import_statement219);
            opt_eol();
            following.pop();

            following.push(FOLLOW_import_name_in_import_statement223);
            name=import_name();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:324:51: ( ';' )?
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
                    new NoViableAltException("324:51: ( \';\' )?", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:324:51: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:331:1: function_import_statement : 'import' 'function' opt_eol name= import_name ( ';' )? opt_eol ;
    public void function_import_statement() throws RecognitionException {   
        String name = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:332:17: ( 'import' 'function' opt_eol name= import_name ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:332:17: 'import' 'function' opt_eol name= import_name ( ';' )? opt_eol
            {
            match(input,17,FOLLOW_17_in_function_import_statement244); 
            match(input,18,FOLLOW_18_in_function_import_statement246); 
            following.push(FOLLOW_opt_eol_in_function_import_statement248);
            opt_eol();
            following.pop();

            following.push(FOLLOW_import_name_in_function_import_statement252);
            name=import_name();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:332:62: ( ';' )?
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
                    new NoViableAltException("332:62: ( \';\' )?", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:332:62: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:340:1: import_name returns [String name] : id= ID ( '.' id= ID )* (star= '.*' )? ;
    public String import_name() throws RecognitionException {   
        String name;
        Token id=null;
        Token star=null;


        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:17: (id= ID ( '.' id= ID )* (star= '.*' )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:17: id= ID ( '.' id= ID )* (star= '.*' )?
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_import_name289); 
             name=id.getText(); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:46: ( '.' id= ID )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);
                if ( LA8_0==19 ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:48: '.' id= ID
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

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:99: (star= '.*' )?
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
                    new NoViableAltException("345:99: (star= \'.*\' )?", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:345:100: star= '.*'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:347:1: expander : 'expander' (name= dotted_name )? ( ';' )? opt_eol ;
    public void expander() throws RecognitionException {   
        String name = null;



        		String config=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:351:17: ( 'expander' (name= dotted_name )? ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:351:17: 'expander' (name= dotted_name )? ( ';' )? opt_eol
            {
            match(input,21,FOLLOW_21_in_expander329); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:351:28: (name= dotted_name )?
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
                    new NoViableAltException("351:28: (name= dotted_name )?", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:351:29: name= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_expander334);
                    name=dotted_name();
                    following.pop();


                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:351:48: ( ';' )?
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
                    new NoViableAltException("351:48: ( \';\' )?", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:351:48: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:361:1: global : 'global' type= dotted_name id= ID ( ';' )? opt_eol ;
    public void global() throws RecognitionException {   
        Token id=null;
        String type = null;



        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:365:17: ( 'global' type= dotted_name id= ID ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:365:17: 'global' type= dotted_name id= ID ( ';' )? opt_eol
            {
            match(input,22,FOLLOW_22_in_global365); 
            following.push(FOLLOW_dotted_name_in_global369);
            type=dotted_name();
            following.pop();

            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_global373); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:365:49: ( ';' )?
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
                    new NoViableAltException("365:49: ( \';\' )?", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:365:49: ';'
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:371:1: function : 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol ;
    public void function() throws RecognitionException {   
        Token name=null;
        String retType = null;

        String paramType = null;

        String paramName = null;

        String body = null;



        		FunctionDescr f = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:376:17: ( 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:376:17: 'function' opt_eol (retType= dotted_name )? opt_eol name= ID opt_eol '(' opt_eol ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )? ')' opt_eol '{' body= curly_chunk '}' opt_eol
            {
            match(input,18,FOLLOW_18_in_function402); 
            following.push(FOLLOW_opt_eol_in_function404);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:376:36: (retType= dotted_name )?
            int alt13=2;
            alt13 = dfa13.predict(input); 
            switch (alt13) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:376:37: retType= dotted_name
                    {
                    following.push(FOLLOW_dotted_name_in_function409);
                    retType=dotted_name();
                    following.pop();


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_function413);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function417); 
            following.push(FOLLOW_opt_eol_in_function419);
            opt_eol();
            following.pop();


            			//System.err.println( "function :: " + name.getText() );
            			f = new FunctionDescr( name.getText(), retType );
            		
            match(input,23,FOLLOW_23_in_function428); 
            following.push(FOLLOW_opt_eol_in_function430);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:382:25: ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )?
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
                    new NoViableAltException("382:25: ( (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( \',\' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )* )?", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:382:33: (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )*
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:382:33: (paramType= dotted_name )?
                    int alt14=2;
                    alt14 = dfa14.predict(input); 
                    switch (alt14) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:382:34: paramType= dotted_name
                            {
                            following.push(FOLLOW_dotted_name_in_function440);
                            paramType=dotted_name();
                            following.pop();


                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_function444);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_argument_name_in_function448);
                    paramName=argument_name();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_function450);
                    opt_eol();
                    following.pop();


                    					f.addParameter( paramType, paramName );
                    				
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:386:33: ( ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol )*
                    loop16:
                    do {
                        int alt16=2;
                        int LA16_0 = input.LA(1);
                        if ( LA16_0==24 ) {
                            alt16=1;
                        }


                        switch (alt16) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:386:41: ',' opt_eol (paramType= dotted_name )? opt_eol paramName= argument_name opt_eol
                    	    {
                    	    match(input,24,FOLLOW_24_in_function464); 
                    	    following.push(FOLLOW_opt_eol_in_function466);
                    	    opt_eol();
                    	    following.pop();

                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:386:53: (paramType= dotted_name )?
                    	    int alt15=2;
                    	    alt15 = dfa15.predict(input); 
                    	    switch (alt15) {
                    	        case 1 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:386:54: paramType= dotted_name
                    	            {
                    	            following.push(FOLLOW_dotted_name_in_function471);
                    	            paramType=dotted_name();
                    	            following.pop();


                    	            }
                    	            break;

                    	    }

                    	    following.push(FOLLOW_opt_eol_in_function475);
                    	    opt_eol();
                    	    following.pop();

                    	    following.push(FOLLOW_argument_name_in_function479);
                    	    paramName=argument_name();
                    	    following.pop();

                    	    following.push(FOLLOW_opt_eol_in_function481);
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

            match(input,25,FOLLOW_25_in_function506); 
            following.push(FOLLOW_opt_eol_in_function510);
            opt_eol();
            following.pop();

            match(input,26,FOLLOW_26_in_function514); 
            following.push(FOLLOW_curly_chunk_in_function521);
            body=curly_chunk();
            following.pop();


            				f.setText( body );
            			
            match(input,27,FOLLOW_27_in_function530); 

            			packageDescr.addFunction( f );
            		
            following.push(FOLLOW_opt_eol_in_function538);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:407:1: query returns [QueryDescr query] : opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol ;
    public QueryDescr query() throws RecognitionException {   
        QueryDescr query;
        Token loc=null;
        String queryName = null;



        		query = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:412:17: ( opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:412:17: opt_eol loc= 'query' queryName= word opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_query562);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,28,FOLLOW_28_in_query568); 
            following.push(FOLLOW_word_in_query572);
            queryName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_query574);
            opt_eol();
            following.pop();

             
            			query = new QueryDescr( queryName, null ); 
            			query.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            			AndDescr lhs = new AndDescr(); query.setLhs( lhs ); 
            			lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:420:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
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
                        new NoViableAltException("420:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 1, input);

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
                        new NoViableAltException("420:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 2, input);

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
                        new NoViableAltException("420:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 3, input);

                    throw nvae;
                }
                break;
            case 52:
                int LA18_4 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("420:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 4, input);

                    throw nvae;
                }
                break;
            case 53:
                int LA18_5 = input.LA(2);
                if (  expander != null  ) {
                    alt18=1;
                }
                else if ( true ) {
                    alt18=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("420:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 5, input);

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
                        new NoViableAltException("420:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 6, input);

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
                        new NoViableAltException("420:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 7, input);

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
                        new NoViableAltException("420:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 8, input);

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
                alt18=1;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("420:17: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:421:25: {...}? expander_lhs_block[lhs]
                    {
                    if ( !( expander != null ) ) {
                        throw new FailedPredicateException(input, "query", " expander != null ");
                    }
                    following.push(FOLLOW_expander_lhs_block_in_query590);
                    expander_lhs_block(lhs);
                    following.pop();


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:422:27: normal_lhs_block[lhs]
                    {
                    following.push(FOLLOW_normal_lhs_block_in_query598);
                    normal_lhs_block(lhs);
                    following.pop();


                    }
                    break;

            }

            match(input,29,FOLLOW_29_in_query613); 
            following.push(FOLLOW_opt_eol_in_query615);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:428:1: template returns [FactTemplateDescr template] : opt_eol loc= 'template' templateName= ID EOL (slot= template_slot )+ 'end' EOL ;
    public FactTemplateDescr template() throws RecognitionException {   
        FactTemplateDescr template;
        Token loc=null;
        Token templateName=null;
        FieldTemplateDescr slot = null;



        		template = null;		
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:433:17: ( opt_eol loc= 'template' templateName= ID EOL (slot= template_slot )+ 'end' EOL )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:433:17: opt_eol loc= 'template' templateName= ID EOL (slot= template_slot )+ 'end' EOL
            {
            following.push(FOLLOW_opt_eol_in_template639);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,30,FOLLOW_30_in_template645); 
            templateName=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template649); 
            match(input,EOL,FOLLOW_EOL_in_template651); 

            			template = new FactTemplateDescr(templateName.getText());
            			template.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );			
            		
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:439:17: (slot= template_slot )+
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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:440:25: slot= template_slot
            	    {
            	    following.push(FOLLOW_template_slot_in_template666);
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

            match(input,29,FOLLOW_29_in_template681); 
            match(input,EOL,FOLLOW_EOL_in_template683); 

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:448:1: template_slot returns [FieldTemplateDescr field] : fieldType= dotted_name name= ID (EOL|';');
    public FieldTemplateDescr template_slot() throws RecognitionException {   
        FieldTemplateDescr field;
        Token name=null;
        String fieldType = null;



        		field = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:454:18: (fieldType= dotted_name name= ID (EOL|';'))
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:454:18: fieldType= dotted_name name= ID (EOL|';')
            {
            following.push(FOLLOW_dotted_name_in_template_slot715);
            fieldType=dotted_name();
            following.pop();

            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_template_slot719); 
            if ( input.LA(1)==EOL||input.LA(1)==15 ) {
                input.consume();
                errorRecovery=false;
            }
            else {
                MismatchedSetException mse =
                    new MismatchedSetException(null,input);
                recoverFromMismatchedSet(input,mse,FOLLOW_set_in_template_slot723);    throw mse;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:463:1: rule returns [RuleDescr rule] : opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )? 'end' opt_eol ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule;
        Token loc=null;
        Token any=null;
        String ruleName = null;



        		rule = null;
        		String consequence = "";
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:469:17: ( opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )? 'end' opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:469:17: opt_eol loc= 'rule' ruleName= word opt_eol ( rule_attributes[rule] )? opt_eol ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )? 'end' opt_eol
            {
            following.push(FOLLOW_opt_eol_in_rule758);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,31,FOLLOW_31_in_rule764); 
            following.push(FOLLOW_word_in_rule768);
            ruleName=word();
            following.pop();

            following.push(FOLLOW_opt_eol_in_rule770);
            opt_eol();
            following.pop();

             
            			debug( "start rule: " + ruleName );
            			rule = new RuleDescr( ruleName, null ); 
            			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            		
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:476:17: ( rule_attributes[rule] )?
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
                    new NoViableAltException("476:17: ( rule_attributes[rule] )?", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:476:25: rule_attributes[rule]
                    {
                    following.push(FOLLOW_rule_attributes_in_rule781);
                    rule_attributes(rule);
                    following.pop();


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule791);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:479:17: ( (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )? )?
            int alt27=2;
            int LA27_0 = input.LA(1);
            if ( LA27_0==EOL||LA27_0==15||LA27_0==32||LA27_0==34 ) {
                alt27=1;
            }
            else if ( LA27_0==29 ) {
                alt27=1;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("479:17: ( (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= \'then\' ( \':\' )? opt_eol ( options {greedy=false; } : any= . )* )? )?", 27, 0, input);

                throw nvae;
            }
            switch (alt27) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:479:18: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )? ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )?
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:479:18: (loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?
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
                            new NoViableAltException("479:18: (loc= \'when\' ( \':\' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] ) )?", 23, 0, input);

                        throw nvae;
                    }
                    switch (alt23) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:479:25: loc= 'when' ( ':' )? opt_eol ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
                            {
                            loc=(Token)input.LT(1);
                            match(input,32,FOLLOW_32_in_rule800); 
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:479:36: ( ':' )?
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
                                        new NoViableAltException("479:36: ( \':\' )?", 21, 1, input);

                                    throw nvae;
                                }
                            }
                            else if ( (LA21_0>=EOL && LA21_0<=32)||(LA21_0>=34 && LA21_0<=67) ) {
                                alt21=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("479:36: ( \':\' )?", 21, 0, input);

                                throw nvae;
                            }
                            switch (alt21) {
                                case 1 :
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:479:36: ':'
                                    {
                                    match(input,33,FOLLOW_33_in_rule802); 

                                    }
                                    break;

                            }

                            following.push(FOLLOW_opt_eol_in_rule805);
                            opt_eol();
                            following.pop();

                             
                            				AndDescr lhs = new AndDescr(); rule.setLhs( lhs ); 
                            				lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                            			
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:484:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )
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
                                        new NoViableAltException("484:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 1, input);

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
                                        new NoViableAltException("484:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 2, input);

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
                                        new NoViableAltException("484:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 3, input);

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
                                        new NoViableAltException("484:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 4, input);

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
                                        new NoViableAltException("484:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 5, input);

                                    throw nvae;
                                }
                                break;
                            case 52:
                                int LA22_6 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("484:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 6, input);

                                    throw nvae;
                                }
                                break;
                            case 53:
                                int LA22_7 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("484:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 7, input);

                                    throw nvae;
                                }
                                break;
                            case 54:
                                int LA22_8 = input.LA(2);
                                if (  expander != null  ) {
                                    alt22=1;
                                }
                                else if ( true ) {
                                    alt22=2;
                                }
                                else {
                                    NoViableAltException nvae =
                                        new NoViableAltException("484:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 8, input);

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
                                        new NoViableAltException("484:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 9, input);

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
                                alt22=1;
                                break;
                            default:
                                NoViableAltException nvae =
                                    new NoViableAltException("484:25: ({...}? expander_lhs_block[lhs] | normal_lhs_block[lhs] )", 22, 0, input);

                                throw nvae;
                            }

                            switch (alt22) {
                                case 1 :
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:485:33: {...}? expander_lhs_block[lhs]
                                    {
                                    if ( !( expander != null ) ) {
                                        throw new FailedPredicateException(input, "rule", " expander != null ");
                                    }
                                    following.push(FOLLOW_expander_lhs_block_in_rule823);
                                    expander_lhs_block(lhs);
                                    following.pop();


                                    }
                                    break;
                                case 2 :
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:486:35: normal_lhs_block[lhs]
                                    {
                                    following.push(FOLLOW_normal_lhs_block_in_rule832);
                                    normal_lhs_block(lhs);
                                    following.pop();


                                    }
                                    break;

                            }


                            }
                            break;

                    }

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:490:17: ( opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )* )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);
                    if ( LA26_0==EOL||LA26_0==15||LA26_0==34 ) {
                        alt26=1;
                    }
                    else if ( LA26_0==29 ) {
                        alt26=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("490:17: ( opt_eol loc= \'then\' ( \':\' )? opt_eol ( options {greedy=false; } : any= . )* )?", 26, 0, input);

                        throw nvae;
                    }
                    switch (alt26) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:490:19: opt_eol loc= 'then' ( ':' )? opt_eol ( options {greedy=false; } : any= . )*
                            {
                            following.push(FOLLOW_opt_eol_in_rule855);
                            opt_eol();
                            following.pop();

                            loc=(Token)input.LT(1);
                            match(input,34,FOLLOW_34_in_rule859); 
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:490:38: ( ':' )?
                            int alt24=2;
                            int LA24_0 = input.LA(1);
                            if ( LA24_0==33 ) {
                                alt24=1;
                            }
                            else if ( (LA24_0>=EOL && LA24_0<=32)||(LA24_0>=34 && LA24_0<=67) ) {
                                alt24=2;
                            }
                            else {
                                NoViableAltException nvae =
                                    new NoViableAltException("490:38: ( \':\' )?", 24, 0, input);

                                throw nvae;
                            }
                            switch (alt24) {
                                case 1 :
                                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:490:38: ':'
                                    {
                                    match(input,33,FOLLOW_33_in_rule861); 

                                    }
                                    break;

                            }

                            following.push(FOLLOW_opt_eol_in_rule865);
                            opt_eol();
                            following.pop();

                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:491:25: ( options {greedy=false; } : any= . )*
                            loop25:
                            do {
                                int alt25=2;
                                int LA25_0 = input.LA(1);
                                if ( LA25_0==29 ) {
                                    alt25=2;
                                }
                                else if ( (LA25_0>=EOL && LA25_0<=28)||(LA25_0>=30 && LA25_0<=67) ) {
                                    alt25=1;
                                }


                                switch (alt25) {
                            	case 1 :
                            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:491:52: any= .
                            	    {
                            	    any=(Token)input.LT(1);
                            	    matchAny(input); 

                            	    					consequence = consequence + " " + any.getText();
                            	    				

                            	    }
                            	    break;

                            	default :
                            	    break loop25;
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

            match(input,29,FOLLOW_29_in_rule911); 
            following.push(FOLLOW_opt_eol_in_rule913);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:512:1: extra_statement : ( import_statement | function_import_statement | global | function ) ;
    public void extra_statement() throws RecognitionException {   
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:514:9: ( ( import_statement | function_import_statement | global | function ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:514:9: ( import_statement | function_import_statement | global | function )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:514:9: ( import_statement | function_import_statement | global | function )
            int alt28=4;
            switch ( input.LA(1) ) {
            case 17:
                int LA28_1 = input.LA(2);
                if ( LA28_1==18 ) {
                    alt28=2;
                }
                else if ( (LA28_1>=EOL && LA28_1<=ID)||LA28_1==15 ) {
                    alt28=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("514:9: ( import_statement | function_import_statement | global | function )", 28, 1, input);

                    throw nvae;
                }
                break;
            case 22:
                alt28=3;
                break;
            case 18:
                alt28=4;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("514:9: ( import_statement | function_import_statement | global | function )", 28, 0, input);

                throw nvae;
            }

            switch (alt28) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:514:17: import_statement
                    {
                    following.push(FOLLOW_import_statement_in_extra_statement933);
                    import_statement();
                    following.pop();


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:515:17: function_import_statement
                    {
                    following.push(FOLLOW_function_import_statement_in_extra_statement938);
                    function_import_statement();
                    following.pop();


                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:516:17: global
                    {
                    following.push(FOLLOW_global_in_extra_statement943);
                    global();
                    following.pop();


                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:517:17: function
                    {
                    following.push(FOLLOW_function_in_extra_statement948);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:521:1: rule_attributes[RuleDescr rule] : ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* ;
    public void rule_attributes(RuleDescr rule) throws RecognitionException {   
        AttributeDescr a = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:523:25: ( ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:523:25: ( 'attributes' )? ( ':' )? opt_eol ( ( ',' )? a= rule_attribute opt_eol )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:523:25: ( 'attributes' )?
            int alt29=2;
            int LA29_0 = input.LA(1);
            if ( LA29_0==35 ) {
                alt29=1;
            }
            else if ( LA29_0==EOL||LA29_0==15||LA29_0==24||LA29_0==29||(LA29_0>=32 && LA29_0<=34)||(LA29_0>=36 && LA29_0<=41) ) {
                alt29=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("523:25: ( \'attributes\' )?", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:523:25: 'attributes'
                    {
                    match(input,35,FOLLOW_35_in_rule_attributes967); 

                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:523:39: ( ':' )?
            int alt30=2;
            int LA30_0 = input.LA(1);
            if ( LA30_0==33 ) {
                alt30=1;
            }
            else if ( LA30_0==EOL||LA30_0==15||LA30_0==24||LA30_0==29||LA30_0==32||LA30_0==34||(LA30_0>=36 && LA30_0<=41) ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("523:39: ( \':\' )?", 30, 0, input);

                throw nvae;
            }
            switch (alt30) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:523:39: ':'
                    {
                    match(input,33,FOLLOW_33_in_rule_attributes970); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_rule_attributes973);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:524:25: ( ( ',' )? a= rule_attribute opt_eol )*
            loop32:
            do {
                int alt32=2;
                int LA32_0 = input.LA(1);
                if ( LA32_0==24||(LA32_0>=36 && LA32_0<=41) ) {
                    alt32=1;
                }


                switch (alt32) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:524:33: ( ',' )? a= rule_attribute opt_eol
            	    {
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:524:33: ( ',' )?
            	    int alt31=2;
            	    int LA31_0 = input.LA(1);
            	    if ( LA31_0==24 ) {
            	        alt31=1;
            	    }
            	    else if ( (LA31_0>=36 && LA31_0<=41) ) {
            	        alt31=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("524:33: ( \',\' )?", 31, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt31) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:524:33: ','
            	            {
            	            match(input,24,FOLLOW_24_in_rule_attributes980); 

            	            }
            	            break;

            	    }

            	    following.push(FOLLOW_rule_attribute_in_rule_attributes985);
            	    a=rule_attribute();
            	    following.pop();

            	    following.push(FOLLOW_opt_eol_in_rule_attributes987);
            	    opt_eol();
            	    following.pop();


            	    					rule.addAttribute( a );
            	    				

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
    // $ANTLR end rule_attributes


    // $ANTLR start rule_attribute
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:531:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus );
    public AttributeDescr rule_attribute() throws RecognitionException {   
        AttributeDescr d;
        AttributeDescr a = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:536:25: (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus )
            int alt33=6;
            switch ( input.LA(1) ) {
            case 36:
                alt33=1;
                break;
            case 37:
                alt33=2;
                break;
            case 40:
                alt33=3;
                break;
            case 41:
                alt33=4;
                break;
            case 39:
                alt33=5;
                break;
            case 38:
                alt33=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("531:1: rule_attribute returns [AttributeDescr d] : (a= salience | a= no_loop | a= agenda_group | a= duration | a= activation_group | a= auto_focus );", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:536:25: a= salience
                    {
                    following.push(FOLLOW_salience_in_rule_attribute1026);
                    a=salience();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:537:25: a= no_loop
                    {
                    following.push(FOLLOW_no_loop_in_rule_attribute1036);
                    a=no_loop();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:538:25: a= agenda_group
                    {
                    following.push(FOLLOW_agenda_group_in_rule_attribute1047);
                    a=agenda_group();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:539:25: a= duration
                    {
                    following.push(FOLLOW_duration_in_rule_attribute1060);
                    a=duration();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:540:25: a= activation_group
                    {
                    following.push(FOLLOW_activation_group_in_rule_attribute1074);
                    a=activation_group();
                    following.pop();

                     d = a; 

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:541:25: a= auto_focus
                    {
                    following.push(FOLLOW_auto_focus_in_rule_attribute1085);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:545:1: salience returns [AttributeDescr d ] : loc= 'salience' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:550:17: (loc= 'salience' opt_eol i= INT ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:550:17: loc= 'salience' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,36,FOLLOW_36_in_salience1118); 
            following.push(FOLLOW_opt_eol_in_salience1120);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience1124); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:550:46: ( ';' )?
            int alt34=2;
            int LA34_0 = input.LA(1);
            if ( LA34_0==15 ) {
                alt34=1;
            }
            else if ( LA34_0==EOL||LA34_0==24||LA34_0==29||LA34_0==32||LA34_0==34||(LA34_0>=36 && LA34_0<=41) ) {
                alt34=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("550:46: ( \';\' )?", 34, 0, input);

                throw nvae;
            }
            switch (alt34) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:550:46: ';'
                    {
                    match(input,15,FOLLOW_15_in_salience1126); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_salience1129);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:557:1: no_loop returns [AttributeDescr d] : ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr no_loop() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:562:17: ( (loc= 'no-loop' opt_eol ( ';' )? opt_eol ) | (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol ) )
            int alt37=2;
            int LA37_0 = input.LA(1);
            if ( LA37_0==37 ) {
                int LA37_1 = input.LA(2);
                if ( LA37_1==BOOL ) {
                    alt37=2;
                }
                else if ( LA37_1==EOL||LA37_1==15||LA37_1==24||LA37_1==29||LA37_1==32||LA37_1==34||(LA37_1>=36 && LA37_1<=41) ) {
                    alt37=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("557:1: no_loop returns [AttributeDescr d] : ( (loc= \'no-loop\' opt_eol ( \';\' )? opt_eol ) | (loc= \'no-loop\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 37, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("557:1: no_loop returns [AttributeDescr d] : ( (loc= \'no-loop\' opt_eol ( \';\' )? opt_eol ) | (loc= \'no-loop\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:562:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:562:17: (loc= 'no-loop' opt_eol ( ';' )? opt_eol )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:563:25: loc= 'no-loop' opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,37,FOLLOW_37_in_no_loop1164); 
                    following.push(FOLLOW_opt_eol_in_no_loop1166);
                    opt_eol();
                    following.pop();

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:563:47: ( ';' )?
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
                            new NoViableAltException("563:47: ( \';\' )?", 35, 0, input);

                        throw nvae;
                    }
                    switch (alt35) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:563:47: ';'
                            {
                            match(input,15,FOLLOW_15_in_no_loop1168); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_no_loop1171);
                    opt_eol();
                    following.pop();


                    				d = new AttributeDescr( "no-loop", "true" );
                    				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:570:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:570:17: (loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:571:25: loc= 'no-loop' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,37,FOLLOW_37_in_no_loop1196); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_no_loop1200); 
                    following.push(FOLLOW_opt_eol_in_no_loop1202);
                    opt_eol();
                    following.pop();

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:571:54: ( ';' )?
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
                            new NoViableAltException("571:54: ( \';\' )?", 36, 0, input);

                        throw nvae;
                    }
                    switch (alt36) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:571:54: ';'
                            {
                            match(input,15,FOLLOW_15_in_no_loop1204); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_no_loop1207);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:581:1: auto_focus returns [AttributeDescr d] : ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) );
    public AttributeDescr auto_focus() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token t=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:586:17: ( (loc= 'auto-focus' opt_eol ( ';' )? opt_eol ) | (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol ) )
            int alt40=2;
            int LA40_0 = input.LA(1);
            if ( LA40_0==38 ) {
                int LA40_1 = input.LA(2);
                if ( LA40_1==BOOL ) {
                    alt40=2;
                }
                else if ( LA40_1==EOL||LA40_1==15||LA40_1==24||LA40_1==29||LA40_1==32||LA40_1==34||(LA40_1>=36 && LA40_1<=41) ) {
                    alt40=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("581:1: auto_focus returns [AttributeDescr d] : ( (loc= \'auto-focus\' opt_eol ( \';\' )? opt_eol ) | (loc= \'auto-focus\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 40, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("581:1: auto_focus returns [AttributeDescr d] : ( (loc= \'auto-focus\' opt_eol ( \';\' )? opt_eol ) | (loc= \'auto-focus\' t= BOOL opt_eol ( \';\' )? opt_eol ) );", 40, 0, input);

                throw nvae;
            }
            switch (alt40) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:586:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:586:17: (loc= 'auto-focus' opt_eol ( ';' )? opt_eol )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:587:25: loc= 'auto-focus' opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_auto_focus1253); 
                    following.push(FOLLOW_opt_eol_in_auto_focus1255);
                    opt_eol();
                    following.pop();

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:587:50: ( ';' )?
                    int alt38=2;
                    int LA38_0 = input.LA(1);
                    if ( LA38_0==15 ) {
                        alt38=1;
                    }
                    else if ( LA38_0==EOL||LA38_0==24||LA38_0==29||LA38_0==32||LA38_0==34||(LA38_0>=36 && LA38_0<=41) ) {
                        alt38=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("587:50: ( \';\' )?", 38, 0, input);

                        throw nvae;
                    }
                    switch (alt38) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:587:50: ';'
                            {
                            match(input,15,FOLLOW_15_in_auto_focus1257); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_auto_focus1260);
                    opt_eol();
                    following.pop();


                    				d = new AttributeDescr( "auto-focus", "true" );
                    				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
                    			

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:594:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:594:17: (loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:595:25: loc= 'auto-focus' t= BOOL opt_eol ( ';' )? opt_eol
                    {
                    loc=(Token)input.LT(1);
                    match(input,38,FOLLOW_38_in_auto_focus1285); 
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_auto_focus1289); 
                    following.push(FOLLOW_opt_eol_in_auto_focus1291);
                    opt_eol();
                    following.pop();

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:595:57: ( ';' )?
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
                            new NoViableAltException("595:57: ( \';\' )?", 39, 0, input);

                        throw nvae;
                    }
                    switch (alt39) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:595:57: ';'
                            {
                            match(input,15,FOLLOW_15_in_auto_focus1293); 

                            }
                            break;

                    }

                    following.push(FOLLOW_opt_eol_in_auto_focus1296);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:605:1: activation_group returns [AttributeDescr d] : loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr activation_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:610:17: (loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:610:17: loc= 'activation-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,39,FOLLOW_39_in_activation_group1338); 
            following.push(FOLLOW_opt_eol_in_activation_group1340);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_activation_group1344); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:610:60: ( ';' )?
            int alt41=2;
            int LA41_0 = input.LA(1);
            if ( LA41_0==15 ) {
                alt41=1;
            }
            else if ( LA41_0==EOL||LA41_0==24||LA41_0==29||LA41_0==32||LA41_0==34||(LA41_0>=36 && LA41_0<=41) ) {
                alt41=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("610:60: ( \';\' )?", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:610:60: ';'
                    {
                    match(input,15,FOLLOW_15_in_activation_group1346); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_activation_group1349);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:617:1: agenda_group returns [AttributeDescr d] : loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol ;
    public AttributeDescr agenda_group() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token name=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:622:17: (loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:622:17: loc= 'agenda-group' opt_eol name= STRING ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,40,FOLLOW_40_in_agenda_group1378); 
            following.push(FOLLOW_opt_eol_in_agenda_group1380);
            opt_eol();
            following.pop();

            name=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_agenda_group1384); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:622:56: ( ';' )?
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
                    new NoViableAltException("622:56: ( \';\' )?", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:622:56: ';'
                    {
                    match(input,15,FOLLOW_15_in_agenda_group1386); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_agenda_group1389);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:630:1: duration returns [AttributeDescr d] : loc= 'duration' opt_eol i= INT ( ';' )? opt_eol ;
    public AttributeDescr duration() throws RecognitionException {   
        AttributeDescr d;
        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:635:17: (loc= 'duration' opt_eol i= INT ( ';' )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:635:17: loc= 'duration' opt_eol i= INT ( ';' )? opt_eol
            {
            loc=(Token)input.LT(1);
            match(input,41,FOLLOW_41_in_duration1421); 
            following.push(FOLLOW_opt_eol_in_duration1423);
            opt_eol();
            following.pop();

            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_duration1427); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:635:46: ( ';' )?
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
                    new NoViableAltException("635:46: ( \';\' )?", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:635:46: ';'
                    {
                    match(input,15,FOLLOW_15_in_duration1429); 

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_duration1432);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:643:1: normal_lhs_block[AndDescr descrs] : (d= lhs opt_eol )* opt_eol ;
    public void normal_lhs_block(AndDescr descrs) throws RecognitionException {   
        PatternDescr d = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:645:17: ( (d= lhs opt_eol )* opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:645:17: (d= lhs opt_eol )* opt_eol
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:645:17: (d= lhs opt_eol )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);
                if ( LA44_0==ID||LA44_0==23||(LA44_0>=52 && LA44_0<=54) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:645:25: d= lhs opt_eol
            	    {
            	    following.push(FOLLOW_lhs_in_normal_lhs_block1458);
            	    d=lhs();
            	    following.pop();

            	    following.push(FOLLOW_opt_eol_in_normal_lhs_block1460);
            	    opt_eol();
            	    following.pop();

            	     descrs.addDescr( d ); 

            	    }
            	    break;

            	default :
            	    break loop44;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_normal_lhs_block1472);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:653:1: expander_lhs_block[AndDescr descrs] : ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )* ;
    public void expander_lhs_block(AndDescr descrs) throws RecognitionException {   
        Token loc=null;
        String text = null;



        		String lhsBlock = null;
        		String eol = System.getProperty( "line.separator" );
        		List constraints = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:660:17: ( ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:660:17: ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:660:17: ( options {greedy=false; } : text= paren_chunk loc= EOL ( EOL )* )*
            loop46:
            do {
                int alt46=2;
                switch ( input.LA(1) ) {
                case 29:
                    alt46=2;
                    break;
                case EOL:
                    alt46=2;
                    break;
                case 34:
                    alt46=2;
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
                    alt46=1;
                    break;
                case 15:
                    alt46=2;
                    break;

                }

                switch (alt46) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:661:25: text= paren_chunk loc= EOL ( EOL )*
            	    {
            	    following.push(FOLLOW_paren_chunk_in_expander_lhs_block1513);
            	    text=paren_chunk();
            	    following.pop();

            	    loc=(Token)input.LT(1);
            	    match(input,EOL,FOLLOW_EOL_in_expander_lhs_block1517); 

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
            	    			
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:687:17: ( EOL )*
            	    loop45:
            	    do {
            	        int alt45=2;
            	        int LA45_0 = input.LA(1);
            	        if ( LA45_0==EOL ) {
            	            alt45=1;
            	        }


            	        switch (alt45) {
            	    	case 1 :
            	    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:687:18: EOL
            	    	    {
            	    	    match(input,EOL,FOLLOW_EOL_in_expander_lhs_block1532); 

            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop45;
            	        }
            	    } while (true);


            	    }
            	    break;

            	default :
            	    break loop46;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:701:1: lhs returns [PatternDescr d] : l= lhs_or ;
    public PatternDescr lhs() throws RecognitionException {   
        PatternDescr d;
        PatternDescr l = null;



        		d=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:705:17: (l= lhs_or )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:705:17: l= lhs_or
            {
            following.push(FOLLOW_lhs_or_in_lhs1574);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:709:1: lhs_column returns [PatternDescr d] : (f= fact_binding | f= fact );
    public PatternDescr lhs_column() throws RecognitionException {   
        PatternDescr d;
        PatternDescr f = null;



        		d=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:713:17: (f= fact_binding | f= fact )
            int alt47=2;
            alt47 = dfa47.predict(input); 
            switch (alt47) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:713:17: f= fact_binding
                    {
                    following.push(FOLLOW_fact_binding_in_lhs_column1602);
                    f=fact_binding();
                    following.pop();

                     d = f; 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:714:17: f= fact
                    {
                    following.push(FOLLOW_fact_in_lhs_column1611);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:717:1: from_statement returns [FromDescr d] : 'from' opt_eol ds= from_source ;
    public FromDescr from_statement() throws RecognitionException {   
        FromDescr d;
        DeclarativeInvokerDescr ds = null;



        		d=factory.createFrom();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:722:17: ( 'from' opt_eol ds= from_source )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:722:17: 'from' opt_eol ds= from_source
            {
            match(input,42,FOLLOW_42_in_from_statement1639); 
            following.push(FOLLOW_opt_eol_in_from_statement1641);
            opt_eol();
            following.pop();

            following.push(FOLLOW_from_source_in_from_statement1645);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:732:1: from_source returns [DeclarativeInvokerDescr ds] : ( (var= ID '.' field= ID ) | (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) | (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) );
    public DeclarativeInvokerDescr from_source() throws RecognitionException {   
        DeclarativeInvokerDescr ds;
        Token var=null;
        Token field=null;
        Token method=null;
        Token functionName=null;
        ArrayList args = null;



        		ds = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:737:17: ( (var= ID '.' field= ID ) | (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) | (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' ) )
            int alt48=3;
            alt48 = dfa48.predict(input); 
            switch (alt48) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:737:17: (var= ID '.' field= ID )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:737:17: (var= ID '.' field= ID )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:737:18: var= ID '.' field= ID
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1689); 
                    match(input,19,FOLLOW_19_in_from_source1691); 
                    field=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1695); 

                    			  FieldAccessDescr fa = new FieldAccessDescr(var.getText(), field.getText());	
                    			  fa.setLine(var.getLine());
                    			  ds = fa;
                    			 

                    }


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:747:17: (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:747:17: (var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:747:18: var= ID '.' method= ID opt_eol '(' opt_eol args= argument_list opt_eol ')'
                    {
                    var=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1722); 
                    match(input,19,FOLLOW_19_in_from_source1724); 
                    method=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1728); 
                    following.push(FOLLOW_opt_eol_in_from_source1730);
                    opt_eol();
                    following.pop();

                    match(input,23,FOLLOW_23_in_from_source1733); 
                    following.push(FOLLOW_opt_eol_in_from_source1735);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_argument_list_in_from_source1739);
                    args=argument_list();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_from_source1741);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_from_source1743); 

                    			MethodAccessDescr mc = new MethodAccessDescr(var.getText(), method.getText());
                    			mc.setArguments(args);
                    			mc.setLine(var.getLine());
                    			ds = mc;
                    			

                    }


                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:756:17: (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    {
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:756:17: (functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')' )
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:756:18: functionName= ID opt_eol '(' opt_eol args= argument_list opt_eol ')'
                    {
                    functionName=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_from_source1765); 
                    following.push(FOLLOW_opt_eol_in_from_source1767);
                    opt_eol();
                    following.pop();

                    match(input,23,FOLLOW_23_in_from_source1769); 
                    following.push(FOLLOW_opt_eol_in_from_source1771);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_argument_list_in_from_source1775);
                    args=argument_list();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_from_source1777);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_from_source1779); 

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


    // $ANTLR start argument_list
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:769:1: argument_list returns [ArrayList args] : (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )? ;
    public ArrayList argument_list() throws RecognitionException {   
        ArrayList args;
        ArgumentValueDescr param = null;



        		args = new ArrayList();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:774:17: ( (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )? )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:774:17: (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )?
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:774:17: (param= argument_value ( opt_eol ',' opt_eol param= argument_value )* )?
            int alt50=2;
            int LA50_0 = input.LA(1);
            if ( (LA50_0>=ID && LA50_0<=FLOAT)||LA50_0==26||LA50_0==43 ) {
                alt50=1;
            }
            else if ( LA50_0==EOL||LA50_0==15||LA50_0==25 ) {
                alt50=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("774:17: (param= argument_value ( opt_eol \',\' opt_eol param= argument_value )* )?", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:774:18: param= argument_value ( opt_eol ',' opt_eol param= argument_value )*
                    {
                    following.push(FOLLOW_argument_value_in_argument_list1822);
                    param=argument_value();
                    following.pop();


                    			if (param != null) {
                    				args.add(param);
                    			}
                    		
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:780:17: ( opt_eol ',' opt_eol param= argument_value )*
                    loop49:
                    do {
                        int alt49=2;
                        alt49 = dfa49.predict(input); 
                        switch (alt49) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:781:25: opt_eol ',' opt_eol param= argument_value
                    	    {
                    	    following.push(FOLLOW_opt_eol_in_argument_list1838);
                    	    opt_eol();
                    	    following.pop();

                    	    match(input,24,FOLLOW_24_in_argument_list1840); 
                    	    following.push(FOLLOW_opt_eol_in_argument_list1842);
                    	    opt_eol();
                    	    following.pop();

                    	    following.push(FOLLOW_argument_value_in_argument_list1846);
                    	    param=argument_value();
                    	    following.pop();


                    	    				if (param != null) {
                    	    					args.add(param);
                    	    				}
                    	    			

                    	    }
                    	    break;

                    	default :
                    	    break loop49;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:790:1: argument_value returns [ArgumentValueDescr value] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map ) ;
    public ArgumentValueDescr argument_value() throws RecognitionException {   
        ArgumentValueDescr value;
        Token t=null;
        ArgumentValueDescr.MapDescr m = null;



        		value = null;
        		String text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:795:17: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:795:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:795:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= 'null' | t= 'null' | m= inline_map )
            int alt51=8;
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
            case ID:
                alt51=5;
                break;
            case 43:
                alt51=6;
                break;
            case 26:
                alt51=8;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("795:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= ID | t= \'null\' | t= \'null\' | m= inline_map )", 51, 0, input);

                throw nvae;
            }

            switch (alt51) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:795:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_argument_value1886); 
                     text = getString( t );  value=new ArgumentValueDescr(ArgumentValueDescr.STRING, text);

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:796:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_argument_value1897); 
                     text = t.getText();  value=new ArgumentValueDescr(ArgumentValueDescr.INTEGRAL, text);

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:797:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_argument_value1910); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.DECIMAL, text); 

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:798:25: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_argument_value1921); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.BOOLEAN, text); 

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:799:25: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_argument_value1933); 
                     text = t.getText(); value=new ArgumentValueDescr(ArgumentValueDescr.VARIABLE, text);

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:800:25: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_argument_value1944); 
                     text = "null"; value=new ArgumentValueDescr(ArgumentValueDescr.NULL, text);

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:801:25: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_argument_value1955); 
                     text = "null"; value=new ArgumentValueDescr(ArgumentValueDescr.NULL, text);

                    }
                    break;
                case 8 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:802:25: m= inline_map
                    {
                    following.push(FOLLOW_inline_map_in_argument_value1974);
                    m=inline_map();
                    following.pop();

                      value=new ArgumentValueDescr(ArgumentValueDescr.MAP, m.getKeyValuePairs() ); 

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:806:1: inline_map returns [ArgumentValueDescr.MapDescr mapDescr] : '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}' ;
    public ArgumentValueDescr.MapDescr inline_map() throws RecognitionException {   
        ArgumentValueDescr.MapDescr mapDescr;
        ArgumentValueDescr key = null;

        ArgumentValueDescr value = null;



                mapDescr = new ArgumentValueDescr.MapDescr();
            
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:810:8: ( '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:810:8: '{' (key= argument_value '=>' value= argument_value ) ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )* '}'
            {
            match(input,26,FOLLOW_26_in_inline_map2014); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:811:12: (key= argument_value '=>' value= argument_value )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:811:14: key= argument_value '=>' value= argument_value
            {
            following.push(FOLLOW_argument_value_in_inline_map2032);
            key=argument_value();
            following.pop();

            match(input,44,FOLLOW_44_in_inline_map2034); 
            following.push(FOLLOW_argument_value_in_inline_map2038);
            value=argument_value();
            following.pop();


                             if ( key != null ) {
                                 mapDescr.add( new ArgumentValueDescr.KeyValuePairDescr( key, value ) );
                             }
                         

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:818:12: ( ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);
                if ( LA54_0==EOL||LA54_0==24 ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:818:14: ( EOL )? ',' ( EOL )? key= argument_value '=>' value= argument_value
            	    {
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:818:14: ( EOL )?
            	    int alt52=2;
            	    int LA52_0 = input.LA(1);
            	    if ( LA52_0==EOL ) {
            	        alt52=1;
            	    }
            	    else if ( LA52_0==24 ) {
            	        alt52=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("818:14: ( EOL )?", 52, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt52) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:818:15: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_map2081); 

            	            }
            	            break;

            	    }

            	    match(input,24,FOLLOW_24_in_inline_map2085); 
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:818:25: ( EOL )?
            	    int alt53=2;
            	    int LA53_0 = input.LA(1);
            	    if ( LA53_0==EOL ) {
            	        alt53=1;
            	    }
            	    else if ( (LA53_0>=ID && LA53_0<=FLOAT)||LA53_0==26||LA53_0==43 ) {
            	        alt53=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("818:25: ( EOL )?", 53, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt53) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:818:26: EOL
            	            {
            	            match(input,EOL,FOLLOW_EOL_in_inline_map2088); 

            	            }
            	            break;

            	    }

            	    following.push(FOLLOW_argument_value_in_inline_map2094);
            	    key=argument_value();
            	    following.pop();

            	    match(input,44,FOLLOW_44_in_inline_map2096); 
            	    following.push(FOLLOW_argument_value_in_inline_map2100);
            	    value=argument_value();
            	    following.pop();


            	                     if ( key != null ) {
            	                         mapDescr.add( new ArgumentValueDescr.KeyValuePairDescr( key, value ) );
            	                     }
            	                 

            	    }
            	    break;

            	default :
            	    break loop54;
                }
            } while (true);

            match(input,27,FOLLOW_27_in_inline_map2136); 

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


    // $ANTLR start fact_binding
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:827:1: fact_binding returns [PatternDescr d] : id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()] ;
    public PatternDescr fact_binding() throws RecognitionException {   
        PatternDescr d;
        Token id=null;
        PatternDescr fe = null;



        		d=null;
        		boolean multi=false;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:833:17: (id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()] )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:833:17: id= ID opt_eol ':' opt_eol fe= fact_expression[id.getText()]
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_fact_binding2168); 
            following.push(FOLLOW_opt_eol_in_fact_binding2178);
            opt_eol();
            following.pop();

            match(input,33,FOLLOW_33_in_fact_binding2180); 
            following.push(FOLLOW_opt_eol_in_fact_binding2182);
            opt_eol();
            following.pop();

            following.push(FOLLOW_fact_expression_in_fact_binding2186);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:841:2: fact_expression[String id] returns [PatternDescr pd] : ( '(' opt_eol fe= fact_expression[id] opt_eol ')' | f= fact opt_eol ( ('or'|'||') opt_eol f= fact )* );
    public PatternDescr fact_expression(String id) throws RecognitionException {   
        PatternDescr pd;
        PatternDescr fe = null;

        PatternDescr f = null;



         		pd = null;
         		boolean multi = false;
         	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:846:17: ( '(' opt_eol fe= fact_expression[id] opt_eol ')' | f= fact opt_eol ( ('or'|'||') opt_eol f= fact )* )
            int alt56=2;
            int LA56_0 = input.LA(1);
            if ( LA56_0==23 ) {
                alt56=1;
            }
            else if ( LA56_0==ID ) {
                alt56=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("841:2: fact_expression[String id] returns [PatternDescr pd] : ( \'(\' opt_eol fe= fact_expression[id] opt_eol \')\' | f= fact opt_eol ( (\'or\'|\'||\') opt_eol f= fact )* );", 56, 0, input);

                throw nvae;
            }
            switch (alt56) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:846:17: '(' opt_eol fe= fact_expression[id] opt_eol ')'
                    {
                    match(input,23,FOLLOW_23_in_fact_expression2218); 
                    following.push(FOLLOW_opt_eol_in_fact_expression2220);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_fact_expression_in_fact_expression2224);
                    fe=fact_expression(id);
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_fact_expression2226);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_fact_expression2228); 
                     pd=fe; 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:847:17: f= fact opt_eol ( ('or'|'||') opt_eol f= fact )*
                    {
                    following.push(FOLLOW_fact_in_fact_expression2239);
                    f=fact();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_fact_expression2241);
                    opt_eol();
                    following.pop();


                     			((ColumnDescr)f).setIdentifier( id );
                     			pd = f;
                     		
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:852:17: ( ('or'|'||') opt_eol f= fact )*
                    loop55:
                    do {
                        int alt55=2;
                        int LA55_0 = input.LA(1);
                        if ( (LA55_0>=45 && LA55_0<=46) ) {
                            alt55=1;
                        }


                        switch (alt55) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:852:25: ('or'|'||') opt_eol f= fact
                    	    {
                    	    if ( (input.LA(1)>=45 && input.LA(1)<=46) ) {
                    	        input.consume();
                    	        errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_fact_expression2254);    throw mse;
                    	    }

                    	    following.push(FOLLOW_opt_eol_in_fact_expression2259);
                    	    opt_eol();
                    	    following.pop();

                    	    	if ( ! multi ) {
                    	     					PatternDescr first = pd;
                    	     					pd = new OrDescr();
                    	     					((OrDescr)pd).addDescr( first );
                    	     					multi=true;
                    	     				}
                    	     			
                    	    following.push(FOLLOW_fact_in_fact_expression2273);
                    	    f=fact();
                    	    following.pop();


                    	     				((ColumnDescr)f).setIdentifier( id );
                    	     				((OrDescr)pd).addDescr( f );
                    	     			

                    	    }
                    	    break;

                    	default :
                    	    break loop55;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:868:1: fact returns [PatternDescr d] : id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol endLoc= ')' opt_eol ;
    public PatternDescr fact() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        Token endLoc=null;
        String id = null;

        List c = null;



        		d=null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:872:17: (id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol endLoc= ')' opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:872:17: id= dotted_name opt_eol loc= '(' opt_eol (c= constraints )? opt_eol endLoc= ')' opt_eol
            {
            following.push(FOLLOW_dotted_name_in_fact2312);
            id=dotted_name();
            following.pop();

             
             			d = new ColumnDescr( id ); 
             		
            following.push(FOLLOW_opt_eol_in_fact2320);
            opt_eol();
            following.pop();

            loc=(Token)input.LT(1);
            match(input,23,FOLLOW_23_in_fact2328); 

             				d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
             			
            following.push(FOLLOW_opt_eol_in_fact2331);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:878:34: (c= constraints )?
            int alt57=2;
            alt57 = dfa57.predict(input); 
            switch (alt57) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:878:41: c= constraints
                    {
                    following.push(FOLLOW_constraints_in_fact2337);
                    c=constraints();
                    following.pop();


                    		 			for ( Iterator cIter = c.iterator() ; cIter.hasNext() ; ) {
                     						((ColumnDescr)d).addDescr( (PatternDescr) cIter.next() );
                     					}
                     				

                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_fact2356);
            opt_eol();
            following.pop();

            endLoc=(Token)input.LT(1);
            match(input,25,FOLLOW_25_in_fact2360); 
            following.push(FOLLOW_opt_eol_in_fact2362);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:892:1: constraints returns [List constraints] : opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol ;
    public List constraints() throws RecognitionException {   
        List constraints;

        		constraints = new ArrayList();
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:896:17: ( opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:896:17: opt_eol ( constraint[constraints] | predicate[constraints] ) ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )* opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraints2394);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:897:17: ( constraint[constraints] | predicate[constraints] )
            int alt58=2;
            int LA58_0 = input.LA(1);
            if ( LA58_0==EOL||LA58_0==15 ) {
                alt58=1;
            }
            else if ( LA58_0==ID ) {
                int LA58_2 = input.LA(2);
                if ( LA58_2==33 ) {
                    int LA58_3 = input.LA(3);
                    if ( LA58_3==ID ) {
                        int LA58_17 = input.LA(4);
                        if ( LA58_17==49 ) {
                            alt58=2;
                        }
                        else if ( LA58_17==EOL||LA58_17==15||(LA58_17>=24 && LA58_17<=25)||(LA58_17>=58 && LA58_17<=67) ) {
                            alt58=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("897:17: ( constraint[constraints] | predicate[constraints] )", 58, 17, input);

                            throw nvae;
                        }
                    }
                    else if ( LA58_3==EOL||LA58_3==15 ) {
                        alt58=1;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("897:17: ( constraint[constraints] | predicate[constraints] )", 58, 3, input);

                        throw nvae;
                    }
                }
                else if ( LA58_2==EOL||LA58_2==15||(LA58_2>=24 && LA58_2<=25)||(LA58_2>=58 && LA58_2<=67) ) {
                    alt58=1;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("897:17: ( constraint[constraints] | predicate[constraints] )", 58, 2, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("897:17: ( constraint[constraints] | predicate[constraints] )", 58, 0, input);

                throw nvae;
            }
            switch (alt58) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:897:18: constraint[constraints]
                    {
                    following.push(FOLLOW_constraint_in_constraints2399);
                    constraint(constraints);
                    following.pop();


                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:897:42: predicate[constraints]
                    {
                    following.push(FOLLOW_predicate_in_constraints2402);
                    predicate(constraints);
                    following.pop();


                    }
                    break;

            }

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:898:17: ( opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] ) )*
            loop60:
            do {
                int alt60=2;
                alt60 = dfa60.predict(input); 
                switch (alt60) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:898:19: opt_eol ',' opt_eol ( constraint[constraints] | predicate[constraints] )
            	    {
            	    following.push(FOLLOW_opt_eol_in_constraints2410);
            	    opt_eol();
            	    following.pop();

            	    match(input,24,FOLLOW_24_in_constraints2412); 
            	    following.push(FOLLOW_opt_eol_in_constraints2414);
            	    opt_eol();
            	    following.pop();

            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:898:39: ( constraint[constraints] | predicate[constraints] )
            	    int alt59=2;
            	    int LA59_0 = input.LA(1);
            	    if ( LA59_0==EOL||LA59_0==15 ) {
            	        alt59=1;
            	    }
            	    else if ( LA59_0==ID ) {
            	        int LA59_2 = input.LA(2);
            	        if ( LA59_2==33 ) {
            	            int LA59_3 = input.LA(3);
            	            if ( LA59_3==ID ) {
            	                int LA59_17 = input.LA(4);
            	                if ( LA59_17==49 ) {
            	                    alt59=2;
            	                }
            	                else if ( LA59_17==EOL||LA59_17==15||(LA59_17>=24 && LA59_17<=25)||(LA59_17>=58 && LA59_17<=67) ) {
            	                    alt59=1;
            	                }
            	                else {
            	                    NoViableAltException nvae =
            	                        new NoViableAltException("898:39: ( constraint[constraints] | predicate[constraints] )", 59, 17, input);

            	                    throw nvae;
            	                }
            	            }
            	            else if ( LA59_3==EOL||LA59_3==15 ) {
            	                alt59=1;
            	            }
            	            else {
            	                NoViableAltException nvae =
            	                    new NoViableAltException("898:39: ( constraint[constraints] | predicate[constraints] )", 59, 3, input);

            	                throw nvae;
            	            }
            	        }
            	        else if ( LA59_2==EOL||LA59_2==15||(LA59_2>=24 && LA59_2<=25)||(LA59_2>=58 && LA59_2<=67) ) {
            	            alt59=1;
            	        }
            	        else {
            	            NoViableAltException nvae =
            	                new NoViableAltException("898:39: ( constraint[constraints] | predicate[constraints] )", 59, 2, input);

            	            throw nvae;
            	        }
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("898:39: ( constraint[constraints] | predicate[constraints] )", 59, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt59) {
            	        case 1 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:898:40: constraint[constraints]
            	            {
            	            following.push(FOLLOW_constraint_in_constraints2417);
            	            constraint(constraints);
            	            following.pop();


            	            }
            	            break;
            	        case 2 :
            	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:898:64: predicate[constraints]
            	            {
            	            following.push(FOLLOW_predicate_in_constraints2420);
            	            predicate(constraints);
            	            following.pop();


            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop60;
                }
            } while (true);

            following.push(FOLLOW_opt_eol_in_constraints2428);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:902:1: constraint[List constraints] : opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )? opt_eol ;
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
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:906:17: ( opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )? opt_eol )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:906:17: opt_eol (fb= ID opt_eol ':' opt_eol )? f= ID opt_eol (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )? opt_eol
            {
            following.push(FOLLOW_opt_eol_in_constraint2447);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:907:17: (fb= ID opt_eol ':' opt_eol )?
            int alt61=2;
            alt61 = dfa61.predict(input); 
            switch (alt61) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:907:19: fb= ID opt_eol ':' opt_eol
                    {
                    fb=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_constraint2455); 
                    following.push(FOLLOW_opt_eol_in_constraint2457);
                    opt_eol();
                    following.pop();

                    match(input,33,FOLLOW_33_in_constraint2459); 
                    following.push(FOLLOW_opt_eol_in_constraint2461);
                    opt_eol();
                    following.pop();


                    }
                    break;

            }

            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint2471); 


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
            									
            			
            		
            following.push(FOLLOW_opt_eol_in_constraint2485);
            opt_eol();
            following.pop();

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:925:33: (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )?
            int alt65=2;
            int LA65_0 = input.LA(1);
            if ( (LA65_0>=58 && LA65_0<=67) ) {
                alt65=1;
            }
            else if ( LA65_0==EOL||LA65_0==15||(LA65_0>=24 && LA65_0<=25) ) {
                alt65=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("925:33: (op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= (\'&\'|\'|\')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )* )?", 65, 0, input);

                throw nvae;
            }
            switch (alt65) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:925:41: op= operator opt_eol (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )*
                    {
                    following.push(FOLLOW_operator_in_constraint2491);
                    op=operator();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_constraint2493);
                    opt_eol();
                    following.pop();

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:927:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    int alt62=4;
                    switch ( input.LA(1) ) {
                    case ID:
                        int LA62_1 = input.LA(2);
                        if ( LA62_1==19 ) {
                            alt62=2;
                        }
                        else if ( LA62_1==EOL||LA62_1==15||(LA62_1>=24 && LA62_1<=25)||(LA62_1>=47 && LA62_1<=48) ) {
                            alt62=1;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("927:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 62, 1, input);

                            throw nvae;
                        }
                        break;
                    case INT:
                    case BOOL:
                    case STRING:
                    case FLOAT:
                    case 43:
                        alt62=3;
                        break;
                    case 23:
                        alt62=4;
                        break;
                    default:
                        NoViableAltException nvae =
                            new NoViableAltException("927:41: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 62, 0, input);

                        throw nvae;
                    }

                    switch (alt62) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:927:49: bvc= ID
                            {
                            bvc=(Token)input.LT(1);
                            match(input,ID,FOLLOW_ID_in_constraint2511); 

                            							
                            														
                            							
                            							VariableRestrictionDescr vd = new VariableRestrictionDescr(op, bvc.getText());
                            							fc.addRestriction(vd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;
                        case 2 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:938:49: lc= enum_constraint
                            {
                            following.push(FOLLOW_enum_constraint_in_constraint2536);
                            lc=enum_constraint();
                            following.pop();

                             

                            							LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc, true);
                            							fc.addRestriction(lrd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;
                        case 3 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:947:49: lc= literal_constraint
                            {
                            following.push(FOLLOW_literal_constraint_in_constraint2568);
                            lc=literal_constraint();
                            following.pop();

                             
                            							
                            							LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc);
                            							fc.addRestriction(lrd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;
                        case 4 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:955:49: rvc= retval_constraint
                            {
                            following.push(FOLLOW_retval_constraint_in_constraint2588);
                            rvc=retval_constraint();
                            following.pop();

                             
                            							
                            							

                            							ReturnValueRestrictionDescr rvd = new ReturnValueRestrictionDescr(op, rvc);							
                            							fc.addRestriction(rvd);
                            							constraints.add(fc);
                            							
                            						

                            }
                            break;

                    }

                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:966:41: (con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint ) )*
                    loop64:
                    do {
                        int alt64=2;
                        int LA64_0 = input.LA(1);
                        if ( (LA64_0>=47 && LA64_0<=48) ) {
                            alt64=1;
                        }


                        switch (alt64) {
                    	case 1 :
                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:967:49: con= ('&'|'|')op= operator (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    	    {
                    	    con=(Token)input.LT(1);
                    	    if ( (input.LA(1)>=47 && input.LA(1)<=48) ) {
                    	        input.consume();
                    	        errorRecovery=false;
                    	    }
                    	    else {
                    	        MismatchedSetException mse =
                    	            new MismatchedSetException(null,input);
                    	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_constraint2623);    throw mse;
                    	    }


                    	    							if (con.getText().equals("&") ) {								
                    	    								fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));	
                    	    							} else {
                    	    								fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	
                    	    							}							
                    	    						
                    	    following.push(FOLLOW_operator_in_constraint2645);
                    	    op=operator();
                    	    following.pop();

                    	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:977:49: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )
                    	    int alt63=4;
                    	    switch ( input.LA(1) ) {
                    	    case ID:
                    	        int LA63_1 = input.LA(2);
                    	        if ( LA63_1==19 ) {
                    	            alt63=2;
                    	        }
                    	        else if ( LA63_1==EOL||LA63_1==15||(LA63_1>=24 && LA63_1<=25)||(LA63_1>=47 && LA63_1<=48) ) {
                    	            alt63=1;
                    	        }
                    	        else {
                    	            NoViableAltException nvae =
                    	                new NoViableAltException("977:49: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 63, 1, input);

                    	            throw nvae;
                    	        }
                    	        break;
                    	    case INT:
                    	    case BOOL:
                    	    case STRING:
                    	    case FLOAT:
                    	    case 43:
                    	        alt63=3;
                    	        break;
                    	    case 23:
                    	        alt63=4;
                    	        break;
                    	    default:
                    	        NoViableAltException nvae =
                    	            new NoViableAltException("977:49: (bvc= ID | lc= enum_constraint | lc= literal_constraint | rvc= retval_constraint )", 63, 0, input);

                    	        throw nvae;
                    	    }

                    	    switch (alt63) {
                    	        case 1 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:977:57: bvc= ID
                    	            {
                    	            bvc=(Token)input.LT(1);
                    	            match(input,ID,FOLLOW_ID_in_constraint2657); 

                    	            								VariableRestrictionDescr vd = new VariableRestrictionDescr(op, bvc.getText());
                    	            								fc.addRestriction(vd);
                    	            							

                    	            }
                    	            break;
                    	        case 2 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:983:57: lc= enum_constraint
                    	            {
                    	            following.push(FOLLOW_enum_constraint_in_constraint2685);
                    	            lc=enum_constraint();
                    	            following.pop();

                    	             
                    	            								LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc, true);
                    	            								fc.addRestriction(lrd);
                    	            								
                    	            							

                    	            }
                    	            break;
                    	        case 3 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:990:57: lc= literal_constraint
                    	            {
                    	            following.push(FOLLOW_literal_constraint_in_constraint2720);
                    	            lc=literal_constraint();
                    	            following.pop();

                    	             
                    	            								LiteralRestrictionDescr lrd  = new LiteralRestrictionDescr(op, lc);
                    	            								fc.addRestriction(lrd);
                    	            								
                    	            							

                    	            }
                    	            break;
                    	        case 4 :
                    	            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:996:57: rvc= retval_constraint
                    	            {
                    	            following.push(FOLLOW_retval_constraint_in_constraint2742);
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
                    	    break loop64;
                        }
                    } while (true);


                    }
                    break;

            }

            following.push(FOLLOW_opt_eol_in_constraint2798);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1009:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) ;
    public String literal_constraint() throws RecognitionException {   
        String text;
        Token t=null;


        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:17: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= 'null' )
            int alt66=5;
            switch ( input.LA(1) ) {
            case STRING:
                alt66=1;
                break;
            case INT:
                alt66=2;
                break;
            case FLOAT:
                alt66=3;
                break;
            case BOOL:
                alt66=4;
                break;
            case 43:
                alt66=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1013:17: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= \'null\' )", 66, 0, input);

                throw nvae;
            }

            switch (alt66) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1013:25: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint2825); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1014:25: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint2836); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1015:25: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint2849); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1016:25: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint2860); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1017:25: t= 'null'
                    {
                    t=(Token)input.LT(1);
                    match(input,43,FOLLOW_43_in_literal_constraint2872); 
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1021:1: enum_constraint returns [String text] : (cls= ID '.' en= ID ) ;
    public String enum_constraint() throws RecognitionException {   
        String text;
        Token cls=null;
        Token en=null;


        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1025:17: ( (cls= ID '.' en= ID ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1025:17: (cls= ID '.' en= ID )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1025:17: (cls= ID '.' en= ID )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1025:18: cls= ID '.' en= ID
            {
            cls=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2903); 
            match(input,19,FOLLOW_19_in_enum_constraint2905); 
            en=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_enum_constraint2909); 

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1028:1: retval_constraint returns [String text] : '(' c= paren_chunk ')' ;
    public String retval_constraint() throws RecognitionException {   
        String text;
        String c = null;



        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1033:17: ( '(' c= paren_chunk ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1033:17: '(' c= paren_chunk ')'
            {
            match(input,23,FOLLOW_23_in_retval_constraint2938); 
            following.push(FOLLOW_paren_chunk_in_retval_constraint2943);
            c=paren_chunk();
            following.pop();

            match(input,25,FOLLOW_25_in_retval_constraint2946); 
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1036:1: predicate[List constraints] : decl= ID ':' field= ID '->' '(' text= paren_chunk ')' ;
    public void predicate(List constraints) throws RecognitionException {   
        Token decl=null;
        Token field=null;
        String text = null;


        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1038:17: (decl= ID ':' field= ID '->' '(' text= paren_chunk ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1038:17: decl= ID ':' field= ID '->' '(' text= paren_chunk ')'
            {
            decl=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2964); 
            match(input,33,FOLLOW_33_in_predicate2966); 
            field=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate2970); 
            match(input,49,FOLLOW_49_in_predicate2972); 
            match(input,23,FOLLOW_23_in_predicate2974); 
            following.push(FOLLOW_paren_chunk_in_predicate2978);
            text=paren_chunk();
            following.pop();

            match(input,25,FOLLOW_25_in_predicate2980); 

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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1045:1: paren_chunk returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* ;
    public String paren_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1051:18: ( ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1051:18: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1051:18: ( options {greedy=false; } : '(' c= paren_chunk ')' | any= . )*
            loop67:
            do {
                int alt67=3;
                switch ( input.LA(1) ) {
                case EOL:
                    alt67=3;
                    break;
                case 25:
                    alt67=3;
                    break;
                case 23:
                    alt67=1;
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
                    alt67=2;
                    break;

                }

                switch (alt67) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1052:25: '(' c= paren_chunk ')'
            	    {
            	    match(input,23,FOLLOW_23_in_paren_chunk3026); 
            	    following.push(FOLLOW_paren_chunk_in_paren_chunk3030);
            	    c=paren_chunk();
            	    following.pop();

            	    match(input,25,FOLLOW_25_in_paren_chunk3032); 

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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1063:19: any= .
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
            	    break loop67;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1075:1: paren_chunk2 returns [String text] : ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )* ;
    public String paren_chunk2() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1081:18: ( ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1081:18: ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1081:18: ( options {greedy=false; } : '(' c= paren_chunk2 ')' | any= . )*
            loop68:
            do {
                int alt68=3;
                switch ( input.LA(1) ) {
                case 25:
                    alt68=3;
                    break;
                case 23:
                    alt68=1;
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
                    alt68=2;
                    break;

                }

                switch (alt68) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1082:25: '(' c= paren_chunk2 ')'
            	    {
            	    match(input,23,FOLLOW_23_in_paren_chunk23103); 
            	    following.push(FOLLOW_paren_chunk2_in_paren_chunk23107);
            	    c=paren_chunk2();
            	    following.pop();

            	    match(input,25,FOLLOW_25_in_paren_chunk23109); 

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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1093:19: any= .
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
            	    break loop68;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1104:1: curly_chunk returns [String text] : ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* ;
    public String curly_chunk() throws RecognitionException {   
        String text;
        Token any=null;
        String c = null;



        		text = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1110:17: ( ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1110:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1110:17: ( options {greedy=false; } : '{' c= curly_chunk '}' | any= . )*
            loop69:
            do {
                int alt69=3;
                switch ( input.LA(1) ) {
                case 27:
                    alt69=3;
                    break;
                case 26:
                    alt69=1;
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
                    alt69=2;
                    break;

                }

                switch (alt69) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1111:25: '{' c= curly_chunk '}'
            	    {
            	    match(input,26,FOLLOW_26_in_curly_chunk3178); 
            	    following.push(FOLLOW_curly_chunk_in_curly_chunk3182);
            	    c=curly_chunk();
            	    following.pop();

            	    match(input,27,FOLLOW_27_in_curly_chunk3184); 

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
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1123:19: any= .
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
        return text;
    }
    // $ANTLR end curly_chunk


    // $ANTLR start lhs_or
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1135:1: lhs_or returns [PatternDescr d] : left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )* ;
    public PatternDescr lhs_or() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1140:17: (left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1140:17: left= lhs_and ( ('or'|'||') opt_eol right= lhs_and )*
            {
             OrDescr or = null; 
            following.push(FOLLOW_lhs_and_in_lhs_or3242);
            left=lhs_and();
            following.pop();

            d = left; 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1142:17: ( ('or'|'||') opt_eol right= lhs_and )*
            loop70:
            do {
                int alt70=2;
                int LA70_0 = input.LA(1);
                if ( (LA70_0>=45 && LA70_0<=46) ) {
                    alt70=1;
                }


                switch (alt70) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1142:19: ('or'|'||') opt_eol right= lhs_and
            	    {
            	    if ( (input.LA(1)>=45 && input.LA(1)<=46) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_or3251);    throw mse;
            	    }

            	    following.push(FOLLOW_opt_eol_in_lhs_or3256);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_lhs_and_in_lhs_or3263);
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
        return d;
    }
    // $ANTLR end lhs_or


    // $ANTLR start lhs_and
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1156:1: lhs_and returns [PatternDescr d] : left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )* ;
    public PatternDescr lhs_and() throws RecognitionException {   
        PatternDescr d;
        PatternDescr left = null;

        PatternDescr right = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1161:17: (left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1161:17: left= lhs_unary ( ('and'|'&&') opt_eol right= lhs_unary )*
            {
             AndDescr and = null; 
            following.push(FOLLOW_lhs_unary_in_lhs_and3303);
            left=lhs_unary();
            following.pop();

             d = left; 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1163:17: ( ('and'|'&&') opt_eol right= lhs_unary )*
            loop71:
            do {
                int alt71=2;
                int LA71_0 = input.LA(1);
                if ( (LA71_0>=50 && LA71_0<=51) ) {
                    alt71=1;
                }


                switch (alt71) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1163:19: ('and'|'&&') opt_eol right= lhs_unary
            	    {
            	    if ( (input.LA(1)>=50 && input.LA(1)<=51) ) {
            	        input.consume();
            	        errorRecovery=false;
            	    }
            	    else {
            	        MismatchedSetException mse =
            	            new MismatchedSetException(null,input);
            	        recoverFromMismatchedSet(input,mse,FOLLOW_set_in_lhs_and3312);    throw mse;
            	    }

            	    following.push(FOLLOW_opt_eol_in_lhs_and3317);
            	    opt_eol();
            	    following.pop();

            	    following.push(FOLLOW_lhs_unary_in_lhs_and3324);
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
        return d;
    }
    // $ANTLR end lhs_and


    // $ANTLR start lhs_unary
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1177:1: lhs_unary returns [PatternDescr d] : (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column (fm= from_statement )? | '(' opt_eol u= lhs opt_eol ')' ) ;
    public PatternDescr lhs_unary() throws RecognitionException {   
        PatternDescr d;
        PatternDescr u = null;

        FromDescr fm = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1181:17: ( (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column (fm= from_statement )? | '(' opt_eol u= lhs opt_eol ')' ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1181:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column (fm= from_statement )? | '(' opt_eol u= lhs opt_eol ')' )
            {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1181:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column (fm= from_statement )? | '(' opt_eol u= lhs opt_eol ')' )
            int alt73=5;
            switch ( input.LA(1) ) {
            case 52:
                alt73=1;
                break;
            case 53:
                alt73=2;
                break;
            case 54:
                alt73=3;
                break;
            case ID:
                alt73=4;
                break;
            case 23:
                alt73=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1181:17: (u= lhs_exist | u= lhs_not | u= lhs_eval | u= lhs_column (fm= from_statement )? | \'(\' opt_eol u= lhs opt_eol \')\' )", 73, 0, input);

                throw nvae;
            }

            switch (alt73) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1181:25: u= lhs_exist
                    {
                    following.push(FOLLOW_lhs_exist_in_lhs_unary3362);
                    u=lhs_exist();
                    following.pop();

                    d = u;

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1182:25: u= lhs_not
                    {
                    following.push(FOLLOW_lhs_not_in_lhs_unary3372);
                    u=lhs_not();
                    following.pop();

                    d = u;

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1183:25: u= lhs_eval
                    {
                    following.push(FOLLOW_lhs_eval_in_lhs_unary3382);
                    u=lhs_eval();
                    following.pop();

                    d = u;

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1184:25: u= lhs_column (fm= from_statement )?
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_unary3396);
                    u=lhs_column();
                    following.pop();

                    d=u;
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1184:45: (fm= from_statement )?
                    int alt72=2;
                    int LA72_0 = input.LA(1);
                    if ( LA72_0==42 ) {
                        alt72=1;
                    }
                    else if ( (LA72_0>=EOL && LA72_0<=ID)||LA72_0==15||LA72_0==23||LA72_0==25||LA72_0==29||LA72_0==34||(LA72_0>=45 && LA72_0<=46)||(LA72_0>=50 && LA72_0<=54) ) {
                        alt72=2;
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("1184:45: (fm= from_statement )?", 72, 0, input);

                        throw nvae;
                    }
                    switch (alt72) {
                        case 1 :
                            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1184:46: fm= from_statement
                            {
                            following.push(FOLLOW_from_statement_in_lhs_unary3403);
                            fm=from_statement();
                            following.pop();

                            fm.setColumn((ColumnDescr) u); d=fm;

                            }
                            break;

                    }


                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1185:25: '(' opt_eol u= lhs opt_eol ')'
                    {
                    match(input,23,FOLLOW_23_in_lhs_unary3413); 
                    following.push(FOLLOW_opt_eol_in_lhs_unary3415);
                    opt_eol();
                    following.pop();

                    following.push(FOLLOW_lhs_in_lhs_unary3419);
                    u=lhs();
                    following.pop();

                    following.push(FOLLOW_opt_eol_in_lhs_unary3421);
                    opt_eol();
                    following.pop();

                    match(input,25,FOLLOW_25_in_lhs_unary3423); 
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1189:1: lhs_exist returns [PatternDescr d] : loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public PatternDescr lhs_exist() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1193:17: (loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1193:17: loc= 'exists' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,52,FOLLOW_52_in_lhs_exist3454); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1193:30: ( '(' column= lhs_column ')' | column= lhs_column )
            int alt74=2;
            int LA74_0 = input.LA(1);
            if ( LA74_0==23 ) {
                alt74=1;
            }
            else if ( LA74_0==ID ) {
                alt74=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1193:30: ( \'(\' column= lhs_column \')\' | column= lhs_column )", 74, 0, input);

                throw nvae;
            }
            switch (alt74) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1193:31: '(' column= lhs_column ')'
                    {
                    match(input,23,FOLLOW_23_in_lhs_exist3457); 
                    following.push(FOLLOW_lhs_column_in_lhs_exist3461);
                    column=lhs_column();
                    following.pop();

                    match(input,25,FOLLOW_25_in_lhs_exist3463); 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1193:59: column= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_exist3469);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1200:1: lhs_not returns [NotDescr d] : loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) ;
    public NotDescr lhs_not() throws RecognitionException {   
        NotDescr d;
        Token loc=null;
        PatternDescr column = null;



        		d = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1204:17: (loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column ) )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1204:17: loc= 'not' ( '(' column= lhs_column ')' | column= lhs_column )
            {
            loc=(Token)input.LT(1);
            match(input,53,FOLLOW_53_in_lhs_not3499); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1204:27: ( '(' column= lhs_column ')' | column= lhs_column )
            int alt75=2;
            int LA75_0 = input.LA(1);
            if ( LA75_0==23 ) {
                alt75=1;
            }
            else if ( LA75_0==ID ) {
                alt75=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("1204:27: ( \'(\' column= lhs_column \')\' | column= lhs_column )", 75, 0, input);

                throw nvae;
            }
            switch (alt75) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1204:28: '(' column= lhs_column ')'
                    {
                    match(input,23,FOLLOW_23_in_lhs_not3502); 
                    following.push(FOLLOW_lhs_column_in_lhs_not3506);
                    column=lhs_column();
                    following.pop();

                    match(input,25,FOLLOW_25_in_lhs_not3509); 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1204:57: column= lhs_column
                    {
                    following.push(FOLLOW_lhs_column_in_lhs_not3515);
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1211:1: lhs_eval returns [PatternDescr d] : 'eval' loc= '(' c= paren_chunk2 ')' ;
    public PatternDescr lhs_eval() throws RecognitionException {   
        PatternDescr d;
        Token loc=null;
        String c = null;



        		d = null;
        		String text = "";
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1216:17: ( 'eval' loc= '(' c= paren_chunk2 ')' )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1216:17: 'eval' loc= '(' c= paren_chunk2 ')'
            {
            match(input,54,FOLLOW_54_in_lhs_eval3541); 
            loc=(Token)input.LT(1);
            match(input,23,FOLLOW_23_in_lhs_eval3545); 
            following.push(FOLLOW_paren_chunk2_in_lhs_eval3553);
            c=paren_chunk2();
            following.pop();

            match(input,25,FOLLOW_25_in_lhs_eval3557); 
             
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1225:1: dotted_name returns [String name] : id= ID ( '.' id= ID )* ( '[' ']' )* ;
    public String dotted_name() throws RecognitionException {   
        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1230:17: (id= ID ( '.' id= ID )* ( '[' ']' )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1230:17: id= ID ( '.' id= ID )* ( '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_dotted_name3589); 
             name=id.getText(); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1230:46: ( '.' id= ID )*
            loop76:
            do {
                int alt76=2;
                int LA76_0 = input.LA(1);
                if ( LA76_0==19 ) {
                    alt76=1;
                }


                switch (alt76) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1230:48: '.' id= ID
            	    {
            	    match(input,19,FOLLOW_19_in_dotted_name3595); 
            	    id=(Token)input.LT(1);
            	    match(input,ID,FOLLOW_ID_in_dotted_name3599); 
            	     name = name + "." + id.getText(); 

            	    }
            	    break;

            	default :
            	    break loop76;
                }
            } while (true);

            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1230:99: ( '[' ']' )*
            loop77:
            do {
                int alt77=2;
                int LA77_0 = input.LA(1);
                if ( LA77_0==55 ) {
                    alt77=1;
                }


                switch (alt77) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1230:101: '[' ']'
            	    {
            	    match(input,55,FOLLOW_55_in_dotted_name3608); 
            	    match(input,56,FOLLOW_56_in_dotted_name3610); 
            	     name = name + "[]";

            	    }
            	    break;

            	default :
            	    break loop77;
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1233:1: argument_name returns [String name] : id= ID ( '[' ']' )* ;
    public String argument_name() throws RecognitionException {   
        String name;
        Token id=null;


        		name = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1238:17: (id= ID ( '[' ']' )* )
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1238:17: id= ID ( '[' ']' )*
            {
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_argument_name3640); 
             name=id.getText(); 
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1238:46: ( '[' ']' )*
            loop78:
            do {
                int alt78=2;
                int LA78_0 = input.LA(1);
                if ( LA78_0==55 ) {
                    alt78=1;
                }


                switch (alt78) {
            	case 1 :
            	    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1238:48: '[' ']'
            	    {
            	    match(input,55,FOLLOW_55_in_argument_name3646); 
            	    match(input,56,FOLLOW_56_in_argument_name3648); 
            	     name = name + "[]";

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
    // $ANTLR end argument_name


    // $ANTLR start word
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1242:1: word returns [String word] : (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING );
    public String word() throws RecognitionException {   
        String word;
        Token id=null;
        Token str=null;


        		word = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1246:17: (id= ID | 'import' | 'use' | 'rule' | 'query' | 'salience' | 'no-loop' | 'when' | 'then' | 'end' | str= STRING )
            int alt79=11;
            switch ( input.LA(1) ) {
            case ID:
                alt79=1;
                break;
            case 17:
                alt79=2;
                break;
            case 57:
                alt79=3;
                break;
            case 31:
                alt79=4;
                break;
            case 28:
                alt79=5;
                break;
            case 36:
                alt79=6;
                break;
            case 37:
                alt79=7;
                break;
            case 32:
                alt79=8;
                break;
            case 34:
                alt79=9;
                break;
            case 29:
                alt79=10;
                break;
            case STRING:
                alt79=11;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1242:1: word returns [String word] : (id= ID | \'import\' | \'use\' | \'rule\' | \'query\' | \'salience\' | \'no-loop\' | \'when\' | \'then\' | \'end\' | str= STRING );", 79, 0, input);

                throw nvae;
            }

            switch (alt79) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1246:17: id= ID
                    {
                    id=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_word3676); 
                     word=id.getText(); 

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1247:17: 'import'
                    {
                    match(input,17,FOLLOW_17_in_word3688); 
                     word="import"; 

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1248:17: 'use'
                    {
                    match(input,57,FOLLOW_57_in_word3697); 
                     word="use"; 

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1249:17: 'rule'
                    {
                    match(input,31,FOLLOW_31_in_word3709); 
                     word="rule"; 

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1250:17: 'query'
                    {
                    match(input,28,FOLLOW_28_in_word3720); 
                     word="query"; 

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1251:17: 'salience'
                    {
                    match(input,36,FOLLOW_36_in_word3730); 
                     word="salience"; 

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1252:17: 'no-loop'
                    {
                    match(input,37,FOLLOW_37_in_word3738); 
                     word="no-loop"; 

                    }
                    break;
                case 8 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1253:17: 'when'
                    {
                    match(input,32,FOLLOW_32_in_word3746); 
                     word="when"; 

                    }
                    break;
                case 9 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1254:17: 'then'
                    {
                    match(input,34,FOLLOW_34_in_word3757); 
                     word="then"; 

                    }
                    break;
                case 10 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1255:17: 'end'
                    {
                    match(input,29,FOLLOW_29_in_word3768); 
                     word="end"; 

                    }
                    break;
                case 11 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1256:17: str= STRING
                    {
                    str=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_word3782); 
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
    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1259:1: operator returns [String str] : ( '==' | '=' | '>' | '>=' | '<' | '<=' | '!=' | 'contains' | 'matches' | 'excludes' );
    public String operator() throws RecognitionException {   
        String str;

        		str = null;
        	
        try {
            // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1265:17: ( '==' | '=' | '>' | '>=' | '<' | '<=' | '!=' | 'contains' | 'matches' | 'excludes' )
            int alt80=10;
            switch ( input.LA(1) ) {
            case 58:
                alt80=1;
                break;
            case 59:
                alt80=2;
                break;
            case 60:
                alt80=3;
                break;
            case 61:
                alt80=4;
                break;
            case 62:
                alt80=5;
                break;
            case 63:
                alt80=6;
                break;
            case 64:
                alt80=7;
                break;
            case 65:
                alt80=8;
                break;
            case 66:
                alt80=9;
                break;
            case 67:
                alt80=10;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("1259:1: operator returns [String str] : ( \'==\' | \'=\' | \'>\' | \'>=\' | \'<\' | \'<=\' | \'!=\' | \'contains\' | \'matches\' | \'excludes\' );", 80, 0, input);

                throw nvae;
            }

            switch (alt80) {
                case 1 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1265:17: '=='
                    {
                    match(input,58,FOLLOW_58_in_operator3811); 
                    str= "==";

                    }
                    break;
                case 2 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1266:18: '='
                    {
                    match(input,59,FOLLOW_59_in_operator3818); 
                    str="==";

                    }
                    break;
                case 3 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1267:18: '>'
                    {
                    match(input,60,FOLLOW_60_in_operator3825); 
                    str=">";

                    }
                    break;
                case 4 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1268:18: '>='
                    {
                    match(input,61,FOLLOW_61_in_operator3832); 
                    str=">=";

                    }
                    break;
                case 5 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1269:18: '<'
                    {
                    match(input,62,FOLLOW_62_in_operator3841); 
                    str="<";

                    }
                    break;
                case 6 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1270:18: '<='
                    {
                    match(input,63,FOLLOW_63_in_operator3848); 
                    str="<=";

                    }
                    break;
                case 7 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1271:18: '!='
                    {
                    match(input,64,FOLLOW_64_in_operator3855); 
                    str="!=";

                    }
                    break;
                case 8 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1272:18: 'contains'
                    {
                    match(input,65,FOLLOW_65_in_operator3862); 
                    str="contains";

                    }
                    break;
                case 9 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1273:18: 'matches'
                    {
                    match(input,66,FOLLOW_66_in_operator3869); 
                    str="matches";

                    }
                    break;
                case 10 :
                    // /home/michael/projects/jboss-rules/drools-compiler/src/main/resources/org/drools/lang/drl.g:1274:18: 'excludes'
                    {
                    match(input,67,FOLLOW_67_in_operator3876); 
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


    protected DFA2 dfa2 = new DFA2();protected DFA4 dfa4 = new DFA4();protected DFA13 dfa13 = new DFA13();protected DFA14 dfa14 = new DFA14();protected DFA15 dfa15 = new DFA15();protected DFA47 dfa47 = new DFA47();protected DFA48 dfa48 = new DFA48();protected DFA49 dfa49 = new DFA49();protected DFA57 dfa57 = new DFA57();protected DFA60 dfa60 = new DFA60();protected DFA61 dfa61 = new DFA61();
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
                if ( LA4_33==55 ) {return s21;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 33, input);

                throw nvae;
            }
        };
        DFA.State s21 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_21 = input.LA(1);
                if ( LA4_21==56 ) {return s33;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 21, input);

                throw nvae;
            }
        };
        DFA.State s32 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 55:
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

                case 55:
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
        DFA.State s104 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s41;

                case 24:
                    return s54;

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
                case 55:
                    return s67;

                case EOL:
                case 15:
                    return s104;

                case 25:
                    return s41;

                case 24:
                    return s54;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 88, input);

                    throw nvae;        }
            }
        };
        DFA.State s84 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s88;

                case EOL:
                case 15:
                    return s84;

                case 25:
                    return s41;

                case 24:
                    return s54;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 84, input);

                    throw nvae;        }
            }
        };
        DFA.State s66 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 55:
                    return s52;

                case EOL:
                case 15:
                    return s84;

                case 25:
                    return s41;

                case 24:
                    return s54;

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
        DFA.State s54 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_54 = input.LA(1);
                if ( LA4_54==EOL||LA4_54==15 ) {return s65;}
                if ( LA4_54==ID ) {return s66;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 54, input);

                throw nvae;
            }
        };
        DFA.State s68 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 24:
                    return s54;

                case 25:
                    return s41;

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
                    return s54;

                case 25:
                    return s41;

                case 55:
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
                if ( LA4_67==56 ) {return s89;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 67, input);

                throw nvae;
            }
        };
        DFA.State s57 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 55:
                    return s67;

                case EOL:
                case 15:
                    return s68;

                case 24:
                    return s54;

                case 25:
                    return s41;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 57, input);

                    throw nvae;        }
            }
        };
        DFA.State s53 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s57;

                case EOL:
                case 15:
                    return s53;

                case 24:
                    return s54;

                case 25:
                    return s41;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 53, input);

                    throw nvae;        }
            }
        };
        DFA.State s60 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s53;

                case ID:
                    return s57;

                case 55:
                    return s52;

                case 24:
                    return s54;

                case 25:
                    return s41;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 4, 60, input);

                    throw nvae;        }
            }
        };
        DFA.State s52 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA4_52 = input.LA(1);
                if ( LA4_52==56 ) {return s60;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 4, 52, input);

                throw nvae;
            }
        };
        DFA.State s40 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 55:
                    return s52;

                case EOL:
                case 15:
                    return s53;

                case 24:
                    return s54;

                case 25:
                    return s41;

                case 19:
                    return s20;

                case ID:
                    return s57;

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
                case 23:
                    return s29;

                case EOL:
                case 15:
                    return s27;

                case ID:
                    return s28;

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

                case 55:
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
        DFA.State s5 = new DFA.State() {{alt=1;}};
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s3 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case ID:
                    return s5;

                case EOL:
                case 15:
                    return s3;

                case 23:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 3, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s3;

                case 23:
                    return s2;

                case ID:
                case 19:
                case 55:
                    return s5;

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

                case 24:
                case 25:
                    return s2;

                case 55:
                    return s4;

                case ID:
                    return s3;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 9, input);

                    throw nvae;        }
            }
        };
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA14_4 = input.LA(1);
                if ( LA14_4==56 ) {return s9;}

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

                case 55:
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

                case 55:
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
                if ( LA15_4==56 ) {return s9;}

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

                case 55:
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

    }class DFA47 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s2 = new DFA.State() {{alt=2;}};
        DFA.State s6 = new DFA.State() {{alt=1;}};
        DFA.State s4 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 33:
                    return s6;

                case EOL:
                case 15:
                    return s4;

                case 23:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 4, input);

                    throw nvae;        }
            }
        };
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 19:
                case 23:
                case 55:
                    return s2;

                case EOL:
                case 15:
                    return s4;

                case 33:
                    return s6;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 47, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA47_0 = input.LA(1);
                if ( LA47_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 47, 0, input);

                throw nvae;
            }
        };

    }class DFA48 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s8 = new DFA.State() {{alt=1;}};
        DFA.State s27 = new DFA.State() {{alt=2;}};
        DFA.State s54 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 24:
                case 25:
                    return s27;

                case EOL:
                case 15:
                    return s54;

                case 23:
                case 33:
                    return s8;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 54, input);

                    throw nvae;        }
            }
        };
        DFA.State s31 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 19:
                case 23:
                case 33:
                case 55:
                    return s8;

                case EOL:
                case 15:
                    return s54;

                case 24:
                case 25:
                    return s27;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 31, input);

                    throw nvae;        }
            }
        };
        DFA.State s26 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case 25:
                case 26:
                case 43:
                    return s27;

                case EOL:
                case 15:
                    return s26;

                case ID:
                    return s31;

                case 23:
                case 52:
                case 53:
                case 54:
                    return s8;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 26, input);

                    throw nvae;        }
            }
        };
        DFA.State s7 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case EOL:
                case 15:
                    return s26;

                case INT:
                case BOOL:
                case STRING:
                case FLOAT:
                case 25:
                case 26:
                case 43:
                    return s27;

                case ID:
                    return s31;

                case 23:
                case 52:
                case 53:
                case 54:
                    return s8;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 7, input);

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
                case 52:
                case 53:
                case 54:
                    return s8;

                case EOL:
                case 15:
                    return s6;

                case 23:
                    return s7;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 6, input);

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
                case 45:
                case 46:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                    return s8;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 48, 5, input);

                    throw nvae;        }
            }
        };
        DFA.State s2 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA48_2 = input.LA(1);
                if ( LA48_2==ID ) {return s5;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 48, 2, input);

                throw nvae;
            }
        };
        DFA.State s3 = new DFA.State() {{alt=3;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA48_1 = input.LA(1);
                if ( LA48_1==19 ) {return s2;}
                if ( LA48_1==EOL||LA48_1==15||LA48_1==23 ) {return s3;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 48, 1, input);

                throw nvae;
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
                        new NoViableAltException("", 49, 1, input);

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
                        new NoViableAltException("", 49, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA57 extends DFA {
        public int predict(IntStream input) throws RecognitionException {
            return predict(input, s0);
        }
        DFA.State s3 = new DFA.State() {{alt=2;}};
        DFA.State s2 = new DFA.State() {{alt=1;}};
        DFA.State s1 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                switch ( input.LA(1) ) {
                case 25:
                    return s3;

                case EOL:
                case 15:
                    return s1;

                case ID:
                    return s2;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 57, 1, input);

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
                        new NoViableAltException("", 57, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA60 extends DFA {
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
                        new NoViableAltException("", 60, 1, input);

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
                        new NoViableAltException("", 60, 0, input);

                    throw nvae;        }
            }
        };

    }class DFA61 extends DFA {
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

                case 24:
                case 25:
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
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 61, 2, input);

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

                case 24:
                case 25:
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
                    return s4;

                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 61, 1, input);

                    throw nvae;        }
            }
        };
        DFA.State s0 = new DFA.State() {
            public DFA.State transition(IntStream input) throws RecognitionException {
                int LA61_0 = input.LA(1);
                if ( LA61_0==ID ) {return s1;}

                NoViableAltException nvae =
        	    new NoViableAltException("", 61, 0, input);

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
    public static final BitSet FOLLOW_18_in_function402 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function404 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_dotted_name_in_function409 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function413 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function417 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function419 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_function428 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function430 = new BitSet(new long[]{0x0000000002008032L});
    public static final BitSet FOLLOW_dotted_name_in_function440 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function444 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_argument_name_in_function448 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function450 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_24_in_function464 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function466 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_dotted_name_in_function471 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function475 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_argument_name_in_function479 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function481 = new BitSet(new long[]{0x0000000003000000L});
    public static final BitSet FOLLOW_25_in_function506 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function510 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_26_in_function514 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_curly_chunk_in_function521 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_function530 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_function538 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_query562 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_query568 = new BitSet(new long[]{0x02000035B0020120L});
    public static final BitSet FOLLOW_word_in_query572 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_query574 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_expander_lhs_block_in_query590 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_normal_lhs_block_in_query598 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_query613 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_query615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_template639 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_template645 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_template649 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_template651 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_template_slot_in_template666 = new BitSet(new long[]{0x0000000020000020L});
    public static final BitSet FOLLOW_29_in_template681 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_template683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_dotted_name_in_template_slot715 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_template_slot719 = new BitSet(new long[]{0x0000000000008010L});
    public static final BitSet FOLLOW_set_in_template_slot723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_rule758 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_rule764 = new BitSet(new long[]{0x02000035B0020120L});
    public static final BitSet FOLLOW_word_in_rule768 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule770 = new BitSet(new long[]{0x0000000A00008012L});
    public static final BitSet FOLLOW_rule_attributes_in_rule781 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule791 = new BitSet(new long[]{0x0000000120008012L});
    public static final BitSet FOLLOW_32_in_rule800 = new BitSet(new long[]{0x0000000200008012L});
    public static final BitSet FOLLOW_33_in_rule802 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule805 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_expander_lhs_block_in_rule823 = new BitSet(new long[]{0x0000000020008012L});
    public static final BitSet FOLLOW_normal_lhs_block_in_rule832 = new BitSet(new long[]{0x0000000020008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule855 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_rule859 = new BitSet(new long[]{0x0000000200008012L});
    public static final BitSet FOLLOW_33_in_rule861 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule865 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000000000FL});
    public static final BitSet FOLLOW_29_in_rule911 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_import_statement_in_extra_statement933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_import_statement_in_extra_statement938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_global_in_extra_statement943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_extra_statement948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_rule_attributes967 = new BitSet(new long[]{0x0000000200008012L});
    public static final BitSet FOLLOW_33_in_rule_attributes970 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes973 = new BitSet(new long[]{0x000003F001000002L});
    public static final BitSet FOLLOW_24_in_rule_attributes980 = new BitSet(new long[]{0x000003F000000000L});
    public static final BitSet FOLLOW_rule_attribute_in_rule_attributes985 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_rule_attributes987 = new BitSet(new long[]{0x000003F001000002L});
    public static final BitSet FOLLOW_salience_in_rule_attribute1026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_no_loop_in_rule_attribute1036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_agenda_group_in_rule_attribute1047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_duration_in_rule_attribute1060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_activation_group_in_rule_attribute1074 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_auto_focus_in_rule_attribute1085 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_salience1118 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_salience1120 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_salience1124 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_salience1126 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_salience1129 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_no_loop1164 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop1166 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_no_loop1168 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop1171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_no_loop1196 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_BOOL_in_no_loop1200 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop1202 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_no_loop1204 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_no_loop1207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_auto_focus1253 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1255 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_auto_focus1257 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_auto_focus1285 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_BOOL_in_auto_focus1289 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1291 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_auto_focus1293 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_auto_focus1296 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_activation_group1338 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_activation_group1340 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_activation_group1344 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_activation_group1346 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_activation_group1349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_agenda_group1378 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_agenda_group1380 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_agenda_group1384 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_agenda_group1386 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_agenda_group1389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_duration1421 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_duration1423 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_duration1427 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_15_in_duration1429 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_duration1432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_in_normal_lhs_block1458 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_normal_lhs_block1460 = new BitSet(new long[]{0x0070000000808032L});
    public static final BitSet FOLLOW_opt_eol_in_normal_lhs_block1472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_paren_chunk_in_expander_lhs_block1513 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block1517 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_EOL_in_expander_lhs_block1532 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_lhs_or_in_lhs1574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_binding_in_lhs_column1602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_lhs_column1611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_from_statement1639 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_statement1641 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_from_source_in_from_statement1645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1689 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_from_source1691 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_from_source1695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1722 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_from_source1724 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_from_source1728 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1730 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_from_source1733 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1735 = new BitSet(new long[]{0x00000800040003E2L});
    public static final BitSet FOLLOW_argument_list_in_from_source1739 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1741 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_from_source1743 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_from_source1765 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1767 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_from_source1769 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1771 = new BitSet(new long[]{0x00000800040003E2L});
    public static final BitSet FOLLOW_argument_list_in_from_source1775 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_from_source1777 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_from_source1779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_argument_value_in_argument_list1822 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_argument_list1838 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_argument_list1840 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_argument_list1842 = new BitSet(new long[]{0x00000800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_argument_list1846 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_STRING_in_argument_value1886 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_argument_value1897 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_argument_value1910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_argument_value1921 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_argument_value1933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_argument_value1944 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_argument_value1955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_inline_map_in_argument_value1974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_inline_map2014 = new BitSet(new long[]{0x00000800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2032 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_inline_map2034 = new BitSet(new long[]{0x00000800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2038 = new BitSet(new long[]{0x0000000009000010L});
    public static final BitSet FOLLOW_EOL_in_inline_map2081 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_inline_map2085 = new BitSet(new long[]{0x00000800040003F0L});
    public static final BitSet FOLLOW_EOL_in_inline_map2088 = new BitSet(new long[]{0x00000800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2094 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_inline_map2096 = new BitSet(new long[]{0x00000800040003E0L});
    public static final BitSet FOLLOW_argument_value_in_inline_map2100 = new BitSet(new long[]{0x0000000009000010L});
    public static final BitSet FOLLOW_27_in_inline_map2136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_fact_binding2168 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding2178 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_fact_binding2180 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_binding2182 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_binding2186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_fact_expression2218 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2220 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_fact_expression_in_fact_expression2224 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2226 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_fact_expression2228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_fact_in_fact_expression2239 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2241 = new BitSet(new long[]{0x0000600000000002L});
    public static final BitSet FOLLOW_set_in_fact_expression2254 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact_expression2259 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_fact_in_fact_expression2273 = new BitSet(new long[]{0x0000600000000002L});
    public static final BitSet FOLLOW_dotted_name_in_fact2312 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2320 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_fact2328 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2331 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_constraints_in_fact2337 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2356 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_fact2360 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_fact2362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2394 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_constraint_in_constraints2399 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_predicate_in_constraints2402 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2410 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_constraints2412 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2414 = new BitSet(new long[]{0x0000000000008032L});
    public static final BitSet FOLLOW_constraint_in_constraints2417 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_predicate_in_constraints2420 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraints2428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2447 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint2455 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2457 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_constraint2459 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2461 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_constraint2471 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2485 = new BitSet(new long[]{0xFC00000000008012L,0x000000000000000FL});
    public static final BitSet FOLLOW_operator_in_constraint2491 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2493 = new BitSet(new long[]{0x00000800008003E0L});
    public static final BitSet FOLLOW_ID_in_constraint2511 = new BitSet(new long[]{0x0001800000008012L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint2536 = new BitSet(new long[]{0x0001800000008012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint2568 = new BitSet(new long[]{0x0001800000008012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint2588 = new BitSet(new long[]{0x0001800000008012L});
    public static final BitSet FOLLOW_set_in_constraint2623 = new BitSet(new long[]{0xFC00000000000000L,0x000000000000000FL});
    public static final BitSet FOLLOW_operator_in_constraint2645 = new BitSet(new long[]{0x00000800008003E0L});
    public static final BitSet FOLLOW_ID_in_constraint2657 = new BitSet(new long[]{0x0001800000008012L});
    public static final BitSet FOLLOW_enum_constraint_in_constraint2685 = new BitSet(new long[]{0x0001800000008012L});
    public static final BitSet FOLLOW_literal_constraint_in_constraint2720 = new BitSet(new long[]{0x0001800000008012L});
    public static final BitSet FOLLOW_retval_constraint_in_constraint2742 = new BitSet(new long[]{0x0001800000008012L});
    public static final BitSet FOLLOW_opt_eol_in_constraint2798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint2825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint2836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint2849 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint2860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_literal_constraint2872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2903 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_enum_constraint2905 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_enum_constraint2909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_retval_constraint2938 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_paren_chunk_in_retval_constraint2943 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_retval_constraint2946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_predicate2964 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_predicate2966 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_predicate2970 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_predicate2972 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_predicate2974 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_paren_chunk_in_predicate2978 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_predicate2980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_paren_chunk3026 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_paren_chunk_in_paren_chunk3030 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_paren_chunk3032 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_23_in_paren_chunk23103 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_paren_chunk2_in_paren_chunk23107 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_paren_chunk23109 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_26_in_curly_chunk3178 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_curly_chunk_in_curly_chunk3182 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_27_in_curly_chunk3184 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3242 = new BitSet(new long[]{0x0000600000000002L});
    public static final BitSet FOLLOW_set_in_lhs_or3251 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_or3256 = new BitSet(new long[]{0x0070000000800020L});
    public static final BitSet FOLLOW_lhs_and_in_lhs_or3263 = new BitSet(new long[]{0x0000600000000002L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3303 = new BitSet(new long[]{0x000C000000000002L});
    public static final BitSet FOLLOW_set_in_lhs_and3312 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_and3317 = new BitSet(new long[]{0x0070000000800020L});
    public static final BitSet FOLLOW_lhs_unary_in_lhs_and3324 = new BitSet(new long[]{0x000C000000000002L});
    public static final BitSet FOLLOW_lhs_exist_in_lhs_unary3362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_not_in_lhs_unary3372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_eval_in_lhs_unary3382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_unary3396 = new BitSet(new long[]{0x0000040000000002L});
    public static final BitSet FOLLOW_from_statement_in_lhs_unary3403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_lhs_unary3413 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_unary3415 = new BitSet(new long[]{0x0070000000800020L});
    public static final BitSet FOLLOW_lhs_in_lhs_unary3419 = new BitSet(new long[]{0x0000000000008012L});
    public static final BitSet FOLLOW_opt_eol_in_lhs_unary3421 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_unary3423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_lhs_exist3454 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_23_in_lhs_exist3457 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3461 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_exist3463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_exist3469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_lhs_not3499 = new BitSet(new long[]{0x0000000000800020L});
    public static final BitSet FOLLOW_23_in_lhs_not3502 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3506 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_not3509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lhs_column_in_lhs_not3515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_lhs_eval3541 = new BitSet(new long[]{0x0000000000800000L});
    public static final BitSet FOLLOW_23_in_lhs_eval3545 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF2L,0x000000000000000FL});
    public static final BitSet FOLLOW_paren_chunk2_in_lhs_eval3553 = new BitSet(new long[]{0x0000000002000000L});
    public static final BitSet FOLLOW_25_in_lhs_eval3557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_dotted_name3589 = new BitSet(new long[]{0x0080000000080002L});
    public static final BitSet FOLLOW_19_in_dotted_name3595 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_dotted_name3599 = new BitSet(new long[]{0x0080000000080002L});
    public static final BitSet FOLLOW_55_in_dotted_name3608 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_dotted_name3610 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_ID_in_argument_name3640 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_55_in_argument_name3646 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_56_in_argument_name3648 = new BitSet(new long[]{0x0080000000000002L});
    public static final BitSet FOLLOW_ID_in_word3676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_word3688 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_word3697 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_word3709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_word3720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_word3730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_word3738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_word3746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_word3757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_word3768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_word3782 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_operator3811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_59_in_operator3818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_60_in_operator3825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_operator3832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_62_in_operator3841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_63_in_operator3848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_64_in_operator3855 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_operator3862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_66_in_operator3869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_operator3876 = new BitSet(new long[]{0x0000000000000002L});

}