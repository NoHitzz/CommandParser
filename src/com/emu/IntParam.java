//
// IntParam.java
// CommandParser
//
// Noah Hitz 2025
//

package com.emu;

public final class IntParam extends Parameter {
    private int value;

    public IntParam(String name, String shortName, int defaultValue, String description) {
        super(name, shortName, description);
        value = defaultValue;
    }

    public IntParam(String name, String shortName, int defaultValue) {
        super(name, shortName);
        value = defaultValue;
    }

    public IntParam(String name, int defaultValue) {
        super(name);
        value = defaultValue;
    }

    @Override
    public String getUsage() {
        StringBuilder usage = new StringBuilder();

        if(!this.getShort().isEmpty())
            usage.append("-" + this.getShort());
        else
            usage.append("--" + this.getName());

        usage.append(" ");
        usage.append(getMetaVar());

        return usage.toString();
    }

    @Override
    public String getHelp() {
        StringBuilder help = new StringBuilder();

        if(!this.getShort().isEmpty())
            help.append("-" + this.getShort() + ", ");
        help.append("--" + this.getName());

        help.append(" ");
        help.append(getMetaVar());

        return help.toString();
    }

    @Override
    public void parse(ArgsReader argsReader) {
        String token = tokenize(argsReader);

        try {
            value = Integer.valueOf(token);
        } catch(NumberFormatException ex) {
            throw new ParameterParserException("Invalid argument '" + token + "' for option '" + "--" + this.getName() + "'");
        }

    }


    public int getValue() { return value; }

}
