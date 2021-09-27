package alexey.tools.common.connections;

import alexey.tools.common.collections.ObjectStorage;
import alexey.tools.common.misc.ByteBufferIO;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.IdentityHashMap;

public class SimpleSerialization implements Serialization<Object, Object> {

    private int nextId = 0;
    private final IdentityHashMap<Class<?>, Registration> encoders = new IdentityHashMap<>();
    private final ObjectStorage<Decoder<?>> decoders = new ObjectStorage<>();

    @SuppressWarnings("unchecked")
    public <T> Registration<T> register(Class<? extends T> encoderClass, Encoder<T> encoder) {
        Registration<T> registration = encoders.get(encoderClass);
        if (registration == null) {
            registration = new Registration<>(nextId++, encoder);
            encoders.put(encoderClass, registration);
        } else registration.encoder = encoder;
        return registration;
    }

    public void register(int id, Decoder<?> decoder) {
        decoders.extendSet(id, decoder);
    }

    public void register(Decoder<?> decoder) {
        decoders.add(decoder);
    }

    @SuppressWarnings("unchecked")
    public <T> Registration<T> getRegistration(Class<T> encoderClass) {
        return encoders.get(encoderClass);
    }

    public Decoder getDecoder(int id) {
        return decoders.get(id);
    }

    public int nextRegistrationId() {
        return nextId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(Connection<Object, Object> connection, ByteBuffer destination, @NotNull Object source) {
        Class<?> clazz = source.getClass();
        Registration registration = encoders.get(clazz);
        if (registration == null) throw new IllegalStateException("Unregistered class (" + clazz.getName() + ")!");
        ByteBufferIO.writeInt(destination, registration.id, true);
        registration.encoder.encode(this, connection, destination, source);
    }

    @Override
    public Object read(Connection<Object, Object> connection, @NotNull ByteBuffer source) {
        int id = ByteBufferIO.readInt(source, true);
        if (id < 0) throw new IllegalStateException("Bad id (" + id + ")!");
        if (id >= decoders.size()) throw new IllegalStateException("Unregistered id (" + id + ")");
        Decoder<?> decoder = decoders.unsafeGet(id);
        if (decoder == null) throw new IllegalStateException("Unregistered id (" + id + ")");
        return decoder.decode(this, connection, source);
    }

    public static class Registration <T> {
        final public int id;
        public Encoder<T> encoder;

        public Registration(int id, Encoder<T> encoder) {
            this.id = id;
            this.encoder = encoder;
        }
    }

    public interface Encoder <T> {
        void encode(SimpleSerialization serialization, Connection<Object, Object> connection, ByteBuffer destination, T source);
    }

    public interface Decoder <T> {
        T decode(SimpleSerialization serialization, Connection<Object, Object> connection, ByteBuffer source);
    }
}
