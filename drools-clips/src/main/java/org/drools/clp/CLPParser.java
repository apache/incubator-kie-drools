// $ANTLR 3.0.1 C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g 2008-01-20 20:25:48

	package org.drools.clp;
	
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
public class CLPParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LEFT_PAREN", "NAME", "RIGHT_PAREN", "DEFFUNCTION", "VAR", "DEFRULE", "STRING", "SALIENCE", "INT", "AND", "OR", "NOT", "EXISTS", "TEST", "ASSIGN_OP", "PIPE", "AMPERSAND", "TILDE", "COLON", "EQUALS", "FLOAT", "BOOL", "NULL", "EOL", "WS", "DECLARE", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SYMBOL", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "MULTI_LINE_COMMENT", "'import'", "'=>'"
    };
    public static final int RIGHT_SQUARE=38;
    public static final int RIGHT_CURLY=40;
    public static final int SYMBOL=34;
    public static final int NULL=26;
    public static final int BOOL=25;
    public static final int AMPERSAND=20;
    public static final int SALIENCE=11;
    public static final int FLOAT=24;
    public static final int EQUALS=23;
    public static final int INT=12;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=35;
    public static final int NOT=15;
    public static final int AND=13;
    public static final int EOF=-1;
    public static final int HexDigit=31;
    public static final int MULTI_LINE_COMMENT=41;
    public static final int DEFFUNCTION=7;
    public static final int COLON=22;
    public static final int ASSIGN_OP=18;
    public static final int RIGHT_PAREN=6;
    public static final int NAME=5;
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
    public static final int PIPE=19;
    public static final int VAR=8;
    public static final int EXISTS=16;
    public static final int LEFT_SQUARE=37;
    public static final int OctalEscape=33;
    public static final int EscapeSequence=30;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=36;
    public static final int STRING=10;

        public CLPParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[27+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g"; }


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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:226:1: eval_script[Shell shell] : (i= importDescr | r= defrule | fc= lisp_list[shell, new LispForm(shell) ] )* ;
    public final void eval_script(Shell  shell) throws RecognitionException {
        ImportDescr i = null;

        RuleDescr r = null;

        ValueHandler fc = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:227:2: ( (i= importDescr | r= defrule | fc= lisp_list[shell, new LispForm(shell) ] )* )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:227:4: (i= importDescr | r= defrule | fc= lisp_list[shell, new LispForm(shell) ] )*
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:227:4: (i= importDescr | r= defrule | fc= lisp_list[shell, new LispForm(shell) ] )*
            loop1:
            do {
                int alt1=4;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==LEFT_PAREN) ) {
                    switch ( input.LA(2) ) {
                    case DEFRULE:
                        {
                        alt1=2;
                        }
                        break;
                    case 42:
                        {
                        alt1=1;
                        }
                        break;
                    case LEFT_PAREN:
                    case NAME:
                    case RIGHT_PAREN:
                    case VAR:
                    case STRING:
                    case INT:
                    case FLOAT:
                    case BOOL:
                    case NULL:
                        {
                        alt1=3;
                        }
                        break;

                    }

                }


                switch (alt1) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:227:9: i= importDescr
            	    {
            	    pushFollow(FOLLOW_importDescr_in_eval_script55);
            	    i=importDescr();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       shell.importDescrHandler( i ); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:228:7: r= defrule
            	    {
            	    pushFollow(FOLLOW_defrule_in_eval_script66);
            	    r=defrule();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       shell.ruleDescrHandler( r ); 
            	    }

            	    }
            	    break;
            	case 3 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:230:7: fc= lisp_list[shell, new LispForm(shell) ]
            	    {
            	    pushFollow(FOLLOW_lisp_list_in_eval_script83);
            	    fc=lisp_list(shell,  new LispForm(shell) );
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       shell.lispFormHandler(fc); 
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
    // $ANTLR end eval_script


    // $ANTLR start importDescr
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:234:1: importDescr returns [ImportDescr importDescr] : LEFT_PAREN 'import' importName= NAME RIGHT_PAREN ;
    public final ImportDescr importDescr() throws RecognitionException {
        ImportDescr importDescr = null;

        Token importName=null;

        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:235:2: ( LEFT_PAREN 'import' importName= NAME RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:235:4: LEFT_PAREN 'import' importName= NAME RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_importDescr106); if (failed) return importDescr;
            match(input,42,FOLLOW_42_in_importDescr108); if (failed) return importDescr;
            importName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_importDescr112); if (failed) return importDescr;
            if ( backtracking==0 ) {
               importDescr = new ImportDescr( importName.getText() ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_importDescr115); if (failed) return importDescr;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:250:1: deffunction returns [Deffunction function] : loc= LEFT_PAREN DEFFUNCTION ruleName= NAME loc= LEFT_PAREN (v= VAR )* RIGHT_PAREN (fc= lisp_list[context, new LispForm(context) ] )* RIGHT_PAREN ;
    public final Deffunction deffunction() throws RecognitionException {
        Deffunction function = null;

        Token loc=null;
        Token ruleName=null;
        Token v=null;
        ValueHandler fc = null;



        			BuildContext context = null;  	
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:254:2: (loc= LEFT_PAREN DEFFUNCTION ruleName= NAME loc= LEFT_PAREN (v= VAR )* RIGHT_PAREN (fc= lisp_list[context, new LispForm(context) ] )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:254:4: loc= LEFT_PAREN DEFFUNCTION ruleName= NAME loc= LEFT_PAREN (v= VAR )* RIGHT_PAREN (fc= lisp_list[context, new LispForm(context) ] )* RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_deffunction141); if (failed) return function;
            match(input,DEFFUNCTION,FOLLOW_DEFFUNCTION_in_deffunction149); if (failed) return function;
            ruleName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_deffunction158); if (failed) return function;
            if ( backtracking==0 ) {

              	    	function = new Deffunction( ruleName.getText() );
              			functionRegistry.addFunction( function );
              	      	context = new ExecutionBuildContext( function, functionRegistry );
              	  	
            }
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_deffunction166); if (failed) return function;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:262:4: (v= VAR )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==VAR) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:262:5: v= VAR
            	    {
            	    v=(Token)input.LT(1);
            	    match(input,VAR,FOLLOW_VAR_in_deffunction176); if (failed) return function;
            	    if ( backtracking==0 ) {

            	      			context.addVariable( function.addParameter( v.getText() ) );
            	      		 
            	    }

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_deffunction189); if (failed) return function;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:266:5: (fc= lisp_list[context, new LispForm(context) ] )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==LEFT_PAREN) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:266:6: fc= lisp_list[context, new LispForm(context) ]
            	    {
            	    pushFollow(FOLLOW_lisp_list_in_deffunction198);
            	    fc=lisp_list(context,  new LispForm(context) );
            	    _fsp--;
            	    if (failed) return function;
            	    if ( backtracking==0 ) {
            	       context.addFunction( (FunctionCaller) fc ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_deffunction209); if (failed) return function;

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


    // $ANTLR start defrule
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:280:1: defrule returns [RuleDescr rule] : loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' engine= execution_block RIGHT_PAREN ;
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
                    Set declarations = null;  
        	      
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:288:2: (loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' engine= execution_block RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:288:4: loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' engine= execution_block RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_defrule235); if (failed) return rule;
            match(input,DEFRULE,FOLLOW_DEFRULE_in_defrule243); if (failed) return rule;
            ruleName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_defrule247); if (failed) return rule;
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
            match(input,STRING,FOLLOW_STRING_in_defrule259); if (failed) return rule;
            if ( backtracking==0 ) {

              	    	// do nothing here for now
              		
            }
            pushFollow(FOLLOW_ruleAttribute_in_defrule265);
            ruleAttribute(rule);
            _fsp--;
            if (failed) return rule;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:327:3: ( ce[lhs, declarations] )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==LEFT_PAREN||LA4_0==VAR) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:327:3: ce[lhs, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_defrule273);
            	    ce(lhs,  declarations);
            	    _fsp--;
            	    if (failed) return rule;

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            match(input,43,FOLLOW_43_in_defrule282); if (failed) return rule;
            pushFollow(FOLLOW_execution_block_in_defrule291);
            engine=execution_block();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               rule.setConsequence( engine ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_defrule300); if (failed) return rule;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:337:1: ruleAttribute[RuleDescr rule] : ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? ;
    public final void ruleAttribute(RuleDescr rule) throws RecognitionException {
        AttributeDescr d = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:338:2: ( ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:339:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:339:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
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
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:339:5: LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute317); if (failed) return ;
                    match(input,DECLARE,FOLLOW_DECLARE_in_ruleAttribute319); if (failed) return ;
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:340:4: ( LEFT_PAREN d= salience RIGHT_PAREN )?
                    int alt5=2;
                    int LA5_0 = input.LA(1);

                    if ( (LA5_0==LEFT_PAREN) ) {
                        alt5=1;
                    }
                    switch (alt5) {
                        case 1 :
                            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:340:6: LEFT_PAREN d= salience RIGHT_PAREN
                            {
                            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute326); if (failed) return ;
                            pushFollow(FOLLOW_salience_in_ruleAttribute330);
                            d=salience();
                            _fsp--;
                            if (failed) return ;
                            if ( backtracking==0 ) {
                               rule.addAttribute( d ); 
                            }
                            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute334); if (failed) return ;

                            }
                            break;

                    }

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute341); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:344:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;


        		d = null;
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:348:2: (loc= SALIENCE i= INT )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:349:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience371); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience375); if (failed) return d;
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:359:1: ce[ConditionalElementDescr in_ce, Set declarations] : ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] ) ;
    public final void ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:360:2: ( ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:360:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:360:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )
            int alt7=7;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
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
                case AND:
                    {
                    alt7=1;
                    }
                    break;
                case TEST:
                    {
                    alt7=5;
                    }
                    break;
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
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("360:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )", 7, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA7_0==VAR) ) {
                alt7=7;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("360:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:360:8: and_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_and_ce_in_ce401);
                    and_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:361:7: or_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_or_ce_in_ce411);
                    or_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:362:7: not_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_not_ce_in_ce420);
                    not_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:363:7: exists_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_exists_ce_in_ce429);
                    exists_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:364:8: eval_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_eval_ce_in_ce443);
                    eval_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:365:7: normal_pattern[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_ce457);
                    normal_pattern(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 7 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:366:7: bound_pattern[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_bound_pattern_in_ce466);
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


    // $ANTLR start execution_block
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:370:1: execution_block returns [ExecutionEngine engine] : (fc= lisp_list[context, new LispForm(context) ] )* ;
    public final ExecutionEngine execution_block() throws RecognitionException {
        ExecutionEngine engine = null;

        ValueHandler fc = null;



        	        engine = new BlockExecutionEngine();
        			BuildContext context = new ExecutionBuildContext( engine, functionRegistry );  	
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:376:2: ( (fc= lisp_list[context, new LispForm(context) ] )* )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:377:3: (fc= lisp_list[context, new LispForm(context) ] )*
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:377:3: (fc= lisp_list[context, new LispForm(context) ] )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==LEFT_PAREN) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:377:4: fc= lisp_list[context, new LispForm(context) ]
            	    {
            	    pushFollow(FOLLOW_lisp_list_in_execution_block498);
            	    fc=lisp_list(context,  new LispForm(context) );
            	    _fsp--;
            	    if (failed) return engine;
            	    if ( backtracking==0 ) {
            	       context.addFunction( (FunctionCaller) fc ); 
            	    }

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:380:1: and_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN ;
    public final void and_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {

                AndDescr andDescr= null;        
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:384:2: ( LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:384:4: LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_and_ce526); if (failed) return ;
            match(input,AND,FOLLOW_AND_in_and_ce531); if (failed) return ;
            if ( backtracking==0 ) {

              	    	andDescr = new AndDescr();
              			in_ce.addDescr( andDescr );
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:389:3: ( ce[andDescr, declarations] )+
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
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:389:3: ce[andDescr, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_and_ce537);
            	    ce(andDescr,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt9 >= 1 ) break loop9;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(9, input);
                        throw eee;
                }
                cnt9++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_and_ce546); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:393:1: or_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN ;
    public final void or_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {

                OrDescr orDescr= null;         
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:397:2: ( LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:397:4: LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_or_ce574); if (failed) return ;
            match(input,OR,FOLLOW_OR_in_or_ce579); if (failed) return ;
            if ( backtracking==0 ) {

              	    	orDescr = new OrDescr();
              			in_ce.addDescr( orDescr );
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:402:3: ( ce[orDescr, declarations] )+
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
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:402:3: ce[orDescr, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_or_ce585);
            	    ce(orDescr,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    if ( cnt10 >= 1 ) break loop10;
            	    if (backtracking>0) {failed=true; return ;}
                        EarlyExitException eee =
                            new EarlyExitException(10, input);
                        throw eee;
                }
                cnt10++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_or_ce594); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:406:1: not_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN ;
    public final void not_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {

                NotDescr notDescr= null;         
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:410:2: ( LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:410:4: LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_not_ce622); if (failed) return ;
            match(input,NOT,FOLLOW_NOT_in_not_ce627); if (failed) return ;
            if ( backtracking==0 ) {

              			notDescr = new NotDescr();
              		    in_ce.addDescr( notDescr );
              		
            }
            pushFollow(FOLLOW_ce_in_not_ce633);
            ce(notDescr,  declarations);
            _fsp--;
            if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_not_ce641); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:419:1: exists_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN ;
    public final void exists_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {

                ExistsDescr existsDescr= null;        
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:423:2: ( LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:423:4: LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_exists_ce670); if (failed) return ;
            match(input,EXISTS,FOLLOW_EXISTS_in_exists_ce675); if (failed) return ;
            if ( backtracking==0 ) {

              		    existsDescr = new ExistsDescr();
              		    in_ce.addDescr( existsDescr );
              		
            }
            pushFollow(FOLLOW_ce_in_exists_ce681);
            ce(existsDescr,  declarations);
            _fsp--;
            if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_exists_ce689); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:432:1: eval_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN TEST fc= lisp_list[context, new LispForm(context)] RIGHT_PAREN ;
    public final void eval_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        ValueHandler fc = null;



                EvalDescr evalDescr= null;    
           		ExecutionEngine engine = new CLPEval();     
        		BuildContext context = new ExecutionBuildContext( engine, functionRegistry );   		         
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:438:2: ( LEFT_PAREN TEST fc= lisp_list[context, new LispForm(context)] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:438:4: LEFT_PAREN TEST fc= lisp_list[context, new LispForm(context)] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_eval_ce717); if (failed) return ;
            match(input,TEST,FOLLOW_TEST_in_eval_ce722); if (failed) return ;
            if ( backtracking==0 ) {

              		    evalDescr = new EvalDescr();
              		    in_ce.addDescr( evalDescr );
              		
            }
            pushFollow(FOLLOW_lisp_list_in_eval_ce730);
            fc=lisp_list(context,  new LispForm(context));
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              					
              		    engine.addFunction( (FunctionCaller) fc );		
              			evalDescr.setContent( engine );			
              		
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_eval_ce741); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:450:1: normal_pattern[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN ;
    public final void normal_pattern(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        Token name=null;


                PatternDescr pattern = null;
                ConditionalElementDescr top = null;
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:455:2: ( LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:455:4: LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_normal_pattern769); if (failed) return ;
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_normal_pattern776); if (failed) return ;
            if ( backtracking==0 ) {

              			pattern = new PatternDescr(name.getText());
              			in_ce.addDescr( pattern );
              			top = pattern.getConstraint();
              			
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:462:3: ( field_constriant[top, declarations] )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==LEFT_PAREN) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:462:3: field_constriant[top, declarations]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_normal_pattern782);
            	    field_constriant(top,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_normal_pattern792); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:468:1: bound_pattern[ConditionalElementDescr in_ce, Set declarations] : var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN ;
    public final void bound_pattern(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        Token var=null;
        Token name=null;


                PatternDescr pattern = null;
                String identifier = null;
                ConditionalElementDescr top = null;        
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:474:2: (var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:474:4: var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_bound_pattern820); if (failed) return ;
            if ( backtracking==0 ) {

              			identifier = var.getText();
              		
            }
            match(input,ASSIGN_OP,FOLLOW_ASSIGN_OP_in_bound_pattern826); if (failed) return ;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_bound_pattern828); if (failed) return ;
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_bound_pattern832); if (failed) return ;
            if ( backtracking==0 ) {

              			pattern = new PatternDescr(name.getText());
              			pattern.setIdentifier( identifier );
              			in_ce.addDescr( pattern );
              			top = pattern.getConstraint();				    
              		
            }
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:484:3: ( field_constriant[top, declarations] )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==LEFT_PAREN) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:484:3: field_constriant[top, declarations]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_bound_pattern841);
            	    field_constriant(top,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_bound_pattern848); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:488:1: field_constriant[ConditionalElementDescr base, Set declarations] : LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN ;
    public final void field_constriant(ConditionalElementDescr base, Set declarations) throws RecognitionException {
        Token f=null;


             	List list = new ArrayList();
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		RestrictionConnectiveDescr top = null;		
        		String op = "==";
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:496:2: ( LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:497:3: LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_field_constriant879); if (failed) return ;
            f=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_field_constriant883); if (failed) return ;
            if ( backtracking==0 ) {

              			fc = new FieldConstraintDescr(f.getText());
              			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
              			base.addDescr( fc );	
              			top = fc.getRestriction();		
              		
            }
            pushFollow(FOLLOW_or_restr_connective_in_field_constriant898);
            or_restr_connective(top,  base,  fc,  declarations);
            _fsp--;
            if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_field_constriant904); if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:524:1: or_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] options {backtrack=true; } : and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )* ;
    public final void or_restr_connective(RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {

        		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:531:2: ( and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )* )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:532:3: and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )*
            {
            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective943);
            and_restr_connective(or,  ceBase,  fcBase,  declarations);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:533:3: ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==PIPE) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:535:6: PIPE and_restr_connective[or, ceBase, fcBase, declarations]
            	    {
            	    match(input,PIPE,FOLLOW_PIPE_in_or_restr_connective967); if (failed) return ;
            	    if ( backtracking==0 ) {

            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective977);
            	    and_restr_connective(or,  ceBase,  fcBase,  declarations);
            	    _fsp--;
            	    if (failed) return ;

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:550:1: and_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] : restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )* ;
    public final void and_restr_connective(RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {

        		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:554:2: ( restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )* )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:555:3: restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )*
            {
            pushFollow(FOLLOW_restriction_in_and_restr_connective1009);
            restriction(and,  ceBase,  fcBase,  declarations);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:556:3: ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==AMPERSAND) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:556:5: AMPERSAND restriction[and, ceBase, fcBase, declarations]
            	    {
            	    match(input,AMPERSAND,FOLLOW_AMPERSAND_in_and_restr_connective1017); if (failed) return ;
            	    pushFollow(FOLLOW_restriction_in_and_restr_connective1019);
            	    restriction(and,  ceBase,  fcBase,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop14;
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:575:1: restriction[RestrictionConnectiveDescr rc, ConditionalElementDescr base, FieldConstraintDescr fcBase, Set declarations ] : ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction ) ;
    public final void restriction(RestrictionConnectiveDescr rc, ConditionalElementDescr base, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        String lc = null;



        			String op = "==";
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:2: ( ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:4: ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:4: ( TILDE )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==TILDE) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:579:5: TILDE
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_restriction1052); if (failed) return ;
                    if ( backtracking==0 ) {
                      op = "!=";
                    }

                    }
                    break;

            }

            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:580:3: ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )
            int alt16=4;
            switch ( input.LA(1) ) {
            case COLON:
                {
                alt16=1;
                }
                break;
            case EQUALS:
                {
                alt16=2;
                }
                break;
            case VAR:
                {
                alt16=3;
                }
                break;
            case NAME:
            case STRING:
            case INT:
            case FLOAT:
            case BOOL:
            case NULL:
                {
                alt16=4;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("580:3: ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:580:5: predicate_constraint[rc, op, base]
                    {
                    pushFollow(FOLLOW_predicate_constraint_in_restriction1068);
                    predicate_constraint(rc,  op,  base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:581:7: return_value_restriction[op, rc]
                    {
                    pushFollow(FOLLOW_return_value_restriction_in_restriction1084);
                    return_value_restriction(op,  rc);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:582:7: variable_restriction[op, rc, base, fcBase, declarations]
                    {
                    pushFollow(FOLLOW_variable_restriction_in_restriction1093);
                    variable_restriction(op,  rc,  base,  fcBase,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:583:8: lc= literal_restriction
                    {
                    pushFollow(FOLLOW_literal_restriction_in_restriction1105);
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:590:1: predicate_constraint[RestrictionConnectiveDescr rc, String op, ConditionalElementDescr base] : COLON fc= lisp_list[context, new LispForm(context)] ;
    public final void predicate_constraint(RestrictionConnectiveDescr rc, String op, ConditionalElementDescr base) throws RecognitionException {
        ValueHandler fc = null;



           		ExecutionEngine engine = new CLPPredicate();
        		BuildContext context = new ExecutionBuildContext( engine, functionRegistry );    
            
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:2: ( COLON fc= lisp_list[context, new LispForm(context)] )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:595:4: COLON fc= lisp_list[context, new LispForm(context)]
            {
            match(input,COLON,FOLLOW_COLON_in_predicate_constraint1147); if (failed) return ;
            pushFollow(FOLLOW_lisp_list_in_predicate_constraint1153);
            fc=lisp_list(context,  new LispForm(context));
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              	
              		        engine.addFunction( (FunctionCaller) fc );
              			rc.addRestriction( new PredicateDescr( engine ) );
              		
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:604:1: return_value_restriction[String op, RestrictionConnectiveDescr rc] : EQUALS func= lisp_list[context, new LispForm(context)] ;
    public final void return_value_restriction(String op, RestrictionConnectiveDescr rc) throws RecognitionException {
        ValueHandler func = null;



        		ExecutionEngine engine = new CLPReturnValue();
        		BuildContext context = new ExecutionBuildContext( engine, functionRegistry );
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:609:2: ( EQUALS func= lisp_list[context, new LispForm(context)] )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:609:4: EQUALS func= lisp_list[context, new LispForm(context)]
            {
            match(input,EQUALS,FOLLOW_EQUALS_in_return_value_restriction1179); if (failed) return ;
            pushFollow(FOLLOW_lisp_list_in_return_value_restriction1186);
            func=lisp_list(context,  new LispForm(context));
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
              					
                 		    engine.addFunction( (FunctionCaller) func );
              			rc.addRestriction( new ReturnValueRestrictionDescr (op, engine ) );
              		
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:617:1: variable_restriction[String op, RestrictionConnectiveDescr rc, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] : VAR ;
    public final void variable_restriction(String op, RestrictionConnectiveDescr rc, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        Token VAR1=null;

        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:618:2: ( VAR )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:618:4: VAR
            {
            VAR1=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_variable_restriction1206); if (failed) return ;
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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:632:1: literal_restriction returns [String text] : t= literal ;
    public final String literal_restriction() throws RecognitionException {
        String text = null;

        String t = null;



        		text = null;
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:636:2: (t= literal )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:637:6: t= literal
            {
            pushFollow(FOLLOW_literal_in_literal_restriction1239);
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


    // $ANTLR start lisp_list
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:642:1: lisp_list[BuildContext context, LispList list] returns [ValueHandler valueHandler] : LEFT_PAREN (a= lisp_atom[context] | a= lisp_list[context, list.createList()] )* RIGHT_PAREN ;
    public final ValueHandler lisp_list(BuildContext context, LispList list) throws RecognitionException {
        ValueHandler valueHandler = null;

        ValueHandler a = null;


        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:643:2: ( LEFT_PAREN (a= lisp_atom[context] | a= lisp_list[context, list.createList()] )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:643:4: LEFT_PAREN (a= lisp_atom[context] | a= lisp_list[context, list.createList()] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lisp_list1258); if (failed) return valueHandler;
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:644:3: (a= lisp_atom[context] | a= lisp_list[context, list.createList()] )*
            loop17:
            do {
                int alt17=3;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==NAME||LA17_0==VAR||LA17_0==STRING||LA17_0==INT||(LA17_0>=FLOAT && LA17_0<=NULL)) ) {
                    alt17=1;
                }
                else if ( (LA17_0==LEFT_PAREN) ) {
                    alt17=2;
                }


                switch (alt17) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:644:6: a= lisp_atom[context]
            	    {
            	    pushFollow(FOLLOW_lisp_atom_in_lisp_list1268);
            	    a=lisp_atom(context);
            	    _fsp--;
            	    if (failed) return valueHandler;
            	    if ( backtracking==0 ) {
            	       list.add( a ); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:645:6: a= lisp_list[context, list.createList()]
            	    {
            	    pushFollow(FOLLOW_lisp_list_in_lisp_list1284);
            	    a=lisp_list(context,  list.createList());
            	    _fsp--;
            	    if (failed) return valueHandler;
            	    if ( backtracking==0 ) {
            	       list.add( a ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lisp_list1314); if (failed) return valueHandler;
            if ( backtracking==0 ) {
               valueHandler = list.getValueHandler(); 
            }

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
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:651:1: lisp_atom[BuildContext context] returns [ValueHandler value] : (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL ) ;
    public final ValueHandler lisp_atom(BuildContext context) throws RecognitionException {
        ValueHandler value = null;

        Token t=null;


        		value  =  null;		
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:655:2: ( (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:3: (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:3: (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL )
            int alt18=7;
            switch ( input.LA(1) ) {
            case VAR:
                {
                alt18=1;
                }
                break;
            case STRING:
                {
                alt18=2;
                }
                break;
            case NAME:
                {
                alt18=3;
                }
                break;
            case FLOAT:
                {
                alt18=4;
                }
                break;
            case INT:
                {
                alt18=5;
                }
                break;
            case BOOL:
                {
                alt18=6;
                }
                break;
            case NULL:
                {
                alt18=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return value;}
                NoViableAltException nvae =
                    new NoViableAltException("656:3: (t= VAR | t= STRING | t= NAME | t= FLOAT | t= INT | t= BOOL | t= NULL )", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:656:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_lisp_atom1351); if (failed) return value;
                    if ( backtracking==0 ) {
                       value = context.getVariableValueHandler(t.getText() ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:657:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_lisp_atom1363); if (failed) return value;
                    if ( backtracking==0 ) {
                       value = new ObjectValueHandler( getString( t ) ); 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:658:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_lisp_atom1375); if (failed) return value;
                    if ( backtracking==0 ) {
                       value = new ObjectValueHandler( t.getText() ); 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:659:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_lisp_atom1390); if (failed) return value;
                    if ( backtracking==0 ) {
                       value = new DoubleValueHandler( t.getText() ); 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:660:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_lisp_atom1402); if (failed) return value;
                    if ( backtracking==0 ) {
                       value = new LongValueHandler( t.getText() ); 
                    }

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:661:6: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lisp_atom1418); if (failed) return value;
                    if ( backtracking==0 ) {
                       value = new BooleanValueHandler( t.getText() ); 
                    }

                    }
                    break;
                case 7 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:662:6: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_lisp_atom1436); if (failed) return value;
                    if ( backtracking==0 ) {
                       value = ObjectValueHandler.NULL; 
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
        return value;
    }
    // $ANTLR end lisp_atom


    // $ANTLR start literal
    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:666:1: literal returns [String text] : (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final String literal() throws RecognitionException {
        String text = null;

        Token t=null;


        		text = null;
        	
        try {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:2: ( (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt19=6;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt19=1;
                }
                break;
            case NAME:
                {
                alt19=2;
                }
                break;
            case INT:
                {
                alt19=3;
                }
                break;
            case FLOAT:
                {
                alt19=4;
                }
                break;
            case BOOL:
                {
                alt19=5;
                }
                break;
            case NULL:
                {
                alt19=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("670:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:670:8: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal1472); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:671:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_literal1485); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:672:7: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal1501); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:673:7: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal1516); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:674:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal1529); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk\\drools-clips\\src\\main\\resources\\org\\drools\\clp\\CLP.g:675:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal1543); if (failed) return text;
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


 

    public static final BitSet FOLLOW_importDescr_in_eval_script55 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_defrule_in_eval_script66 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_lisp_list_in_eval_script83 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_importDescr106 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_importDescr108 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_importDescr112 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_importDescr115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_deffunction141 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_DEFFUNCTION_in_deffunction149 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_deffunction158 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_deffunction166 = new BitSet(new long[]{0x0000000000000140L});
    public static final BitSet FOLLOW_VAR_in_deffunction176 = new BitSet(new long[]{0x0000000000000140L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_deffunction189 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_lisp_list_in_deffunction198 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_deffunction209 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_defrule235 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_DEFRULE_in_defrule243 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_defrule247 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_STRING_in_defrule259 = new BitSet(new long[]{0x0000080000000110L});
    public static final BitSet FOLLOW_ruleAttribute_in_defrule265 = new BitSet(new long[]{0x0000080000000110L});
    public static final BitSet FOLLOW_ce_in_defrule273 = new BitSet(new long[]{0x0000080000000110L});
    public static final BitSet FOLLOW_43_in_defrule282 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_execution_block_in_defrule291 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_defrule300 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute317 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_DECLARE_in_ruleAttribute319 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute326 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_salience_in_ruleAttribute330 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute334 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience371 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_INT_in_salience375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_ce401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_ce411 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_ce420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_ce429 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_ce443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_ce457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bound_pattern_in_ce466 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lisp_list_in_execution_block498 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_and_ce526 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_AND_in_and_ce531 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_and_ce537 = new BitSet(new long[]{0x0000000000000150L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_and_ce546 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_or_ce574 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_OR_in_or_ce579 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_or_ce585 = new BitSet(new long[]{0x0000000000000150L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_or_ce594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_not_ce622 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_NOT_in_not_ce627 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_not_ce633 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_not_ce641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_exists_ce670 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_EXISTS_in_exists_ce675 = new BitSet(new long[]{0x0000000000000110L});
    public static final BitSet FOLLOW_ce_in_exists_ce681 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_exists_ce689 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_eval_ce717 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_TEST_in_eval_ce722 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_list_in_eval_ce730 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_eval_ce741 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_normal_pattern769 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_normal_pattern776 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_normal_pattern782 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_normal_pattern792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_bound_pattern820 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_ASSIGN_OP_in_bound_pattern826 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_bound_pattern828 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_bound_pattern832 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_bound_pattern841 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_bound_pattern848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_field_constriant879 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_field_constriant883 = new BitSet(new long[]{0x0000000007E01520L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constriant898 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_field_constriant904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective943 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_PIPE_in_or_restr_connective967 = new BitSet(new long[]{0x0000000007E01520L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective977 = new BitSet(new long[]{0x0000000000080002L});
    public static final BitSet FOLLOW_restriction_in_and_restr_connective1009 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_AMPERSAND_in_and_restr_connective1017 = new BitSet(new long[]{0x0000000007E01520L});
    public static final BitSet FOLLOW_restriction_in_and_restr_connective1019 = new BitSet(new long[]{0x0000000000100002L});
    public static final BitSet FOLLOW_TILDE_in_restriction1052 = new BitSet(new long[]{0x0000000007C01520L});
    public static final BitSet FOLLOW_predicate_constraint_in_restriction1068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_return_value_restriction_in_restriction1084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_restriction_in_restriction1093 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_restriction_in_restriction1105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_predicate_constraint1147 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_list_in_predicate_constraint1153 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_return_value_restriction1179 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_list_in_return_value_restriction1186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_variable_restriction1206 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_literal_restriction1239 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lisp_list1258 = new BitSet(new long[]{0x0000000007001570L});
    public static final BitSet FOLLOW_lisp_atom_in_lisp_list1268 = new BitSet(new long[]{0x0000000007001570L});
    public static final BitSet FOLLOW_lisp_list_in_lisp_list1284 = new BitSet(new long[]{0x0000000007001570L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lisp_list1314 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_lisp_atom1351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_lisp_atom1363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_lisp_atom1375 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_lisp_atom1390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_lisp_atom1402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_lisp_atom1418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_lisp_atom1436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal1472 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_literal1485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal1501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal1516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal1529 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal1543 = new BitSet(new long[]{0x0000000000000002L});

}