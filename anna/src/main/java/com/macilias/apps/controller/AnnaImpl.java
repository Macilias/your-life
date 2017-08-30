package com.macilias.apps.controller;

import com.macilias.apps.model.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;

import java.util.*;

import static com.macilias.apps.model.Keyword.*;

/**
 * Anna implementation along with her private Consciousness
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class AnnaImpl implements Anna {

    public static Logger LOG = Logger.getLogger(AnnaImpl.class);

    private final String romanPattern = "^M{0,4}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$";

    private Consciousness consciousness = new Consciousness();

    @Override
    public Optional<String> consume(Sentence sentence) {
        try {
            LOG.debug("consume(): " + sentence.getOriginal());
            String[] split = sentence.getOriginal().split("[\\p{Punct}\\s]+");
            LinkedList<Term> recognized = new LinkedList<>();
            if (sentence.getOriginal().contains("?")) {
                Term term = new Term(Term.Type.WORD, "?");
                if (consciousness.learnOne(term)) {
                    recognized.add(term);
                }
            }
            for (String value : split) {
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                if (StringUtils.isNumeric(value)) {
                    manageValue(sentence, recognized, value, Term.Type.ARABIC_NUMBER);
                } else if (value.matches(romanPattern)) {
                    manageValue(sentence, recognized, value, Term.Type.ROMAN_NUMBER);
                } else {
                    manageValue(sentence, recognized, value, Term.Type.WORD);
                }
            }
            if (recognized.isEmpty()) {
                return Optional.of(pickOneOf(MarvinQuotes.NO_CLUE));
            } else if (recognized.getFirst().getValue().equals("?")) {
                Question question = new Question(sentence);
                return handleQuestion(question, recognized);
            } else {
                Statement statement = new Statement(sentence);
                return handleStatement(statement, recognized);
            }
        } catch (Exception e) {
            return Optional.of(pickOneOf(MarvinQuotes.ERROR));
        }
    }

    private Optional<String> handleStatement(Statement statement, LinkedList<Term> recognized) {
        for (Term term : recognized) {
            if (consciousness.canBeSubstitutedBy(consciousness.word(HI.name()), term)) {
                statement.setConsumed(true, Intent.GREETING);
                return handelGreetings(recognized);
            } else if (consciousness.canBeSubstitutedBy(consciousness.word(BYE.name()), term)) {
                statement.setConsumed(true, Intent.ENDING);
                return Optional.of(pickOneOf(MarvinQuotes.END));
            } else if (consciousness.canBeSubstitutedBy(consciousness.word(HELP.name()), term)) {
                statement.setConsumed(true, Intent.HELP);
                return Optional.of(pickOneOf(MarvinQuotes.HELP));
            }
        }
        // now lets get to problem, what are the new information
        int predicateIndex = getIsPredicateIndex(statement);
        if (predicateIndex < 0 ) {
            return Optional.of(pickOneOf(MarvinQuotes.NO_CLUE_STATEMENT));
        }
        // At the beginning Anna understands only a very limited gamer: subject predicate object
        // the keyword IS is our predicate which defines the relation between subject and object
        // as soon as an equation is solved and the fact are saved, the statement is consumed
        List<Term> subject = statement.getTerms().subList(0, predicateIndex);
        List<Term> object  = statement.getTerms().subList(predicateIndex + 1, statement.getTerms().size());
        long startTime = System.nanoTime();
        boolean substitutionDone   = false;
        boolean consolidationDone  = false;
        while (!statement.isConsumed()) {
            if (subject.size() == 1 && object.size() == 1) {
                // its a simple substitution
                consciousness.learnEquivalentSubstitutes(subject.get(0), object.get(0));
                statement.setConsumed(true, Intent.LEARNING);
                return Optional.empty();
            }

            // to solve the equation we need to bring it to:
            //        [numeral * word = numeral * word] form
            // if     [          word = numeral * word] or
            //        [numeral * word = word          ]
            // we need to add missing numeral with cardinality of 1 to the equation
            // if     [numeral * numeral * word = ... ]
            // we need to consolidate the numeral's to one with respect to it's system
            // if     [numeral *    word * word = ... ]
            // we need to consolidate the word's to one complex term

            // further rules:
            // 1. if a word(s) has a numeral as it's substitute -> perform the substitutions
            // 2. consolidate numerals and words (numerals of different system are not valid)
            // 3. if the base of numerals is roman -> convert to arabic
            // 4. if subject or object are missing a numeral, create one with cardinality 1 and arabic base
            // 5. now we should have [numeral * word = numeral * word] save this as Triple:
            //    c.learnConversion(new MutableTriple<>(aTerm, new Term(Term.Type.CONVERSION, "3"), bTerm));

            if (!substitutionDone){
                subject = consciousness.substituteToNumeric(subject);
                object  = consciousness.substituteToNumeric(object);
                substitutionDone = true;
            }

            if (!consolidationDone) {
                subject = consciousness.consolidate(subject);
                object = consciousness.consolidate(object);
                consolidationDone = true;
            }

            double subjectNumeric = popConvertedNumeric(subject);
            double objectNumeric  = popConvertedNumeric(object);

            boolean subjectReady  = false;
            boolean objectReady   = false;

            if (isOneWord(subject)) {
                subjectReady = true;
            }
            if (isOneWord(object)) {
                objectReady = true;
            }

            if (subjectNumeric >= 0 || objectNumeric >=0) {
                if (subjectNumeric < 0) {
                    subjectNumeric = 1;
                }
                if (objectNumeric < 0) {
                    objectNumeric = 1;
                }
                if (subjectReady && objectReady) {
                    // well we are done making sense of it
                    double factor = (objectNumeric / subjectNumeric);
                    consciousness.learnConversion(new MutableTriple<>(subject.get(0), new Term(Term.Type.CONVERSION, String.valueOf(factor)), object.get(0)));
                    statement.setConsumed(true, Intent.LEARNING);
                    return Optional.empty();
                }
            }
            LOG.debug("handleStatement(): one processing cycle took: " + (System.nanoTime() - startTime));
            if ((System.nanoTime() - startTime) > 1000000) {
                return Optional.of(pickOneOf(MarvinQuotes.TIME_OUT));
            }
        }
        return Optional.empty();
    }

    private Optional<String> handleQuestion(Question question, LinkedList<Term> recognized) {
        for (Term term : recognized) {
            if (consciousness.canBeSubstitutedBy(consciousness.word(HELP.name()), term)) {
                question.setConsumed(true, Intent.HELP);
                return Optional.of(pickOneOf(MarvinQuotes.HELP));
            } else if (consciousness.canBeSubstitutedBy(consciousness.word(HI.name()), term)) {
                question.setConsumed(true, Intent.GREETING);
                return handelGreetings(recognized);
            }
        }
        // now lets get to problem, what are the new information
        int predicateIndex = getIsPredicateIndex(question);
        if (predicateIndex < 0 ) {
            return Optional.of(pickOneOf(MarvinQuotes.NO_CLUE_STATEMENT));
        }
        // At the beginning Anna understands only a very limited gamer: subject predicate object
        // the keyword IS is our predicate which defines the relation between subject and object
        // as soon as an equation is solved and the fact are saved, the statement is consumed
        List<Term> subject = question.getTerms().subList(0, predicateIndex);
        List<Term> object  = question.getTerms().subList(predicateIndex + 1, question.getTerms().size());
        long startTime = System.nanoTime();
        boolean substitutionDone   = false;
        boolean consolidationDone  = false;
        while (!question.isConsumed()) {
            if (subject.size() == 1 && object.size() == 1) {
                // its a simple question for equity
                boolean equal = consciousness.canBeSubstitutedBy(subject.get(0), object.get(0));
                question.setConsumed(true, Intent.FACT_CHECK);
                return Optional.of(equal ? "Yes" : "No");
            }

            // marvin answers conversion questions by repeating the object as subject with solution as object
            // x? <- numeral * Optional(word) the solution is
            // numeral * Optional(word) -> y
            // because the object gets repeated as such, marvin will have to remember it

            // we don´t need to do a deep copy here because the terms get substituted not changed
            List<Term> originalObject = new ArrayList<>(object);

            // to solve the equation of conversion questions we need to bring it to one of the forms:
            // 1.      [          word = numeral * word] or
            // 2.      [               = numeral       ]
            // (bonus) the forms represents further identity checks:
            // 3.      [numeral * word = numeral * word]
            // 4.      [numeral        = numeral       ]
            // 5.      [          word =           word] (only relevant in multi term scenario - otherwise done by now)

            // further rules:
            // 1. if a word(s) has a numeral as it's substitute -> perform the substitutions
            // 2. consolidate numerals and words (numerals of different system are not valid)
            // 3. if the base of numerals is roman -> convert to arabic

            // 5. now we should have one of the forms
            // case 1   return: originalObject is numeral * factor
            // case 2   return: originalObject is numeral
            // case 3   return: boolean answer if words related (equal or convertible): numeral / numeral == factor
            // case 4   return: boolean answer:   numeral == numeral
            // case 5   return: boolean answer:   c.canBeSubstitutedBy(word1, word2);

            if (!substitutionDone){
                subject = consciousness.substituteToNumeric(subject);
                object  = consciousness.substituteToNumeric(object);
                substitutionDone = true;
            }

            if (!consolidationDone) {
                subject = consciousness.consolidate(subject);
                object = consciousness.consolidate(object);
                consolidationDone = true;
            }

            double subjectNumeric = getConvertedNumeric(subject);
            double objectNumeric  = getConvertedNumeric(object);

            boolean sNum = subjectNumeric >= 0;
            boolean oNum = objectNumeric >= 0;

            boolean sWord = hasWord(subject);
            boolean oWord = hasWord(object);

            if (!sNum && sWord && oNum && oWord) {
                // Form 1.      [          word = numeral * word]
                Optional<Term> conversion = consciousness.getConversion(getWord(object), getWord(subject), false);
                if (conversion.isPresent()) {
                    question.setConsumed(true, Intent.CONVERTING);
                    return Optional.of(getAnswerPrefix(originalObject, getCapitalizedWord(object)) + convert(objectNumeric, conversion.get()) + " " + getCapitalizedWord(subject));
                }
                return Optional.of("Sorry, I don´t know how to convert " + getCapitalizedWord(subject) + " to " + getCapitalizedWord(object));
            } else if (!sNum && !sWord && oNum && !oWord) {
                // Form 2.      [               = numeral       ]
                question.setConsumed(true, Intent.CONVERTING);
                return Optional.of(getAnswerPrefix(originalObject, null) + formatDouble(objectNumeric));
            } else if (sNum && sWord && oNum && oWord) {
                // Form 3.      [numeral * word = numeral * word]
                Optional<Term> conversion = consciousness.getConversion(getWord(object), getWord(subject), false);
                if (conversion.isPresent()) {
                    question.setConsumed(true, Intent.FACT_CHECK);
                    String subjectNumeralTarget = convert(objectNumeric, conversion.get());
                    return subjectNumeralTarget.equals(String.valueOf(subjectNumeric)) ? Optional.of("Yes") : Optional.of("No");
                }
                return Optional.of("Sorry, I don´t know how to convert " + getCapitalizedWord(subject) + " to " + getCapitalizedWord(object));
            } else if (sNum && !sWord && oNum && !oWord) {
                // Form 4.      [numeral        = numeral       ]
                question.setConsumed(true, Intent.FACT_CHECK);
                return subjectNumeric == objectNumeric ? Optional.of("Yes") : Optional.of("No");
            } else if (!sNum && sWord && !oNum && oWord) {
                // Form 5.      [          word =           word]
                Term s = getWord(subject);
                Term o = getWord(object);
                question.setConsumed(true, Intent.FACT_CHECK);
                boolean result = s.equals(o) || consciousness.canBeSubstitutedBy(s, o);
                return Optional.of(result ? "Yes" : "No");
            }
            LOG.debug("handleQuestion() one processing cycle took: " + (System.nanoTime() - startTime));
            if ((System.nanoTime() - startTime) > 1000000) {
                return Optional.of(pickOneOf(MarvinQuotes.TIME_OUT));
            }
        }
        return Optional.of(pickOneOf(MarvinQuotes.NO_CLUE_QUESTION));
    }

    private String formatDouble(double numeric) {
        if(numeric == (long) numeric) {
            return String.format("%d", (long)numeric);
        } else {
            return String.format("%s", numeric);
        }
    }

    private String convert(double numeric, Term factor) {
        return formatDouble(numeric * Double.valueOf(factor.getValue()));
    }

    private void manageValue(Sentence sentence, LinkedList<Term> recognized, String value, Term.Type arabicNumber) {
        Term term = new Term(arabicNumber, value);
        sentence.addTerms(term);
        if (consciousness.learnOne(term)) {
            recognized.add(term);
        }
    }

    private String getAnswerPrefix(List<Term> newSubject, String nomen) {
        StringBuffer sb = new StringBuffer();
        // append subject
        for (Term term : newSubject) {
            if (nomen != null && term.getValue().equalsIgnoreCase(nomen)) {
                sb.append(StringUtils.capitalize(term.getValue()) + " ");
            } else {
                sb.append(term.getValue() + " ");
            }
        }
        // append predicate
        sb.append(Keyword.IS.name().toLowerCase() + " ");
        return sb.toString();
    }

    private String getCapitalizedWord(List<Term> terms) {
        Term word = getWord(terms);
        if (word != null) {
            return StringUtils.capitalize(word.getValue());
        } else {
            LOG.error("getCapitalizedWord(): should not happen, no word found in " + terms );
            return null;
        }
    }

    private boolean hasWord(List<Term> terms) {
        return getWord(terms) != null;
    }

    private Term getWord(List<Term> terms) {
        for (Term term : terms) {
            if (isWord(term)) {
                return term;
            }
        }
        return null;
    }

    private boolean isWord(Term term) {
        return term.getType().equals(Term.Type.WORD);
    }

    private boolean isOneWord(List<Term> terms) {
        return terms.size() == 1 && terms.get(0).getType().equals(Term.Type.WORD);
    }

    private double getConvertedNumeric(List<Term> subject){
        return getAndOptionallyRemoveConvertedNumeric(subject, false);
    }

    private double popConvertedNumeric(List<Term> subject) {
        return getAndOptionallyRemoveConvertedNumeric(subject, true);
    }

    private double getAndOptionallyRemoveConvertedNumeric(List<Term> subject, boolean remove) {
        ListIterator<Term> subjectIt = subject.listIterator();
        while (subjectIt.hasNext()) {
            Term term = subjectIt.next();
            if (term.getType().equals(Term.Type.ROMAN_NUMBER)) {
                if (remove) subjectIt.remove();
                return Converter.romanToArabic(term.getValue());
            } else if (term.getType().equals(Term.Type.ARABIC_NUMBER)) {
                if (remove) subjectIt.remove();
                return Double.valueOf(term.getValue());
            }
        }
        return -1;
    }

    private int getIsPredicateIndex(Sentence statement) {
        Term is = consciousness.word(IS.name());
        int predicateIndex = statement.indexOf(is);
        if (predicateIndex < 0 ) {
            predicateIndex = statement.indexOf(consciousness.getSubstitutes(is).get());
        }
        return predicateIndex;
    }

    private Optional<String> handelGreetings(LinkedList<Term> recognized) {
        if (recognized.contains(consciousness.word(UP.name()))) {
            return Optional.of(pickOneOf(MarvinQuotes.GREETING_WITH_UP));
        } else if (recognized.contains(consciousness.word(HOW.name()))) {
            return Optional.of(pickOneOf(MarvinQuotes.GREETING_WITH_HOW));
        } else {
            return Optional.of(pickOneOf(MarvinQuotes.GREETING));
        }
    }

    private String pickOneOf(List<String> quotes) {
        Validate.notNull(quotes, "quotes list must not be NULL");
        Validate.notEmpty(quotes, "there must be some options to choose from");
        Random rand = new Random();
        return quotes.get(rand.nextInt(quotes.size()));
    }


    /**
     *  This is Anna's Consciousness. This class must be private because of the Data Protection Act.
     *  However we need to test it, so it's package private, sorry Anna.
     */
    static class Consciousness {

        final boolean RECOGNIZED = true;
        final boolean NEW = !RECOGNIZED;

        public Logger LOG = Logger.getLogger(Consciousness.class);

        Map<Term, Set<Term>> equalityKnowledge = new HashMap<>();
        Map<Term, Set<Triple<Term, Term, Term>>> inequalityKnowledge = new HashMap<>();

        public Consciousness() {
            init();
        }

        /**
         * This method adds multiple terms to Consciousness if its not present yet
         *
         * @param terms the terms, which should be learned
         * @return weather or not the one of terms has been recognized
         */
        boolean learn(Term... terms) {
            boolean result = NEW;
            for (Term term : terms) {
                if (learnOne(term)) {
                    result = RECOGNIZED;
                }
            }
            return result;
        }

        /**
         * This method adds a term to Anna's consciousness if its not present yet
         *
         * @param term the term, which should be learned
         * @return weather or the term has been recognized as known
         */
        boolean learnOne(Term term) {
            if (!equalityKnowledge.containsKey(term)) {
                equalityKnowledge.put(term, new HashSet<>());
                LOG.debug("learn(): " + term);
                return NEW;
            } else {
                LOG.debug("learn(): " + term + " has already been known");
                return RECOGNIZED;
            }
        }

        /**
         * This method adds multiple triples of terms to consciousness or updates those if present
         *
         * @param conversions the relation, which should be learned
         * @return weather or not the one of inequalityKnowledge has been recognized and updated
         */
        boolean learnConversions(Triple<Term, Term, Term>... conversions) {
            boolean result = NEW;
            for (Triple conversion : conversions) {
                if (learnConversion(conversion)) {
                    result = RECOGNIZED;
                }
            }
            return result;
        }

        /**
         * This method adds two triple of terms to Anna's consciousness or updates it if present
         * The first one is the one provided, the other one is a inverted one
         *
         * @param conversion the term, which should be learned
         * @return weather on of the relations has been recognized as known and updated
         */
        boolean learnConversion(Triple<Term, Term, Term> conversion) {
            // left to right
            boolean result1 = learnOneWayConversion(conversion);
            Triple<Term, Term, Term> inverted = new ImmutableTriple<>(conversion.getRight(), invert(conversion.getMiddle()), conversion.getLeft());
            boolean result2 = learnOneWayConversion(inverted);
            return result1 && result2;
        }

        /**
         * This method adds a triple of terms to Anna's consciousness or updates it if present
         *
         * @param conversion the term, which should be learned
         * @return weather or the relation has been recognized as known and updated
         */
        boolean learnOneWayConversion(Triple<Term, Term, Term> conversion) {
            boolean result = NEW;
            if (!inequalityKnowledge.containsKey(conversion.getLeft())) {
                // create
                LOG.debug("learnOneConversion(): " + conversion);
                inequalityKnowledge.put(conversion.getLeft(), new HashSet<>(Arrays.asList(conversion)));
            } else {
                Optional<Term> optionalConversion = getConversion(conversion.getLeft(), conversion.getRight(), true);
                Set<Triple<Term, Term, Term>> triples = inequalityKnowledge.get(conversion.getLeft());
                if (optionalConversion.isPresent()) {
                    result = RECOGNIZED;
                }
                triples.add(conversion);
            }

            return result;
        }

        /**
         * Checks for known conversion between two Terms
         * @param a The Term that should be converted into b
         * @param b s.o.
         * @param remove In case the conversion should be found, because the terms are immutable it needs to be removed
         *               before it can be replaced by a new one.
         * @return
         */
        Optional<Term> getConversion(Term a, Term b, boolean remove) {
            Validate.notNull(a, "Term a can not be NULL");
            Validate.notNull(b, "Term b can not be NULL");
            Set<Triple<Term, Term, Term>> triples = inequalityKnowledge.get(a);
            Iterator<Triple<Term, Term, Term>> iterator = triples.iterator();
            while (iterator.hasNext()) {
                Triple<Term, Term, Term> triple = iterator.next();
                if (triple.getLeft().equals(a) &&
                        triple.getRight().equals(b) &&
                        triple.getMiddle().getType().equals(Term.Type.CONVERSION)) {
                    Term conversion = triple.getMiddle();
                    if (remove) {
                        iterator.remove();
                    }
                    return Optional.of(conversion);
                }
            }
            return Optional.empty();
        }

        Term invert(Term middle) {
            Validate.isTrue(middle.getType().equals(Term.Type.CONVERSION), "The type of the predicate " + middle + " is not " + Term.Type.CONVERSION);
            Validate.notBlank(middle.getValue(), "The value of of predicate " + middle + " can not be blank or empty");
            // StringUtils fails to recognize 17.0 for example
//            Validate.isTrue(StringUtils.isNumeric(middle.getValue()), "The value of predicate " + middle + " is not numeric");
            return new Term(Term.Type.CONVERSION, String.valueOf(1 / Double.valueOf(middle.getValue())));
        }

        boolean isNumeric(Term term) {
            return term.getType().equals(Term.Type.ROMAN_NUMBER) || term.getType().equals(Term.Type.ARABIC_NUMBER);
        }

        /**
         * This method creates the cross product between the substitutes and add those to Anna's consciousness if its not present yet
         *
         * @param substitutesToAdd the substitutes can be used for the term
         * @return weather or one of the term has been recognized as known
         */
        boolean learnEquivalentSubstitutes(Term... substitutesToAdd) {
            boolean result = NEW;
            for (Term term : substitutesToAdd) {
                if (learnImplicitSubstitutes(term, ArrayUtils.removeElement(substitutesToAdd, term))) {
                    result = RECOGNIZED;
                }
            }
            return result;
        }

        /**
         * This method adds substitutes for a term to Anna's consciousness if its not present yet
         *
         * @param term the term, which can be substituted by substitutesToAdd
         * @param substitutesToAdd the substitutes can be used for the term
         * @return weather or the term has been recognized as known
         */
        boolean learnImplicitSubstitutes(Term term, Term... substitutesToAdd) {
            if (!equalityKnowledge.containsKey(term)) {
                equalityKnowledge.put(term, new HashSet<>(Arrays.asList(substitutesToAdd)));
                LOG.debug("learnSubstitute(): for term " + term + " I can also use " + Arrays.toString(substitutesToAdd));
                return NEW;
            } else {
                Set<Term> terms = equalityKnowledge.get(term);
                for (Term termN : substitutesToAdd) {
                    if (terms.add(termN)) {
                        LOG.debug("learnSubstitute(): for term " + term + " I can also use " + termN);
                    } else {
                        LOG.debug("learnSubstitute(): for term " + term + " substitute " + termN + " has already been known");
                    }
                }
                return RECOGNIZED;
            }
        }

        /**
         * if a word(s) has a numeral as it's substitute -> perform the substitutions
         *
         * @param terms which are might need substitution
         * @return
         */
        List<Term> substituteToNumeric(List<Term> terms) {
            ListIterator<Term> iterator = terms.listIterator();
            while (iterator.hasNext()) {
                Term term = iterator.next();
                if (!isNumeric(term)) {
                    Optional<Set<Term>> substitutes = getNumericSubstitutes(term);
                    if (substitutes.isPresent()) {
                        Set<Term> numeralTerms = substitutes.get();
                        if (numeralTerms.size() == 1) {
                            Term substitute = numeralTerms.iterator().next();
                            LOG.debug("substituteToNumeric(): exact one substitute for " +term+ " has been found: " + substitute);
                            iterator.set(substitute);
                        } else if (numeralTerms.size() > 1) {
                            Term bestMatch = null;
                            for (Term numeralTerm : numeralTerms) {
                                if (bestMatch == null) {
                                    bestMatch = numeralTerm;
                                }
                                if (numeralTerm.getType().equals(Term.Type.ARABIC_NUMBER)) {
                                    bestMatch = numeralTerm;
                                    break;
                                }
                            }
                            iterator.set(bestMatch);
                            LOG.warn("substituteToNumeric(): multiple substitutes for " +term+ " has been found. Choose: " + bestMatch);
                        } else {
                            LOG.debug("substituteToNumeric(): no substitution found for found: " + term);
                        }
                    }
                }
            }
            return terms;
        }

        /**
         * consolidate numerals and words (numerals of different system are not valid)
         * Two roman numeric terms can be merged into one by concatenation (glob glob)
         * Two literals must be merged into one
         * Keywords are for intents, thus should be taken out of the equation
         *
         * @param terms which are might need consolidation
         * @return
         */
        public List<Term> consolidate(List<Term> terms) {
            List<Term> result = new LinkedList<>();
            Term last = null;
            for (int i = 0; i < terms.size(); i++) {
                Term current = terms.get(i);
                if (last == null) {
                    if (!(current.getType().equals(Term.Type.WORD) && isKeyword(current.getValue()))) {
                        last = current;
                    }
                    continue;
                }
                Term.Type lType = last.getType();
                Term.Type cType = current.getType();
                if (lType.equals(Term.Type.ROMAN_NUMBER) && cType.equals(Term.Type.ROMAN_NUMBER)) {
                    Term consolidated = new Term(Term.Type.ROMAN_NUMBER, last.getValue() + current.getValue());
                    last = consolidated;
                } else if (lType.equals(Term.Type.ARABIC_NUMBER) && cType.equals(Term.Type.ARABIC_NUMBER)) {
                    // Two arabic numbers are probably a result of the split on the decimal sign
                    // FIXME provide a better solution in the future
                    Term consolidated = new Term(Term.Type.ARABIC_NUMBER, last.getValue() + "." + current.getValue());
                    last = consolidated;
                } else if (lType.equals(Term.Type.WORD) && cType.equals(Term.Type.WORD) && !isKeyword(current.getValue())) {
                    Term consolidated = new Term(Term.Type.WORD, last.getValue() + " " + current.getValue());
                    last = consolidated;
                }else if (!isKeyword(last.getValue())) {
                    result.add(last);
                    last = current;
                } else {
                    last = null;
                }
            }
            if (last != null && !isKeyword(last.getValue())) {
                result.add(last);
            }
            return result;
        }

        boolean canBeSubstitutedBy(Term a, Term b) {
            Set<Term> terms = equalityKnowledge.get(a);
            return a.getValue().equals(b.getValue()) || (terms != null && terms.contains(b));
        }

        Optional<Set<Term>> getSubstitutes(Term a) {
            return Optional.ofNullable(equalityKnowledge.get(a));
        }

        Optional<Set<Term>> getNumericSubstitutes(Term a) {
            Set<Term> result = new HashSet<>();
            Optional<Set<Term>> substitutes = getSubstitutes(a);
            if (substitutes.isPresent()) {
                for (Term term : substitutes.get()) {
                    if (isNumeric(term)) {
                        result.add(term);
                    }
                }
            }
            return result.size() == 0 ? Optional.empty() : Optional.of(result);
        }

        Term word(String value) {
            return new Term(Term.Type.WORD, value);
        }

        Term arabic(String value) {
            return new Term(Term.Type.ARABIC_NUMBER, value);
        }

        Term learnRoman(String value, String arabicEquivalent) {
            Term roma = new Term(Term.Type.ROMAN_NUMBER, value);
            Term arab = arabic(arabicEquivalent);
            learnEquivalentSubstitutes(roma, arab);
            return roma;
        }

        private void init() {
            // civilities
            learnEquivalentSubstitutes(word(HI.name()), word("holla"), word("hello"), word("hallo"), word("moin"), word("whats"));
            learnEquivalentSubstitutes(word(BYE.name()), word("exit"), word("terminate"), word("fine"), word("finito"), word("by"));
            learnEquivalentSubstitutes(word(HELP.name()), word("manual"), word("suggestion"));
            // conversation
            learn(word("?"), word(A.name()), word(IS.name()), word(UP.name()), word(HOW.name()), word(MUCH.name()), word(MANY.name()));
            // conversion
            learnRoman("I", "1");
            learnRoman("V", "5");
            learnRoman("X", "10");
            learnRoman("L", "50");
            learnRoman("C", "100");
            learnRoman("D", "500");
            learnRoman("M", "1000");
        }

    }

}
