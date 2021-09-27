package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;
import java.util.*;

public class ObjectCollection<T> extends ObjectStorage<T> {

    protected UnsafeObjectCollectionIterator iterator;



    protected ObjectCollection(Object[] data, int size) {
        super(data, size);
    }

    protected ObjectCollection(Object data) {
        super(data);
    }



    public ObjectCollection() {
        super();
        iterator = new UnsafeObjectCollectionIterator();
    }

    public ObjectCollection(Class<T> type, int capacity) {
        super(type, capacity);
        iterator = new UnsafeObjectCollectionIterator();
    }

    public ObjectCollection(Class<T> type) {
        super(type);
        iterator = new UnsafeObjectCollectionIterator();
    }

    public ObjectCollection(int capacity) {
        super(capacity);
        iterator = new UnsafeObjectCollectionIterator();
    }



    @NotNull
    @Override
    public Iterator<T> iterator() {
        iterator.cursor = 0;
        return iterator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ObjectCollection(");
        if (size > 0) {
            sb.append(data[0]);
            for (int i = 1; i < size; i++)
                sb.append(", ").append(data[i]);
        }
        sb.append(')');
        return sb.toString();
    }



    protected class UnsafeObjectCollectionIterator implements Iterator<T> {

        public int cursor = 0;



        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            return (T) data[cursor++];
        }

        @Override
        public void remove() {
            data[--cursor] = data[--size];
            data[size] = null;
        }
    }
}
