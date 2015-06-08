package de.katzenpapst.proceduralgalaxy.worldgen.helper;

import org.apache.commons.lang3.StringUtils;

/**
 * Stolen from http://rosettacode.org/wiki/Roman_numerals/Encode#Java
 * @author katzenpapst
 *
 */
public class NumeralHelper {
	enum RomanNumeral {
        I(1), IV(4), V(5), IX(9), X(10), XL(40), L(50), XC(90), C(100), CD(400), D(500), CM(900), M(1000);
        int weight;
 
        RomanNumeral(int weight) {
            this.weight = weight;
        }
    };
 
    public static String roman(long n) {
 
        if( n <= 0) {
            throw new IllegalArgumentException();
        }
 
        StringBuilder buf = new StringBuilder();
 
        final RomanNumeral[] values = RomanNumeral.values();
        for (int i = values.length - 1; i >= 0; i--) {
            while (n >= values[i].weight) {
                buf.append(values[i]);
                n -= values[i].weight;
            }
        }
        return buf.toString();
    }
    
    public static String letter(int n) {
    	if( n <= 0) {
            throw new IllegalArgumentException();
        }
    	String result = "";
    	// n >= 1
    	// a = 97
    	// z = 122
    	if(n > 26) {
    		result = StringUtils.repeat("z", (int)n/26);
    		n = n % 26;
    	}
    	result = result.concat(new String(Character.toChars(n+96)));
    	
    	return result;
    }
}
