package example;


import com.google.protobuf.AbstractMessage;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.ziften.server.protocol.message.PostgresType;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProtobufToJSON {
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toJSONObjectModel(AbstractMessage message) {
        final Map<String, Object> object = new TreeMap<>();
        object.put("protobufName", message.getDescriptorForType().getName());
        object.put("message_type", message.getDescriptorForType().getName());
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
            final Descriptors.FieldDescriptor key = entry.getKey();
            final Object value = message.getField(key);
            if (value instanceof AbstractMessage) {
                object.put(key.getName(), toJSONObjectModel((AbstractMessage) value));
            } else if (value instanceof Descriptors.EnumValueDescriptor) {
                final Descriptors.EnumValueDescriptor descriptor = (Descriptors.EnumValueDescriptor) value;
                object.put(key.getName(), descriptor.getNumber());
            } else if (value instanceof List) {
                final List list = new LinkedList();
                for (Object item : (List) value) {
                    list.add(item);
                }
                object.put(key.getName(), list);
            } else {

                DescriptorProtos.FieldOptions options = key.getOptions();
                String postgresType = options.getExtension(PostgresType.postgresType);
                if ("timestamp".equals(postgresType) || "timestamp with time zone".equals(postgresType)) {
                    // Timestamps are Windows 100s of ns, not Unix epoch ms, so adjust.
                    long winTime = Long.parseLong(value.toString());
                    object.put(key.getName(), ((winTime / 10000000L) - 11644473600L) * 1000L);
                }
                else {
                    object.put(key.getName(), value);
                }
            }
        }
        return object;
    }
}