package alexey.tools.common.identity;

import alexey.tools.common.collections.IntCollection;

public class IdFactory {
    protected final IntCollection used = new IntCollection(4);
    protected int nextId = 0;

    public int obtain() {
        return used.isEmpty() ? nextId++ : used.unsafeRemoveLast();
    }

    public void free(int id) {
        if (id < nextId && !used.contains(id)) used.add(id);
    }

    public void unsafeFree(int id) {
        used.add(id);
    }

    public int size() {
        return nextId;
    }

    public void clear() {
        nextId = 0;
        used.clear();
    }
}
