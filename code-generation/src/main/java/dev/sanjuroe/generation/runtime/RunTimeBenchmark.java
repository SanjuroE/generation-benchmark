/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package dev.sanjuroe.generation.runtime;

import dev.sanjuroe.generation.Data;
import dev.sanjuroe.generation.Employee;
import dev.sanjuroe.generation.Unmarshaller;
import dev.sanjuroe.generation.impl.DataInputParser;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class RunTimeBenchmark {

    byte[] input;

    Unmarshaller basicUnmarshaller;

    Unmarshaller deployUnmarshaller;

    Unmarshaller handleUnmarshaller;

    @Setup
    public void setup() throws Exception {
        this.input = Data.generateEmployee();
        this.basicUnmarshaller = new BasicRunTimeUnmarshaller();
        this.basicUnmarshaller.init();
        this.deployUnmarshaller = new DeployRunTimeUnmarshaller();
        this.deployUnmarshaller.init();
        this.handleUnmarshaller = new HandleRunTimeUnmarshaller();
        this.handleUnmarshaller.init();
    }

    @Benchmark
    public Employee basic() throws Throwable {
        var parser = new DataInputParser(new ByteArrayInputStream(input));

        return basicUnmarshaller.readEmployee(parser);
    }

    @Benchmark
    public Employee deploy() throws Throwable {
        var parser = new DataInputParser(new ByteArrayInputStream(input));

        return deployUnmarshaller.readEmployee(parser);
    }

    @Benchmark
    public Employee handle() throws Throwable {
        var parser = new DataInputParser(new ByteArrayInputStream(input));

        return handleUnmarshaller.readEmployee(parser);
    }
}
