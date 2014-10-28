package com.github.nikolavp.commander;

import java.io.InputStream;
import java.io.OutputStream;

/*
 * #%L
 * com.github.nikolavp:commander-dist
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

/**
 * A standalone shell class.
 */
public class CommanderStandaloneShell extends Shell {
    /**
     * Constructor accepting configuration arguments.
     * @param inputStream input stream
     * @param output output stream
     * @param prompt the prompt to be used
     */
    public CommanderStandaloneShell(InputStream inputStream, OutputStream output, String prompt) {
        super(inputStream, output, prompt);
    }

    /**
     * Default constructor that uses the system input, output and sets the prompt to 'commander'.
     */
    public CommanderStandaloneShell() {
        this(System.in, System.out, "commander");
    }


    /**
     * Main entry point that allows this class to be used as a main class.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new CommanderStandaloneShell().run();
    }
}
