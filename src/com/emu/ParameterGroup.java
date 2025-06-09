//
// ParameterGroup.java
// CommandParser
//
// Noah Hitz 2025
//

package com.emu;

import java.util.LinkedHashSet;
import java.util.Arrays;

/**
 * Internal class used to manage grouped parameters.
 * @see CommandParser
 */
class ParameterGroup {
    private static final int DEFAULT_GROUP_ID = -1;
    private static int nextGroupId = 0;

    private int groupId;
    private String groupedNames;
    private boolean required = false;
    private boolean isMutex;

    private LinkedHashSet<Parameter> groupParams;

    private boolean invoked;

    ParameterGroup(boolean isMutex, Parameter... parameters) {
        this.groupId = nextGroupId++;
        this.isMutex = isMutex;
        this.invoked = false;
        StringBuilder names = new StringBuilder();
        for(Parameter p : parameters) {
            if(isMutex && p.getRequired())
                throw new IllegalArgumentException("Parameter '" + p.getName()
                        + "' is required and part of mutex group. Set the group to required instead.");
            p.setGroupId(this.groupId);
            names.append((p.getShort().isEmpty() ? "--" + p.getName() : "-" + p.getShort()) +  ", ");
        }
        names.delete(names.length()-2, names.length());
        groupedNames = names.toString();

        groupParams = new LinkedHashSet<>(Arrays.asList(parameters));
    }

    ParameterGroup(boolean isMutex, boolean isRequired, Parameter... parameters) {
        this(isMutex, parameters);
        this.required = true;
    }

    boolean getRequired() { return required; }

    int getGroupId() { return groupId; }

    boolean isMutex() { return isMutex; }

    void setInvoked() { invoked = true; }

    boolean getInvoked() { return invoked; }

    int getSize() { return groupParams.size(); }

    LinkedHashSet<Parameter> getGroupParams() { return groupParams; }

    String getNames() { return groupedNames; }

    static int getDefaultGroupId() { return DEFAULT_GROUP_ID; }
}
