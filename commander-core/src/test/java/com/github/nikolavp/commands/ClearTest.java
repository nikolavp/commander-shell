package com.github.nikolavp.commands;

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
import com.github.nikolavp.AbstractTestShell;
import com.github.nikolavp.Shell;
import com.github.nikolavp.ShellCommand;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author nikola.
 */
public class ClearTest extends AbstractTestShell {
    @Test
    public void shouldProperlyShowHelpMessage() {
        InputStream stream = toInputStream(
                "help\n" +
                "clear\n");

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        new Shell(stream, result, "") {

            @Override
            public List<Object> buildCommands(JCommander jCommander) {
                return Arrays.<Object>asList(
                        new Help(jCommander),
                        new Clear(this)
                );
            }
        }.run();
        approval.verify(result, Paths.get(""));
    }
}
