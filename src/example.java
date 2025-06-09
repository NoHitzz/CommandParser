// 
// CommandParser.java
// CommandParser 
// 
// Noah Hitz 2025
// 

import com.emu.*;

class example {
    static SwitchParam active = new SwitchParam("active", "a", false, "Switches mode to active");
    static StrParam inputFile = new StrParam("file", "f", "", "FILE", "Specify an input file");
    static StrParam inputUrl  = new StrParam("url", "u", "", "URL", "Specify an input url");
    static BoolParam convert  = new BoolParam("convert", "", false, "BOOL", "Enable/Disable number conversions");
    static IntParam group     = new IntParam("group", "", 1, "INT", "Number of group entries");
    static StrParam output    = new StrParam("out", "o", "", "FILE", "Specify an output file");

    public static void main(String[] args) {
        CommandParser commandParser = new CommandParser("java -jar example.jar");
        commandParser.setProgramDisplayName("Example")
                     .setVersion("1.0.0")
                     .setHelpDescription("A small example for the CommandParser library") 
                     .setHelpSynopsis("More information")
                     .setHelpExample("java -jar example.jar -a --convert=true --group=1 -o output.txt");
        commandParser.setPosArgsArity(0)       // Expects no positional arguments
                     .setPosArgsUsage("");     

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
