package com.macilias.apps.controller.service;

import java.io.IOException;
import java.util.Optional;

/**
 * Some Description
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public interface Service {

    int directContactCount(Optional<String> where, Optional<String> since) throws IOException;

}
