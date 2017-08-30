package com.macilias.apps.model;

import org.apache.commons.lang3.Validate;

/**
 * The Atom of a Sentence
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class Term {

    private final Type type;
    private final String  value;

    public Term(Type type, String value) {
        Validate.notNull(type, "type can not be NULL");
        value = value.replaceAll("[^\\p{IsAlphabetic}^\\p{IsDigit}^?^.^ ^$^€^£]", "").toLowerCase();
        Validate.notBlank(value, "value can not be blank or empty");
        this.type = type;
        this.value = value;
    }

    public enum Type {

        CONVERSION, WORD, ARABIC_NUMBER, ROMAN_NUMBER

    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Term term = (Term) o;

        if (type != term.type) return false;
        return value != null ? value.equalsIgnoreCase(term.value) : term.value == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Term{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
