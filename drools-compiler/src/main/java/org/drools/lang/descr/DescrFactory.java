package org.drools.lang.descr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.RuntimeDroolsException;
import org.drools.lang.DroolsTree;

/**
 * This is the factory for ALL descriptors.
 * 
 * @author porcelli
 * 
 */
public class DescrFactory {

    /**
     * Factory method that creates a PackageDescr based on a identifier list.
     * 
     * @param idList
     *            identifier list that composes package, may be null
     * @return PackageDescr filled
     * @see PackageDescr
     */
    public PackageDescr createPackage( List<DroolsTree> idList ) {
        PackageDescr packageDescr = new PackageDescr(
                                                      createDotedIdFromList( idList ) );

        if ( null != idList && idList.size() > 0 ) {
            packageDescr.setLocation( getLineLocation( idList.get( 0 ) ),
                                      getColumnLocation( idList.get( 0 ) ) );
            packageDescr
                    .setStartCharacter( getStartOffsetLocation( idList.get( 0 ) ) );
            packageDescr.setEndLocation( getLineLocation( idList.get( 0 ) ),
                                         getEndOffsetLocation( idList.get( 0 ) ) );
            packageDescr.setEndCharacter( getEndOffsetLocation( idList.get( 0 ) ) );
        }

        return packageDescr;
    }

    /**
     * Factory method that creates an AttributeDescr based on name and value.
     * 
     * If value is single or double quoted, the quotes are removed. And if value
     * is null AttributeDescr is setted to true (boolean value).
     * 
     * @param attributeName
     *            attribute name
     * @param value
     *            attribute value, may be null (default value is true)
     * @return AttributeDescr filled
     * @see AttributeDescr
     */
    public AttributeDescr createAttribute( DroolsTree attributeName,
                                           DroolsTree value ) {
        AttributeDescr attributeDescr = new AttributeDescr( attributeName
                .getText() );
        attributeDescr.setLocation( getLineLocation( attributeName ),
                                    getColumnLocation( attributeName ) );

        attributeDescr.setStartCharacter( getStartOffsetLocation( attributeName ) );

        if ( null != value ) {
            attributeDescr.setValue( getCleanId( value ) );
            attributeDescr.setEndLocation( getLineLocation( value ),
                                           getEndOffsetLocation( value ) );
            attributeDescr.setEndCharacter( getEndOffsetLocation( value ) );
        } else {
            attributeDescr.setEndLocation( getLineLocation( attributeName ),
                                           getEndOffsetLocation( attributeName ) );
            attributeDescr.setEndCharacter( getEndOffsetLocation( attributeName ) );
            attributeDescr.setValue( "true" );
        }

        return attributeDescr;
    }

    /**
     * Factory method that creates a FunctionImportDescr.
     * 
     * @param importStart
     *            import statement, used to get offset location
     * @param idList
     *            identifier list that composes imported function
     * @param dotStar
     *            token that represents a ".*", may be null
     * @return FunctionImportDescr filled
     * @see FunctionImportDescr
     */
    public FunctionImportDescr createFunctionImport( DroolsTree importStart,
                                                     List<DroolsTree> idList,
                                                     DroolsTree dotStar ) {

        return (FunctionImportDescr) createGenericImport(
                                                          new FunctionImportDescr(),
                                                          importStart,
                                                          idList,
                                                          dotStar );
    }

    /**
     * Factory method that creates an ImportDescr.
     * 
     * @param importStart
     *            import statement, used to get offset location
     * @param idList
     *            identifier list that composes import
     * @param dotStar
     *            token that represents a ".*", may be null
     * @return ImportDescr filled
     * @see ImportDescr
     */
    public ImportDescr createImport( DroolsTree importStart,
                                     List<DroolsTree> idList,
                                     DroolsTree dotStar ) {

        return createGenericImport( new ImportDescr(),
                                    importStart,
                                    idList,
                                    dotStar );
    }

    /**
     * Generic method that creates import
     * 
     * @param importDescr
     *            import descriptor (FunctionImportDescr or ImportDescr)
     * @param importStart
     *            import statement, used to get offset location
     * @param idList
     *            identifier list that composes imported name
     * @param dotStar
     *            token that represents a ".*", may be null
     * @return ImportDescr filled
     * @see FunctionImportDescr
     * @see ImportDescr
     */
    private ImportDescr createGenericImport( ImportDescr importDescr,
                                             DroolsTree importStart,
                                             List<DroolsTree> idList,
                                             DroolsTree dotStar ) {
        ImportDescr genericImport = importDescr;

        genericImport.setLocation( getLineLocation( importStart ),
                                   getColumnLocation( importStart ) );
        genericImport.setStartCharacter( getStartOffsetLocation( importStart ) );

        genericImport.setEndLocation( getLineLocation( idList
                                              .get( idList.size() - 1 ) ),
                                      getEndOffsetLocation( idList
                                              .get( idList.size() - 1 ) ) );

        genericImport.setEndCharacter( getEndOffsetLocation( idList.get( idList
                .size() - 1 ) ) );

        StringBuilder sb = new StringBuilder();
        sb.append( createDotedIdFromList( idList ) );
        if ( null != dotStar ) {
            sb.append( ".*" );
            genericImport.setEndLocation( getLineLocation( dotStar ),
                                          getEndOffsetLocation( dotStar ) );
            genericImport.setEndCharacter( getEndOffsetLocation( dotStar ) );
        }
        genericImport.setTarget( sb.toString() );

        return genericImport;
    }

    /**
     * Factory method that creates a GlobalDescr.
     * 
     * @param start
     *            global statement, used to get offset location
     * @param dataType
     *            dataType descriptor (generated by createDataType method)
     * @param globalId
     *            global identifier
     * @return GlobalDescr filled
     * @see GlobalDescr
     */
    public GlobalDescr createGlobal( DroolsTree start,
                                     BaseDescr dataType,
                                     DroolsTree globalId ) {
        GlobalDescr globalDescr = new GlobalDescr();

        globalDescr.setIdentifier( globalId.getText() );
        globalDescr.setType( dataType.getText() );

        globalDescr.setLocation( getLineLocation( start ),
                                 getColumnLocation( start ) );
        globalDescr.setEndLocation( getLineLocation( globalId ),
                                    getEndOffsetLocation( globalId ) );
        globalDescr.setEndCharacter( getEndOffsetLocation( globalId ) );

        return globalDescr;
    }

