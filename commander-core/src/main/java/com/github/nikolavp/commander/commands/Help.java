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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;
import com.github.nikolavp.commander.ShellCommand;

/**
 * Shows the usage information on the console.
 * <p>
 * Note that this can be used to show information for {@link com.github.nikolavp.commander.MultiCommand} objects too(not just the whole command stack).
 * </p>
 */
@Parameters(commandDescription = "Shows the help from all commands", commandNames = "help")
public class Help implements ShellCommand {

    private final JCommander parent;

    /**
     * Main constructor.
     * @param parent the jcommander object for which we are going to show the help usage
     */
    public Help(JCommander parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        parent.usage();
    }
}
