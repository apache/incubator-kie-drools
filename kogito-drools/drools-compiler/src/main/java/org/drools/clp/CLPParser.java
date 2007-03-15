// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-15 04:40:35

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LEFT_PAREN", "DEFRULE", "ID", "STRING", "RIGHT_PAREN", "MODULE", "SALIENCE", "INT", "AND", "OR", "NOT", "EXISTS", "TEST", "VAR", "AMPERSAND", "PIPE", "TILDE", "FLOAT", "BOOL", "NULL", "MISC", "SYMBOL", "EOL", "WS", "DECLARE", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "MULTI_LINE_COMMENT", "';'", "'=>'", "'<-'", "':'", "'='", "'modify'"
    };
    public static final int EXISTS=15;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=38;
    public static final int BOOL=22;
    public static final int DEFRULE=5;
    public static final int HexDigit=30;
    public static final int WS=27;
    public static final int MISC=24;
    public static final int STRING=7;
    public static final int FLOAT=21;
    public static final int TILDE=20;
    public static final int OR=13;
    public static final int PIPE=19;
    public static final int VAR=17;
    public static final int UnicodeEscape=31;
    public static final int AND=12;
    public static final int EscapeSequence=29;
    public static final int INT=11;
    public static final int EOF=-1;
    public static final int EOL=26;
    public static final int NULL=23;
    public static final int MODULE=9;
    public static final int SYMBOL=25;
    public static final int LEFT_SQUARE=35;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=33;
    public static final int OctalEscape=32;
    public static final int SALIENCE=10;
    public static final int MULTI_LINE_COMMENT=39;
    public static final int TEST=16;
    public static final int AMPERSAND=18;
    public static final int DECLARE=28;
    public static final int RIGHT_PAREN=8;
    public static final int NOT=14;
    public static final int LEFT_CURLY=37;
    public static final int RIGHT_SQUARE=36;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=34;
    public static final int ID=6;

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
            if ( (LA1_0==40) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:168:4: ';'
                    {
                    match(input,40,FOLLOW_40_in_opt_semicolon38); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:211:1: rule returns [RuleDescr rule] : loc= LEFT_PAREN DEFRULE (module= agenda_group )? ruleName= ID documentation= STRING ( ruleAttribute[rule] )* ( lhs[lhs] )* rhs[rule] RIGHT_PAREN ;
    public RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        Token ruleName=null;
        Token documentation=null;
        AttributeDescr module = null;


         
        	        rule = null; 
        	        AndDescr lhs = null;
        	        ColumnDescr colum = null;	        
        	      
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:217:4: (loc= LEFT_PAREN DEFRULE (module= agenda_group )? ruleName= ID documentation= STRING ( ruleAttribute[rule] )* ( lhs[lhs] )* rhs[rule] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:217:4: loc= LEFT_PAREN DEFRULE (module= agenda_group )? ruleName= ID documentation= STRING ( ruleAttribute[rule] )* ( lhs[lhs] )* rhs[rule] RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_rule65); 
            match(input,DEFRULE,FOLLOW_DEFRULE_in_rule73); 
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:221:3: (module= agenda_group )?
            int alt2=2;
            int LA2_0 = input.LA(1);
            if ( (LA2_0==MODULE) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:221:5: module= agenda_group
                    {
                     System.err.println( "before" ); 
                    pushFollow(FOLLOW_agenda_group_in_rule86);
                    module=agenda_group();
                    _fsp--;


                    }
                    break;

            }

            ruleName=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_rule100); 
             	  			  		
            	  		debug( "start rule: " + ruleName.getText() );
            	        rule = new RuleDescr( ruleName.getText(), null ); 
            	        
            	        if ( module != null ) {
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
            match(input,STRING,FOLLOW_STRING_in_rule112); 
            
            	    	// do nothing here for now
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:244:3: ( ruleAttribute[rule] )*
            loop3:
            do {
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:244:3: ruleAttribute[rule]
            	    {
            	    pushFollow(FOLLOW_ruleAttribute_in_rule118);
            	    ruleAttribute(rule);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:246:3: ( lhs[lhs] )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);
                if ( (LA4_0==LEFT_PAREN||LA4_0==VAR) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:246:3: lhs[lhs]
            	    {
            	    pushFollow(FOLLOW_lhs_in_rule127);
            	    lhs(lhs);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            pushFollow(FOLLOW_rhs_in_rule136);
            rhs(rule);
            _fsp--;

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_rule144); 

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


    // $ANTLR start agenda_group
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:254:1: agenda_group returns [AttributeDescr d ] : t= MODULE ;
    public AttributeDescr agenda_group() throws RecognitionException {
        AttributeDescr d = null;

        Token t=null;

        
        		d = null;
        		System.out.println( "agenda group init" );
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:260:3: (t= MODULE )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:260:3: t= MODULE
            {
            t=(Token)input.LT(1);
            match(input,MODULE,FOLLOW_MODULE_in_agenda_group170); 
            
            			System.out.println( "agenda group body" );		
            			d = new AttributeDescr( "agenda-group", t.getText() );
            			d.setLocation( offset(t.getLine()), t.getCharPositionInLine() );
            			d.setStartCharacter( ((CommonToken)t).getStartIndex() );
            			d.setEndCharacter( ((CommonToken)t).getStopIndex() );
            		

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


    // $ANTLR start ruleAttribute
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:271:1: ruleAttribute[RuleDescr rule] : LEFT_PAREN 'declare' LEFT_PAREN d= salience RIGHT_PAREN RIGHT_PAREN ;
    public void ruleAttribute(RuleDescr rule) throws RecognitionException {
        AttributeDescr d = null;


        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:273:3: ( LEFT_PAREN 'declare' LEFT_PAREN d= salience RIGHT_PAREN RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:273:3: LEFT_PAREN 'declare' LEFT_PAREN d= salience RIGHT_PAREN RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute190); 
            match(input,DECLARE,FOLLOW_DECLARE_in_ruleAttribute192); 
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute197); 
            pushFollow(FOLLOW_salience_in_ruleAttribute201);
            d=salience();
            _fsp--;

             rule.addAttribute( d ); 
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute208); 
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute212); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:279:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:284:3: (loc= SALIENCE i= INT )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:284:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience239); 
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience243); 
            
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:294:1: lhs[ConditionalElementDescr in_ce] : ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) ;
    public void lhs(ConditionalElementDescr in_ce) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:295:4: ( ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:295:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:295:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            int alt5=7;
            int LA5_0 = input.LA(1);
            if ( (LA5_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case ID:
                    alt5=6;
                    break;
                case NOT:
                    alt5=3;
                    break;
                case TEST:
                    alt5=5;
                    break;
                case AND:
                    alt5=1;
                    break;
                case EXISTS:
                    alt5=4;
                    break;
                case OR:
                    alt5=2;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("295:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 5, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA5_0==VAR) ) {
                alt5=7;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("295:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:295:8: and_ce[in_ce]
                    {
                    pushFollow(FOLLOW_and_ce_in_lhs270);
                    and_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:296:7: or_ce[in_ce]
                    {
                    pushFollow(FOLLOW_or_ce_in_lhs280);
                    or_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:297:7: not_ce[in_ce]
                    {
                    pushFollow(FOLLOW_not_ce_in_lhs289);
                    not_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:298:7: exists_ce[in_ce]
                    {
                    pushFollow(FOLLOW_exists_ce_in_lhs298);
                    exists_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:299:8: eval_ce[in_ce]
                    {
                    pushFollow(FOLLOW_eval_ce_in_lhs312);
                    eval_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:300:7: normal_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_lhs326);
                    normal_pattern(in_ce);
                    _fsp--;


                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:301:7: bound_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_bound_pattern_in_lhs335);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:305:1: rhs[RuleDescr rule] : '=>' ( function[context] )* ;
    public void rhs(RuleDescr rule) throws RecognitionException {
        
        	        ExecutionEngine engine = new BlockExecutionEngine();
        			ExecutionBuildContext context = new ExecutionBuildContext( engine );  	
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:312:3: ( '=>' ( function[context] )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:312:3: '=>' ( function[context] )*
            {
            match(input,41,FOLLOW_41_in_rhs362); 
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:313:4: ( function[context] )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);
                if ( (LA6_0==LEFT_PAREN) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:313:4: function[context]
            	    {
            	    pushFollow(FOLLOW_function_in_rhs367);
            	    function(context);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop6;
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:316:1: ce[ConditionalElementDescr in_ce] : ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] ) ;
    public void ce(ConditionalElementDescr in_ce) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:317:4: ( ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:317:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:317:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )
            int alt7=6;
            int LA7_0 = input.LA(1);
            if ( (LA7_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case EXISTS:
                    alt7=4;
                    break;
                case ID:
                    alt7=6;
                    break;
                case TEST:
                    alt7=5;
                    break;
                case AND:
                    alt7=1;
                    break;
                case OR:
                    alt7=2;
                    break;
                case NOT:
                    alt7=3;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("317:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )", 7, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("317:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] )", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:317:8: and_ce[in_ce]
                    {
                    pushFollow(FOLLOW_and_ce_in_ce391);
                    and_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:318:10: or_ce[in_ce]
                    {
                    pushFollow(FOLLOW_or_ce_in_ce404);
                    or_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:319:7: not_ce[in_ce]
                    {
                    pushFollow(FOLLOW_not_ce_in_ce413);
                    not_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:320:7: exists_ce[in_ce]
                    {
                    pushFollow(FOLLOW_exists_ce_in_ce422);
                    exists_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:321:8: eval_ce[in_ce]
                    {
                    pushFollow(FOLLOW_eval_ce_in_ce436);
                    eval_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:322:10: normal_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_ce460);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:327:1: and_ce[ConditionalElementDescr in_ce] : LEFT_PAREN AND ( ce[andDescr] )* RIGHT_PAREN ;
    public void and_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                AndDescr andDescr= null;        
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:331:4: ( LEFT_PAREN AND ( ce[andDescr] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:331:4: LEFT_PAREN AND ( ce[andDescr] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_and_ce492); 
            match(input,AND,FOLLOW_AND_in_and_ce497); 
            
            	    	andDescr = new AndDescr();
            			in_ce.addDescr( andDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:336:3: ( ce[andDescr] )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);
                if ( (LA8_0==LEFT_PAREN) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:336:3: ce[andDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_and_ce503);
            	    ce(andDescr);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_and_ce512); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:340:1: or_ce[ConditionalElementDescr in_ce] : LEFT_PAREN OR ( ce[orDescr] )* RIGHT_PAREN ;
    public void or_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                OrDescr orDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:344:4: ( LEFT_PAREN OR ( ce[orDescr] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:344:4: LEFT_PAREN OR ( ce[orDescr] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_or_ce540); 
            match(input,OR,FOLLOW_OR_in_or_ce545); 
            
            	    	orDescr = new OrDescr();
            			in_ce.addDescr( orDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:349:3: ( ce[orDescr] )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);
                if ( (LA9_0==LEFT_PAREN) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:349:3: ce[orDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_or_ce551);
            	    ce(orDescr);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_or_ce560); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:353:1: not_ce[ConditionalElementDescr in_ce] : LEFT_PAREN NOT ( ce[notDescr] )? RIGHT_PAREN ;
    public void not_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                NotDescr notDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:357:4: ( LEFT_PAREN NOT ( ce[notDescr] )? RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:357:4: LEFT_PAREN NOT ( ce[notDescr] )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_not_ce588); 
            match(input,NOT,FOLLOW_NOT_in_not_ce593); 
            
            			notDescr = new NotDescr();
            		    in_ce.addDescr( notDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:362:3: ( ce[notDescr] )?
            int alt10=2;
            int LA10_0 = input.LA(1);
            if ( (LA10_0==LEFT_PAREN) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:362:3: ce[notDescr]
                    {
                    pushFollow(FOLLOW_ce_in_not_ce599);
                    ce(notDescr);
                    _fsp--;


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_not_ce608); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:366:1: exists_ce[ConditionalElementDescr in_ce] : LEFT_PAREN EXISTS ( ce[existsDescr] )? RIGHT_PAREN ;
    public void exists_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                ExistsDescr existsDescr= null;        
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:370:4: ( LEFT_PAREN EXISTS ( ce[existsDescr] )? RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:370:4: LEFT_PAREN EXISTS ( ce[existsDescr] )? RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_exists_ce637); 
            match(input,EXISTS,FOLLOW_EXISTS_in_exists_ce642); 
            
            		    existsDescr = new ExistsDescr();
            		    in_ce.addDescr( existsDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:375:3: ( ce[existsDescr] )?
            int alt11=2;
            int LA11_0 = input.LA(1);
            if ( (LA11_0==LEFT_PAREN) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:375:3: ce[existsDescr]
                    {
                    pushFollow(FOLLOW_ce_in_exists_ce648);
                    ce(existsDescr);
                    _fsp--;


                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_exists_ce657); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:379:1: eval_ce[ConditionalElementDescr in_ce] : LEFT_PAREN TEST function[context] RIGHT_PAREN ;
    public void eval_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                EvalDescr evalDescr= null;    
           		ExecutionEngine engine = new CLPEval();     
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );   		         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:385:4: ( LEFT_PAREN TEST function[context] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:385:4: LEFT_PAREN TEST function[context] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_eval_ce685); 
            match(input,TEST,FOLLOW_TEST_in_eval_ce690); 
            
            		    evalDescr = new EvalDescr();
            		    in_ce.addDescr( evalDescr );
            		
            pushFollow(FOLLOW_function_in_eval_ce696);
            function(context);
            _fsp--;

            					
            			evalDescr.setContent( engine );			
            		
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_eval_ce707); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:396:1: normal_pattern[ConditionalElementDescr in_ce] : LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN ;
    public void normal_pattern(ConditionalElementDescr in_ce) throws RecognitionException {
        Token name=null;

        
                ColumnDescr column = null;
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:400:4: ( LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:400:4: LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_normal_pattern735); 
            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_normal_pattern742); 
            
            			column = new ColumnDescr(name.getText());
            			in_ce.addDescr( column );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:405:3: ( field_constriant[column] )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);
                if ( (LA12_0==LEFT_PAREN) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:405:3: field_constriant[column]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_normal_pattern748);
            	    field_constriant(column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_normal_pattern758); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:411:1: bound_pattern[ConditionalElementDescr in_ce] : var= VAR '<-' LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN ;
    public void bound_pattern(ConditionalElementDescr in_ce) throws RecognitionException {
        Token var=null;
        Token name=null;

        
                ColumnDescr column = null;
                String identifier = null;
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:416:4: (var= VAR '<-' LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:416:4: var= VAR '<-' LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_bound_pattern786); 
            
            			identifier = var.getText();
            		
            match(input,42,FOLLOW_42_in_bound_pattern792); 
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_bound_pattern797); 
            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_bound_pattern804); 
            
            			column = new ColumnDescr(name.getText());
            			column.setIdentifier( identifier );
            			in_ce.addDescr( column );	    
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:426:3: ( field_constriant[column] )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);
                if ( (LA13_0==LEFT_PAREN) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:426:3: field_constriant[column]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_bound_pattern810);
            	    field_constriant(column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_bound_pattern817); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:430:1: field_constriant[ColumnDescr column] : LEFT_PAREN f= ID restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN ;
    public void field_constriant(ColumnDescr column) throws RecognitionException {
        Token f=null;

        
             	List list = new ArrayList();
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		String op = "==";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:437:4: ( LEFT_PAREN f= ID restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:437:4: LEFT_PAREN f= ID restriction[fc, column] ( connective[fc] restriction[fc, column] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_field_constriant845); 
            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_field_constriant852); 
            
            			fc = new FieldConstraintDescr(f.getText());
            			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
            			column.addDescr( fc );			
            		
            pushFollow(FOLLOW_restriction_in_field_constriant864);
            restriction(fc,  column);
            _fsp--;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:446:3: ( connective[fc] restriction[fc, column] )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);
                if ( ((LA14_0>=AMPERSAND && LA14_0<=PIPE)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:447:5: connective[fc] restriction[fc, column]
            	    {
            	    pushFollow(FOLLOW_connective_in_field_constriant876);
            	    connective(fc);
            	    _fsp--;

            	    pushFollow(FOLLOW_restriction_in_field_constriant893);
            	    restriction(fc,  column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_field_constriant916); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:453:1: connective[FieldConstraintDescr fc] : ( AMPERSAND | PIPE ) ;
    public void connective(FieldConstraintDescr fc) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:454:4: ( ( AMPERSAND | PIPE ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:454:4: ( AMPERSAND | PIPE )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:454:4: ( AMPERSAND | PIPE )
            int alt15=2;
            int LA15_0 = input.LA(1);
            if ( (LA15_0==AMPERSAND) ) {
                alt15=1;
            }
            else if ( (LA15_0==PIPE) ) {
                alt15=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("454:4: ( AMPERSAND | PIPE )", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:454:6: AMPERSAND
                    {
                    match(input,AMPERSAND,FOLLOW_AMPERSAND_in_connective934); 
                     fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND)); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:455:9: PIPE
                    {
                    match(input,PIPE,FOLLOW_PIPE_in_connective946); 
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:459:1: restriction[FieldConstraintDescr fc, ColumnDescr column] : ( TILDE )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) ;
    public void restriction(FieldConstraintDescr fc, ColumnDescr column) throws RecognitionException {
        String lc = null;


        
        			String op = "==";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:463:4: ( ( TILDE )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:463:4: ( TILDE )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:463:4: ( TILDE )?
            int alt16=2;
            int LA16_0 = input.LA(1);
            if ( (LA16_0==TILDE) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:463:5: TILDE
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_restriction981); 
                    op = "!=";

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:464:3: ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            int alt17=4;
            switch ( input.LA(1) ) {
            case 43:
                alt17=1;
                break;
            case 44:
                alt17=2;
                break;
            case VAR:
                alt17=3;
                break;
            case ID:
            case STRING:
            case INT:
            case FLOAT:
            case BOOL:
            case NULL:
                alt17=4;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("464:3: ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:464:6: predicate_constraint[op, column]
                    {
                    pushFollow(FOLLOW_predicate_constraint_in_restriction998);
                    predicate_constraint(op,  column);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:465:11: return_value_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_return_value_restriction_in_restriction1018);
                    return_value_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:466:10: variable_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_variable_restriction_in_restriction1030);
                    variable_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:467:12: lc= literal_restriction
                    {
                    pushFollow(FOLLOW_literal_restriction_in_restriction1046);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:474:1: predicate_constraint[String op, ColumnDescr column] : ':' function[context] ;
    public void predicate_constraint(String op, ColumnDescr column) throws RecognitionException {
        
           		ExecutionEngine engine = new CLPPredicate();
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );    
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:479:4: ( ':' function[context] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:479:4: ':' function[context]
            {
            match(input,43,FOLLOW_43_in_predicate_constraint1088); 
            pushFollow(FOLLOW_function_in_predicate_constraint1092);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:486:1: return_value_restriction[String op, FieldConstraintDescr fc] : '=' function[context] ;
    public void return_value_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {
        
        		ExecutionEngine engine = new CLPReturnValue();
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:491:4: ( '=' function[context] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:491:4: '=' function[context]
            {
            match(input,44,FOLLOW_44_in_return_value_restriction1115); 
            pushFollow(FOLLOW_function_in_return_value_restriction1120);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:497:1: variable_restriction[String op, FieldConstraintDescr fc] : var= VAR ;
    public void variable_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {
        Token var=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:498:4: (var= VAR )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:498:4: var= VAR
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_variable_restriction1141); 
            
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:504:1: literal_restriction returns [String text] : t= literal ;
    public String literal_restriction() throws RecognitionException {
        String text = null;

        String t = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:509:6: (t= literal )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:509:6: t= literal
            {
            pushFollow(FOLLOW_literal_in_literal_restriction1174);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:514:1: function[ExecutionBuildContext context] returns [Function f] : LEFT_PAREN name= function_name ( function_params[context, f] )+ RIGHT_PAREN ;
    public Function function(ExecutionBuildContext context) throws RecognitionException {
        Function f = null;

        Token name = null;


        
        	    FunctionFactory factory = FunctionFactory.getInstance();
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:518:4: ( LEFT_PAREN name= function_name ( function_params[context, f] )+ RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:518:4: LEFT_PAREN name= function_name ( function_params[context, f] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function1199); 
            pushFollow(FOLLOW_function_name_in_function1205);
            name=function_name();
            _fsp--;

            
            			if ( name.getText().equals("bind") ) {
            		  		context.createLocalVariable( name.getText() );
            			}
            		  	f = factory.createFunction( name.getText() );		  
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:526:3: ( function_params[context, f] )+
            int cnt18=0;
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);
                if ( (LA18_0==LEFT_PAREN||(LA18_0>=ID && LA18_0<=STRING)||LA18_0==INT||LA18_0==VAR||(LA18_0>=FLOAT && LA18_0<=NULL)) ) {
                    alt18=1;
                }


                switch (alt18) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:526:3: function_params[context, f]
            	    {
            	    pushFollow(FOLLOW_function_params_in_function1218);
            	    function_params(context,  f);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function1228); 
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:532:1: modify_function[ExecutionBuildContext context] returns [Function f] : LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN ;
    public Function modify_function(ExecutionBuildContext context) throws RecognitionException {
        Function f = null;

        
        	    FunctionFactory factory = FunctionFactory.getInstance();
        		f = factory.createFunction( "modify" );
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:538:3: ( LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:538:3: LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_modify_function1259); 
            match(input,45,FOLLOW_45_in_modify_function1264); 
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:540:4: ( slot_name_value_pair[context, f] )+
            int cnt19=0;
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);
                if ( (LA19_0==LEFT_PAREN) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:540:4: slot_name_value_pair[context, f]
            	    {
            	    pushFollow(FOLLOW_slot_name_value_pair_in_modify_function1269);
            	    slot_name_value_pair(context,  f);
            	    _fsp--;


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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_modify_function1275); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:544:1: function_params[ExecutionBuildContext context, Function f] : (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) ;
    public void function_params(ExecutionBuildContext context, Function f) throws RecognitionException {
        Token t=null;
        Function nf = null;


        
        		ValueHandler value  =  null;		
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:549:3: ( (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:549:3: (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:549:3: (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            int alt20=8;
            switch ( input.LA(1) ) {
            case VAR:
                alt20=1;
                break;
            case STRING:
                alt20=2;
                break;
            case ID:
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
                    new NoViableAltException("549:3: (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:549:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_function_params1304); 
                     value = context.getVariableValueHandler(t.getText() ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:550:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_function_params1316); 
                     value = new ObjectLiteralValue( getString( t ) ); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:551:7: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_function_params1331); 
                     value = new ObjectLiteralValue( t.getText() ); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:552:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_function_params1352); 
                     value = new DoubleLiteralValue( t.getText() ); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:553:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_function_params1367); 
                     value = new LongLiteralValue( t.getText() ); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:6: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_function_params1387); 
                     value = new BooleanLiteralValue( t.getText() ); 

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:555:6: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_function_params1409); 
                     value = ObjectLiteralValue.NULL; 

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:556:6: nf= function[context]
                    {
                    pushFollow(FOLLOW_function_in_function_params1425);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:562:1: slot_name_value_pair[ExecutionBuildContext context, Function f] : LEFT_PAREN id= ID (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN ;
    public void slot_name_value_pair(ExecutionBuildContext context, Function f) throws RecognitionException {
        Token id=null;
        Token t=null;
        Function nf = null;


        
        		SlotNameValuePair nameValuePair = null;
        		String name = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:568:3: ( LEFT_PAREN id= ID (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:568:3: LEFT_PAREN id= ID (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_slot_name_value_pair1467); 
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_slot_name_value_pair1473); 
            
            			name = id.getText();
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:572:3: (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            int alt21=8;
            switch ( input.LA(1) ) {
            case VAR:
                alt21=1;
                break;
            case STRING:
                alt21=2;
                break;
            case ID:
                alt21=3;
                break;
            case FLOAT:
                alt21=4;
                break;
            case INT:
                alt21=5;
                break;
            case BOOL:
                alt21=6;
                break;
            case NULL:
                alt21=7;
                break;
            case LEFT_PAREN:
                alt21=8;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("572:3: (t= VAR | t= STRING | t= ID | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:572:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_slot_name_value_pair1484); 
                     nameValuePair = new SlotNameValuePair(name, context.getVariableValueHandler( t.getText() ) ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:573:7: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_slot_name_value_pair1502); 
                     nameValuePair = new SlotNameValuePair(name, new ObjectLiteralValue( getString( t ) ) ); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:574:7: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_slot_name_value_pair1517); 
                     nameValuePair = new SlotNameValuePair(name, new ObjectLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_slot_name_value_pair1538); 
                     nameValuePair = new SlotNameValuePair(name, new DoubleLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:576:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_slot_name_value_pair1553); 
                     nameValuePair = new SlotNameValuePair(name, new LongLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:577:6: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_slot_name_value_pair1573); 
                     nameValuePair = new SlotNameValuePair(name, new BooleanLiteralValue( t.getText() ) ) ; 

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:578:6: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_slot_name_value_pair1595); 
                     nameValuePair = new SlotNameValuePair(name, ObjectLiteralValue.NULL ); 

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:6: nf= function[context]
                    {
                    pushFollow(FOLLOW_function_in_slot_name_value_pair1611);
                    nf=function(context);
                    _fsp--;

                     nameValuePair = new SlotNameValuePair(name, nf ); 

                    }
                    break;

            }

             f.addParameter( nameValuePair ); 
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_slot_name_value_pair1648); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:586:1: literal returns [String text] : (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public String literal() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:4: ( (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:4: (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:4: (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt22=6;
            switch ( input.LA(1) ) {
            case STRING:
                alt22=1;
                break;
            case ID:
                alt22=2;
                break;
            case INT:
                alt22=3;
                break;
            case FLOAT:
                alt22=4;
                break;
            case BOOL:
                alt22=5;
                break;
            case NULL:
                alt22=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("590:4: (t= STRING | t= ID | t= INT | t= FLOAT | t= BOOL | t= NULL )", 22, 0, input);

                throw nvae;
            }

            switch (alt22) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:8: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal1677); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:591:7: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_literal1690); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:592:7: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal1706); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:593:7: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal1721); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:594:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal1734); 
                     text = t.getText(); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal1748); 
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:599:1: function_name returns [Token tok] : (t= ID | t= MISC | t= SYMBOL ) ;
    public Token function_name() throws RecognitionException {
        Token tok = null;

        Token t=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:2: ( (t= ID | t= MISC | t= SYMBOL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:2: (t= ID | t= MISC | t= SYMBOL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:2: (t= ID | t= MISC | t= SYMBOL )
            int alt23=3;
            switch ( input.LA(1) ) {
            case ID:
                alt23=1;
                break;
            case MISC:
                alt23=2;
                break;
            case SYMBOL:
                alt23=3;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("601:2: (t= ID | t= MISC | t= SYMBOL )", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:601:4: t= ID
                    {
                    t=(Token)input.LT(1);
                    match(input,ID,FOLLOW_ID_in_function_name1777); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:602:4: t= MISC
                    {
                    t=(Token)input.LT(1);
                    match(input,MISC,FOLLOW_MISC_in_function_name1785); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:603:4: t= SYMBOL
                    {
                    t=(Token)input.LT(1);
                    match(input,SYMBOL,FOLLOW_SYMBOL_in_function_name1792); 

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


 

    public static final BitSet FOLLOW_40_in_opt_semicolon38 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_rule65 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DEFRULE_in_rule73 = new BitSet(new long[]{0x0000000000000240L});
    public static final BitSet FOLLOW_agenda_group_in_rule86 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ID_in_rule100 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_rule112 = new BitSet(new long[]{0x0000020000020010L});
    public static final BitSet FOLLOW_ruleAttribute_in_rule118 = new BitSet(new long[]{0x0000020000020010L});
    public static final BitSet FOLLOW_lhs_in_rule127 = new BitSet(new long[]{0x0000020000020010L});
    public static final BitSet FOLLOW_rhs_in_rule136 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_rule144 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MODULE_in_agenda_group170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute190 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_DECLARE_in_ruleAttribute192 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute197 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_salience_in_ruleAttribute201 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute208 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience239 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_INT_in_salience243 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_lhs270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_lhs280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_lhs289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_lhs298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_lhs312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_lhs326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bound_pattern_in_lhs335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_rhs362 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_function_in_rhs367 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_and_ce_in_ce391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_ce404 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_ce413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_ce422 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_ce436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_ce460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_and_ce492 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_AND_in_and_ce497 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_and_ce503 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_and_ce512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_or_ce540 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_OR_in_or_ce545 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_or_ce551 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_or_ce560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_not_ce588 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_NOT_in_not_ce593 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_not_ce599 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_not_ce608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_exists_ce637 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_EXISTS_in_exists_ce642 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_exists_ce648 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_exists_ce657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_eval_ce685 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_TEST_in_eval_ce690 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_eval_ce696 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_eval_ce707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_normal_pattern735 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ID_in_normal_pattern742 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_field_constriant_in_normal_pattern748 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_normal_pattern758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_bound_pattern786 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_bound_pattern792 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_bound_pattern797 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ID_in_bound_pattern804 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_field_constriant_in_bound_pattern810 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_bound_pattern817 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_field_constriant845 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ID_in_field_constriant852 = new BitSet(new long[]{0x0000180000F208C0L});
    public static final BitSet FOLLOW_restriction_in_field_constriant864 = new BitSet(new long[]{0x00000000000C0100L});
    public static final BitSet FOLLOW_connective_in_field_constriant876 = new BitSet(new long[]{0x0000180000F208C0L});
    public static final BitSet FOLLOW_restriction_in_field_constriant893 = new BitSet(new long[]{0x00000000000C0100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_field_constriant916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AMPERSAND_in_connective934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PIPE_in_connective946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_restriction981 = new BitSet(new long[]{0x0000180000E208C0L});
    public static final BitSet FOLLOW_predicate_constraint_in_restriction998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_return_value_restriction_in_restriction1018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_restriction_in_restriction1030 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_restriction_in_restriction1046 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_predicate_constraint1088 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_predicate_constraint1092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_return_value_restriction1115 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_return_value_restriction1120 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_variable_restriction1141 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_literal_restriction1174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function1199 = new BitSet(new long[]{0x0000000003000040L});
    public static final BitSet FOLLOW_function_name_in_function1205 = new BitSet(new long[]{0x0000000000E208D0L});
    public static final BitSet FOLLOW_function_params_in_function1218 = new BitSet(new long[]{0x0000000000E209D0L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function1228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_modify_function1259 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_45_in_modify_function1264 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_slot_name_value_pair_in_modify_function1269 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_modify_function1275 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_function_params1304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_function_params1316 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_params1331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_function_params1352 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_function_params1367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_function_params1387 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_function_params1409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_function_params1425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_slot_name_value_pair1467 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ID_in_slot_name_value_pair1473 = new BitSet(new long[]{0x0000000000E208D0L});
    public static final BitSet FOLLOW_VAR_in_slot_name_value_pair1484 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_slot_name_value_pair1502 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_slot_name_value_pair1517 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_FLOAT_in_slot_name_value_pair1538 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_INT_in_slot_name_value_pair1553 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_slot_name_value_pair1573 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_NULL_in_slot_name_value_pair1595 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_function_in_slot_name_value_pair1611 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_slot_name_value_pair1648 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal1677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_literal1690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal1706 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal1721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal1734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal1748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_function_name1777 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MISC_in_function_name1785 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SYMBOL_in_function_name1792 = new BitSet(new long[]{0x0000000000000002L});

}