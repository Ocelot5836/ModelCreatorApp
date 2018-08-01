package com.ocelot.mod.lib;

public class Rational {

    private int numerator, denominator;

    public Rational(double decimal) {
        String string = String.valueOf(decimal);
        int digitsDec = string.length() - 1 - string.indexOf('.');
        int denominator = 1; 

        for (int i = 0; i < digitsDec; i++) {
            decimal *= 10;    
            denominator *= 10;
        }

        int numerator = (int) Math.round(decimal);
        int gcd = gcd(numerator, denominator); 

        this.numerator = numerator / gcd;
        this.denominator = denominator /gcd;
    }

    public Rational(int num, int denom) {
        this.numerator = num;
        this.denominator = denom;
    }
    
    public int getNumerator() {
		return numerator;
	}
    
    public int getDenominator() {
		return denominator;
	}

    public static int gcd(int numerator, int denominator) {
        return denominator == 0 ? numerator : gcd(denominator, numerator % denominator);
    }
}