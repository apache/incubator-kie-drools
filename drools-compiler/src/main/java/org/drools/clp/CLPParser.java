// $ANTLR 3.0b7 C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2007-03-04 18:04:41

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LEFT_PAREN", "ID", "RIGHT_PAREN", "VAR", "STRING", "INT", "FLOAT", "BOOL", "NULL", "EOL", "WS", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "MULTI_LINE_COMMENT", "MISC", "';'", "'defrule'", "'~'", "'&'", "'|'", "'='"
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
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:210:1: rule returns [RuleDescr rule] : loc= LEFT_PAREN 'defrule' ruleName= ID (column= bound_pattern | column= pattern ) RIGHT_PAREN ;
    public RuleDescr rule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        Token ruleName=null;
        ColumnDescr column = null;


         
        	        rule = null; 
        	        AndDescr lhs = null;
        	        ColumnDescr colum = null;
        	      
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:216:4: (loc= LEFT_PAREN 'defrule' ruleName= ID (column= bound_pattern | column= pattern ) RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:216:4: loc= LEFT_PAREN 'defrule' ruleName= ID (column= bound_pattern | column= pattern ) RIGHT_PAREN
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
            	  
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:230:4: (column= bound_pattern | column= pattern )
            int alt2=2;
            int LA2_0 = input.LA(1);
            if ( (LA2_0==VAR) ) {
                alt2=1;
            }
            else if ( (LA2_0==LEFT_PAREN) ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("230:4: (column= bound_pattern | column= pattern )", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:230:6: column= bound_pattern
                    {
                    pushFollow(FOLLOW_bound_pattern_in_rule89);
                    column=bound_pattern();
                    _fsp--;

                    
                    	        lhs.addDescr( column );
                    	    

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:234:6: column= pattern
                    {
                    pushFollow(FOLLOW_pattern_in_rule105);
                    column=pattern();
                    _fsp--;

                    
                    	        lhs.addDescr( column );
                    	    

                    }
                    break;

            }

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_rule130); 

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


    // $ANTLR start bound_pattern
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:243:1: bound_pattern returns [ColumnDescr column] : id= VAR ;
    public ColumnDescr bound_pattern() throws RecognitionException {
        ColumnDescr column = null;

        Token id=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:245:4: (id= VAR )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:245:4: id= VAR
            {
            id=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_bound_pattern148); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return column;
    }
    // $ANTLR end bound_pattern


    // $ANTLR start pattern
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:254:1: pattern returns [ColumnDescr column] : LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN ;
    public ColumnDescr pattern() throws RecognitionException {
        ColumnDescr column = null;

        Token name=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:255:5: ( LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:255:5: LEFT_PAREN name= ID ( field_constriant[column] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_pattern167); 
            name=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_pattern175); 
            
            	      column = new ColumnDescr(name.getText());
            	  
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:260:4: ( field_constriant[column] )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);
                if ( (LA3_0==LEFT_PAREN) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:260:4: field_constriant[column]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_pattern186);
            	    field_constriant(column);
            	    _fsp--;


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_pattern194); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return column;
    }
    // $ANTLR end pattern


    // $ANTLR start field_constriant
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:265:1: field_constriant[ColumnDescr column] : LEFT_PAREN f= ID ( '~' )? ( return_value_constraint[op, fc] | lc= literal_constraint ) ( ( '&' | '|' ) ( '~' )? ( return_value_constraint[op, fc] | lc= literal_constraint ) )* RIGHT_PAREN ;
    public void field_constriant(ColumnDescr column) throws RecognitionException {
        Token f=null;
        String lc = null;


        
             	List list = new ArrayList();
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		String op = "==";
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:272:4: ( LEFT_PAREN f= ID ( '~' )? ( return_value_constraint[op, fc] | lc= literal_constraint ) ( ( '&' | '|' ) ( '~' )? ( return_value_constraint[op, fc] | lc= literal_constraint ) )* RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:272:4: LEFT_PAREN f= ID ( '~' )? ( return_value_constraint[op, fc] | lc= literal_constraint ) ( ( '&' | '|' ) ( '~' )? ( return_value_constraint[op, fc] | lc= literal_constraint ) )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_field_constriant222); 
            f=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_field_constriant230); 
            
            			fc = new FieldConstraintDescr(f.getText());
            			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
             	        column.addDescr( fc );			
            	  
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:281:6: ( '~' )?
            int alt4=2;
            int LA4_0 = input.LA(1);
            if ( (LA4_0==29) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:281:7: '~'
                    {
                    match(input,29,FOLLOW_29_in_field_constriant252); 
                    op = "!=";

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:282:5: ( return_value_constraint[op, fc] | lc= literal_constraint )
            int alt5=2;
            int LA5_0 = input.LA(1);
            if ( (LA5_0==32) ) {
                alt5=1;
            }
            else if ( ((LA5_0>=STRING && LA5_0<=NULL)) ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("282:5: ( return_value_constraint[op, fc] | lc= literal_constraint )", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:283:7: return_value_constraint[op, fc]
                    {
                    pushFollow(FOLLOW_return_value_constraint_in_field_constriant271);
                    return_value_constraint(op,  fc);
                    _fsp--;


                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:285:4: lc= literal_constraint
                    {
                    pushFollow(FOLLOW_literal_constraint_in_field_constriant286);
                    lc=literal_constraint();
                    _fsp--;

                    
                    	      LiteralRestrictionDescr rd  = new LiteralRestrictionDescr(op, lc, true);   
                    	      fc.addRestriction( rd );
                    	      op = "==";
                    	  

                    }
                    break;

            }

            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:293:4: ( ( '&' | '|' ) ( '~' )? ( return_value_constraint[op, fc] | lc= literal_constraint ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);
                if ( ((LA9_0>=30 && LA9_0<=31)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:294:8: ( '&' | '|' ) ( '~' )? ( return_value_constraint[op, fc] | lc= literal_constraint )
            	    {
            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:294:8: ( '&' | '|' )
            	    int alt6=2;
            	    int LA6_0 = input.LA(1);
            	    if ( (LA6_0==30) ) {
            	        alt6=1;
            	    }
            	    else if ( (LA6_0==31) ) {
            	        alt6=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("294:8: ( '&' | '|' )", 6, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt6) {
            	        case 1 :
            	            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:294:9: '&'
            	            {
            	            match(input,30,FOLLOW_30_in_field_constriant317); 
            	            
            	            		   		fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND));
            	            	       

            	            }
            	            break;
            	        case 2 :
            	            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:299:8: '|'
            	            {
            	            match(input,31,FOLLOW_31_in_field_constriant345); 
            	            
            	             				fc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR));	      
            	            	      

            	            }
            	            break;

            	    }

            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:305:7: ( '~' )?
            	    int alt7=2;
            	    int LA7_0 = input.LA(1);
            	    if ( (LA7_0==29) ) {
            	        alt7=1;
            	    }
            	    switch (alt7) {
            	        case 1 :
            	            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:305:8: '~'
            	            {
            	            match(input,29,FOLLOW_29_in_field_constriant394); 
            	            op = "!=";

            	            }
            	            break;

            	    }

            	    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:306:7: ( return_value_constraint[op, fc] | lc= literal_constraint )
            	    int alt8=2;
            	    int LA8_0 = input.LA(1);
            	    if ( (LA8_0==32) ) {
            	        alt8=1;
            	    }
            	    else if ( ((LA8_0>=STRING && LA8_0<=NULL)) ) {
            	        alt8=2;
            	    }
            	    else {
            	        NoViableAltException nvae =
            	            new NoViableAltException("306:7: ( return_value_constraint[op, fc] | lc= literal_constraint )", 8, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt8) {
            	        case 1 :
            	            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:307:8: return_value_constraint[op, fc]
            	            {
            	            pushFollow(FOLLOW_return_value_constraint_in_field_constriant422);
            	            return_value_constraint(op,  fc);
            	            _fsp--;


            	            }
            	            break;
            	        case 2 :
            	            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:309:5: lc= literal_constraint
            	            {
            	            pushFollow(FOLLOW_literal_constraint_in_field_constriant439);
            	            lc=literal_constraint();
            	            _fsp--;

            	            
            	            		      LiteralRestrictionDescr rd  = new LiteralRestrictionDescr(op, lc, true);   
            	            		      fc.addRestriction( rd );
            	            		      op = "==";
            	            		  

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_field_constriant480); 

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


    // $ANTLR start constraint
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:333:1: constraint[String op] returns [RestrictionDescr rd] : bvc= ID ;
    public RestrictionDescr constraint(String op) throws RecognitionException {
        RestrictionDescr rd = null;

        Token bvc=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:335:6: (bvc= ID )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:335:6: bvc= ID
            {
            bvc=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_constraint510); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return rd;
    }
    // $ANTLR end constraint


    // $ANTLR start return_value_constraint
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:339:1: return_value_constraint[String op, FieldConstraintDescr fc] : '=' LEFT_PAREN id= ID RIGHT_PAREN ;
    public void return_value_constraint(String op, FieldConstraintDescr fc) throws RecognitionException {
        Token id=null;

        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:341:3: ( '=' LEFT_PAREN id= ID RIGHT_PAREN )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:341:3: '=' LEFT_PAREN id= ID RIGHT_PAREN
            {
            match(input,32,FOLLOW_32_in_return_value_constraint527); 
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_return_value_constraint528); 
            id=(Token)input.LT(1);
            match(input,ID,FOLLOW_ID_in_return_value_constraint535); 
             
            			fc.addRestriction( new ReturnValueRestrictionDescr(op, id.getText() ) );
            		
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_return_value_constraint544); 

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
    // $ANTLR end return_value_constraint


    // $ANTLR start literal_constraint
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:350:1: literal_constraint returns [String text] : (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public String literal_constraint() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:354:4: ( (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:354:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:354:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt10=5;
            switch ( input.LA(1) ) {
            case STRING:
                alt10=1;
                break;
            case INT:
                alt10=2;
                break;
            case FLOAT:
                alt10=3;
                break;
            case BOOL:
                alt10=4;
                break;
            case NULL:
                alt10=5;
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("354:4: (t= STRING | t= INT | t= FLOAT | t= BOOL | t= NULL )", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:354:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal_constraint571); 
                     text = getString( t ); 

                    }
                    break;
                case 2 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:355:5: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal_constraint582); 
                     text = t.getText(); 

                    }
                    break;
                case 3 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:356:5: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal_constraint595); 
                     text = t.getText(); 

                    }
                    break;
                case 4 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:357:5: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal_constraint606); 
                     text = t.getText(); 

                    }
                    break;
                case 5 :
                    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:358:5: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal_constraint618); 
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


    // $ANTLR start function
    // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:363:1: function : ;
    public void function() throws RecognitionException {
        try {
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:366:2: ()
            // C:\\dev\\jbossrules\\trunk\\drools-compiler\\src\\main\\resources\\org\\drools\\clp\\CLP.g:366:2: 
            {
            }

        }
        finally {
        }
        return ;
    }
    // $ANTLR end function


 

    public static final BitSet FOLLOW_27_in_opt_semicolon38 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_rule64 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_28_in_rule66 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_rule74 = new BitSet(new long[]{0x0000000000000090L});
    public static final BitSet FOLLOW_bound_pattern_in_rule89 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_pattern_in_rule105 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_rule130 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_bound_pattern148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_pattern167 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_pattern175 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_pattern186 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_pattern194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_field_constriant222 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_field_constriant230 = new BitSet(new long[]{0x0000000120001F00L});
    public static final BitSet FOLLOW_29_in_field_constriant252 = new BitSet(new long[]{0x0000000100001F00L});
    public static final BitSet FOLLOW_return_value_constraint_in_field_constriant271 = new BitSet(new long[]{0x00000000C0000040L});
    public static final BitSet FOLLOW_literal_constraint_in_field_constriant286 = new BitSet(new long[]{0x00000000C0000040L});
    public static final BitSet FOLLOW_30_in_field_constriant317 = new BitSet(new long[]{0x0000000120001F00L});
    public static final BitSet FOLLOW_31_in_field_constriant345 = new BitSet(new long[]{0x0000000120001F00L});
    public static final BitSet FOLLOW_29_in_field_constriant394 = new BitSet(new long[]{0x0000000100001F00L});
    public static final BitSet FOLLOW_return_value_constraint_in_field_constriant422 = new BitSet(new long[]{0x00000000C0000040L});
    public static final BitSet FOLLOW_literal_constraint_in_field_constriant439 = new BitSet(new long[]{0x00000000C0000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_field_constriant480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_constraint510 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_return_value_constraint527 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_return_value_constraint528 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_return_value_constraint535 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_return_value_constraint544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal_constraint571 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal_constraint582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal_constraint595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal_constraint606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal_constraint618 = new BitSet(new long[]{0x0000000000000002L});

}