    /**
     * Factory method that creates a FunctionDescr.
     * 
     * @param start
     *            function statement, used to get offset location
     * @param dataType
     *            dataType descriptor (generated by createDataType method), may
     *            be null
     * @param functionId
     *            function identifier
     * @param params
     *            a list, that contains pair of parameter name and data type
     * @param content
     *            chunk data
     * @return FunctionDescr filled
     * @see FunctionDescr
     */
    public FunctionDescr createFunction( DroolsTree start,
                                         BaseDescr dataType,
                                         DroolsTree functionId,
                                         List<Map<BaseDescr, BaseDescr>> params,
                                         DroolsTree content ) {
        String type = null;
        if ( null != dataType ) {
            type = dataType.getText();
        }

        FunctionDescr functionDescr = new FunctionDescr( functionId.getText(),
                                                         type );

        for ( Map<BaseDescr, BaseDescr> map : params ) {
            for ( Entry<BaseDescr, BaseDescr> entry : map.entrySet() ) {
                functionDescr.addParameter( entry.getValue().getText(),
                                            entry
                                                    .getKey().getText() );
            }
        }

        functionDescr.setText( content.getText().substring( 1,
                                                            content.getText().length() - 1 ) );

        functionDescr.setLocation( getLineLocation( start ),
                                   getColumnLocation( start ) );
        functionDescr.setEndCharacter( getEndOffsetLocation( content ) );

        return functionDescr;
    }

    /**
     * Factory method that creates a FactTemplateDescr.
     * 
     * @param start
     *            template statement, used to get offset location
     * @param id
     *            template identifier
     * @param slotList
     *            a slot list
     * @param end
     *            end statement, used to get offset location
     * @return FactTemplateDescr filled
     * @see FactTemplateDescr
     */
    public FactTemplateDescr createFactTemplate( DroolsTree start,
                                                 DroolsTree id,
                                                 List<FieldTemplateDescr> slotList,
                                                 DroolsTree end ) {
        FactTemplateDescr factTemplateDescr = new FactTemplateDescr(
                                                                     getCleanId( id ) );
        for ( FieldTemplateDescr fieldTemplateDescr : slotList ) {
            factTemplateDescr.addFieldTemplate( fieldTemplateDescr );
        }

        factTemplateDescr.setLocation( getLineLocation( start ),
                                       getColumnLocation( start ) );
        factTemplateDescr.setEndLocation( getLineLocation( end ),
                                          getEndOffsetLocation( end ) );
        factTemplateDescr.setEndCharacter( getEndOffsetLocation( end ) );

        return factTemplateDescr;
    }

    /**
     * Factory method that creates a FieldTemplateDescr.
     * 
     * @param dataType
     *            dataType descriptor (generated by createDataType method)
     * @param id
     *            field template identifier
     * @return FieldTemplateDescr filled
     * @see FieldTemplateDescr
     */
    public FieldTemplateDescr createFieldTemplate( BaseDescr dataType,
                                                   DroolsTree id ) {
        FieldTemplateDescr fieldTemplateDescr = new FieldTemplateDescr();
        fieldTemplateDescr.setClassType( dataType.getText() );
        fieldTemplateDescr.setName( id.getText() );

        fieldTemplateDescr
                .setLocation( dataType.getLine(),
                              dataType.getColumn() );
        fieldTemplateDescr.setEndLocation( getLineLocation( id ),
                                           getEndOffsetLocation( id ) );
        fieldTemplateDescr.setEndCharacter( getEndOffsetLocation( id ) );

        return fieldTemplateDescr;
    }

    /**
     * Factory method that creates a QueryDescr.
     * 
     * @param start
     *            query statement, used to get offset location
     * @param id
     *            query identifier
     * @param params
     *            a list, that contains pair of parameter name and data type,
     *            may be null. Parameter without data type Object is assumed.
     * @param andDescr
     *            AndDescr returned by lhs block
     * @param end
     *            end statement, used to get offset location
     * @return QueryDescr filled
     * @see QueryDescr
     * @see AndDescr
     */
    public QueryDescr createQuery( DroolsTree start,
                                   DroolsTree id,
                                   List<Map<BaseDescr, BaseDescr>> params,
                                   AndDescr andDescr,
                                   DroolsTree end ) {

        QueryDescr queryDescr = new QueryDescr( getCleanId( id ),
                                                "" );

        if ( null != params && params.size() > 0 ) {
            for ( Map<BaseDescr, BaseDescr> map : params ) {
                for ( Entry<BaseDescr, BaseDescr> entry : map.entrySet() ) {
                    queryDescr.addParameter( null == entry.getValue() ?  "Object" : entry.getValue().getText(), entry.getKey().getText() );
                }
            }
        }

        queryDescr.setLhs( andDescr );
        queryDescr
                .setLocation( getLineLocation( start ),
                              getColumnLocation( start ) );
        queryDescr.setEndLocation( getLineLocation( end ),
                                   getEndOffsetLocation( end ) );
        queryDescr.setEndCharacter( getEndOffsetLocation( end ) );

        return queryDescr;
    }

    /**
     * Factory method that creates a TypeDeclarationDescr.
     * 
     * @param id
     *            type declaration descriptior id
     * @param declMetadaList
     *            metadata list
     * @param declFieldList
     *            field list
     * @return TypeDeclarationDescr filled
     */
    @SuppressWarnings("unchecked")
    public TypeDeclarationDescr createTypeDeclr( DroolsTree id,
                                                 String superType,
                                                 Collection<String> interfaces,
                                                 List<Map> declMetadaList,
                                                 List<TypeFieldDescr> declFieldList ) {
        TypeDeclarationDescr typeDeclr = new TypeDeclarationDescr();
        typeDeclr.setTypeName( id.getText() );

        typeDeclr.setSuperTypeName( superType );

        for ( Map activeMetadata : declMetadaList ) {
            Entry activeEntry = (Entry) activeMetadata.entrySet().iterator().next();

            typeDeclr.addAnnotation( (String) activeEntry.getKey(),
                                     (String) activeEntry.getValue() );
        }

        for ( TypeFieldDescr typeFieldDescr : declFieldList ) {
            typeDeclr.addField( typeFieldDescr );
        }
        return typeDeclr;
    }

    @SuppressWarnings("unchecked")
    public TypeDeclarationDescr createTypeDeclr( DroolsTree id,
                                                 List<Map> declMetadaList,
                                                 List<TypeFieldDescr> declFieldList ) {
        return createTypeDeclr( id,
                                null,
                                Collections.<String> emptyList(),
                                declMetadaList,
                                declFieldList );
    }

    /**
     * Factory method that creates a TypeFieldDescr.
     * 
     * @param id
     *            type field id
     * @param initExpr
     *            initial expression
     * @param dt
     *            data type
     * @param declMetadaList
     *            metadata list
     * @return TypeFieldDescr filled
     */
    @SuppressWarnings("unchecked")
    public TypeFieldDescr createTypeField( DroolsTree id,
                                           String initExpr,
                                           BaseDescr dt,
                                           List<Map> declMetadaList ) {
        TypeFieldDescr field = new TypeFieldDescr( id.getText() );
        if ( null != initExpr ) {
            field.setInitExpr( initExpr );
        }
        field.setPattern( new PatternDescr( dt.getText() ) );
        for ( Map activeMetadata : declMetadaList ) {
            Entry activeEntry = (Entry) activeMetadata.entrySet().iterator()
                    .next();

            field.addAnnotation( (String) activeEntry.getKey(),
                                 (String) activeEntry.getValue() );
            //String chunkData = ((DroolsTree) activeEntry.getValue() != null ) ?((DroolsTree) activeEntry.getValue()).getText() : "()";
            //field.addMetaAttribute(((DroolsTree) activeEntry.getKey())
            //		.getText(), chunkData.substring(1, chunkData.length() - 1));
        }

        return field;
    }

