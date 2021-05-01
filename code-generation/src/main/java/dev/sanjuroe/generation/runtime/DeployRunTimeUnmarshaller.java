package dev.sanjuroe.generation.runtime;

import dev.sanjuroe.generation.Employee;
import dev.sanjuroe.generation.Parser;
import dev.sanjuroe.generation.Unmarshaller;
import dev.sanjuroe.generation.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DeployRunTimeUnmarshaller implements Unmarshaller<Employee> {

    private FieldInfo[] fields;

    private Class<?> clazz = Employee.class;

    private Constructor<?> constructor;

    @Override
    public void init() throws Exception {
        this.constructor = clazz.getDeclaredConstructor();

        List<FieldInfo> list = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            var name = field.getName();
            var type = field.getType();
            var setterName = ReflectionUtils.determineSetter(name);
            var setter = clazz.getDeclaredMethod(setterName, type);
            list.add(new FieldInfo(type, setter));
        }
        fields = list.toArray(FieldInfo[]::new);
    }

    @Override
    public Employee read(Parser parser) throws Exception {
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
        private Method setter;

        public FieldInfo(Class<?> type, Method setter) {
            this.type = type;
            this.setter = setter;
        }

        public Class<?> getType() {
            return type;
        }

        public Method getSetter() {
            return setter;
        }
    }
}
