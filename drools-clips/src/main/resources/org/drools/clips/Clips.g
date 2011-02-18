grammar Clips;

@parser::header {
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
}

@parser::members {
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
        if ( state.errorRecovery ) {
            //System.err.print("[SPURIOUS] ");
            return;
        }
        state.syntaxErrors++; // don't count spurious
        state.errorRecovery = true;

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
        
            void addTypeFieldDescr(LispForm lispForm, TypeDeclarationDescr typeDescr) {
                if ( !(lispForm.getSExpressions()[0] instanceof SymbolLispAtom) ) {
                    throw new RuntimeException("should specify a slot");
                }

                SymbolLispAtom slot = (SymbolLispAtom) lispForm.getSExpressions()[0];
                if ( !"slot".equals( slot.getValue().trim() )) {
                    throw new RuntimeException("should specify a slot");
                }

                if ( !(lispForm.getSExpressions()[1] instanceof SymbolLispAtom) ) {
                    throw new RuntimeException("should specify a slot name");
                }
                SymbolLispAtom slotName = (SymbolLispAtom) lispForm.getSExpressions()[1];

                if ( !(lispForm.getSExpressions()[2] instanceof LispForm) ) {
                    throw new RuntimeException("should specify a type");
                }

                LispForm typeForm = (LispForm) lispForm.getSExpressions()[2];
                if ( !(typeForm.getSExpressions()[0] instanceof SymbolLispAtom) ) {
                    throw new RuntimeException("should specify a type");
                }
                SymbolLispAtom type = (SymbolLispAtom) typeForm.getSExpressions()[0];
                if ( !"type".equals( type.getValue().trim() )) {
                    throw new RuntimeException("should specify a type");
                }

                if ( !(typeForm.getSExpressions()[1] instanceof SymbolLispAtom) ) {
                    throw new RuntimeException("should specify a slot name");
                }
                SymbolLispAtom typeName = (SymbolLispAtom) typeForm.getSExpressions()[1];

                TypeFieldDescr fieldDescr = new TypeFieldDescr(removeQuotes(slotName.getValue()), new PatternDescr(removeQuotes(typeName.getValue())));
                typeDescr.addField( fieldDescr );
            }

            String removeQuotes(String string) {
                return string.substring( 1, string.length() -1 );
            }
      
}

@lexer::header {
    package org.drools.clips;
}

/*
opt_semicolon
    : ';'?
    ;


compilation_unit
    :
        ( statement )+
    ;
*/
/*
statement
    :
    //later we add the other possible statements here
    (  //do something with the returned rule here )
    ;
*/		
/* prolog
    @init {
        String packageName = "";
    }
    :	( n=package_statement { packageName = n; } )?
        {
            this.packageDescr = factory.createPackage( packageName );
        }
    ;

statement
    :
    (	import_statement
    |       function_import_statement
    |	global
    |	function
    |       t=template {this.packageDescr.addFactTemplate( t ); }
    |	r=rule { if( r != null ) this.packageDescr.addRule( r ); }
    |	q=query	{ if( q != null ) this.packageDescr.addRule( q ); }
    )
    ;

package_statement returns [String packageName]
    @init{
        packageName = null;
    }
    :
        PACKAGE n=dotted_name[null] opt_semicolon
        {
            packageName = n;
        }
    ;
*/

eval[ParserHandler handler]
    :
       (		  i=importDescr{ handler.importHandler( i ); }
                | f=deffunction { handler.functionHandler( f ); }
                | t=deftemplate { handler.templateHandler( t ); }
                | r=defrule { handler.ruleHandler( r ); }
                | form=lisp_form { handler.lispFormHandler( form ); }
        )*
    ;

    /*
eval_sExpressions[MVELClipsContext context] returns[List<SExpression> list]
    @init {
        list = new ArrayList<SExpression>();
    }
    :
        (  	a=lisp_list { list.add( a ); }
           | a=deffunction { FunctionHandlers.dump(a, null, context); }
        )*
//		{ sExpressions = ( SExpression[] ) list.toArray( new SExpression[ list.size () ] ); }
    ;
    */

importDescr returns[ImportDescr importDescr]
    : LEFT_PAREN 'import' importName=NAME { importDescr = new ImportDescr( importName.getText() ); } RIGHT_PAREN
    ;
