package spliterators.example4;

import org.jetbrains.annotations.NotNull;

public class IndexedValue<T> extends Pair<Long, T> implements Comparable<IndexedValue<T>> {

    public IndexedValue(Long index, T value) {
        super(index, value);
    }

    public Long getIndex() {
        return getValue1();
    }

    @Override
    public String toString() {
        return "[" + getValue1() + "] = " + getValue2();
    }

    @Override
    public int compareTo(@NotNull IndexedValue<T> other) {
        return Long.compare(getIndex(), other.getIndex());
    }
}
