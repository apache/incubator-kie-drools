# Drools Reliability (experimental)

drools-reliability is a module to add reliability to stateful ksessions backed by Infinispan so that stateful ksessions can resume after a JVM crash.

## Usage
- Add drools-reliability as a dependency to your project

```xml
    <dependency>
      <groupId>org.drools</groupId>
      <artifactId>drools-reliability</artifactId>
    </dependency>
```

- Create a stateful ksession with `PersistedSessionOption.newSession`

```Java
KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
conf.setOption(PersistedSessionOption.newSession(PersistedSessionOption.Strategy.STORES_ONLY));
KieSession ksession = kbase.newKieSession(conf, null);
long savedSessionId = session.getIdentifier();
```
- Keep `savedSessionId` at the client application side. It is used to resume the session after a JVM crash.
- After a JVM crash, create a new stateful ksession with `PersistedSessionOption.fromSession` and `savedSessionId`. The created ksession will resume from the last saved state.

```Java
KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
conf.PersistedSessionOption.fromSession(savedSessionId, PersistedSessionOption.Strategy.STORES_ONLY));
KieSession ksession = kbase.newKieSession(conf, null);
long savedSessionId = session.getIdentifier();
```

## Configuration
- drools.reliability.cache.mode
  - EMBEDDED (default) : Use Infinispan embedded in the same process as Drools engine. It stores data in a filesystem.
  - REMOTE : Use remote Infinispan instances (currently, only one) to store data. It requires Infinispan server to be running.
    - Test cases note: At the moment, remote testing is not enabled by default (because containers' startup is a little time-consuming). You can run test cases with REMOTE mode with `mvn test -Premote`
    - You can find a remote mode use case example in `org.drools.reliability.example.RemoteCacheManagerExample`.
- drools.reliability.cache.allowedpackages
  - A comma-separated list of packages that are allowed to be stored in Infinispan. It is used to prevent malicious users from storing arbitrary objects in Infinispan. It is recommended to set this property to the package name of your application.
  - Default: None
- (EMBEDDED mode only) drools.reliability.cache.directory
  - The directory where Infinispan stores data.
  - Default: `global/state`
- (REMOTE mode only) drools.reliability.cache.remote.host
  - The host name of remote Infinispan server.
  - Default: None
- (REMOTE mode only) drools.reliability.cache.remote.port
  - The port number of remote Infinispan server.
  - Default: None
- (REMOTE mode only) drools.reliability.cache.remote.user
  - The username of remote Infinispan server.
  - Default: None
- (REMOTE mode only) drools.reliability.cache.remote.pass
  - The password of remote Infinispan server.
  - Default: None

- PersistedSessionOption.Strategy
  - STORES_ONLY : Persist only ObjectStore. On resume, restore the ksession state by re-propagation. It is faster than FULL at runtime, but it could be slow on the re-propagation phase if the ksession has many facts.
  - FULL (development-in-progress) : Persist various state of the ksession. On resume, restore the ksession with the persisted state. It is faster than STORES_ONLY at resume-time, but it could be slower at runtime.

