package com.ryanmichela.MCStats2.reporting;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Created on 2008/08/20
 * @author Jason Morris
 */
public class JSONEncoder<T> {
    private static final Map<Class<?>, ObjectEncoder> encoderCache =
            Collections.synchronizedMap(new WeakHashMap<Class<?>, ObjectEncoder>());

    private final Encoder encoder;

    private JSONEncoder(final Class<T> type) throws IntrospectionException {
        encoder = getEncoder(type);
    }

    private static Encoder getEncoder(final Class<?> type) throws IntrospectionException {
        if(CharSequence.class.isAssignableFrom(type) || Character.class.equals(type) ||
                Character.TYPE.equals(type) || UUID.class.equals(type)) {

            return StringEncoder.INSTANCE;
        } else if(Number.class.isAssignableFrom(type) ||
                Byte.TYPE.equals(type) || Short.TYPE.equals(type) ||
                Integer.TYPE.equals(type) || Long.TYPE.equals(type) ||
                Boolean.TYPE.equals(type) || Boolean.class.equals(type)) {

            return PlainFormEncoder.INSTANCE;
        } else if(type.isArray()) {
            return new ArrayEncoder(getEncoder(type.getComponentType()));
        } else if(Iterable.class.isAssignableFrom(type)) {
            return IterableEncoder.INSTANCE;
        } else if(Map.class.isAssignableFrom(type)) {
            return MapEncoder.INSTANCE;
        } else if(Date.class.isAssignableFrom(type)) {
            return DateEncoder.INSTANCE;
        } else {
            ObjectEncoder encoder = encoderCache.get(type);

            if(encoder == null) {
                encoder = new ObjectEncoder(type);
                encoderCache.put(type, encoder);
            }

            return encoder;
        }
    }

    public String encode(final T value) {
        final StringWriter out = new StringWriter();

        try {
            encode(value, out);
        } catch(IOException ex) {
            // ignore this... it won't happen with a StringWriter
        }

        return out.toString();
    }

    public void encode(final T value, final Writer out) throws IOException {
        encoder.encode(value, out);
    }

    public static <T> JSONEncoder<T> getJSONEncoder(final Class<T> type) throws IntrospectionException {
        return new JSONEncoder<T>(type);
    }

    private static class Property {
        private final String name;

        private final Method getter;

        private final Encoder encoder;

        public Property(final String name, final Method getter) throws IntrospectionException {
            this.name = name;
            this.getter = getter;
            this.encoder = getEncoder(getter.getReturnType());
        }

        private void encode(final Object object, final Writer out) throws IOException {
            try {
                out.write("\"" + name + "\"");
                out.write(':');

                final Object value = getter.invoke(object);

                if(value == null) {
                    out.write("null");
                } else {
                    encoder.encode(value, out);
                }
            } catch(IllegalAccessException ex) {
                throw new IOException(ex);
            } catch(IllegalArgumentException ex) {
                throw new IOException(ex);
            } catch(InvocationTargetException ex) {
                throw new IOException(ex);
            }
        }

    }

    private static interface Encoder {
        void encode(Object value, Writer out) throws IOException;

    }

    private static class PlainFormEncoder implements Encoder {
        private static final PlainFormEncoder INSTANCE = new PlainFormEncoder();

        public PlainFormEncoder() {
        }

        public void encode(final Object value, final Writer out) throws IOException {
            out.write(value.toString());
        }

    }

    private static class DateEncoder implements Encoder {
        private static final DateEncoder INSTANCE = new DateEncoder();

        public DateEncoder() {
        }

        public void encode(final Object value, final Writer out) throws IOException {
            out.write("new Date(");
            out.write(Long.toString(((Date)value).getTime()));
            out.write(")");
        }

    }

    private static class StringEncoder implements Encoder {
        private static final StringEncoder INSTANCE = new StringEncoder();

        public StringEncoder() {
        }

