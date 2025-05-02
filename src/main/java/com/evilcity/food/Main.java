package com.evilcity.food;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        parseCLI(args);
    }

    private static final HashMap<String, Object> cli = new HashMap<>();

    /**
     * Parses command line arguments storing them in Main.cli hashmap
     * <br>
     * Arguments are being parsed in following format: <br>
     * Boolean TRUE argument: --arg
     * to pass FALSE in boolean argument you just shouldn't specify it
     * String argument: --arg value
     */
    private static void parseCLI(String[] args) {
        String lastArgumentName = null;
        StringBuilder last = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--")) {
                if (args.length - 1 == i || args[i + 1].startsWith("--")) {
                    cli.put(arg, true);
                }
                lastArgumentName = arg;
            } else {
                if (lastArgumentName == null) {
                    System.out.println(" ".repeat(15) + last + args[i]);
                    System.out.println("               ^ ");
                    System.out.println("Excepted \"--\" after value of command line argument.\nRun this executable with just \"?\" im command line arguments to get help about command line arguments.");
                    System.exit(32);
                }
                if (!cli.containsKey(lastArgumentName)) cli.put(lastArgumentName, arg);
                else cli.put(lastArgumentName, cli.get(lastArgumentName) + " " + arg);
            }
            last.append(args[i]).append(" ");
        }
    }
    public static boolean getBooleanCommandLineArgument(String name) {
        String n = "--" + name;
        if (cli.containsKey(n)) {
            if (cli.get(n) instanceof String) {
                throw new IllegalStateException(name + " command line argument MUST be boolean (meaning it shouldn't have any value)");
            }
            return true;
        }
        return false;
    }
    public static String getStringCommandLineArgument(String name) {
        String n = "--" + name;
        if (cli.containsKey(n)) {
            if (cli.get(n) instanceof String s) {
                return s;
            }
            throw new IllegalStateException(name + " command line MUST be a string");
        }
        throw new IllegalStateException(name + " command line MUST be present. It shall contain string");
    }
}
