package alexey.tools.common.collections;

import alexey.tools.common.misc.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class IntCollection implements ImmutableIntCollection {

    protected int size;
    protected int[] data;

    protected IntCollection(int[] data, int size) {
        this.data = data;
        this.size = size;
    }



    public IntCollection() {
        data = new int[8];
        size = 0;
    }

    public IntCollection(int capacity) {
        if (capacity < 1) throw new IllegalArgumentException("Illegal capacity: " + capacity);
        data = new int[capacity];
        size = 0;
    }

    public IntCollection(@NotNull int[] other) {
        size = other.length;
        data = size == 0 ? new int[8] : other.clone();
    }

    public IntCollection(@NotNull int[] other, int off, int len) {
        if (off < 0 || len < 0 || len > other.length - off) throw new IndexOutOfBoundsException();
        size = len;
        if (size == 0) { data = new int[8]; return; }
        data = new int[size];
        System.arraycopy(other, off, data, 0, size);
    }

    public IntCollection(@NotNull ImmutableIntCollection other) {
        size = other.size();
        if (size < 1) {
            data = new int[8];
            if (size < 0) size = 0;
        } else {
            data = new int[size];
            other.toArray(data);
        }
    }



    public int[] data() {
        return data;
    }

    public int removeAt(int index) {
        rangeCheck(index);
        int e = data[index];
        unsafeClearAt(index);
        return e;
    }

    public void incSize() {
        ensureAdd(); size++;
    }

    public void clearAt(int index) {
        if (index >= size || index < 0) outOfBounds(index);
        unsafeClearAt(index);
    }

    public void unsafeClearAt(int index) {
        data[index] = data[--size];
    }

    public int unsafeRemoveAt(int index) {
        int e = data[index];
        unsafeClearAt(index);
        return e;
    }

    public int removeLast() {
        if (size < 1) throw new IllegalStateException("Collection is empty!");
        return unsafeRemoveLast();
    }

    public int unsafeRemoveLast() {
        return data[--size];
    }

    public boolean removeValue(int e) {
        for (int i = 0; size > i; i++)
            if (e == data[i]) { data[i] = data[--size]; return true; }
        return false;
    }

    public boolean removeAll(@NotNull IntCollection list) {
        if (size == 0) return false;
        int s = list.size;
        if (s == 0) return false;
        int[] d = list.data;
        boolean modified = false;
        int j, r;
        for (int i = 0; i < s; i++) {
            r = d[i];
            for (j = 0; j < size; j++) {
                if (r == data[j]) {
                    data[j] = data[--size];
                    modified = true;
                    break;
                }
            }
        }
        return modified;
    }

    public void unsafeInc(int index) {
        data[index]++;
    }

    public void unsafeDec(int index) {
        data[index]--;
    }

    public void add(int e) {
        ensureAdd(); data[size++] = e;
    }

    public void extendSet(int index, int e) {
        ensureSpace(index);
        data[index] = e;
    }

    public void unsafeAdd(int e) {
        data[size++] = e;
    }

    public void unsafeSet(int index, int e) {
        data[index] = e;
    }

    public void set(int index, int e) {
        rangeCheck(index);
        data[index] = e;
    }

    public void setSize(int newSize) {
        if (newSize < 0) throw new IllegalStateException("Illegal size: " + newSize);
        unsafeExtendSetSize(newSize);
    }

    public void unsafeSetSize(int newSize) {
        size = newSize;
    }

    public void ensureSpace(int index) {
        if (index < size) return;
        unsafeExtendSetSize(index + 1);
    }

    public void ensureSize(int newSize) {
        if (newSize > size) unsafeExtendSetSize(newSize);
    }

    public void clear() {
        size = 0;
    }

    public void reset() {
        data = new int[8];
        size = 0;
    }

    public void unsafeAddAll(@NotNull ImmutableIntCollection items) {
        int s = items.size();
        for (int i = 0; i < s; i++) data[size++] = items.unsafeGet(i);
    }

    public void addAll(@NotNull ImmutableIntCollection items) {
        if (items.isEmpty()) return;
        int newSize = items.size() + size;
        grow(newSize);
        for (int i = 0; size < newSize; i++) { data[size] = items.unsafeGet(i); size++; }
    }

    public void unsafeAddAll(@NotNull IntCollection items) {
        int itemsSize = items.size;
        System.arraycopy(items.data, 0, data, size, itemsSize);
        size += itemsSize;
    }

    public void addAll(@NotNull IntCollection items) {
        int itemsSize = items.size;
        if (itemsSize < 1) return;
        int newSize = size + itemsSize;
        grow(newSize);
        System.arraycopy(items.data, 0, data, size, itemsSize);
        size = newSize;
    }

    public void addAll(@NotNull int[] items) {
        int itemsSize = items.length;
        if (itemsSize == 0) return;
        int newSize = size + itemsSize;
        grow(newSize);
        System.arraycopy(items, 0, data, size, itemsSize);
        size = newSize;
    }

    public void setAll(@NotNull int[] items) {
        size = items.length;
        if (size == 0) return;
        if (size > data.length)
            data = items.clone(); else
            System.arraycopy(items, 0, data, 0, size);
    }

    public void unsafeSetAll(int[] items) {
        unsafeSetAll(items, items.length);
    }

    public void unsafeSetAll(int[] items, int newSize) {
        System.arraycopy(items, 0, data, 0, newSize);
        size = newSize;
    }

    public void ensureAdd() {
        if (size == data.length) unsafeSetCapacity(data.length << 1);
    }

    public void ensureAdd(int amount) {
        grow(size + amount);
    }

    public void grow(int newCapacity) {
        if (newCapacity > data.length) unsafeSetCapacity(Math.max(data.length << 1, newCapacity));
    }

    public void setData(@NotNull int[] d) {
        int l = d.length;
        if (l == 0) throw new IllegalStateException("Other array is empty!");
        if (l < size) size = l;
        data = d;
    }

    public void unsafeSetData(int[] d) {
        data = d;
    }

    public void unsafeSetData(int capacity) {
        data = new int[capacity];
    }

    public void fix() {
        if (size < 0) size = 0;
        if (data == null) data = new int[8];
        int l = data.length;
        if (size > l) size = l;
    }

    public void fullClear() {
        fullClearData();
        size = 0;
    }

    public void hardClear() {
        hardClearData();
        size = 0;
    }

    public void hardClearData() {
        for (int i = 0; i < size; i++) data[i] = 0;
    }

    public void fullClearData() {
        Arrays.fill(data, 0);
    }

    public int getOrExtendSet(int index, int defaultValue) {
        if (index < size) return data[index];
        unsafeExtendSetSize(index + 1);
        data[index] = defaultValue;
        return defaultValue;
    }

    public void unsafeExtendSetSize(int newSize) {
        size = newSize;
        grow(size);
    }

    public int getOrDefault(int index, int defaultValue) {
        return index < size ? data[index] : defaultValue;
    }

    public int getOrZero(int index) {
        return getOrDefault(index, 0);
    }

    public void unsafeSetCapacity(int newCapacity) {
        data = ArrayUtils.unsafeCopyOf(data, newCapacity, size);
    }



    protected void outOfBounds(int index) {
        throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    protected void rangeCheck(int index) {
        if (index >= size) outOfBounds(index);
    }



    @Override
    public int indexOf(int value) {
        for (int i = 0; size > i; i++) if (value == data[i]) return i;
        return -1;
    }

    @Override
    public int first() {
        if (size < 1) outOfBounds(0);
        return data[0];
    }

    @Override
    public int last() {
        if (size < 1) outOfBounds(size - 1);
        return data[size - 1];
    }

    @Override
    public boolean contains(int e) {
        for (int v : data) if (e == v) return true;
        return false;
    }

    @Override
    public int get(int index) {
        rangeCheck(index);
        return data[index];
    }

    @Override
    public int unsafeGet(int index) {
        return data[index];
    }

    @Override
    public int capacity() {
        return data.length;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean isNotEmpty() {
        return size > 0;
    }

    @Override
    public int[] toArray() {
        return ArrayUtils.unsafeCopyOf(data, size, size);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int[] toArray(@NotNull int[] a) {
        if (a.length < size) return toArray();
        System.arraycopy(data, 0, a, 0, size);
        if (a.length > size) a[size] = 0;
        return a;
    }

    @Override
    public boolean isBroken() {
        return size < 0 || size > data.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IntList(");
        if (size > 0) {
            sb.append(data[0]);
            for (int i = 1; i < size; i++) sb.append(", ").append(data[i]);
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntCollection list = (IntCollection) o;
        if (size != list.size) return false;
        for (int i = 0; size > i; i++) if (data[i] != list.data[i]) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int e : data) hash = (127 * hash) + e;
        return hash;
    }



    public static ImmutableIntCollection EMPTY_LIST = new ImmutableIntCollection() {};

    @NotNull
    @Contract("null, _ -> new")
    public static IntCollection wrap(int[] data, int size) {
        if (data == null) return new IntCollection(new int[Math.max(1, size)], size);
        int l = data.length;
        if (size < 0) size = 0;
        if (l == 0) return new IntCollection(new int[Math.max(1, size)], size);
        if (l >= size) return new IntCollection(data, size);
        return new IntCollection(ArrayUtils.unsafeCopyOf(data, size, l), size);
    }

    @NotNull
    @Contract("null -> new")
    public static IntCollection wrap(int[] data) {
        if (data == null) return new IntCollection();
        int l = data.length;
        if (l == 0) return new IntCollection();
        return new IntCollection(data, l);
    }

    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static IntCollection unsafeWrap(int[] data, int size) {
        return new IntCollection(data, size);
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static IntCollection unsafeWrap(@NotNull int[] data) {
        return new IntCollection(data, data.length);
    }
}
