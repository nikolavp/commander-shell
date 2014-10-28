package com.github.nikolavp;

/*
 * #%L
 * commander-shell
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

import jline.console.ConsoleReader;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author nikola.
 */
public class SSHD {

    private SshServer sshd;

    public void start() {
        this.sshd = SshServer.setUpDefaultServer();
        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(String username, String password, ServerSession serverSession) {
                boolean b = "admin".equals(username) && "root".equals(password);
                System.out.println(b);
                return b;
            }
        });
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
        sshd.setPort(9000);

        sshd.setShellFactory(new Factory<Command>() {
            @Override
            public Command create() {
                return new Command() {
                    public InputStream inputStream;
                    public OutputStream outputStream;
                    public OutputStream errorStream;

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
                                // work around the fact that the console is not
                                // reseted at least on mac and linux. TODO: Test
                                // this on Windows
                                if (b == ConsoleReader.CR.toCharArray()[0]) {
                                    super.write(ConsoleReader.RESET_LINE);
                                }
                            }
                        };
                    }

                    @Override
                    public void setErrorStream(OutputStream outputStream) {
                        this.errorStream = outputStream;
                    }

                    @Override
                    public void setExitCallback(ExitCallback exitCallback) {

                    }

                    @Override
                    public void start(Environment environment) throws IOException {
                        Shell commander = new Shell(inputStream, outputStream, "commander");
                        new Thread(commander).start();
                    }

                    @Override
                    public void destroy() {
                        // TODO: Close the shell properly!?
                    }
                };
            }
        });

        try {
            sshd.start();
        } catch (IOException e) {
            // TODO: Properly implement this
            throw new RuntimeException("Exception in sshd", e);
        }
    }

    public void stop() {
        if (sshd != null) {
            try {
                sshd.stop();
            } catch (InterruptedException e) {
                // TODO: Properly implement this
                e.printStackTrace();
            }
        }
    }
}
