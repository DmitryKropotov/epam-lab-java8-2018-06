package spliterators.exercise;

import org.junit.Test;
import spliterators.example5.AdvancedStreamImpl;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Exercise2 {

    @Test
    public void test1() {
        Stream<String> original = Stream.of("Hello happy world!")
                                        .flatMap(Pattern.compile("\\s+")::splitAsStream)
                                        .map(String::toLowerCase);

        List<String> result = new AdvancedStreamImpl<>(original).takeWhile(word -> word.startsWith("h"))
                                                                .collect(Collectors.toList());

        assertEquals(result, Arrays.asList("hello", "happy"));
    }

    @Test
    public void test2() {
        Stream<String> original = IntStream.rangeClosed(0, 20)
                                           .mapToObj(String::valueOf);

        String[] result = new AdvancedStreamImpl<>(original).takeWhile(word -> word.length() == 1)
                                                            .toArray(String[]::new);

        assertArrayEquals(result, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"});
    }

}
