package spliterators.exercise;

import org.junit.Test;
import spliterators.example4.Pair;
import spliterators.example5.AdvancedStreamImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.junit.Assert.assertEquals;

public class Exercise3 {

    @Test
    public void test1() {
        Stream<String> left = Stream.of("a", "b", "c", "d");
        Stream<String> right = Stream.of("A", "B", "C", "D");


        List<Pair<String, String>> result = new AdvancedStreamImpl<>(left).zip(right)
                                                                          .collect(Collectors.toList());

        assertEquals(result, Arrays.asList(new Pair<>("a", "A"),
                new Pair<>("b", "B"),
                new Pair<>("c", "C"),
                new Pair<>("d", "D")));
    }

    @Test
    public void test2() {
        Stream<String> left = Stream.of("a", "b", "c", "d");
        Stream<Integer> right = IntStream.range(10, 14).boxed();


        Map<Integer, String> result = new AdvancedStreamImpl<>(left).zip(right)
                                                                    .collect(toMap(Pair::getValue2, Pair::getValue1));

        assertEquals(result, new HashMap<Integer, String>() {
            {
                put(10, "a");
                put(11, "b");
                put(12, "c");
                put(13, "d");
            }
        });
    }
}
