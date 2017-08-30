package com.macilias.apps.model;

import org.apache.log4j.Logger;

/**
 * Question
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class Question extends Sentence {

    public static Logger LOG = Logger.getLogger(Question.class);

    public Question(Sentence sentence) {
        LOG.debug("This sentence is considered as a question: " + sentence.getOriginal());
        this.setOriginal(sentence.getOriginal());
        this.setConsumed(sentence.isConsumed(), sentence.getIntent());
        this.setTerms(sentence.getTerms());
    }
}