        public void encode(final Object value, final Writer out) throws IOException {
            out.write('\"');

            final String string = value.toString();

            for(int i = 0; i < string.length(); i++) {
                final char ch = string.charAt(i);

                switch(ch) {
                    case '\n':
                        out.write("\\n");
                        break;
                    case '\r':
                        out.write("\\r");
                        break;
                    case '\"':
                        out.write("\\\"");
                        break;
                    default:
                        out.write(ch);
                        break;
                }
            }

            out.write('\"');
        }

    }

    private static class ArrayEncoder implements Encoder {
        private final Encoder encoder;

        public ArrayEncoder(final Encoder encoder) {
            this.encoder = encoder;
        }

        public void encode(final Object value, final Writer out) throws IOException {
            out.write('[');

            final int length = Array.getLength(value);

            for(int i = 0; i < length; i++) {
                encoder.encode(Array.get(value, i), out);

                if(i + 1 < length) {
                    out.write(',');
                }
            }

            out.write(']');
        }

    }

    private static class IterableEncoder implements Encoder {
        private static final IterableEncoder INSTANCE = new IterableEncoder();

        public IterableEncoder() {
        }

        public void encode(final Object value, final Writer out) throws IOException {
            final Iterator<? extends Object> iterator = ((Iterable<?>)value).iterator();

            Encoder lastEncoder = null;
            Class<?> lastType = null;

            out.write('[');

            while(iterator.hasNext()) {
                try {
                    final Object instance = iterator.next();
                    final Class<?> type = instance.getClass();
                    final Encoder enc = type == lastType
                            ? lastEncoder
                            : getEncoder(type);

                    enc.encode(instance, out);

                    lastType = type;
                    lastEncoder = enc;
                } catch(IntrospectionException ie) {
                    throw new IOException(ie);
                }

                if(iterator.hasNext()) {
                    out.write(',');
                }
            }

            out.write(']');
        }

    }

    private static class MapEncoder implements Encoder {
        private static final MapEncoder INSTANCE = new MapEncoder();

        public MapEncoder() {
        }

        public void encode(final Object value, final Writer out) throws IOException {
            @SuppressWarnings("unchecked")
            final Map<Object, Object> map = (Map<Object, Object>)value;
            final Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
            final Iterator<Map.Entry<Object, Object>> iterator = entrySet.iterator();

            Encoder lastEncoder = null;
            Class<?> lastType = null;

            out.write('{');

            while(iterator.hasNext()) {
                try {
                    final Map.Entry<Object, Object> entry = iterator.next();
                    final Object entryValue = entry.getValue();
                    final Class<?> type = entryValue.getClass();
                    final Encoder enc = type == lastType
                            ? lastEncoder
                            : getEncoder(type);

                    out.write("\"" + entry.getKey().toString() + "\"");
                    out.write(':');
                    enc.encode(entryValue, out);

                    lastType = type;
                    lastEncoder = enc;
                } catch(IntrospectionException ie) {
                    throw new IOException(ie);
                }

                if(iterator.hasNext()) {
                    out.write(',');
                }
            }

            out.write('}');
        }

    }

    private static class ObjectEncoder implements Encoder {
        private final Property[] properties;

        public ObjectEncoder(final Class<?> type) throws IntrospectionException {
            final BeanInfo info = Introspector.getBeanInfo(type);
            final PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
            final List<Property> props = new ArrayList<Property>(descriptors.length);

            for(int i = 0; i < descriptors.length; i++) {
                final PropertyDescriptor d = descriptors[i];
                final Method read = d.getReadMethod();

                if(!read.getDeclaringClass().equals(Object.class)) {
                    props.add(new Property(d.getName(), read));
                }
            }

            properties = props.toArray(new Property[props.size()]);
        }

        public void encode(final Object value, final Writer out) throws IOException {
            out.write('{');

            final int length = properties.length;

            for(int i = 0; i < length; i++) {
                properties[i].encode(value, out);

                if(i + 1 < length) {
                    out.write(',');
                }
            }

            out.write('}');
        }

    }
}