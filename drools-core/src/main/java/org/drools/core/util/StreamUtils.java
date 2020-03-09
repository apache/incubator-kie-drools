package org.drools.core.util;

import java.util.Optional;
import java.util.stream.Stream;

public class StreamUtils {

    private StreamUtils() {

    }

    public static <T> Stream<T> optionalToStream(Optional<T> opt) {
        return opt.map(Stream::of).orElse(Stream.empty());
    }
}
