package dev.sanjuroe.generation.runtime;

import dev.sanjuroe.generation.Employee;
import dev.sanjuroe.generation.Parser;
import dev.sanjuroe.generation.Unmarshaller;
import dev.sanjuroe.generation.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Field;

public class BasicRunTimeUnmarshaller implements Unmarshaller<Employee> {

    @Override
    public Employee read(Parser parser) throws IOException, ReflectiveOperationException {
        Class<?> clazz = Employee.class;

        var employee = (Employee) clazz.getDeclaredConstructor().newInstance();

        var fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            var name = field.getName();
            var type = field.getType();
            var setterName = ReflectionUtils.determineSetter(name);
            var setter = clazz.getDeclaredMethod(setterName, type);
            if (boolean.class.equals(type)) {
                setter.invoke(employee, parser.readBoolean());
            } else if (int.class.equals(type)) {
                setter.invoke(employee, parser.readInteger());
            } else if (String.class.equals(type)) {
                setter.invoke(employee, parser.readString());
            } else {
                throw new IllegalArgumentException("Unknown type");
            }
        }

        return employee;
    }
}
