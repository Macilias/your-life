package com.macilias.apps.controller;

import org.apache.commons.lang3.Validate;

import java.util.TreeMap;

/**
 * Converter based on common programming katas solutions
 *
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class Converter {

    /**
     * Convert arabic numeral to roman
     *
     * @param value
     * @return
     */
    public static String arabicToRoman(int value) {
        Validate.isTrue(value > 0, "Only positive numbers can be converted");
        int recognized = equivalents.floorKey(value);
        if ( value == recognized ) {
            return equivalents.get(value);
        }
        return equivalents.get(recognized) + arabicToRoman(value - recognized);
    }

    /**
     * Convert roman numeral to arabic
     *
     * @param value
     * @return
     */
    public static int romanToArabic(String value) {
        Validate.notBlank("Only not blank roman numbers can be converted");
        int result = 0;
        int last = 0;
        String roman = value.trim().toUpperCase();

        for (int i = roman.length() - 1; i >= 0; i--) {

            char convertToDecimal = roman.charAt(i);

            switch (convertToDecimal) {
                case 'M':
                    result = internalProcessRoman(1000, last, result);
                    last = 1000;
                    break;
                case 'D':
                    result = internalProcessRoman(500, last, result);
                    last = 500;
                    break;
                case 'C':
                    result = internalProcessRoman(100, last, result);
                    last = 100;
                    break;
                case 'L':
                    result = internalProcessRoman(50, last, result);
                    last = 50;
                    break;
                case 'X':
                    result = internalProcessRoman(10, last, result);
                    last = 10;
                    break;
                case 'V':
                    result = internalProcessRoman(5, last, result);
                    last = 5;
                    break;
                case 'I':
                    result = internalProcessRoman(1, last, result);
                    last = 1;
                    break;
            }
        }
        return result;
    }

    private static int internalProcessRoman(int current, int last, int result) {
        if (last > current) {
            return result - current;
        } else {
            return result + current;
        }
    }

    private static TreeMap<Integer, String> equivalents = new TreeMap<>();

    static {
        equivalents.put(1000, "M");
        equivalents.put(900, "CM");
        equivalents.put(500, "D");
        equivalents.put(400, "CD");
        equivalents.put(100, "C");
        equivalents.put(90, "XC");
        equivalents.put(50, "L");
        equivalents.put(40, "XL");
        equivalents.put(10, "X");
        equivalents.put(9, "IX");
        equivalents.put(5, "V");
        equivalents.put(4, "IV");
        equivalents.put(1, "I");
    }



}
