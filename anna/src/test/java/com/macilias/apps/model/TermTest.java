package com.macilias.apps.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class TermTest {

    @Test
    public void testStripValue () {

        Term term1 = new Term(Term.Type.WORD, "hi,");
        Assert.assertEquals("hi", term1.getValue());

    }

    @Test
    public void testStripAllButQuestionMark () {

        Term term1 = new Term(Term.Type.WORD, "-?");
        Assert.assertEquals("?", term1.getValue());

        Term term2 = new Term(Term.Type.WORD, "-?");
        Assert.assertEquals("?", term2.getValue());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testStripValueToBlank () {

        Term term2 = new Term(Term.Type.WORD, "/)(/&)§/");
        Assert.assertNull(term2.getValue());

    }

}
