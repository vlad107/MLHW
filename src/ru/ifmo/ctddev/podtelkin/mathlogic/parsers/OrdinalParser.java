package ru.ifmo.ctddev.podtelkin.mathlogic.parsers;

import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ArithmeticMatcherException;
import ru.ifmo.ctddev.podtelkin.mathlogic.ordinals.Ordinal;

/**
 * Created by vlad107 on 10.05.16.
 *
 */
public class OrdinalParser {

    private final String str;
    private int ptr;

    public OrdinalParser(String str) {
        str = str.replaceAll("\\s+", "");
        this.str = str;
    }

    public Ordinal parse() throws ArithmeticMatcherException {
        ptr = 0;
        return parseOrdinal();
    }

    private Ordinal parseOrdinal() throws ArithmeticMatcherException {
        Ordinal summand = parseSummand();
        if ((ptr < str.length()) && (str.charAt(ptr) == '+')) {
            ++ptr;
            Ordinal ordinal = parseOrdinal();
            return Ordinal.addO(summand, ordinal);
        }
        return summand;
    }

    private Ordinal parseSummand() throws ArithmeticMatcherException {
        Ordinal mult = parseMult();
        if ((ptr < str.length()) && (str.charAt(ptr) == '*')) {
            ++ptr;
            Ordinal summand = parseSummand();
            return Ordinal.multO(mult, summand);
        }
        return mult;
    }

    private Ordinal parseMult() throws ArithmeticMatcherException {
        Ordinal term = parseTerm();
        if ((ptr < str.length()) && (str.charAt(ptr) == '^')) {
            ++ptr;
            Ordinal mult = parseMult();
            return Ordinal.expO(term, mult);
        }
        return term;
    }

    private Ordinal parseTerm() throws ArithmeticMatcherException {
        if ((ptr < str.length()) && (str.charAt(ptr) == 'w')) {
            ++ptr;
            return new Ordinal(-1);
        }
        if ((ptr < str.length()) && (Character.isDigit(str.charAt(ptr)))) {
            int ptr2 = ptr + 1;
            while ((ptr2 < str.length()) && (Character.isDigit(str.charAt(ptr2)))) ++ptr2;
            String valStr = str.substring(ptr, ptr2);
            ptr = ptr2;
            return new Ordinal(Long.valueOf(valStr));
        }
        if ((ptr < str.length()) && (str.charAt(ptr) == '(')) {
            ++ptr;
            Ordinal ordinal = parseOrdinal();
            if ((ptr >= str.length()) || (str.charAt(ptr) != ')')) {
                throw new ArithmeticMatcherException("couldn't parse ordinal: " + str.substring(ptr));
            }
            ++ptr;
            return ordinal;
        }
        throw new ArithmeticMatcherException("couldn't parse ordinal: " + str.substring(ptr));
    }
}