    /**
     * Factory method that creates a RuleDescr.
     * 
     * @param start
     *            rule statement, used to get offset location
     * @param id
     *            rule identifier
     * @param attributeList
     *            attribute list
     * @param andDescr
     *            AndDescr returned by lhs block, may be null
     * @param content
     *            chunk data
     * @return RuleDescr filled
     * @see RuleDescr
     */
    public RuleDescr createRule( DroolsTree start,
                                 DroolsTree id,
                                 DroolsTree parentId,
                                 List<AttributeDescr> attributeList,
                                 AndDescr andDescr,
                                 DroolsTree content,
                                 List<Map> metadata ) {

        RuleDescr ruleDescr = new RuleDescr( getCleanId( id ),
                                             null );

        //Add parentId, the rule you are extending to the ruleDescr 
        if ( null != parentId ) {
            ruleDescr.setParentName( getCleanId( parentId ) );
        }

        if ( null != attributeList && attributeList.size() > 0 ) {
            for ( AttributeDescr attributeDescr : attributeList ) {
                ruleDescr.addAttribute( attributeDescr );
            }
        }

        if ( null != metadata && metadata.size() > 0 ) {

            for ( Map activeMetadata : metadata ) {
                Entry activeEntry = (Entry) activeMetadata.entrySet().iterator().next();

                ruleDescr.addAnnotation(
                                            (String) activeEntry.getKey(),
                                            (String) activeEntry.getValue() );
                //for (Map<DroolsTree,DroolsTree> map : metadata){
                //	for (Map.Entry<DroolsTree,DroolsTree> entry : map.entrySet() ){
                //		String chunkData = entry.getValue().getText();
                //		ruleDescr.addMetaAttribute( entry.getKey().getText(),chunkData.substring(1, chunkData.length() - 1).trim()  );
                //	}
            }
        }

        if ( null == andDescr ) {
            ruleDescr.setLhs( new AndDescr() );
        } else {
            ruleDescr.setLhs( andDescr );
        }

        // ignoring first line in the consequence
        String buf = content.getText();
        // removing final END keyword
        int idx = 4;
        while ( idx < buf.length() - 3
                && (buf.charAt( idx ) == ' ' || buf.charAt( idx ) == '\t') ) {
            idx++;
        }
        if ( idx < buf.length() - 3 && buf.charAt( idx ) == '\r' ) idx++;
        if ( idx < buf.length() - 3 && buf.charAt( idx ) == '\n' ) idx++;
        buf = buf.substring( idx,
                             buf.length() - 3 );
        ruleDescr.setConsequence( buf );
        ruleDescr.setConsequenceLocation( getLineLocation( content ),
                                          getColumnLocation( content ) );

        ruleDescr.setLocation( getLineLocation( start ),
                               getColumnLocation( start ) );
        ruleDescr.setEndCharacter( getEndOffsetLocation( content ) );

        return ruleDescr;
    }

    /**
     * Factory method that creates an Argument.
     * 
     * @param id
     *            argument identifier
     * @param rightList
     *            right square list
     * @return BaseDescr (argument info) filled.
     */
    public BaseDescr createArgument( DroolsTree id,
                                     List<DroolsTree> rightList ) {
        List<DroolsTree> idList = new ArrayList<DroolsTree>( 1 );
        idList.add( id );
        return createGenericBaseDescr( idList,
                                       rightList );
    }

    // LHS Start
    /**
     * Factory method that creates an OrDescr. This method handles prefixed and
     * infixed formats.
     * 
     * @param start
     *            "or" statement or symbol, used to get offset location
     * @param lhsList
     *            binded descriptor list
     * @return OrDescr filled.
     * @see OrDescr
     */
    public OrDescr createOr( DroolsTree start,
                             List<BaseDescr> lhsList ) {
        OrDescr or = new OrDescr();
        or.setLocation( getColumnLocation( start ),
                        getColumnLocation( start ) );
        or.setStartCharacter( getStartOffsetLocation( start ) );
        or.setEndLocation( lhsList.get( lhsList.size() - 1 ).getEndLine(),
                           lhsList
                                   .get( lhsList.size() - 1 ).getEndColumn() );
        or.setEndCharacter( lhsList.get( lhsList.size() - 1 ).getEndCharacter() );
        for ( BaseDescr baseDescr : lhsList ) {
            or.addDescr( baseDescr );
        }
        return or;
    }

    /**
     * Factory method that creates an AndDescr. This method handles prefixed and
     * infixed formats.
     * 
     * @param start
     *            "and" statement or symbol, used to get offset location
     * @param lhsList
     *            binded descriptor list
     * @return AndDescr filled.
     * @see AndDescr
     */
    public AndDescr createAnd( DroolsTree start,
                               List<BaseDescr> lhsList ) {
        AndDescr and = new AndDescr();
        and.setLocation( getColumnLocation( start ),
                         getColumnLocation( start ) );
        and.setStartCharacter( getStartOffsetLocation( start ) );
        and.setEndLocation( lhsList.get( lhsList.size() - 1 ).getEndLine(),
                            lhsList.get( lhsList.size() - 1 ).getEndColumn() );
        and.setEndCharacter( lhsList.get( lhsList.size() - 1 ).getEndCharacter() );
        for ( BaseDescr baseDescr : lhsList ) {
            and.addDescr( baseDescr );
        }
        return and;
    }

    /**
     * Factory method that creates an ExistsDescr.
     * 
     * @param start
     *            exists statement, used to get offset location
     * @param baseDescr
     *            binded descriptor
     * @return ExistsDescr filled.
     * @see ExistsDescr
     */
    public ExistsDescr createExists( DroolsTree start,
                                     BaseDescr baseDescr ) {
        ExistsDescr exists = new ExistsDescr();
        exists.setLocation( getColumnLocation( start ),
                            getColumnLocation( start ) );
        exists.setStartCharacter( getStartOffsetLocation( start ) );
        exists.setEndLocation( baseDescr.getEndLine(),
                               baseDescr.getEndColumn() );
        exists.setEndCharacter( baseDescr.getEndCharacter() );
        exists.addDescr( baseDescr );
        return exists;
    }

    /**
     * Factory method that creates a NotDescr.
     * 
     * @param start
     *            not statement, used to get offset location
     * @param baseDescr
     *            binded descriptor
     * @return NotDescr filled.
     * @see NotDescr
     */
    public NotDescr createNot( DroolsTree start,
                               BaseDescr baseDescr ) {
        NotDescr not = new NotDescr();
        not.setLocation( getColumnLocation( start ),
                         getColumnLocation( start ) );
        not.setStartCharacter( getStartOffsetLocation( start ) );
        not.setEndLocation( baseDescr.getEndLine(),
                            baseDescr.getEndColumn() );
        not.setEndCharacter( baseDescr.getEndCharacter() );
        not.addDescr( baseDescr );
        return not;
    }

