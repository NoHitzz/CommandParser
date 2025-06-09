//
// Parameter.java
// CommandParser
//
// Noah Hitz 2025
//

package com.emu;

import java.util.ArrayList;
import java.util.Scanner;


/**
 * This is the base class used for all Param classes.
 * Parameters represent command line options and get parsed by the CommandParser class.
 * @see CommandParser
 */
public abstract class Parameter {
    /**
     * Constant for variable arity, meaning one or more.
     */
    public static final int VAR_ARITY = Integer.MAX_VALUE;
    private static final String ARRAY_DELIM = ",";

    private int groupId = 0;

    private final String longName;
    private final String shortName;
    private String description;
    private String metaVar = "VALUE";
    private boolean required = false;
    private boolean invoked = false;

    /**
     * Class constructor for Paramater without short option name.
     * @param name Long option name (--name).
     */
    public Parameter(String name) {
        this.longName = name;
        this.shortName = "";
        this.description = "";
    }

    /**
     * Class constructor for Paramater with long and short option name.
     * @param name Long version option name (--name).
     * @param shortName Short option name (-n).
     */
    public Parameter(String name, String shortName) {
        this.longName = name;
        this.shortName = shortName;
        this.description = "";
    }

    /**
     * Class constructor for Paramater with long and short option names.
     * @param name Long version option name (--name).
     * @param shortName Short option name (-n).
     * @param description Description for the help text.
     */
    public Parameter(String name, String shortName, String description) {
        this.longName = name;
        this.shortName = shortName;
        this.description = description;
    }

    /**
     * Set the Parameter to required. Parser will throw exception if parameter is not encountered.
     * If a parameter is in a mutex group, it can't also be required,
     * set the mutex group to required instead.
     * @return this.
     */
    public Parameter setRequired() { required = true; return this; }

    public boolean getRequired() { return required; }

    /**
     * Add a description for help text.
     * @param description String describing the functionality of this parameter.
     * @return this.
     */
    public Parameter setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets metaVar, used in help text to specify parameter argument type (Default: VALUE).
     * @param metaVar String used in help text
     */
    public void setMetaVar(String metaVar) { this.metaVar = metaVar.toUpperCase(); }

    public String getMetaVar() { return metaVar; }

    void setInvoked() { invoked = true; }

    boolean getInvoked() { return invoked; }

    void setGroupId(int id) { groupId = id; }

    int getGroupId() { return groupId; }

    public String getName() {
        return longName;
    }

    public String getShort() {
        return shortName;
    }

    public String getDescription() {
        return description;
    }

    public abstract String getUsage();
    public abstract String getHelp();

    /**
     * Abstract method that needs to be implemented by any class extending Parameter.
     * Used to parse command line arguments.
     * @param argsReader Reader wrapping the command line arguments.
     */
    public abstract void parse(ArgsReader argsReader);

    /**
     * Internal method used to parse paramater arguments.
     * @param argsReader Reader wrapping the command line arguments.
     * @return Token.
     */
    protected static String tokenize(ArgsReader argsReader) {
        String token = "";

        if(argsReader.current().contains("=")) {
            token = argsReader.current().substring(
                    argsReader.current().indexOf("=") + 1,
                    argsReader.current().length());

            if(token.contains(ARRAY_DELIM))
                throw new ParameterParserException(
                        "Invalid number of arguments to option '"
                        + argsReader.getLastOption() + "', expected one");

            token = clean(token);

        } else if(argsReader.hasNext()) {
            if(isOption(argsReader.peek()))
                throw new ParameterParserException(
                        "Expected <value> for option '" + argsReader.getLastOption()
                        + "', got '" + argsReader.peek() + "'");
            token = argsReader.next();
            token = clean(token);
        } else{
            throw new ParameterParserException("Expected <value> for option '"
                    + argsReader.getLastOption() + "', got '"
                    + (argsReader.hasNext() ? argsReader.next() : "") + "'");
        }

        return token;
    }


    /**
     * Internal method used to parse parameter arguments to array parameters.
     * @param argsReader Reader wrapping command line arguments.
     * @param arity Number of expected arguments, can be Parameter.VAR_ARITY if unknown.
     * @return ArrayList of extracted tokens.
     */
    protected ArrayList<String> tokenizeArray(ArgsReader argsReader, Integer arity) {
        ArrayList<String> tokens = new ArrayList<>();

        if(argsReader.current().contains("=")) {
            String line = argsReader.current().substring(
                    argsReader.current().indexOf("=") + 1,
                    argsReader.current().length());

            Scanner scan = new Scanner(line).useDelimiter(ARRAY_DELIM);
            while(scan.hasNext())
                tokens.add(clean(scan.next()));
            scan.close();

        } else if(argsReader.hasNext()) {
            if(isOption(argsReader.peek()))
                throw new ParameterParserException("Expected argument for option '"
                        + argsReader.getLastOption() + "', got '"
                        + argsReader.peek() + "'");

            while(argsReader.hasNext() && !isOption(argsReader.peek()) && tokens.size() + 1 <= arity) {
                String token = argsReader.next();
                token = clean(token);
                tokens.add(token);
            }

        } else{
            throw new ParameterParserException(
                    "Expected array of arguments for option '"
                    + argsReader.getLastOption() + "', got '"
                    + (argsReader.hasNext() ? argsReader.peek() : "") + "'");
        }

        if(arity != VAR_ARITY && tokens.size() != arity)
            throw new ParameterParserException("Expected " + arity
                    + " arguments for option '" + argsReader.getLastOption()
                    + "', got " + tokens.size());
        else if(arity == VAR_ARITY && tokens.size() == 0)
                throw new ParameterParserException("Expected one or more arguments for option '"
                        + argsReader.getLastOption() + "', got " + tokens.size());

        return tokens;
    }

    /**
     * Internal method used to check if a token is an option.
     * @param str Token to be checked.
     * @return Boolean result.
     */
    private static boolean isOption(String str) {
        return (str.startsWith("--") && !str.equals("--"))
            || (str.startsWith("-") && !str.startsWith("--") && !str.equals("-"));

    }

    /**
     * Internal method used to remove unwanted characters from tokens.
     * @param token Token to be processed.
     * @return Cleaned token.
     */
    private static String clean(String token) {
        token = token.trim();

        if(token.endsWith(","))
            token = token.substring(0, token.length()-1);

        if((token.startsWith("\"") && token.endsWith("\""))
                || token.startsWith("'") && token.endsWith("'")) {
            token = token.substring(1, token.length()-1);
        }

        return token;
    }
}
