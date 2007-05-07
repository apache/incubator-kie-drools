// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-05-08 00:12:51

	package org.drools.clp;
	
	import org.drools.clp.valuehandlers.*;
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

public class CLPParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LEFT_PAREN", "DEFFUNCTION", "NAME", "RIGHT_PAREN", "VAR", "DEFRULE", "STRING", "SALIENCE", "INT", "AND", "OR", "NOT", "EXISTS", "TEST", "ASSIGN_OP", "AMPERSAND", "PIPE", "TILDE", "COLON", "EQUALS", "FLOAT", "BOOL", "NULL", "EOL", "WS", "DECLARE", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SYMBOL", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "MULTI_LINE_COMMENT", "'=>'"
    };
    public static final int RIGHT_SQUARE=38;
    public static final int RIGHT_CURLY=40;
    public static final int SYMBOL=34;
    public static final int NULL=26;
    public static final int BOOL=25;
    public static final int SALIENCE=11;
    public static final int AMPERSAND=19;
    public static final int FLOAT=24;
    public static final int EQUALS=23;
    public static final int INT=12;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=35;
    public static final int NOT=15;
    public static final int AND=13;
    public static final int EOF=-1;
    public static final int HexDigit=31;
    public static final int MULTI_LINE_COMMENT=41;
    public static final int DEFFUNCTION=5;
    public static final int COLON=22;
    public static final int ASSIGN_OP=18;
    public static final int RIGHT_PAREN=7;
    public static final int NAME=6;
    public static final int WS=28;
    public static final int EOL=27;
    public static final int UnicodeEscape=32;
    public static final int LEFT_CURLY=39;
    public static final int DEFRULE=9;
    public static final int OR=14;
    public static final int TILDE=21;
    public static final int TEST=17;
    public static final int LEFT_PAREN=4;
    public static final int DECLARE=29;
    public static final int PIPE=20;
    public static final int VAR=8;
    public static final int EXISTS=16;
    public static final int LEFT_SQUARE=37;
    public static final int OctalEscape=33;
    public static final int EscapeSequence=30;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=36;
    public static final int STRING=10;

        public CLPParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    
    	private PackageDescr packageDescr;
    	private List errors = new ArrayList();
    	private String source = "unknown";
    	private int lineOffset = 0;
    	private DescrFactory factory = new DescrFactory();
    	private boolean parserDebug = false;
    	private FunctionRegistry functionRegistry;	
    	
    	public void setFunctionRegistry(FunctionRegistry functionRegistry) {
    		this.functionRegistry = functionRegistry;
    	}
    	
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
                                                               //mtne.foundNode+ FIXME
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
          



    // $ANTLR start execution_list
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:230:1: execution_list returns [ExecutionEngine engine] : (fc= lisp_list[context, new LispForm(context) ] ) ;
    public final ExecutionEngine execution_list() throws RecognitionException {
        ExecutionEngine engine = null;

        ValueHandler fc = null;


        
        	        engine = new BlockExecutionEngine();
        			BuildContext context = new ExecutionBuildContext( engine, functionRegistry );  	
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:237:3: ( (fc= lisp_list[context, new LispForm(context) ] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:237:3: (fc= lisp_list[context, new LispForm(context) ] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:237:3: (fc= lisp_list[context, new LispForm(context) ] )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:237:4: fc= lisp_list[context, new LispForm(context) ]
            {
            pushFollow(FOLLOW_lisp_list_in_execution_list66);
            fc=lisp_list(context,  new LispForm(context) );
            _fsp--;

             context.addFunction( (FunctionCaller) fc ); 

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return engine;
    }
    // $ANTLR end execution_list


    // $ANTLR start deffunction
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:241:1: deffunction returns [Deffunction function] : loc= LEFT_PAREN DEFFUNCTION ruleName= NAME ( deffunction_params[context] )? (fc= lisp_list[context, new LispForm(context) ] )* RIGHT_PAREN ;
    public final Deffunction deffunction() throws RecognitionException {
        Deffunction function = null;

        Token loc=null;
        Token ruleName=null;
        ValueHandler fc = null;


        
        			BuildContext context;  	
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:245:4: (loc= LEFT_PAREN DEFFUNCTION ruleName= NAME ( deffunction_params[context] )? (fc= lisp_list[context, new LispForm(context) ] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:245:4: loc= LEFT_PAREN DEFFUNCTION ruleName= NAME ( deffunction_params[context] )? (fc= lisp_list[context, new LispForm(context) ] )* RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_deffunction95); 
            match(input,DEFFUNCTION,FOLLOW_DEFFUNCTION_in_deffunction103); 
            ruleName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_deffunction112); 
            
            	    	function = new Deffunction( ruleName.getText() );
            			functionRegistry.addFunction( function );
            	      	context = new ExecutionBuildContext( function, functionRegistry );
            	  	
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:252:5: ( deffunction_params[context] )?
            int alt1=2;
            alt1 = dfa1.predict(input);
            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:252:5: deffunction_params[context]
                    {
                    pushFollow(FOLLOW_deffunction_params_in_deffunction120);
                    deffunction_params(context);
                    _fsp--;


                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:253:5: (fc= lisp_list[context, new LispForm(context) ] )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==LEFT_PAREN) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:253:6: fc= lisp_list[context, new LispForm(context) ]
            	    {
            	    pushFollow(FOLLOW_lisp_list_in_deffunction131);
            	    fc=lisp_list(context,  new LispForm(context) );
            	    _fsp--;

            	     context.addFunction( (FunctionCaller) fc ); 

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_deffunction142); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return function;
    }
    // $ANTLR end deffunction


    // $ANTLR start deffunction_params
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:257:1: deffunction_params[BuildContext context] : loc= LEFT_PAREN (v= VAR )* RIGHT_PAREN ;
    public final void deffunction_params(BuildContext context) throws RecognitionException {
        Token loc=null;
        Token v=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:258:4: (loc= LEFT_PAREN (v= VAR )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:258:4: loc= LEFT_PAREN (v= VAR )* RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_deffunction_params157); 
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:259:4: (v= VAR )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==VAR) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:259:5: v= VAR
            	    {
            	    v=(Token)input.LT(1);
            	    match(input,VAR,FOLLOW_VAR_in_deffunction_params167); 
            	    
            	    		    // this creates a parameter on the underlying function
            	    		 	context.createLocalVariable( v.getText() );
            	    		 

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_deffunction_params180); 

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
    // $ANTLR end deffunction_params


    // $ANTLR start defrule
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:266:1: defrule returns [RuleDescr rule] : loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs] )* '=>' engine= execution_block RIGHT_PAREN ;
    public final RuleDescr defrule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        Token ruleName=null;
        Token documentation=null;
        ExecutionEngine engine = null;


         
        	        rule = null; 
        	        AndDescr lhs = null;
        	        PatternDescr colum = null;
        	        AttributeDescr module = null;	        
        	      
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:273:4: (loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs] )* '=>' engine= execution_block RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:273:4: loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs] )* '=>' engine= execution_block RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_defrule205); 
            match(input,DEFRULE,FOLLOW_DEFRULE_in_defrule213); 
            ruleName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_defrule217); 
             	  			  		
            	  		debug( "start rule: " + ruleName.getText() );
            	  		String ruleStr = ruleName.getText();
            
            	        if ( ruleStr.indexOf("::") >= 0 ) {
            	            String mod = ruleStr.substring(0, ruleStr.indexOf("::"));
            	            ruleStr = ruleStr.substring(ruleStr.indexOf("::")+2);
            				module = new AttributeDescr( "agenda-group", mod );
            				module.setLocation( offset(ruleName.getLine()), ruleName.getCharPositionInLine() );
            				module.setStartCharacter( ((CommonToken)ruleName).getStartIndex() );
            				module.setEndCharacter( ((CommonToken)ruleName).getStopIndex() );
            			}
            		    
            		    rule = new RuleDescr( ruleStr, null ); 
            		    if( module != null ) {
            		    	rule.addAttribute( module );
            		    }
            		        
            			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            			rule.setStartCharacter( ((CommonToken)loc).getStartIndex() ); 
            		
            			// not sure how you define where a LHS starts in clips, so just putting it here for now
              	        	lhs = new AndDescr(); 
            	  	        rule.setLhs( lhs ); 
               		        lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            			lhs.setStartCharacter( ((CommonToken)loc).getStartIndex() );				
            		
            documentation=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_defrule229); 
            
            	    	// do nothing here for now
            		
            pushFollow(FOLLOW_ruleAttribute_in_defrule235);
            ruleAttribute(rule);
            _fsp--;

            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:308:3: ( ce[lhs] )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==LEFT_PAREN||LA4_0==VAR) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:308:3: ce[lhs]
            	    {
            	    pushFollow(FOLLOW_ce_in_defrule243);
            	    ce(lhs);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            match(input,42,FOLLOW_42_in_defrule252); 
            pushFollow(FOLLOW_execution_block_in_defrule261);
            engine=execution_block();
            _fsp--;

             rule.setConsequence( engine ); 
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_defrule270); 

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
    // $ANTLR end defrule


    // $ANTLR start ruleAttribute
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:318:1: ruleAttribute[RuleDescr rule] : ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? ;
    public final void ruleAttribute(RuleDescr rule) throws RecognitionException {
        AttributeDescr d = null;


        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:320:3: ( ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:320:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
            {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:320:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==LEFT_PAREN) ) {
                int LA6_1 = input.LA(2);

                if ( (LA6_1==DECLARE) ) {
                    alt6=1;
                }
            }
            switch (alt6) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:320:5: LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute287); 
                    match(input,DECLARE,FOLLOW_DECLARE_in_ruleAttribute289); 
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:321:4: ( LEFT_PAREN d= salience RIGHT_PAREN )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==LEFT_PAREN) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:321:6: LEFT_PAREN d= salience RIGHT_PAREN
                            {
                            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute296); 
                            pushFollow(FOLLOW_salience_in_ruleAttribute300);
                            d=salience();
                            _fsp--;

                             rule.addAttribute( d ); 
                            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute304); 

                            }
                            break;

                    }

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute311); 

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
    // $ANTLR end ruleAttribute


    // $ANTLR start salience
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:325:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:330:3: (loc= SALIENCE i= INT )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:330:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience341); 
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience345); 
            
            			d = new AttributeDescr( "salience", i.getText() );
            			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
            			d.setEndCharacter( ((CommonToken)i).getStopIndex() );
            		

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


    // $ANTLR start ce
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:340:1: ce[ConditionalElementDescr in_ce] : ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) ;
    public final void ce(ConditionalElementDescr in_ce) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:341:4: ( ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:341:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:341:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            int alt7=7;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case EXISTS:
                    {
                    alt7=4;
                    }
                    break;
                case NOT:
                    {
                    alt7=3;
                    }
                    break;
                case NAME:
                    {
                    alt7=6;
                    }
                    break;
                case OR:
                    {
                    alt7=2;
                    }
                    break;
                case TEST:
                    {
                    alt7=5;
                    }
                    break;
                case AND:
                    {
                    alt7=1;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("341:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 7, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA7_0==VAR) ) {
                alt7=7;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("341:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:341:8: and_ce[in_ce]
                    {
                    pushFollow(FOLLOW_and_ce_in_ce371);
                    and_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:342:7: or_ce[in_ce]
                    {
                    pushFollow(FOLLOW_or_ce_in_ce381);
                    or_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:343:7: not_ce[in_ce]
                    {
                    pushFollow(FOLLOW_not_ce_in_ce390);
                    not_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:344:7: exists_ce[in_ce]
                    {
                    pushFollow(FOLLOW_exists_ce_in_ce399);
                    exists_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:345:8: eval_ce[in_ce]
                    {
                    pushFollow(FOLLOW_eval_ce_in_ce413);
                    eval_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:346:7: normal_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_ce427);
                    normal_pattern(in_ce);
                    _fsp--;


                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:347:7: bound_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_bound_pattern_in_ce436);
                    bound_pattern(in_ce);
                    _fsp--;


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
    // $ANTLR end ce


    // $ANTLR start execution_block
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:351:1: execution_block returns [ExecutionEngine engine] : (fc= lisp_list[context, new LispForm(context) ] )* ;
    public final ExecutionEngine execution_block() throws RecognitionException {
        ExecutionEngine engine = null;

        ValueHandler fc = null;


        
        	        engine = new BlockExecutionEngine();
        			BuildContext context = new ExecutionBuildContext( engine, functionRegistry );  	
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:358:3: ( (fc= lisp_list[context, new LispForm(context) ] )* )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:358:3: (fc= lisp_list[context, new LispForm(context) ] )*
            {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:358:3: (fc= lisp_list[context, new LispForm(context) ] )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==LEFT_PAREN) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:358:4: fc= lisp_list[context, new LispForm(context) ]
            	    {
            	    pushFollow(FOLLOW_lisp_list_in_execution_block468);
            	    fc=lisp_list(context,  new LispForm(context) );
            	    _fsp--;

            	     context.addFunction( (FunctionCaller) fc ); 

            	    }
            	    break;

            	default :
            	    break loop8;
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
        return engine;
    }
    // $ANTLR end execution_block


    // $ANTLR start and_ce
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:361:1: and_ce[ConditionalElementDescr in_ce] : LEFT_PAREN AND ( ce[andDescr] )+ RIGHT_PAREN ;
    public final void and_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                AndDescr andDescr= null;        
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:365:4: ( LEFT_PAREN AND ( ce[andDescr] )+ RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:365:4: LEFT_PAREN AND ( ce[andDescr] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_and_ce496); 
            match(input,AND,FOLLOW_AND_in_and_ce501); 
            
            	    	andDescr = new AndDescr();
            			in_ce.addDescr( andDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:370:3: ( ce[andDescr] )+
            int cnt9=0;
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==LEFT_PAREN||LA9_0==VAR) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:370:3: ce[andDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_and_ce507);
            	    ce(andDescr);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_and_ce516); 

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
    // $ANTLR end and_ce


    // $ANTLR start or_ce
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:374:1: or_ce[ConditionalElementDescr in_ce] : LEFT_PAREN OR ( ce[orDescr] )+ RIGHT_PAREN ;
    public final void or_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                OrDescr orDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:378:4: ( LEFT_PAREN OR ( ce[orDescr] )+ RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:378:4: LEFT_PAREN OR ( ce[orDescr] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_or_ce544); 
            match(input,OR,FOLLOW_OR_in_or_ce549); 
            
            	    	orDescr = new OrDescr();
            			in_ce.addDescr( orDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:383:3: ( ce[orDescr] )+
            int cnt10=0;
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==LEFT_PAREN||LA10_0==VAR) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:383:3: ce[orDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_or_ce555);
            	    ce(orDescr);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_or_ce564); 

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
    // $ANTLR end or_ce


    // $ANTLR start not_ce
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:387:1: not_ce[ConditionalElementDescr in_ce] : LEFT_PAREN NOT ce[notDescr] RIGHT_PAREN ;
    public final void not_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                NotDescr notDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:391:4: ( LEFT_PAREN NOT ce[notDescr] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:391:4: LEFT_PAREN NOT ce[notDescr] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_not_ce592); 
            match(input,NOT,FOLLOW_NOT_in_not_ce597); 
            
            			notDescr = new NotDescr();
            		    in_ce.addDescr( notDescr );
            		
            pushFollow(FOLLOW_ce_in_not_ce603);
            ce(notDescr);
            _fsp--;

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_not_ce611); 

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
    // $ANTLR end not_ce


    // $ANTLR start exists_ce
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:400:1: exists_ce[ConditionalElementDescr in_ce] : LEFT_PAREN EXISTS ce[existsDescr] RIGHT_PAREN ;
    public final void exists_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                ExistsDescr existsDescr= null;        
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:404:4: ( LEFT_PAREN EXISTS ce[existsDescr] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:404:4: LEFT_PAREN EXISTS ce[existsDescr] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_exists_ce640); 
            match(input,EXISTS,FOLLOW_EXISTS_in_exists_ce645); 
            
            		    existsDescr = new ExistsDescr();
            		    in_ce.addDescr( existsDescr );
            		
            pushFollow(FOLLOW_ce_in_exists_ce651);
            ce(existsDescr);
            _fsp--;

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_exists_ce659); 

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
    // $ANTLR end exists_ce


    // $ANTLR start eval_ce
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:413:1: eval_ce[ConditionalElementDescr in_ce] : LEFT_PAREN TEST fc= lisp_list[context, new LispForm(context)] RIGHT_PAREN ;
    public final void eval_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        ValueHandler fc = null;


        
                EvalDescr evalDescr= null;    
           		ExecutionEngine engine = new CLPEval();     
        		BuildContext context = new ExecutionBuildContext( engine, functionRegistry );   		         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:419:4: ( LEFT_PAREN TEST fc= lisp_list[context, new LispForm(context)] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:419:4: LEFT_PAREN TEST fc= lisp_list[context, new LispForm(context)] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_eval_ce687); 
            match(input,TEST,FOLLOW_TEST_in_eval_ce692); 
            
            		    evalDescr = new EvalDescr();
            		    in_ce.addDescr( evalDescr );
            		
            pushFollow(FOLLOW_lisp_list_in_eval_ce700);
            fc=lisp_list(context,  new LispForm(context));
            _fsp--;

            					
            		    engine.addFunction( (FunctionCaller) fc );		
            			evalDescr.setContent( engine );			
            		
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_eval_ce711); 

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
    // $ANTLR end eval_ce


    // $ANTLR start normal_pattern
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:431:1: normal_pattern[ConditionalElementDescr in_ce] : LEFT_PAREN name= NAME ( field_constriant[pattern] )* RIGHT_PAREN ;
    public final void normal_pattern(ConditionalElementDescr in_ce) throws RecognitionException {
        Token name=null;

        
                PatternDescr pattern = null;
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:435:4: ( LEFT_PAREN name= NAME ( field_constriant[pattern] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:435:4: LEFT_PAREN name= NAME ( field_constriant[pattern] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_normal_pattern739); 
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_normal_pattern746); 
            
            			pattern = new PatternDescr(name.getText());
            			in_ce.addDescr( pattern );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:440:3: ( field_constriant[pattern] )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==LEFT_PAREN) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:440:3: field_constriant[pattern]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_normal_pattern752);
            	    field_constriant(pattern);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_normal_pattern762); 

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
    // $ANTLR end normal_pattern


    // $ANTLR start bound_pattern
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:446:1: bound_pattern[ConditionalElementDescr in_ce] : var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[pattern] )* RIGHT_PAREN ;
    public final void bound_pattern(ConditionalElementDescr in_ce) throws RecognitionException {
        Token var=null;
        Token name=null;

        
                PatternDescr pattern = null;
                String identifier = null;
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:451:4: (var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[pattern] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:451:4: var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[pattern] )* RIGHT_PAREN
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_bound_pattern790); 
            
            			identifier = var.getText();
            		
            match(input,ASSIGN_OP,FOLLOW_ASSIGN_OP_in_bound_pattern796); 
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_bound_pattern798); 
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_bound_pattern802); 
            
            			pattern = new PatternDescr(name.getText());
            			pattern.setIdentifier( identifier );
            			in_ce.addDescr( pattern );	    
            		
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:460:3: ( field_constriant[pattern] )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==LEFT_PAREN) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:460:3: field_constriant[pattern]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_bound_pattern811);
            	    field_constriant(pattern);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_bound_pattern818); 

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
    // $ANTLR end bound_pattern


    // $ANTLR start field_constriant
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:464:1: field_constriant[PatternDescr pattern] : LEFT_PAREN f= NAME connected_constraint[fc, pattern] RIGHT_PAREN ;
    public final void field_constriant(PatternDescr pattern) throws RecognitionException {
        Token f=null;

        
             	List list = new ArrayList();
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		String op = "==";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:472:3: ( LEFT_PAREN f= NAME connected_constraint[fc, pattern] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:472:3: LEFT_PAREN f= NAME connected_constraint[fc, pattern] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_field_constriant849); 
            f=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_field_constriant853); 
            
            			fc = new FieldConstraintDescr(f.getText());
            			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
            			pattern.addDescr( fc );			
            		
            pushFollow(FOLLOW_connected_constraint_in_field_constriant868);
            connected_constraint(fc,  pattern);
            _fsp--;

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_field_constriant874); 

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
    // $ANTLR end field_constriant


    // $ANTLR start connected_constraint
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:484:1: connected_constraint[FieldConstraintDescr fc, PatternDescr pattern] : restriction[fc, pattern] ( AMPERSAND connected_constraint[fc, pattern] | PIPE connected_constraint[fc, pattern] )? ;
    public final void connected_constraint(FieldConstraintDescr fc, PatternDescr pattern) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:486:2: ( restriction[fc, pattern] ( AMPERSAND connected_constraint[fc, pattern] | PIPE connected_constraint[fc, pattern] )? )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:486:2: restriction[fc, pattern] ( AMPERSAND connected_constraint[fc, pattern] | PIPE connected_constraint[fc, pattern] )?
            {
            pushFollow(FOLLOW_restriction_in_connected_constraint890);
            restriction(fc,  pattern);
            _fsp--;

            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:487:2: ( AMPERSAND connected_constraint[fc, pattern] | PIPE connected_constraint[fc, pattern] )?
            int alt13=3;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==AMPERSAND) ) {
                alt13=1;
            }
            else if ( (LA13_0==PIPE) ) {
                alt13=2;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:488:6: AMPERSAND connected_constraint[fc, pattern]
                    {
                    match(input,AMPERSAND,FOLLOW_AMPERSAND_in_connected_constraint902); 
                     fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND)); 
                    pushFollow(FOLLOW_connected_constraint_in_connected_constraint911);
                    connected_constraint(fc,  pattern);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:491:6: PIPE connected_constraint[fc, pattern]
                    {
                    match(input,PIPE,FOLLOW_PIPE_in_connected_constraint923); 
                    fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR)); 
                    pushFollow(FOLLOW_connected_constraint_in_connected_constraint932);
                    connected_constraint(fc,  pattern);
                    _fsp--;


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
    // $ANTLR end connected_constraint


    // $ANTLR start restriction
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:496:1: restriction[FieldConstraintDescr fc, PatternDescr pattern] : ( TILDE )? ( predicate_constraint[op, pattern] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) ;
    public final void restriction(FieldConstraintDescr fc, PatternDescr pattern) throws RecognitionException {
        String lc = null;


        
        			String op = "==";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:500:4: ( ( TILDE )? ( predicate_constraint[op, pattern] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:500:4: ( TILDE )? ( predicate_constraint[op, pattern] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:500:4: ( TILDE )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==TILDE) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:500:5: TILDE
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_restriction958); 
                    op = "!=";

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:501:3: ( predicate_constraint[op, pattern] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            int alt15=4;
            switch ( input.LA(1) ) {
            case COLON:
                {
                alt15=1;
                }
                break;
            case EQUALS:
                {
                alt15=2;
                }
                break;
            case VAR:
                {
                alt15=3;
                }
                break;
            case NAME:
            case STRING:
            case INT:
            case FLOAT:
            case BOOL:
            case NULL:
                {
                alt15=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("501:3: ( predicate_constraint[op, pattern] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:501:5: predicate_constraint[op, pattern]
                    {
                    pushFollow(FOLLOW_predicate_constraint_in_restriction974);
                    predicate_constraint(op,  pattern);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:502:7: return_value_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_return_value_restriction_in_restriction990);
                    return_value_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:503:7: variable_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_variable_restriction_in_restriction999);
                    variable_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:504:8: lc= literal_restriction
                    {
                    pushFollow(FOLLOW_literal_restriction_in_restriction1011);
                    lc=literal_restriction();
                    _fsp--;

                    
                         	    			fc.addRestriction( new LiteralRestrictionDescr(op, lc, true) );
                    		      		op = "==";
                    		        

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
    // $ANTLR end restriction


    // $ANTLR start predicate_constraint
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:511:1: predicate_constraint[String op, PatternDescr pattern] : COLON fc= lisp_list[context, new LispForm(context)] ;
    public final void predicate_constraint(String op, PatternDescr pattern) throws RecognitionException {
        ValueHandler fc = null;


        
           		ExecutionEngine engine = new CLPPredicate();
        		BuildContext context = new ExecutionBuildContext( engine, functionRegistry );    
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:516:4: ( COLON fc= lisp_list[context, new LispForm(context)] )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:516:4: COLON fc= lisp_list[context, new LispForm(context)]
            {
            match(input,COLON,FOLLOW_COLON_in_predicate_constraint1053); 
            pushFollow(FOLLOW_lisp_list_in_predicate_constraint1059);
            fc=lisp_list(context,  new LispForm(context));
            _fsp--;

            	
            		    engine.addFunction( (FunctionCaller) fc );
            			pattern.addDescr( new PredicateDescr( engine ) );
            		

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
    // $ANTLR end predicate_constraint


    // $ANTLR start return_value_restriction
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:525:1: return_value_restriction[String op, FieldConstraintDescr fc] : EQUALS func= lisp_list[context, new LispForm(context)] ;
    public final void return_value_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {
        ValueHandler func = null;


        
        		ExecutionEngine engine = new CLPReturnValue();
        		BuildContext context = new ExecutionBuildContext( engine, functionRegistry );
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:530:4: ( EQUALS func= lisp_list[context, new LispForm(context)] )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:530:4: EQUALS func= lisp_list[context, new LispForm(context)]
            {
            match(input,EQUALS,FOLLOW_EQUALS_in_return_value_restriction1085); 
            pushFollow(FOLLOW_lisp_list_in_return_value_restriction1092);
            func=lisp_list(context,  new LispForm(context));
            _fsp--;

            					
               		    engine.addFunction( (FunctionCaller) func );
            			fc.addRestriction( new ReturnValueRestrictionDescr (op, engine ) );
            		

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
    // $ANTLR end return_value_restriction


    // $ANTLR start variable_restriction
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:537:1: variable_restriction[String op, FieldConstraintDescr fc] : var= VAR ;
    public final void variable_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {
        Token var=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:538:4: (var= VAR )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:538:4: var= VAR
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_variable_restriction1113); 
            
            			fc.addRestriction( new VariableRestrictionDescr(op, var.getText()) );
            		

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
    // $ANTLR end variable_restriction


    // $ANTLR start literal_restriction
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:544:1: literal_restriction returns [String text] : t= literal ;
    public final String literal_restriction() throws RecognitionException {
        String text = null;

        String t = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:549:6: (t= literal )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:549:6: t= literal
            {
            pushFollow(FOLLOW_literal_in_literal_restriction1146);
            t=literal();
            _fsp--;

            
            	    	text = t;
            	    

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
    // $ANTLR end literal_restriction


    // $ANTLR start lisp_list
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:1: lisp_list[BuildContext context, LispList list] returns [ValueHandler valueHandler] : LEFT_PAREN (a= lisp_atom[context] | a= lisp_list[context, list.createList()] )* RIGHT_PAREN ;
    public final ValueHandler lisp_list(BuildContext context, LispList list) throws RecognitionException {
        ValueHandler valueHandler = null;

        ValueHandler a = null;


        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:555:4: ( LEFT_PAREN (a= lisp_atom[context] | a= lisp_list[context, list.createList()] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:555:4: LEFT_PAREN (a= lisp_atom[context] | a= lisp_list[context, list.createList()] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lisp_list1165); 
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:556:3: (a= lisp_atom[context] | a= lisp_list[context, list.createList()] )*
            loop16:
            do {
                int alt16=3;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==NAME||LA16_0==VAR||LA16_0==STRING||LA16_0==INT||(LA16_0>=FLOAT && LA16_0<=NULL)) ) {
                    alt16=1;
                }
                else if ( (LA16_0==LEFT_PAREN) ) {
                    alt16=2;
                }


                switch (alt16) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:556:6: a= lisp_atom[context]
            	    {
            	    pushFollow(FOLLOW_lisp_atom_in_lisp_list1175);
            	    a=lisp_atom(context);
            	    _fsp--;

            	     list.add( a ); 

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:557:6: a= lisp_list[context, list.createList()]
            	    {
            	    pushFollow(FOLLOW_lisp_list_in_lisp_list1191);
            	    a=lisp_list(context,  list.createList());
            	    _fsp--;

            	     list.add( a ); 

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lisp_list1221); 
             valueHandler = list.getValueHandler(); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return valueHandler;
    }
    // $ANTLR end lisp_list


    // $ANTLR start lisp_atom
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:563:1: lisp_atom[BuildContext context] returns [ValueHandler value] : (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL ) ;
    public final ValueHandler lisp_atom(BuildContext context) throws RecognitionException {
        ValueHandler value = null;

        Token t=null;

        
        		value  =  null;		
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:568:3: ( (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:568:3: (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:568:3: (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL )
            int alt17=7;
            switch ( input.LA(1) ) {
            case VAR:
                {
                alt17=1;
                }
                break;
            case STRING:
                {
                alt17=2;
                }
                break;
            case NAME:
                {
                alt17=3;
                }
                break;
            case FLOAT:
                {
                alt17=4;
                }
                break;
            case INT:
                {
                alt17=5;
                }
                break;
            case BOOL:
                {
                alt17=6;
                }
                break;
            case NULL:
                {
                alt17=7;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("568:3: (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL )", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:568:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_lisp_atom1258); 
                     value = context.getVariableValueHandler(t.getText() ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:569:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_lisp_atom1270); 
                     value = new ObjectValueHandler( getString( t ) ); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:570:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_lisp_atom1282); 
                     value = new ObjectValueHandler( t.getText() ); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:571:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_lisp_atom1297); 
                     value = new DoubleValueHandler( t.getText() ); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:572:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_lisp_atom1309); 
                     value = new LongValueHandler( t.getText() ); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:573:6: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lisp_atom1325); 
                     value = new BooleanValueHandler( t.getText() ); 

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:574:6: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_lisp_atom1343); 
                     value = ObjectValueHandler.NULL; 

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
    // $ANTLR end lisp_atom


    // $ANTLR start literal
    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:578:1: literal returns [String text] : (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final String literal() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:582:4: ( (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:582:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:582:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt18=6;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt18=1;
                }
                break;
            case NAME:
                {
                alt18=2;
                }
                break;
            case INT:
                {
                alt18=3;
                }
                break;
            case FLOAT:
                {
                alt18=4;
                }
                break;
            case BOOL:
                {
                alt18=5;
                }
                break;
            case NULL:
                {
                alt18=6;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("582:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:582:8: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal1379); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:583:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_literal1392); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:584:7: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal1408); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:585:7: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal1423); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:586:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal1436); 
                     text = t.getText(); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:587:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal1450); 
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
    // $ANTLR end literal


    protected DFA1 dfa1 = new DFA1(this);
    static final String DFA1_eotS =
        "\5\uffff";
    static final String DFA1_eofS =
        "\5\uffff";
    static final String DFA1_minS =
        "\2\4\1\uffff\1\4\1\uffff";
    static final String DFA1_maxS =
        "\1\7\1\32\1\uffff\1\32\1\uffff";
    static final String DFA1_acceptS =
        "\2\uffff\1\2\1\uffff\1\1";
    static final String DFA1_specialS =
        "\5\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\1\2\uffff\1\2",
            "\1\2\1\uffff\1\2\1\4\1\3\1\uffff\1\2\1\uffff\1\2\13\uffff\3"+
            "\2",
            "",
            "\1\2\1\uffff\1\2\1\4\1\3\1\uffff\1\2\1\uffff\1\2\13\uffff\3"+
            "\2",
            ""
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "252:5: ( deffunction_params[context] )?";
        }
    }
 

    public static final BitSet FOLLOW_lisp_list_in_execution_list66 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_deffunction95 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DEFFUNCTION_in_deffunction103 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_deffunction112 = new BitSet(new long[]{0x0000000000000090L});
    public static final BitSet FOLLOW_deffunction_params_in_deffunction120 = new BitSet(new long[]{0x0000000000000090L});
    public static final BitSet FOLLOW_lisp_list_in_deffunction131 = new BitSet(new long[]{0x0000000000000090L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_deffunction142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_deffunction_params157 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_VAR_in_deffunction_params167 = new BitSet(new long[]{0x0000000000000180L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_deffunction_params180 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_defrule205 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_DEFRULE_in_defrule213 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_defrule217 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_STRING_in_defrule229 = new BitSet(new long[]{0x0000040000000110L});
    public static final BitSet FOLLOW_ruleAttribute_in_defrule235 = new BitSet(new long[]{0x0000040000000110L});
    public static final BitSet FOLLOW_ce_in_defrule243 = new BitSet(new long[]{0x0000040000000110L});
    public static final BitSet FOLLOW_42_in_defrule252 = new BitSet(new long[]{0x0000000000000090L});
    public static final BitSet FOLLOW_execution_block_in_defrule261 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_defrule270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute287 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_DECLARE_in_ruleAttribute289 = new BitSet(new long[]{0x0000000000000090L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute296 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_salience_in_ruleAttribute300 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute304 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience341 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_INT_in_salience345 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_ce371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_ce381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_ce390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_ce399 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_ce413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_ce427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bound_pattern_in_ce436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lisp_list_in_execution_block468 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_and_ce496 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_AND_in_and_ce501 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_and_ce507 = new BitSet(new long[]{0x0000000000000190L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_and_ce516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_or_ce544 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_OR_in_or_ce549 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_or_ce555 = new BitSet(new long[]{0x0000000000000190L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_or_ce564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_not_ce592 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_NOT_in_not_ce597 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_not_ce603 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_not_ce611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_exists_ce640 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_EXISTS_in_exists_ce645 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_exists_ce651 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_exists_ce659 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_eval_ce687 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_TEST_in_eval_ce692 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_list_in_eval_ce700 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_eval_ce711 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_normal_pattern739 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_normal_pattern746 = new BitSet(new long[]{0x0000000000000090L});
    public static final BitSet FOLLOW_field_constriant_in_normal_pattern752 = new BitSet(new long[]{0x0000000000000090L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_normal_pattern762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_bound_pattern790 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_ASSIGN_OP_in_bound_pattern796 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_bound_pattern798 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_bound_pattern802 = new BitSet(new long[]{0x0000000000000090L});
    public static final BitSet FOLLOW_field_constriant_in_bound_pattern811 = new BitSet(new long[]{0x0000000000000090L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_bound_pattern818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_field_constriant849 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_field_constriant853 = new BitSet(new long[]{0x0000000007E01540L});
    public static final BitSet FOLLOW_connected_constraint_in_field_constriant868 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_field_constriant874 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_restriction_in_connected_constraint890 = new BitSet(new long[]{0x0000000000180002L});
    public static final BitSet FOLLOW_AMPERSAND_in_connected_constraint902 = new BitSet(new long[]{0x0000000007E01540L});
    public static final BitSet FOLLOW_connected_constraint_in_connected_constraint911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PIPE_in_connected_constraint923 = new BitSet(new long[]{0x0000000007E01540L});
    public static final BitSet FOLLOW_connected_constraint_in_connected_constraint932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_restriction958 = new BitSet(new long[]{0x0000000007C01540L});
    public static final BitSet FOLLOW_predicate_constraint_in_restriction974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_return_value_restriction_in_restriction990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_restriction_in_restriction999 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_restriction_in_restriction1011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_predicate_constraint1053 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_list_in_predicate_constraint1059 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_return_value_restriction1085 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_list_in_return_value_restriction1092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_variable_restriction1113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_literal_restriction1146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lisp_list1165 = new BitSet(new long[]{0x00000000070015D0L});
    public static final BitSet FOLLOW_lisp_atom_in_lisp_list1175 = new BitSet(new long[]{0x00000000070015D0L});
    public static final BitSet FOLLOW_lisp_list_in_lisp_list1191 = new BitSet(new long[]{0x00000000070015D0L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lisp_list1221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_lisp_atom1258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_lisp_atom1270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_lisp_atom1282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_lisp_atom1297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_lisp_atom1309 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_lisp_atom1325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_lisp_atom1343 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal1379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_literal1392 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal1408 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal1423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal1436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal1450 = new BitSet(new long[]{0x0000000000000002L});

}