// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-18 12:17:59

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LEFT_PAREN", "DEFRULE", "NAME", "STRING", "RIGHT_PAREN", "SALIENCE", "INT", "AND", "OR", "NOT", "EXISTS", "TEST", "VAR", "ASSIGN_OP", "AMPERSAND", "PIPE", "TILDE", "COLON", "EQUALS", "FLOAT", "BOOL", "NULL", "EOL", "WS", "DECLARE", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SYMBOL", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "MULTI_LINE_COMMENT", "';'", "'=>'", "'modify'"
    };
    public static final int EXISTS=14;
    public static final int LEFT_PAREN=4;
    public static final int RIGHT_CURLY=39;
    public static final int BOOL=24;
    public static final int DEFRULE=5;
    public static final int HexDigit=30;
    public static final int WS=27;
    public static final int STRING=7;
    public static final int FLOAT=23;
    public static final int TILDE=20;
    public static final int OR=12;
    public static final int PIPE=19;
    public static final int EQUALS=22;
    public static final int VAR=16;
    public static final int ASSIGN_OP=17;
    public static final int UnicodeEscape=31;
    public static final int AND=11;
    public static final int EscapeSequence=29;
    public static final int INT=10;
    public static final int EOF=-1;
    public static final int EOL=26;
    public static final int NULL=25;
    public static final int SYMBOL=33;
    public static final int LEFT_SQUARE=36;
    public static final int COLON=21;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=34;
    public static final int OctalEscape=32;
    public static final int SALIENCE=9;
    public static final int MULTI_LINE_COMMENT=40;
    public static final int TEST=15;
    public static final int AMPERSAND=18;
    public static final int NAME=6;
    public static final int DECLARE=28;
    public static final int RIGHT_PAREN=8;
    public static final int NOT=13;
    public static final int LEFT_CURLY=38;
    public static final int RIGHT_SQUARE=37;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=35;

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:164:1: opt_semicolon : ( ';' )? ;
    public void opt_semicolon() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:165:4: ( ( ';' )? )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:165:4: ( ';' )?
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:165:4: ( ';' )?
            int alt1=2;
            int LA1_0 = input.LA(1);
            if ( (LA1_0==41) ) {
                alt1=1;
            }
            switch (alt1) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:165:4: ';'
                    {
                    match(input,41,FOLLOW_41_in_opt_semicolon38); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:169:1: compilation_unit : ( statement )+ ;
    public void compilation_unit() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:171:3: ( ( statement )+ )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:171:3: ( statement )+
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:171:3: ( statement )+
            int cnt2=0;
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);
                if ( (LA2_0==LEFT_PAREN) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:171:5: statement
            	    {
            	    pushFollow(FOLLOW_statement_in_compilation_unit56);
            	    statement();
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
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


    // $ANTLR start statement
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:174:1: statement : ( rule ) ;
    public void statement() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:177:2: ( ( rule ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:177:2: ( rule )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:177:2: ( rule )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:177:4: rule
            {
            pushFollow(FOLLOW_rule_in_statement76);
            rule();
            _fsp--;


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


    // $ANTLR start rule
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:214:1: rule returns [RuleDescr rule] : loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs] )* '=>' rhs[rule] RIGHT_PAREN ;
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
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:221:4: (loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs] )* '=>' rhs[rule] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:221:4: loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs] )* '=>' rhs[rule] RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_rule107); 
            match(input,DEFRULE,FOLLOW_DEFRULE_in_rule115); 
            ruleName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_rule119); 
             	  			  		
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
            match(input,STRING,FOLLOW_STRING_in_rule131); 
            
            	    	// do nothing here for now
            		
            pushFollow(FOLLOW_ruleAttribute_in_rule137);
            ruleAttribute(rule);
            _fsp--;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:255:3: ( ce[lhs] )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);
                if ( (LA3_0==LEFT_PAREN||LA3_0==VAR) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:255:3: ce[lhs]
            	    {
            	    pushFollow(FOLLOW_ce_in_rule145);
            	    ce(lhs);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            match(input,42,FOLLOW_42_in_rule154); 
            pushFollow(FOLLOW_rhs_in_rule161);
            rhs(rule);
            _fsp--;

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_rule169); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:265:1: ruleAttribute[RuleDescr rule] : ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? ;
    public void ruleAttribute(RuleDescr rule) throws RecognitionException {
        AttributeDescr d = null;


        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:267:3: ( ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:267:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:267:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( (LA5_0==LEFT_PAREN) ) {
                int LA5_1 = input.LA(2);
                if ( (LA5_1==DECLARE) ) {
                    alt5=1;
                }
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:267:5: LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute186); 
                    match(input,DECLARE,FOLLOW_DECLARE_in_ruleAttribute188); 
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:268:4: ( LEFT_PAREN d= salience RIGHT_PAREN )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);
                    if ( (LA4_0==LEFT_PAREN) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:268:6: LEFT_PAREN d= salience RIGHT_PAREN
                            {
                            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute195); 
                            pushFollow(FOLLOW_salience_in_ruleAttribute199);
                            d=salience();
                            _fsp--;

                             rule.addAttribute( d ); 
                            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute203); 

                            }
                            break;

                    }

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute210); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:272:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:277:3: (loc= SALIENCE i= INT )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:277:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience240); 
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience244); 
            
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:287:1: ce[ConditionalElementDescr in_ce] : ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) ;
    public void ce(ConditionalElementDescr in_ce) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:288:4: ( ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:288:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:288:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )
            int alt6=7;
            int LA6_0 = input.LA(1);
            if ( (LA6_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case NAME:
                    alt6=6;
                    break;
                case EXISTS:
                    alt6=4;
                    break;
                case NOT:
                    alt6=3;
                    break;
                case AND:
                    alt6=1;
                    break;
                case TEST:
                    alt6=5;
                    break;
                case OR:
                    alt6=2;
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("288:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 6, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA6_0==VAR) ) {
                alt6=7;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("288:4: ( and_ce[in_ce] | or_ce[in_ce] | not_ce[in_ce] | exists_ce[in_ce] | eval_ce[in_ce] | normal_pattern[in_ce] | bound_pattern[in_ce] )", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:288:8: and_ce[in_ce]
                    {
                    pushFollow(FOLLOW_and_ce_in_ce270);
                    and_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:289:7: or_ce[in_ce]
                    {
                    pushFollow(FOLLOW_or_ce_in_ce280);
                    or_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:290:7: not_ce[in_ce]
                    {
                    pushFollow(FOLLOW_not_ce_in_ce289);
                    not_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:291:7: exists_ce[in_ce]
                    {
                    pushFollow(FOLLOW_exists_ce_in_ce298);
                    exists_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:292:8: eval_ce[in_ce]
                    {
                    pushFollow(FOLLOW_eval_ce_in_ce312);
                    eval_ce(in_ce);
                    _fsp--;


                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:293:7: normal_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_ce326);
                    normal_pattern(in_ce);
                    _fsp--;


                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:294:7: bound_pattern[in_ce]
                    {
                    pushFollow(FOLLOW_bound_pattern_in_ce335);
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


    // $ANTLR start rhs
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:298:1: rhs[RuleDescr rule] : ( function[context] )* ;
    public void rhs(RuleDescr rule) throws RecognitionException {
        
        	        ExecutionEngine engine = new BlockExecutionEngine();
        			ExecutionBuildContext context = new ExecutionBuildContext( engine );  	
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:305:4: ( ( function[context] )* )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:305:4: ( function[context] )*
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:305:4: ( function[context] )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);
                if ( (LA7_0==LEFT_PAREN) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:305:4: function[context]
            	    {
            	    pushFollow(FOLLOW_function_in_rhs363);
            	    function(context);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop7;
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


    // $ANTLR start and_ce
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:308:1: and_ce[ConditionalElementDescr in_ce] : LEFT_PAREN AND ( ce[andDescr] )+ RIGHT_PAREN ;
    public void and_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                AndDescr andDescr= null;        
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:312:4: ( LEFT_PAREN AND ( ce[andDescr] )+ RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:312:4: LEFT_PAREN AND ( ce[andDescr] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_and_ce392); 
            match(input,AND,FOLLOW_AND_in_and_ce397); 
            
            	    	andDescr = new AndDescr();
            			in_ce.addDescr( andDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:317:3: ( ce[andDescr] )+
            int cnt8=0;
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);
                if ( (LA8_0==LEFT_PAREN||LA8_0==VAR) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:317:3: ce[andDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_and_ce403);
            	    ce(andDescr);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_and_ce412); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:321:1: or_ce[ConditionalElementDescr in_ce] : LEFT_PAREN OR ( ce[orDescr] )+ RIGHT_PAREN ;
    public void or_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                OrDescr orDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:325:4: ( LEFT_PAREN OR ( ce[orDescr] )+ RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:325:4: LEFT_PAREN OR ( ce[orDescr] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_or_ce440); 
            match(input,OR,FOLLOW_OR_in_or_ce445); 
            
            	    	orDescr = new OrDescr();
            			in_ce.addDescr( orDescr );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:330:3: ( ce[orDescr] )+
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
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:330:3: ce[orDescr]
            	    {
            	    pushFollow(FOLLOW_ce_in_or_ce451);
            	    ce(orDescr);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_or_ce460); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:334:1: not_ce[ConditionalElementDescr in_ce] : LEFT_PAREN NOT ce[notDescr] RIGHT_PAREN ;
    public void not_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                NotDescr notDescr= null;         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:338:4: ( LEFT_PAREN NOT ce[notDescr] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:338:4: LEFT_PAREN NOT ce[notDescr] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_not_ce488); 
            match(input,NOT,FOLLOW_NOT_in_not_ce493); 
            
            			notDescr = new NotDescr();
            		    in_ce.addDescr( notDescr );
            		
            pushFollow(FOLLOW_ce_in_not_ce499);
            ce(notDescr);
            _fsp--;

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_not_ce507); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:347:1: exists_ce[ConditionalElementDescr in_ce] : LEFT_PAREN EXISTS ce[existsDescr] RIGHT_PAREN ;
    public void exists_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                ExistsDescr existsDescr= null;        
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:351:4: ( LEFT_PAREN EXISTS ce[existsDescr] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:351:4: LEFT_PAREN EXISTS ce[existsDescr] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_exists_ce536); 
            match(input,EXISTS,FOLLOW_EXISTS_in_exists_ce541); 
            
            		    existsDescr = new ExistsDescr();
            		    in_ce.addDescr( existsDescr );
            		
            pushFollow(FOLLOW_ce_in_exists_ce547);
            ce(existsDescr);
            _fsp--;

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_exists_ce555); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:360:1: eval_ce[ConditionalElementDescr in_ce] : LEFT_PAREN TEST function[context] RIGHT_PAREN ;
    public void eval_ce(ConditionalElementDescr in_ce) throws RecognitionException {
        
                EvalDescr evalDescr= null;    
           		ExecutionEngine engine = new CLPEval();     
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );   		         
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:366:4: ( LEFT_PAREN TEST function[context] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:366:4: LEFT_PAREN TEST function[context] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_eval_ce583); 
            match(input,TEST,FOLLOW_TEST_in_eval_ce588); 
            
            		    evalDescr = new EvalDescr();
            		    in_ce.addDescr( evalDescr );
            		
            pushFollow(FOLLOW_function_in_eval_ce594);
            function(context);
            _fsp--;

            					
            			evalDescr.setContent( engine );			
            		
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_eval_ce605); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:377:1: normal_pattern[ConditionalElementDescr in_ce] : LEFT_PAREN name= NAME ( field_constriant[column] )* RIGHT_PAREN ;
    public void normal_pattern(ConditionalElementDescr in_ce) throws RecognitionException {
        Token name=null;

        
                ColumnDescr column = null;
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:381:4: ( LEFT_PAREN name= NAME ( field_constriant[column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:381:4: LEFT_PAREN name= NAME ( field_constriant[column] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_normal_pattern633); 
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_normal_pattern640); 
            
            			column = new ColumnDescr(name.getText());
            			in_ce.addDescr( column );
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:386:3: ( field_constriant[column] )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);
                if ( (LA10_0==LEFT_PAREN) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:386:3: field_constriant[column]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_normal_pattern646);
            	    field_constriant(column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_normal_pattern656); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:392:1: bound_pattern[ConditionalElementDescr in_ce] : var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[column] )* RIGHT_PAREN ;
    public void bound_pattern(ConditionalElementDescr in_ce) throws RecognitionException {
        Token var=null;
        Token name=null;

        
                ColumnDescr column = null;
                String identifier = null;
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:397:4: (var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:397:4: var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[column] )* RIGHT_PAREN
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_bound_pattern684); 
            
            			identifier = var.getText();
            		
            match(input,ASSIGN_OP,FOLLOW_ASSIGN_OP_in_bound_pattern690); 
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_bound_pattern692); 
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_bound_pattern696); 
            
            			column = new ColumnDescr(name.getText());
            			column.setIdentifier( identifier );
            			in_ce.addDescr( column );	    
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:406:3: ( field_constriant[column] )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);
                if ( (LA11_0==LEFT_PAREN) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:406:3: field_constriant[column]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_bound_pattern705);
            	    field_constriant(column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_bound_pattern712); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:410:1: field_constriant[ColumnDescr column] : LEFT_PAREN f= NAME connected_constraint[fc, column] RIGHT_PAREN ;
    public void field_constriant(ColumnDescr column) throws RecognitionException {
        Token f=null;

        
             	List list = new ArrayList();
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		String op = "==";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:418:3: ( LEFT_PAREN f= NAME connected_constraint[fc, column] RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:418:3: LEFT_PAREN f= NAME connected_constraint[fc, column] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_field_constriant743); 
            f=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_field_constriant747); 
            
            			fc = new FieldConstraintDescr(f.getText());
            			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
            			column.addDescr( fc );			
            		
            pushFollow(FOLLOW_connected_constraint_in_field_constriant762);
            connected_constraint(fc,  column);
            _fsp--;

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_field_constriant768); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:430:1: connected_constraint[FieldConstraintDescr fc, ColumnDescr column] : restriction[fc, column] ( AMPERSAND connected_constraint[fc, column] | PIPE connected_constraint[fc, column] )? ;
    public void connected_constraint(FieldConstraintDescr fc, ColumnDescr column) throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:432:2: ( restriction[fc, column] ( AMPERSAND connected_constraint[fc, column] | PIPE connected_constraint[fc, column] )? )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:432:2: restriction[fc, column] ( AMPERSAND connected_constraint[fc, column] | PIPE connected_constraint[fc, column] )?
            {
            pushFollow(FOLLOW_restriction_in_connected_constraint784);
            restriction(fc,  column);
            _fsp--;

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:433:2: ( AMPERSAND connected_constraint[fc, column] | PIPE connected_constraint[fc, column] )?
            int alt12=3;
            int LA12_0 = input.LA(1);
            if ( (LA12_0==AMPERSAND) ) {
                alt12=1;
            }
            else if ( (LA12_0==PIPE) ) {
                alt12=2;
            }
            switch (alt12) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:434:6: AMPERSAND connected_constraint[fc, column]
                    {
                    match(input,AMPERSAND,FOLLOW_AMPERSAND_in_connected_constraint796); 
                     fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND)); 
                    pushFollow(FOLLOW_connected_constraint_in_connected_constraint805);
                    connected_constraint(fc,  column);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:437:6: PIPE connected_constraint[fc, column]
                    {
                    match(input,PIPE,FOLLOW_PIPE_in_connected_constraint817); 
                    fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR)); 
                    pushFollow(FOLLOW_connected_constraint_in_connected_constraint826);
                    connected_constraint(fc,  column);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:442:1: restriction[FieldConstraintDescr fc, ColumnDescr column] : ( TILDE )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) ;
    public void restriction(FieldConstraintDescr fc, ColumnDescr column) throws RecognitionException {
        String lc = null;


        
        			String op = "==";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:446:4: ( ( TILDE )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:446:4: ( TILDE )? ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:446:4: ( TILDE )?
            int alt13=2;
            int LA13_0 = input.LA(1);
            if ( (LA13_0==TILDE) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:446:5: TILDE
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_restriction852); 
                    op = "!=";

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:447:3: ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )
            int alt14=4;
            switch ( input.LA(1) ) {
            case COLON:
                alt14=1;
                break;
            case EQUALS:
                alt14=2;
                break;
            case VAR:
                alt14=3;
                break;
            case NAME:
            case STRING:
            case INT:
            case FLOAT:
            case BOOL:
            case NULL:
                alt14=4;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("447:3: ( predicate_constraint[op, column] | return_value_restriction[op, fc] | variable_restriction[op, fc] | lc= literal_restriction )", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:447:5: predicate_constraint[op, column]
                    {
                    pushFollow(FOLLOW_predicate_constraint_in_restriction868);
                    predicate_constraint(op,  column);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:448:7: return_value_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_return_value_restriction_in_restriction884);
                    return_value_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:449:7: variable_restriction[op, fc]
                    {
                    pushFollow(FOLLOW_variable_restriction_in_restriction893);
                    variable_restriction(op,  fc);
                    _fsp--;


                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:450:8: lc= literal_restriction
                    {
                    pushFollow(FOLLOW_literal_restriction_in_restriction905);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:457:1: predicate_constraint[String op, ColumnDescr column] : COLON function[context] ;
    public void predicate_constraint(String op, ColumnDescr column) throws RecognitionException {
        
           		ExecutionEngine engine = new CLPPredicate();
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );    
            
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:462:4: ( COLON function[context] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:462:4: COLON function[context]
            {
            match(input,COLON,FOLLOW_COLON_in_predicate_constraint947); 
            pushFollow(FOLLOW_function_in_predicate_constraint951);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:469:1: return_value_restriction[String op, FieldConstraintDescr fc] : EQUALS function[context] ;
    public void return_value_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {
        
        		ExecutionEngine engine = new CLPReturnValue();
        		ExecutionBuildContext context = new ExecutionBuildContext( engine );
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:474:4: ( EQUALS function[context] )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:474:4: EQUALS function[context]
            {
            match(input,EQUALS,FOLLOW_EQUALS_in_return_value_restriction974); 
            pushFollow(FOLLOW_function_in_return_value_restriction979);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:480:1: variable_restriction[String op, FieldConstraintDescr fc] : var= VAR ;
    public void variable_restriction(String op, FieldConstraintDescr fc) throws RecognitionException {
        Token var=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:481:4: (var= VAR )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:481:4: var= VAR
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_variable_restriction1000); 
            
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:487:1: literal_restriction returns [String text] : t= literal ;
    public String literal_restriction() throws RecognitionException {
        String text = null;

        String t = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:492:6: (t= literal )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:492:6: t= literal
            {
            pushFollow(FOLLOW_literal_in_literal_restriction1033);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:497:1: function[ExecutionBuildContext context] returns [Function f] : LEFT_PAREN name= NAME ( function_params[context, f] )+ RIGHT_PAREN ;
    public Function function(ExecutionBuildContext context) throws RecognitionException {
        Function f = null;

        Token name=null;

        
        	    FunctionFactory factory = FunctionFactory.getInstance();
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:501:4: ( LEFT_PAREN name= NAME ( function_params[context, f] )+ RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:501:4: LEFT_PAREN name= NAME ( function_params[context, f] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_function1058); 
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_function1064); 
            
            			if ( name.getText().equals("bind") ) {
            		  		context.createLocalVariable( name.getText() );
            			}
            		  	f = factory.createFunction( name.getText() );		  
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:509:3: ( function_params[context, f] )+
            int cnt15=0;
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);
                if ( (LA15_0==LEFT_PAREN||(LA15_0>=NAME && LA15_0<=STRING)||LA15_0==INT||LA15_0==VAR||(LA15_0>=FLOAT && LA15_0<=NULL)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:509:3: function_params[context, f]
            	    {
            	    pushFollow(FOLLOW_function_params_in_function1077);
            	    function_params(context,  f);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    if ( cnt15 >= 1 ) break loop15;
                        EarlyExitException eee =
                            new EarlyExitException(15, input);
                        throw eee;
                }
                cnt15++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_function1087); 
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:515:1: modify_function[ExecutionBuildContext context] returns [Function f] : LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN ;
    public Function modify_function(ExecutionBuildContext context) throws RecognitionException {
        Function f = null;

        
        	    FunctionFactory factory = FunctionFactory.getInstance();
        		f = factory.createFunction( "modify" );
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:521:3: ( LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:521:3: LEFT_PAREN 'modify' ( slot_name_value_pair[context, f] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_modify_function1118); 
            match(input,43,FOLLOW_43_in_modify_function1123); 
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:523:4: ( slot_name_value_pair[context, f] )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);
                if ( (LA16_0==LEFT_PAREN) ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:523:4: slot_name_value_pair[context, f]
            	    {
            	    pushFollow(FOLLOW_slot_name_value_pair_in_modify_function1128);
            	    slot_name_value_pair(context,  f);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_modify_function1134); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:527:1: function_params[ExecutionBuildContext context, Function f] : (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) ;
    public void function_params(ExecutionBuildContext context, Function f) throws RecognitionException {
        Token t=null;
        Function nf = null;


        
        		ValueHandler value  =  null;		
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:532:3: ( (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:532:3: (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:532:3: (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            int alt17=8;
            switch ( input.LA(1) ) {
            case VAR:
                alt17=1;
                break;
            case STRING:
                alt17=2;
                break;
            case NAME:
                alt17=3;
                break;
            case FLOAT:
                alt17=4;
                break;
            case INT:
                alt17=5;
                break;
            case BOOL:
                alt17=6;
                break;
            case NULL:
                alt17=7;
                break;
            case LEFT_PAREN:
                alt17=8;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("532:3: (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:532:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_function_params1163); 
                     value = context.getVariableValueHandler(t.getText() ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:533:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_function_params1175); 
                     value = new ObjectLiteralValue( getString( t ) ); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:534:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_function_params1190); 
                     value = new ObjectLiteralValue( t.getText() ); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:535:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_function_params1207); 
                     value = new DoubleLiteralValue( t.getText() ); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:536:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_function_params1222); 
                     value = new LongLiteralValue( t.getText() ); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:537:6: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_function_params1242); 
                     value = new BooleanLiteralValue( t.getText() ); 

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:538:6: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_function_params1264); 
                     value = ObjectLiteralValue.NULL; 

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:539:6: nf= function[context]
                    {
                    pushFollow(FOLLOW_function_in_function_params1280);
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:545:1: slot_name_value_pair[ExecutionBuildContext context, Function f] : LEFT_PAREN id= NAME (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN ;
    public void slot_name_value_pair(ExecutionBuildContext context, Function f) throws RecognitionException {
        Token id=null;
        Token t=null;
        Function nf = null;


        
        		SlotNameValuePair nameValuePair = null;
        		String name = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:551:3: ( LEFT_PAREN id= NAME (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:551:3: LEFT_PAREN id= NAME (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] ) RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_slot_name_value_pair1322); 
            id=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_slot_name_value_pair1328); 
            
            			name = id.getText();
            		
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:555:3: (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )
            int alt18=8;
            switch ( input.LA(1) ) {
            case VAR:
                alt18=1;
                break;
            case STRING:
                alt18=2;
                break;
            case NAME:
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
                    new NoViableAltException("555:3: (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL | nf= function[context] )", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:555:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_slot_name_value_pair1339); 
                     nameValuePair = new SlotNameValuePair(name, context.getVariableValueHandler( t.getText() ) ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:556:7: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_slot_name_value_pair1357); 
                     nameValuePair = new SlotNameValuePair(name, new ObjectLiteralValue( getString( t ) ) ); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:557:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_slot_name_value_pair1372); 
                     nameValuePair = new SlotNameValuePair(name, new ObjectLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:558:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_slot_name_value_pair1389); 
                     nameValuePair = new SlotNameValuePair(name, new DoubleLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:559:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_slot_name_value_pair1404); 
                     nameValuePair = new SlotNameValuePair(name, new LongLiteralValue( t.getText() ) ); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:560:6: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_slot_name_value_pair1424); 
                     nameValuePair = new SlotNameValuePair(name, new BooleanLiteralValue( t.getText() ) ) ; 

                    }
                    break;
                case 7 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:561:6: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_slot_name_value_pair1446); 
                     nameValuePair = new SlotNameValuePair(name, ObjectLiteralValue.NULL ); 

                    }
                    break;
                case 8 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:562:6: nf= function[context]
                    {
                    pushFollow(FOLLOW_function_in_slot_name_value_pair1462);
                    nf=function(context);
                    _fsp--;

                     nameValuePair = new SlotNameValuePair(name, nf ); 

                    }
                    break;

            }

             f.addParameter( nameValuePair ); 
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_slot_name_value_pair1499); 

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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:569:1: literal returns [String text] : (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public String literal() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:573:4: ( (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:573:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:573:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt19=6;
            switch ( input.LA(1) ) {
            case STRING:
                alt19=1;
                break;
            case NAME:
                alt19=2;
                break;
            case INT:
                alt19=3;
                break;
            case FLOAT:
                alt19=4;
                break;
            case BOOL:
                alt19=5;
                break;
            case NULL:
                alt19=6;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("573:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:573:8: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal1528); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:574:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_literal1541); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:7: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal1557); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:576:7: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal1572); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:577:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal1585); 
                     text = t.getText(); 

                    }
                    break;
                case 6 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:578:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal1599); 
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


 

    public static final BitSet FOLLOW_41_in_opt_semicolon38 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_compilation_unit56 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_rule_in_statement76 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_rule107 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DEFRULE_in_rule115 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_rule119 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_STRING_in_rule131 = new BitSet(new long[]{0x0000040000010010L});
    public static final BitSet FOLLOW_ruleAttribute_in_rule137 = new BitSet(new long[]{0x0000040000010010L});
    public static final BitSet FOLLOW_ce_in_rule145 = new BitSet(new long[]{0x0000040000010010L});
    public static final BitSet FOLLOW_42_in_rule154 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_rhs_in_rule161 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_rule169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute186 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_DECLARE_in_ruleAttribute188 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute195 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_salience_in_ruleAttribute199 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute203 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience240 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_INT_in_salience244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_ce270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_ce280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_ce289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_ce298 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_ce312 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_ce326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bound_pattern_in_ce335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_rhs363 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_and_ce392 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_AND_in_and_ce397 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_ce_in_and_ce403 = new BitSet(new long[]{0x0000000000010110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_and_ce412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_or_ce440 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_OR_in_or_ce445 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_ce_in_or_ce451 = new BitSet(new long[]{0x0000000000010110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_or_ce460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_not_ce488 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_NOT_in_not_ce493 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_ce_in_not_ce499 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_not_ce507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_exists_ce536 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EXISTS_in_exists_ce541 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_ce_in_exists_ce547 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_exists_ce555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_eval_ce583 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_TEST_in_eval_ce588 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_eval_ce594 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_eval_ce605 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_normal_pattern633 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_normal_pattern640 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_field_constriant_in_normal_pattern646 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_normal_pattern656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_bound_pattern684 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_ASSIGN_OP_in_bound_pattern690 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_bound_pattern692 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_bound_pattern696 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_field_constriant_in_bound_pattern705 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_bound_pattern712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_field_constriant743 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_field_constriant747 = new BitSet(new long[]{0x0000000003F104C0L});
    public static final BitSet FOLLOW_connected_constraint_in_field_constriant762 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_field_constriant768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_restriction_in_connected_constraint784 = new BitSet(new long[]{0x00000000000C0002L});
    public static final BitSet FOLLOW_AMPERSAND_in_connected_constraint796 = new BitSet(new long[]{0x0000000003F104C0L});
    public static final BitSet FOLLOW_connected_constraint_in_connected_constraint805 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PIPE_in_connected_constraint817 = new BitSet(new long[]{0x0000000003F104C0L});
    public static final BitSet FOLLOW_connected_constraint_in_connected_constraint826 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_restriction852 = new BitSet(new long[]{0x0000000003E104C0L});
    public static final BitSet FOLLOW_predicate_constraint_in_restriction868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_return_value_restriction_in_restriction884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_restriction_in_restriction893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_restriction_in_restriction905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_predicate_constraint947 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_predicate_constraint951 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_return_value_restriction974 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_function_in_return_value_restriction979 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_variable_restriction1000 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_literal_restriction1033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_function1058 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_function1064 = new BitSet(new long[]{0x00000000038104D0L});
    public static final BitSet FOLLOW_function_params_in_function1077 = new BitSet(new long[]{0x00000000038105D0L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_function1087 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_modify_function1118 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_modify_function1123 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_slot_name_value_pair_in_modify_function1128 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_modify_function1134 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_function_params1163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_function_params1175 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_function_params1190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_function_params1207 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_function_params1222 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_function_params1242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_function_params1264 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_function_params1280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_slot_name_value_pair1322 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_NAME_in_slot_name_value_pair1328 = new BitSet(new long[]{0x00000000038104D0L});
    public static final BitSet FOLLOW_VAR_in_slot_name_value_pair1339 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_slot_name_value_pair1357 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_NAME_in_slot_name_value_pair1372 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_FLOAT_in_slot_name_value_pair1389 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_INT_in_slot_name_value_pair1404 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_BOOL_in_slot_name_value_pair1424 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_NULL_in_slot_name_value_pair1446 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_function_in_slot_name_value_pair1462 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_slot_name_value_pair1499 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal1528 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_literal1541 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal1557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal1572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal1585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal1599 = new BitSet(new long[]{0x0000000000000002L});

}