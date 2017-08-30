package com.macilias.apps.model.anna.api.v1;

import com.macilias.apps.model.api.v1.CustomRequest;
import com.macilias.apps.model.api.v1.CustomResponse;
import com.macilias.apps.model.sidekick.api.v1.SidekickResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * Some Description
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class AnnaRequest extends CustomRequest {

    public AnnaRequest(String channel) {
        super(channel);
    }

    public CustomResponse getResponse() {
        return response;
    }

    public void setResponse(SidekickResponse response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "AnnaRequest{" +
                "intent=" + intent +
                ", arguments=" + StringUtils.join(arguments, ", ") +
                ", date=" + date +
                ", channel=" + channel +
                ", response='" + response + '\'' +
                '}';
    }
}
