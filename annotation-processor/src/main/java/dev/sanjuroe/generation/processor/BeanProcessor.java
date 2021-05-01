package dev.sanjuroe.generation.processor;

import com.google.auto.service.AutoService;
import dev.sanjuroe.generation.util.ReflectionUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementKindVisitor9;
import javax.lang.model.util.TypeKindVisitor9;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes("dev.sanjuroe.generation.annotation.Bean")
public class BeanProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        var elements = getElementsByAnnotations(annotations, roundEnv);

        List<BeanInfo> beans = getBeans(elements);

        beans.forEach(this::writeUnmarshaller);

        return true;
    }

    private Set<Element> getElementsByAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return annotations.stream()
                .flatMap(a -> roundEnv.getElementsAnnotatedWith(a).stream())
                .collect(Collectors.toSet());
    }

    private List<BeanInfo> getBeans(Set<Element> elements) {
        return elements.stream()
                .map(e -> e.accept(new BeanVisitor(), null))
                .collect(Collectors.toList());
    }

    private void writeUnmarshaller(BeanInfo bean) {
        try {
            var packageName = bean.getPackageName();
            var simpleName = bean.getSimpleName();
            var className = simpleName + "Unmarshaller";

            var sourceFile = processingEnv.getFiler().createSourceFile(packageName + "." + className);

            var w = sourceFile.openWriter();

            w.append("package " + packageName + ";\n");

            w.append("import dev.sanjuroe.generation.Parser;\n");
            w.append("import dev.sanjuroe.generation.Unmarshaller;\n");

            w.append("import java.io.IOException;\n");

            w.append("public class " + className + " implements Unmarshaller<" + simpleName  + "> {\n");

            w.append("    public " + simpleName + " read(Parser parser) throws IOException {\n");
            w.append("        var bean = new " + simpleName + "();\n");
            for (var property : bean.getProperties()) {
                var setMethod = ReflectionUtils.determineSetter(property.getName());
                var parseMethod = ReflectionUtils.determineParseMethod(property.getType());
                w.append("        bean." + setMethod + "(parser." + parseMethod + "());\n");
            }
            w.append("        return bean;\n");
            w.append("    }\n");

            w.append("}\n");

            w.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class BeanInfo {

        private String packageName;

        private String simpleName;

        private List<PropertyInfo> properties;

        public BeanInfo(String packageName, String simpleName, List<PropertyInfo> properties) {
            this.packageName = packageName;
            this.simpleName = simpleName;
            this.properties = properties;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getSimpleName() {
            return simpleName;
        }

        public List<PropertyInfo> getProperties() {
            return properties;
        }
    }

    static class PropertyInfo {

        private String name;

        private Class<?> type;

        public PropertyInfo(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public Class<?> getType() {
            return type;
        }
    }

    static class BeanVisitor extends ElementKindVisitor9<BeanInfo, Void> {

        @Override
        public BeanInfo visitTypeAsClass(TypeElement e, Void unused) {
            var pe = (PackageElement) e.getEnclosingElement();
            var packageName = pe.getQualifiedName().toString();
            var simpleName = e.getSimpleName().toString();
            var properties = e.getEnclosedElements().stream()
                    .flatMap(ee -> ee.accept(new BeanPropertyVisitor(), unused).stream())
                    .collect(Collectors.toList());
            return new BeanInfo(packageName, simpleName, properties);
        }
    }

    static class BeanPropertyVisitor extends ElementKindVisitor9<Optional<PropertyInfo>, Void> {

        @Override
        public Optional<PropertyInfo> visitVariableAsField(VariableElement e, Void unused) {
            var name = e.getSimpleName().toString();
            var cls = e.asType().accept(new TypeVisitor(), null);
            return Optional.of(new PropertyInfo(name, cls));
        }

        @Override
        protected Optional<PropertyInfo> defaultAction(Element e, Void unused) {
            return Optional.empty();
        }
    }

    static class TypeVisitor extends TypeKindVisitor9<Class<?>, Void> {

        @Override
        public Class<?> visitPrimitiveAsBoolean(PrimitiveType t, Void unused) {
            return boolean.class;
        }

        @Override
        public Class<?> visitPrimitiveAsInt(PrimitiveType t, Void unused) {
            return int.class;
        }

        @Override
        public Class<?> visitDeclared(DeclaredType t, Void unused) {
            return t.asElement().accept(new TypeElementVisitor(), null);
        }

        @Override
        protected Class<?> defaultAction(TypeMirror e, Void unused) {
            throw new IllegalArgumentException("Unsupported type: " + e);
        }
    }

    static class TypeElementVisitor extends ElementKindVisitor9<Class<?>, Void> {

        @Override
        public Class<?> visitTypeAsClass(TypeElement e, Void o) {
            try {
                return Class.forName(e.getQualifiedName().toString());
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException(cnfe);
            }
        }
    }
}
