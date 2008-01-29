// $ANTLR 3.0.1 C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g 2008-01-29 07:31:27

	package org.drools.clips;

    import org.drools.clips.*;
import org.drools.clips.mvel.*;
    
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LEFT_PAREN", "NAME", "RIGHT_PAREN", "DEFFUNCTION", "DEFRULE", "STRING", "SALIENCE", "INT", "AND", "OR", "NOT", "EXISTS", "TEST", "VAR", "ASSIGN_OP", "PIPE", "AMPERSAND", "TILDE", "COLON", "EQUALS", "FLOAT", "BOOL", "NULL", "EOL", "WS", "DECLARE", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SYMBOL_CHAR", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "MULTI_LINE_COMMENT", "SYMBOL", "FIRST_SYMBOL_CHAR", "'import'", "'=>'"
    };
    public static final int RIGHT_SQUARE=38;
    public static final int RIGHT_CURLY=40;
    public static final int EQUALS=23;
    public static final int FLOAT=24;
    public static final int NOT=14;
    public static final int SYMBOL_CHAR=34;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=35;
    public static final int AND=12;
    public static final int EOF=-1;
    public static final int FIRST_SYMBOL_CHAR=43;
    public static final int HexDigit=31;
    public static final int DEFFUNCTION=7;
    public static final int ASSIGN_OP=18;
    public static final int RIGHT_PAREN=6;
    public static final int NAME=5;
    public static final int EOL=27;
    public static final int DEFRULE=8;
    public static final int TILDE=21;
    public static final int PIPE=19;
    public static final int VAR=17;
    public static final int EXISTS=15;
    public static final int SYMBOL=42;
    public static final int NULL=26;
    public static final int BOOL=25;
    public static final int SALIENCE=10;
    public static final int AMPERSAND=20;
    public static final int INT=11;
    public static final int MULTI_LINE_COMMENT=41;
    public static final int COLON=22;
    public static final int WS=28;
    public static final int UnicodeEscape=32;
    public static final int LEFT_CURLY=39;
    public static final int OR=13;
    public static final int TEST=16;
    public static final int LEFT_PAREN=4;
    public static final int DECLARE=29;
    public static final int LEFT_SQUARE=37;
    public static final int EscapeSequence=30;
    public static final int OctalEscape=33;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=36;
    public static final int STRING=9;

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
    	private MVELBuildContext context;
    	private boolean parserDebug = false;
    	private Location location = new Location( Location.LOCATION_UNKNOWN );	
    	
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
          



    // $ANTLR start eval
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:224:1: eval[ParserHandler handler, MVELBuildContext context] : (i= importDescr | f= deffunction | r= defrule | form= lisp_form ) ;
    public final void eval(ParserHandler handler, MVELBuildContext context) throws RecognitionException {
        ImportDescr i = null;

        FunctionDescr f = null;

        RuleDescr r = null;

        LispForm form = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:225:2: ( (i= importDescr | f= deffunction | r= defrule | form= lisp_form ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:226:2: (i= importDescr | f= deffunction | r= defrule | form= lisp_form )
            {
            if ( backtracking==0 ) {
               this.context = context; 
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:227:2: (i= importDescr | f= deffunction | r= defrule | form= lisp_form )
            int alt1=4;
            int LA1_0 = input.LA(1);

            if ( (LA1_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case DEFFUNCTION:
                    {
                    alt1=2;
                    }
                    break;
                case 44:
                    {
                    alt1=1;
                    }
                    break;
                case DEFRULE:
                    {
                    alt1=3;
                    }
                    break;
                case NAME:
                case VAR:
                    {
                    alt1=4;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("227:2: (i= importDescr | f= deffunction | r= defrule | form= lisp_form )", 1, 1, input);

                    throw nvae;
                }

            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("227:2: (i= importDescr | f= deffunction | r= defrule | form= lisp_form )", 1, 0, input);

                throw nvae;
            }
            switch (alt1) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:227:7: i= importDescr
                    {
                    pushFollow(FOLLOW_importDescr_in_eval60);
                    i=importDescr();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       handler.importHandler( i ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:228:7: f= deffunction
                    {
                    pushFollow(FOLLOW_deffunction_in_eval71);
                    f=deffunction();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       handler.functionHandler( f ); 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:229:7: r= defrule
                    {
                    pushFollow(FOLLOW_defrule_in_eval84);
                    r=defrule();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       handler.ruleHandler( r ); 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:230:7: form= lisp_form
                    {
                    pushFollow(FOLLOW_lisp_form_in_eval96);
                    form=lisp_form();
                    _fsp--;
                    if (failed) return ;
                    if ( backtracking==0 ) {
                       handler.lispFormHandler( form ); 
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
    // $ANTLR end eval


    // $ANTLR start importDescr
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:247:1: importDescr returns [ImportDescr importDescr] : LEFT_PAREN 'import' importName= NAME RIGHT_PAREN ;
    public final ImportDescr importDescr() throws RecognitionException {
        ImportDescr importDescr = null;

        Token importName=null;

        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:248:2: ( LEFT_PAREN 'import' importName= NAME RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:248:4: LEFT_PAREN 'import' importName= NAME RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_importDescr123); if (failed) return importDescr;
            match(input,44,FOLLOW_44_in_importDescr125); if (failed) return importDescr;
            importName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_importDescr129); if (failed) return importDescr;
            if ( backtracking==0 ) {
               importDescr = new ImportDescr( importName.getText() ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_importDescr133); if (failed) return importDescr;

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


    // $ANTLR start deffunction
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:296:1: deffunction returns [FunctionDescr functionDescr] : LEFT_PAREN t= DEFFUNCTION name= lisp_atom params= lisp_form (form= lisp_form )+ RIGHT_PAREN ;
    public final FunctionDescr deffunction() throws RecognitionException {
        FunctionDescr functionDescr = null;

        Token t=null;
        SExpression name = null;

        LispForm params = null;

        LispForm form = null;


        
                List content = null;
                functionDescr = null;
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:301:2: ( LEFT_PAREN t= DEFFUNCTION name= lisp_atom params= lisp_form (form= lisp_form )+ RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:301:4: LEFT_PAREN t= DEFFUNCTION name= lisp_atom params= lisp_form (form= lisp_form )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_deffunction168); if (failed) return functionDescr;
            t=(Token)input.LT(1);
            match(input,DEFFUNCTION,FOLLOW_DEFFUNCTION_in_deffunction178); if (failed) return functionDescr;
            pushFollow(FOLLOW_lisp_atom_in_deffunction188);
            name=lisp_atom();
            _fsp--;
            if (failed) return functionDescr;
            pushFollow(FOLLOW_lisp_form_in_deffunction198);
            params=lisp_form();
            _fsp--;
            if (failed) return functionDescr;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:305:3: (form= lisp_form )+
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
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:305:4: form= lisp_form
            	    {
            	    pushFollow(FOLLOW_lisp_form_in_deffunction207);
            	    form=lisp_form();
            	    _fsp--;
            	    if (failed) return functionDescr;
            	    if ( backtracking==0 ) {
            	       if ( content == null ) content = new ArrayList(); content.add( form ); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt2 >= 1 ) break loop2;
            	    if (backtracking>0) {failed=true; return functionDescr;}
                        EarlyExitException eee =
                            new EarlyExitException(2, input);
                        throw eee;
                }
                cnt2++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_deffunction229); if (failed) return functionDescr;
            if ( backtracking==0 ) {
               functionDescr = FunctionHandlers.createFunctionDescr( name, params, content ); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return functionDescr;
    }
    // $ANTLR end deffunction


    // $ANTLR start defrule
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:311:1: defrule returns [RuleDescr rule] : loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' t= lisp_form RIGHT_PAREN ;
    public final RuleDescr defrule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        Token ruleName=null;
        Token documentation=null;
        LispForm t = null;


         
        	        rule = null; 
        	        AndDescr lhs = null;
        	        PatternDescr colum = null;
        	        AttributeDescr module = null;	      
                    Set declarations = null;  
        	      
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:319:2: (loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' t= lisp_form RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:319:4: loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' t= lisp_form RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_defrule266); if (failed) return rule;
            match(input,DEFRULE,FOLLOW_DEFRULE_in_defrule274); if (failed) return rule;
            ruleName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_defrule278); if (failed) return rule;
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
            match(input,STRING,FOLLOW_STRING_in_defrule290); if (failed) return rule;
            if ( backtracking==0 ) {
              
              	    	// do nothing here for now
              		
            }
            pushFollow(FOLLOW_ruleAttribute_in_defrule296);
            ruleAttribute(rule);
            _fsp--;
            if (failed) return rule;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:358:3: ( ce[lhs, declarations] )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==LEFT_PAREN||LA3_0==VAR) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:358:3: ce[lhs, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_defrule304);
            	    ce(lhs,  declarations);
            	    _fsp--;
            	    if (failed) return rule;

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            match(input,45,FOLLOW_45_in_defrule313); if (failed) return rule;
            pushFollow(FOLLOW_lisp_form_in_defrule322);
            t=lisp_form();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               rule.setConsequence( t ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_defrule331); if (failed) return rule;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:368:1: ruleAttribute[RuleDescr rule] : ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? ;
    public final void ruleAttribute(RuleDescr rule) throws RecognitionException {
        AttributeDescr d = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:369:2: ( ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:370:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:370:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
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
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:370:5: LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute348); if (failed) return ;
                    match(input,DECLARE,FOLLOW_DECLARE_in_ruleAttribute350); if (failed) return ;
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:371:4: ( LEFT_PAREN d= salience RIGHT_PAREN )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0==LEFT_PAREN) ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:371:6: LEFT_PAREN d= salience RIGHT_PAREN
                            {
                            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute357); if (failed) return ;
                            pushFollow(FOLLOW_salience_in_ruleAttribute361);
                            d=salience();
                            _fsp--;
                            if (failed) return ;
                            if ( backtracking==0 ) {
                               rule.addAttribute( d ); 
                            }
                            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute365); if (failed) return ;

                            }
                            break;

                    }

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute372); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:375:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:379:2: (loc= SALIENCE i= INT )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:380:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience402); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience406); if (failed) return d;
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:390:1: ce[ConditionalElementDescr in_ce, Set declarations] : ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] ) ;
    public final void ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:391:2: ( ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:391:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:391:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )
            int alt6=7;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case NOT:
                    {
                    alt6=3;
                    }
                    break;
                case EXISTS:
                    {
                    alt6=4;
                    }
                    break;
                case OR:
                    {
                    alt6=2;
                    }
                    break;
                case AND:
                    {
                    alt6=1;
                    }
                    break;
                case NAME:
                    {
                    alt6=6;
                    }
                    break;
                case TEST:
                    {
                    alt6=5;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("391:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )", 6, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA6_0==VAR) ) {
                alt6=7;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("391:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:391:8: and_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_and_ce_in_ce432);
                    and_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:392:7: or_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_or_ce_in_ce442);
                    or_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:393:7: not_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_not_ce_in_ce451);
                    not_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:394:7: exists_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_exists_ce_in_ce460);
                    exists_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:395:8: eval_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_eval_ce_in_ce474);
                    eval_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:396:7: normal_pattern[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_ce488);
                    normal_pattern(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 7 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:397:7: bound_pattern[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_bound_pattern_in_ce497);
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:401:1: and_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN ;
    public final void and_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                AndDescr andDescr= null;        
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:405:2: ( LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:405:4: LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_and_ce524); if (failed) return ;
            match(input,AND,FOLLOW_AND_in_and_ce529); if (failed) return ;
            if ( backtracking==0 ) {
              
              	    	andDescr = new AndDescr();
              			in_ce.addDescr( andDescr );
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:410:3: ( ce[andDescr, declarations] )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( (LA7_0==LEFT_PAREN||LA7_0==VAR) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:410:3: ce[andDescr, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_and_ce535);
            	    ce(andDescr,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_and_ce544); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:414:1: or_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN ;
    public final void or_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                OrDescr orDescr= null;         
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:418:2: ( LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:418:4: LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_or_ce572); if (failed) return ;
            match(input,OR,FOLLOW_OR_in_or_ce577); if (failed) return ;
            if ( backtracking==0 ) {
              
              	    	orDescr = new OrDescr();
              			in_ce.addDescr( orDescr );
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:423:3: ( ce[orDescr, declarations] )+
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
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:423:3: ce[orDescr, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_or_ce583);
            	    ce(orDescr,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt8 >= 1 ) break loop8;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(8, input);
                        throw eee;
                }
                cnt8++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_or_ce592); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:427:1: not_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN ;
    public final void not_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                NotDescr notDescr= null;         
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:431:2: ( LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:431:4: LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_not_ce620); if (failed) return ;
            match(input,NOT,FOLLOW_NOT_in_not_ce625); if (failed) return ;
            if ( backtracking==0 ) {
              
              			notDescr = new NotDescr();
              		    in_ce.addDescr( notDescr );
              		
            }
            pushFollow(FOLLOW_ce_in_not_ce631);
            ce(notDescr,  declarations);
            _fsp--;
            if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_not_ce639); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:440:1: exists_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN ;
    public final void exists_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                ExistsDescr existsDescr= null;        
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:444:2: ( LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:444:4: LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_exists_ce668); if (failed) return ;
            match(input,EXISTS,FOLLOW_EXISTS_in_exists_ce673); if (failed) return ;
            if ( backtracking==0 ) {
              
              		    existsDescr = new ExistsDescr();
              		    in_ce.addDescr( existsDescr );
              		
            }
            pushFollow(FOLLOW_ce_in_exists_ce679);
            ce(existsDescr,  declarations);
            _fsp--;
            if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_exists_ce687); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:453:1: eval_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN TEST t= lisp_form RIGHT_PAREN ;
    public final void eval_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        LispForm t = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:454:2: ( LEFT_PAREN TEST t= lisp_form RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:454:4: LEFT_PAREN TEST t= lisp_form RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_eval_ce706); if (failed) return ;
            match(input,TEST,FOLLOW_TEST_in_eval_ce711); if (failed) return ;
            pushFollow(FOLLOW_lisp_form_in_eval_ce718);
            t=lisp_form();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               EvalDescr evalDescr = new EvalDescr(); evalDescr.setContent( t ); in_ce.addDescr( evalDescr ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_eval_ce728); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:460:1: normal_pattern[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN ;
    public final void normal_pattern(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        Token name=null;

        
                PatternDescr pattern = null;
                ConditionalElementDescr top = null;
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:465:2: ( LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:465:4: LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_normal_pattern756); if (failed) return ;
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_normal_pattern763); if (failed) return ;
            if ( backtracking==0 ) {
              
              			pattern = new PatternDescr(name.getText());
              			in_ce.addDescr( pattern );
              			top = pattern.getConstraint();
              			
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:472:3: ( field_constriant[top, declarations] )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==LEFT_PAREN) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:472:3: field_constriant[top, declarations]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_normal_pattern769);
            	    field_constriant(top,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_normal_pattern779); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:478:1: bound_pattern[ConditionalElementDescr in_ce, Set declarations] : var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN ;
    public final void bound_pattern(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        Token var=null;
        Token name=null;

        
                PatternDescr pattern = null;
                String identifier = null;
                ConditionalElementDescr top = null;        
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:484:2: (var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:484:4: var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_bound_pattern807); if (failed) return ;
            if ( backtracking==0 ) {
              
              			identifier = var.getText();
              		
            }
            match(input,ASSIGN_OP,FOLLOW_ASSIGN_OP_in_bound_pattern813); if (failed) return ;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_bound_pattern815); if (failed) return ;
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_bound_pattern819); if (failed) return ;
            if ( backtracking==0 ) {
              
              			pattern = new PatternDescr(name.getText());
              			pattern.setIdentifier( identifier );
              			in_ce.addDescr( pattern );
              			top = pattern.getConstraint();				    
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:494:3: ( field_constriant[top, declarations] )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==LEFT_PAREN) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:494:3: field_constriant[top, declarations]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_bound_pattern828);
            	    field_constriant(top,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_bound_pattern835); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:498:1: field_constriant[ConditionalElementDescr base, Set declarations] : LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN ;
    public final void field_constriant(ConditionalElementDescr base, Set declarations) throws RecognitionException {
        Token f=null;

        
             	List list = new ArrayList();
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		RestrictionConnectiveDescr top = null;		
        		String op = "==";
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:506:2: ( LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:507:3: LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_field_constriant866); if (failed) return ;
            f=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_field_constriant870); if (failed) return ;
            if ( backtracking==0 ) {
              
              			fc = new FieldConstraintDescr(f.getText());
              			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
              			base.addDescr( fc );	
              			top = fc.getRestriction();		
              		
            }
            pushFollow(FOLLOW_or_restr_connective_in_field_constriant885);
            or_restr_connective(top,  base,  fc,  declarations);
            _fsp--;
            if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_field_constriant891); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:534:1: or_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] options {backtrack=true; } : and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )* ;
    public final void or_restr_connective(RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        
        		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:541:2: ( and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )* )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:542:3: and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )*
            {
            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective930);
            and_restr_connective(or,  ceBase,  fcBase,  declarations);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:543:3: ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==PIPE) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:545:6: PIPE and_restr_connective[or, ceBase, fcBase, declarations]
            	    {
            	    match(input,PIPE,FOLLOW_PIPE_in_or_restr_connective954); if (failed) return ;
            	    if ( backtracking==0 ) {
            	      
            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective964);
            	    and_restr_connective(or,  ceBase,  fcBase,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop11;
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:560:1: and_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] : restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )* ;
    public final void and_restr_connective(RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        
        		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:564:2: ( restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )* )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:565:3: restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )*
            {
            pushFollow(FOLLOW_restriction_in_and_restr_connective996);
            restriction(and,  ceBase,  fcBase,  declarations);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:566:3: ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==AMPERSAND) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:566:5: AMPERSAND restriction[and, ceBase, fcBase, declarations]
            	    {
            	    match(input,AMPERSAND,FOLLOW_AMPERSAND_in_and_restr_connective1004); if (failed) return ;
            	    pushFollow(FOLLOW_restriction_in_and_restr_connective1006);
            	    restriction(and,  ceBase,  fcBase,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop12;
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:585:1: restriction[RestrictionConnectiveDescr rc, ConditionalElementDescr base, FieldConstraintDescr fcBase, Set declarations ] : ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction ) ;
    public final void restriction(RestrictionConnectiveDescr rc, ConditionalElementDescr base, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        String lc = null;


        
        			String op = "==";
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:589:2: ( ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:589:4: ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:589:4: ( TILDE )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==TILDE) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:589:5: TILDE
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_restriction1039); if (failed) return ;
                    if ( backtracking==0 ) {
                      op = "!=";
                    }

                    }
                    break;

            }

            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:590:3: ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )
            int alt14=4;
            switch ( input.LA(1) ) {
            case COLON:
                {
                alt14=1;
                }
                break;
            case EQUALS:
                {
                alt14=2;
                }
                break;
            case VAR:
                {
                alt14=3;
                }
                break;
            case NAME:
            case STRING:
            case INT:
            case FLOAT:
            case BOOL:
            case NULL:
                {
                alt14=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("590:3: ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )", 14, 0, input);

                throw nvae;
            }

            switch (alt14) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:590:5: predicate_constraint[rc, op, base]
                    {
                    pushFollow(FOLLOW_predicate_constraint_in_restriction1055);
                    predicate_constraint(rc,  op,  base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:591:7: return_value_restriction[op, rc]
                    {
                    pushFollow(FOLLOW_return_value_restriction_in_restriction1071);
                    return_value_restriction(op,  rc);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:592:7: variable_restriction[op, rc, base, fcBase, declarations]
                    {
                    pushFollow(FOLLOW_variable_restriction_in_restriction1080);
                    variable_restriction(op,  rc,  base,  fcBase,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:593:8: lc= literal_restriction
                    {
                    pushFollow(FOLLOW_literal_restriction_in_restriction1092);
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:600:1: predicate_constraint[RestrictionConnectiveDescr rc, String op, ConditionalElementDescr base] : COLON t= lisp_form ;
    public final void predicate_constraint(RestrictionConnectiveDescr rc, String op, ConditionalElementDescr base) throws RecognitionException {
        LispForm t = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:601:2: ( COLON t= lisp_form )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:601:4: COLON t= lisp_form
            {
            match(input,COLON,FOLLOW_COLON_in_predicate_constraint1125); if (failed) return ;
            pushFollow(FOLLOW_lisp_form_in_predicate_constraint1131);
            t=lisp_form();
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:607:1: return_value_restriction[String op, RestrictionConnectiveDescr rc] : EQUALS t= lisp_form ;
    public final void return_value_restriction(String op, RestrictionConnectiveDescr rc) throws RecognitionException {
        LispForm t = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:608:2: ( EQUALS t= lisp_form )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:608:4: EQUALS t= lisp_form
            {
            match(input,EQUALS,FOLLOW_EQUALS_in_return_value_restriction1150); if (failed) return ;
            pushFollow(FOLLOW_lisp_form_in_return_value_restriction1157);
            t=lisp_form();
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:613:1: variable_restriction[String op, RestrictionConnectiveDescr rc, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] : VAR ;
    public final void variable_restriction(String op, RestrictionConnectiveDescr rc, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        Token VAR1=null;

        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:614:2: ( VAR )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:614:4: VAR
            {
            VAR1=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_variable_restriction1176); if (failed) return ;
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:628:1: literal_restriction returns [String text] : t= literal ;
    public final String literal_restriction() throws RecognitionException {
        String text = null;

        String t = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:632:2: (t= literal )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:633:6: t= literal
            {
            pushFollow(FOLLOW_literal_in_literal_restriction1209);
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


    // $ANTLR start lisp_form
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:650:1: lisp_form returns [LispForm lispForm] : LEFT_PAREN (t= NAME | t= VAR ) (a= lisp_atom | l= lisp_form )* RIGHT_PAREN ;
    public final LispForm lisp_form() throws RecognitionException {
        LispForm lispForm = null;

        Token t=null;
        SExpression a = null;

        LispForm l = null;


        
                List list = new ArrayList();
                lispForm = null;
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:655:2: ( LEFT_PAREN (t= NAME | t= VAR ) (a= lisp_atom | l= lisp_form )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:655:4: LEFT_PAREN (t= NAME | t= VAR ) (a= lisp_atom | l= lisp_form )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lisp_form1237); if (failed) return lispForm;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:657:3: (t= NAME | t= VAR )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==NAME) ) {
                alt15=1;
            }
            else if ( (LA15_0==VAR) ) {
                alt15=2;
            }
            else {
                if (backtracking>0) {failed=true; return lispForm;}
                NoViableAltException nvae =
                    new NoViableAltException("657:3: (t= NAME | t= VAR )", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:658:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_lisp_form1254); if (failed) return lispForm;
                    if ( backtracking==0 ) {
                       list.add( new SymbolLispAtom( t.getText() ) ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:660:7: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_lisp_form1274); if (failed) return lispForm;
                    if ( backtracking==0 ) {
                       list.add( new VariableLispAtom( t.getText(), context ) ); 
                    }

                    }
                    break;

            }

            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:662:3: (a= lisp_atom | l= lisp_form )*
            loop16:
            do {
                int alt16=3;
                int LA16_0 = input.LA(1);

                if ( (LA16_0==NAME||LA16_0==STRING||LA16_0==INT||LA16_0==VAR||(LA16_0>=FLOAT && LA16_0<=NULL)) ) {
                    alt16=1;
                }
                else if ( (LA16_0==LEFT_PAREN) ) {
                    alt16=2;
                }


                switch (alt16) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:662:6: a= lisp_atom
            	    {
            	    pushFollow(FOLLOW_lisp_atom_in_lisp_form1297);
            	    a=lisp_atom();
            	    _fsp--;
            	    if (failed) return lispForm;
            	    if ( backtracking==0 ) {
            	       list.add( a ); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:663:6: l= lisp_form
            	    {
            	    pushFollow(FOLLOW_lisp_form_in_lisp_form1308);
            	    l=lisp_form();
            	    _fsp--;
            	    if (failed) return lispForm;
            	    if ( backtracking==0 ) {
            	       list.add( l ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop16;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lisp_form1335); if (failed) return lispForm;
            if ( backtracking==0 ) {
               lispForm = new LispForm( (org.drools.clips.SExpression[] ) list.toArray( new SExpression[ list.size () ] ) ); 
            }

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return lispForm;
    }
    // $ANTLR end lisp_form


    // $ANTLR start lisp_atom
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:669:1: lisp_atom returns [SExpression sExpression] : (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME ) ;
    public final SExpression lisp_atom() throws RecognitionException {
        SExpression sExpression = null;

        Token t=null;

        
        		sExpression  =  null;		
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:673:2: ( (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:674:3: (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:674:3: (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME )
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
            case FLOAT:
                {
                alt17=3;
                }
                break;
            case INT:
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
            case NAME:
                {
                alt17=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return sExpression;}
                NoViableAltException nvae =
                    new NoViableAltException("674:3: (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME )", 17, 0, input);

                throw nvae;
            }

            switch (alt17) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:675:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_lisp_atom1379); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new VariableLispAtom( t.getText(), context ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:676:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_lisp_atom1391); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new StringLispAtom( getString( t ) ); 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:677:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_lisp_atom1413); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new FloatLispAtom( t.getText() ); 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:678:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_lisp_atom1425); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new IntLispAtom( t.getText() ); 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:679:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lisp_atom1438); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new BoolLispAtom( t.getText() ); 
                    }

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:680:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_lisp_atom1454); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new NullLispAtom( null ); 
                    }

                    }
                    break;
                case 7 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:681:14: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_lisp_atom1480); if (failed) return sExpression;
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:686:1: literal returns [String text] : (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final String literal() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:690:2: ( (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:690:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:690:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
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
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("690:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:690:8: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal1524); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:691:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_literal1537); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:692:7: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal1553); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:693:7: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal1568); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:694:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal1581); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLPMVEL.g:695:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal1595); if (failed) return text;
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


 

    public static final BitSet FOLLOW_importDescr_in_eval60 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_deffunction_in_eval71 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_defrule_in_eval84 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lisp_form_in_eval96 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_importDescr123 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_44_in_importDescr125 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_importDescr129 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_importDescr133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_deffunction168 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_DEFFUNCTION_in_deffunction178 = new BitSet(new long[]{0x0000000007020A20L});
    public static final BitSet FOLLOW_lisp_atom_in_deffunction188 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_form_in_deffunction198 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_form_in_deffunction207 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_deffunction229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_defrule266 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_DEFRULE_in_defrule274 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_defrule278 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_STRING_in_defrule290 = new BitSet(new long[]{0x0000200000020010L});
    public static final BitSet FOLLOW_ruleAttribute_in_defrule296 = new BitSet(new long[]{0x0000200000020010L});
    public static final BitSet FOLLOW_ce_in_defrule304 = new BitSet(new long[]{0x0000200000020010L});
    public static final BitSet FOLLOW_45_in_defrule313 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_form_in_defrule322 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_defrule331 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute348 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_DECLARE_in_ruleAttribute350 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute357 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_salience_in_ruleAttribute361 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute365 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience402 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_INT_in_salience406 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_ce432 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_ce442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_ce451 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_ce460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_ce474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_ce488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bound_pattern_in_ce497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_and_ce524 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_AND_in_and_ce529 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_ce_in_and_ce535 = new BitSet(new long[]{0x0000000000020050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_and_ce544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_or_ce572 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_OR_in_or_ce577 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_ce_in_or_ce583 = new BitSet(new long[]{0x0000000000020050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_or_ce592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_not_ce620 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_NOT_in_not_ce625 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_ce_in_not_ce631 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_not_ce639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_exists_ce668 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_EXISTS_in_exists_ce673 = new BitSet(new long[]{0x0000000000020010L});
    public static final BitSet FOLLOW_ce_in_exists_ce679 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_exists_ce687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_eval_ce706 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_TEST_in_eval_ce711 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_form_in_eval_ce718 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_eval_ce728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_normal_pattern756 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_normal_pattern763 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_normal_pattern769 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_normal_pattern779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_bound_pattern807 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_ASSIGN_OP_in_bound_pattern813 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_bound_pattern815 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_bound_pattern819 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_bound_pattern828 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_bound_pattern835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_field_constriant866 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_field_constriant870 = new BitSet(new long[]{0x0000000007E20A20L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constriant885 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_field_constriant891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective930 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_PIPE_in_or_restr_connective954 = new BitSet(new long[]{0x0000000007E20A20L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective964 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_restriction_in_and_restr_connective996 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_AMPERSAND_in_and_restr_connective1004 = new BitSet(new long[]{0x0000000007E20A20L});
    public static final BitSet FOLLOW_restriction_in_and_restr_connective1006 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_TILDE_in_restriction1039 = new BitSet(new long[]{0x0000000007C20A20L});
    public static final BitSet FOLLOW_predicate_constraint_in_restriction1055 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_return_value_restriction_in_restriction1071 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_restriction_in_restriction1080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_restriction_in_restriction1092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_predicate_constraint1125 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_form_in_predicate_constraint1131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_return_value_restriction1150 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_form_in_return_value_restriction1157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_variable_restriction1176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_literal_restriction1209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lisp_form1237 = new BitSet(new long[]{0x0000000000020020L});
    public static final BitSet FOLLOW_NAME_in_lisp_form1254 = new BitSet(new long[]{0x0000000007020A70L});
    public static final BitSet FOLLOW_VAR_in_lisp_form1274 = new BitSet(new long[]{0x0000000007020A70L});
    public static final BitSet FOLLOW_lisp_atom_in_lisp_form1297 = new BitSet(new long[]{0x0000000007020A70L});
    public static final BitSet FOLLOW_lisp_form_in_lisp_form1308 = new BitSet(new long[]{0x0000000007020A70L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lisp_form1335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_lisp_atom1379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_lisp_atom1391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_lisp_atom1413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_lisp_atom1425 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_lisp_atom1438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_lisp_atom1454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_lisp_atom1480 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal1524 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_literal1537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal1553 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal1568 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal1581 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal1595 = new BitSet(new long[]{0x0000000000000002L});

}