    /**
     * Factory method that creates an EvalDescr.
     * 
     * @param start
     *            eval statement, used to get offset location
     * @param content
     *            chunk data
     * @return EvalDescr filled.
     * @see EvalDescr
     */
    public EvalDescr createEval( DroolsTree start,
                                 DroolsTree content ) {
        EvalDescr eval = new EvalDescr();
        eval.setLocation( getColumnLocation( start ),
                          getColumnLocation( start ) );
        eval.setStartCharacter( getStartOffsetLocation( start ) );
        eval.setEndCharacter( getEndOffsetLocation( content ) );
        eval.setContent( content.getText().substring( 1,
                                                      content.getText().length() - 1 ) );
        return eval;
    }

    /**
     * Factory method that creates a ForallDescr.
     * 
     * @param start
     *            forall statement, used to get offset location
     * @param lhsList
     *            binded descriptor list
     * @return ForallDescr filled.
     * @see ForallDescr
     */
    public ForallDescr createForAll( DroolsTree start,
                                     List<BaseDescr> lhsList ) {
        ForallDescr forAll = new ForallDescr();
        forAll.setLocation( getColumnLocation( start ),
                            getColumnLocation( start ) );
        forAll.setStartCharacter( getStartOffsetLocation( start ) );
        forAll.setEndLocation( lhsList.get( lhsList.size() - 1 ).getEndLine(),
                               lhsList.get( lhsList.size() - 1 ).getEndColumn() );
        forAll.setEndCharacter( lhsList.get( lhsList.size() - 1 )
                .getEndCharacter() );
        for ( BaseDescr baseDescr : lhsList ) {
            forAll.addDescr( baseDescr );
        }
        return forAll;
    }

    /**
     * Factory method that creates a PatternDescr.
     * 
     * @param from
     *            lhs pattern
     * @param fromSource
     *            generic pattern source
     * @return PatternDescr filled.
     * @see PatternDescr
     */
    public PatternDescr setupFrom( BaseDescr from,
                                   PatternSourceDescr fromSource ) {
        PatternDescr tempFrom = (PatternDescr) from;
        tempFrom.setSource( fromSource );
        return tempFrom;
    }

    /**
     * Factory method that creates an empty AccumulateDescr.
     */
    public AccumulateDescr createAccumulate() {
        return new AccumulateDescr();
    }

    /**
     * Factory method that creates an AccumulateDescr.
     * 
     * @param start
     *            accumulate statement, used to get offset location
     * @param baseDescr
     *            binded descriptor
     * @return AccumulateDescr filled.
     * @see AccumulateDescr
     */
    public AccumulateDescr createAccumulate( DroolsTree start,
                                             BaseDescr baseDescr ) {
        AccumulateDescr accumulate = new AccumulateDescr();
        accumulate.setLocation( getColumnLocation( start ),
                                getColumnLocation( start ) );
        accumulate.setStartCharacter( getStartOffsetLocation( start ) );
        accumulate.setInput( baseDescr );

        return accumulate;
    }

    /**
     * Factory method that creates a ForFunctionDescr.
     * 
     * @param id
     *            id of the function
     * @param label 
     *            label bound to the function
     * @param arguments
     *            arguments to the function
     * @return ForFunctionDescr filled.
     * @see ForFunctionDescr
     */
    @SuppressWarnings("unchecked")
    public ForFunctionDescr createForFunction( DroolsTree id,
                                               DroolsTree label,
                                               List<String> arguments ) {
        ForFunctionDescr ff = new ForFunctionDescr();
        ff.setId( id.getText() );
        ff.setLabel( label.getText() );
        ff.setStartCharacter( getStartOffsetLocation( label ) );
        ff.setLocation( getColumnLocation( label ),
                        getColumnLocation( label ) );
        ff.setArguments( arguments );
        // for( DroolsTree arg : (List<DroolsTree>) arguments.getChildren() ) {
        // if( arg.getType() == DRLParser.RIGHT_PAREN ) {
        // ff.setEndCharacter( getEndOffsetLocation( arg ) );
        // ff.setEndLocation( getColumnLocation( arg ),
        // getColumnLocation( arg ) );
        // } else {
        // ff.getArguments().add( arg.getText() );
        // }
        // }
        return ff;
    }

    /**
     * Factory method that creates a CollectDescr.
     * 
     * @param start
     *            collect statement, used to get offset location
     * @param baseDescr
     *            binded descriptor
     * @return CollectDescr filled.
     * @see CollectDescr
     */
    public CollectDescr createCollect( DroolsTree start,
                                       BaseDescr baseDescr ) {
        CollectDescr collect = new CollectDescr();
        collect.setLocation( getColumnLocation( start ),
                             getColumnLocation( start ) );
        collect.setStartCharacter( getStartOffsetLocation( start ) );
        collect
                .setEndLocation( baseDescr.getEndLine(),
                                 baseDescr
                                         .getEndColumn() );
        collect.setEndCharacter( baseDescr.getEndCharacter() );
        collect.setInputPattern( (PatternDescr) baseDescr );
        return collect;
    }

    /**
     * Factory method that creates an EntryPointDescr.
     * 
     * @param start
     *            entry point statement, used to get offset location
     * @param entryId
     *            entry point identifier
     * @return EntryPointDescr filled.
     * @see EntryPointDescr
     */
    public EntryPointDescr createEntryPoint( DroolsTree start,
                                             DroolsTree entryId ) {
        EntryPointDescr entryPoint = new EntryPointDescr();
        entryPoint.setLocation( getColumnLocation( start ),
                                getColumnLocation( start ) );
        entryPoint.setStartCharacter( getStartOffsetLocation( start ) );
        entryPoint.setEndLocation( getLineLocation( entryId ),
                                   getColumnLocation( entryId ) );
        entryPoint.setEndCharacter( getEndOffsetLocation( entryId ) );
        entryPoint.setEntryId( getCleanId( entryId ) );
        return entryPoint;
    }

    /**
     * Method that setups the AccumulateDescr, setting all the chunk data
     * 
     * @param accumulateParam
     *            accumulate descriptor
     * @param start
     *            start (init) statement, used to get offset location
     * @param initChunk
     *            init chunk data
     * @param actionChunk
     *            action chunk data
     * @param resultChunk
     *            result chunk data
     * @param reverseChunk
     *            reverse chunk data, may be null
     * @return AccumulateDescr filled
     * @see AccumulateDescr
     */
    public AccumulateDescr setupAccumulateInit(
                                                PatternSourceDescr accumulateParam,
                                                DroolsTree start,
                                                DroolsTree initChunk,
                                                DroolsTree actionChunk,
                                                DroolsTree resultChunk,
                                                DroolsTree reverseChunk ) {
        AccumulateDescr accumulate = (AccumulateDescr) accumulateParam;
        accumulate.setEndCharacter( getEndOffsetLocation( resultChunk ) );
        accumulate.setInitCode( initChunk.getText().substring( 1,
                                                               initChunk.getText().length() - 1 ) );
        accumulate.setActionCode( actionChunk.getText().substring( 1,
                                                                   actionChunk.getText().length() - 1 ) );
        accumulate.setResultCode( resultChunk.getText().substring( 1,
                                                                   resultChunk.getText().length() - 1 ) );
        if ( reverseChunk != null ) {
            accumulate.setReverseCode( reverseChunk.getText().substring( 1,
                                                                         reverseChunk.getText().length() - 1 ) );
        }

        return accumulate;
    }

