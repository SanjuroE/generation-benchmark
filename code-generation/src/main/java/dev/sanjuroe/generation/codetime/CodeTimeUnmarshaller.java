package dev.sanjuroe.generation.codetime;

import dev.sanjuroe.generation.Employee;
import dev.sanjuroe.generation.Parser;
import dev.sanjuroe.generation.Unmarshaller;

import java.io.IOException;

public class CodeTimeUnmarshaller implements Unmarshaller {

    @Override
    public Employee readEmployee(Parser parser) throws IOException {
        var employee = new Employee();

        employee.setId(parser.readInteger());
        employee.setActive(parser.readBoolean());
        employee.setFirstName(parser.readString());
        employee.setLastName(parser.readString());
        employee.setStartYear(parser.readInteger());
        employee.setJobTitle(parser.readString());

        return employee;
    }
}
