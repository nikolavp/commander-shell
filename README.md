commander-shell
===

A Shell framework that uses the already proven and bugfree jcommander and jline. Here is how you can write your new command and use it in a custom shell

```java
    @Parameters(commandDescription = "My new command", commandNames = "mycommand")
    class MyCommand implements ShellCommand {

        @Parameter(description = "argument", names = "-a")
        private String argument;

        @Override
        public void run() {
            System.out.println(argument);
        }
    }

    new Shell(System.in, System.out, "my-cool-shell") {
        @Override
        public List<Object> buildCommands(JCommander jCommander) {
            final List<Object> commands = super.buildCommands(jCommander);
            commands.add(new MyCommand());
            return commands;
        }
    }.run();
```

and here is a live session from the terminal:

    my-cool-shell>> help
    Usage: <main class> [options] [command] [command options]
      Commands:
        clear      Clears the console screen
          Usage: clear [options]

        help      Shows the help from all commands
          Usage: help [options]

        mycommand      My new command
          Usage: mycommand [options]
            Options:
              -a
                 argument


    my-cool-shell>> mycommand -a
    Couldn't execute command 'mycommand'. Expected a value after parameter -a
    my-cool-shell>> mycommand -a test
    test

some things to note:

1) Configuration is done with the annotations from [http://jcommander.org/](jcommander)
2) You get some commands for free - we also ship some ready to use commands that can be used in production. Look in the commander-commands module
3) Auto completion for commands work in the terminal
4) Validation of commands works based on the annotations from jcommander


Development
===

You want to contribute? Go on, the project is really, really young and needs help. Here are some guidelines that should be followed:


Verification
============
Run

    mvn clean verify

before committing your code. 


If you want to use a logger in your code please use jline.internal.Log although it might change in the future, we are sticking to it.
