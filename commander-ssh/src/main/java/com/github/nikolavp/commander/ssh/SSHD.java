package com.github.nikolavp.commander.ssh;

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

import jline.internal.Log;
import org.apache.sshd.SshServer;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * A class to expose a ssh interface by a given shell object.
 * <p>
 * This class is convenient if you want to attach to a running daemon with a ssh interface.
 * </p>
 * @author nikola.
 */
public class SSHD {

    private static final String COMMANDER_SSH_PORT = "commander.ssh.port";
    private static final String COMMANDER_SSH_USERNAME = "commander.ssh.username";
    private static final String COMMANDER_SSH_PASSWORD = "commander.ssh.password";
    private SshServer sshd;

    /**
     * Start the ssh daemon.
     */
    public void start() {
        final String portString = System.getProperty(COMMANDER_SSH_PORT);
        if (portString == null) {
            Log.warn("No 'commander.ssh.port' specified, ssh support will not be enabled!");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portString);
        } catch (NumberFormatException ex) {
            Log.error("Bad port '" + portString + "' specified, ssh support will not be enabled!");
            return;
        }

        final String username = System.getProperty(COMMANDER_SSH_USERNAME);
        if (username == null) {
            Log.warn("No 'commander.ssh.username' specified, ssh support will not be enabled!");
            return;
        }

        final String password = System.getProperty(COMMANDER_SSH_PASSWORD);
        if (password == null) {
            Log.warn("No 'commander.ssh.password' specified, ssh support will not be enabled!");
            return;
        }

        this.sshd = SshServer.setUpDefaultServer();
        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(@Nullable String u, @Nullable String p, ServerSession serverSession) {
                if (p == null || u == null) {
                    return false;
                }
                return MessageDigest.isEqual(username.getBytes(StandardCharsets.UTF_8), u.getBytes(StandardCharsets.UTF_8))
                        && MessageDigest.isEqual(password.getBytes(StandardCharsets.UTF_8), p.getBytes(StandardCharsets.UTF_8));
            }
        });
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));


        sshd.setPort(port);

        sshd.setShellFactory(new CommanderSshCommandFactory());

        try {
            sshd.start();
            Log.info("Ssh support started on port " + port);
        } catch (IOException e) {
            Log.error("Couldn't start the ssh daemon", e);
        }
    }


    /**
     * Stop the sshd daemon.
     */
    public void stop() {
        if (sshd != null) {
            try {
                sshd.stop();
            } catch (InterruptedException e) {
                Log.warn("Couldn't stop the sshd daemon gracefully", e);
            }
        }
    }

    /**
     * A command ssh factory that uses our command adapter.
     */
    private static class CommanderSshCommandFactory implements Factory<Command> {
        @Override
        public Command create() {
            return new CommanderSshCommand();
        }
    }
}