/*	


execution_list returns[ExecutionEngine engine]
    @init {
            engine = new BlockExecutionEngine();
            BuildContext context = new ExecutionBuildContext( engine, functionRegistry );
    }

    :
        (fc=lisp_list[context, new LispForm(context) ] { context.addFunction( (FunctionCaller) fc ); })
    ;
*/	

/*
deffunction returns[Deffunction function]
    @init {
            BuildContext context = null;
    }
    :	loc=LEFT_PAREN
          DEFFUNCTION
          ruleName=NAME {
            function = new Deffunction( ruleName.getText() );
            functionRegistry.addFunction( function );
              context = new ExecutionBuildContext( function, functionRegistry );
          }
        loc=LEFT_PAREN
         (v=VAR {
            context.addVariable( function.addParameter( v.getText() ) );
         })*
          RIGHT_PAREN
          (fc=lisp_list[context, new LispForm(context) ] { context.addFunction( (FunctionCaller) fc ); })*
          RIGHT_PAREN
    ;
*/	

/*	
deffunction_params[BuildContext context]
    :	loc=LEFT_PAREN
         (v=VAR {
            // this creates a parameter on the underlying function
             context.createLocalVariable( v.getText() );
         })*
          RIGHT_PAREN
    ;
*/

deffunction returns[FunctionDescr functionDescr]
    @init {
        List content = null;
        functionDescr = null;
    }
    :	LEFT_PAREN
        t=DEFFUNCTION //{ list.add( new SymbolLispAtom( t.getText() ) ); }    	//deffunction
        name=lisp_atom //name
        params=lisp_form  // params
        (form=lisp_form { if ( content == null ) content = new ArrayList(); content.add( form ); } )+
        RIGHT_PAREN
        { functionDescr = FunctionHandlers.createFunctionDescr( name, params, content ); }
        //{ sExpression = new LispForm( ( SExpression[] ) list.toArray( new SExpression[ list.size () ] ) ); }
    ;