    /**
     * Method that setups the AccumulateDescr, setting identifier and expression
     * chunk data
     * 
     * @param accumulateParam
     *            accumulate descriptor
     * @param id
     *            accumulate identifier
     * @param expressionChunk
     *            chunk data
     * @return AccumulateDescr filled
     * @see AccumulateDescr
     */
    public AccumulateDescr setupAccumulateId(
                                              PatternSourceDescr accumulateParam,
                                              DroolsTree id,
                                              DroolsTree expressionChunk ) {
        AccumulateDescr accumulate = (AccumulateDescr) accumulateParam;
        accumulate.setEndCharacter( getEndOffsetLocation( expressionChunk ) );

        accumulate.setExternalFunction( true );
        accumulate.setFunctionIdentifier( id.getText() );
        accumulate.setExpression( expressionChunk.getText().substring( 1,
                                                                       expressionChunk.getText().length() - 1 ) );
        return accumulate;
    }

    /**
     * Factory method that creates an AccessorDescr.
     * 
     * @param id
     *            accessor identifier
     * @param chunk
     *            chunk data, may be null
     * @return AccessorDescr filled
     * @see AccessorDescr
     */
    public AccessorDescr createAccessor( DroolsTree id,
                                         DroolsTree chunk ) {
        AccessorDescr accessor = new AccessorDescr( id.getText() );
        accessor.setLocation( getLineLocation( id ),
                              getColumnLocation( id ) );
        accessor.setStartCharacter( getStartOffsetLocation( id ) );
        accessor.setEndCharacter( getEndOffsetLocation( id ) );
        if ( chunk != null ) {
            accessor.setVariableName( null );
            FunctionCallDescr functionCall = new FunctionCallDescr( id.getText() );
            functionCall
                    .setLocation( getLineLocation( id ),
                                  getColumnLocation( id ) );
            functionCall.setStartCharacter( getStartOffsetLocation( id ) );
            functionCall.setEndCharacter( getEndOffsetLocation( chunk ) );
            functionCall.setArguments( chunk.getText() );
            accessor.addInvoker( functionCall );
        }
        return accessor;
    }

    /**
     * Factory method that creates an AccessorDescr.
     *
     * @param text
     *            chunk data, may be null
     * @return AccessorDescr filled
     * @see AccessorDescr
     */
    public AccessorDescr createAccessor( String text ) {
        AccessorDescr accessor = new AccessorDescr( null );
        //        accessor.setLocation(getLineLocation(start), getColumnLocation(start));
        //        accessor.setStartCharacter(getStartOffsetLocation(start));
        //        accessor.setEndCharacter(getEndOffsetLocation(stop));
        MVELExprDescr expr = new MVELExprDescr( text );
        accessor.addInvoker( expr );
        return accessor;
    }

    /**
     * Setup the char offset for AccessorDescr.
     * 
     * @param accessorDescr
     *            descriptor
     * @return AccessorDescr with char offset filled
     */
    public AccessorDescr setupAccessorOffset( AccessorDescr accessorDescr ) {
        if ( null != accessorDescr.getInvokers()
                && accessorDescr.getInvokers().size() > 0 ) {
            BaseDescr desc = (BaseDescr) accessorDescr.getInvokers().get(
                                                                          accessorDescr.getInvokers().size() - 1 );
            accessorDescr.setEndCharacter( desc.getEndCharacter() );
        }
        return accessorDescr;
    }

    /**
     * Factory method that based on parenChunk parameter defines if it creates a
     * FieldAccessDescr or a MethodAccessDescr.
     * 
     * @param start
     *            start (dot) token, used to get offset location
     * @param id
     *            identifier
     * @param squareChunk
     *            square chunk data, may be null
     * @param parenChunk
     *            paren chunk data, may be null
     * @return DeclarativeInvokerDescr filled.
     * @see DeclarativeInvokerDescr
     * @see FieldAccessDescr
     * @see MethodAccessDescr
     */
    public DeclarativeInvokerDescr createExpressionChain( DroolsTree start,
                                                          DroolsTree id,
                                                          DroolsTree squareChunk,
                                                          DroolsTree parenChunk ) {
        DeclarativeInvokerDescr declarativeInvoker = null;
        if ( null == parenChunk ) {
            FieldAccessDescr field = new FieldAccessDescr( id.getText() );
            field.setLocation( getLineLocation( id ),
                               getColumnLocation( id ) );
            field.setStartCharacter( getStartOffsetLocation( id ) );
            field.setEndCharacter( getEndOffsetLocation( id ) );
            if ( null != squareChunk ) {
                field.setArgument( squareChunk.getText() );
                field.setEndCharacter( getEndOffsetLocation( squareChunk ) );
            }
            declarativeInvoker = field;
        } else {
            MethodAccessDescr method = new MethodAccessDescr( id.getText(),
                                                              parenChunk.getText() );
            method.setLocation( getLineLocation( id ),
                                getColumnLocation( id ) );
            method.setStartCharacter( getStartOffsetLocation( id ) );
            method.setEndCharacter( getEndOffsetLocation( parenChunk ) );
            declarativeInvoker = method;
        }

        return declarativeInvoker;
    }

    /**
     * Method that setups the PatternDescr, setting all the behavior data
     * 
     * @param descr
     *            PatternDescr to be setted
     * @param behaviorList
     *            list of behaviors
     * @return BaseDescr setted
     */
    public BaseDescr setupBehavior( BaseDescr descr,
                                    List<BehaviorDescr> behaviorList ) {
        if ( null != behaviorList && descr instanceof PatternDescr ) {
            for ( BehaviorDescr activeBehavior : behaviorList ) {
                ((PatternDescr) descr).addBehavior( activeBehavior );
            }
        }
        return descr;
    }

    /**
     * Factory method that creates a BehaviorDescr.
     * 
     * @param type
     *            behavior type
     * @param param
     *            chunk data
     * @return BehaviorDescr filled
     */
    public BehaviorDescr createBehavior( DroolsTree type,
                                         DroolsTree param ) {
        //return new SlidingWindowDescr(type.getText(), param.getText()
        //		.substring(1, param.getText().length() - 1));
        return new SlidingWindowDescr( type.getText(),
                                       param.getText() );
    }

    /**
     * Factory method that creates a FromDescr.
     * 
     * @param accessor
     *            binded accessor descriptor
     * @return FromDescr filled
     */
    public FromDescr createFromSource( AccessorDescr accessor ) {
        FromDescr from = new FromDescr();
        from.setDataSource( accessor );
        from.setEndCharacter( accessor.getEndCharacter() );
        return from;
    }

