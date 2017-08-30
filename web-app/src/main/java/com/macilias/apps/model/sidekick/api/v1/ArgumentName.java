package com.macilias.apps.model.sidekick.api.v1;

import java.util.HashSet;
import java.util.Set;

/**
 * Some Description
 *
q * @author Maciej Niemczyk [maciej@gmx.de]
 */
public enum ArgumentName {

    /**
     * POST has 2 Arguments: “WHERE” and “WHAT”
     * The UPDATE, FOLLOWER_COUNT, GET_COMMENTS & GET_NEWS might have a optional Filter WHERE; no means do it on all.
     * And one additional optional Argument which applies to all 5, SINCE YYYY-MM-DD otherwise SINCE is equals to last time checked.
     */
    WHERE, WHAT, SINCE, FAKED_UPDATE;

    public static Set<String> getLowerValues() {
        ArgumentName[] values = values();
        Set<String> result = new HashSet<>();
        for (int i = 0; i < values.length; i++) {
            result.add(values[i].name().toLowerCase());
        }
        return result;
    }

    public static ArgumentName fromValue(String value) {

        ArgumentName[] values = values();
        for (ArgumentName argumentName : values) {
            if(value.equalsIgnoreCase(argumentName.name().toLowerCase())) {
                return argumentName;
            }
        }

        throw new RuntimeException("the provided value " + value + " is not a valid argument");

    }

}
