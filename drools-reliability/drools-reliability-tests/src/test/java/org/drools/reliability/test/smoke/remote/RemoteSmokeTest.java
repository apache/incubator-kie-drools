package org.drools.reliability.test.smoke.remote;

import org.drools.reliability.test.BeforeAllMethodExtension;
import org.drools.reliability.test.smoke.BaseSmokeTest;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.condition.OS.WINDOWS;

@DisabledOnOs(WINDOWS)
@EnabledIf("isRemoteInfinispan")
@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(BeforeAllMethodExtension.class)
class RemoteSmokeTest extends BaseSmokeTest {

}
