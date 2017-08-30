package com.macilias.apps.model.sidekick.api.v1;

import com.macilias.apps.model.api.v1.CustomArgument;

/**
 * Some Description
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class ResponseArgument extends CustomArgument {

    ResponseArgumentName enumeratedName;

    public ResponseArgument(ResponseArgumentName name, String value) {
        super(name.name(), value);
        this.enumeratedName = name;
    }

    @Override
    public String toString() {
        return "ResponseArgument{" +
                "enumeratedName=" + enumeratedName.name() +
                ", value='" + getValue() + '\'' +
                '}';
    }
}
