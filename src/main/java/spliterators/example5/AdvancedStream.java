package spliterators.example5;

import spliterators.example4.IndexedValue;
import spliterators.example4.Pair;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * AdvancedStream<Character> a = {A, B, C, D, E, F}
 * AdvancedStream<Integer> b = {1, 2, 3, 4, 5, 6}
 */
public interface AdvancedStream<T> extends Stream<T> {

    /**
     * a.takeWhile(sym -> sym < 'D') => {A, B, C}
     */
    AdvancedStream<T> takeWhile(Predicate<? super T> predicate);
}