    /**
     * Factory method that creates a PatternDescr.
     * 
     * FieldBindingDescr holds the original field descriptor, first it is
     * necessary add the field and later the bindind field info.
     * 
     * @param dataType
     *            pattern data type
     * @param exprList
     *            binded expression list, may be null or empty
     * @return
     */
    public PatternDescr createPattern( BaseDescr dataType,
                                       List<BaseDescr> exprList ) {
        PatternDescr pattern = new PatternDescr();
        pattern.setLocation( dataType.getEndLine(),
                             dataType.getEndColumn() );
        pattern.setEndLocation( dataType.getEndLine(),
                                dataType.getEndColumn() );
        pattern.setEndCharacter( dataType.getEndCharacter() );
        pattern.setObjectType( dataType.getText() );

        if ( null != exprList && exprList.size() > 0 ) {
            if ( exprList.size() == 1 ) {
                pattern.getConstraint().addOrMerge( exprList.get( 0 ) );
            } else {
                for ( BaseDescr constraint : exprList ) {
                    if ( constraint != null ) {
                        pattern.getConstraint().addDescr( constraint );
                    }
                }
            }
            BaseDescr last = exprList.get( exprList.size() - 1 );
            if ( last != null ) {
                pattern.setEndCharacter( last.getEndCharacter() );
            }
        }
        return pattern;
    }

    /**
     * Method that setups a pattern binding.
     * 
     * The fact may be an or descriptor and the or elements should be binded.
     * 
     * @param label
     *            bind identifier
     * @param fact
     *            fact descriptor
     * @return fact binded.
     * @see PatternDescr
     * @see OrDescr
     */
    public BaseDescr setupPatternBiding( DroolsTree label,
                                         BaseDescr fact ) {
        fact.setStartCharacter( getStartOffsetLocation( label ) );
        if ( fact instanceof OrDescr ) {
            OrDescr or = (OrDescr) fact;
            for ( Object descr : or.getDescrs() ) {
                setupPatternBiding( label,
                                    (BaseDescr) descr );
            }
        } else if ( fact instanceof PatternDescr ) {
            ((PatternDescr) fact).setIdentifier( label.getText() );
        } else {
            throw new RuntimeDroolsException( "This is a bug. Please contact the Development Team. Only Patterns or OrDescr may have attached bindings. Found: " + fact.getClass().getName() );
        }
        return fact;
    }

    /**
     * Factory method that creates an OrDescr.
     * 
     * @param start
     *            "or" statement or symbol, used to get offset location
     * @param left
     *            left descriptor
     * @param right
     *            right descriptor
     * @return OrDescr filled.
     * @see OrDescr
     */
    public OrDescr createFactOr( DroolsTree start,
                                 BaseDescr left,
                                 BaseDescr right ) {
        OrDescr or = new OrDescr();
        or.addDescr( left );
        or.addDescr( right );
        return or;
    }

    /**
     * Setup FieldConstraintDescr
     * 
     * @param field
     *            field descriptor
     * @param descr
     *            binded descriptor
     * @return FieldConstraintDescr filled
     * @see FieldConstraintDescr
     */
    public FieldConstraintDescr setupFieldConstraint(
                                                      FieldConstraintDescr field,
                                                      BaseDescr descr ) {
        if ( null != descr && descr instanceof RestrictionDescr ) {
            field.getRestriction().addOrMerge( (RestrictionDescr) descr );
        }
        return field;
    }

    /**
     * Factory method that creates a FieldBindingDescr.
     * 
     * @param label
     *            bind identifier
     * @param descr
     *            binded field constraint descriptor
     * @return FieldBindingDescr filled
     * @see FieldBindingDescr
     */
    public FieldBindingDescr createFieldBinding( DroolsTree label,
                                                 BaseDescr descr ) {
        FieldBindingDescr fieldBiding = new FieldBindingDescr();
        fieldBiding.setLocation( getLineLocation( label ),
                                 getColumnLocation( label ) );
        fieldBiding.setStartCharacter( getStartOffsetLocation( label ) );
        FieldConstraintDescr fieldConstraint = (FieldConstraintDescr) descr;
        fieldBiding.setIdentifier( label.getText() );
        fieldBiding.setFieldName( fieldConstraint.getFieldName() );
        if ( !fieldConstraint.getRestrictions().isEmpty() ) {
            fieldBiding.setFieldConstraint( fieldConstraint );
        }
        return fieldBiding;
    }

    /**
     * Factory method that creates a PredicateDescr.
     * 
     * @param pc
     *            chunk data
     * @return PredicateDescr filled
     * @see PredicateDescr
     */
    public PredicateDescr createPredicate( DroolsTree pc ) {
        PredicateDescr predicate = new PredicateDescr();
        predicate.setContent( pc.getText().subSequence( 1,
                                                        pc.getText().length() - 1 ) );
        predicate.setEndCharacter( getEndOffsetLocation( pc ) );
        return predicate;
    }

    /**
     * Method that setups descriptor with operator and negated information.
     * 
     * This method just setups EvaluatorBasedRestrictionDescr descriptors.
     * 
     * @param operator
     *            operator
     * @param not
     *            negated operator
     * @param descr
     *            descriptor
     * @return descriptor setted.
     * @see EvaluatorBasedRestrictionDescr
     */
    public BaseDescr setupRestriction( DroolsTree operator,
                                       DroolsTree not,
                                       BaseDescr descr ) {
        if ( descr instanceof EvaluatorBasedRestrictionDescr ) {
            EvaluatorBasedRestrictionDescr evaluator = (EvaluatorBasedRestrictionDescr) descr;
            evaluator.setEvaluator( operator.getText() );
            if ( null == not ) {
                evaluator.setNegated( false );
            } else {
                evaluator.setNegated( true );
            }
        }
        return descr;
    }

    /**
     * Method that setups descriptor with operator and negated information.
     * 
     * This method just setups EvaluatorBasedRestrictionDescr descriptors.
     * 
     * @param operator
     *            operator
     * @param not
     *            negated operator
     * @param descr
     *            descriptor
     * @param param
     *            parameter
     * @return descriptor setted.
     * @see EvaluatorBasedRestrictionDescr
     */
    public BaseDescr setupRestriction( DroolsTree operator,
                                       DroolsTree not,
                                       BaseDescr descr,
                                       DroolsTree param ) {
        BaseDescr retDescr = setupRestriction( operator,
                                               not,
                                               descr );
        if ( null != param && descr instanceof EvaluatorBasedRestrictionDescr ) {
            EvaluatorBasedRestrictionDescr evaluator = (EvaluatorBasedRestrictionDescr) descr;
            evaluator.setParameterText( param.getText().substring( 1,
                                                                   param.getText().length() - 1 ) );
        }
        return retDescr;
    }

