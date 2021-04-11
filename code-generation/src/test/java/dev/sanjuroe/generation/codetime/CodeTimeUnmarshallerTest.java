package dev.sanjuroe.generation.codetime;

import dev.sanjuroe.generation.Data;
import dev.sanjuroe.generation.impl.DataInputParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

class CodeTimeUnmarshallerTest {

    @Test
    void basicInput() throws IOException {
        // Given
        var unmarshaller = new CodeTimeUnmarshaller();
        unmarshaller.init();
        byte[] buf = Data.generateEmployee();
        var parser = new DataInputParser(new ByteArrayInputStream(buf));

        // When
        var employee = unmarshaller.readEmployee(parser);

        // Then
        assertThat(employee.getId()).isEqualTo(101);
        assertThat(employee.isActive()).isTrue();
        assertThat(employee.getFirstName()).isEqualTo("Peter");
        assertThat(employee.getLastName()).isEqualTo("Johnson");
        assertThat(employee.getStartYear()).isEqualTo(2007);
        assertThat(employee.getJobTitle()).isEqualTo("Engineer");
    }
}