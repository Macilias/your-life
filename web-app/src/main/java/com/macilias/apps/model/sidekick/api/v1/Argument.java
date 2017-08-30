package com.macilias.apps.model.sidekick.api.v1;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Some Description
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class Argument {

    private ArgumentName argumentName;
    private Set<String> argumentValues = new HashSet<>();

    public Argument(ArgumentName argumentName, List<String> argumentValues) {
        this.argumentName = argumentName;
        this.argumentValues.addAll(argumentValues);
    }

    public ArgumentName getArgumentName() {
        return argumentName;
    }

    public void setArgumentName(ArgumentName argumentName) {
        this.argumentName = argumentName;
    }

    public Set<String> getArgumentValues() {
        return argumentValues;
    }

    public boolean containsValue(String other) {
        return argumentValues.contains(other);
    }

    public boolean containsAllValues(Set<String> other) {
        return argumentValues.containsAll(other);
    }

    public String getDefaultValue() {
        if (argumentValues.size() == 1) {
            return argumentValues.iterator().next();
        }
        return null;
    }

    public String getValuesAsString() {
        return StringUtils.join(getArgumentValues(), ", ");
    }

    @Override
    public String toString() {
        return "Argument{" +
                "argumentName=" + argumentName +
                ", argumentValues=" + argumentValues +
                '}';
    }
}
