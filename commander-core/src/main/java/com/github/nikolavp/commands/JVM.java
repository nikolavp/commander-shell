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
import com.beust.jcommander.Parameters;
import com.github.nikolavp.MultiCommand;
import com.github.nikolavp.Shell;
import com.github.nikolavp.ShellCommand;

import java.io.PrintStream;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;

/**
 * Multi command for JVM information commands.
 */
@Parameters(commandDescription = "JVM commands for managing the jvm and showing statistics", commandNames = "jvm")
public class JVM extends MultiCommand {

    private final PrintStream output;

    /**
     * Default ctor.
     * @param shell the shell to be used
     */
    public JVM(Shell shell) {
        super(shell);
        this.output = shell.getOutput();
    }

    /**
     * The system command. This will show information about the current OS on which the JVM is running.
     */
    @Parameters(commandDescription = "Show JMX data about the current operating system", commandNames = "system")
    public class System implements ShellCommand {
        @Override
        public void run() {
            OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
            if (operatingSystemMXBean == null) {
                output.println("Couldn't get the system bean!");
                return;
            }
            output.println(operatingSystemMXBean.getArch());
            output.println(operatingSystemMXBean.getName());
            output.println(operatingSystemMXBean.getVersion());
            output.println(operatingSystemMXBean.getAvailableProcessors());
        }
    }

    /**
     * The runtime command. This will show information about the JVM runtime on which we are running.
     */
    @Parameters(commandDescription = "Show JVM runtime information", commandNames = "runtime")
    public class Runtime implements ShellCommand {

        @Override
        public void run() {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            if (runtimeMXBean == null) {
                output.println("Couldn't get the runtime bean!");
                return;
            }
            output.println(runtimeMXBean.getName());
            output.println(runtimeMXBean.getSpecName());
            output.println(runtimeMXBean.getSpecVendor());
            output.println(runtimeMXBean.getManagementSpecVersion());
        }
    }

    /**
     * Show JMX data about Class Loading System.
     */
    @Parameters(commandDescription = "Show JVM classloding", commandNames = "classloading")
    public class Classloading implements ShellCommand {

        @Override
        public void run() {
            ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
            if (classLoadingMXBean == null) {
                output.println("Couldn't get the runtime bean!");
                return;
            }
            output.println(classLoadingMXBean.isVerbose());
            output.println(classLoadingMXBean.getLoadedClassCount());
            output.println(classLoadingMXBean.getTotalLoadedClassCount());
            output.println(classLoadingMXBean.getUnloadedClassCount());
        }
    }


    /**
     * Show JMX data about Compilation.
     */
    @Parameters(commandDescription = "Show JVM compilation", commandNames = "compilation")
    public class Compilation implements ShellCommand {
        @Override
        public void run() {
            CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
            if (compilationMXBean == null) {
                output.println("Couldn't get the runtime bean!");
                return;
            }
            output.println(compilationMXBean.getTotalCompilationTime());
        }
    }

    /**
     * Shows JVM memory heap information.
     */
    @Parameters(commandDescription = "Show JVM memory heap", commandNames = "heap")
    public class Heap implements ShellCommand {

        @Override
        public void run() {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            if (memoryMXBean == null) {
                output.println("Couldn't get the runtime bean!");
                return;
            }
            output.println(memoryMXBean.getHeapMemoryUsage());
        }
    }

    /**
     * Shows JVM memory non heap information.
     */
    @Parameters(commandDescription = "Show JVM memory non heap", commandNames = "nonheap")
    public class NonHeap implements ShellCommand {

        @Override
        public void run() {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            if (memoryMXBean == null) {
                output.println("Couldn't get the runtime bean!");
                return;
            }
            output.println(memoryMXBean.getNonHeapMemoryUsage());
        }
    }

    /**
     * Shows JVM memory pools information.
     */
    @Parameters(commandDescription = "Show JVM memory pools", commandNames = "pools")
    public class Pools implements ShellCommand {

        @Override
        public void run() {
            List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
            for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
                output.println(memoryPoolMXBean.getName());
            }
        }
    }

    /**
     * A command that mimics the top unix utility.
     */
    @Parameters(commandDescription = "Mimics the top functionality", commandNames = "top")
    public class Top implements ShellCommand {

        @Override
        public void run() {
            // TODO Write the while cycle here
            new Clear(getShell()).run();
            new Heap().run();
        }
    }

    /**
     * Runs the garbage collector.
     */
    @Parameters(commandDescription = "Show JVM garbage collection", commandNames = "gc")
    public class GC implements ShellCommand {

        @Override
        public void run() {
            output.println("\nGARBAGE COLLECTION");
            List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
            for (GarbageCollectorMXBean gc : garbageCollectorMXBeans) {
                output.println("name :" + gc.getName() + "\n");
                output.println("collection count: " +  gc.getCollectionCount());
                output.println("collection time: " + gc.getCollectionTime());

                String[] memoryPoolNames = gc.getMemoryPoolNames();
                for (String memoryPoolName : memoryPoolNames) {
                    output.println(memoryPoolName);
                }
                output.println("\n");
            }

        }
    }

    @Override
    public List<Object> buildCommands(JCommander jCommander) {
        return Arrays.<Object>asList(
                new GC(),
                new Top(),
                new Pools(),
                new Compilation(),
                new NonHeap(),
                new Classloading(),
                new Runtime(),
                new System(),
                new Help(jCommander));
    }
}