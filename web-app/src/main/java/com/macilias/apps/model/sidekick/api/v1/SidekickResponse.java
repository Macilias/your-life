package com.macilias.apps.model.sidekick.api.v1;

import com.macilias.apps.model.api.v1.CustomResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * Some Description
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class SidekickResponse extends CustomResponse {

    public SidekickResponse(String responseText) {
        super(responseText);
    }

    @Override
    public String toString() {
        return "SidekickResponse{" +
                "responseText='" + responseText + '\'' +
                ", responseArguments=" + StringUtils.join(customArguments, ",") +
                '}';
    }

}
