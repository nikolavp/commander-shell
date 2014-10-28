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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.github.nikolavp.commands.Help;
import jline.console.completer.StringsCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract class for multi commands.
 * <p>Those are just commands that
 * group and keep the infrastructure/delegate to other commands from which they are built.
 * </p>
 * @author nikola.
 */
public abstract class MultiCommand implements ShellCommandGroup {
    private final Shell shell;

    /**
     * Main constructor.
     * @param shell the shell object that we are going to use
     */
    public MultiCommand(Shell shell) {
        this.shell = shell;
    }

    /**
     * Get the internal shell object.
     * @return the shell object
     */
    public Shell getShell() {
        return shell;
    }


    @Override
    public List<Object> buildCommands(JCommander jCommander) {
        ArrayList<Object> result = new ArrayList<Object>();
        result.add(new Help(jCommander));
        return result;
    }

    /**
     * Executes this shell command with the provided arguments. Note that this
     * is using raw arguments instead of offloading some of the logic to jcommander.
     * @param args the arguments from the shell that are targeted for this shell command
     */
    public final void execute(String ...args) {
        if (args.length == 0) {
            shell.getOutput().println("No subcommand specified");
            return;
        }

        JCommander jCommander = new JCommander();
        for (Object command : buildCommands(jCommander)) {
            jCommander.addCommand(command);
        }
        shell.getConsoleReader().addCompleter(new StringsCompleter(jCommander.getCommands().keySet()));

        String commandName = args[0];
        JCommander commandCommander = jCommander.getCommands().get(commandName);
        if (commandCommander == null) {
            shell.getOutput().println("Unknown command '" + commandName + "'");
            return;
        }

        List<Object> objects = commandCommander.getObjects();
        if (objects.isEmpty()) {
            throw new IllegalStateException("Returned object doesn't contain a shell command!");
        }

        if (objects.size() > 1) {
            throw new IllegalStateException("More than one object found in the commander object");
        }

        Object command = objects.get(0);
        if (command instanceof Runnable) {
            // populate any praameters with values in command
            try {
                jCommander.parse(args);
                ((Runnable) command).run();
            } catch (ParameterException ex) {
                shell.getOutput().println("Couldn't execute command '" + commandName + "'. " + ex.getMessage());
            }
        } else if (command instanceof MultiCommand) {
            MultiCommand compositeShellCommand = (MultiCommand) command;
            List<String> argsList = Arrays.asList(args);
            compositeShellCommand.execute(argsList.subList(1, argsList.size()).toArray(new String[argsList.size() - 1]));
        }
    }
}