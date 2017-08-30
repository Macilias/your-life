package com.macilias.apps.model.anna.api.v1;

import com.macilias.apps.model.api.v1.CustomResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * Some Description
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class AnnaResponse extends CustomResponse {

    public AnnaResponse(String responseText) {
        super(responseText);
    }

    @Override
    public String toString() {
        return "AnnaResponse{" +
                "responseText='" + responseText + '\'' +
                ", responseArguments=" + StringUtils.join(customArguments, ",") +
                '}';
    }

}