    /**
     * Factory method that creates a RestrictionConnectiveDescr.
     * 
     * RestrictionConnectiveDescr is just a syntax suggar to implement severals
     * "ORs" or "ANDs".
     * 
     * @param not
     *            negated operator
     * @param exprList
     *            binded expression descriptor list
     * @return RestrictionConnectiveDescr filled.
     * @see RestrictionConnectiveDescr
     */
    public RestrictionConnectiveDescr createRestrictionConnective(
                                                                   DroolsTree not,
                                                                   List<BaseDescr> exprList ) {

        RestrictionConnectiveDescr group;
        String op = null;

        if ( null == not ) {
            op = "==";
            group = new RestrictionConnectiveDescr(
                                                    RestrictionConnectiveDescr.OR );
        } else {
            op = "!=";
            group = new RestrictionConnectiveDescr(
                                                    RestrictionConnectiveDescr.AND );
        }

        for ( BaseDescr baseDescr : exprList ) {
            if ( baseDescr instanceof EvaluatorBasedRestrictionDescr ) {
                EvaluatorBasedRestrictionDescr evaluator = (EvaluatorBasedRestrictionDescr) baseDescr;
                evaluator.setEvaluator( op );
                evaluator.setNegated( false );
                group.addRestriction( evaluator );
            }
        }

        return group;
    }

    /**
     * Factory method that creates a RestrictionConnectiveDescr of type OR or an
     * OrDescr (based on left paramater).
     * 
     * If left parameter is a kind of RestrictionDescr (except for
     * PredicateDescr), this method creates a RestrictionConnectiveDescr, if not
     * creates an OrDescr.
     * 
     * @param left
     *            left descriptor
     * @param right
     *            right descriptor
     * @return RestrictionConnectiveDescr or OrDescr filled.
     * @see RestrictionConnectiveDescr
     * @see OrDescr
     */
    public BaseDescr createOrRestrictionConnective( BaseDescr left,
                                                    BaseDescr right ) {
        BaseDescr or = null;
        if ( left instanceof RestrictionDescr
                && !(left instanceof PredicateDescr) ) {
            RestrictionConnectiveDescr restOr = new RestrictionConnectiveDescr(
                                                                                RestrictionConnectiveDescr.OR );
            restOr.addOrMerge( (RestrictionDescr) left );
            restOr.addOrMerge( (RestrictionDescr) right );
            or = restOr;
        } else {
            OrDescr consOr = new OrDescr();
            consOr.addOrMerge( left );
            consOr.addOrMerge( right );
            or = consOr;
        }

        return or;

    }

    /**
     * Factory method that creates a RestrictionConnectiveDescr of type AND or
     * an AndDescr (based on left paramater).
     * 
     * If left parameter is a kind of RestrictionDescr (except for
     * PredicateDescr), this method creates a RestrictionConnectiveDescr, if not
     * creates an AndDescr.
     * 
     * @param left
     *            left descriptor
     * @param right
     *            right descriptor
     * @return RestrictionConnectiveDescr or AndDescr filled.
     * @see RestrictionConnectiveDescr
     * @see AndDescr
     */
    public BaseDescr createAndRestrictionConnective( BaseDescr left,
                                                     BaseDescr right ) {
        BaseDescr and = null;
        if ( left instanceof RestrictionDescr
                && !(left instanceof PredicateDescr) ) {
            RestrictionConnectiveDescr restAnd = new RestrictionConnectiveDescr(
                                                                                 RestrictionConnectiveDescr.AND );
            restAnd.addOrMerge( (RestrictionDescr) left );
            restAnd.addOrMerge( (RestrictionDescr) right );
            and = restAnd;
        } else {
            AndDescr consAnd = new AndDescr();
            consAnd.addOrMerge( left );
            consAnd.addOrMerge( right );
            and = consAnd;
        }

        return and;
    }

    /**
     * Factory method that creates an EvaluatorBasedRestrictionDescr.
     * 
     * @param aeList
     *            accessor element list
     * @return EvaluatorBasedRestrictionDescr filled.
     * @see EvaluatorBasedRestrictionDescr
     */
    public BaseDescr createAccessorPath( List<BaseDescr> aeList ) {
        StringBuilder sb = new StringBuilder();
        sb.append( aeList.get( 0 ).getText() );
        if ( aeList.size() > 1 ) {
            for ( int i = 1; i < aeList.size(); i++ ) {
                sb.append( "." );
                sb.append( aeList.get( i ).getText() );
            }
        }

        EvaluatorBasedRestrictionDescr evaluator;
        String name = sb.toString();
        if ( name.indexOf( '.' ) > -1 || name.indexOf( '[' ) > -1 ) {
            evaluator = new QualifiedIdentifierRestrictionDescr();
        } else {
            evaluator = new VariableRestrictionDescr();
        }
        evaluator.setText( name );

        return evaluator;
    }

    /**
     * Factory method that creates a LiteralRestrictionDescr of String type,
     * also remove the quotes.
     * 
     * @param s
     *            string data
     * @return LiteralRestrictionDescr filled
     * @see LiteralRestrictionDescr
     */
    public LiteralRestrictionDescr createStringLiteralRestriction( DroolsTree s ) {
        LiteralRestrictionDescr stringLit = new LiteralRestrictionDescr();
        stringLit.setType( LiteralRestrictionDescr.TYPE_STRING );
        stringLit.setText( s.getText().substring( 1,
                                                  s.getText().length() - 1 ) );
        return stringLit;
    }

    /**
     * Factory method that creates a LiteralRestrictionDescr of Number type.
     * 
     * @param i
     *            integer data
     * @return LiteralRestrictionDescr filled
     * @see LiteralRestrictionDescr
     */
    public LiteralRestrictionDescr createIntLiteralRestriction( DroolsTree i,
                                                                boolean negative ) {
        LiteralRestrictionDescr intLit = new LiteralRestrictionDescr();
        intLit.setType( LiteralRestrictionDescr.TYPE_NUMBER );
        intLit.setText( (negative ? "-" : "") + i.getText() );
        return intLit;
    }

    /**
     * Factory method that creates a LiteralRestrictionDescr of Number type.
     * 
     * @param f
     *            float data
     * @return LiteralRestrictionDescr filled
     * @see LiteralRestrictionDescr
     */
    public LiteralRestrictionDescr createFloatLiteralRestriction( DroolsTree f,
                                                                  boolean negative ) {
        LiteralRestrictionDescr floatLit = new LiteralRestrictionDescr();
        floatLit.setType( LiteralRestrictionDescr.TYPE_NUMBER );
        floatLit.setText( (negative ? "-" : "") + f.getText() );
        return floatLit;
    }

    /**
     * Factory method that creates a LiteralRestrictionDescr of Boolean type.
     * 
     * @param b
     *            boolean data
     * @return LiteralRestrictionDescr filled
     * @see LiteralRestrictionDescr
     */
    public LiteralRestrictionDescr createBoolLiteralRestriction( DroolsTree b ) {
        LiteralRestrictionDescr boolLit = new LiteralRestrictionDescr();
        boolLit.setType( LiteralRestrictionDescr.TYPE_BOOLEAN );
        boolLit.setText( b.getText() );
        return boolLit;
    }

