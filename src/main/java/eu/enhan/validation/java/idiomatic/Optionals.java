package eu.enhan.validation.java.idiomatic;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 *
 */
public class Optionals {

    private Optionals() {

    }

    public static <A,B,C> Optional<C> combine(Optional<A> a, Optional<B> b, BiFunction<A,B,C> comb) {
        return a.flatMap( aValue ->
                b.map(bValue -> comb.apply(aValue, bValue)
                )
        );
    }

}
