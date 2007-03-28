package org.drools.clp;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.clp.functions.PlusFunction;
import org.drools.clp.valuehandlers.CLPLocalDeclarationVariable;
import org.drools.clp.valuehandlers.CLPPreviousDeclarationVariable;
import org.drools.clp.valuehandlers.FunctionCaller;
import org.drools.clp.valuehandlers.LocalVariableValue;
import org.drools.clp.valuehandlers.ObjectValueHandler;
import org.drools.clp.valuehandlers.TempTokenVariable;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.Column;
import org.drools.rule.Declaration;
import org.drools.spi.ColumnExtractor;
import org.drools.spi.ObjectType;

import junit.framework.TestCase;

public class AddFunctionTest extends TestCase {
    public void testAdd() {
        ValueHandler val1 = new ObjectValueHandler( new BigDecimal( 10 ) );
        ValueHandler val2 = new ObjectValueHandler( new BigDecimal( 10 ) );

        ValueHandler[] params = new ValueHandler[]{val1, val2};
        PlusFunction add = new PlusFunction();

        assertEquals( new BigDecimal( 20 ),
                      add.execute( params,
                                   null ).getObject( null ) );
    }

    public void testNestedAdd() {
        ValueHandler val1 = new ObjectValueHandler( new BigDecimal( 10 ) );
        ValueHandler val2 = new ObjectValueHandler( new BigDecimal( 10 ) );
        ValueHandler val3 = new ObjectValueHandler( new BigDecimal( 10 ) );

        FunctionCaller functionValue = new FunctionCaller( new PlusFunction() );
        functionValue.addParameter( val1 );
        functionValue.addParameter( val2 );

        ValueHandler[] params = new ValueHandler[]{val3, functionValue};

        PlusFunction add = new PlusFunction();

        assertEquals( new BigDecimal( 30 ),
                      add.execute( params,
                                   null ).getObject( null ) );
    }

    public void testNestedAddWithVars() {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) ruleBase.newWorkingMemory();

        InternalFactHandle factHandle = (InternalFactHandle) workingMemory.assertObject( new BigDecimal( 10 ) );
        ReteTuple tuple = new ReteTuple( factHandle );

        ObjectType objectType = new ClassObjectType( BigDecimal.class );
        Column column0 = new Column( 0,
                                     objectType );
        Column column1 = new Column( 1,
                                     objectType );
        ColumnExtractor extractor = new ColumnExtractor( objectType );

//        VariableValueHandler pd = new CLPPreviousDeclarationVariable( new Declaration( "pd",
//                                                                                       extractor,
//                                                                                       column0 ) );
//
//        VariableValueHandler ld = new CLPLocalDeclarationVariable( new Declaration( "ld",
//                                                                                    extractor,
//                                                                                    column1 ) );

        Map variables = new HashMap();
        variables.put( "pd",
                       new CLPPreviousDeclarationVariable( new Declaration( "pd",
                                                                            extractor,
                                                                            column0 ) ) );
        variables.put( "ld",
                       new CLPLocalDeclarationVariable( new Declaration( "ld",
                                                                         extractor,
                                                                         column1 ) ) );

        ValueHandler val1 = new TempTokenVariable( "pd" );
        ValueHandler val2 = new LocalVariableValue( "lv",
                                                    0 );
        ValueHandler val3 = new TempTokenVariable( "ld" );

        ExecutionContext context = new ExecutionContext( workingMemory,
                                                         tuple,
                                                         new BigDecimal( 10 ),
                                                         1 );
        context.setLocalVariable( 0,
                                  new ObjectValueHandler( new BigDecimal( 10 ) ) );

        FunctionCaller functionValue = new FunctionCaller( new PlusFunction() );
        functionValue.addParameter( val1 );
        functionValue.addParameter( val2 );

        PlusFunction add = new PlusFunction();

        FunctionCaller f = new FunctionCaller( add );
        f.addParameter( val3 );
        f.addParameter( functionValue );

        f.replaceTempTokens( variables );

        assertEquals( new BigDecimal( 30 ),
                      f.getBigDecimalValue( context ) ); 

    }
}
