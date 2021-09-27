package alexey.tools.common.identity;

public class TypeProperties<T> {
    final public Class<T> type;
    final public int id;

    public TypeProperties(Class<T> type, int id) {
        this.type = type;
        this.id = id;
    }
}
