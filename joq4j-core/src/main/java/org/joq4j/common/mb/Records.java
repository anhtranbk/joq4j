package org.joq4j.common.mb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
public final class Records implements Iterable<Record> {

    private final List<Record> records;

    public Records(List<Record> records) {
        this.records = records;
    }

    public Records() {
        this(new LinkedList<>());
    }

    public Records add(Record record) {
        this.records.add(record);
        return this;
    }

    public void clear() {
        this.records.clear();;
    }

    public Record first() {
        return records.get(0);
    }

    public Record last() {
        return records.get(records.size() - 1);
    }

    public int size() {
        return records.size();
    }

    @Override
    public Iterator<Record> iterator() {
        return records.iterator();
    }

    @Override
    public void forEach(Consumer<? super Record> action) {
        records.forEach(action);
    }

    @Override
    public Spliterator<Record> spliterator() {
        return records.spliterator();
    }
}
