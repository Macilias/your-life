package com.macilias.apps.model;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Some Description
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class Sentence {

    public static Logger LOG = Logger.getLogger(Sentence.class);

    private List<Term> terms = new LinkedList<>();

    private boolean consumed = false;

    private String original = "";

    private Intent intent = Intent.UNKNOWN;

    public Sentence(){}

    public Sentence(String original) {
        this.original = original;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed, Intent finalIntent) {
        LOG.debug("setConsumed(): " + consumed + " detected intent: " + finalIntent.name());
        this.consumed = consumed;
        this.intent   = finalIntent;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getOriginal() {
        return original;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    public void addTerms(Term... term) {
        this.terms.addAll(Arrays.asList(term));
    }

    public int indexOf(Term term) {
        return terms.indexOf(term);
    }

    public int indexOf(Set<Term> substitutes) {
        int index = -1;
        for (Term substitute : substitutes) {
            index = terms.indexOf(substitute);
            if (index >= 0) {
                return index;
            }
        }
        return index;
    }
}
