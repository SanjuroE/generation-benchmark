package dev.sanjuroe.generation.processor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

public class BeanProcessorTest {

    @TempDir
    Path tempDir;

    @Test
    public void test() throws IOException {
        // Given
        var compiler = ToolProvider.getSystemJavaCompiler();

        var diagnostics = new DiagnosticCollector<JavaFileObject>();

        var fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(tempDir.toFile()));
        fileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Arrays.asList(tempDir.toFile()));

        var file = new File("src/test/java/dev/sanjuroe/generation/processor/TestBean.java");
        var compilationUnits = fileManager.getJavaFileObjects(file);

        var task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
        task.setProcessors(Collections.singletonList(new BeanProcessor()));

        // When
        boolean success = task.call();

        // Then
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            System.err.println(diagnostic);
        }
        assertThat(success).isTrue();
        var outputPath = tempDir.resolve("dev/sanjuroe/generation/processor/TestBeanUnmarshaller.java");
        var output = Files.readString(outputPath);
        assertThat(output).isEqualTo(EXPECTED_OUTPUT);
    }

    private static final String EXPECTED_OUTPUT =
            "package dev.sanjuroe.generation.processor;\n" +
            "import dev.sanjuroe.generation.Parser;\n" +
            "import dev.sanjuroe.generation.Unmarshaller;\n" +
            "import java.io.IOException;\n" +
            "public class TestBeanUnmarshaller implements Unmarshaller<TestBean> {\n" +
            "    public TestBean read(Parser parser) throws IOException {\n" +
            "        var bean = new TestBean();\n" +
            "        bean.setStr(parser.readString());\n" +
            "        bean.setI(parser.readInteger());\n" +
            "        bean.setB(parser.readBoolean());\n" +
            "        return bean;\n" +
            "    }\n" +
            "}\n";
}
