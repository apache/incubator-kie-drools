package org.optaplanner.quarkus.drl.it;

import org.junit.jupiter.api.Disabled;

import io.quarkus.test.junit.NativeImageTest;

/**
 * Test various OptaPlanner operations running in native mode
 */
@NativeImageTest
@Disabled("Constraints DRL is not supported in native mode.")
public class OptaPlannerTestResourceIT extends OptaPlannerTestResourceTest {

}
