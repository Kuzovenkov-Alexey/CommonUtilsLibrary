package alexey.tools.common.collections;

import java.util.Iterator;

public class CompactObjectCollection<T> extends CompactObjectStorage<T> {

    protected UnsafeObjectCollectionFastIterator iterator = new UnsafeObjectCollectionFastIterator();



    public CompactObjectCollection(Object[] data) {
        this.data = data;
    }



    @Override
    public Iterator<T> iterator() {
        iterator.cursor = 0;
        return iterator;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CompactObjectCollection(");
        int size = data.length;
        if (size > 0) {
            sb.append(data[0]);
            for (int i = 1; i < size; i++)
                sb.append(", ").append(data[i]);
        }
        sb.append(')');
        return sb.toString();
    }



    protected class UnsafeObjectCollectionFastIterator implements Iterator<T> {

        public int cursor = 0;



        @Override
        public boolean hasNext() {
            return cursor < data.length;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            return (T) data[cursor++];
        }

        @Override
        public void remove() {
            unsafeClearAt(--cursor);
        }
    }
}