    /**
     * Factory method that creates a LiteralRestrictionDescr of Null type.
     * 
     * @param n
     *            null data
     * @return LiteralRestrictionDescr filled
     * @see LiteralRestrictionDescr
     */
    public LiteralRestrictionDescr createNullLiteralRestriction( DroolsTree n ) {
        LiteralRestrictionDescr nullLit = new LiteralRestrictionDescr();
        nullLit.setType( LiteralRestrictionDescr.TYPE_NULL );
        nullLit.setText( null );
        return nullLit;
    }

    /**
     * Factory method that creates a ReturnValueRestrictionDescr.
     * 
     * @param pc
     *            chunk data
     * @return ReturnValueRestrictionDescr filled
     * @see ReturnValueRestrictionDescr
     */
    public ReturnValueRestrictionDescr createReturnValue( DroolsTree pc ) {
        ReturnValueRestrictionDescr returnValue = new ReturnValueRestrictionDescr();
        returnValue.setContent( pc.getText().substring( 1,
                                                        pc.getText().length() - 1 ) );
        returnValue.setLocation( getLineLocation( pc ),
                                 getColumnLocation( pc ) );
        returnValue.setStartCharacter( getStartOffsetLocation( pc ) );
        returnValue.setEndCharacter( getEndOffsetLocation( pc ) );
        return returnValue;
    }

    /**
     * Factory method that creates a FieldConstraintDescr.
     * 
     * @param aeList
     *            accessor element descriptor list
     * @return FieldConstraintDescr filled.
     */
    public FieldConstraintDescr createFieldConstraint( List<BaseDescr> aeList ) {
        StringBuilder sb = new StringBuilder();
        sb.append( aeList.get( 0 ).getText() );
        if ( aeList.size() > 1 ) {
            for ( int i = 1; i < aeList.size(); i++ ) {
                sb.append( "." );
                sb.append( aeList.get( i ).getText() );
            }
        }
        FieldConstraintDescr fieldConstraint = new FieldConstraintDescr( sb
                .toString() );
        fieldConstraint.setLocation( aeList.get( 0 ).getLine(),
                                     aeList.get( 0 )
                                             .getColumn() );
        fieldConstraint.setStartCharacter( aeList.get( 0 ).getStartCharacter() );
        fieldConstraint.setEndCharacter( aeList.get( aeList.size() - 1 )
                .getEndCharacter() );

        return fieldConstraint;
    }

    /**
     * Factory method that creates an accessor element descriptor.
     * 
     * @param id
     *            accessor identifier
     * @param squareChunk
     *            chunk data
     * @return BaseDescr accessor descriptor filled.
     */
    public BaseDescr createAccessorElement( DroolsTree id,
                                            List<DroolsTree> squareChunk ) {
        BaseDescr element = new BaseDescr();
        element.setLocation( getLineLocation( id ),
                             getColumnLocation( id ) );
        element.setStartCharacter( getStartOffsetLocation( id ) );
        element.setEndCharacter( getEndOffsetLocation( id ) );
        StringBuilder sb = new StringBuilder();
        sb.append( id.getText() );
        if ( null != squareChunk && squareChunk.size() > 0 ) {
            for ( DroolsTree chunk : squareChunk ) {
                sb.append( chunk.getText() );
            }
            element.setEndCharacter( getEndOffsetLocation( squareChunk
                    .get( squareChunk.size() - 1 ) ) );
        }
        element.setText( sb.toString() );
        return element;
    }

    /**
     * A factory method that returns a data type descriptor.
     * 
     * @param idList
     *            identifier list
     * @param rightList
     *            right square list, may be null
     * @return BaseDescr data type filled.
     */
    public BaseDescr createDataType( List<DroolsTree> idList,
                                     List<DroolsTree> rightList ) {
        return createGenericBaseDescr( idList,
                                       rightList );
    }

    /**
     * Helper method that creates a full identifier descriptor with correct char
     * offset.
     * 
     * @param idList
     *            identifiers
     * @param rightList
     *            right square list, may be null
     * @return BaseDescr filled.
     */
    private BaseDescr createGenericBaseDescr( List<DroolsTree> idList,
                                              List<DroolsTree> rightList ) {
        int endLine = getLineLocation( idList.get( idList.size() - 1 ) );
        int endColumn = getEndOffsetLocation( idList.get( idList.size() - 1 ) );
        int endChar = getEndOffsetLocation( idList.get( idList.size() - 1 ) );

        StringBuilder text = new StringBuilder();
        text.append( createDotedIdFromList( idList ) );

        if ( null != rightList && rightList.size() > 0 ) {
            for ( int i = 0; i < rightList.size(); i++ ) {
                text.append( "[]" );
            }

            endLine = getLineLocation( rightList.get( idList.size() - 1 ) );
            endColumn = getEndOffsetLocation( rightList.get( idList.size() - 1 ) );
            endChar = getEndOffsetLocation( rightList.get( idList.size() - 1 ) );
        }

        BaseDescr baseDescr = new BaseDescr();
        baseDescr.setText( text.toString() );
        baseDescr.setLocation( getLineLocation( idList.get( 0 ) ),
                               getColumnLocation( idList.get( 0 ) ) );
        baseDescr.setEndLocation( endLine,
                                  endColumn );
        baseDescr.setEndCharacter( endChar );

        return baseDescr;
    }

    /**
     * Helper method returns a dotted identifier based on identifier list.
     * 
     * @param idList
     *            identifiers
     * @return dotted identifier
     */
    private String createDotedIdFromList( List<DroolsTree> idList ) {
        StringBuilder sb = new StringBuilder();

        if ( null != idList && idList.size() > 0 ) {
            for ( DroolsTree commonTree : idList ) {
                sb.append( commonTree.getText() );
                sb.append( "." );
            }
            sb.deleteCharAt( sb.length() - 1 );
        }

        return sb.toString();
    }

    /**
     * Helper method that returns an id text without initial and final quotes
     * (if it starts with).
     * 
     * @param id
     *            id
     * @return text without quotes
     */
    private String getCleanId( DroolsTree id ) {
        String cleanedId = id.getText();
        if ( cleanedId.startsWith( "\"" ) || cleanedId.startsWith( "'" ) ) {
            cleanedId = cleanedId.substring( 1,
                                             cleanedId.length() - 1 ).trim();
        }
        return cleanedId;
    }

    /**
     * Helper method that returns line location
     * 
     * @param tree
     *            tree
     * @return line location
     */
    private int getLineLocation( DroolsTree tree ) {
        return tree.getLine();
    }

    /**
     * Helper method that returns column location
     * 
     * @param tree
     *            tree
     * @return column location
     */
    private int getColumnLocation( DroolsTree tree ) {
        return tree.getCharPositionInLine();
    }

    /**
     * Helper method that returns start char offset
     * 
     * @param tree
     *            tree
     * @return start char offset
     */
    private int getStartOffsetLocation( DroolsTree tree ) {
        return tree.getStartCharOffset();
    }

    /**
     * Helper method that returns end char offset
     * 
     * @param tree
     *            tree
     * @return end char offset
     */
    private int getEndOffsetLocation( DroolsTree tree ) {
        return tree.getEndCharOffset();
    }
}