/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;

public class OnlineDatingTest extends BaseInterpretedVsCompiledTest {

    @ParameterizedTest
    @MethodSource("params")
    void dMChallengeMarch2017(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("dmcommunity_challenge_2017_03.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_1b5a3a8f-ccf0-459b-8783-38601977e828",
                "DMCommunity Challenge - March 2017" );

        final Map<?, ?> lonelySoul = createProfile("Bob", "Male", "Boston", 30,
                                                   Arrays.asList( "swimming", "cinema", "jogging", "writing" ),
                                                   25, 35, Collections.singletonList("Female"), 1 );
        final List<Map<?, ?>> profiles = createProfiles();

        final DMNContext ctx = runtime.newContext();
        ctx.set( "Lonely Soul", lonelySoul );
        ctx.set( "Potential Soul Mates", profiles );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx );

        System.out.format( "Matches for %s:%n", lonelySoul.get( "Name" ) );
        int i = 0;
        for ( final Map<String, Object> soulMate : (List<Map<String, Object>>) dmnResult.getContext().get("Sorted Souls" ) ) {
            System.out.format( "%d. %-10s - Score = %2.0f%n", ++i, ((Map<String, Object>) soulMate.get( "Profile2" )).get( "Name" ), soulMate.get( "Score" ) );
        }
    }

    private List<Map<?, ?>> createProfiles() {
        final List<Map<?, ?>> profiles = new ArrayList<>();
        profiles.add( createProfile("Alice", "Female", "Boston", 28,
                                    Arrays.asList( "cinema", "singing", "dancing" ),
                                    20, 30, Collections.singletonList("Male"), 1 ) );
        profiles.add( createProfile("Charlie", "Male", "New York", 28,
                                    Arrays.asList( "dancing", "writing", "hiking" ),
                                    30, 40, Collections.singletonList("Female"), 2 ) );
        profiles.add( createProfile("Donna", "Female", "Boston", 32,
                                    Arrays.asList( "swimming", "cinema", "jogging", "writing" ),
                                    30, 40, Collections.singletonList("Female"), 2 ) );
        profiles.add( createProfile("Eleonore", "Female", "Boston", 30,
                                    Arrays.asList( "swimming", "cinema", "dancing", "writing" ),
                                    22, 32, Collections.singletonList("Male"), 2 ) );
        profiles.add( createProfile( "Fernand", "Male", "New York", 31,
                                     Arrays.asList( "cinema", "dancing", "jogging" ),
                                     28, 36, Arrays.asList( "Male", "Female" ), 1 ) );
        profiles.add( createProfile( "Grace", "Female", "Boston", 25,
                                     Arrays.asList( "cinema", "dancing", "jogging" ),
                                     28, 36, Arrays.asList( "Male", "Female" ), 1 ) );
        profiles.add( createProfile("Hector", "Male", "Boston", 29,
                                    Arrays.asList( "hiking", "racing", "jogging" ),
                                    20, 27, Collections.singletonList("Female"), 1 ) );
        profiles.add( createProfile("Isis", "Female", "Boston", 29,
                                    Arrays.asList( "hiking", "racing", "jogging", "cinema", "swimming" ),
                                    20, 40, Collections.singletonList("Male"), 1 ) );
        return profiles;
    }

    private Map<String, Object> createProfile(
            final String name, final String gender, final String city, final int age,
            final List<String> interests, final int minAge, final int maxAge, final List<String> genders,
            final int matchingInterests) {
        final Map<String, Object> profile = new HashMap<>();
        profile.put( "Name", name );
        profile.put( "Gender", gender );
        profile.put( "City", city );
        profile.put( "Age", age );
        profile.put( "List of Interests", interests );
        profile.put( "Minimum Acceptable Age", minAge );
        profile.put( "Maximum Acceptable Age", maxAge );
        profile.put( "Acceptable Genders", genders );
        profile.put( "Minimum Matching Interests", matchingInterests );
        return profile;
    }
}
