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
import com.github.nikolavp.commands.Clear;
import com.github.nikolavp.commands.Help;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Main object for the whole shell workflow.
 * <p>This is the main starting point for the library.
 * Here is an example usage for the shell object:
 * </p>
 *
 * <pre>
 * {@code
 *  new Shell(System.in, System.out, "test").run();
 * }
 * </pre>
 *
 * if you want to add/customize the commands that are used by the shell, you should most probably extend this
 * object and implement a custom {@link #buildCommands(com.beust.jcommander.JCommander)} method.
 * {@link #run()}
 * {@link #buildCommands(com.beust.jcommander.JCommander)}
 */
public class Shell implements Runnable, ShellCommandGroup {

    private static final Pattern TOKENIZER = Pattern.compile("\\s+");
    private final PrintStream output;
    private final String prompt;
    private ConsoleReader consoleReader;

    /**
     * Gets the internal output stream
     * @return the output stream
     */
    public PrintStream getOutput() {
        return output;
    }

    /**
     * Gets the internal console reader.
     * @return the console reader
     */
    public ConsoleReader getConsoleReader() {
        return consoleReader;
    }

    public Shell(InputStream inputStream, OutputStream output, String prompt) {
        this.output = new PrintStream(output);
        this.prompt = prompt + ">> ";
        try {
            this.consoleReader = new ConsoleReader(inputStream, output);
            consoleReader.setPrompt(prompt);


            patchJCommanderConsole(consoleReader);

        } catch (IOException e) {
            this.output.println("Couldn't open console... aborting: '" + e.getMessage() + "'");
        }
    }

    @Override
    public void run() {
        if (consoleReader == null) {
            return;
        }

        while (true) {
            try {
                String result = consoleReader.readLine(prompt);
                if (result == null) {
                    output.println("Goodbye!");
                    return;
                }
                if (result.isEmpty()) {
                    continue;
                }

                String[] args = TOKENIZER.split(result);

                new MultiCommand(this) {

                    @Override
                    public List<Object> buildCommands(JCommander jCommander) {
                        return Shell.this.buildCommands(jCommander);
                    }
                }.execute(args);
            } catch (IOException e) {
                output.println("Couldn't read from terminal... trying again!");
            }
        }
    }

    private void patchJCommanderConsole(final ConsoleReader consoleReader) {
        try {
            final Field console = JCommander.class.getDeclaredField("m_console");
            console.setAccessible(true);
            console.set(null, new com.beust.jcommander.internal.Console() {
                @Override
                public void print(String msg) {
                    output.print(msg);
                }

                @Override
                public void println(String msg) {
                    output.println(msg);
                }

                @Override
                public char[] readPassword(boolean echoInput) {
                    //TODO: Test this one with password parameter
                    try {
                        return consoleReader.readLine(prompt, '*').toCharArray();
                    } catch (IOException e) {
                        println("Couldn't read password: " + e.getMessage());
                        return null;
                    }
                }
            });
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Couldn't set the console on jcommander. Incompatible versions?!", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Couldn't set the console on jcommander. Incompatible versions?!", e);
        }
    }

    @Override
    public List<Object> buildCommands(JCommander jCommander) {
        ArrayList<Object> commands = new ArrayList<Object>();
        commands.add(new Clear(this));
        commands.add(new Help(jCommander));
        return commands;
    }

    /**
     * Main starting point for the default terminal.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Shell(System.in, System.out, "commander").run();
    }
}
