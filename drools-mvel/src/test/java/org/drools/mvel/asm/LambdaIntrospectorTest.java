package org.drools.mvel.asm;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Supplier;

import org.drools.mvel.compiler.Person;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LambdaIntrospectorTest {

    @Test
    public void testLambdaFingerprint() {
        LambdaIntrospector lambdaIntrospector = new LambdaIntrospector();
        Predicate1<Person> predicate1 = p -> p.getAge() > 35;
        String fingerprint = lambdaIntrospector.apply(predicate1);

        assertThat(fingerprint.contains("ALOAD 0")).isTrue();
        assertThat(fingerprint.contains("INVOKEVIRTUAL org/drools/mvel/compiler/Person.getAge()I")).isTrue();
    }

    @Test
    public void testMaterializedLambdaFingerprint() {
        LambdaIntrospector lambdaIntrospector = new LambdaIntrospector();
        String fingerprint = lambdaIntrospector.apply(LambdaPredicate21D56248F6A2E8DA3990031D77D229DD.INSTANCE);

        assertThat(fingerprint).isEqualTo("4DEB93975D9859892B1A5FD4B38E2155");
    }

    public enum LambdaPredicate21D56248F6A2E8DA3990031D77D229DD implements Predicate1<Person> {

        INSTANCE;

        public static final String EXPRESSION_HASH = "4DEB93975D9859892B1A5FD4B38E2155";

        @Override()
        public boolean test(Person p) {
            return p.getAge() > 35;
        }
    }

    @Test
    public void testMethodFingerprintsMapCacheSize() throws Exception {
        // To test system property, you need to run this test method only.
        // (mvn test -Dtest=LambdaIntrospectorTest#testMethodFingerprintsMapCacheSize)
        // System.setProperty(LambdaIntrospector.LAMBDA_INTROSPECTOR_CACHE_SIZE, "0");

        Map<ClassLoader, LambdaIntrospector.ClassesFingerPrintsCache> methodFingerprintsMapPerClassLoader = LambdaIntrospector.getMethodFingerprintsMapPerClassLoader();
        methodFingerprintsMapPerClassLoader.clear();

        LambdaIntrospector lambdaIntrospector = new LambdaIntrospector();

        Predicate1<Person> predicate1 = p -> p.getAge() > 35;
        lambdaIntrospector.apply(predicate1);

        LambdaIntrospector.ClassesFingerPrintsCache methodFingerprintsMap = methodFingerprintsMapPerClassLoader.get(predicate1.getClass().getClassLoader());

        assertThat(methodFingerprintsMap.size()).isEqualTo(1); // methodFingerprintsMap is null if cache size is 0.
    }

    public interface Predicate1<A> extends Serializable {
        boolean test(A a) throws Exception;

        default Predicate1<A> negate() {
            return a -> !test( a );
        }

        class Impl<A> implements Predicate1<A>, Supplier<Object> {

            private final Predicate1<A> predicate;

            public Impl(Predicate1<A> predicate) {
                this.predicate = predicate;
            }

            @Override
            public boolean test(A a) throws Exception {
                return predicate.test(a);
            }

            @Override
            public Object get() {
                return predicate;
            }
        }
    }
}
