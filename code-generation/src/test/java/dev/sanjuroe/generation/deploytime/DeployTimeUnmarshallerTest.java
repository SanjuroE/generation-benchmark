package dev.sanjuroe.generation.deploytime;

import dev.sanjuroe.generation.Data;
import dev.sanjuroe.generation.impl.DataInputParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.*;

class DeployTimeUnmarshallerTest {

    @Test
    public void test() throws Throwable {
        // Given
        var unmarshaller = new DeployTimeUnmarshaller();
        unmarshaller.init();

        byte[] buf = Data.generateEmployee();
        var parser = new DataInputParser(new ByteArrayInputStream(buf));

        // When
        var employee = unmarshaller.read(parser);

        // Then
        assertThat(employee.getId()).isEqualTo(101);
        assertThat(employee.isActive()).isTrue();
        assertThat(employee.getFirstName()).isEqualTo("Peter");
        assertThat(employee.getLastName()).isEqualTo("Johnson");
        assertThat(employee.getStartYear()).isEqualTo(2007);
        assertThat(employee.getJobTitle()).isEqualTo("Engineer");
    }
}
