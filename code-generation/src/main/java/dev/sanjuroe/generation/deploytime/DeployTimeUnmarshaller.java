package dev.sanjuroe.generation.deploytime;

import dev.sanjuroe.generation.Employee;
import dev.sanjuroe.generation.Parser;
import dev.sanjuroe.generation.Unmarshaller;
import dev.sanjuroe.generation.util.ReflectionUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;

public class DeployTimeUnmarshaller implements Unmarshaller<Employee> {

    private EmployeeUnmarshaller innerUnmarshaller;

    @Override
    public void init() throws Exception {
        var cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);

        // Class
        cw.visit(
                Opcodes.V11,
                Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                "dev/sanjuroe/generation/deploytime/EmployeeUnmarshallerImpl",
                null,
                "dev/sanjuroe/generation/deploytime/EmployeeUnmarshaller",
                null
        );

        // Constructor
        var cv = cw.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null
        );
        cv.visitCode();
        cv.visitVarInsn(Opcodes.ALOAD, 0);
        cv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "dev/sanjuroe/generation/deploytime/EmployeeUnmarshaller",
                "<init>",
                "()V",
                false
        );
        cv.visitInsn(Opcodes.RETURN);
        cv.visitMaxs(0, 0);
        cv.visitEnd();

        // Read method
        var mv = cw.visitMethod(
                Opcodes.ACC_PUBLIC,
                "read",
                "(Ldev/sanjuroe/generation/Parser;)Ldev/sanjuroe/generation/Employee;",
                null,
                new String[]{"java/lang/Throwable"}
        );
        mv.visitCode();
        mv.visitTypeInsn(Opcodes.NEW, "dev/sanjuroe/generation/Employee");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "dev/sanjuroe/generation/Employee",
                "<init>",
                "()V",
                false
        );
        mv.visitVarInsn(Opcodes.ASTORE, 2);

        Class<?> clazz = Employee.class;
        var fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            var fieldName = field.getName();
            var fieldType = field.getType();
            appendField(mv, fieldName, fieldType);
        }

        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitMaxs(0, 0);
        mv.visitEnd();

        cw.visitEnd();

        // Instantiate
        var byteCode = cw.toByteArray();
        Class<?> clz = ReflectionUtils.loadClass("dev.sanjuroe.generation.deploytime.EmployeeUnmarshallerImpl", byteCode, getClass().getClassLoader());
        this.innerUnmarshaller = (EmployeeUnmarshaller) clz.getDeclaredConstructor().newInstance();
    }

    @Override
    public Employee read(Parser parser) throws Throwable {
        return innerUnmarshaller.read(parser);
    }

    private void appendField(MethodVisitor mv, String fieldName, Class<?> fieldType) {
        mv.visitVarInsn(Opcodes.ALOAD, 2);
        mv.visitVarInsn(Opcodes.ALOAD, 1);

        String readMethod;
        String readSignature;
        String setterName;
        String setterSignature;
        if (int.class.equals(fieldType)) {
            readMethod = "readInteger";
            readSignature = "()I";
            setterName = ReflectionUtils.determineSetter(fieldName);
            setterSignature = "(I)V";
        } else if (boolean.class.equals(fieldType)) {
            readMethod = "readBoolean";
            readSignature = "()Z";
            setterName = ReflectionUtils.determineSetter(fieldName);
            setterSignature = "(Z)V";
        } else if (String.class.equals(fieldType)) {
            readMethod = "readString";
            readSignature = "()Ljava/lang/String;";
            setterName = ReflectionUtils.determineSetter(fieldName);
            setterSignature = "(Ljava/lang/String;)V";
        } else {
            throw new IllegalArgumentException("Unknown type " + fieldType);
        }

        mv.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                "dev/sanjuroe/generation/Parser",
                readMethod,
                readSignature,
                true
        );
        mv.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "dev/sanjuroe/generation/Employee",
                setterName,
                setterSignature,
                false
        );
    }
}
