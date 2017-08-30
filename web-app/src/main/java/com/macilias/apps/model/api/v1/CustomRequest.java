package com.macilias.apps.model.api.v1;

import com.macilias.apps.model.sidekick.api.v1.Argument;
import com.macilias.apps.model.sidekick.api.v1.ArgumentName;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * your-life
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public abstract class CustomRequest {

    protected String intent;
    protected Set<Argument> arguments = new HashSet<>();
    protected LocalDateTime date;
    protected String channel;
    protected CustomResponse response;

    public CustomRequest(String channel) {
        this.date = LocalDateTime.now();
        this.channel = channel;
    }

    public CustomResponse getResponse() {
        return response;
    }

    public void setResponse(CustomResponse response) {
        this.response = response;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Set<Argument> getArguments() {
        return arguments;
    }

    public String getChannel() {
        return channel;
    }

    public Optional<Argument> getOptionalArgument(ArgumentName argumentName) {
        for (Argument argument : arguments) {
            if (argument.getArgumentName().equals(argumentName)) {
                return Optional.of(argument);
            }
        }
        return Optional.empty();
    }

    /**
     * This method throws an RuntimeException if the argument is not present
     * @param argumentName
     * @return
     */
    public Argument getArgument(ArgumentName argumentName) {
        for (Argument argument : arguments) {
            if (argument.getArgumentName().equals(argumentName)) {
                return argument;
            }
        }
        throw new RuntimeException("This intent " + intent + " does not have the argument " + argumentName.name());
    }

    public void addArgument(ArgumentName argumentName, String... values) {
        arguments.add(new Argument(argumentName, Arrays.asList(values)));
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

}
