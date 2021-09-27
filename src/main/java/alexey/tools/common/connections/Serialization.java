package alexey.tools.common.connections;

import java.nio.ByteBuffer;

public interface Serialization<R, W> {
    void write(Connection<R, W> connection, ByteBuffer destination, W source);
        R read(Connection<R, W> connection, ByteBuffer source);
}
