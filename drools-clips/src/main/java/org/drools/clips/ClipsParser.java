// $ANTLR 3.0.1 C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g 2008-06-24 01:45:58

	package org.drools.clips;

    import org.drools.clips.*;		
    
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
public class ClipsParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "LEFT_PAREN", "NAME", "RIGHT_PAREN", "DEFTEMPLATE", "STRING", "SLOT", "TYPE", "DEFFUNCTION", "DEFRULE", "SALIENCE", "INT", "AND", "OR", "NOT", "EXISTS", "TEST", "VAR", "ASSIGN_OP", "PIPE", "AMPERSAND", "TILDE", "COLON", "EQUALS", "FLOAT", "BOOL", "NULL", "EOL", "WS", "DECLARE", "EscapeSequence", "HexDigit", "UnicodeEscape", "OctalEscape", "SYMBOL_CHAR", "SH_STYLE_SINGLE_LINE_COMMENT", "C_STYLE_SINGLE_LINE_COMMENT", "LEFT_SQUARE", "RIGHT_SQUARE", "LEFT_CURLY", "RIGHT_CURLY", "MULTI_LINE_COMMENT", "SYMBOL", "FIRST_SYMBOL_CHAR", "'import'", "'=>'"
    };
    public static final int RIGHT_SQUARE=41;
    public static final int RIGHT_CURLY=43;
    public static final int EQUALS=26;
    public static final int FLOAT=27;
    public static final int NOT=17;
    public static final int SYMBOL_CHAR=37;
    public static final int SH_STYLE_SINGLE_LINE_COMMENT=38;
    public static final int AND=15;
    public static final int FIRST_SYMBOL_CHAR=46;
    public static final int EOF=-1;
    public static final int HexDigit=34;
    public static final int DEFFUNCTION=11;
    public static final int TYPE=10;
    public static final int ASSIGN_OP=21;
    public static final int RIGHT_PAREN=6;
    public static final int NAME=5;
    public static final int EOL=30;
    public static final int DEFRULE=12;
    public static final int TILDE=24;
    public static final int PIPE=22;
    public static final int VAR=20;
    public static final int EXISTS=18;
    public static final int SYMBOL=45;
    public static final int NULL=29;
    public static final int BOOL=28;
    public static final int SALIENCE=13;
    public static final int AMPERSAND=23;
    public static final int INT=14;
    public static final int MULTI_LINE_COMMENT=44;
    public static final int COLON=25;
    public static final int WS=31;
    public static final int UnicodeEscape=35;
    public static final int SLOT=9;
    public static final int LEFT_CURLY=42;
    public static final int OR=16;
    public static final int TEST=19;
    public static final int LEFT_PAREN=4;
    public static final int DECLARE=32;
    public static final int DEFTEMPLATE=7;
    public static final int LEFT_SQUARE=40;
    public static final int EscapeSequence=33;
    public static final int OctalEscape=36;
    public static final int C_STYLE_SINGLE_LINE_COMMENT=39;
    public static final int STRING=8;

        public ClipsParser(TokenStream input) {
            super(input);
            ruleMemo = new HashMap[29+1];
         }
        

    public String[] getTokenNames() { return tokenNames; }
    public String getGrammarFileName() { return "C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g"; }

    
    	private PackageDescr packageDescr;
    	private List errors = new ArrayList();
    	private String source = "unknown";
    	private int lineOffset = 0;
    	private DescrFactory factory = new DescrFactory();
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:222:1: eval[ParserHandler handler] : (i= importDescr | f= deffunction | t= deftemplate | r= defrule | form= lisp_form )* ;
    public final void eval(ParserHandler handler) throws RecognitionException {
        ImportDescr i = null;

        FunctionDescr f = null;

        TypeDeclarationDescr t = null;

        RuleDescr r = null;

        LispForm form = null;


        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:223:2: ( (i= importDescr | f= deffunction | t= deftemplate | r= defrule | form= lisp_form )* )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:224:5: (i= importDescr | f= deffunction | t= deftemplate | r= defrule | form= lisp_form )*
            {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:224:5: (i= importDescr | f= deffunction | t= deftemplate | r= defrule | form= lisp_form )*
            loop1:
            do {
                int alt1=6;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==LEFT_PAREN) ) {
                    switch ( input.LA(2) ) {
                    case 47:
                        {
                        alt1=1;
                        }
                        break;
                    case DEFRULE:
                        {
                        alt1=4;
                        }
                        break;
                    case DEFTEMPLATE:
                        {
                        alt1=3;
                        }
                        break;
                    case DEFFUNCTION:
                        {
                        alt1=2;
                        }
                        break;
                    case NAME:
                    case VAR:
                        {
                        alt1=5;
                        }
                        break;

                    }

                }


                switch (alt1) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:224:10: i= importDescr
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
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:225:7: f= deffunction
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
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:226:7: t= deftemplate
            	    {
            	    pushFollow(FOLLOW_deftemplate_in_eval84);
            	    t=deftemplate();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       handler.templateHandler( t ); 
            	    }

            	    }
            	    break;
            	case 4 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:227:7: r= defrule
            	    {
            	    pushFollow(FOLLOW_defrule_in_eval100);
            	    r=defrule();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       handler.ruleHandler( r ); 
            	    }

            	    }
            	    break;
            	case 5 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:228:7: form= lisp_form
            	    {
            	    pushFollow(FOLLOW_lisp_form_in_eval112);
            	    form=lisp_form();
            	    _fsp--;
            	    if (failed) return ;
            	    if ( backtracking==0 ) {
            	       handler.lispFormHandler( form ); 
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
    // $ANTLR end eval


    // $ANTLR start importDescr
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:245:1: importDescr returns [ImportDescr importDescr] : LEFT_PAREN 'import' importName= NAME RIGHT_PAREN ;
    public final ImportDescr importDescr() throws RecognitionException {
        ImportDescr importDescr = null;

        Token importName=null;

        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:246:2: ( LEFT_PAREN 'import' importName= NAME RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:246:4: LEFT_PAREN 'import' importName= NAME RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_importDescr140); if (failed) return importDescr;
            match(input,47,FOLLOW_47_in_importDescr142); if (failed) return importDescr;
            importName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_importDescr146); if (failed) return importDescr;
            if ( backtracking==0 ) {
               importDescr = new ImportDescr( importName.getText() ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_importDescr150); if (failed) return importDescr;

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


    // $ANTLR start deftemplate
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:295:1: deftemplate returns [TypeDeclarationDescr typeDescr] : loc= LEFT_PAREN DEFTEMPLATE deftemplateName= NAME documentation= STRING ( deftemplate_slot[typeDescr] )* RIGHT_PAREN ;
    public final TypeDeclarationDescr deftemplate() throws RecognitionException {
        TypeDeclarationDescr typeDescr = null;

        Token loc=null;
        Token deftemplateName=null;
        Token documentation=null;

        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:296:5: (loc= LEFT_PAREN DEFTEMPLATE deftemplateName= NAME documentation= STRING ( deftemplate_slot[typeDescr] )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:297:5: loc= LEFT_PAREN DEFTEMPLATE deftemplateName= NAME documentation= STRING ( deftemplate_slot[typeDescr] )* RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_deftemplate185); if (failed) return typeDescr;
            match(input,DEFTEMPLATE,FOLLOW_DEFTEMPLATE_in_deftemplate192); if (failed) return typeDescr;
            deftemplateName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_deftemplate196); if (failed) return typeDescr;
            if ( backtracking==0 ) {
               	  			  		
              	  		debug( "start rule: " + deftemplateName.getText() );
              	  		String templateStr = deftemplateName.getText();
              
                          String mod = null;
              	        if ( templateStr.indexOf("::") >= 0 ) {
                              mod = templateStr.substring(0, templateStr.indexOf("::"));
              	            templateStr = templateStr.substring(templateStr.indexOf("::")+2);
              			}		    
              		    
              		    typeDescr = new TypeDeclarationDescr( templateStr );
              		    if( mod != null ) {
              		        typeDescr.setNamespace( mod );
              		    }		    
              		        
              			typeDescr.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
              			typeDescr.setStartCharacter( ((CommonToken)loc).getStartIndex() ); 
              		
              											
              		
            }
            documentation=(Token)input.LT(1);
            match(input,STRING,FOLLOW_STRING_in_deftemplate207); if (failed) return typeDescr;
            if ( backtracking==0 ) {
              
              		// do nothing here for now
              	
            }
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:322:4: ( deftemplate_slot[typeDescr] )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==LEFT_PAREN) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:322:4: deftemplate_slot[typeDescr]
            	    {
            	    pushFollow(FOLLOW_deftemplate_slot_in_deftemplate220);
            	    deftemplate_slot(typeDescr);
            	    _fsp--;
            	    if (failed) return typeDescr;

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_deftemplate228); if (failed) return typeDescr;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return typeDescr;
    }
    // $ANTLR end deftemplate


    // $ANTLR start deftemplate_slot
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:326:1: deftemplate_slot[TypeDeclarationDescr typeDescr] : LEFT_PAREN SLOT slotName= NAME LEFT_PAREN TYPE slotType= NAME RIGHT_PAREN RIGHT_PAREN ;
    public final void deftemplate_slot(TypeDeclarationDescr typeDescr) throws RecognitionException {
        Token slotName=null;
        Token slotType=null;

        
             
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:329:6: ( LEFT_PAREN SLOT slotName= NAME LEFT_PAREN TYPE slotType= NAME RIGHT_PAREN RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:330:5: LEFT_PAREN SLOT slotName= NAME LEFT_PAREN TYPE slotType= NAME RIGHT_PAREN RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_deftemplate_slot267); if (failed) return ;
            match(input,SLOT,FOLLOW_SLOT_in_deftemplate_slot279); if (failed) return ;
            slotName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_deftemplate_slot283); if (failed) return ;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_deftemplate_slot293); if (failed) return ;
            match(input,TYPE,FOLLOW_TYPE_in_deftemplate_slot309); if (failed) return ;
            slotType=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_deftemplate_slot313); if (failed) return ;
            if ( backtracking==0 ) {
              
                          typeDescr.addField( new TypeFieldDescr(slotName.getText(), new PatternDescr( slotType.getText() ) ) );
                      
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_deftemplate_slot333); if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_deftemplate_slot340); if (failed) return ;

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
    // $ANTLR end deftemplate_slot


    // $ANTLR start deffunction
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:341:1: deffunction returns [FunctionDescr functionDescr] : LEFT_PAREN t= DEFFUNCTION name= lisp_atom params= lisp_form (form= lisp_form )+ RIGHT_PAREN ;
    public final FunctionDescr deffunction() throws RecognitionException {
        FunctionDescr functionDescr = null;

        Token t=null;
        SExpression name = null;

        LispForm params = null;

        LispForm form = null;


        
                List content = null;
                functionDescr = null;
            
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:346:2: ( LEFT_PAREN t= DEFFUNCTION name= lisp_atom params= lisp_form (form= lisp_form )+ RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:346:4: LEFT_PAREN t= DEFFUNCTION name= lisp_atom params= lisp_form (form= lisp_form )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_deffunction375); if (failed) return functionDescr;
            t=(Token)input.LT(1);
            match(input,DEFFUNCTION,FOLLOW_DEFFUNCTION_in_deffunction385); if (failed) return functionDescr;
            pushFollow(FOLLOW_lisp_atom_in_deffunction395);
            name=lisp_atom();
            _fsp--;
            if (failed) return functionDescr;
            pushFollow(FOLLOW_lisp_form_in_deffunction405);
            params=lisp_form();
            _fsp--;
            if (failed) return functionDescr;
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:350:3: (form= lisp_form )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==LEFT_PAREN) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:350:4: form= lisp_form
            	    {
            	    pushFollow(FOLLOW_lisp_form_in_deffunction414);
            	    form=lisp_form();
            	    _fsp--;
            	    if (failed) return functionDescr;
            	    if ( backtracking==0 ) {
            	       if ( content == null ) content = new ArrayList(); content.add( form ); 
            	    }

            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
            	    if (backtracking>0) {failed=true; return functionDescr;}
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_deffunction436); if (failed) return functionDescr;
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:356:1: defrule returns [RuleDescr rule] : loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' list= rule_consequence RIGHT_PAREN ;
    public final RuleDescr defrule() throws RecognitionException {
        RuleDescr rule = null;

        Token loc=null;
        Token ruleName=null;
        Token documentation=null;
        List list = null;


         
        	        rule = null; 
        	        AndDescr lhs = null;
        	        PatternDescr colum = null;  
                    Set declarations = null;  
        	      
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:363:2: (loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' list= rule_consequence RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:363:4: loc= LEFT_PAREN DEFRULE ruleName= NAME documentation= STRING ruleAttribute[rule] ( ce[lhs, declarations] )* '=>' list= rule_consequence RIGHT_PAREN
            {
            loc=(Token)input.LT(1);
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_defrule473); if (failed) return rule;
            match(input,DEFRULE,FOLLOW_DEFRULE_in_defrule481); if (failed) return rule;
            ruleName=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_defrule485); if (failed) return rule;
            if ( backtracking==0 ) {
               	  			  		
              	  		debug( "start rule: " + ruleName.getText() );
              	  		String ruleStr = ruleName.getText();
              	  		AttributeDescr module = null;
              
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
              		        rule.setNamespace( module.getValue() );
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
            match(input,STRING,FOLLOW_STRING_in_defrule497); if (failed) return rule;
            if ( backtracking==0 ) {
              
              	    	// do nothing here for now
              		
            }
            pushFollow(FOLLOW_ruleAttribute_in_defrule503);
            ruleAttribute(rule);
            _fsp--;
            if (failed) return rule;
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:404:3: ( ce[lhs, declarations] )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==LEFT_PAREN||LA4_0==VAR) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:404:3: ce[lhs, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_defrule511);
            	    ce(lhs,  declarations);
            	    _fsp--;
            	    if (failed) return rule;

            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);

            match(input,48,FOLLOW_48_in_defrule520); if (failed) return rule;
            pushFollow(FOLLOW_rule_consequence_in_defrule529);
            list=rule_consequence();
            _fsp--;
            if (failed) return rule;
            if ( backtracking==0 ) {
               rule.setConsequence( list ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_defrule537); if (failed) return rule;

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


    // $ANTLR start rule_consequence
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:413:1: rule_consequence returns [List list] : (l= lisp_form )* ;
    public final List rule_consequence() throws RecognitionException {
        List list = null;

        LispForm l = null;


        
                list = null;
            
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:417:5: ( (l= lisp_form )* )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:418:3: (l= lisp_form )*
            {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:418:3: (l= lisp_form )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==LEFT_PAREN) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:418:4: l= lisp_form
            	    {
            	    pushFollow(FOLLOW_lisp_form_in_rule_consequence569);
            	    l=lisp_form();
            	    _fsp--;
            	    if (failed) return list;
            	    if ( backtracking==0 ) {
            	       if ( list == null ) list = new ArrayList(); list.add( l ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop5;
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
    // $ANTLR end rule_consequence


    // $ANTLR start ruleAttribute
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:421:1: ruleAttribute[RuleDescr rule] : ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? ;
    public final void ruleAttribute(RuleDescr rule) throws RecognitionException {
        AttributeDescr d = null;


        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:422:2: ( ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )? )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:423:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
            {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:423:3: ( LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==LEFT_PAREN) ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1==DECLARE) ) {
                    alt7=1;
                }
            }
            switch (alt7) {
                case 1 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:423:5: LEFT_PAREN 'declare' ( LEFT_PAREN d= salience RIGHT_PAREN )? RIGHT_PAREN
                    {
                    match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute602); if (failed) return ;
                    match(input,DECLARE,FOLLOW_DECLARE_in_ruleAttribute604); if (failed) return ;
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:424:4: ( LEFT_PAREN d= salience RIGHT_PAREN )?
                    int alt6=2;
                    int LA6_0 = input.LA(1);

                    if ( (LA6_0==LEFT_PAREN) ) {
                        alt6=1;
                    }
                    switch (alt6) {
                        case 1 :
                            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:424:6: LEFT_PAREN d= salience RIGHT_PAREN
                            {
                            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_ruleAttribute611); if (failed) return ;
                            pushFollow(FOLLOW_salience_in_ruleAttribute615);
                            d=salience();
                            _fsp--;
                            if (failed) return ;
                            if ( backtracking==0 ) {
                               rule.addAttribute( d ); 
                            }
                            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute619); if (failed) return ;

                            }
                            break;

                    }

                    match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_ruleAttribute626); if (failed) return ;

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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:428:1: salience returns [AttributeDescr d ] : loc= SALIENCE i= INT ;
    public final AttributeDescr salience() throws RecognitionException {
        AttributeDescr d = null;

        Token loc=null;
        Token i=null;

        
        		d = null;
        	
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:432:2: (loc= SALIENCE i= INT )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:433:3: loc= SALIENCE i= INT
            {
            loc=(Token)input.LT(1);
            match(input,SALIENCE,FOLLOW_SALIENCE_in_salience656); if (failed) return d;
            i=(Token)input.LT(1);
            match(input,INT,FOLLOW_INT_in_salience660); if (failed) return d;
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:443:1: ce[ConditionalElementDescr in_ce, Set declarations] : ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] ) ;
    public final void ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:444:2: ( ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] ) )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:444:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )
            {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:444:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )
            int alt8=7;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==LEFT_PAREN) ) {
                switch ( input.LA(2) ) {
                case AND:
                    {
                    alt8=1;
                    }
                    break;
                case TEST:
                    {
                    alt8=5;
                    }
                    break;
                case EXISTS:
                    {
                    alt8=4;
                    }
                    break;
                case NOT:
                    {
                    alt8=3;
                    }
                    break;
                case OR:
                    {
                    alt8=2;
                    }
                    break;
                case NAME:
                    {
                    alt8=6;
                    }
                    break;
                default:
                    if (backtracking>0) {failed=true; return ;}
                    NoViableAltException nvae =
                        new NoViableAltException("444:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )", 8, 1, input);

                    throw nvae;
                }

            }
            else if ( (LA8_0==VAR) ) {
                alt8=7;
            }
            else {
                if (backtracking>0) {failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("444:4: ( and_ce[in_ce, declarations] | or_ce[in_ce, declarations] | not_ce[in_ce, declarations] | exists_ce[in_ce, declarations] | eval_ce[in_ce, declarations] | normal_pattern[in_ce, declarations] | bound_pattern[in_ce, declarations] )", 8, 0, input);

                throw nvae;
            }
            switch (alt8) {
                case 1 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:444:8: and_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_and_ce_in_ce686);
                    and_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:445:7: or_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_or_ce_in_ce696);
                    or_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:446:7: not_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_not_ce_in_ce705);
                    not_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:447:7: exists_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_exists_ce_in_ce714);
                    exists_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:448:8: eval_ce[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_eval_ce_in_ce728);
                    eval_ce(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:449:7: normal_pattern[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_normal_pattern_in_ce742);
                    normal_pattern(in_ce,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 7 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:450:7: bound_pattern[in_ce, declarations]
                    {
                    pushFollow(FOLLOW_bound_pattern_in_ce751);
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:454:1: and_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN ;
    public final void and_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                AndDescr andDescr= null;        
            
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:458:2: ( LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:458:4: LEFT_PAREN AND ( ce[andDescr, declarations] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_and_ce778); if (failed) return ;
            match(input,AND,FOLLOW_AND_in_and_ce783); if (failed) return ;
            if ( backtracking==0 ) {
              
              	    	andDescr = new AndDescr();
              			in_ce.addDescr( andDescr );
              		
            }
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:463:3: ( ce[andDescr, declarations] )+
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
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:463:3: ce[andDescr, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_and_ce789);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_and_ce798); if (failed) return ;

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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:467:1: or_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN ;
    public final void or_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                OrDescr orDescr= null;         
            
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:471:2: ( LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:471:4: LEFT_PAREN OR ( ce[orDescr, declarations] )+ RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_or_ce826); if (failed) return ;
            match(input,OR,FOLLOW_OR_in_or_ce831); if (failed) return ;
            if ( backtracking==0 ) {
              
              	    	orDescr = new OrDescr();
              			in_ce.addDescr( orDescr );
              		
            }
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:476:3: ( ce[orDescr, declarations] )+
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
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:476:3: ce[orDescr, declarations]
            	    {
            	    pushFollow(FOLLOW_ce_in_or_ce837);
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

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_or_ce846); if (failed) return ;

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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:480:1: not_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN ;
    public final void not_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                NotDescr notDescr= null;         
            
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:484:2: ( LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:484:4: LEFT_PAREN NOT ce[notDescr, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_not_ce874); if (failed) return ;
            match(input,NOT,FOLLOW_NOT_in_not_ce879); if (failed) return ;
            if ( backtracking==0 ) {
              
              			notDescr = new NotDescr();
              		    in_ce.addDescr( notDescr );
              		
            }
            pushFollow(FOLLOW_ce_in_not_ce885);
            ce(notDescr,  declarations);
            _fsp--;
            if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_not_ce893); if (failed) return ;

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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:493:1: exists_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN ;
    public final void exists_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        
                ExistsDescr existsDescr= null;        
            
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:497:2: ( LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:497:4: LEFT_PAREN EXISTS ce[existsDescr, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_exists_ce922); if (failed) return ;
            match(input,EXISTS,FOLLOW_EXISTS_in_exists_ce927); if (failed) return ;
            if ( backtracking==0 ) {
              
              		    existsDescr = new ExistsDescr();
              		    in_ce.addDescr( existsDescr );
              		
            }
            pushFollow(FOLLOW_ce_in_exists_ce933);
            ce(existsDescr,  declarations);
            _fsp--;
            if (failed) return ;
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_exists_ce941); if (failed) return ;

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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:506:1: eval_ce[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN TEST t= lisp_form RIGHT_PAREN ;
    public final void eval_ce(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        LispForm t = null;


        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:507:2: ( LEFT_PAREN TEST t= lisp_form RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:507:4: LEFT_PAREN TEST t= lisp_form RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_eval_ce960); if (failed) return ;
            match(input,TEST,FOLLOW_TEST_in_eval_ce965); if (failed) return ;
            pushFollow(FOLLOW_lisp_form_in_eval_ce972);
            t=lisp_form();
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               EvalDescr evalDescr = new EvalDescr(); evalDescr.setContent( t ); in_ce.addDescr( evalDescr ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_eval_ce982); if (failed) return ;

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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:513:1: normal_pattern[ConditionalElementDescr in_ce, Set declarations] : LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN ;
    public final void normal_pattern(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        Token name=null;

        
                PatternDescr pattern = null;
                ConditionalElementDescr top = null;
            
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:518:2: ( LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:518:4: LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_normal_pattern1010); if (failed) return ;
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_normal_pattern1017); if (failed) return ;
            if ( backtracking==0 ) {
              
              			pattern = new PatternDescr(name.getText());
              			in_ce.addDescr( pattern );
              			top = pattern.getConstraint();
              			
              		
            }
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:525:3: ( field_constriant[top, declarations] )*
            loop11:
            do {
                int alt11=2;
                int LA11_0 = input.LA(1);

                if ( (LA11_0==LEFT_PAREN) ) {
                    alt11=1;
                }


                switch (alt11) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:525:3: field_constriant[top, declarations]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_normal_pattern1023);
            	    field_constriant(top,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop11;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_normal_pattern1033); if (failed) return ;

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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:531:1: bound_pattern[ConditionalElementDescr in_ce, Set declarations] : var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN ;
    public final void bound_pattern(ConditionalElementDescr in_ce, Set declarations) throws RecognitionException {
        Token var=null;
        Token name=null;

        
                PatternDescr pattern = null;
                String identifier = null;
                ConditionalElementDescr top = null;        
            
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:537:2: (var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:537:4: var= VAR ASSIGN_OP LEFT_PAREN name= NAME ( field_constriant[top, declarations] )* RIGHT_PAREN
            {
            var=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_bound_pattern1061); if (failed) return ;
            if ( backtracking==0 ) {
              
              			identifier = var.getText();
              		
            }
            match(input,ASSIGN_OP,FOLLOW_ASSIGN_OP_in_bound_pattern1067); if (failed) return ;
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_bound_pattern1069); if (failed) return ;
            name=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_bound_pattern1073); if (failed) return ;
            if ( backtracking==0 ) {
              
              			pattern = new PatternDescr(name.getText());
              			pattern.setIdentifier( identifier.replace( '?', '$') );
              			in_ce.addDescr( pattern );
              			top = pattern.getConstraint();				    
              		
            }
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:547:3: ( field_constriant[top, declarations] )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==LEFT_PAREN) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:547:3: field_constriant[top, declarations]
            	    {
            	    pushFollow(FOLLOW_field_constriant_in_bound_pattern1082);
            	    field_constriant(top,  declarations);
            	    _fsp--;
            	    if (failed) return ;

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_bound_pattern1089); if (failed) return ;

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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:551:1: field_constriant[ConditionalElementDescr base, Set declarations] : LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN ;
    public final void field_constriant(ConditionalElementDescr base, Set declarations) throws RecognitionException {
        Token f=null;

        
             	List list = new ArrayList();
        		FieldBindingDescr fbd = null;
        		FieldConstraintDescr fc = null;
        		RestrictionConnectiveDescr top = null;		
        		String op = "==";
        	
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:559:2: ( LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:560:3: LEFT_PAREN f= NAME or_restr_connective[top, base, fc, declarations] RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_field_constriant1120); if (failed) return ;
            f=(Token)input.LT(1);
            match(input,NAME,FOLLOW_NAME_in_field_constriant1124); if (failed) return ;
            if ( backtracking==0 ) {
              
              			fc = new FieldConstraintDescr(f.getText());
              			fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
              			fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
              			//base.addDescr( fc );    
              			top = fc.getRestriction();		
              		
            }
            pushFollow(FOLLOW_or_restr_connective_in_field_constriant1139);
            or_restr_connective(top,  base,  fc,  declarations);
            _fsp--;
            if (failed) return ;
            if ( backtracking==0 ) {
               if ( top.getRestrictions().size() != 0 ) base.addDescr( fc ); 
            }
            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_field_constriant1149); if (failed) return ;

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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:588:1: or_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] options {backtrack=true; } : and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )* ;
    public final void or_restr_connective(RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        
        		RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
        	
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:595:2: ( and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )* )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:596:3: and_restr_connective[or, ceBase, fcBase, declarations] ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )*
            {
            pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective1188);
            and_restr_connective(or,  ceBase,  fcBase,  declarations);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:597:3: ( options {backtrack=true; } : PIPE and_restr_connective[or, ceBase, fcBase, declarations] )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==PIPE) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:599:6: PIPE and_restr_connective[or, ceBase, fcBase, declarations]
            	    {
            	    match(input,PIPE,FOLLOW_PIPE_in_or_restr_connective1212); if (failed) return ;
            	    if ( backtracking==0 ) {
            	      
            	      				location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            	      			
            	    }
            	    pushFollow(FOLLOW_and_restr_connective_in_or_restr_connective1222);
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:614:1: and_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] : restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )* ;
    public final void and_restr_connective(RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        
        		RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
        	
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:618:2: ( restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )* )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:619:3: restriction[and, ceBase, fcBase, declarations] ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )*
            {
            pushFollow(FOLLOW_restriction_in_and_restr_connective1254);
            restriction(and,  ceBase,  fcBase,  declarations);
            _fsp--;
            if (failed) return ;
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:620:3: ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==AMPERSAND) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:620:5: AMPERSAND restriction[and, ceBase, fcBase, declarations]
            	    {
            	    match(input,AMPERSAND,FOLLOW_AMPERSAND_in_and_restr_connective1262); if (failed) return ;
            	    pushFollow(FOLLOW_restriction_in_and_restr_connective1264);
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:639:1: restriction[RestrictionConnectiveDescr rc, ConditionalElementDescr base, FieldConstraintDescr fcBase, Set declarations ] : ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction ) ;
    public final void restriction(RestrictionConnectiveDescr rc, ConditionalElementDescr base, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        String lc = null;


        
        			String op = "==";
        	
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:643:2: ( ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction ) )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:643:4: ( TILDE )? ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )
            {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:643:4: ( TILDE )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==TILDE) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:643:5: TILDE
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_restriction1297); if (failed) return ;
                    if ( backtracking==0 ) {
                      op = "!=";
                    }

                    }
                    break;

            }

            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:644:3: ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )
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
                    new NoViableAltException("644:3: ( predicate_constraint[rc, op, base] | return_value_restriction[op, rc] | variable_restriction[op, rc, base, fcBase, declarations] | lc= literal_restriction )", 16, 0, input);

                throw nvae;
            }

            switch (alt16) {
                case 1 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:644:5: predicate_constraint[rc, op, base]
                    {
                    pushFollow(FOLLOW_predicate_constraint_in_restriction1313);
                    predicate_constraint(rc,  op,  base);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:645:7: return_value_restriction[op, rc]
                    {
                    pushFollow(FOLLOW_return_value_restriction_in_restriction1329);
                    return_value_restriction(op,  rc);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:646:7: variable_restriction[op, rc, base, fcBase, declarations]
                    {
                    pushFollow(FOLLOW_variable_restriction_in_restriction1338);
                    variable_restriction(op,  rc,  base,  fcBase,  declarations);
                    _fsp--;
                    if (failed) return ;

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:647:8: lc= literal_restriction
                    {
                    pushFollow(FOLLOW_literal_restriction_in_restriction1350);
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:654:1: predicate_constraint[RestrictionConnectiveDescr rc, String op, ConditionalElementDescr base] : COLON t= lisp_form ;
    public final void predicate_constraint(RestrictionConnectiveDescr rc, String op, ConditionalElementDescr base) throws RecognitionException {
        LispForm t = null;


        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:655:2: ( COLON t= lisp_form )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:655:4: COLON t= lisp_form
            {
            match(input,COLON,FOLLOW_COLON_in_predicate_constraint1383); if (failed) return ;
            pushFollow(FOLLOW_lisp_form_in_predicate_constraint1389);
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:661:1: return_value_restriction[String op, RestrictionConnectiveDescr rc] : EQUALS t= lisp_form ;
    public final void return_value_restriction(String op, RestrictionConnectiveDescr rc) throws RecognitionException {
        LispForm t = null;


        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:662:2: ( EQUALS t= lisp_form )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:662:4: EQUALS t= lisp_form
            {
            match(input,EQUALS,FOLLOW_EQUALS_in_return_value_restriction1408); if (failed) return ;
            pushFollow(FOLLOW_lisp_form_in_return_value_restriction1415);
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:667:1: variable_restriction[String op, RestrictionConnectiveDescr rc, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ] : VAR ;
    public final void variable_restriction(String op, RestrictionConnectiveDescr rc, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations) throws RecognitionException {
        Token VAR1=null;

         String identifier = null;
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:669:2: ( VAR )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:669:4: VAR
            {
            VAR1=(Token)input.LT(1);
            match(input,VAR,FOLLOW_VAR_in_variable_restriction1443); if (failed) return ;
            if ( backtracking==0 ) {
              
              	        identifier =  VAR1.getText().replace( '?', '$');
              	        if ( declarations.contains( identifier) ) {
              				rc.addRestriction( new VariableRestrictionDescr(op, identifier ) );
              		 	} else {
              		 		FieldBindingDescr fbd = new FieldBindingDescr();
              		 		fbd.setIdentifier( identifier );		
              		 		fbd.setFieldName( fcBase.getFieldName() ); 		
              		 		ceBase.insertBeforeLast( FieldConstraintDescr.class, fbd );
              		 		declarations.add( identifier );
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:684:1: literal_restriction returns [String text] : t= literal ;
    public final String literal_restriction() throws RecognitionException {
        String text = null;

        String t = null;


        
        		text = null;
        	
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:688:2: (t= literal )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:689:6: t= literal
            {
            pushFollow(FOLLOW_literal_in_literal_restriction1476);
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:706:1: lisp_form returns [LispForm lispForm] : LEFT_PAREN (t= NAME | t= VAR ) (a= lisp_atom | l= lisp_form )* RIGHT_PAREN ;
    public final LispForm lisp_form() throws RecognitionException {
        LispForm lispForm = null;

        Token t=null;
        SExpression a = null;

        LispForm l = null;


        
                List list = new ArrayList();
                lispForm = null;
            
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:711:2: ( LEFT_PAREN (t= NAME | t= VAR ) (a= lisp_atom | l= lisp_form )* RIGHT_PAREN )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:711:4: LEFT_PAREN (t= NAME | t= VAR ) (a= lisp_atom | l= lisp_form )* RIGHT_PAREN
            {
            match(input,LEFT_PAREN,FOLLOW_LEFT_PAREN_in_lisp_form1504); if (failed) return lispForm;
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:713:3: (t= NAME | t= VAR )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==NAME) ) {
                alt17=1;
            }
            else if ( (LA17_0==VAR) ) {
                alt17=2;
            }
            else {
                if (backtracking>0) {failed=true; return lispForm;}
                NoViableAltException nvae =
                    new NoViableAltException("713:3: (t= NAME | t= VAR )", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:714:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_lisp_form1521); if (failed) return lispForm;
                    if ( backtracking==0 ) {
                       list.add( new SymbolLispAtom( t.getText() ) ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:716:7: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_lisp_form1541); if (failed) return lispForm;
                    if ( backtracking==0 ) {
                       list.add( new VariableLispAtom( t.getText() ) ); 
                    }

                    }
                    break;

            }

            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:718:3: (a= lisp_atom | l= lisp_form )*
            loop18:
            do {
                int alt18=3;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==NAME||LA18_0==STRING||LA18_0==INT||LA18_0==VAR||(LA18_0>=FLOAT && LA18_0<=NULL)) ) {
                    alt18=1;
                }
                else if ( (LA18_0==LEFT_PAREN) ) {
                    alt18=2;
                }


                switch (alt18) {
            	case 1 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:718:6: a= lisp_atom
            	    {
            	    pushFollow(FOLLOW_lisp_atom_in_lisp_form1564);
            	    a=lisp_atom();
            	    _fsp--;
            	    if (failed) return lispForm;
            	    if ( backtracking==0 ) {
            	       list.add( a ); 
            	    }

            	    }
            	    break;
            	case 2 :
            	    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:719:6: l= lisp_form
            	    {
            	    pushFollow(FOLLOW_lisp_form_in_lisp_form1575);
            	    l=lisp_form();
            	    _fsp--;
            	    if (failed) return lispForm;
            	    if ( backtracking==0 ) {
            	       list.add( l ); 
            	    }

            	    }
            	    break;

            	default :
            	    break loop18;
                }
            } while (true);

            match(input,RIGHT_PAREN,FOLLOW_RIGHT_PAREN_in_lisp_form1602); if (failed) return lispForm;
            if ( backtracking==0 ) {
               lispForm = new LispForm( ( SExpression[] ) list.toArray( new SExpression[ list.size () ] ) ); 
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:725:1: lisp_atom returns [SExpression sExpression] : (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME ) ;
    public final SExpression lisp_atom() throws RecognitionException {
        SExpression sExpression = null;

        Token t=null;

        
        		sExpression  =  null;		
        	
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:729:2: ( (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME ) )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:730:3: (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME )
            {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:730:3: (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME )
            int alt19=7;
            switch ( input.LA(1) ) {
            case VAR:
                {
                alt19=1;
                }
                break;
            case STRING:
                {
                alt19=2;
                }
                break;
            case FLOAT:
                {
                alt19=3;
                }
                break;
            case INT:
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
            case NAME:
                {
                alt19=7;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return sExpression;}
                NoViableAltException nvae =
                    new NoViableAltException("730:3: (t= VAR | t= STRING | t= FLOAT | t= INT | t= BOOL | t= NULL | t= NAME )", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:731:6: t= VAR
                    {
                    t=(Token)input.LT(1);
                    match(input,VAR,FOLLOW_VAR_in_lisp_atom1646); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new VariableLispAtom( t.getText() ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:732:6: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_lisp_atom1658); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new StringLispAtom( getString( t ) ); 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:733:6: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_lisp_atom1680); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new FloatLispAtom( t.getText() ); 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:734:6: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_lisp_atom1692); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new IntLispAtom( t.getText() ); 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:735:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_lisp_atom1705); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new BoolLispAtom( t.getText() ); 
                    }

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:736:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_lisp_atom1721); if (failed) return sExpression;
                    if ( backtracking==0 ) {
                       sExpression = new NullLispAtom( null ); 
                    }

                    }
                    break;
                case 7 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:737:14: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_lisp_atom1747); if (failed) return sExpression;
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
    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:742:1: literal returns [String text] : (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) ;
    public final String literal() throws RecognitionException {
        String text = null;

        Token t=null;

        
        		text = null;
        	
        try {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:746:2: ( (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL ) )
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:746:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
            {
            // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:746:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )
            int alt20=6;
            switch ( input.LA(1) ) {
            case STRING:
                {
                alt20=1;
                }
                break;
            case NAME:
                {
                alt20=2;
                }
                break;
            case INT:
                {
                alt20=3;
                }
                break;
            case FLOAT:
                {
                alt20=4;
                }
                break;
            case BOOL:
                {
                alt20=5;
                }
                break;
            case NULL:
                {
                alt20=6;
                }
                break;
            default:
                if (backtracking>0) {failed=true; return text;}
                NoViableAltException nvae =
                    new NoViableAltException("746:4: (t= STRING | t= NAME | t= INT | t= FLOAT | t= BOOL | t= NULL )", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:746:8: t= STRING
                    {
                    t=(Token)input.LT(1);
                    match(input,STRING,FOLLOW_STRING_in_literal1791); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = getString( t ); 
                    }

                    }
                    break;
                case 2 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:747:7: t= NAME
                    {
                    t=(Token)input.LT(1);
                    match(input,NAME,FOLLOW_NAME_in_literal1804); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 3 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:748:7: t= INT
                    {
                    t=(Token)input.LT(1);
                    match(input,INT,FOLLOW_INT_in_literal1820); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 4 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:749:7: t= FLOAT
                    {
                    t=(Token)input.LT(1);
                    match(input,FLOAT,FOLLOW_FLOAT_in_literal1835); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 5 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:750:7: t= BOOL
                    {
                    t=(Token)input.LT(1);
                    match(input,BOOL,FOLLOW_BOOL_in_literal1848); if (failed) return text;
                    if ( backtracking==0 ) {
                       text = t.getText(); 
                    }

                    }
                    break;
                case 6 :
                    // C:\\dev\\drools\\trunk6\\drools-clips\\src\\main\\resources\\org\\drools\\cips\\Clips.g:751:7: t= NULL
                    {
                    t=(Token)input.LT(1);
                    match(input,NULL,FOLLOW_NULL_in_literal1862); if (failed) return text;
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


 

    public static final BitSet FOLLOW_importDescr_in_eval60 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_deffunction_in_eval71 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_deftemplate_in_eval84 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_defrule_in_eval100 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_lisp_form_in_eval112 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_importDescr140 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_importDescr142 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_importDescr146 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_importDescr150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_deftemplate185 = new BitSet(new long[]{0x0000000000000080L});
    public static final BitSet FOLLOW_DEFTEMPLATE_in_deftemplate192 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_deftemplate196 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_deftemplate207 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_deftemplate_slot_in_deftemplate220 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_deftemplate228 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_deftemplate_slot267 = new BitSet(new long[]{0x0000000000000200L});
    public static final BitSet FOLLOW_SLOT_in_deftemplate_slot279 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_deftemplate_slot283 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_deftemplate_slot293 = new BitSet(new long[]{0x0000000000000400L});
    public static final BitSet FOLLOW_TYPE_in_deftemplate_slot309 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_deftemplate_slot313 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_deftemplate_slot333 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_deftemplate_slot340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_deffunction375 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_DEFFUNCTION_in_deffunction385 = new BitSet(new long[]{0x0000000038104120L});
    public static final BitSet FOLLOW_lisp_atom_in_deffunction395 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_form_in_deffunction405 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_form_in_deffunction414 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_deffunction436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_defrule473 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_DEFRULE_in_defrule481 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_defrule485 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_STRING_in_defrule497 = new BitSet(new long[]{0x0001000000100010L});
    public static final BitSet FOLLOW_ruleAttribute_in_defrule503 = new BitSet(new long[]{0x0001000000100010L});
    public static final BitSet FOLLOW_ce_in_defrule511 = new BitSet(new long[]{0x0001000000100010L});
    public static final BitSet FOLLOW_48_in_defrule520 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_rule_consequence_in_defrule529 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_defrule537 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_lisp_form_in_rule_consequence569 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute602 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_DECLARE_in_ruleAttribute604 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_ruleAttribute611 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_salience_in_ruleAttribute615 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute619 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_ruleAttribute626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SALIENCE_in_salience656 = new BitSet(new long[]{0x0000000000004000L});
    public static final BitSet FOLLOW_INT_in_salience660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_ce_in_ce686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_ce_in_ce696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_not_ce_in_ce705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_exists_ce_in_ce714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_eval_ce_in_ce728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_normal_pattern_in_ce742 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bound_pattern_in_ce751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_and_ce778 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_AND_in_and_ce783 = new BitSet(new long[]{0x0000000000100010L});
    public static final BitSet FOLLOW_ce_in_and_ce789 = new BitSet(new long[]{0x0000000000100050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_and_ce798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_or_ce826 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_OR_in_or_ce831 = new BitSet(new long[]{0x0000000000100010L});
    public static final BitSet FOLLOW_ce_in_or_ce837 = new BitSet(new long[]{0x0000000000100050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_or_ce846 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_not_ce874 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_NOT_in_not_ce879 = new BitSet(new long[]{0x0000000000100010L});
    public static final BitSet FOLLOW_ce_in_not_ce885 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_not_ce893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_exists_ce922 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_EXISTS_in_exists_ce927 = new BitSet(new long[]{0x0000000000100010L});
    public static final BitSet FOLLOW_ce_in_exists_ce933 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_exists_ce941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_eval_ce960 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_TEST_in_eval_ce965 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_form_in_eval_ce972 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_eval_ce982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_normal_pattern1010 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_normal_pattern1017 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_normal_pattern1023 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_normal_pattern1033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_bound_pattern1061 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_ASSIGN_OP_in_bound_pattern1067 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_bound_pattern1069 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_bound_pattern1073 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_field_constriant_in_bound_pattern1082 = new BitSet(new long[]{0x0000000000000050L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_bound_pattern1089 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_field_constriant1120 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_NAME_in_field_constriant1124 = new BitSet(new long[]{0x000000003F104120L});
    public static final BitSet FOLLOW_or_restr_connective_in_field_constriant1139 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_field_constriant1149 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective1188 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_PIPE_in_or_restr_connective1212 = new BitSet(new long[]{0x000000003F104120L});
    public static final BitSet FOLLOW_and_restr_connective_in_or_restr_connective1222 = new BitSet(new long[]{0x0000000000400002L});
    public static final BitSet FOLLOW_restriction_in_and_restr_connective1254 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_AMPERSAND_in_and_restr_connective1262 = new BitSet(new long[]{0x000000003F104120L});
    public static final BitSet FOLLOW_restriction_in_and_restr_connective1264 = new BitSet(new long[]{0x0000000000800002L});
    public static final BitSet FOLLOW_TILDE_in_restriction1297 = new BitSet(new long[]{0x000000003E104120L});
    public static final BitSet FOLLOW_predicate_constraint_in_restriction1313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_return_value_restriction_in_restriction1329 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_restriction_in_restriction1338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_restriction_in_restriction1350 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_predicate_constraint1383 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_form_in_predicate_constraint1389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQUALS_in_return_value_restriction1408 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_lisp_form_in_return_value_restriction1415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_variable_restriction1443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_literal_restriction1476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LEFT_PAREN_in_lisp_form1504 = new BitSet(new long[]{0x0000000000100020L});
    public static final BitSet FOLLOW_NAME_in_lisp_form1521 = new BitSet(new long[]{0x0000000038104170L});
    public static final BitSet FOLLOW_VAR_in_lisp_form1541 = new BitSet(new long[]{0x0000000038104170L});
    public static final BitSet FOLLOW_lisp_atom_in_lisp_form1564 = new BitSet(new long[]{0x0000000038104170L});
    public static final BitSet FOLLOW_lisp_form_in_lisp_form1575 = new BitSet(new long[]{0x0000000038104170L});
    public static final BitSet FOLLOW_RIGHT_PAREN_in_lisp_form1602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VAR_in_lisp_atom1646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_lisp_atom1658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_lisp_atom1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_lisp_atom1692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_lisp_atom1705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_lisp_atom1721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_lisp_atom1747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_literal1791 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NAME_in_literal1804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INT_in_literal1820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOAT_in_literal1835 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BOOL_in_literal1848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_literal1862 = new BitSet(new long[]{0x0000000000000002L});

}