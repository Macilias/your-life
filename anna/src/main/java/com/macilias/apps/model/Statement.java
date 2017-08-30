package com.macilias.apps.model;

import org.apache.log4j.Logger;

/**
 * Statement
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class Statement extends Sentence {

    public static Logger LOG = Logger.getLogger(Statement.class);

    public Statement(Sentence sentence) {
        LOG.debug("This sentence is considered as a statement: " + sentence.getOriginal());
        this.setOriginal(sentence.getOriginal());
        this.setConsumed(sentence.isConsumed(), sentence.getIntent());
        this.setTerms(sentence.getTerms());
    }

}
