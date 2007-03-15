// $ANTLR 3.0b5 D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-15 13:31:31

	package org.drools.clp;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LEFT_PAREN", "DEFRULE", "SYMBOL", "STRING", "RIGHT_PAREN", "SALIENCE", "INT", "AND", "OR", "NOT", "EXISTS", "TEST", "VAR", "AMPERSAND", "PIPE", "TILDE", "FLOAT", "BOOL", "NULL", "MISC", "EOL", "WS", "DECLARE", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "MULTI_LINE_COMMENT", "';'", "'=>'", "'<-'", "':'", "'='", "'modify'"
    };
    public static final int EXISTS=14;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=36;
    public static final int BOOL=21;
    public static final int DEFRULE=5;
    public static final int HexDigit=28;
    public static final int WS=25;
    public static final int MISC=23;
    public static final int STRING=7;
    public static final int FLOAT=20;
    public static final int TILDE=19;
    public static final int OR=12;
    public static final int PIPE=18;
    public static final int VAR=16;
    public static final int UnicodeEscape=29;
    public static final int AND=11;
    public static final int EscapeSequence=27;
    public static final int INT=10;
    public static final int EOF=-1;
    public static final int EOL=24;
    public static final int NULL=22;
    public static final int SYMBOL=6;
    public static final int LEFT_SQUARE=33;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=31;
    public static final int OctalEscape=30;
    public static final int SALIENCE=9;
    public static final int MULTI_LINE_COMMENT=37;
    public static final int TEST=15;
    public static final int AMPERSAND=17;
    public static final int DECLARE=26;
    public static final int RIGHT_PAREN=8;
    public static final int NOT=13;
    public static final int LEFT_CURLY=35;
    public static final int RIGHT_SQUARE=34;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=32;

        public CLPParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    
    	private PackageDescr packageDescr;
    	private List errors = new ArrayList();
    	private String source = "unknown";
    	private int lineOffset = 0;
    	private DescrFactory factory = new DescrFactory();
    	private boolean parserDebug = false;
    	
    	// THE FOLLOWING LINE IS A DUMMY ATTRIBUTE TO WORK AROUND AN ANTLR BUG
    	private BaseDescr from = null;
    	
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:167:1: opt_semicolon : ( ';' )? ;
    public void opt_semicolon() throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:168:4: ( ( ';' )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:168:4: ( ';' )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:168:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);
            if ( (LA1_0==38) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:168:4: ';'
                    {
                    match(input,38,FOLLOW_38_in_opt_semicolon38); 

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


    // $ANTLR start rule
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:211:1: rule returns [RuleDescr rule] : loc= LEFT_PAREN DEFRULE ruleName= SYMBOL documentation= STRING ruleAttribute[rule] ( lhs[lhs] )* '=>' rhs[rule] RIGHT_PAREN ;
    public RuleDescr rule() throws RecognitionException {   
        RuleDescr rule = null;

        Token loc=null;
        Token ruleName=null;
        Token documentation=null;

         
        	        rule = null; 
        	        AndDescr lhs = null;
        	        ColumnDescr colum = null;
        	        AttributeDescr module = null;	        
        	      
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:218:4: (loc= LEFT_PAREN DEFRULE ruleName= SYMBOL documentation= STRING ruleAttribute[rule] ( lhs[lhs] )* '=>' rhs[rule] RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:218:4: loc= LEFT_PAREN DEFRULE ruleName= SYMBOL documentation= STRING ruleAttribute[rule] ( lhs[lhs] )* '=>' rhs[rule] RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_rule65); 
            match(input,DEFRULE,FOLLOW_DEFRULE_in_rule73); 
            ruleName=(Token)input.LT(1);
            match(input,SYMBOL,FOLLOW_SYMBOL_in_rule77); 
             	  			  		
            	  		debug( "start rule: " + ruleName.getText() );
            	  		String ruleStr = ruleName.getText();
            
            	        	if ( ruleStr.indexOf("::") >= 0 ) {
            	        	        String mod = ruleStr.substring(0, ruleStr.indexOf("::")+2);
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
            match(input,STRING,FOLLOW_STRING_in_rule89); 
            
            	    	// do nothing here for now
            		
            pushFollow(FOLLOW_ruleAttribute_in_rule95);
            ruleAttribute(rule);
            _fsp--;

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:252:3: ( lhs[lhs] )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( (LA2_0==LEFT_PAREN||LA2_0==VAR) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:252:3: lhs[lhs]
            	    {
            	    pushFollow(FOLLOW_lhs_in_rule103);
            	    lhs(lhs);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            match(input,39,FOLLOW_39_in_rule112); 
            pushFollow(FOLLOW_rhs_in_rule119);
            rhs(rule);
            _fsp--;

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_rule127); 

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


    // $ANTLR start ruleAttribute
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:262:1: ruleAttribute[RuleDescr rule] : ( LEFT_PAREN 'declare' LEFT_PAREN d= salience RIGHT_PAREN RIGHT_PAREN )? ;
    public void ruleAttribute(RuleDescr rule) throws RecognitionException {   
        AttributeDescr d = null;


        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:264:3: ( ( LEFT_PAREN 'declare' LEFT_PAREN d= salience RIGHT_PAREN RIGHT_PAREN )? )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:264:3: ( LEFT_PAREN 'declare' LEFT_PAREN d= salience RIGHT_PAREN RIGHT_PAREN )?
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:264:3: ( LEFT_PAREN 'declare' LEFT_PAREN d= salience RIGHT_PAREN RIGHT_PAREN )?
            int alt3=2;
            int LA3_0 = input.LA(1);
            if ( (LA3_0==LEFT_PAREN) ) {
                int LA3_1 = input.LA(2);
                if ( (LA3_1==DECLARE) ) {
                    alt3=1;
                }
            }
            switch (alt3) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:264:5: LEFT_PAREN 'declare' LEFT_PAREN d= salience RIGHT_PAREN RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute144); 
                    match(input,DECLARE,FOLLOW_DECLARE_in_ruleAttribute146); 
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute151); 
                    pushFollow(FOLLOW_salience_in_ruleAttribute155);
                    d=salience();
                    _fsp--;

                     rule.addAttribute( d ); 
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute159); 
                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute163); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:269:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public AttributeDescr salience() throws RecognitionException {   
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:274:3: (loc= SALIENCE i= INT )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:274:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience193); 
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience197); 
            
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


    // $ANTLR start lhs
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:284:1: lhs[ConditionalElementDescr in_ce] : ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) ;
    public void lhs(ConditionalElementDescr in_ce) throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:285:4: ( ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:285:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:285:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            int alt4=7;
            int LA4_0 = input.LA(1);
            if ( (LA4_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case NOT:
                    alt4=3;
                    break;
                case AND:
                    alt4=1;
                    break;
                case EXISTS:
                    alt4=4;
                    break;
                case TEST:
                    alt4=5;
                    break;
                case OR:
                    alt4=2;
                    break;
                case SYMBOL:
                    alt4=6;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("285:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 4, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA4_0==VAR) ) {
                alt4=7;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("285:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:285:8: and_ce[in_ce]
                    {
                    pushFollow(FOLLOW_and_ce_in_lhs224);
                    and_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:286:7: or_ce[in_ce]
                    {
                    pushFollow(FOLLOW_or_ce_in_lhs234);
                    or_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:287:7: not_ce[in_ce]
                    {
                    pushFollow(FOLLOW_not_ce_in_lhs243);
                    not_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:288:7: exists_ce[in_ce]
                    {
                    pushFollow(FOLLOW_exists_ce_in_lhs252);
                    exists_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:289:8: eval_ce[in_ce]
                    {
                    pushFollow(FOLLOW_eval_ce_in_lhs266);
                    eval_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:290:7: normal_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_lhs280);
                    normal_pattern(in_ce);
                    _fsp--;


                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:291:7: bound_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_bound_pattern_in_lhs289);
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
    // $ANTLR end lhs


    // $ANTLR start rhs
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:295:1: rhs[RuleDescr rule] : ( function[context] )* ;
    public void rhs(RuleDescr rule) throws RecognitionException {   
        
        	        ExecutionEngine engine = new BlockExecutionEngine();
        			ExecutionBuildContext context = new ExecutionBuildContext( engine );  	
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:302:4: ( ( function[context] )* )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:302:4: ( function[context] )*
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:302:4: ( function[context] )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);
                if ( (LA5_0==LEFT_PAREN) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:302:4: function[context]
            	    {
            	    pushFollow(FOLLOW_function_in_rhs317);
            	    function(context);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

             rule.setConsequence( engine ); 

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
    // $ANTLR end rhs


    // $ANTLR start ce
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:305:1: ce[ConditionalElementDescr in_ce] : ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] ) ;
    public void ce(ConditionalElementDescr in_ce) throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:306:4: ( ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:306:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:306:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )
            int alt6=6;
            int LA6_0 = input.LA(1);
            if ( (LA6_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case TEST:
                    alt6=5;
                    break;
                case SYMBOL:
                    alt6=6;
                    break;
                case AND:
                    alt6=1;
                    break;
                case NOT:
                    alt6=3;
                    break;
                case OR:
                    alt6=2;
                    break;
                case EXISTS:
                    alt6=4;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("306:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )", 6, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("306:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:306:8: and_ce[in_ce]
                    {
                    pushFollow(FOLLOW_and_ce_in_ce341);
                    and_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:307:10: or_ce[in_ce]
                    {
                    pushFollow(FOLLOW_or_ce_in_ce354);
                    or_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:308:7: not_ce[in_ce]
                    {
                    pushFollow(FOLLOW_not_ce_in_ce363);
                    not_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:309:7: exists_ce[in_ce]
                    {
                    pushFollow(FOLLOW_exists_ce_in_ce372);
                    exists_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:310:8: eval_ce[in_ce]
                    {
                    pushFollow(FOLLOW_eval_ce_in_ce386);
                    eval_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:311:10: normal_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_ce410);
                    normal_pattern(in_ce);
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


    // $ANTLR start and_ce
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:316:1: and_ce[ConditionalElementDescr in_ce] : LEFT_PAREN AND ( ce[andDescr] )* RIGHT_PAREN ;
    public void and_ce(ConditionalElementDescr in_ce) throws RecognitionException {   
        
                AndDescr andDescr= null;        
            
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:320:4: ( LEFT_PAREN AND ( ce[andDescr] )* RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:320:4: LEFT_PAREN AND ( ce[andDescr] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_and_ce442); 
            match(input,AND,FOLLOW_AND_in_and_ce447); 
            
            	    	andDescr = new AndDescr();
            			in_ce.addDescr( andDescr );
            		
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:325:3: ( ce[andDescr] )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);
                if ( (LA7_0==LEFT_PAREN) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:325:3: ce[andDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_and_ce453);
            	    ce(andDescr);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_and_ce462); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:329:1: or_ce[ConditionalElementDescr in_ce] : LEFT_PAREN OR ( ce[orDescr] )* RIGHT_PAREN ;
    public void or_ce(ConditionalElementDescr in_ce) throws RecognitionException {   
        
                OrDescr orDescr= null;         
            
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:333:4: ( LEFT_PAREN OR ( ce[orDescr] )* RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:333:4: LEFT_PAREN OR ( ce[orDescr] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_or_ce490); 
            match(input,OR,FOLLOW_OR_in_or_ce495); 
            
            	    	orDescr = new OrDescr();
            			in_ce.addDescr( orDescr );
            		
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:338:3: ( ce[orDescr] )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);
                if ( (LA8_0==LEFT_PAREN) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:338:3: ce[orDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_or_ce501);
            	    ce(orDescr);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_or_ce510); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:342:1: not_ce[ConditionalElementDescr in_ce] : LEFT_PAREN NOT ( ce[notDescr] )? RIGHT_PAREN ;
    public void not_ce(ConditionalElementDescr in_ce) throws RecognitionException {   
        
                NotDescr notDescr= null;         
            
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:346:4: ( LEFT_PAREN NOT ( ce[notDescr] )? RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:346:4: LEFT_PAREN NOT ( ce[notDescr] )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_not_ce538); 
            match(input,NOT,FOLLOW_NOT_in_not_ce543); 
            
            			notDescr = new NotDescr();
            		    in_ce.addDescr( notDescr );
            		
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:351:3: ( ce[notDescr] )?
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( (LA9_0==LEFT_PAREN) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:351:3: ce[notDescr]
                    {
                    pushFollow(FOLLOW_ce_in_not_ce549);
                    ce(notDescr);
                    _fsp--;


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_not_ce558); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:355:1: exists_ce[ConditionalElementDescr in_ce] : LEFT_PAREN EXISTS ( ce[existsDescr] )? RIGHT_PAREN ;
    public void exists_ce(ConditionalElementDescr in_ce) throws RecognitionException {   
        
                ExistsDescr existsDescr= null;        
            
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:359:4: ( LEFT_PAREN EXISTS ( ce[existsDescr] )? RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:359:4: LEFT_PAREN EXISTS ( ce[existsDescr] )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_exists_ce587); 
            match(input,EXISTS,FOLLOW_EXISTS_in_exists_ce592); 
            
            		    existsDescr = new ExistsDescr();
            		    in_ce.addDescr( existsDescr );
            		
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:364:3: ( ce[existsDescr] )?
            int alt10=2;
            int LA10_0 = input.LA(1);
            if ( (LA10_0==LEFT_PAREN) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:364:3: ce[existsDescr]
                    {
                    pushFollow(FOLLOW_ce_in_exists_ce598);
                    ce(existsDescr);
                    _fsp--;


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_exists_ce607); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:368:1: eval_ce[ConditionalElementDescr in_ce] : LEFT_PAREN TEST function[context] RIGHT_PAREN ;
    public void eval_ce(ConditionalElementDescr in_ce) throws RecognitionException {   
        
                EvalDescr evalDescr= null;    
           		ExecutionEngine engine = new CLPEval();     
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );   		         
            
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:374:4: ( LEFT_PAREN TEST function[context] RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:374:4: LEFT_PAREN TEST function[context] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_eval_ce635); 
            match(input,TEST,FOLLOW_TEST_in_eval_ce640); 
            
            		    evalDescr = new EvalDescr();
            		    in_ce.addDescr( evalDescr );
            		
            pushFollow(FOLLOW_function_in_eval_ce646);
            function(context);
            _fsp--;

            					
            			evalDescr.setContent( engine );			
            		
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_eval_ce657); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:385:1: normal_pattern[ConditionalElementDescr in_ce] : LEFT_PAREN name= SYMBOL ( field_constriant[column] )* RIGHT_PAREN ;
    public void normal_pattern(ConditionalElementDescr in_ce) throws RecognitionException {   
        Token name=null;

        
                ColumnDescr column = null;
            
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:389:4: ( LEFT_PAREN name= SYMBOL ( field_constriant[column] )* RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:389:4: LEFT_PAREN name= SYMBOL ( field_constriant[column] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_normal_pattern685); 
            name=(Token)input.LT(1);
            match(input,SYMBOL,FOLLOW_SYMBOL_in_normal_pattern692); 
            
            			column = new ColumnDescr(name.getText());
            			in_ce.addDescr( column );
            		
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:394:3: ( field_constriant[column] )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);
                if ( (LA11_0==LEFT_PAREN) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:394:3: field_constriant[column]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_normal_pattern698);
            	    field_constriant(column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_normal_pattern708); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:400:1: bound_pattern[ConditionalElementDescr in_ce] : var= VAR '<-' LEFT_PAREN name= SYMBOL ( field_constriant[column] )* RIGHT_PAREN ;
    public void bound_pattern(ConditionalElementDescr in_ce) throws RecognitionException {   
        Token var=null;
        Token name=null;

        
                ColumnDescr column = null;
                String identifier = null;
            
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:405:4: (var= VAR '<-' LEFT_PAREN name= SYMBOL ( field_constriant[column] )* RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:405:4: var= VAR '<-' LEFT_PAREN name= SYMBOL ( field_constriant[column] )* RIGHT_PAREN
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_bound_pattern736); 
            
            			identifier = var.getText();
            		
            match(input,40,FOLLOW_40_in_bound_pattern742); 
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_bound_pattern747); 
            name=(Token)input.LT(1);
            match(input,SYMBOL,FOLLOW_SYMBOL_in_bound_pattern754); 
            
            			column = new ColumnDescr(name.getText());
            			column.setIdentifier( identifier );
            			in_ce.addDescr( column );	    
            		
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:415:3: ( field_constriant[column] )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);
                if ( (LA12_0==LEFT_PAREN) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:415:3: field_constriant[column]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_bound_pattern760);
            	    field_constriant(column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_bound_pattern767); 

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:419:1: field_constriant[ColumnDescr column] : LEFT_PAREN f= SYMBOL restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN ;
    public void field_constriant(ColumnDescr column) throws RecognitionException {   
        Token f=null;

        
             	List list = new ArrayList();
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		String op = "==";
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:426:4: ( LEFT_PAREN f= SYMBOL restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:426:4: LEFT_PAREN f= SYMBOL restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_field_constriant795); 
            f=(Token)input.LT(1);
            match(input,SYMBOL,FOLLOW_SYMBOL_in_field_constriant802); 
            
            			fc = new FieldConstraintDescr(f.getText());
            			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
            			column.addDescr( fc );			
            		
            pushFollow(FOLLOW_restriction_in_field_constriant814);
            restriction(fc,  column);
            _fsp--;

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:435:3: ( connective[fc] restriction[fc, column] )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);
                if ( ((LA13_0>=AMPERSAND && LA13_0<=PIPE)) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:436:5: connective[fc] restriction[fc, column]
            	    {
            	    pushFollow(FOLLOW_connective_in_field_constriant826);
            	    connective(fc);
            	    _fsp--;

            	    pushFollow(FOLLOW_restriction_in_field_constriant843);
            	    restriction(fc,  column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_field_constriant866); 

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


    // $ANTLR start connective
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:442:1: connective[FieldConstraintDescr fc] : ( AMPERSAND | PIPE ) ;
    public void connective(FieldConstraintDescr fc) throws RecognitionException {   
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:443:4: ( ( AMPERSAND | PIPE ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:443:4: ( AMPERSAND | PIPE )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:443:4: ( AMPERSAND | PIPE )
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0==AMPERSAND) ) {
                alt14=1;
            }
            else if ( (LA14_0==PIPE) ) {
                alt14=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("443:4: ( AMPERSAND | PIPE )", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:443:6: AMPERSAND
                    {
                    match(input,AMPERSAND,FOLLOW_AMPERSAND_in_connective884); 
                     fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND)); 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:444:9: PIPE
                    {
                    match(input,PIPE,FOLLOW_PIPE_in_connective896); 
                    fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR)); 

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
    // $ANTLR end connective


    // $ANTLR start restriction
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:448:1: restriction[FieldConstraintDescr fc, ColumnDescr column] : ( TILDE )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) ;
    public void restriction(FieldConstraintDescr fc, ColumnDescr column) throws RecognitionException {   
        String lc = null;


        
        			String op = "==";
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:452:4: ( ( TILDE )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:452:4: ( TILDE )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:452:4: ( TILDE )?
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( (LA15_0==TILDE) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:452:5: TILDE
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_restriction931); 
                    op = "!=";

                    }
                    break;

            }

            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:453:3: ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            int alt16=4;
            switch ( input.LA(1) ) {
            case 41:
                alt16=1;
                break;
            case 42:
                alt16=2;
                break;
            case VAR:
                alt16=3;
                break;
            case SYMBOL:
            case STRING:
            case INT:
            case FLOAT:
            case BOOL:
            case NULL:
                alt16=4;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("453:3: ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:453:6: predicate_constraint[op, column]
                    {
                    pushFollow(FOLLOW_predicate_constraint_in_restriction948);
                    predicate_constraint(op,  column);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:454:11: return_value_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_return_value_restriction_in_restriction968);
                    return_value_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:455:10: variable_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_variable_restriction_in_restriction980);
                    variable_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:456:12: lc= literal_restriction
                    {
                    pushFollow(FOLLOW_literal_restriction_in_restriction996);
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:463:1: predicate_constraint[String op, ColumnDescr column] : ':' function[context] ;
    public void predicate_constraint(String op, ColumnDescr column) throws RecognitionException {   
        
           		ExecutionEngine engine = new CLPPredicate();
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );    
            
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:468:4: ( ':' function[context] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:468:4: ':' function[context]
            {
            match(input,41,FOLLOW_41_in_predicate_constraint1038); 
            pushFollow(FOLLOW_function_in_predicate_constraint1042);
            function(context);
            _fsp--;

            	
            			column.addDescr( new PredicateDescr( engine ) );
            		

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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:475:1: return_value_restriction[String op, FieldConstraintDescr fc] : '=' function[context] ;
    public void return_value_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {   
        
        		ExecutionEngine engine = new CLPReturnValue();
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:480:4: ( '=' function[context] )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:480:4: '=' function[context]
            {
            match(input,42,FOLLOW_42_in_return_value_restriction1065); 
            pushFollow(FOLLOW_function_in_return_value_restriction1070);
            function(context);
            _fsp--;

            					
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:486:1: variable_restriction[String op, FieldConstraintDescr fc] : var= VAR ;
    public void variable_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {   
        Token var=null;

        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:487:4: (var= VAR )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:487:4: var= VAR
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_variable_restriction1091); 
            
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
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:493:1: literal_restriction returns [String text] : t= literal ;
    public String literal_restriction() throws RecognitionException {   
        String text = null;

        String t = null;


        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:498:6: (t= literal )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:498:6: t= literal
            {
            pushFollow(FOLLOW_literal_in_literal_restriction1124);
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


    // $ANTLR start function
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:503:1: function[ExecutionBuildContext context] returns [Function f] : LEFT_PAREN name= function_name ( function_params[context, f] )+ RIGHT_PAREN ;
    public Function function(ExecutionBuildContext context) throws RecognitionException {   
        Function f = null;

        Token name = null;


        
        	    FunctionFactory factory = FunctionFactory.getInstance();
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:507:4: ( LEFT_PAREN name= function_name ( function_params[context, f] )+ RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:507:4: LEFT_PAREN name= function_name ( function_params[context, f] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function1149); 
            pushFollow(FOLLOW_function_name_in_function1155);
            name=function_name();
            _fsp--;

            
            			if ( name.getText().equals("bind") ) {
            		  		context.createLocalVariable( name.getText() );
            			}
            		  	f = factory.createFunction( name.getText() );		  
            		
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:515:3: ( function_params[context, f] )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                if ( (LA17_0==LEFT_PAREN||(LA17_0>=SYMBOL && LA17_0<=STRING)||LA17_0==INT||LA17_0==VAR||(LA17_0>=FLOAT && LA17_0<=NULL)) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:515:3: function_params[context, f]
            	    {
            	    pushFollow(FOLLOW_function_params_in_function1168);
            	    function_params(context,  f);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt17 >= 1 ) break loop17;
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function1178); 
             context.addFunction( f ); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return f;
    }
    // $ANTLR end function


    // $ANTLR start modify_function
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:521:1: modify_function[ExecutionBuildContext context] returns [Function f] : LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN ;
    public Function modify_function(ExecutionBuildContext context) throws RecognitionException {   
        Function f = null;

        
        	    FunctionFactory factory = FunctionFactory.getInstance();
        		f = factory.createFunction( "modify" );
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:527:3: ( LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:527:3: LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_modify_function1209); 
            match(input,43,FOLLOW_43_in_modify_function1214); 
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:529:4: ( slot_name_value_pair[context, f] )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);
                if ( (LA18_0==LEFT_PAREN) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:529:4: slot_name_value_pair[context, f]
            	    {
            	    pushFollow(FOLLOW_slot_name_value_pair_in_modify_function1219);
            	    slot_name_value_pair(context,  f);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt18 >= 1 ) break loop18;
                        EarlyExitException eee =
                            new EarlyExitException(18, input);
                        throw eee;
                }
                cnt18++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_modify_function1225); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return f;
    }
    // $ANTLR end modify_function


    // $ANTLR start function_params
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:533:1: function_params[ExecutionBuildContext context, Function f] : (t= VAR | t= STRING | t= SYMBOL | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) ;
    public void function_params(ExecutionBuildContext context, Function f) throws RecognitionException {   
        Token t=null;
        Function nf = null;


        
        		ValueHandler value  =  null;		
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:538:3: ( (t= VAR | t= STRING | t= SYMBOL | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:538:3: (t= VAR | t= STRING | t= SYMBOL | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:538:3: (t= VAR | t= STRING | t= SYMBOL | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            int alt19=8;
            switch ( input.LA(1) ) {
            case VAR:
                alt19=1;
                break;
            case STRING:
                alt19=2;
                break;
            case SYMBOL:
                alt19=3;
                break;
            case FLOAT:
                alt19=4;
                break;
            case INT:
                alt19=5;
                break;
            case BOOL:
                alt19=6;
                break;
            case NULL:
                alt19=7;
                break;
            case LEFT_PAREN:
                alt19=8;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("538:3: (t= VAR | t= STRING | t= SYMBOL | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:538:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_function_params1254); 
                     value = context.getVariableValueHandler(t.getText() ); 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:539:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_function_params1266); 
                     value = new ObjectLiteralValue( getString( t ) ); 

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:540:7: t= SYMBOL
                    {
                    t=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_function_params1281); 
                     value = new ObjectLiteralValue( t.getText() ); 

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:541:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_function_params1298); 
                     value = new DoubleLiteralValue( t.getText() ); 

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:542:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_function_params1313); 
                     value = new LongLiteralValue( t.getText() ); 

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:543:6: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_function_params1333); 
                     value = new BooleanLiteralValue( t.getText() ); 

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:544:6: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_function_params1355); 
                     value = ObjectLiteralValue.NULL; 

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:545:6: nf= function[context]
                    {
                    pushFollow(FOLLOW_function_in_function_params1371);
                    nf=function(context);
                    _fsp--;

                     value = nf; 

                    }
                    break;

            }

             f.addParameter( value ); 

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
    // $ANTLR end function_params


    // $ANTLR start slot_name_value_pair
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:551:1: slot_name_value_pair[ExecutionBuildContext context, Function f] : LEFT_PAREN id= SYMBOL (t= VAR | t= STRING | t= SYMBOL | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN ;
    public void slot_name_value_pair(ExecutionBuildContext context, Function f) throws RecognitionException {   
        Token id=null;
        Token t=null;
        Function nf = null;


        
        		SlotNameValuePair nameValuePair = null;
        		String name = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:557:3: ( LEFT_PAREN id= SYMBOL (t= VAR | t= STRING | t= SYMBOL | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:557:3: LEFT_PAREN id= SYMBOL (t= VAR | t= STRING | t= SYMBOL | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_slot_name_value_pair1413); 
            id=(Token)input.LT(1);
            match(input,SYMBOL,FOLLOW_SYMBOL_in_slot_name_value_pair1419); 
            
            			name = id.getText();
            		
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:561:3: (t= VAR | t= STRING | t= SYMBOL | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            int alt20=8;
            switch ( input.LA(1) ) {
            case VAR:
                alt20=1;
                break;
            case STRING:
                alt20=2;
                break;
            case SYMBOL:
                alt20=3;
                break;
            case FLOAT:
                alt20=4;
                break;
            case INT:
                alt20=5;
                break;
            case BOOL:
                alt20=6;
                break;
            case NULL:
                alt20=7;
                break;
            case LEFT_PAREN:
                alt20=8;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("561:3: (t= VAR | t= STRING | t= SYMBOL | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:561:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_slot_name_value_pair1430); 
                     nameValuePair = new SlotNameValuePair(name, context.getVariableValueHandler( t.getText() ) ); 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:562:7: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_slot_name_value_pair1448); 
                     nameValuePair = new SlotNameValuePair(name, new ObjectLiteralValue( getString( t ) ) ); 

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:563:7: t= SYMBOL
                    {
                    t=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_slot_name_value_pair1463); 
                     nameValuePair = new SlotNameValuePair(name, new ObjectLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:564:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_slot_name_value_pair1480); 
                     nameValuePair = new SlotNameValuePair(name, new DoubleLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:565:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_slot_name_value_pair1495); 
                     nameValuePair = new SlotNameValuePair(name, new LongLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:566:6: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_slot_name_value_pair1515); 
                     nameValuePair = new SlotNameValuePair(name, new BooleanLiteralValue( t.getText() ) ) ; 

                    }
                    break;
                case 7 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:567:6: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_slot_name_value_pair1537); 
                     nameValuePair = new SlotNameValuePair(name, ObjectLiteralValue.NULL ); 

                    }
                    break;
                case 8 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:568:6: nf= function[context]
                    {
                    pushFollow(FOLLOW_function_in_slot_name_value_pair1553);
                    nf=function(context);
                    _fsp--;

                     nameValuePair = new SlotNameValuePair(name, nf ); 

                    }
                    break;

            }

             f.addParameter( nameValuePair ); 
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_slot_name_value_pair1590); 

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
    // $ANTLR end slot_name_value_pair


    // $ANTLR start literal
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:1: literal returns [String text] : (t= STRING | t= SYMBOL | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public String literal() throws RecognitionException {   
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:4: ( (t= STRING | t= SYMBOL | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:4: (t= STRING | t= SYMBOL | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:4: (t= STRING | t= SYMBOL | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt21=6;
            switch ( input.LA(1) ) {
            case STRING:
                alt21=1;
                break;
            case SYMBOL:
                alt21=2;
                break;
            case INT:
                alt21=3;
                break;
            case FLOAT:
                alt21=4;
                break;
            case BOOL:
                alt21=5;
                break;
            case NULL:
                alt21=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("579:4: (t= STRING | t= SYMBOL | t= INT | t= FLOAT | t= BOOL | t= NULL )", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:8: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal1619); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:580:7: t= SYMBOL
                    {
                    t=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_literal1632); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:581:7: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal1648); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:582:7: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal1663); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:583:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal1676); 
                     text = t.getText(); 

                    }
                    break;
                case 6 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:584:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal1690); 
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


    // $ANTLR start function_name
    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:588:1: function_name returns [Token tok] : (t= SYMBOL | t= MISC ) ;
    public Token function_name() throws RecognitionException {   
        Token tok = null;

        Token t=null;

        try {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:2: ( (t= SYMBOL | t= MISC ) )
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:2: (t= SYMBOL | t= MISC )
            {
            // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:2: (t= SYMBOL | t= MISC )
            int alt22=2;
            int LA22_0 = input.LA(1);
            if ( (LA22_0==SYMBOL) ) {
                alt22=1;
            }
            else if ( (LA22_0==MISC) ) {
                alt22=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("590:2: (t= SYMBOL | t= MISC )", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:4: t= SYMBOL
                    {
                    t=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_function_name1719); 

                    }
                    break;
                case 2 :
                    // D:\\workspace\\jboss\\jbossrules\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:591:4: t= MISC
                    {
                    t=(Token)input.LT(1);
                    match(input,MISC,FOLLOW_MISC_in_function_name1727); 

                    }
                    break;

            }

            
            	    tok = t;
            	

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
       }
        return tok;
    }
    // $ANTLR end function_name


 

    public static final BitSet FOLLOW_38_in_opt_semicolon38 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_rule65 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DEFRULE_in_rule73 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_SYMBOL_in_rule77 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_rule89 = new BitSet(new long[]{0x0000008000010010L});
    public static final BitSet FOLLOW_ruleAttribute_in_rule95 = new BitSet(new long[]{0x0000008000010010L});
    public static final BitSet FOLLOW_lhs_in_rule103 = new BitSet(new long[]{0x0000008000010010L});
    public static final BitSet FOLLOW_39_in_rule112 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_rhs_in_rule119 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_rule127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute144 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_DECLARE_in_ruleAttribute146 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute151 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_salience_in_ruleAttribute155 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute159 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience193 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_INT_in_salience197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_lhs224 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_lhs234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_lhs243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_lhs252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_lhs266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_lhs280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bound_pattern_in_lhs289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_rhs317 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_and_ce_in_ce341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_ce354 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_ce363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_ce372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_ce386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_ce410 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_and_ce442 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_AND_in_and_ce447 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_and_ce453 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_and_ce462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_or_ce490 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_OR_in_or_ce495 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_or_ce501 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_or_ce510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_not_ce538 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_NOT_in_not_ce543 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_not_ce549 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_not_ce558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_exists_ce587 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EXISTS_in_exists_ce592 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_exists_ce598 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_exists_ce607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_eval_ce635 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_TEST_in_eval_ce640 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_eval_ce646 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_eval_ce657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_normal_pattern685 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_SYMBOL_in_normal_pattern692 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_field_constriant_in_normal_pattern698 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_normal_pattern708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_bound_pattern736 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_bound_pattern742 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_bound_pattern747 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_SYMBOL_in_bound_pattern754 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_field_constriant_in_bound_pattern760 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_bound_pattern767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_field_constriant795 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_SYMBOL_in_field_constriant802 = new BitSet(new long[]{0x00000600007904C0L});
    public static final BitSet FOLLOW_restriction_in_field_constriant814 = new BitSet(new long[]{0x0000000000060100L});
    public static final BitSet FOLLOW_connective_in_field_constriant826 = new BitSet(new long[]{0x00000600007904C0L});
    public static final BitSet FOLLOW_restriction_in_field_constriant843 = new BitSet(new long[]{0x0000000000060100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_field_constriant866 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AMPERSAND_in_connective884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PIPE_in_connective896 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_restriction931 = new BitSet(new long[]{0x00000600007104C0L});
    public static final BitSet FOLLOW_predicate_constraint_in_restriction948 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_return_value_restriction_in_restriction968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_restriction_in_restriction980 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_restriction_in_restriction996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_predicate_constraint1038 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_predicate_constraint1042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_42_in_return_value_restriction1065 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_return_value_restriction1070 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_variable_restriction1091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_literal_restriction1124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function1149 = new BitSet(new long[]{0x0000000000800040L});
    public static final BitSet FOLLOW_function_name_in_function1155 = new BitSet(new long[]{0x00000000007104D0L});
    public static final BitSet FOLLOW_function_params_in_function1168 = new BitSet(new long[]{0x00000000007105D0L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function1178 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_modify_function1209 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_modify_function1214 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_slot_name_value_pair_in_modify_function1219 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_modify_function1225 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_function_params1254 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_function_params1266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_function_params1281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_function_params1298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_function_params1313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_function_params1333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_function_params1355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_function_params1371 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_slot_name_value_pair1413 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_SYMBOL_in_slot_name_value_pair1419 = new BitSet(new long[]{0x00000000007104D0L});
    public static final BitSet FOLLOW_VAR_in_slot_name_value_pair1430 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_slot_name_value_pair1448 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_SYMBOL_in_slot_name_value_pair1463 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_FLOAT_in_slot_name_value_pair1480 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_INT_in_slot_name_value_pair1495 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_slot_name_value_pair1515 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_NULL_in_slot_name_value_pair1537 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_function_in_slot_name_value_pair1553 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_slot_name_value_pair1590 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal1619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_literal1632 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal1648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal1663 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal1676 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal1690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_function_name1719 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MISC_in_function_name1727 = new BitSet(new long[]{0x0000000000000002L});

}