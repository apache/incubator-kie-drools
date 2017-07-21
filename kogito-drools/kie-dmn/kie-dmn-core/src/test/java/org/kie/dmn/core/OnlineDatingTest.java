package org.kie.dmn.core;

import org.junit.Test;
import org.kie.dmn.api.core.*;
import org.kie.dmn.core.util.DMNRuntimeUtil;

import java.util.*;

public class OnlineDatingTest {

    @Test
    public void testDMChallengeMarch2017() {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "dmcommunity_challenge_2017_03.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel(
                "http://www.trisotech.com/definitions/_1b5a3a8f-ccf0-459b-8783-38601977e828",
                "DMCommunity Challenge - March 2017" );

        Map<?, ?> lonelySoul = createProfile( "Bob", "Male", "Boston", 30,
                                              Arrays.asList( "swimming", "cinema", "jogging", "writing" ),
                                              25, 35, Arrays.asList( "Female" ), 1 );
        List<Map<?, ?>> profiles = createProfiles();

        DMNContext ctx = runtime.newContext();
        ctx.set( "Lonely Soul", lonelySoul );
        ctx.set( "Potential Soul Mates", profiles );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, ctx );

        System.out.format( "Matches for %s:%n", lonelySoul.get( "Name" ) );
        int i = 0;
        for ( Map<String, Object> soulMate : (List<Map<String, Object>>) dmnResult.getContext().get( "Sorted Souls" ) ) {
            System.out.format( "%d. %-10s - Score = %2.0f%n", ++i, ((Map<String, Object>) soulMate.get( "Profile2" )).get( "Name" ), soulMate.get( "Score" ) );
        }
    }

    private List<Map<?, ?>> createProfiles() {
        List<Map<?, ?>> profiles = new ArrayList<>();
        profiles.add( createProfile( "Alice", "Female", "Boston", 28,
                                     Arrays.asList( "cinema", "singing", "dancing" ),
                                     20, 30, Arrays.asList( "Male" ), 1 ) );
        profiles.add( createProfile( "Charlie", "Male", "New York", 28,
                                     Arrays.asList( "dancing", "writing", "hiking" ),
                                     30, 40, Arrays.asList( "Female" ), 2 ) );
        profiles.add( createProfile( "Donna", "Female", "Boston", 32,
                                     Arrays.asList( "swimming", "cinema", "jogging", "writing" ),
                                     30, 40, Arrays.asList( "Female" ), 2 ) );
        profiles.add( createProfile( "Eleonore", "Female", "Boston", 30,
                                     Arrays.asList( "swimming", "cinema", "dancing", "writing" ),
                                     22, 32, Arrays.asList( "Male" ), 2 ) );
        profiles.add( createProfile( "Fernand", "Male", "New York", 31,
                                     Arrays.asList( "cinema", "dancing", "jogging" ),
                                     28, 36, Arrays.asList( "Male", "Female" ), 1 ) );
        profiles.add( createProfile( "Grace", "Female", "Boston", 25,
                                     Arrays.asList( "cinema", "dancing", "jogging" ),
                                     28, 36, Arrays.asList( "Male", "Female" ), 1 ) );
        profiles.add( createProfile( "Hector", "Male", "Boston", 29,
                                     Arrays.asList( "hiking", "racing", "jogging" ),
                                     20, 27, Arrays.asList( "Female" ), 1 ) );
        profiles.add( createProfile( "Isis", "Female", "Boston", 29,
                                     Arrays.asList( "hiking", "racing", "jogging", "cinema", "swimming" ),
                                     20, 40, Arrays.asList( "Male" ), 1 ) );
        return profiles;
    }

    private Map<String, Object> createProfile(
            String name, String gender, String city, int age,
            List<String> interests, int minAge, int maxAge, List<String> genders,
            int matchingInterests) {
        Map<String, Object> profile = new HashMap<>();
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
