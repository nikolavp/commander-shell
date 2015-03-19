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
import com.github.nikolavp.commander.Shell;
import com.github.nikolavp.commander.commands.Clear;
import com.github.nikolavp.commander.commands.Help;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ShellTest extends AbstractTestShell {

    @Test
    public void shouldProperlyOpenPrompt() {
        InputStream stream = toInputStream("");

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

    @Test
    public void shouldProperlyHandleEmptyLines() {
        InputStream stream = toInputStream("\n" +
                "\n" +
                "\n");

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        new Shell(stream, result, "").run();
        approval.verify(result, Paths.get(""));
    }
}
