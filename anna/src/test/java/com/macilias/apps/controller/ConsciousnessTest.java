package com.macilias.apps.controller;

import com.macilias.apps.model.Term;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class ConsciousnessTest {

    AnnaImpl.Consciousness c;

    @Before
    public void init() {
        c = new AnnaImpl.Consciousness();
    }

    @Test
    public void learn() throws Exception {
        int size = c.equalityKnowledge.size();
        c.learn(c.word("test"), c.word("test"), c.word("other"));
        // two are identical and two different, we expect 2 to be added
        assertTrue(c.equalityKnowledge.size() == size + 2);
    }

    @Test
    public void learnOne() throws Exception {
        int size = c.equalityKnowledge.size();
        c.learnOne(c.word("test"));
        assertTrue(c.equalityKnowledge.size() == size + 1);
    }

    @Test
    public void invert() throws Exception {
        Term inverted = c.invert(new Term(Term.Type.CONVERSION, "2"));
        assertEquals("0.5", inverted.getValue());
    }

    @Test
    public void learnConversions() throws Exception {
        int size = c.inequalityKnowledge.size();
        Term aTerm = new Term(Term.Type.WORD, "a");
        Term bTerm = new Term(Term.Type.WORD, "b");
        Term cTerm = new Term(Term.Type.WORD, "c");
        Term dTerm = new Term(Term.Type.WORD, "d");
        MutableTriple<Term, Term, Term> triple1 = new MutableTriple<>(aTerm, new Term(Term.Type.CONVERSION, "3"), bTerm);
        MutableTriple<Term, Term, Term> triple2 = new MutableTriple<>(cTerm, new Term(Term.Type.CONVERSION, "2"), dTerm);
        MutableTriple<Term, Term, Term> triple3 = new MutableTriple<>(cTerm, new Term(Term.Type.CONVERSION, "6"), dTerm);
        c.learnConversions(triple1, triple2, triple3);
        // we expect both unique triples to be added with inversion (twice) 2 * 2 = 4
        assertTrue(c.inequalityKnowledge.size() == size + 4);
        // beside this, the conversions between c and d should be 6 resp. 3 now
        Optional<Term> conversion = c.getConversion(cTerm, dTerm, false);
        assertTrue(conversion.isPresent());
        assertEquals("6", conversion.get().getValue());
    }

    @Test
    public void learnEquivalentSubstitutes() throws Exception {
        Term aTerm = new Term(Term.Type.WORD, "ecq1");
        Term bTerm = new Term(Term.Type.WORD, "ecq2");
        c.learnEquivalentSubstitutes(aTerm, bTerm);
        // we expect two additional
        assertTrue(c.getSubstitutes(aTerm).get().contains(bTerm));
        assertTrue(c.getSubstitutes(bTerm).get().contains(aTerm));
    }

    @Test
    public void learnImplicitSubstitutes() throws Exception {
        Term aTerm = new Term(Term.Type.WORD, "impl1");
        Term bTerm = new Term(Term.Type.WORD, "impl2");
        c.learnImplicitSubstitutes(aTerm, bTerm);
        // we expect two additional
        assertTrue(c.getSubstitutes(aTerm).isPresent() && c.getSubstitutes(aTerm).get().contains(bTerm));
        assertFalse(c.getSubstitutes(bTerm).isPresent() && c.getSubstitutes(bTerm).get().contains(aTerm));
    }

    @Test
    public void canBeSubstitutedBy() throws Exception {
        Term aTerm = new Term(Term.Type.WORD, "ecq3");
        Term bTerm = new Term(Term.Type.WORD, "ecq4");
        c.learnImplicitSubstitutes(aTerm, bTerm);
        assertTrue(c.canBeSubstitutedBy(aTerm, bTerm));
    }

    @Test
    public void substituteToNumeric() throws Exception {
        Term aTerm = new Term(Term.Type.WORD, "five");
        Term bTerm = new Term(Term.Type.ROMAN_NUMBER, "V");
        Term cTerm = new Term(Term.Type.ARABIC_NUMBER, "5");
        c.learnEquivalentSubstitutes(aTerm, bTerm, cTerm);
        Optional<Set<Term>> substitutes = c.getNumericSubstitutes(aTerm);
        assertTrue(substitutes.isPresent());
        assertTrue(substitutes.get().size() == 2);

        List<Term> terms = c.substituteToNumeric(Arrays.asList(aTerm));
        assertTrue(terms.size() == 1);
        assertTrue(terms.get(0).equals(cTerm));
    }

    @Test
    public void consolidate() throws Exception {
        Term word1 = new Term(Term.Type.WORD, "Peter");
        Term word2 = new Term(Term.Type.WORD, "Pan");
        Term roma1 = new Term(Term.Type.ROMAN_NUMBER, "I");
        Term roma2 = new Term(Term.Type.ROMAN_NUMBER, "V");
        Term arab1 = new Term(Term.Type.ARABIC_NUMBER, "1");
        Term arab2 = new Term(Term.Type.ARABIC_NUMBER, "2");
        Term keyw1 = new Term(Term.Type.WORD, "how");
        Term keyw2 = new Term(Term.Type.WORD, "much");
        Term keyw3 = new Term(Term.Type.WORD, "is");

        List<Term> terms1 = Arrays.asList(word1, word2, roma1, roma2);
        List<Term> result1 = c.consolidate(terms1);
        assertEquals(2, result1.size());

        List<Term> terms2 = Arrays.asList(word1, roma1, word2, roma2);
        List<Term> result2 = c.consolidate(terms2);
        assertEquals(4, result2.size());

        List<Term> terms3 = Arrays.asList(arab1, roma1, roma2, word1, word2, arab1);
        List<Term> result3 = c.consolidate(terms3);
        assertEquals(4, result3.size());

        List<Term> terms4 = Arrays.asList(keyw1, keyw2, keyw3, roma1, roma2);
        List<Term> result4 = c.consolidate(terms4);
        assertEquals(1, result4.size());

        // Two arabic numbers are probably a result of the split on the decimal sign
        // FIXME provide a better solution in the future
        List<Term> terms5 = Arrays.asList(arab1, arab2);
        List<Term> result5 = c.consolidate(terms5);
        assertEquals(1, result5.size());
    }

}