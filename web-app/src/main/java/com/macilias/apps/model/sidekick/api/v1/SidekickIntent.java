package com.macilias.apps.model.sidekick.api.v1;

import com.macilias.apps.model.api.v1.Intent;

import java.util.HashSet;
import java.util.Set;

/**
 * Some Description
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public enum SidekickIntent implements Intent {

    UPDATE, FOLLOWER_COUNT, GET_COMMENTS, GET_NEW, POST;

    public static Set<String> getLowerValues() {
        SidekickIntent[] values = values();
        Set<String> result = new HashSet<>();
        for (int i = 0; i < values.length; i++) {
            result.add(values[i].name().toLowerCase());
        }
        return result;
    }

    public static SidekickIntent fromValue(String value) {

        SidekickIntent[] values = values();
        for (SidekickIntent intent : values) {
            if(value.equalsIgnoreCase(intent.name().toLowerCase())) {
                return intent;
            }
        }

        throw new RuntimeException("the provided value " + value + " is not a valid intent");

    }

}