defrule returns [RuleDescr rule]
    @init {
            rule = null;
            AndDescr lhs = null;
            PatternDescr colum = null;
            Set declarations = null;  
          }
    :	loc=LEFT_PAREN

        DEFRULE ruleName=NAME
          {
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
        documentation=STRING {
            // do nothing here for now
        }
        ruleAttribute[rule]

        ce[lhs, declarations]*

        '=>'

        list=rule_consequence{ rule.setConsequence( list ); }

        RIGHT_PAREN
    ;

rule_consequence returns[List list]
    @init {
        list = null;
    }
    :	
        (l=lisp_form	{ if ( list == null ) list = new ArrayList(); list.add( l ); })*
    ;

ruleAttribute[RuleDescr rule]
    :
        ( LEFT_PAREN 'declare'
            ( LEFT_PAREN d=salience { rule.addAttribute( d ); } RIGHT_PAREN )?
        RIGHT_PAREN )?
    ;

salience returns [AttributeDescr d ]
    @init {
        d = null;
    }
    :
        loc=SALIENCE i=INT
        {
            d = new AttributeDescr( "salience", i.getText() );
            d.setLocation( offset(loc.getLine()), loc.getCharPositionInLine() );
            d.setStartCharacter( ((CommonToken)loc).getStartIndex() );
            d.setEndCharacter( ((CommonToken)i).getStopIndex() );
        }
    ;


ce[ConditionalElementDescr in_ce, Set declarations]
    :	(   and_ce[in_ce, declarations]
          | or_ce[in_ce, declarations]
          | not_ce[in_ce, declarations]
          | exists_ce[in_ce, declarations]
           | eval_ce[in_ce, declarations]
          | normal_pattern[in_ce, declarations]
          | bound_pattern[in_ce, declarations]
        )
    ;

and_ce[ConditionalElementDescr in_ce, Set declarations]
    @init {
        AndDescr andDescr= null;        
    }
    :	LEFT_PAREN
        AND {
            andDescr = new AndDescr();
            in_ce.addDescr( andDescr );
        }
        ce[andDescr, declarations]+
        RIGHT_PAREN
    ;

or_ce[ConditionalElementDescr in_ce, Set declarations]
    @init {
        OrDescr orDescr= null;         
    }
    :	LEFT_PAREN
        OR {
            orDescr = new OrDescr();
            in_ce.addDescr( orDescr );
        }
        ce[orDescr, declarations]+
        RIGHT_PAREN
    ;

not_ce[ConditionalElementDescr in_ce, Set declarations]
    @init {
        NotDescr notDescr= null;         
    }
    :	LEFT_PAREN
        NOT {
            notDescr = new NotDescr();
            in_ce.addDescr( notDescr );
        }
        ce[notDescr, declarations]
        RIGHT_PAREN
    ;

exists_ce[ConditionalElementDescr in_ce, Set declarations]
    @init {
        ExistsDescr existsDescr= null;        
    }
    :	LEFT_PAREN
        EXISTS {
            existsDescr = new ExistsDescr();
            in_ce.addDescr( existsDescr );
        }
        ce[existsDescr, declarations]
        RIGHT_PAREN
    ;

eval_ce[ConditionalElementDescr in_ce, Set declarations]
    :	LEFT_PAREN
        TEST
        t=lisp_form { EvalDescr evalDescr = new EvalDescr(); evalDescr.setContent( t ); in_ce.addDescr( evalDescr ); }
        RIGHT_PAREN
    ;

normal_pattern[ConditionalElementDescr in_ce, Set declarations]
    @init {
        PatternDescr pattern = null;
        ConditionalElementDescr top = null;
    }
    :	LEFT_PAREN
        name=NAME {
            pattern = new PatternDescr(name.getText());
            in_ce.addDescr( pattern );
            top = pattern.getConstraint();

        }
        field_constriant[top, declarations]*
        RIGHT_PAREN
    ;



bound_pattern[ConditionalElementDescr in_ce, Set declarations]
    @init {
        PatternDescr pattern = null;
        String identifier = null;
        ConditionalElementDescr top = null;        
    }
    :	var=VAR {
            identifier = var.getText();
        }
        ASSIGN_OP LEFT_PAREN name=NAME
        {
            pattern = new PatternDescr(name.getText());
            pattern.setIdentifier( identifier.replace( '?', '$') );
            in_ce.addDescr( pattern );
            top = pattern.getConstraint();
        }
        field_constriant[top, declarations]*
        RIGHT_PAREN
    ;

field_constriant[ConditionalElementDescr base, Set declarations] 
    @init {
         List list = new ArrayList();
        FieldBindingDescr fbd = null;
        FieldConstraintDescr fc = null;
        RestrictionConnectiveDescr top = null;
        String op = "==";
    }
    :
        LEFT_PAREN f=NAME
        {
            fc = new FieldConstraintDescr(f.getText());
            fc.setLocation( offset(f.getLine()), f.getCharPositionInLine() );
            fc.setStartCharacter( ((CommonToken)f).getStartIndex() );
            //base.addDescr( fc );
            top = fc.getRestriction();
        }

        or_restr_connective[top, base, fc, declarations]
        { if ( top.getRestrictions().size() != 0 ) {
            base.insertBeforeLast( PredicateDescr.class, fc );
          }
        }
        RIGHT_PAREN
    ;
/*	
connected_constraint[RestrictionConnectiveDescr rc, ConditionalElementDescr base]
    :
    restriction[rc, base]
    (
        AMPERSAND { rc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND)); }
        connected_constraint[rc, base]
    |
        PIPE {rc.addRestriction(new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR)); }
        connected_constraint[rc, base]
    )?
    ;
*/


or_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ]
    options {
        backtrack=true;
    }
    @init {
        RestrictionConnectiveDescr or = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.OR);
    }
    :
        and_restr_connective[or, ceBase, fcBase, declarations]
        (
            options {backtrack=true;}
            : PIPE
            {
                location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            }
            and_restr_connective[or, ceBase, fcBase, declarations]
        )*
    ;
    finally {
            if( or.getRestrictions().size() == 1 ) {
                    $rcBase.addOrMerge( (RestrictionDescr) or.getRestrictions().get( 0 ) );
            } else if ( or.getRestrictions().size() > 1 ) {
                $rcBase.addRestriction( or );
            }
    }

and_restr_connective[ RestrictionConnectiveDescr rcBase, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ]
    @init {
        RestrictionConnectiveDescr and = new RestrictionConnectiveDescr(RestrictionConnectiveDescr.AND);
    }
    :
        restriction[and, ceBase, fcBase, declarations]
        ( AMPERSAND restriction[and, ceBase, fcBase, declarations] )*
        /*
        (	options {backtrack=true;}
        :	t=AMPERSAND
            {
                location.setType(Location.LOCATION_LHS_INSIDE_CONDITION_OPERATOR);
            }
            restriction[and, ceBase]
        )*
        */
    ;
    finally {
            if( and.getRestrictions().size() == 1) {
                    $rcBase.addOrMerge( (RestrictionDescr) and.getRestrictions().get( 0 ) );
            } else if ( and.getRestrictions().size() > 1 ) {
                $rcBase.addRestriction( and );
            }
    }

