package org.drools.modelcompiler.util;

import java.lang.reflect.Field;
import java.util.Map;

import org.drools.model.functions.Predicate1;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class LambdaIntrospectorTest {

    @Test
    public void testLambdaFingerprint() {
        LambdaIntrospector lambdaIntrospector = new LambdaIntrospector();
        Predicate1<Person> predicate1 = p -> p.getAge() > 35;
        String fingerprint = lambdaIntrospector.getLambdaFingerprint(predicate1);

        assertThat(fingerprint, containsString("ALOAD 0"));
        assertThat(fingerprint, containsString("INVOKEVIRTUAL org/drools/modelcompiler/domain/Person.getAge()I"));
    }

    @Test
    public void testMaterializedLambdaFingerprint() {
        LambdaIntrospector lambdaIntrospector = new LambdaIntrospector();
        String fingerprint = lambdaIntrospector.getLambdaFingerprint(LambdaPredicate21D56248F6A2E8DA3990031D77D229DD.INSTANCE);

        assertEquals("4DEB93975D9859892B1A5FD4B38E2155", fingerprint);
    }

    public enum LambdaPredicate21D56248F6A2E8DA3990031D77D229DD implements org.drools.model.functions.Predicate1<org.drools.modelcompiler.domain.Person> {

        INSTANCE;

        public static final String EXPRESSION_HASH = "4DEB93975D9859892B1A5FD4B38E2155";

        @Override()
        public boolean test(org.drools.modelcompiler.domain.Person p) {
            return p.getAge() > 35;
        }
    }

    @Test
    public void testMethodFingerprintsMapCacheSize() throws Exception {
        // To test system property, you need to run this test method only.
        // (mvn test -Dtest=LambdaIntrospectorTest#testMethodFingerprintsMapCacheSize)
        // System.setProperty(LambdaIntrospector.LAMBDA_INTROSPECTOR_CACHE_SIZE, "0");

        Map<ClassLoader, Map<String, Map<String, String>>> methodFingerprintsMapPerClassLoader = LambdaIntrospector.getMethodFingerprintsMapPerClassLoader();
        methodFingerprintsMapPerClassLoader.clear();

        LambdaIntrospector lambdaIntrospector = new LambdaIntrospector();

        Predicate1<Person> predicate1 = p -> p.getAge() > 35;
        lambdaIntrospector.getLambdaFingerprint(predicate1);

        Map<String, Map<String, String>> methodFingerprintsMap = methodFingerprintsMapPerClassLoader.get(predicate1.getClass().getClassLoader());

        assertEquals(1, methodFingerprintsMap.size()); // methodFingerprintsMap is null if cache size is 0.
    }
}
