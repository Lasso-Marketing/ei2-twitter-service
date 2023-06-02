package io.lassomarketing.ei2.twitter.util;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public interface Functions {

    static <E> UnaryOperator<E> peek(Consumer<E> consumer) {
        return e -> {
            consumer.accept(e);
            return e;
        };
    }
}
