// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-12 19:19:14

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LEFT_PAREN", "ID", "RIGHT_PAREN", "AND", "OR", "NOT", "EXISTS", "TEST", "VAR", "STRING", "FLOAT", "INT", "BOOL", "NULL", "DEFRULE", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "MULTI_LINE_COMMENT", "MISC", "';'", "'<-'", "'&'", "'|'", "'~'", "':'", "'='", "'modify'"
    };
    public static final int EXISTS=10;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=30;
    public static final int BOOL=16;
    public static final int DEFRULE=18;
    public static final int HexDigit=22;
    public static final int WS=20;
    public static final int MISC=32;
    public static final int STRING=13;
    public static final int FLOAT=14;
    public static final int OR=8;
    public static final int VAR=12;
    public static final int UnicodeEscape=23;
    public static final int AND=7;
    public static final int EscapeSequence=21;
    public static final int INT=15;
    public static final int EOF=-1;
    public static final int NULL=17;
    public static final int EOL=19;
    public static final int LEFT_SQUARE=27;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=25;
    public static final int OctalEscape=24;
    public static final int MULTI_LINE_COMMENT=31;
    public static final int TEST=11;
    public static final int RIGHT_PAREN=6;
    public static final int NOT=9;
    public static final int LEFT_CURLY=29;
    public static final int RIGHT_SQUARE=28;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=26;
    public static final int ID=5;

        public CLPParser(TokenStream input) {
            super(input);
        }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }

    
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:167:1: opt_semicolon : ( ';' )? ;
    public void opt_semicolon() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:168:4: ( ( ';' )? )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:168:4: ( ';' )?
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:168:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);
            if ( (LA1_0==33) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:168:4: ';'
                    {
                    match(input,33,FOLLOW_33_in_opt_semicolon38); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:210:1: rule returns [RuleDescr rule] : loc= LEFT_PAREN 'defrule' ruleName= ID ( lhs[lhs] )* ( function[context] )* RIGHT_PAREN ;
    public RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        Token ruleName=null;

         
        	        rule = null; 
        	        AndDescr lhs = null;
        	        ColumnDescr colum = null;
        	        ExecutionEngine engine = new BlockExecutionEngine();
        			ExecutionBuildContext context = new ExecutionBuildContext( engine );  	        
        	      
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:218:4: (loc= LEFT_PAREN 'defrule' ruleName= ID ( lhs[lhs] )* ( function[context] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:218:4: loc= LEFT_PAREN 'defrule' ruleName= ID ( lhs[lhs] )* ( function[context] )* RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_rule64); 
            match(input,DEFRULE,FOLLOW_DEFRULE_in_rule66); 
            ruleName=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_rule74); 
             
            	  		debug( "start rule: " + ruleName.getText() );
            	        rule = new RuleDescr( ruleName.getText(), null ); 
            			rule.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            			rule.setStartCharacter( ((CommonToken)loc).getStartIndex() ); 
            		
            			// not sure how you define where a LHS starts in clips, so just putting it here for now
              	        lhs = new AndDescr(); 
              	        rule.setLhs( lhs ); 
               	        lhs.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            			lhs.setStartCharacter( ((CommonToken)loc).getStartIndex() );				
            	  
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:232:4: ( lhs[lhs] )*
            loop2:
            do {
                int alt2=2;
                alt2 = dfa2.predict(input);
                switch (alt2) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:232:4: lhs[lhs]
            	    {
            	    pushFollow(FOLLOW_lhs_in_rule85);
            	    lhs(lhs);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:233:4: ( function[context] )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);
                if ( (LA3_0==LEFT_PAREN) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:233:4: function[context]
            	    {
            	    pushFollow(FOLLOW_function_in_rule92);
            	    function(context);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

             rule.setConsequence( engine ); 
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_rule101); 

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


    // $ANTLR start lhs
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:239:1: lhs[ConditionalElementDescr in_ce] : ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) ;
    public void lhs(ConditionalElementDescr in_ce) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:240:4: ( ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:240:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:240:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            int alt4=7;
            int LA4_0 = input.LA(1);
            if ( (LA4_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case AND:
                    alt4=1;
                    break;
                case EXISTS:
                    alt4=4;
                    break;
                case ID:
                    alt4=6;
                    break;
                case OR:
                    alt4=2;
                    break;
                case NOT:
                    alt4=3;
                    break;
                case TEST:
                    alt4=5;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("240:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 4, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA4_0==VAR) ) {
                alt4=7;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("240:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:240:8: and_ce[in_ce]
                    {
                    pushFollow(FOLLOW_and_ce_in_lhs120);
                    and_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:241:7: or_ce[in_ce]
                    {
                    pushFollow(FOLLOW_or_ce_in_lhs130);
                    or_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:242:7: not_ce[in_ce]
                    {
                    pushFollow(FOLLOW_not_ce_in_lhs139);
                    not_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:243:7: exists_ce[in_ce]
                    {
                    pushFollow(FOLLOW_exists_ce_in_lhs148);
                    exists_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:244:8: eval_ce[in_ce]
                    {
                    pushFollow(FOLLOW_eval_ce_in_lhs162);
                    eval_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:245:7: normal_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_lhs176);
                    normal_pattern(in_ce);
                    _fsp--;


                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:246:7: bound_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_bound_pattern_in_lhs185);
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


    // $ANTLR start ce
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:250:1: ce[ConditionalElementDescr in_ce] : ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] ) ;
    public void ce(ConditionalElementDescr in_ce) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:251:4: ( ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:251:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:251:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )
            int alt5=6;
            int LA5_0 = input.LA(1);
            if ( (LA5_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case NOT:
                    alt5=3;
                    break;
                case EXISTS:
                    alt5=4;
                    break;
                case AND:
                    alt5=1;
                    break;
                case OR:
                    alt5=2;
                    break;
                case ID:
                    alt5=6;
                    break;
                case TEST:
                    alt5=5;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("251:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )", 5, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("251:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:251:8: and_ce[in_ce]
                    {
                    pushFollow(FOLLOW_and_ce_in_ce207);
                    and_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:252:10: or_ce[in_ce]
                    {
                    pushFollow(FOLLOW_or_ce_in_ce220);
                    or_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:253:7: not_ce[in_ce]
                    {
                    pushFollow(FOLLOW_not_ce_in_ce229);
                    not_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:254:7: exists_ce[in_ce]
                    {
                    pushFollow(FOLLOW_exists_ce_in_ce238);
                    exists_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:255:8: eval_ce[in_ce]
                    {
                    pushFollow(FOLLOW_eval_ce_in_ce252);
                    eval_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:256:10: normal_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_ce276);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:261:1: and_ce[ConditionalElementDescr in_ce] : LEFT_PAREN AND ( ce[andDescr] )* RIGHT_PAREN ;
    public void and_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                AndDescr andDescr= null;        
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:265:4: ( LEFT_PAREN AND ( ce[andDescr] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:265:4: LEFT_PAREN AND ( ce[andDescr] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_and_ce308); 
            match(input,AND,FOLLOW_AND_in_and_ce313); 
            
            	    	andDescr = new AndDescr();
            			in_ce.addDescr( andDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:270:3: ( ce[andDescr] )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( (LA6_0==LEFT_PAREN) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:270:3: ce[andDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_and_ce319);
            	    ce(andDescr);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_and_ce328); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:274:1: or_ce[ConditionalElementDescr in_ce] : LEFT_PAREN OR ( ce[orDescr] )* RIGHT_PAREN ;
    public void or_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                OrDescr orDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:278:4: ( LEFT_PAREN OR ( ce[orDescr] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:278:4: LEFT_PAREN OR ( ce[orDescr] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_or_ce356); 
            match(input,OR,FOLLOW_OR_in_or_ce361); 
            
            	    	orDescr = new OrDescr();
            			in_ce.addDescr( orDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:283:3: ( ce[orDescr] )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);
                if ( (LA7_0==LEFT_PAREN) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:283:3: ce[orDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_or_ce367);
            	    ce(orDescr);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_or_ce376); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:287:1: not_ce[ConditionalElementDescr in_ce] : LEFT_PAREN NOT ( ce[notDescr] )? RIGHT_PAREN ;
    public void not_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                NotDescr notDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:291:4: ( LEFT_PAREN NOT ( ce[notDescr] )? RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:291:4: LEFT_PAREN NOT ( ce[notDescr] )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_not_ce404); 
            match(input,NOT,FOLLOW_NOT_in_not_ce409); 
            
            			notDescr = new NotDescr();
            		    in_ce.addDescr( notDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:296:3: ( ce[notDescr] )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            if ( (LA8_0==LEFT_PAREN) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:296:3: ce[notDescr]
                    {
                    pushFollow(FOLLOW_ce_in_not_ce415);
                    ce(notDescr);
                    _fsp--;


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_not_ce424); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:300:1: exists_ce[ConditionalElementDescr in_ce] : LEFT_PAREN EXISTS ( ce[existsDescr] )? RIGHT_PAREN ;
    public void exists_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                ExistsDescr existsDescr= null;        
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:304:4: ( LEFT_PAREN EXISTS ( ce[existsDescr] )? RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:304:4: LEFT_PAREN EXISTS ( ce[existsDescr] )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_exists_ce453); 
            match(input,EXISTS,FOLLOW_EXISTS_in_exists_ce458); 
            
            		    existsDescr = new ExistsDescr();
            		    in_ce.addDescr( existsDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:309:3: ( ce[existsDescr] )?
            int alt9=2;
            int LA9_0 = input.LA(1);
            if ( (LA9_0==LEFT_PAREN) ) {
                alt9=1;
            }
            switch (alt9) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:309:3: ce[existsDescr]
                    {
                    pushFollow(FOLLOW_ce_in_exists_ce464);
                    ce(existsDescr);
                    _fsp--;


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_exists_ce473); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:313:1: eval_ce[ConditionalElementDescr in_ce] : LEFT_PAREN TEST function[context] RIGHT_PAREN ;
    public void eval_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                EvalDescr evalDescr= null;    
           		ExecutionEngine engine = new CLPEval();     
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );   		         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:319:4: ( LEFT_PAREN TEST function[context] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:319:4: LEFT_PAREN TEST function[context] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_eval_ce501); 
            match(input,TEST,FOLLOW_TEST_in_eval_ce506); 
            
            		    evalDescr = new EvalDescr();
            		    in_ce.addDescr( evalDescr );
            		
            pushFollow(FOLLOW_function_in_eval_ce512);
            function(context);
            _fsp--;

            					
            			evalDescr.setContent( engine );			
            		
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_eval_ce523); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:330:1: normal_pattern[ConditionalElementDescr in_ce] : LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN ;
    public void normal_pattern(ConditionalElementDescr in_ce) throws RecognitionException {
        Token name=null;

        
                ColumnDescr column = null;
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:334:4: ( LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:334:4: LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_normal_pattern551); 
            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_normal_pattern558); 
            
            			column = new ColumnDescr(name.getText());
            			in_ce.addDescr( column );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:339:3: ( field_constriant[column] )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);
                if ( (LA10_0==LEFT_PAREN) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:339:3: field_constriant[column]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_normal_pattern564);
            	    field_constriant(column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_normal_pattern574); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:345:1: bound_pattern[ConditionalElementDescr in_ce] : var= VAR '<-' LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN ;
    public void bound_pattern(ConditionalElementDescr in_ce) throws RecognitionException {
        Token var=null;
        Token name=null;

        
                ColumnDescr column = null;
                String identifier = null;
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:350:4: (var= VAR '<-' LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:350:4: var= VAR '<-' LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_bound_pattern602); 
            
            			identifier = var.getText();
            		
            match(input,34,FOLLOW_34_in_bound_pattern608); 
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_bound_pattern613); 
            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_bound_pattern620); 
            
            			column = new ColumnDescr(name.getText());
            			column.setIdentifier( identifier );
            			in_ce.addDescr( column );	    
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:360:3: ( field_constriant[column] )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);
                if ( (LA11_0==LEFT_PAREN) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:360:3: field_constriant[column]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_bound_pattern626);
            	    field_constriant(column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_bound_pattern633); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:364:1: field_constriant[ColumnDescr column] : LEFT_PAREN f= ID restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN ;
    public void field_constriant(ColumnDescr column) throws RecognitionException {
        Token f=null;

        
             	List list = new ArrayList();
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		String op = "==";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:371:4: ( LEFT_PAREN f= ID restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:371:4: LEFT_PAREN f= ID restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_field_constriant661); 
            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_field_constriant668); 
            
            			fc = new FieldConstraintDescr(f.getText());
            			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
            			column.addDescr( fc );			
            		
            pushFollow(FOLLOW_restriction_in_field_constriant680);
            restriction(fc,  column);
            _fsp--;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:380:3: ( connective[fc] restriction[fc, column] )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);
                if ( ((LA12_0>=35 && LA12_0<=36)) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:381:5: connective[fc] restriction[fc, column]
            	    {
            	    pushFollow(FOLLOW_connective_in_field_constriant692);
            	    connective(fc);
            	    _fsp--;

            	    pushFollow(FOLLOW_restriction_in_field_constriant709);
            	    restriction(fc,  column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_field_constriant732); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:387:1: connective[FieldConstraintDescr fc] : ( '&' | '|' ) ;
    public void connective(FieldConstraintDescr fc) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:388:4: ( ( '&' | '|' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:388:4: ( '&' | '|' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:388:4: ( '&' | '|' )
            int alt13=2;
            int LA13_0 = input.LA(1);
            if ( (LA13_0==35) ) {
                alt13=1;
            }
            else if ( (LA13_0==36) ) {
                alt13=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("388:4: ( '&' | '|' )", 13, 0, input);

                throw nvae;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:388:6: '&'
                    {
                    match(input,35,FOLLOW_35_in_connective750); 
                     fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND)); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:389:9: '|'
                    {
                    match(input,36,FOLLOW_36_in_connective762); 
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:393:1: restriction[FieldConstraintDescr fc, ColumnDescr column] : ( '~' )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) ;
    public void restriction(FieldConstraintDescr fc, ColumnDescr column) throws RecognitionException {
        String lc = null;


        
        			String op = "==";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:397:4: ( ( '~' )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:397:4: ( '~' )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:397:4: ( '~' )?
            int alt14=2;
            int LA14_0 = input.LA(1);
            if ( (LA14_0==37) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:397:5: '~'
                    {
                    match(input,37,FOLLOW_37_in_restriction797); 
                    op = "!=";

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:398:3: ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            int alt15=4;
            switch ( input.LA(1) ) {
            case 38:
                alt15=1;
                break;
            case 39:
                alt15=2;
                break;
            case VAR:
                alt15=3;
                break;
            case ID:
            case STRING:
            case FLOAT:
            case INT:
            case BOOL:
            case NULL:
                alt15=4;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("398:3: ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:398:6: predicate_constraint[op, column]
                    {
                    pushFollow(FOLLOW_predicate_constraint_in_restriction814);
                    predicate_constraint(op,  column);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:399:11: return_value_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_return_value_restriction_in_restriction834);
                    return_value_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:400:10: variable_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_variable_restriction_in_restriction846);
                    variable_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:401:12: lc= literal_restriction
                    {
                    pushFollow(FOLLOW_literal_restriction_in_restriction862);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:408:1: predicate_constraint[String op, ColumnDescr column] : ':' function[context] ;
    public void predicate_constraint(String op, ColumnDescr column) throws RecognitionException {
        
           		ExecutionEngine engine = new CLPPredicate();
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );    
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:413:4: ( ':' function[context] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:413:4: ':' function[context]
            {
            match(input,38,FOLLOW_38_in_predicate_constraint904); 
            pushFollow(FOLLOW_function_in_predicate_constraint908);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:420:1: return_value_restriction[String op, FieldConstraintDescr fc] : '=' function[context] ;
    public void return_value_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {
        
        		ExecutionEngine engine = new CLPReturnValue();
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:425:4: ( '=' function[context] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:425:4: '=' function[context]
            {
            match(input,39,FOLLOW_39_in_return_value_restriction931); 
            pushFollow(FOLLOW_function_in_return_value_restriction936);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:431:1: variable_restriction[String op, FieldConstraintDescr fc] : var= VAR ;
    public void variable_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {
        Token var=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:432:4: (var= VAR )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:432:4: var= VAR
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_variable_restriction957); 
            
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:438:1: literal_restriction returns [String text] : t= literal ;
    public String literal_restriction() throws RecognitionException {
        String text = null;

        String t = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:443:6: (t= literal )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:443:6: t= literal
            {
            pushFollow(FOLLOW_literal_in_literal_restriction990);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:448:1: function[ExecutionBuildContext context] returns [Function f] : LEFT_PAREN name= ID ( function_params[context, f] )+ RIGHT_PAREN ;
    public Function function(ExecutionBuildContext context) throws RecognitionException {
        Function f = null;

        Token name=null;

        
        	    FunctionFactory factory = FunctionFactory.getInstance();
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:452:4: ( LEFT_PAREN name= ID ( function_params[context, f] )+ RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:452:4: LEFT_PAREN name= ID ( function_params[context, f] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function1015); 
            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_function1021); 
            
            			if ( name.getText().equals("bind") ) {
            		  		context.createLocalVariable( name.getText() );
            			}
            		  	f = factory.createFunction( name.getText() );		  
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:460:3: ( function_params[context, f] )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);
                if ( ((LA16_0>=LEFT_PAREN && LA16_0<=ID)||(LA16_0>=VAR && LA16_0<=NULL)) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:460:3: function_params[context, f]
            	    {
            	    pushFollow(FOLLOW_function_params_in_function1034);
            	    function_params(context,  f);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function1044); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:465:1: modify_function[ExecutionBuildContext context] returns [Function f] : LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN ;
    public Function modify_function(ExecutionBuildContext context) throws RecognitionException {
        Function f = null;

        
        	    FunctionFactory factory = FunctionFactory.getInstance();
        		f = factory.createFunction( "modify" );
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:471:3: ( LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:471:3: LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_modify_function1068); 
            match(input,40,FOLLOW_40_in_modify_function1073); 
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:473:4: ( slot_name_value_pair[context, f] )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);
                if ( (LA17_0==LEFT_PAREN) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:473:4: slot_name_value_pair[context, f]
            	    {
            	    pushFollow(FOLLOW_slot_name_value_pair_in_modify_function1078);
            	    slot_name_value_pair(context,  f);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_modify_function1084); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:477:1: function_params[ExecutionBuildContext context, Function f] : (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) ;
    public void function_params(ExecutionBuildContext context, Function f) throws RecognitionException {
        Token t=null;
        Function nf = null;


        
        		ValueHandler value  =  null;		
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:482:3: ( (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:482:3: (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:482:3: (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            int alt18=8;
            switch ( input.LA(1) ) {
            case VAR:
                alt18=1;
                break;
            case STRING:
                alt18=2;
                break;
            case ID:
                alt18=3;
                break;
            case FLOAT:
                alt18=4;
                break;
            case INT:
                alt18=5;
                break;
            case BOOL:
                alt18=6;
                break;
            case NULL:
                alt18=7;
                break;
            case LEFT_PAREN:
                alt18=8;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("482:3: (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:482:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_function_params1113); 
                     value = context.getVariableValueHandler(t.getText() ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:483:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_function_params1125); 
                     value = new ObjectLiteralValue( getString( t ) ); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:484:7: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_function_params1140); 
                     value = new ObjectLiteralValue( t.getText() ); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:485:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_function_params1161); 
                     value = new DoubleLiteralValue( t.getText() ); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:486:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_function_params1176); 
                     value = new LongLiteralValue( t.getText() ); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:487:6: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_function_params1196); 
                     value = new BooleanLiteralValue( t.getText() ); 

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:488:6: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_function_params1218); 
                     value = ObjectLiteralValue.NULL; 

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:489:6: nf= function[context]
                    {
                    pushFollow(FOLLOW_function_in_function_params1234);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:495:1: slot_name_value_pair[ExecutionBuildContext context, Function f] : LEFT_PAREN id= ID (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN ;
    public void slot_name_value_pair(ExecutionBuildContext context, Function f) throws RecognitionException {
        Token id=null;
        Token t=null;
        Function nf = null;


        
        		SlotNameValuePair nameValuePair = null;
        		String name = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:501:3: ( LEFT_PAREN id= ID (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:501:3: LEFT_PAREN id= ID (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_slot_name_value_pair1276); 
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_slot_name_value_pair1282); 
            
            			name = id.getText();
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:505:3: (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            int alt19=8;
            switch ( input.LA(1) ) {
            case VAR:
                alt19=1;
                break;
            case STRING:
                alt19=2;
                break;
            case ID:
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
                    new NoViableAltException("505:3: (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:505:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_slot_name_value_pair1293); 
                     nameValuePair = new SlotNameValuePair(name, context.getVariableValueHandler( t.getText() ) ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:506:7: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_slot_name_value_pair1311); 
                     nameValuePair = new SlotNameValuePair(name, new ObjectLiteralValue( getString( t ) ) ); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:507:7: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_slot_name_value_pair1326); 
                     nameValuePair = new SlotNameValuePair(name, new ObjectLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:508:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_slot_name_value_pair1347); 
                     nameValuePair = new SlotNameValuePair(name, new DoubleLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:509:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_slot_name_value_pair1362); 
                     nameValuePair = new SlotNameValuePair(name, new LongLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:510:6: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_slot_name_value_pair1382); 
                     nameValuePair = new SlotNameValuePair(name, new BooleanLiteralValue( t.getText() ) ) ; 

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:511:6: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_slot_name_value_pair1404); 
                     nameValuePair = new SlotNameValuePair(name, ObjectLiteralValue.NULL ); 

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:512:6: nf= function[context]
                    {
                    pushFollow(FOLLOW_function_in_slot_name_value_pair1420);
                    nf=function(context);
                    _fsp--;

                     nameValuePair = new SlotNameValuePair(name, nf ); 

                    }
                    break;

            }

             f.addParameter( nameValuePair ); 
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_slot_name_value_pair1457); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:519:1: literal returns [String text] : (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public String literal() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:523:4: ( (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:523:4: (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:523:4: (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt20=6;
            switch ( input.LA(1) ) {
            case STRING:
                alt20=1;
                break;
            case ID:
                alt20=2;
                break;
            case INT:
                alt20=3;
                break;
            case FLOAT:
                alt20=4;
                break;
            case BOOL:
                alt20=5;
                break;
            case NULL:
                alt20=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("523:4: (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL )", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:523:8: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal1486); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:524:7: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_literal1499); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:525:7: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal1515); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:526:7: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal1530); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:527:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal1543); 
                     text = t.getText(); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:528:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal1557); 
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


    protected DFA2 dfa2 = new DFA2(this);
    public static final String DFA2_eotS =
        "\20\uffff";
    public static final String DFA2_eofS =
        "\20\uffff";
    public static final String DFA2_minS =
        "\1\4\1\5\2\uffff\1\4\1\5\11\4\1\uffff";
    public static final String DFA2_maxS =
        "\1\14\1\13\2\uffff\1\21\1\5\1\47\7\44\1\21\1\uffff";
    public static final String DFA2_acceptS =
        "\2\uffff\1\2\1\1\13\uffff\1\1";
    public static final String DFA2_specialS =
        "\20\uffff}>";
    public static final String[] DFA2_transition = {
        "\1\1\1\uffff\1\2\5\uffff\1\3",
        "\1\4\1\uffff\5\3",
        "",
        "",
        "\1\5\1\2\1\3\5\uffff\6\2",
        "\1\6",
        "\1\2\1\11\6\uffff\1\7\1\10\1\12\1\13\1\14\1\15\23\uffff\3\3",
        "\2\2\1\16\5\uffff\6\2\21\uffff\2\3",
        "\2\2\1\16\5\uffff\6\2\21\uffff\2\3",
        "\2\2\1\16\5\uffff\6\2\21\uffff\2\3",
        "\2\2\1\16\5\uffff\6\2\21\uffff\2\3",
        "\2\2\1\16\5\uffff\6\2\21\uffff\2\3",
        "\2\2\1\16\5\uffff\6\2\21\uffff\2\3",
        "\2\2\1\16\5\uffff\6\2\21\uffff\2\3",
        "\1\5\1\2\1\17\5\uffff\6\2",
        ""
    };

    class DFA2 extends DFA {
        public DFA2(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 2;
            this.eot = DFA.unpackEncodedString(DFA2_eotS);
            this.eof = DFA.unpackEncodedString(DFA2_eofS);
            this.min = DFA.unpackEncodedStringToUnsignedChars(DFA2_minS);
            this.max = DFA.unpackEncodedStringToUnsignedChars(DFA2_maxS);
            this.accept = DFA.unpackEncodedString(DFA2_acceptS);
            this.special = DFA.unpackEncodedString(DFA2_specialS);
            int numStates = DFA2_transition.length;
            this.transition = new short[numStates][];
            for (int i=0; i<numStates; i++) {
                transition[i] = DFA.unpackEncodedString(DFA2_transition[i]);
            }
        }
        public String getDescription() {
            return "()* loopback of 232:4: ( lhs[lhs] )*";
        }
    }
 

    public static final BitSet FOLLOW_33_in_opt_semicolon38 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_rule64 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_DEFRULE_in_rule66 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_rule74 = new BitSet(new long[]{0x0000000000001050L});
    public static final BitSet FOLLOW_lhs_in_rule85 = new BitSet(new long[]{0x0000000000001050L});
    public static final BitSet FOLLOW_function_in_rule92 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_rule101 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_lhs120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_lhs130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_lhs139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_lhs148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_lhs162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_lhs176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bound_pattern_in_lhs185 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_ce207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_ce220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_ce229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_ce238 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_ce252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_ce276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_and_ce308 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_AND_in_and_ce313 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_ce_in_and_ce319 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_and_ce328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_or_ce356 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_OR_in_or_ce361 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_ce_in_or_ce367 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_or_ce376 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_not_ce404 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_NOT_in_not_ce409 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_ce_in_not_ce415 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_not_ce424 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_exists_ce453 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_EXISTS_in_exists_ce458 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_ce_in_exists_ce464 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_exists_ce473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_eval_ce501 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_TEST_in_eval_ce506 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_eval_ce512 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_eval_ce523 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_normal_pattern551 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_normal_pattern558 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_normal_pattern564 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_normal_pattern574 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_bound_pattern602 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_bound_pattern608 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_bound_pattern613 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_bound_pattern620 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_bound_pattern626 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_bound_pattern633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_field_constriant661 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_field_constriant668 = new BitSet(new long[]{0x000000E00003F020L});
    public static final BitSet FOLLOW_restriction_in_field_constriant680 = new BitSet(new long[]{0x0000001800000040L});
    public static final BitSet FOLLOW_connective_in_field_constriant692 = new BitSet(new long[]{0x000000E00003F020L});
    public static final BitSet FOLLOW_restriction_in_field_constriant709 = new BitSet(new long[]{0x0000001800000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_field_constriant732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_connective750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_connective762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_restriction797 = new BitSet(new long[]{0x000000C00003F020L});
    public static final BitSet FOLLOW_predicate_constraint_in_restriction814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_return_value_restriction_in_restriction834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_restriction_in_restriction846 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_restriction_in_restriction862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_predicate_constraint904 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_predicate_constraint908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_return_value_restriction931 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_return_value_restriction936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_variable_restriction957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_literal_restriction990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function1015 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function1021 = new BitSet(new long[]{0x000000000003F030L});
    public static final BitSet FOLLOW_function_params_in_function1034 = new BitSet(new long[]{0x000000000003F070L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function1044 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_modify_function1068 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_modify_function1073 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_slot_name_value_pair_in_modify_function1078 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_modify_function1084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_function_params1113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_function_params1125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_params1140 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_function_params1161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_function_params1176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_function_params1196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_function_params1218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_function_params1234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_slot_name_value_pair1276 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_slot_name_value_pair1282 = new BitSet(new long[]{0x000000000003F030L});
    public static final BitSet FOLLOW_VAR_in_slot_name_value_pair1293 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_STRING_in_slot_name_value_pair1311 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ID_in_slot_name_value_pair1326 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_FLOAT_in_slot_name_value_pair1347 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_INT_in_slot_name_value_pair1362 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_BOOL_in_slot_name_value_pair1382 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NULL_in_slot_name_value_pair1404 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_function_in_slot_name_value_pair1420 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_slot_name_value_pair1457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal1486 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_literal1499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal1515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal1530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal1543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal1557 = new BitSet(new long[]{0x0000000000000002L});

}