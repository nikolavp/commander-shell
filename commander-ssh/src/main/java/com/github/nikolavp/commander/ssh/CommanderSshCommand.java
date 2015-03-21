package com.github.nikolavp.commander.ssh;

/*
 * #%L
 * com.github.nikolavp:commander-ssh
 * %%
 * Copyright (C) 2014 Nikolavp
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.nikolavp.commander.Shell;
import jline.console.ConsoleReader;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An adapter class for our commands and those in the apache mina ssh interface.
 *
 */
class CommanderSshCommand implements Command {
    private InputStream inputStream;
    private OutputStream outputStream;
    private Thread thread;

    @Override
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = new FilterOutputStream(outputStream) {
            @Override
            public void write(int b) throws IOException {
                super.write(b);
                if (b == ConsoleReader.CR.toCharArray()[0]) {
                    super.write(ConsoleReader.RESET_LINE);
                }
            }
        };
    }

    @Override
    public void setErrorStream(OutputStream errorStream) {

    }

    @Override
    public void setExitCallback(ExitCallback exitCallback) {

    }

    @Override
    public void start(Environment environment) throws IOException {
        Shell commander = new Shell(inputStream, outputStream, "commander");
        thread = new Thread(commander);
        thread.start();
    }

    @Override
    public void destroy() {
        if (thread != null) {
            thread.stop();
        }
    }
}
