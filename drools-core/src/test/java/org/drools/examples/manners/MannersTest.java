package org.drools.examples.manners;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.drools.rule.Column;
import org.drools.rule.ColumnBinding;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Rule;
import org.drools.rule.RuleSet;
import org.drools.spi.ClassObjectType;
import org.drools.spi.BaseEvaluator;
import org.drools.spi.LiteralExpressionConstraint;

public class MannersTest {
    /** Number of guests at the dinner (default: 16). */
    private int             numGuests  = 16;

    /** Number of seats at the table (default: 16). */
    private int             numSeats   = 16;

    /** Minimum number of hobbies each guest should have (default: 2). */
    private int             minHobbies = 2;

    /** Maximun number of hobbies each guest should have (default: 3). */
    private int             maxHobbies = 3;

    private ClassObjectType contextType;
    private ClassObjectType guestType;
    private ClassObjectType seatingType;
    private ClassObjectType lastSeatType;

    protected void setUp() throws Exception {
        contextType = new ClassObjectType( Context.class );
        guestType = new ClassObjectType( Guest.class );
        seatingType = new ClassObjectType( Seating.class );
        lastSeatType = new ClassObjectType( LastSeat.class );

        RuleSet ruleSet = new RuleSet( "Miss Manners" );
        Rule assignFirstSeat = getAssignFirstSeatRule();

    }

    private Rule getAssignFirstSeatRule() {
        Rule rule = new Rule( "assignFirstSeat" );

        Column context = new Column( 0,
                                     contextType,
                                     "context" );

//        LiteralExpressionConstraint isCheddar = new LiteralExpressionConstraint() {
//
//            public boolean isAllowed(Object object,
//                                     BaseEvaluator comparator) {
//                Context context = (Context) object;
//                return comparator.compare( context,
//                                           Context.START_UP );
//            }
//
//        };

        /*
         * Creates a constraint with the given expression
         */
//        LiteralConstraint constraint0 = new LiteralConstraint( isCheddar,
//                                                               new ObjectConstraintComparator( BaseEvaluator.EQUAL ) );

        // context.

        return rule;
    }

    private InputStream generateData() {
        final String LINE_SEPARATOR = System.getProperty( "line.separator" );

        StringWriter writer = new StringWriter();

        int maxMale = numGuests / 2;
        int maxFemale = numGuests / 2;

        int maleCount = 0;
        int femaleCount = 0;

        // init hobbies
        List hobbyList = new ArrayList();
        for ( int i = 1; i <= maxHobbies; i++ ) {
            hobbyList.add( "h" + i );
        }

        Random rnd = new Random();
        for ( int i = 1; i <= numGuests; i++ ) {
            char sex = rnd.nextBoolean() ? 'm' : 'f';
            if ( sex == 'm' && maleCount == maxMale ) {
                sex = 'f';
            }
            if ( sex == 'f' && femaleCount == maxFemale ) {
                sex = 'm';
            }
            if ( sex == 'm' ) {
                maleCount++;
            }
            if ( sex == 'f' ) {
                femaleCount++;
            }

            List guestHobbies = new ArrayList( hobbyList );

            int numHobbies = minHobbies + rnd.nextInt( maxHobbies - minHobbies + 1 );
            for ( int j = 0; j < numHobbies; j++ ) {
                int hobbyIndex = rnd.nextInt( guestHobbies.size() );
                String hobby = (String) guestHobbies.get( hobbyIndex );
                writer.write( "(guest (name n" + i + ") (sex " + sex + ") (hobby " + hobby + "))" + LINE_SEPARATOR );
                guestHobbies.remove( hobbyIndex );
            }
        }
        writer.write( "(last_seat (seat " + numSeats + "))" + LINE_SEPARATOR );

        writer.write( LINE_SEPARATOR );
        writer.write( "(context (state start))" + LINE_SEPARATOR );

        return new ByteArrayInputStream( writer.getBuffer().toString().getBytes() );
    }
}
