package com.macilias.apps.model.api.v1;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * your-life
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public abstract class CustomResponse {

    protected String responseText;
    protected Set<CustomArgument> customArguments = new HashSet<>();

    public CustomResponse(String responseText) {
        this.responseText = responseText;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public Optional<CustomArgument> getResponseArgument(String name) {
        for (CustomArgument responseArgument : customArguments) {
            if (responseArgument.getName().equals(name)) {
                return Optional.of(responseArgument);
            }
        }
        return Optional.empty();
    }

    public Set<CustomArgument> getResponseArguments() {
        return customArguments;
    }

    public void addResponseArgument(CustomArgument customArgument) {
        customArguments.add(customArgument);
    }

}
