package org.kie.efesto.common.api.identifiers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.componentroots.ComponentRootA;
import org.kie.efesto.common.api.identifiers.componentroots.LocalComponentIdA;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReflectiveAppRootTest {

    private static AppRoot appRoot;

    @BeforeAll
    public static void setup() {
        appRoot = new ReflectiveAppRoot("testing");
    }

    @Test
    void constructorNoName() {
        ReflectiveAppRoot retrieved = new ReflectiveAppRoot();
        assertThat(retrieved.name()).isEqualTo("efesto-app");
    }

    @Test
    void constructorName() {
        String name = "name";
        ReflectiveAppRoot retrieved = new ReflectiveAppRoot(name);
        assertThat(retrieved.name()).isEqualTo(name);
    }

    @Test
    void get() {
        String fileName = "fileName";
        String name = "name";
        LocalUri retrieved = appRoot.get(ComponentRootA.class)
                .get(fileName, name)
                .toLocalId()
                .asLocalUri();

        appRoot.get(ComponentRootA.class)
                .get(fileName, name)
                .toLocalId();

        assertThat(retrieved).isNotNull();
        String expected = String.format("/%1$s/%2$s/%3$s", LocalComponentIdA.PREFIX, fileName, name);
        assertThat(retrieved.path()).isEqualTo(expected);
    }

    @Test
    void getPrivateConstructorImplementation() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> appRoot.get(ComponentRootPrivateConstructor.class),
                "Expected constructor to throw, but it didn't"
        );
        String expectedMessage = "java.lang.NoSuchMethodException";
        assertThat(thrown.getMessage()).startsWith(expectedMessage);
    }

    @Test
    void getNoDefaultConstructorImplementation() {
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> appRoot.get(ComponentRootNoDefaultConstructor.class),
                "Expected constructor to throw, but it didn't"
        );
        String expectedMessage = "java.lang.NoSuchMethodException";
        assertThat(thrown.getMessage()).startsWith(expectedMessage);
    }

    private static class ComponentRootPrivateConstructor implements ComponentRoot {

        private ComponentRootPrivateConstructor() {
        }
    }

    private static class ComponentRootNoDefaultConstructor implements ComponentRoot {

        private final String arg;
        public ComponentRootNoDefaultConstructor(String arg) {
            this.arg = arg;
        }
    }
}