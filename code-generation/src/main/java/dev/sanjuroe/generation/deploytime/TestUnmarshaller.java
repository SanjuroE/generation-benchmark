package dev.sanjuroe.generation.deploytime;

import dev.sanjuroe.generation.Employee;
import dev.sanjuroe.generation.Parser;

public class TestUnmarshaller extends EmployeeUnmarshaller {

    @Override
    public Employee read(Parser parser) throws Throwable {
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
