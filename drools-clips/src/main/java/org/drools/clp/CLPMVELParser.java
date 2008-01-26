// $ANTLR 3.0.1 C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g 2008-01-26 17:55:37

	package org.drools.clp;
	
    import org.drools.clp.mvel.*;	
    
	import org.drools.clp.valuehandlers.*;
	import java.util.List;
	import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.HashMap;	
	import java.util.Set;	
	import java.util.HashSet;			
	import java.util.StringTokenizer;
	import org.drools.lang.descr.*;
	import org.drools.lang.Location;	


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class CLPMVELParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LEFT_PAREN", "NAME", "RIGHT_PAREN", "DEFRULE", "STRING", "SALIENCE", "INT", "AND", "OR", "NOT", "EXISTS", "TEST", "VAR", "ASSIGN_OP", "PIPE", "AMPERSAND", "TILDE", "COLON", "EQUALS", "FLOAT", "BOOL", "NULL", "EOL", "WS", "DECLARE", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SYMBOL_CHAR", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "MULTI_LINE_COMMENT", "SYMBOL", "FIRST_SYMBOL_CHAR", "'import'", "'=>'"
    };
    public static final int RIGHT_SQUARE=37;
    public static final int RIGHT_CURLY=39;
    public static final int SYMBOL=41;
    public static final int NULL=25;
    public static final int BOOL=24;
    public static final int SALIENCE=9;
    public static final int AMPERSAND=19;
    public static final int FLOAT=23;
    public static final int EQUALS=22;
    public static final int INT=10;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=34;
    public static final int SYMBOL_CHAR=33;
    public static final int NOT=13;
    public static final int AND=11;
    public static final int FIRST_SYMBOL_CHAR=42;
    public static final int EOF=-1;
    public static final int HexDigit=30;
    public static final int MULTI_LINE_COMMENT=40;
    public static final int COLON=21;
    public static final int ASSIGN_OP=17;
    public static final int RIGHT_PAREN=6;
    public static final int NAME=5;
    public static final int WS=27;
    public static final int EOL=26;
    public static final int UnicodeEscape=31;
    public static final int LEFT_CURLY=38;
    public static final int DEFRULE=7;
    public static final int OR=12;
    public static final int TILDE=20;
    public static final int TEST=15;
    public static final int LEFT_PAREN=4;
    public static final int DECLARE=28;
    public static final int PIPE=18;
    public static final int VAR=16;
    public static final int EXISTS=14;
    public static final int LEFT_SQUARE=36;
    public static final int OctalEscape=32;
    public static final int EscapeSequence=29;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=35;
    public static final int STRING=8;

        public CLPMVELParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[26+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g"; }

    
    	private PackageDescr packageDescr;
    	private List errors = new ArrayList();
    	private String source = "unknown";
    	private int lineOffset = 0;
    	private DescrFactory factory = new DescrFactory();
    	private boolean parserDebug = false;
    	private FunctionRegistry functionRegistry;	
    	private Location location = new Location( Location.LOCATION_UNKNOWN );	
    	
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
          



    // $ANTLR start eval_script
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:228:1: eval_script[Shell shell] : ;
    public final void eval_script(Shell  shell) throws RecognitionException {
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:229:2: ()
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:234:2: 
            {
            }

        }
        finally {
        }
        return ;
    }
    // $ANTLR end eval_script


    // $ANTLR start importDescr
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:236:1: importDescr returns [ImportDescr importDescr] : LEFT_PAREN 'import' importName= NAME RIGHT_PAREN ;
    public final ImportDescr importDescr() throws RecognitionException {
        ImportDescr importDescr = null;

        Token importName=null;

        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:237:2: ( LEFT_PAREN 'import' importName= NAME RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:237:4: LEFT_PAREN 'import' importName= NAME RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_importDescr63); if (failed) return importDescr;
            match(input,43,FOLLOW_43_in_importDescr65); if (failed) return importDescr;
            importName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_importDescr69); if (failed) return importDescr;
            if ( backtracking==0 ) {
               importDescr = new ImportDescr( importName.getText() ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_importDescr72); if (failed) return importDescr;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return importDescr;
    }
    // $ANTLR end importDescr


    // $ANTLR start defrule
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:284:1: defrule returns [RuleDescr rule] : loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' t= lisp_list RIGHT_PAREN ;
    public final RuleDescr defrule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        Token ruleName=null;
        Token documentation=null;
        SExpression t = null;


         
        	        rule = null; 
        	        AndDescr lhs = null;
        	        PatternDescr colum = null;
        	        AttributeDescr module = null;	      
                    Set declarations = null;  
        	      
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:292:2: (loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' t= lisp_list RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:292:4: loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' t= lisp_list RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_defrule106); if (failed) return rule;
            match(input,DEFRULE,FOLLOW_DEFRULE_in_defrule114); if (failed) return rule;
            ruleName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_defrule118); if (failed) return rule;
            if ( backtracking==0 ) {
               	  			  		
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
              			
              			rule.addAttribute( new AttributeDescr( "dialect", "clips") );	
              			
              			declarations = new HashSet();  											
              		
            }
            documentation=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_defrule130); if (failed) return rule;
            if ( backtracking==0 ) {
              
              	    	// do nothing here for now
              		
            }
            pushFollow(FOLLOW_ruleAttribute_in_defrule136);
            ruleAttribute(rule);
            _fsp--;
            if (failed) return rule;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:331:3: ( ce[lhs, declarations] )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==LEFT_PAREN||LA1_0==VAR) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:331:3: ce[lhs, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_defrule144);
            	    ce(lhs,  declarations);
            	    _fsp--;
            	    if (failed) return rule;

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            match(input,44,FOLLOW_44_in_defrule153); if (failed) return rule;
            pushFollow(FOLLOW_lisp_list_in_defrule162);
            t=lisp_list();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               rule.setConsequence( t ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_defrule171); if (failed) return rule;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:341:1: ruleAttribute[RuleDescr rule] : ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? ;
    public final void ruleAttribute(RuleDescr rule) throws RecognitionException {
        AttributeDescr d = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:342:2: ( ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:343:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:343:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
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
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:343:5: LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute188); if (failed) return ;
                    match(input,DECLARE,FOLLOW_DECLARE_in_ruleAttribute190); if (failed) return ;
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:344:4: ( LEFT_PAREN d= salience RIGHT_PAREN )?
                    int alt2=2;
                    int LA2_0 = input.LA(1);

                    if ( (LA2_0==LEFT_PAREN) ) {
                        alt2=1;
                    }
                    switch (alt2) {
                        case 1 :
                            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:344:6: LEFT_PAREN d= salience RIGHT_PAREN
                            {
                            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute197); if (failed) return ;
                            pushFollow(FOLLOW_salience_in_ruleAttribute201);
                            d=salience();
                            _fsp--;
                            if (failed) return ;
                            if ( backtracking==0 ) {
                               rule.addAttribute( d ); 
                            }
                            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute205); if (failed) return ;

                            }
                            break;

                    }

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute212); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:348:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:352:2: (loc= SALIENCE i= INT )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:353:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience242); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience246); if (failed) return d;
            if ( backtracking==0 ) {
              
              			d = new AttributeDescr( "salience", i.getText() );
              			d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
              			d.setEndCharacter( ((CommonToken)i).getStopIndex() );
              		
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
    // $ANTLR end salience


    // $ANTLR start ce
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:363:1: ce[ConditionalElementDescr in_ce, Set declarations] : ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] ) ;
    public final void ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:364:2: ( ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:364:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:364:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )
            int alt4=7;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case NOT:
                    {
                    alt4=3;
                    }
                    break;
                case OR:
                    {
                    alt4=2;
                    }
                    break;
                case TEST:
                    {
                    alt4=5;
                    }
                    break;
                case NAME:
                    {
                    alt4=6;
                    }
                    break;
                case EXISTS:
                    {
                    alt4=4;
                    }
                    break;
                case AND:
                    {
                    alt4=1;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("364:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )", 4, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA4_0==VAR) ) {
                alt4=7;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("364:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:364:8: and_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_and_ce_in_ce272);
                    and_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:365:7: or_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_or_ce_in_ce282);
                    or_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:366:7: not_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_not_ce_in_ce291);
                    not_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:367:7: exists_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_exists_ce_in_ce300);
                    exists_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:368:8: eval_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_eval_ce_in_ce314);
                    eval_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:369:7: normal_pattern[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_ce328);
                    normal_pattern(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 7 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:370:7: bound_pattern[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_bound_pattern_in_ce337);
                    bound_pattern(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:374:1: and_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN ;
    public final void and_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                AndDescr andDescr= null;        
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:378:2: ( LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:378:4: LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_and_ce364); if (failed) return ;
            match(input,AND,FOLLOW_AND_in_and_ce369); if (failed) return ;
            if ( backtracking==0 ) {
              
              	    	andDescr = new AndDescr();
              			in_ce.addDescr( andDescr );
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:383:3: ( ce[andDescr, declarations] )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==LEFT_PAREN||LA5_0==VAR) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:383:3: ce[andDescr, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_and_ce375);
            	    ce(andDescr,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_and_ce384); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:387:1: or_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN ;
    public final void or_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                OrDescr orDescr= null;         
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:391:2: ( LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:391:4: LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_or_ce412); if (failed) return ;
            match(input,OR,FOLLOW_OR_in_or_ce417); if (failed) return ;
            if ( backtracking==0 ) {
              
              	    	orDescr = new OrDescr();
              			in_ce.addDescr( orDescr );
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:396:3: ( ce[orDescr, declarations] )+
            int cnt6=0;
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==LEFT_PAREN||LA6_0==VAR) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:396:3: ce[orDescr, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_or_ce423);
            	    ce(orDescr,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt6 >= 1 ) break loop6;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(6, input);
                        throw eee;
                }
                cnt6++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_or_ce432); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:400:1: not_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN ;
    public final void not_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                NotDescr notDescr= null;         
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:404:2: ( LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:404:4: LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_not_ce460); if (failed) return ;
            match(input,NOT,FOLLOW_NOT_in_not_ce465); if (failed) return ;
            if ( backtracking==0 ) {
              
              			notDescr = new NotDescr();
              		    in_ce.addDescr( notDescr );
              		
            }
            pushFollow(FOLLOW_ce_in_not_ce471);
            ce(notDescr,  declarations);
            _fsp--;
            if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_not_ce479); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:413:1: exists_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN ;
    public final void exists_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                ExistsDescr existsDescr= null;        
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:417:2: ( LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:417:4: LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_exists_ce508); if (failed) return ;
            match(input,EXISTS,FOLLOW_EXISTS_in_exists_ce513); if (failed) return ;
            if ( backtracking==0 ) {
              
              		    existsDescr = new ExistsDescr();
              		    in_ce.addDescr( existsDescr );
              		
            }
            pushFollow(FOLLOW_ce_in_exists_ce519);
            ce(existsDescr,  declarations);
            _fsp--;
            if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_exists_ce527); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:426:1: eval_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN TEST t= lisp_list RIGHT_PAREN ;
    public final void eval_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        SExpression t = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:427:2: ( LEFT_PAREN TEST t= lisp_list RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:427:4: LEFT_PAREN TEST t= lisp_list RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_eval_ce546); if (failed) return ;
            match(input,TEST,FOLLOW_TEST_in_eval_ce551); if (failed) return ;
            pushFollow(FOLLOW_lisp_list_in_eval_ce558);
            t=lisp_list();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               EvalDescr evalDescr = new EvalDescr(); evalDescr.setContent( t ); in_ce.addDescr( evalDescr ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_eval_ce568); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:433:1: normal_pattern[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN ;
    public final void normal_pattern(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        Token name=null;

        
                PatternDescr pattern = null;
                ConditionalElementDescr top = null;
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:438:2: ( LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:438:4: LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_normal_pattern596); if (failed) return ;
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_normal_pattern603); if (failed) return ;
            if ( backtracking==0 ) {
              
              			pattern = new PatternDescr(name.getText());
              			in_ce.addDescr( pattern );
              			top = pattern.getConstraint();
              			
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:445:3: ( field_constriant[top, declarations] )*
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==LEFT_PAREN) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:445:3: field_constriant[top, declarations]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_normal_pattern609);
            	    field_constriant(top,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop7;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_normal_pattern619); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:451:1: bound_pattern[ConditionalElementDescr in_ce, Set declarations] : var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN ;
    public final void bound_pattern(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        Token var=null;
        Token name=null;

        
                PatternDescr pattern = null;
                String identifier = null;
                ConditionalElementDescr top = null;        
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:457:2: (var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:457:4: var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_bound_pattern647); if (failed) return ;
            if ( backtracking==0 ) {
              
              			identifier = var.getText();
              		
            }
            match(input,ASSIGN_OP,FOLLOW_ASSIGN_OP_in_bound_pattern653); if (failed) return ;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_bound_pattern655); if (failed) return ;
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_bound_pattern659); if (failed) return ;
            if ( backtracking==0 ) {
              
              			pattern = new PatternDescr(name.getText());
              			pattern.setIdentifier( identifier );
              			in_ce.addDescr( pattern );
              			top = pattern.getConstraint();				    
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:467:3: ( field_constriant[top, declarations] )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==LEFT_PAREN) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:467:3: field_constriant[top, declarations]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_bound_pattern668);
            	    field_constriant(top,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_bound_pattern675); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:471:1: field_constriant[ConditionalElementDescr base, Set declarations] : LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN ;
    public final void field_constriant(ConditionalElementDescr base, Set declarations) throws RecognitionException {
        Token f=null;

        
             	List list = new ArrayList();
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		RestrictionConnectiveDescr top = null;		
        		String op = "==";
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:479:2: ( LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:480:3: LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_field_constriant706); if (failed) return ;
            f=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_field_constriant710); if (failed) return ;
            if ( backtracking==0 ) {
              
              			fc = new FieldConstraintDescr(f.getText());
              			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
              			base.addDescr( fc );	
              			top = fc.getRestriction();		
              		
            }
            pushFollow(FOLLOW_or_restr_connective_in_field_constriant725);
            or_restr_connective(top,  base,  fc,  declarations);
            _fsp--;
            if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_field_constriant731); if (failed) return ;

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


    // $ANTLR start or_restr_connective
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:507:1: or_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] options {backtrack=true; } : and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )* ;
    public final void or_restr_connective(RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        
        		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:514:2: ( and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )* )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:515:3: and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )*
            {
            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective770);
            and_restr_connective(or,  ceBase,  fcBase,  declarations);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:516:3: ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==PIPE) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:518:6: PIPE and_restr_connective[or, ceBase, fcBase, declarations]
            	    {
            	    match(input,PIPE,FOLLOW_PIPE_in_or_restr_connective794); if (failed) return ;
            	    if ( backtracking==0 ) {
            	      
            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective804);
            	    and_restr_connective(or,  ceBase,  fcBase,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            
            	        if( or.getRestrictions().size() == 1 ) {
            	                rcBase.addOrMerge( (RestrictionDescr) or.getRestrictions().get( 0 ) );
            	        } else if ( or.getRestrictions().size() > 1 ) {
            	        	rcBase.addRestriction( or );
            	        }
            	
        }
        return ;
    }
    // $ANTLR end or_restr_connective


    // $ANTLR start and_restr_connective
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:533:1: and_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] : restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )* ;
    public final void and_restr_connective(RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        
        		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:537:2: ( restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )* )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:538:3: restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )*
            {
            pushFollow(FOLLOW_restriction_in_and_restr_connective836);
            restriction(and,  ceBase,  fcBase,  declarations);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:539:3: ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==AMPERSAND) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:539:5: AMPERSAND restriction[and, ceBase, fcBase, declarations]
            	    {
            	    match(input,AMPERSAND,FOLLOW_AMPERSAND_in_and_restr_connective844); if (failed) return ;
            	    pushFollow(FOLLOW_restriction_in_and_restr_connective846);
            	    restriction(and,  ceBase,  fcBase,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
            
            	        if( and.getRestrictions().size() == 1) {
            	                rcBase.addOrMerge( (RestrictionDescr) and.getRestrictions().get( 0 ) );
            	        } else if ( and.getRestrictions().size() > 1 ) {
            	        	rcBase.addRestriction( and );
            	        }
            	
        }
        return ;
    }
    // $ANTLR end and_restr_connective


    // $ANTLR start restriction
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:558:1: restriction[RestrictionConnectiveDescr rc, ConditionalElementDescr base, FieldConstraintDescr fcBase, Set declarations ] : ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction ) ;
    public final void restriction(RestrictionConnectiveDescr rc, ConditionalElementDescr base, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        String lc = null;


        
        			String op = "==";
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:562:2: ( ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:562:4: ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:562:4: ( TILDE )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==TILDE) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:562:5: TILDE
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_restriction879); if (failed) return ;
                    if ( backtracking==0 ) {
                      op = "!=";
                    }

                    }
                    break;

            }

            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:563:3: ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )
            int alt12=4;
            switch ( input.LA(1) ) {
            case COLON:
                {
                alt12=1;
                }
                break;
            case EQUALS:
                {
                alt12=2;
                }
                break;
            case VAR:
                {
                alt12=3;
                }
                break;
            case NAME:
            case STRING:
            case INT:
            case FLOAT:
            case BOOL:
            case NULL:
                {
                alt12=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("563:3: ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:563:5: predicate_constraint[rc, op, base]
                    {
                    pushFollow(FOLLOW_predicate_constraint_in_restriction895);
                    predicate_constraint(rc,  op,  base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:564:7: return_value_restriction[op, rc]
                    {
                    pushFollow(FOLLOW_return_value_restriction_in_restriction911);
                    return_value_restriction(op,  rc);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:565:7: variable_restriction[op, rc, base, fcBase, declarations]
                    {
                    pushFollow(FOLLOW_variable_restriction_in_restriction920);
                    variable_restriction(op,  rc,  base,  fcBase,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:566:8: lc= literal_restriction
                    {
                    pushFollow(FOLLOW_literal_restriction_in_restriction932);
                    lc=literal_restriction();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                      
                           	    			rc.addRestriction( new LiteralRestrictionDescr(op, lc) );
                      		      		op = "==";
                      		        
                    }

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:573:1: predicate_constraint[RestrictionConnectiveDescr rc, String op, ConditionalElementDescr base] : COLON t= lisp_list ;
    public final void predicate_constraint(RestrictionConnectiveDescr rc, String op, ConditionalElementDescr base) throws RecognitionException {
        SExpression t = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:574:2: ( COLON t= lisp_list )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:574:4: COLON t= lisp_list
            {
            match(input,COLON,FOLLOW_COLON_in_predicate_constraint965); if (failed) return ;
            pushFollow(FOLLOW_lisp_list_in_predicate_constraint971);
            t=lisp_list();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               rc.addRestriction( new PredicateDescr( t ) ); 
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
    // $ANTLR end predicate_constraint


    // $ANTLR start return_value_restriction
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:580:1: return_value_restriction[String op, RestrictionConnectiveDescr rc] : EQUALS t= lisp_list ;
    public final void return_value_restriction(String op, RestrictionConnectiveDescr rc) throws RecognitionException {
        SExpression t = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:581:2: ( EQUALS t= lisp_list )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:581:4: EQUALS t= lisp_list
            {
            match(input,EQUALS,FOLLOW_EQUALS_in_return_value_restriction990); if (failed) return ;
            pushFollow(FOLLOW_lisp_list_in_return_value_restriction997);
            t=lisp_list();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              rc.addRestriction( new ReturnValueRestrictionDescr (op, t ) ); 
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
    // $ANTLR end return_value_restriction


    // $ANTLR start variable_restriction
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:586:1: variable_restriction[String op, RestrictionConnectiveDescr rc, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] : VAR ;
    public final void variable_restriction(String op, RestrictionConnectiveDescr rc, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        Token VAR1=null;

        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:587:2: ( VAR )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:587:4: VAR
            {
            VAR1=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_variable_restriction1016); if (failed) return ;
            if ( backtracking==0 ) {
              
              	        if ( declarations.contains( VAR1.getText() ) ) {
              				rc.addRestriction( new VariableRestrictionDescr(op, VAR1.getText()) );
              		 	} else {
              		 		FieldBindingDescr fbd = new FieldBindingDescr();
              		 		fbd.setIdentifier( VAR1.getText() );		
              		 		fbd.setFieldName( fcBase.getFieldName() ); 		
              		 		ceBase.insertBeforeLast( FieldConstraintDescr.class, fbd );
              		 		declarations.add( VAR1.getText() );
              		 	}
              		
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
    // $ANTLR end variable_restriction


    // $ANTLR start literal_restriction
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:601:1: literal_restriction returns [String text] : t= literal ;
    public final String literal_restriction() throws RecognitionException {
        String text = null;

        String t = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:605:2: (t= literal )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:606:6: t= literal
            {
            pushFollow(FOLLOW_literal_in_literal_restriction1049);
            t=literal();
            _fsp--;
            if (failed) return text;
            if ( backtracking==0 ) {
              
              	    	text = t;
              	    
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
    // $ANTLR end literal_restriction


    // $ANTLR start eval_sExpressions
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:612:1: eval_sExpressions returns [List<SExpression> list] : (a= lisp_list )* ;
    public final List<SExpression> eval_sExpressions() throws RecognitionException {
        List<SExpression> list = null;

        SExpression a = null;


        
        		list = new ArrayList<SExpression>();
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:616:2: ( (a= lisp_list )* )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:617:3: (a= lisp_list )*
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:617:3: (a= lisp_list )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==LEFT_PAREN) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:617:4: a= lisp_list
            	    {
            	    pushFollow(FOLLOW_lisp_list_in_eval_sExpressions1081);
            	    a=lisp_list();
            	    _fsp--;
            	    if (failed) return list;
            	    if ( backtracking==0 ) {
            	       list.add( a ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop13;
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
        return list;
    }
    // $ANTLR end eval_sExpressions


    // $ANTLR start lisp_list
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:621:1: lisp_list returns [SExpression sExpression] : LEFT_PAREN (t= NAME | t= VAR ) (a= lisp_atom | a= lisp_list )* RIGHT_PAREN ;
    public final SExpression lisp_list() throws RecognitionException {
        SExpression sExpression = null;

        Token t=null;
        SExpression a = null;


        
                List list = new ArrayList();
                sExpression = null;
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:626:2: ( LEFT_PAREN (t= NAME | t= VAR ) (a= lisp_atom | a= lisp_list )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:626:4: LEFT_PAREN (t= NAME | t= VAR ) (a= lisp_atom | a= lisp_list )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lisp_list1110); if (failed) return sExpression;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:628:3: (t= NAME | t= VAR )
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==NAME) ) {
                alt14=1;
            }
            else if ( (LA14_0==VAR) ) {
                alt14=2;
            }
            else {
                if (backtracking>0) {failed=true; return sExpression;}
                NoViableAltException nvae =
                    new NoViableAltException("628:3: (t= NAME | t= VAR )", 14, 0, input);

                throw nvae;
            }
            switch (alt14) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:629:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_lisp_list1127); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       list.add( new SymbolLispAtom( t.getText() ) ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:631:7: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_lisp_list1147); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       list.add( new VariableLispAtom( t.getText() ) ); 
                    }

                    }
                    break;

            }

            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:633:3: (a= lisp_atom | a= lisp_list )*
            loop15:
            do {
                int alt15=3;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==NAME||LA15_0==STRING||LA15_0==INT||LA15_0==VAR||(LA15_0>=FLOAT && LA15_0<=NULL)) ) {
                    alt15=1;
                }
                else if ( (LA15_0==LEFT_PAREN) ) {
                    alt15=2;
                }


                switch (alt15) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:633:6: a= lisp_atom
            	    {
            	    pushFollow(FOLLOW_lisp_atom_in_lisp_list1170);
            	    a=lisp_atom();
            	    _fsp--;
            	    if (failed) return sExpression;
            	    if ( backtracking==0 ) {
            	       list.add( a ); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:634:6: a= lisp_list
            	    {
            	    pushFollow(FOLLOW_lisp_list_in_lisp_list1181);
            	    a=lisp_list();
            	    _fsp--;
            	    if (failed) return sExpression;
            	    if ( backtracking==0 ) {
            	       list.add( a ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lisp_list1208); if (failed) return sExpression;
            if ( backtracking==0 ) {
               sExpression = new LispForm( ( SExpression[] ) list.toArray( new SExpression[ list.size () ] ) ); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return sExpression;
    }
    // $ANTLR end lisp_list


    // $ANTLR start lisp_atom
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:640:1: lisp_atom returns [SExpression sExpression] : (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME ) ;
    public final SExpression lisp_atom() throws RecognitionException {
        SExpression sExpression = null;

        Token t=null;

        
        		sExpression  =  null;		
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:644:2: ( (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:655:3: (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:655:3: (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME )
            int alt16=7;
            switch ( input.LA(1) ) {
            case VAR:
                {
                alt16=1;
                }
                break;
            case STRING:
                {
                alt16=2;
                }
                break;
            case FLOAT:
                {
                alt16=3;
                }
                break;
            case INT:
                {
                alt16=4;
                }
                break;
            case BOOL:
                {
                alt16=5;
                }
                break;
            case NULL:
                {
                alt16=6;
                }
                break;
            case NAME:
                {
                alt16=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return sExpression;}
                NoViableAltException nvae =
                    new NoViableAltException("655:3: (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME )", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:656:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_lisp_atom1258); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new VariableLispAtom( t.getText() ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:657:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_lisp_atom1270); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new StringLispAtom( getString( t ) ); 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:658:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_lisp_atom1292); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new FloatLispAtom( t.getText() ); 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:659:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_lisp_atom1304); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new IntLispAtom( t.getText() ); 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:660:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lisp_atom1317); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new BoolLispAtom( t.getText() ); 
                    }

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:661:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_lisp_atom1333); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new NullLispAtom( null ); 
                    }

                    }
                    break;
                case 7 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:662:14: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_lisp_atom1359); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new SymbolLispAtom( "\"" +t.getText() + "\""); 
                    }

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
        return sExpression;
    }
    // $ANTLR end lisp_atom


    // $ANTLR start literal
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:667:1: literal returns [String text] : (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final String literal() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:671:2: ( (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:671:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:671:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt17=6;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt17=1;
                }
                break;
            case NAME:
                {
                alt17=2;
                }
                break;
            case INT:
                {
                alt17=3;
                }
                break;
            case FLOAT:
                {
                alt17=4;
                }
                break;
            case BOOL:
                {
                alt17=5;
                }
                break;
            case NULL:
                {
                alt17=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("671:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:671:8: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal1403); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:672:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_literal1416); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:673:7: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal1432); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:674:7: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal1447); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:675:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal1460); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:676:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal1474); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = null; 
                    }

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


 

    public static final BitSet FOLLOW_LEFT_PAREN_in_importDescr63 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_importDescr65 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_importDescr69 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_importDescr72 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_defrule106 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_DEFRULE_in_defrule114 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_defrule118 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_defrule130 = new BitSet(new long[]{0x0000100000010010L});
    public static final BitSet FOLLOW_ruleAttribute_in_defrule136 = new BitSet(new long[]{0x0000100000010010L});
    public static final BitSet FOLLOW_ce_in_defrule144 = new BitSet(new long[]{0x0000100000010010L});
    public static final BitSet FOLLOW_44_in_defrule153 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_list_in_defrule162 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_defrule171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute188 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_DECLARE_in_ruleAttribute190 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute197 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_salience_in_ruleAttribute201 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute205 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience242 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_INT_in_salience246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_ce272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_ce282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_ce291 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_ce300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_ce314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_ce328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bound_pattern_in_ce337 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_and_ce364 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_AND_in_and_ce369 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_ce_in_and_ce375 = new BitSet(new long[]{0x0000000000010050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_and_ce384 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_or_ce412 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_OR_in_or_ce417 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_ce_in_or_ce423 = new BitSet(new long[]{0x0000000000010050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_or_ce432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_not_ce460 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_NOT_in_not_ce465 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_ce_in_not_ce471 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_not_ce479 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_exists_ce508 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_EXISTS_in_exists_ce513 = new BitSet(new long[]{0x0000000000010010L});
    public static final BitSet FOLLOW_ce_in_exists_ce519 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_exists_ce527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_eval_ce546 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_TEST_in_eval_ce551 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_list_in_eval_ce558 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_eval_ce568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_normal_pattern596 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_normal_pattern603 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_normal_pattern609 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_normal_pattern619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_bound_pattern647 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_ASSIGN_OP_in_bound_pattern653 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_bound_pattern655 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_bound_pattern659 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_bound_pattern668 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_bound_pattern675 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_field_constriant706 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_field_constriant710 = new BitSet(new long[]{0x0000000003F10520L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constriant725 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_field_constriant731 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective770 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_PIPE_in_or_restr_connective794 = new BitSet(new long[]{0x0000000003F10520L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective804 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_restriction_in_and_restr_connective836 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_AMPERSAND_in_and_restr_connective844 = new BitSet(new long[]{0x0000000003F10520L});
    public static final BitSet FOLLOW_restriction_in_and_restr_connective846 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_TILDE_in_restriction879 = new BitSet(new long[]{0x0000000003E10520L});
    public static final BitSet FOLLOW_predicate_constraint_in_restriction895 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_return_value_restriction_in_restriction911 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_restriction_in_restriction920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_restriction_in_restriction932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_predicate_constraint965 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_list_in_predicate_constraint971 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_return_value_restriction990 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_list_in_return_value_restriction997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_variable_restriction1016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_literal_restriction1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lisp_list_in_eval_sExpressions1081 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lisp_list1110 = new BitSet(new long[]{0x0000000000010020L});
    public static final BitSet FOLLOW_NAME_in_lisp_list1127 = new BitSet(new long[]{0x0000000003810570L});
    public static final BitSet FOLLOW_VAR_in_lisp_list1147 = new BitSet(new long[]{0x0000000003810570L});
    public static final BitSet FOLLOW_lisp_atom_in_lisp_list1170 = new BitSet(new long[]{0x0000000003810570L});
    public static final BitSet FOLLOW_lisp_list_in_lisp_list1181 = new BitSet(new long[]{0x0000000003810570L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lisp_list1208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_lisp_atom1258 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_lisp_atom1270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_lisp_atom1292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_lisp_atom1304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_lisp_atom1317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_lisp_atom1333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_lisp_atom1359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal1403 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_literal1416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal1432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal1447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal1460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal1474 = new BitSet(new long[]{0x0000000000000002L});

}