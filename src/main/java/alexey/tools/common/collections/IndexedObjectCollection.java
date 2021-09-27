package alexey.tools.common.collections;

import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Array;

public class IndexedObjectCollection<T extends IndexedObject> extends ObjectCollection<T> {

    public IndexedObjectCollection() {
        data = (Object[]) Array.newInstance(Object.class, 16);
        size = 0;
        iterator = new UnsafeIndexedObjectCollectionIterator();
    }

    public IndexedObjectCollection(Class<T> type, int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Illegal capacity: " + capacity);
        data = (Object[]) Array.newInstance(type, capacity);
        size = 0;
        iterator = new UnsafeIndexedObjectCollectionIterator();
    }

    public IndexedObjectCollection(Class<T> type) {
        data = (Object[]) Array.newInstance(type, 16);
        size = 0;
        iterator = new UnsafeIndexedObjectCollectionIterator();
    }

    public IndexedObjectCollection(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Illegal capacity: " + capacity);
        data = (Object[]) Array.newInstance(Object.class, capacity);
        size = 0;
        iterator = new UnsafeIndexedObjectCollectionIterator();
    }



    @Override
    public boolean add(@NotNull T e) {
        ensureAdd();
        e.setIndex(size);
        data[size++] = e;
        return true;
    }

    @Override
    public void unsafeAdd(@NotNull T e) {
        e.setIndex(size);
        data[size++] = e;
    }

    public boolean removeReference(@NotNull T e) {
        int removeIndex = e.getIndex();
        if (removeIndex == -1) return false;
        IndexedObject last = (IndexedObject) data[--size];
        last.setIndex(removeIndex);
        data[removeIndex] = last;
        data[size] = null;
        e.setIndex(-1);
        return true;
    }

    public void unsafeRemoveReference(@NotNull T e) {
        IndexedObject last = (IndexedObject) data[--size];
        int removeIndex = e.getIndex();
        last.setIndex(removeIndex);
        data[removeIndex] = last;
        data[size] = null;
    }



    protected class UnsafeIndexedObjectCollectionIterator extends UnsafeObjectCollectionIterator {
        @Override
        public void remove() {
            IndexedObject last = (IndexedObject) data[--size];
            last.setIndex(--cursor);
            data[cursor] = last;
            data[size] = null;
        }
    }

}