restriction[RestrictionConnectiveDescr rc, ConditionalElementDescr base, FieldConstraintDescr fcBase, Set declarations ]
    @init {
            String op = "==";
    }
    :	(TILDE{op = "!=";})?
        (	predicate_constraint[rc, op, base]
          |	return_value_restriction[op, rc]
          |	variable_restriction[op, rc, base, fcBase, declarations]
          | 	lc=literal_restriction {
                         rc.addRestriction( new LiteralRestrictionDescr(op, lc) );
                      op = "==";
                }
        )
    ;

predicate_constraint[RestrictionConnectiveDescr rc, String op, ConditionalElementDescr base]	
    :	COLON
        t=lisp_form { $base.addDescr( new PredicateDescr( t ) ); }

    ;


return_value_restriction[String op, RestrictionConnectiveDescr rc]
    :	EQUALS
        t=lisp_form {rc.addRestriction( new ReturnValueRestrictionDescr (op, t ) ); }
    ;

//will add a declaration field binding, if this is the first time the name  is used		
variable_restriction[String op, RestrictionConnectiveDescr rc, ConditionalElementDescr ceBase, FieldConstraintDescr fcBase, Set declarations ]
    @init { String identifier = null;}
    :	VAR {
            identifier =  $VAR.text.replace( '?', '$');
            if ( declarations.contains( identifier) ) {
                rc.addRestriction( new VariableRestrictionDescr(op, identifier ) );
             } else {
                 FieldBindingDescr fbd = new FieldBindingDescr();
                 fbd.setIdentifier( identifier );
                 fbd.setFieldName( fcBase.getFieldName() );
                 ceBase.addDescr( fbd );
                 declarations.add( identifier );
             }
        }
    ;


literal_restriction returns [String text]
    @init {
        text = null;
    }
    :
        t=literal {
            text = t;
        }
    ;

/* 
eval_sExpressions[MVELClipsContext context] returns[List<SExpression> list]
    @init {
        list = new ArrayList<SExpression>();
    }
    :
        (  	a=lisp_list { list.add( a ); }
           | a=deffunction { FunctionHandlers.dump(a, null, context); }
        )*
//		{ sExpressions = ( SExpression[] ) list.toArray( new SExpression[ list.size () ] ); }
    ;
*/	
lisp_form returns[LispForm lispForm]
    @init {
        List list = new ArrayList();
        lispForm = null;
    }
    :	LEFT_PAREN

        (
            t=NAME { list.add( new SymbolLispAtom( t.getText() ) ); }
            |
            t=VAR { list.add( new VariableLispAtom( t.getText() ) ); }
        )
        (		a=lisp_atom	{ list.add( a ); }
            |	l=lisp_form	{ list.add( l ); }
        )*
        RIGHT_PAREN
        { lispForm = new LispForm( ( SExpression[] ) list.toArray( new SExpression[ list.size () ] ) ); }
    ;

lisp_atom returns[SExpression sExpression] 
    @init {
        sExpression  =  null;
    }
    :
        (
                 t=VAR		{ sExpression = new VariableLispAtom( t.getText() ); }
            |	t=STRING	{ sExpression = new StringLispAtom( getString( t ) ); }
            |	t=FLOAT		{ sExpression = new FloatLispAtom( t.getText() ); }
            |	t=INT		{ sExpression = new IntLispAtom( t.getText() ); }
            | 	t=BOOL		{ sExpression = new BoolLispAtom( t.getText() ); }
            | 	t=NULL		{ sExpression = new NullLispAtom( null ); }
            |   t=NAME		{ sExpression = new SymbolLispAtom( "\"" +t.getText() + "\""); }

        )
    ;


