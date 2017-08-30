package com.macilias.apps.controller;

import com.macilias.apps.model.Sentence;

import java.util.Optional;

/**
 * Anna
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public interface Anna {

    Optional<String> consume(Sentence sentence);

}
