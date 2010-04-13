package org.drools.rule.builder;

import java.util.ArrayList;
import java.util.List;

import org.drools.base.ArrayElements;
import org.drools.base.ClassObjectType;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.base.extractors.SelfReferenceClassFieldReader;
import org.drools.lang.descr.LiteralDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.VariableDescr;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.rule.Query;
import org.drools.rule.QueryElement;
import org.drools.rule.Rule;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.Variable;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ObjectType;

public class QueryElementBuilder
    implements
    RuleConditionBuilder {

    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr) {
        return this.build( context,
                           descr,
                           null );
    }

    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr,
                                      Pattern prefixPattern) {
        PatternDescr patternDescr = (PatternDescr) descr;
        Query query = (Query) context.getPkg().getRule( patternDescr.getObjectType() );

        Declaration[] params = query.getParameters();

        List<BaseDescr> args = (List<BaseDescr>) patternDescr.getDescrs();
        List<Integer> declrIndexes = new ArrayList<Integer>();
        List<Integer> varIndexes = new ArrayList<Integer>();
        List<Object> arguments = new ArrayList<Object>();
        List<Declaration> requiredDeclarations = new ArrayList<Declaration>();

        ObjectType argsObjectType = new ClassObjectType( Object[].class );
        InternalReadAccessor arrayReader = new SelfReferenceClassFieldReader(Object[].class, "this");
        Pattern pattern = new Pattern( context.getNextPatternId(),
                                       0,
                                       argsObjectType,
                                       null );

        for ( int i = 0, length = args.size(); i < length; i++ ) {
            BaseDescr arg = args.get( i );

            if ( arg instanceof LiteralDescr ) {
                arguments.add( ((LiteralDescr) arg).getValue() );
            } else if ( arg instanceof VariableDescr ) {
                String var = ((VariableDescr) arg).getIdentifier();
                Declaration declr = context.getDeclarationResolver().getDeclaration( query,
                                                                                     var );
                if ( declr != null ) {
                    // declaration
                    declrIndexes.add( i );
                    arguments.add( declr );
                    requiredDeclarations.add( declr );
                } else {
                    // unification variable                    
                    declr = pattern.addDeclaration( var );

                    // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
                    ArrayElementReader reader = new ArrayElementReader( arrayReader,
                                                                        varIndexes.size(),
                                                                        params[i].getExtractor().getExtractToClass() );

                    declr.setReadAccessor( reader );

                    varIndexes.add( i );
                    arguments.add( new Variable() );
                }
            }
        }
        Declaration[] declrsArray = requiredDeclarations.toArray( new Declaration[requiredDeclarations.size()] );
        int[] declrIndexArray = new int[declrIndexes.size()];
        for( int i = 0; i < declrsArray.length; i++ ) {
            declrIndexArray[i] = declrIndexes.get( i );
        }
        int[] varIndexesArray = new int[varIndexes.size()];
        for( int i = 0; i < varIndexesArray.length; i++ ) {
            varIndexesArray[i] = varIndexes.get( i );
        }
        
        return new QueryElement( pattern,
                                 query.getName(),
                                 arguments.toArray( new Object[arguments.size()] ),
                                 declrsArray,
                                 declrIndexArray,
                                 varIndexesArray );
    }

}