deftemplate returns[TypeDeclarationDescr typeDescr]
    :
    loc=LEFT_PAREN 
    DEFTEMPLATE deftemplateName=NAME	{ 	  			  		
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

    documentation=STRING {
        // do nothing here for now
    }

        /*
        // can't get this to work, so process manually as a lisp_form
    (LEFT_PAREN     
    'slot' slotName=NAME
        LEFT_PAREN 
             'type' slotType=NAME {
            typeDescr.addField( new TypeFieldDescr(slotName.getText(), new PatternDescr( slotType.getText() ) ) );
        }        
        RIGHT_PAREN 
    RIGHT_PAREN)*
    */

    (list=lisp_form { addTypeFieldDescr(list, typeDescr); })*

    RIGHT_PAREN
    ;   


literal returns [String text]
    @init {
        text = null;
    }
    :	(   t=STRING { text = getString( t ); }
          | t=NAME     { text = t.getText(); }
          | t=INT    { text = t.getText(); }
          | t=FLOAT	 { text = t.getText(); }
          | t=BOOL 	 { text = t.getText(); }
          | t=NULL   { text = null; }
        )
    ;

WS      :       (	' '
                |	'\t'
                |	'\f'
                |	EOL
                )
                { $channel=HIDDEN; }
        ;                      

     
DEFTEMPLATE :   'deftemplate';
  
//SLOT        :	'slot';       
DEFRULE		:	'defrule';
DEFFUNCTION :	'deffunction';
OR 			:	'or';
AND 		:	'and';
NOT 		:	'not';
EXISTS 		:	'exists';
TEST 		:	'test';
NULL		:	'null';

DECLARE 	:	'declare';        		

SALIENCE	:	'salience';

//MODIFY  :	'modify';

fragment
EOL 	:	     
           (       ( '\r\n' )=> '\r\n'  // Evil DOS
                |       '\r'    // Macintosh
                |       '\n'    // Unix (the right way)
                )
        ;  
        
INT	
    :	('-')?('0'..'9')+
    ;

FLOAT
    :	('-')?('0'..'9')+ '.' ('0'..'9')+
    ;

STRING
    :  ('"' ( EscapeSequence | ~('\\'|'"') )* '"')
     | ('\'' ( EscapeSequence | ~('\\'|'\'') )* '\'')
    ;

fragment
HexDigit : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
EscapeSequence
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UnicodeEscape
    |   OctalEscape
    ;

fragment
OctalEscape
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UnicodeEscape
    :   '\\' 'u' HexDigit HexDigit HexDigit HexDigit
    ;

BOOL
    :	('true'|'false')
    ;

VAR 	: '?' SYMBOL_CHAR+
        ;            

SH_STYLE_SINGLE_LINE_COMMENT	
    :	'#' ( options{greedy=false;} : .)* EOL /* ('\r')? '\n'  */
                { $channel=HIDDEN; }
    ;
        
        
C_STYLE_SINGLE_LINE_COMMENT	
    :	'//' ( options{greedy=false;} : .)* EOL // ('\r')? '\n'
                { $channel=HIDDEN; }
    ;


LEFT_PAREN
    :	'('
    ;

RIGHT_PAREN
    :	')'
    ;
        
LEFT_SQUARE
    :	'['
    ;

RIGHT_SQUARE
    :	']'
    ;

LEFT_CURLY
    :	'{'
    ;

RIGHT_CURLY
    :	'}'
    ;

TILDE	:	'~'
    ;

AMPERSAND 
    :	'&'
    ;

PIPE
    :	'|'
    ;

ASSIGN_OP 
    :	'<-'
    ;

COLON	:	':';

EQUALS	:	'=';	
        
MULTI_LINE_COMMENT
    :	'/*' (options{greedy=false;} : .)* '*/'
                { $channel=HIDDEN; }
    ;

NAME :	SYMBOL ;

fragment
SYMBOL : FIRST_SYMBOL_CHAR SYMBOL_CHAR* ;	

// allowed <
// not allowed ?
fragment
FIRST_SYMBOL_CHAR : ('a'..'z'|'A'..'Z'|'0'..'9'|'!'|'$'|'%'|'^'|'*'|'_'|'-'|'+'|'='|'\\'|'/'|'@'|'#'|':'|'>'|'<'|','|'.'|'['|']'|'{'|'}');	

// allowed ? 
// not allowed <
fragment
SYMBOL_CHAR : ('a'..'z'|'A'..'Z'|'0'..'9'|'!'|'$'|'%'|'^'|'*'|'_'|'-'|'+'|'='|'\\'|'/'|'@'|'#'|':'|'>'|','|'.'|'['|']'|'{'|'}'|'?');		
