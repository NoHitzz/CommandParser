//
// CommandParser.java
// CommandParser
//
// Noah Hitz 2025
//


package com.emu;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * This class is used to parse command line arguments. 
 * <p>
 * Parameters (also known as options) of different types can be added to the CommandParser. <br>
 * Every parameter has a long option name --name. Additionally a parameter can have a short option name -n.<br>
 * A parameter with an empty string for the short option name is treated as not having a short option name.<br>
 * After parsing, the values of the added parameters can be accessed with {@code getValue()}. 
 * <p>
 * The following Parameter implementations are provided:
 * <pre>
 *     SwitchParam         A boolean flag/switch (doesn't take any further arguments)          
 *     BoolParam           A boolean value, accepts a true/false or yes/no argument
 *     StrParam            A string value, accepts a string argument
 *     IntParam            An integer value, accepts an integer argument
 *     DoubleParam         A double value, accepts a floating point argument
 *     StrArrayParam       An array of strings, accepts 'arity' many arguments 
 * </pre>
 * Custom parameters can be added by inheriting from the Parameter base class. <br>
 * By default positional arguments are only allowed at the end. They can be retrieved with {@code getPosArgs()} after parsing. 
 * <p>
 * Arguments to parameters can be specified either with --option=value or --option value. <br>
 * The following syntax is used for arrays of parameter arguments: 
 * {@code --option=value1,value2,value3 or --option value1 value2 value3}.
 * <p>
 * The help command (--help, -h) and the version command (--version) are added by default. <br>
 * The help command shows an auto-generated help text that can be extended with a display name, <br> 
 * a description, a synopsis and an example. The version command shows the specified version string.
 * <p>
 * Methods of the CommandParser class throw IllegalArgumentException for programming errors <br>
 * (for example trying to add the same parameter to the parser multiple times) and <br>
 * ParameterParserException for end-user errors (for example passing the -h option multiple times).
 * <p>
 * Example:
 * <pre>
 * import com.emu.*;
 * 
 * class example {
 *     static SwitchParam active = new SwitchParam("active", "a", false, "Switches mode to active");
 *     static StrParam inputFile = new StrParam("file", "f", "", "Specify an input file");
 *     static StrParam inputUrl = new StrParam("url", "u", "", "Specify an input url");
 *     static BoolParam convert = new BoolParam("convert", "", false, "Enable/Disable number conversions");
 *     static IntParam group = new IntParam("group", "", 1, "Number of group entries");
 *     static StrParam output = new StrParam("out", "o", "Specify an output file");
 * 
 *     public static void main(String[] args) {
 *         CommandParser commandParser = new CommandParser("example");
 *         commandParser.setProgramDisplayName("Example")
 *                      .setVersion("1.0.0")
 *                      .setHelpDescription("A small example for the CommandParser library") 
 *                      .setHelpSynopsis("More information")
 *                      .setHelpExample("java example -a --convert=true --group=1 -o output.txt");
 *         commandParser.setPosArgsArity(0)       // Expects no positional arguments
 *                      .setPosArgsUsage("");     
 * 
 *         inputFile.setMetaVar("FILE");
 *         inputUrl.setMetaVar("URL");
 *         output.setMetaVar("FILE");
 * 
 *         commandParser.add(active);
 *         commandParser.addMutex(true, inputFile, inputUrl); 
 *         commandParser.add(convert)
 *                      .add(group)
 *                      .add(output);
 * 
 *         commandParser.parse(args);
 * 
 *         if(active.getValue()) {
 *             // ...
 *         }
 *     }
 * }
 * </pre> 
 */
public class CommandParser {
    private static final int HELP_OUTER_MARGIN = 2;
    private static final int HELP_INDENT = 2;
    private static final int HELP_USAGE_MAX_WIDTH = 75;
    private static final int HELP_OPTIONS_MIN_SEP_WIDTH = 10;

    private final String programName;
    private String programDisplayName = null;
    private String helpDescription = null;
    private String helpSynopsis = null;
    private String helpExample = null;
    private String version = "1.0.0";
    private boolean isInteractive = System.console() != null;

    private boolean disableDefaultParams = false;
    private boolean enforcePosArgsAtEnd = true;
    private String posArgsMetaVar = "[ARGS...]";
    private int posArgsArity = Parameter.VAR_ARITY;

    private ArrayList<ParameterGroup> paramGroups = new ArrayList<>();
    private LinkedHashMap<String, Parameter> params = new LinkedHashMap<>();
    private HashMap<String, Parameter> shortParams = new HashMap<>();
    private ArrayList<String> posArgs = new ArrayList<>();

    // Default parameters
    private SwitchParam pHelp = new SwitchParam("help", "h", false);
    private SwitchParam pVersion = new SwitchParam("version", "", false);


    /**
     * Class constructor for CommandParser. 
     * @param programName Program name for help text usage.
     */
    public CommandParser(String programName) {
        this.programName = programName;
    }

    /**
     * Sets a display name used for help header.
     * @param name Display name.
     * @return this.
     */
    public CommandParser setProgramDisplayName(String name) {
        this.programDisplayName = name;
        return this;
    }

    /**
     * Specify a version string used for --version. 
     * @param version Version string.
     * @return this.
     */
    public CommandParser setVersion(String version) {
        this.version = version;
        return this;
    }

    /**
     * Specify a description used for help header.
     * @param description Short description of the program.
     * @return this.
     */
    public CommandParser setHelpDescription(String description) {
        this.helpDescription =  description;
        return this;
    }

    /**
     * Specify a longer description of the program for the help text.
     * This gets copy pasted as is.
     * @param synopsis String describing the program.
     * @return this.
     */
    public CommandParser setHelpSynopsis(String synopsis) {
        this.helpSynopsis = synopsis;
        return this;
    }

    /**
     * Provide an example for help text.
     * @param example String with example for the help text.
     * @return this.
     */
    public CommandParser setHelpExample(String example) {
        this.helpExample = indent(example, HELP_OUTER_MARGIN + HELP_INDENT);
        return this;
    }

    /**
     * Disables default behaviour of throwing an exception
     * if a positional argument is encountered between options.
     * @return this.
     */
    public CommandParser disablePosArgsEnforcment() {
        enforcePosArgsAtEnd = false;
        return this;
    }

    /**
     * Specify a string used in help text for usage of positional arguments.
     * @param str String to be displayed in usage.
     * @return this.
     */
    public CommandParser setPosArgsUsage(String str) {
        posArgsMetaVar = str;
        return this;
    }

    /**
     * Set an arity for positional arguments, which gets validated at the end of parsing.
     * Defaults to Paremeter.VAR_ARITY, which means one or more positional arguments get accepted.
     * @param arity Number of positional arguments.
     * @return this.
     */
    public CommandParser setPosArgsArity(int arity) {
        if(arity < 0)
            throw new IllegalArgumentException("Invalid positional arguments arity '"
                    + arity + "', arity can't be negative");
        posArgsArity = arity;
        return this;
    }

    /**
     * Disable the default '-h, --help' and '--version' parameters.
     * @return
     */
    public CommandParser disableDefaultParameters() {
        disableDefaultParams = true;
        return this;
    }

    /**
     * Prints the version String.
     * @see setVersion
     */
    public void showVersion() {
        System.out.println("Version: " + version);
        System.exit(0);
    }

    /**
     * Prints a partly auto generated help text.
     */
    public void showHelp() {
        ArrayList<Parameter> sortedParams = new ArrayList<>(params.values());
        StringBuilder helpMsg = new StringBuilder();
        boolean withAnsi = isInteractive;

        String outerMargin = " ".repeat(HELP_OUTER_MARGIN);
        String indent = " ".repeat(HELP_INDENT);
        String bold = (withAnsi ? Ansi.BOLD : "");
        String nobold = (withAnsi ? Ansi.NOBOLD : "");

        // Header
        helpMsg.append(outerMargin + bold
                + (programDisplayName != null ? programDisplayName : programName) + nobold
                + (helpDescription != null ? " - " + helpDescription : "") + "\n");
        helpMsg.append("\n");

        // Usage
        helpMsg.append(outerMargin + bold + "Usage: " + nobold + "\n");
        String usageProgramName = outerMargin + indent + programName + " ";
        helpMsg.append(usageProgramName);

        StringBuilder usageString = new StringBuilder();

        for(int i = 0; i < sortedParams.size(); i++) {
            Parameter param = sortedParams.get(i);
            if(param.getGroupId() != ParameterGroup.getDefaultGroupId()
                    && paramGroups.get(param.getGroupId()).isMutex()) {
                usageString.append(!paramGroups.get(param.getGroupId()).getRequired() ? "[" : "(");
                int id = param.getGroupId();
                ArrayList<Parameter> groupedParams = new ArrayList<>(paramGroups.get(id).getGroupParams());
                for(int j = 0; j < groupedParams.size(); j++)
                    usageString.append(groupedParams.get(j).getUsage()
                            + (j != groupedParams.size()-1 ? " | " : ""));
                usageString.append(!paramGroups.get(param.getGroupId()).getRequired() ? "]" : ")");
                i += paramGroups.get(param.getGroupId()).getSize()-1;
            } else {
                usageString.append(!param.getRequired() ? "[" : "");
                usageString.append(param.getUsage());
                usageString.append(!param.getRequired() ? "]" : "");
            }

            usageString.append(" ");
        }

        usageString.append(posArgsMetaVar);

        helpMsg.append(wrapUsage(usageString.toString(), HELP_USAGE_MAX_WIDTH-usageProgramName.length(),
                    usageProgramName.length(), false));

        helpMsg.append("\n");

        // Add long description here (maybe synopsis?))
        if(helpSynopsis != null) {
            helpMsg.append(outerMargin + bold + "Synopsis: " + nobold + "\n");
            helpMsg.append(indent(helpSynopsis, HELP_OUTER_MARGIN + HELP_INDENT));
            helpMsg.append("\n\n");
        }

        // Options
        helpMsg.append(outerMargin + bold + "Options: " + nobold + "\n");

        int offset = HELP_OPTIONS_MIN_SEP_WIDTH;
        int maxLength = 0;
        String[] paramHelp = new String[params.size()];
        String[] paramDescr = new String[params.size()];
        for(int i = 0; i < sortedParams.size(); i++) {
            Parameter param = sortedParams.get(i);
            paramHelp[i] = param.getHelp();
            paramDescr[i] = param.getDescription();
            maxLength = Math.max(maxLength, paramHelp[i].length());
        }

        offset += maxLength;

        for(int i = 0; i < sortedParams.size(); i++) {
            helpMsg.append(outerMargin + indent + paramHelp[i]
                    + " ".repeat(offset - paramHelp[i].length())
                    + paramDescr[i] + "\n");
        }
        helpMsg.append("\n");

        // User provided example
        if(helpExample != null) {
            helpMsg.append(outerMargin + bold + "Example: " + nobold + "\n");
            helpMsg.append(helpExample);
        }

        System.out.println(helpMsg);
        System.exit(0);
    }


    /**
     * Register a parameter with the parser.
     * @param param Parameter to be added to parser.
     * @return this.
     */
    public CommandParser add(Parameter param) {
        param.setGroupId(ParameterGroup.getDefaultGroupId());

        Parameter p = params.put(param.getName(), param);
        if(p != null)
            throw new IllegalArgumentException("The parameter '"
                    + param.getName() + "' was already registered");

        if(param.getShort().isEmpty())
            return this;

        Parameter sp = shortParams.put(param.getShort(), param);
        if(sp != null)
            throw new IllegalArgumentException("A parameter with shortName '"
                    + param.getShort() + "' already exists");

        return this;
    }

    /**
     * Register a group of parameters with the parser.
     * A parameter can only be in one group.
     * @param parameters Vararg of Parameter arguments.
     * @return this.
     */
    public CommandParser add(Parameter... parameters) {
        for(Parameter param : parameters)
            add(param);

        paramGroups.add(new ParameterGroup(false, parameters));

        return this;
    }

    /**
     * Register a group of mutually exclusive parameters with the parser.
     * A parameter can only be in one group.
     * @param required Boolean whether or not the mutex group is required
     * (this means exactly one of the grouped parameters must be specified by the user).
     * @param parameters Vararg of Parameter arguments.
     * @return this.
     */
    public CommandParser addMutex(boolean required, Parameter... parameters) {
         for(Parameter param : parameters)
            add(param);

         paramGroups.add(new ParameterGroup(true, required, parameters));

        return this;
    }

    /**
     * Parse command line arguments.
     * @param args Arguments array to be parsed.
     */
    public void parse(String[] args) {
        if(!disableDefaultParams) {
            add(pHelp.setDescription("Print this help text"));
            add(pVersion.setDescription("Print the version number"));
        }

        ArgsReader argsReader = new ArgsReader(args);
        boolean processingPosArgs = false;

        while(argsReader.hasNext()) {
            String token = argsReader.next();
            argsReader.setLastOption(ArgsReader.extractOption(token));

            if(token.startsWith("--") && !token.equals("--")) {
                if(processingPosArgs && enforcePosArgsAtEnd)
                    throw new ParameterParserException("Invalid positional argument '"
                            + posArgs.get(0) + "'");

                int end = token.length();
                if(token.contains("="))
                    end = token.indexOf("=");

                String name = token.substring(2, end);
                Parameter match = params.get(name);
                validateParam(match, argsReader.getLastOption());

                match.parse(argsReader);
                match.setInvoked();

            } else if(token.startsWith("-") && !token.startsWith("--") && !token.equals("-")) {
                if(processingPosArgs && enforcePosArgsAtEnd)
                    throw new ParameterParserException("Invalid positional argument '"
                            + posArgs.get(0) + "'");

                token = token.substring(1, token.length());
                for(int i = 0; i < token.length(); i++) {
                    String shortName = String.valueOf(token.charAt(i));
                    argsReader.setLastOption("-" + shortName);
                    Parameter match = shortParams.get(shortName);
                    validateParam(match, argsReader.getLastOption());

                    match.parse(argsReader);
                    match.setInvoked();

                    if(!(match instanceof SwitchParam))
                        break;
                }

            } else {
                posArgs.add(token);
                processingPosArgs = true;
            }
        }

        if(pHelp.getValue() && !disableDefaultParams)
            showHelp();
        else if(pVersion.getValue() && !disableDefaultParams)
            showVersion();

        // Check that independent required parameters were invoked
        ArrayList<Parameter> sortedParams = new ArrayList<>(params.values());
        for(int i = 0; i < sortedParams.size(); i++) {
            int groupId = sortedParams.get(i).getGroupId();
            if(groupId == ParameterGroup.getDefaultGroupId() || !paramGroups.get(groupId).isMutex()) {
                if(sortedParams.get(i).getRequired() && !sortedParams.get(i).getInvoked())
                    throw new ParameterParserException("Missing required option '"
                            + sortedParams.get(i).getName() + "'");
            }
        }

        // Check that required mutex parameters were invoked
        for(ParameterGroup group : paramGroups) {
            if(group.isMutex() && !group.getInvoked())
                throw new ParameterParserException("Missing required mutex option. Exactly one of '"
                        + group.getNames() + "' must be specified.");
        }

        // Check posargs arity
        if(posArgsArity == Parameter.VAR_ARITY && posArgs.size() == 0)
            throw new ParameterParserException("Expected one or more positional arguments, got '"
                    + posArgs.size() + "'");
        else if(posArgsArity != Parameter.VAR_ARITY && posArgs.size() != posArgsArity)
             throw new ParameterParserException("Expected exactly '" + posArgsArity
                     + "' positional arguments, got '" + posArgs.size() + "'");
    }

    /**
     * Validated parameter and throws exception if the parameter is
     * unknown, has already been invoked or is in an already invoked mutex group.
     * @param param The parameter to be validated.
     * @param lastOption The user string invoking the parameter (for error msg).
     */
    private void validateParam(Parameter param, String lastOption) {
        if(param == null)
            throw new ParameterParserException("Unknown option '"
                    + lastOption + "', see '--help' for more information");
        if(param.getInvoked())
            throw new ParameterParserException("Option '" + lastOption
                    + "' has already been invoked");
        if(param.getGroupId() != ParameterGroup.getDefaultGroupId()
                && paramGroups.get(param.getGroupId()).isMutex()) {
            if(paramGroups.get(param.getGroupId()).getInvoked())
                throw new ParameterParserException(
                        "Another option from the same mutex group as '"
                        + lastOption + "' has already been invoked");
            else
                paramGroups.get(param.getGroupId()).setInvoked();
        }
    }

    /**
     * Wraps usage text at a space and without breaking words if possible.
     * @param text String to wrap.
     * @param width Desired width.
     * @param indent Line indentation.
     * @param indentFirstLine Whether or not to indent first line.
     * @return Wrapped text.
     */
    private static String wrapUsage(String text, int width, int indent, boolean indentFirstLine) {
        StringBuilder result = new StringBuilder();
        String indentStr = " ".repeat(indent);
        int isBlock = 0;

        if(indentFirstLine) result.append(indentStr);

        int lineStart = 0;
        int lastSpace = 0;
        for(int i = 0; i < text.length(); i++) {
            if(text.charAt(i) == ' ' && isBlock == 0)
                lastSpace = i;
            else if(text.charAt(i) == '(' || text.charAt(i) == '[' || text.charAt(i) == '<')
                isBlock++;
            else if(text.charAt(i) == ')' || text.charAt(i) == ']' || text.charAt(i) == '>')
                isBlock--;
            if(i-lineStart >= width) {
                lastSpace = lastSpace >= lineStart + width/2 ? lastSpace : i;
                result.append(text, lineStart, lastSpace).append("\n" + indentStr);
                lineStart = lastSpace+1;
            }
        }
        result.append(text, lineStart, text.length()).append("\n");

        return result.toString();
    }

    /**
     * Indents text by indent number of spaces.
     * @param text Text to indent.
     * @param indent Amount of indentation.
     * @return Indented text.
     */
    private static String indent(String text, int indent) {
        return Arrays.stream(text.split("\n"))
            .map(line -> " ".repeat(indent) + line)
            .collect(Collectors.joining("\n"));
    }

    /**
     * @return List of positional argument strings.
     */
    public ArrayList<String> getPositionalArguments() {
        return posArgs;
    }

    /**
     * Check if the terminal that the program is running in, is interactive.<br>
     * Useful for disabling output formatting and switching input to System.in for file redirects or pipes.
     * @return Boolean if terminal is interactive or not. 
     */
    public boolean isTerminalInteractive() { return isInteractive; }

}

