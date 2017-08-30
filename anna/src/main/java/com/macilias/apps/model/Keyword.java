package com.macilias.apps.model;

import java.util.Optional;

/**
 * This Keywords helps to identify the Intent of the Sentence
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public enum Keyword {

    HI(Intent.GREETING),
    BYE(Intent.ENDING),
    HELP(Intent.HELP),
    A(null),
    IS(null),
    UP(null),
    HOW(null),
    MUCH(null),
    MANY(null);

    Optional<Intent> intent;

    Keyword(Intent intent) {
        if (intent == null) {
            this.intent = Optional.empty();
        } else {
            this.intent = Optional.of(intent);
        }
    }

    public static boolean isKeyword(String s) {
        Keyword[] values = values();
        for (Keyword value : values) {
            if (s.trim().toLowerCase().equalsIgnoreCase(value.name())) {
                return true;
            }
        }
        return false;
    }
}
