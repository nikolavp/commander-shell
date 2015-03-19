package com.github.nikolavp.commander.web;

/*
 * #%L
 * com.github.nikolavp:commander-web
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

import com.github.nikolavp.commander.ssh.SSHD;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * A web listener that will start expose a ssh interface on a configured port in your application.
 * <p>
 * If you are using servlet 3.0 you shouldn't have to register the listener in your web.xml otherwise
 * it must be explicitly defined :(.
 * </p>
 */
@WebListener
public class ShellWebListener implements ServletContextListener {

    private SSHD sshd = new SSHD();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        sshd.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        sshd.stop();
    }
}
