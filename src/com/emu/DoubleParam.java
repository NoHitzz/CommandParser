//
// DoubleParam.java
// CommandParser
//
// Noah Hitz 2025
//

package com.emu;

public final class DoubleParam extends Parameter {
    private double value;

    public DoubleParam(String name, String shortName, double defaultValue, String description) {
        super(name, shortName, description);
        value = defaultValue;
    }

    public DoubleParam(String name, String shortName, double defaultValue) {
        super(name, shortName);
        value = defaultValue;
    }

    public DoubleParam(String name, double defaultValue) {
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
            value = Double.valueOf(token);
        } catch(NumberFormatException ex) {
            throw new ParameterParserException("Invalid argument '" + token + "' for option '" + "--" + this.getName() + "'");
        }

    }

    public double getValue() { return value; }



}
