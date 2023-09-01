package org.drools.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtils {

    private StreamUtils() {

    }

    public static <T> Stream<T> optionalToStream(Optional<T> opt) {
        return opt.map(Stream::of).orElse(Stream.empty());
    }
    

    public static <T> List<T> optionalToList(Optional<T> opt) {
        return optionalToStream(opt).collect(Collectors.toList());
    }
}
