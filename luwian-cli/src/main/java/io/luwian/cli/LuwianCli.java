package io.luwian.cli;

import io.luwian.cli.commands.NewServiceCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "luwian",
    mixinStandardHelpOptions = true,
    version = "luwian 0.1.0",
    description = "Luwian CLI — Enterprise Java scaffolding",
    subcommands = {
        NewServiceCommand.class
    }
)
public class LuwianCli implements Runnable {

    public static void main(String[] args) {
        int exit = new CommandLine(new LuwianCli()).execute(args);
        System.exit(exit);
    }

    @Override
    public void run() {       
        new CommandLine(this).usage(System.out);
    }
}