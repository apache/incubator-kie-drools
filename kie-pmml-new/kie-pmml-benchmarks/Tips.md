Tips about benchmarkings
========================

If the evaulated method is meant to be _hot_ (repeatedly called) at target runtime, do use **Throughput** or **AverageTime** Mode.
If the evaulated method is meant to be _single-shot_ (called only once) at target runtime, do use **SingleShotTime** Mode.

If the reported error is suspicious (e.g. >= 10% of the measured result) do check for garbage collection influence:

    run with "-prof gc" or "-prof gc  -gc true" to profile gc and to have a (more) predictable gc impact.

Explanations:

 -prof <profiler>            Use profilers to collect additional benchmark data.
                              Some profilers are not available on all JVMs and/or
                              all OSes. Please see the list of available profilers
                              with -lprof.

 -gc <boolean>               Should JMH force GC between iterations? Forcing
                              the GC may help to lower the noise in GC-heavy benchmarks,
                              at the expense of jeopardizing GC ergonomics decisions.
                              Use with care.

To profile with async-profiler, if most of the expected invoked methods are _small_, add the following jvmarga

    jvmargs : -XX:UnlockDiagnosticVMOptions -XX:DebugNonSafepoints