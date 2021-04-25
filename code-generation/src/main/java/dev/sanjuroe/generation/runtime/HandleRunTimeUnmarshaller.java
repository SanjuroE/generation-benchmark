package dev.sanjuroe.generation.runtime;

import dev.sanjuroe.generation.Employee;
import dev.sanjuroe.generation.Parser;
import dev.sanjuroe.generation.Unmarshaller;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HandleRunTimeUnmarshaller implements Unmarshaller {

    private FieldInfo[] fields;

    private Class<?> clazz = Employee.class;

    private Constructor<?> constructor;

    private MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Override
    public void init() throws Exception {
        constructor = clazz.getDeclaredConstructor();

        List<FieldInfo> list = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            var name = field.getName();
            var type = field.getType();
            var setterName = ReflectionUtils.determineSetter(name);
            var setter = clazz.getDeclaredMethod(setterName, type);
            var mh = lookup.unreflect(setter);
            list.add(new FieldInfo(type, mh));
        }
        fields = list.toArray(FieldInfo[]::new);
    }

    @Override
    public Employee readEmployee(Parser parser) throws Throwable {
        var employee = (Employee) constructor.newInstance();

        for (FieldInfo field : fields) {
            if (boolean.class.equals(field.getType())) {
                field.getSetter().invoke(employee, parser.readBoolean());
            } else if (int.class.equals(field.getType())) {
                field.getSetter().invoke(employee, parser.readInteger());
            } else if (String.class.equals(field.getType())) {
                field.getSetter().invoke(employee, parser.readString());
            } else {
                throw new IllegalArgumentException("Unknown type");
            }
        }

        return employee;
    }

    static class FieldInfo {
        private Class<?> type;
        private MethodHandle setter;

        public FieldInfo(Class<?> type, MethodHandle setter) {
            this.type = type;
            this.setter = setter;
        }

        public Class<?> getType() {
            return type;
        }

        public MethodHandle getSetter() {
            return setter;
        }
    }
}
