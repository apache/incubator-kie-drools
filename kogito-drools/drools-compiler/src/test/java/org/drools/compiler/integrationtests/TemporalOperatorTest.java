/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;

public class TemporalOperatorTest {

    @Test
    public void testAfterWithLocalDateTime() {
        checkTemporalConstraint( "localDateTime after $t1.localDateTime" );
    }

    @Test
    public void testAfterWithZonedDateTime() {
        checkTemporalConstraint( "zonedDateTime after $t1.zonedDateTime" );
    }

    @Test
    public void testAfterWithDate() {
        checkTemporalConstraint( "date after $t1.date" );
    }

    @Test
    public void testAfterWithDateUsingOr() {
        checkTemporalConstraint( "date == null || date after $t1.date" );
    }

    @Test
    public void testAfterMixDateAndLocaldateTime() {
        checkTemporalConstraint( "date after $t1.localDateTime" );
    }

    public void checkTemporalConstraint(String constraint) {
        String str = "import " + TimestampedObject.class.getCanonicalName() + ";\n" +
                     "global java.util.List list;\n" +
                     "rule R when\n" +
                     "  $t1 : TimestampedObject()\n" +
                     "  $t2 : TimestampedObject( " + constraint + " )\n" +
                     "then\n" +
                     "  list.add($t2.getName());\n" +
                     "end\n";

        KieSession ksession = new KieHelper().addContent( str, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        TimestampedObject t1 = new TimestampedObject( "t1", LocalDateTime.now() );
        TimestampedObject t2 = new TimestampedObject( "t2", LocalDateTime.now().plusHours( 1 ) );

        ksession.insert( t1 );
        ksession.insert( t2 );
        ksession.fireAllRules();

        assertEquals(t2.getName(), list.get(0));
    }

    public static class TimestampedObject {
        private final String name;
        private final LocalDateTime localDateTime;

        public TimestampedObject( String name, LocalDateTime time ) {
            this.name = name;
            this.localDateTime = time;
        }

        public String getName() {
            return name;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public ZonedDateTime getZonedDateTime() {
            return localDateTime.atZone( ZoneId.systemDefault() );
        }

        public Date getDate() {
            return Date.from(localDateTime.atZone( ZoneId.systemDefault() ).toInstant() );
        }
    }

    @Test
    public void testAfterWithConstant() {
        checkConstantTemporalConstraint( "date after \"1-Jan-1970\"" );
    }

    @Test
    public void testAfterWithConstantUsingOR() {
        // RHBRMS-2799
        checkConstantTemporalConstraint( "date == null || date after \"1-Jan-1970\"" );
    }

    @Test
    public void testAfterWithLocalDateTimeUsingOr() {
        // RHBRMS-2799
        checkConstantTemporalConstraint( "localDateTime == null || localDateTime after \"1-Jan-1970\"" );
    }

    public void checkConstantTemporalConstraint(String constraint) {
        String str = "import " + TimestampedObject.class.getCanonicalName() + ";\n" +
                     "global java.util.List list;\n" +
                     "rule R when\n" +
                     "  $t1 : TimestampedObject( " + constraint + " )\n" +
                     "then\n" +
                     "  list.add($t1.getName());\n" +
                     "end\n";

        KieSession ksession = new KieHelper().addContent( str, ResourceType.DRL )
                                             .build()
                                             .newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        TimestampedObject t1 = new TimestampedObject( "t1", LocalDateTime.now() );

        ksession.insert( t1 );
        ksession.fireAllRules();

        assertEquals(t1.getName(), list.get(0));
    }
}
