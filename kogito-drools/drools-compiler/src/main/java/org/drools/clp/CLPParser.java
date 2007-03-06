// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-06 05:09:16

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LEFT_PAREN", "ID", "RIGHT_PAREN", "VAR", "STRING", "INT", "FLOAT", "BOOL", "NULL", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "MULTI_LINE_COMMENT", "MISC", "';'", "'defrule'", "'and'", "'or'", "'not'", "'exists'", "'test'", "'<-'", "'&'", "'|'", "'~'", "':'", "'='"
    };
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=24;
    public static final int BOOL=11;
    public static final int HexDigit=16;
    public static final int WS=14;
    public static final int MISC=26;
    public static final int STRING=8;
    public static final int FLOAT=10;
    public static final int VAR=7;
    public static final int UnicodeEscape=17;
    public static final int EscapeSequence=15;
    public static final int INT=9;
    public static final int EOF=-1;
    public static final int NULL=12;
    public static final int EOL=13;
    public static final int LEFT_SQUARE=21;
    public static final int OctalEscape=18;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=19;
    public static final int MULTI_LINE_COMMENT=25;
    public static final int RIGHT_PAREN=6;
    public static final int LEFT_CURLY=23;
    public static final int RIGHT_SQUARE=22;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=20;
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
            if ( (LA1_0==27) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:168:4: ';'
                    {
                    match(input,27,FOLLOW_27_in_opt_semicolon38); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:210:1: rule returns [RuleDescr rule] : loc= LEFT_PAREN 'defrule' ruleName= ID ( lhs[lhs] )* RIGHT_PAREN ;
    public RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        Token ruleName=null;

         
        	        rule = null; 
        	        AndDescr lhs = null;
        	        ColumnDescr colum = null;
        	      
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:216:4: (loc= LEFT_PAREN 'defrule' ruleName= ID ( lhs[lhs] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:216:4: loc= LEFT_PAREN 'defrule' ruleName= ID ( lhs[lhs] )* RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_rule64); 
            match(input,28,FOLLOW_28_in_rule66); 
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
            	  
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:230:4: ( lhs[lhs] )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( (LA2_0==LEFT_PAREN||LA2_0==VAR) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:230:4: lhs[lhs]
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_rule92); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:235:1: lhs[ConditionalElementDescr in_ce] : ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) ;
    public void lhs(ConditionalElementDescr in_ce) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:236:4: ( ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:236:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:236:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            int alt3=7;
            int LA3_0 = input.LA(1);
            if ( (LA3_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case 30:
                    alt3=2;
                    break;
                case 32:
                    alt3=4;
                    break;
                case ID:
                    alt3=6;
                    break;
                case 29:
                    alt3=1;
                    break;
                case 31:
                    alt3=3;
                    break;
                case 33:
                    alt3=5;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("236:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 3, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA3_0==VAR) ) {
                alt3=7;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("236:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 3, 0, input);

                throw nvae;
            }
            switch (alt3) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:236:8: and_ce[in_ce]
                    {
                    pushFollow(FOLLOW_and_ce_in_lhs109);
                    and_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:237:7: or_ce[in_ce]
                    {
                    pushFollow(FOLLOW_or_ce_in_lhs119);
                    or_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:238:7: not_ce[in_ce]
                    {
                    pushFollow(FOLLOW_not_ce_in_lhs128);
                    not_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:239:7: exists_ce[in_ce]
                    {
                    pushFollow(FOLLOW_exists_ce_in_lhs137);
                    exists_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:240:8: eval_ce[in_ce]
                    {
                    pushFollow(FOLLOW_eval_ce_in_lhs151);
                    eval_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:241:7: normal_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_lhs165);
                    normal_pattern(in_ce);
                    _fsp--;


                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:242:7: bound_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_bound_pattern_in_lhs174);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:246:1: ce[ConditionalElementDescr in_ce] : ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] ) ;
    public void ce(ConditionalElementDescr in_ce) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:247:4: ( ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:247:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:247:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )
            int alt4=6;
            int LA4_0 = input.LA(1);
            if ( (LA4_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case 29:
                    alt4=1;
                    break;
                case 31:
                    alt4=3;
                    break;
                case ID:
                    alt4=6;
                    break;
                case 33:
                    alt4=5;
                    break;
                case 30:
                    alt4=2;
                    break;
                case 32:
                    alt4=4;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("247:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )", 4, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("247:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:247:8: and_ce[in_ce]
                    {
                    pushFollow(FOLLOW_and_ce_in_ce196);
                    and_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:248:10: or_ce[in_ce]
                    {
                    pushFollow(FOLLOW_or_ce_in_ce209);
                    or_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:249:7: not_ce[in_ce]
                    {
                    pushFollow(FOLLOW_not_ce_in_ce218);
                    not_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:250:7: exists_ce[in_ce]
                    {
                    pushFollow(FOLLOW_exists_ce_in_ce227);
                    exists_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:251:8: eval_ce[in_ce]
                    {
                    pushFollow(FOLLOW_eval_ce_in_ce241);
                    eval_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:252:10: normal_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_ce265);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:257:1: and_ce[ConditionalElementDescr in_ce] : LEFT_PAREN 'and' ( ce[andDescr] )* RIGHT_PAREN ;
    public void and_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                AndDescr andDescr= null;        
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:261:4: ( LEFT_PAREN 'and' ( ce[andDescr] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:261:4: LEFT_PAREN 'and' ( ce[andDescr] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_and_ce297); 
            match(input,29,FOLLOW_29_in_and_ce302); 
            
            	    	andDescr = new AndDescr();
            			in_ce.addDescr( andDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:266:3: ( ce[andDescr] )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);
                if ( (LA5_0==LEFT_PAREN) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:266:3: ce[andDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_and_ce308);
            	    ce(andDescr);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_and_ce317); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:270:1: or_ce[ConditionalElementDescr in_ce] : LEFT_PAREN 'or' ( ce[orDescr] )* RIGHT_PAREN ;
    public void or_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                OrDescr orDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:274:4: ( LEFT_PAREN 'or' ( ce[orDescr] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:274:4: LEFT_PAREN 'or' ( ce[orDescr] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_or_ce345); 
            match(input,30,FOLLOW_30_in_or_ce350); 
            
            	    	orDescr = new OrDescr();
            			in_ce.addDescr( orDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:279:3: ( ce[orDescr] )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( (LA6_0==LEFT_PAREN) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:279:3: ce[orDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_or_ce356);
            	    ce(orDescr);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_or_ce365); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:283:1: not_ce[ConditionalElementDescr in_ce] : LEFT_PAREN 'not' ( ce[notDescr] )? RIGHT_PAREN ;
    public void not_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                NotDescr notDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:287:4: ( LEFT_PAREN 'not' ( ce[notDescr] )? RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:287:4: LEFT_PAREN 'not' ( ce[notDescr] )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_not_ce393); 
            match(input,31,FOLLOW_31_in_not_ce398); 
            
            			notDescr = new NotDescr();
            		    in_ce.addDescr( notDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:292:3: ( ce[notDescr] )?
            int alt7=2;
            int LA7_0 = input.LA(1);
            if ( (LA7_0==LEFT_PAREN) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:292:3: ce[notDescr]
                    {
                    pushFollow(FOLLOW_ce_in_not_ce404);
                    ce(notDescr);
                    _fsp--;


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_not_ce413); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:296:1: exists_ce[ConditionalElementDescr in_ce] : LEFT_PAREN 'exists' ( ce[existsDescr] )? RIGHT_PAREN ;
    public void exists_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                ExistsDescr existsDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:300:4: ( LEFT_PAREN 'exists' ( ce[existsDescr] )? RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:300:4: LEFT_PAREN 'exists' ( ce[existsDescr] )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_exists_ce442); 
            match(input,32,FOLLOW_32_in_exists_ce447); 
            
            		    existsDescr = new ExistsDescr();
            		    in_ce.addDescr( existsDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:305:3: ( ce[existsDescr] )?
            int alt8=2;
            int LA8_0 = input.LA(1);
            if ( (LA8_0==LEFT_PAREN) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:305:3: ce[existsDescr]
                    {
                    pushFollow(FOLLOW_ce_in_exists_ce453);
                    ce(existsDescr);
                    _fsp--;


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_exists_ce462); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:309:1: eval_ce[ConditionalElementDescr in_ce] : LEFT_PAREN 'test' LEFT_PAREN t= ID RIGHT_PAREN RIGHT_PAREN ;
    public void eval_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        Token t=null;

        
                EvalDescr evalDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:313:4: ( LEFT_PAREN 'test' LEFT_PAREN t= ID RIGHT_PAREN RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:313:4: LEFT_PAREN 'test' LEFT_PAREN t= ID RIGHT_PAREN RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_eval_ce490); 
            match(input,33,FOLLOW_33_in_eval_ce495); 
            
            		    evalDescr = new EvalDescr();
            		    in_ce.addDescr( evalDescr );
            		
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_eval_ce501); 
            t=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_eval_ce510); 
            
            			evalDescr.setText( t.getText() );
            		
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_eval_ce516); 
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_eval_ce520); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:326:1: normal_pattern[ConditionalElementDescr in_ce] : LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN ;
    public void normal_pattern(ConditionalElementDescr in_ce) throws RecognitionException {
        Token name=null;

        
                ColumnDescr column = null;
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:330:4: ( LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:330:4: LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_normal_pattern548); 
            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_normal_pattern555); 
            
            			column = new ColumnDescr(name.getText());
            			in_ce.addDescr( column );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:335:3: ( field_constriant[column] )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);
                if ( (LA9_0==LEFT_PAREN) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:335:3: field_constriant[column]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_normal_pattern561);
            	    field_constriant(column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_normal_pattern571); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:341:1: bound_pattern[ConditionalElementDescr in_ce] : var= VAR '<-' LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN ;
    public void bound_pattern(ConditionalElementDescr in_ce) throws RecognitionException {
        Token var=null;
        Token name=null;

        
                ColumnDescr column = null;
                String identifier = null;
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:346:4: (var= VAR '<-' LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:346:4: var= VAR '<-' LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_bound_pattern599); 
            
            			identifier = var.getText();
            		
            match(input,34,FOLLOW_34_in_bound_pattern605); 
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_bound_pattern610); 
            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_bound_pattern617); 
            
            			column = new ColumnDescr(name.getText());
            			column.setIdentifier( identifier );
            			in_ce.addDescr( column );	    
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:356:3: ( field_constriant[column] )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);
                if ( (LA10_0==LEFT_PAREN) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:356:3: field_constriant[column]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_bound_pattern623);
            	    field_constriant(column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_bound_pattern630); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:360:1: field_constriant[ColumnDescr column] : LEFT_PAREN f= ID restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN ;
    public void field_constriant(ColumnDescr column) throws RecognitionException {
        Token f=null;

        
             	List list = new ArrayList();
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		String op = "==";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:367:4: ( LEFT_PAREN f= ID restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:367:4: LEFT_PAREN f= ID restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_field_constriant658); 
            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_field_constriant665); 
            
            			fc = new FieldConstraintDescr(f.getText());
            			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
            			column.addDescr( fc );			
            		
            pushFollow(FOLLOW_restriction_in_field_constriant677);
            restriction(fc,  column);
            _fsp--;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:376:3: ( connective[fc] restriction[fc, column] )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);
                if ( ((LA11_0>=35 && LA11_0<=36)) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:377:5: connective[fc] restriction[fc, column]
            	    {
            	    pushFollow(FOLLOW_connective_in_field_constriant689);
            	    connective(fc);
            	    _fsp--;

            	    pushFollow(FOLLOW_restriction_in_field_constriant706);
            	    restriction(fc,  column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_field_constriant729); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:383:1: connective[FieldConstraintDescr fc] : ( '&' | '|' ) ;
    public void connective(FieldConstraintDescr fc) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:384:4: ( ( '&' | '|' ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:384:4: ( '&' | '|' )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:384:4: ( '&' | '|' )
            int alt12=2;
            int LA12_0 = input.LA(1);
            if ( (LA12_0==35) ) {
                alt12=1;
            }
            else if ( (LA12_0==36) ) {
                alt12=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("384:4: ( '&' | '|' )", 12, 0, input);

                throw nvae;
            }
            switch (alt12) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:384:6: '&'
                    {
                    match(input,35,FOLLOW_35_in_connective747); 
                     fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND)); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:385:9: '|'
                    {
                    match(input,36,FOLLOW_36_in_connective759); 
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:389:1: restriction[FieldConstraintDescr fc, ColumnDescr column] : ( '~' )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) ;
    public void restriction(FieldConstraintDescr fc, ColumnDescr column) throws RecognitionException {
        String lc = null;


        
        			String op = "==";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:393:4: ( ( '~' )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:393:4: ( '~' )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:393:4: ( '~' )?
            int alt13=2;
            int LA13_0 = input.LA(1);
            if ( (LA13_0==37) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:393:5: '~'
                    {
                    match(input,37,FOLLOW_37_in_restriction794); 
                    op = "!=";

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:394:3: ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            int alt14=4;
            switch ( input.LA(1) ) {
            case 38:
                alt14=1;
                break;
            case 39:
                alt14=2;
                break;
            case VAR:
                alt14=3;
                break;
            case ID:
            case STRING:
            case INT:
            case FLOAT:
            case BOOL:
            case NULL:
                alt14=4;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("394:3: ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:394:7: predicate_constraint[op, column]
                    {
                    pushFollow(FOLLOW_predicate_constraint_in_restriction812);
                    predicate_constraint(op,  column);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:395:11: return_value_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_return_value_restriction_in_restriction832);
                    return_value_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:396:10: variable_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_variable_restriction_in_restriction844);
                    variable_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:397:12: lc= literal_restriction
                    {
                    pushFollow(FOLLOW_literal_restriction_in_restriction860);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:404:1: predicate_constraint[String op, ColumnDescr column] : ':' LEFT_PAREN id= ID RIGHT_PAREN ;
    public void predicate_constraint(String op, ColumnDescr column) throws RecognitionException {
        Token id=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:405:4: ( ':' LEFT_PAREN id= ID RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:405:4: ':' LEFT_PAREN id= ID RIGHT_PAREN
            {
            match(input,38,FOLLOW_38_in_predicate_constraint893); 
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_predicate_constraint894); 
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_predicate_constraint901); 
             column.addDescr( new PredicateDescr( id.getText() ) ); 
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_predicate_constraint908); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:411:1: return_value_restriction[String op, FieldConstraintDescr fc] : '=' LEFT_PAREN id= ID RIGHT_PAREN ;
    public void return_value_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {
        Token id=null;

        
        		PredicateDescr d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:415:4: ( '=' LEFT_PAREN id= ID RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:415:4: '=' LEFT_PAREN id= ID RIGHT_PAREN
            {
            match(input,39,FOLLOW_39_in_return_value_restriction927); 
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_return_value_restriction928); 
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_return_value_restriction935); 
             fc.addRestriction( new ReturnValueRestrictionDescr(op, id.getText() ) ); 
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_return_value_restriction942); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:420:1: variable_restriction[String op, FieldConstraintDescr fc] : var= VAR ;
    public void variable_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {
        Token var=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:421:4: (var= VAR )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:421:4: var= VAR
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_variable_restriction958); 
            
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:427:1: literal_restriction returns [String text] : t= literal ;
    public String literal_restriction() throws RecognitionException {
        String text = null;

        String t = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:432:6: (t= literal )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:432:6: t= literal
            {
            pushFollow(FOLLOW_literal_in_literal_restriction991);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:437:1: function : LEFT_PAREN ID ( VAR | literal | function ) RIGHT_PAREN ;
    public void function() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:438:4: ( LEFT_PAREN ID ( VAR | literal | function ) RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:438:4: LEFT_PAREN ID ( VAR | literal | function ) RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function1011); 
            match(input,ID,FOLLOW_ID_in_function1015); 
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:440:6: ( VAR | literal | function )
            int alt15=3;
            switch ( input.LA(1) ) {
            case VAR:
                alt15=1;
                break;
            case ID:
            case STRING:
            case INT:
            case FLOAT:
            case BOOL:
            case NULL:
                alt15=2;
                break;
            case LEFT_PAREN:
                alt15=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("440:6: ( VAR | literal | function )", 15, 0, input);

                throw nvae;
            }

            switch (alt15) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:440:10: VAR
                    {
                    match(input,VAR,FOLLOW_VAR_in_function1026); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:441:10: literal
                    {
                    pushFollow(FOLLOW_literal_in_function1037);
                    literal();
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:442:10: function
                    {
                    pushFollow(FOLLOW_function_in_function1048);
                    function();
                    _fsp--;


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function1062); 

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


    // $ANTLR start literal
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:448:1: literal returns [String text] : (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public String literal() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:452:4: ( (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:452:4: (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:452:4: (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt16=6;
            switch ( input.LA(1) ) {
            case STRING:
                alt16=1;
                break;
            case ID:
                alt16=2;
                break;
            case INT:
                alt16=3;
                break;
            case FLOAT:
                alt16=4;
                break;
            case BOOL:
                alt16=5;
                break;
            case NULL:
                alt16=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("452:4: (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL )", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:452:8: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal1091); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:453:7: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_literal1104); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:454:7: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal1120); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:455:7: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal1135); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:456:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal1148); 
                     text = t.getText(); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:457:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal1162); 
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


 

    public static final BitSet FOLLOW_27_in_opt_semicolon38 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_rule64 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_rule66 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_rule74 = new BitSet(new long[]{0x00000000000000D0L});
    public static final BitSet FOLLOW_lhs_in_rule85 = new BitSet(new long[]{0x00000000000000D0L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_rule92 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_lhs109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_lhs119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_lhs128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_lhs137 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_lhs151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_lhs165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bound_pattern_in_lhs174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_ce196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_ce209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_ce218 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_ce227 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_ce241 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_ce265 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_and_ce297 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_and_ce302 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_ce_in_and_ce308 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_and_ce317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_or_ce345 = new BitSet(new long[]{0x0000000040000000L});
    public static final BitSet FOLLOW_30_in_or_ce350 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_ce_in_or_ce356 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_or_ce365 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_not_ce393 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_not_ce398 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_ce_in_not_ce404 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_not_ce413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_exists_ce442 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_32_in_exists_ce447 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_ce_in_exists_ce453 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_exists_ce462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_eval_ce490 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_eval_ce495 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_eval_ce501 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_eval_ce510 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_eval_ce516 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_eval_ce520 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_normal_pattern548 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_normal_pattern555 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_normal_pattern561 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_normal_pattern571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_bound_pattern599 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_bound_pattern605 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_bound_pattern610 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_bound_pattern617 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_bound_pattern623 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_bound_pattern630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_field_constriant658 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_field_constriant665 = new BitSet(new long[]{0x000000E000001FA0L});
    public static final BitSet FOLLOW_restriction_in_field_constriant677 = new BitSet(new long[]{0x0000001800000040L});
    public static final BitSet FOLLOW_connective_in_field_constriant689 = new BitSet(new long[]{0x000000E000001FA0L});
    public static final BitSet FOLLOW_restriction_in_field_constriant706 = new BitSet(new long[]{0x0000001800000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_field_constriant729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_connective747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_connective759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_restriction794 = new BitSet(new long[]{0x000000C000001FA0L});
    public static final BitSet FOLLOW_predicate_constraint_in_restriction812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_return_value_restriction_in_restriction832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_restriction_in_restriction844 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_restriction_in_restriction860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_predicate_constraint893 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_predicate_constraint894 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_predicate_constraint901 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_predicate_constraint908 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_return_value_restriction927 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_return_value_restriction928 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_return_value_restriction935 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_return_value_restriction942 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_variable_restriction958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_literal_restriction991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function1011 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_function1015 = new BitSet(new long[]{0x0000000000001FB0L});
    public static final BitSet FOLLOW_VAR_in_function1026 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_literal_in_function1037 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_function_in_function1048 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function1062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal1091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_literal1104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal1120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal1135 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal1148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal1162 = new BitSet(new long[]{0x0000000000000002L});

}