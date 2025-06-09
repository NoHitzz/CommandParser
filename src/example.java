// 
// CommandParser.java
// CommandParser 
// 
// Noah Hitz 2025
// 

import com.emu.*;

class example {
    static SwitchParam active = new SwitchParam("active", "a", false, "Switches mode to active");
    static StrParam inputFile = new StrParam("file", "f", "", "Specify an input file");
    static StrParam inputUrl = new StrParam("url", "u", "", "Specify an input url");
    static BoolParam convert = new BoolParam("convert", "", false, "Enable/Disable number conversions");
    static IntParam group = new IntParam("group", "", 1, "Number of group entries");
    static StrParam output = new StrParam("out", "o", "", "Specify an output file");

    public static void main(String[] args) {
        CommandParser commandParser = new CommandParser("java example.jar");
        commandParser.setProgramDisplayName("Example")
                     .setVersion("1.0.0")
                     .setHelpDescription("A small example for the CommandParser library") 
                     .setHelpSynopsis("More information")
                     .setHelpExample("java example -a --convert=true --group=1 -o output.txt");
        commandParser.setPosArgsArity(0)       // Expects no positional arguments
                     .setPosArgsUsage("");     

        inputFile.setMetaVar("FILE");
        inputUrl.setMetaVar("URL");
        output.setMetaVar("FILE");

        commandParser.add(active);
        commandParser.addMutex(true, inputFile, inputUrl); 
        commandParser.add(convert)
                     .add(group)
                     .add(output);

        try {
            commandParser.parse(args);
        } catch(ParameterParserException ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(2);
        }

        if(active.getValue()) {
            // ...
        }
    }
}
