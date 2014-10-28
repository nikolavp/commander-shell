package com.github.nikolavp;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

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
