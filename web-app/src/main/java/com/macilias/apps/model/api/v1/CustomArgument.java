package com.macilias.apps.model.api.v1;

/**
 * your-life
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class CustomArgument {

    String name;
    String value;

    public CustomArgument(String name, String value) {
        this.name  = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
