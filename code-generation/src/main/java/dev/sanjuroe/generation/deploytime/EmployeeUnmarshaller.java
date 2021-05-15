package dev.sanjuroe.generation.deploytime;

import dev.sanjuroe.generation.Employee;
import dev.sanjuroe.generation.Parser;

public abstract class EmployeeUnmarshaller {

    public abstract Employee read(Parser parser) throws Throwable;
}
