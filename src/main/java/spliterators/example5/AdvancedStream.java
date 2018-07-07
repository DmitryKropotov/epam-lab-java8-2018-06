package spliterators.example5;

import spliterators.example4.Pair;

import java.util.stream.Stream;

/**
 * AdvancedStream<Character> a = {A, B, C, D, E, F}
 * AdvancedStream<Integer> b = {1, 2, 3, 4, 5, 6}
 */
public interface AdvancedStream<T> extends Stream<T> {

    /**
     * a.zip(b) => { (A, 1), (B, 2), (C, 3)... }
     * AdvancedStream<Pair<Character, Integer>>
     */
    <U> AdvancedStream<Pair<T, U>> zip(Stream<U> another);
}