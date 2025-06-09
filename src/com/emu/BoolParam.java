//
// BoolParam.java
// CommandParser
//
// Noah Hitz 2025
//

package com.emu;

public final class BoolParam extends Parameter {
    private boolean value = false;

    public BoolParam(String name, String shortName, boolean defaultValue, String description) {
        super(name, shortName, description);
        value = defaultValue;
    }

    public BoolParam(String name, String shortName, boolean defaultValue) {
        super(name, shortName);
        value = defaultValue;
    }

    public BoolParam(String name, boolean defaultValue) {
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

        if(token.equals("true") || token.equals("yes"))
            value = true;
        else if(token.equals("false") || token.equals("no"))
            value = false;
        else
            throw new ParameterParserException("Invalid argument '" + token + "' for option '" + "--" + this.getName() + "'");
    }

    public boolean getValue() { return value; }

}
