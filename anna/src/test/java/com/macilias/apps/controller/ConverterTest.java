package com.macilias.apps.controller;

import org.junit.Test;

import static com.macilias.apps.controller.Converter.arabicToRoman;
import static com.macilias.apps.controller.Converter.romanToArabic;
import static org.junit.Assert.assertEquals;

/**
 * @author Maciej Niemczyk [maciej@gmx.de]
 */
public class ConverterTest {

    @Test
    public void convertArabicToRoman() throws Exception {

        assertEquals("MCMXCIX", arabicToRoman(1999));
        assertEquals("LXXX", arabicToRoman(80));
        assertEquals("II", arabicToRoman(2));
        assertEquals("IV", arabicToRoman(4));
        assertEquals("XX", arabicToRoman(20));
        assertEquals("XLII", arabicToRoman(42));

    }

    @Test
    public void convertRomanToArabic() throws Exception {

        assertEquals(1999, romanToArabic("MCMXCIX"));
        assertEquals(80, romanToArabic("LXXX"));
        assertEquals(2, romanToArabic("II"));
        assertEquals(4, romanToArabic("IV"));
        assertEquals(20, romanToArabic("XX"));
        assertEquals(42, romanToArabic("XLII"));

    }

}