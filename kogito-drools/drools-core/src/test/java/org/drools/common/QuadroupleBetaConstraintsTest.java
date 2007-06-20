/**
 * 
 */
package org.drools.common;

import org.drools.Person;
import org.drools.RuleBaseConfiguration;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassFieldExtractorFactory;
import org.drools.base.ClassObjectType;
import org.drools.base.evaluators.Operator;
import org.drools.base.evaluators.StringFactory;
import org.drools.rule.Pattern;
import org.drools.rule.Declaration;
import org.drools.rule.VariableConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.Evaluator;
import org.drools.spi.FieldExtractor;

import junit.framework.TestCase;

/**
 * @author etirelli
 *
 */
public class QuadroupleBetaConstraintsTest extends TestCase {

    private RuleBaseConfiguration     conf;
    private BetaNodeFieldConstraint[] constraints;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        conf = new RuleBaseConfiguration();

        constraints = new BetaNodeFieldConstraint[4];
        Class clazz = Person.class;
        constraints[0] = getConstraint( "street",
                                        clazz );
        constraints[1] = getConstraint( "city",
                                        clazz );
        constraints[2] = getConstraint( "state",
                                        clazz );
        constraints[3] = getConstraint( "country",
                                        clazz );
        super.setUp();
    }

    private BetaNodeFieldConstraint getConstraint(String fieldName,
                                                  Class clazz) {
        FieldExtractor extractor = ClassFieldExtractorCache.getExtractor( clazz,
                                                                          fieldName,
                                                                          getClass().getClassLoader() );
        Declaration declaration = new Declaration( fieldName,
                                                   extractor,
                                                   new Pattern( 0,
                                                                new ClassObjectType( clazz ) ) );
        Evaluator evaluator = StringFactory.getInstance().getEvaluator( Operator.EQUAL );
        return new VariableConstraint( extractor,
                                       declaration,
                                       evaluator );
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link org.drools.common.QuadroupleBetaConstraints#QuadroupleBetaConstraints(org.drools.spi.BetaNodeFieldConstraint[], org.drools.RuleBaseConfiguration)}.
     */
    public void testQuadroupleBetaConstraints() {
        try {
            QuadroupleBetaConstraints qbc = new QuadroupleBetaConstraints( constraints,
                                                                           conf );
        } catch ( Exception e ) {
            fail( "Should not raise any exception: " + e.getMessage() );
        }

    }

}
