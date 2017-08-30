package com.macilias.apps.model.sidekick.api.v1;

import com.macilias.apps.model.api.v1.CustomRequest;
import com.macilias.apps.model.api.v1.CustomResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

/**
 * Some Description
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class SidekickRequest extends CustomRequest {

    public SidekickRequest(String channel) {
        super(channel);
    }

    public SidekickIntent getSidekickIntent() {
        return SidekickIntent.fromValue(intent);
    }

    public void setIntent(SidekickIntent intent) {
        Validate.notNull(intent, "intent can not be NULL");
        if (this.intent != null) {
            throw new RuntimeException("The intent of this request has already been set to " + this.intent);
        }
        this.intent = intent.name();
    }

    public CustomResponse getResponse() {
        return response;
    }

    public void setResponse(SidekickResponse response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "SidekickRequest{" +
                "intent=" + intent +
                ", arguments=" + StringUtils.join(arguments, ", ") +
                ", date=" + date +
                ", channel=" + channel +
                ", response='" + response + '\'' +
                '}';
    }
}
