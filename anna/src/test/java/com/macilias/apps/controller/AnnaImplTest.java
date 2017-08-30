package com.macilias.apps.controller;

import com.macilias.apps.model.MarvinQuotes;
import com.macilias.apps.model.Sentence;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class AnnaImplTest {

    AnnaImpl marvin;

    @Before
    public void init() {
        marvin = new AnnaImpl();
    }

    @Test
    public void consumeMinimalGrammarSet() throws Exception {
        // Do manual tests, those make more fun
        assertEquals(Optional.empty(), answerTo("glob is I"));
        assertEquals(Optional.empty(), answerTo("prok is V"));
        assertEquals(Optional.empty(), answerTo("pish is X"));
        assertEquals(Optional.empty(), answerTo("pish is X"));
        assertEquals(Optional.empty(), answerTo("tegj is L"));
        assertEquals(Optional.empty(), answerTo("glob glob Silver is 34 Credits"));
        assertEquals(Optional.empty(), answerTo("glob prok Gold is 57800 Credits"));
        assertEquals(Optional.empty(), answerTo("pish pish Iron is 3910 Credits"));
        assertEquals(Optional.of("pish tegj glob glob is 42"), answerTo("how much is pish tegj glob glob?"));
        assertEquals(Optional.of("glob prok Silver is 68 Credits"), answerTo("how many Credits is glob prok Silver ?"));
        assertEquals(Optional.of("glob prok Gold is 57800 Credits"), answerTo("how many Credits is glob prok Gold ?"));
        assertEquals(Optional.of("glob prok Iron is 782 Credits"), answerTo("how many Credits is glob prok Iron?"));
        assertTrue(oneOf(MarvinQuotes.NO_CLUE_STATEMENT, answerTo("how much wood could a woodchuck chuck if a woodchuck could chuck wood ?")));
    }

    @Test
    public void consumeAdditionalGrammarSet () throws Exception {
        // Form 3.      [numeral * word = numeral * word]
        assertEquals(Optional.empty(), answerTo("1 € is 1.18509 US$"));
        assertEquals(Optional.of("Yes"), answerTo("1.18509 US$ is 1 € ?"));
        assertEquals(Optional.of("No"), answerTo("2 US$ is 1 € ?"));
        assertEquals(Optional.of("3 € is 3.55527 Us"), answerTo("how much US$ is 3 € ?"));
        // Form 4.      [numeral        = numeral       ]
        assertEquals(Optional.of("Yes"), answerTo("3 is 3 ?"));
        assertEquals(Optional.of("No"), answerTo("2 is 3 ?"));
        // Form 5.      [          word =           word]
        assertEquals(Optional.empty(), answerTo("Peter Pan is Peter Pan"));
        assertEquals(Optional.of("Yes"), answerTo("Peter Pan is Peter Pan ?"));
        assertEquals(Optional.of("No"), answerTo("Anna is Peter Pan ?"));
        assertEquals(Optional.empty(), answerTo("Anna is a Robot"));
        assertEquals(Optional.of("Yes"), answerTo("Anna is Robot ?"));
        assertEquals(Optional.of("No"), answerTo("Arthur Dent is a Robot ?"));
    }

    private boolean oneOf(List<String> list, Optional<String> optionalAnswer) {
        if (!optionalAnswer.isPresent()) {
            return false;
        }
        String answer = optionalAnswer.get();
        for (String possibleAnswer : list) {
            if (answer.equals(possibleAnswer)) {
                return true;
            }
        }
        return false;
    }

    private Optional<String> answerTo(String sentence) {
        return marvin.consume(new Sentence(sentence));
    }

}