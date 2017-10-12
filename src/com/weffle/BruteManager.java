package com.weffle;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * The com.weffle.BruteManager class using brute force methods for finding passwords and ect.
 * @author Danylo Prykhodko
 */

public class BruteManager {
    private final char[] chars; // Charset.
    private final int length; // Brute length.
    private volatile BigInteger index; // Index.
    private BigInteger lastIndex; // Last index.

    /** Constructor. */
    public BruteManager(char[] chars, int length) {
        if (chars == null)
            throw new NullPointerException("Charset is null.");
        if (length <= 0)
            throw new IllegalArgumentException("Out of range. Length is negative or equals zero.");

        this.chars = chars;
        this.length = length;
        index = BigInteger.ZERO;
        lastIndex = BigInteger.valueOf(chars.length).pow(length);
    }

    /** Brute from the index. */
    public String brute(BigInteger index) {
        if (index.compareTo(BigInteger.ZERO) == -1)
            throw new IllegalArgumentException("Out of range. Index less than zero.");
        if (index.compareTo(lastIndex) == 1)
            throw new IllegalArgumentException("Out of range. Index greater than last index.");

        StringBuilder brute = new StringBuilder();
        // While the index is greater than 0.
        while (index.signum() == 1) {
            brute.insert(0, chars[index.subtract(index.divide(BigInteger.valueOf(chars.length)).multiply(BigInteger.valueOf(chars.length))).intValue()]);
            index = index.divide(BigInteger.valueOf(chars.length));
        }
        while (brute.length() < length)
            brute.insert(0, chars[0]);
        return brute.toString();
    }

    /** Next brute. */
    public synchronized String  nextBrute() throws NullPointerException {
        if (!isAlive())
            throw new NullPointerException();
        String brute = brute(index);
        // Index increment.
        index = index.add(BigInteger.ONE);
        return brute;
    }

    /** Get index of the brute. */
    public BigInteger toIndex(String brute) {
        if (brute == null)
            throw new NullPointerException("Brute is null");
        if (brute.length() != length)
            throw new IllegalArgumentException("Out of range. Length isn't the same as brute length.");
        String illegals = brute.replaceAll(String.format("[%s]", String.valueOf(chars)), "");
        if (illegals.length() > 0) {
            String s = "Brute contains illegal char";
            if (illegals.length() == 1)
                s += String.format(" = %s.", illegals);
            else
                s += String.format("s = %s.", Arrays.toString(illegals.toCharArray()));
            throw new IllegalArgumentException(s);
        }

        BigInteger index = BigInteger.ZERO;
        int[] count = new int[brute.length()];
        for (int i = 0; i < brute.length(); i++) {
            char c = brute.toCharArray()[i];
            for (int j = 0; j < chars.length; j++)
                if (c == chars[j])
                    count[i] = j;
        }
        for (int i = 0; i < brute.length(); i++) {
            BigInteger a = new BigInteger(String.valueOf(count[i]));
            for (int j = 0; j < brute.length() - (i + 1); j++)
                a = a.multiply(BigInteger.valueOf(chars.length));
            index = index.add(a);
        }
        return index;
    }

    /** Get brute force progress. */
    public double getProgress(int afterDecimalPoint) {
        if (afterDecimalPoint < 0)
            throw new IllegalArgumentException("Out of range. Argument can't be less zero.");
        if (afterDecimalPoint > 16)
            throw new IllegalArgumentException("Out of range. Argument can't be greater sixteen.");

        BigInteger e = BigInteger.valueOf(10).pow(afterDecimalPoint);
        return index.multiply(e).divide(lastIndex).doubleValue() / e.doubleValue();
    }

    /** Get brute force progress with two digit after decimal point. */
    public double getProgress() {
        return getProgress(2);
    }

    /** The index is less than the last index. */
    public boolean isAlive() {
        return index.compareTo(lastIndex) == -1;
    }

    /** Get brute index. */
    public BigInteger getIndex() {
        return index;
    }

    /** Set brute index. */
    public String setIndex(BigInteger index) {
        this.index = index;
        return brute(index);
    }

    /** Get last index of brute. */
    public BigInteger getLastIndex() {
        return lastIndex;
    }
}
