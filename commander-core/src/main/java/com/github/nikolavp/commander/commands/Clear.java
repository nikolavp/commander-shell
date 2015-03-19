package com.github.nikolavp.commander.commands;

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

import com.beust.jcommander.Parameters;
import com.github.nikolavp.commander.Shell;
import com.github.nikolavp.commander.ShellCommand;

import java.io.IOException;

/**
 * A command that clears the shell screen.
 */
@Parameters(commandDescription = "Clears the console screen", commandNames = "clear")
public class Clear implements ShellCommand {

    private static final String COULDN_T_CLEAR_THE_SCREEN = "Couldn't clear the screen!";
    private final Shell shell;

    /**
     * Main constructor.
     * @param shell the shell object
     */
    public Clear(Shell shell) {
        this.shell = shell;
    }

    @Override
    public void run() {
        try {
            boolean b = shell.getConsoleReader().clearScreen();
            if (!b) {
                shell.getOutput().print(COULDN_T_CLEAR_THE_SCREEN);
            }
        } catch (IOException e) {
            shell.getOutput().println(COULDN_T_CLEAR_THE_SCREEN + ": " + e.getMessage());
        }
    }
}
