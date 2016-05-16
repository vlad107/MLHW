package ru.ifmo.ctddev.podtelkin.mathlogic.ordinals;

import java.util.List;
import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by vlad107 on 10.05.16.
 */
public class Ordinal {

    private final Ordinal firstO;
    private final Ordinal restO;
    private final long p;

    public Ordinal(long x, long y) {
        p = -1;
        firstO = new Ordinal(x);
        restO = new Ordinal(y);
    }

    public Ordinal(Ordinal a, Ordinal b) {
        firstO = a;
        restO = b;
        p = -1;
    }

    public Ordinal(Ordinal a, long x) {
        firstO = a;
        restO = new Ordinal(x);
        p = -1;
    }

    public Ordinal(long val) {
        if (val == -1) {
            firstO = new Ordinal(new Ordinal(1), new Ordinal(1));
            restO = new Ordinal(0);
            p = -1;
        } else {
            firstO = null;
            restO = null;
            p = val;
        }
    }

    public static Ordinal addO(Ordinal a, Ordinal b) {
        if ((atom(a)) && (atom(b))) return new Ordinal(a.val() + b.val());
        int tmp = cmpO(fe(a), fe(b));
        if (tmp == -1) return b;
        if (tmp == 0) return dot(new Ordinal(fe(a), fc(a) + fc(b)), rest(b));
        return dot(new Ordinal(fe(a), fc(a)), addO(rest(a), b));
    }

    public static Ordinal multO(Ordinal a, Ordinal b) {
        return pmult(a, b, 0);
    }

    public static int cmpO(Ordinal a, Ordinal b) {
        if ((atom(a)) && (atom(b))) return Long.compare(a.val(), b.val());
        if (atom(a)) return -1;
        if (atom(b)) return +1;
        int tmp;
        if ((tmp = cmpO(fe(a), fe(b))) != 0) return tmp;
        if ((tmp = Long.compare(fc(a), fc(b))) != 0) return tmp;
        return cmpO(rest(a), rest(b));
    }

    public static Ordinal expO(Ordinal a, Ordinal b) {
        if ((isZero(b)) || (isOne(a))) return oneO();
        if (isZero(a)) return zeroO();
        if ((atom(a)) && (atom(b))) return new Ordinal(expW(a.val(), b.val()));
        if (atom(a)) return exp1(a.val(), b);
        if (atom(b)) return exp3(a, b.val());
        return exp4(a, b);
    }

    private static boolean isOne(Ordinal a) {
        return atom(a) && a.val() == 1;
    }

    private static Ordinal exp1(long p, Ordinal b) {
        if (fe(b).val() == 1) {            // p**(k*w+b) = (w**k)*(p**b)
            return new Ordinal(new Ordinal(fc(b), expW(p, rest(b).val())), zeroO());
        }
        if (atom(rest(b))) { // p**((w**alpha)*k+b) = w**(w**(alpha-1)*k)+p**b
            return new Ordinal(
                    new Ordinal(
                            new Ordinal(
                                    new Ordinal(
                                            minusO(fe(b), oneO()),
                                            fc(b)),
                                    zeroO()),
                            expW(p, rest(b).val())),
                    zeroO());
        }
        Ordinal c = exp1(p, rest(b));
        return new Ordinal(new Ordinal(dot(new Ordinal(minusO(fe(b), oneO()), oneO()), fe(c)), fc(c)), zeroO());
    }

    private static Ordinal exp2(Ordinal a, long q) {
        if (q == 1) return a;
        return multO(
                new Ordinal(
                    new Ordinal(
                            multO(fe(a), new Ordinal(q - 1)),
                            oneO()),
                    zeroO()),
                a);
    }

    private static Ordinal exp3(Ordinal a, long q) {
        if (q == 0) return oneO();
        if (q == 1) return a;
        if (limitp(a)) return exp2(a, q);
        return multO(exp3(a, q - 1), a);
    }

    private static Ordinal exp4(Ordinal a, Ordinal b) {
        return multO(
                new Ordinal(new Ordinal(multO(fe(a), limitpart(b)), oneO()), zeroO()),
                exp3(a, natpart(b))
        );
    }


    private static int len(Ordinal a) {
        if (atom(a)) return 0;
        return 1 + len(rest(a));
    }

    private static long natpart(Ordinal a) {
        if (atom(a)) return a.val();
        return natpart(rest(a));
    }

    private static Ordinal limitpart(Ordinal a) {
        if (atom(a)) return zeroO();
        return dot(first(a), limitpart(rest(a)));
    }

    private static boolean limitp(Ordinal a) {
        if (atom(a)) return a.val() == 0;
        return limitp(rest(a));
    }

    private static Ordinal minusO(Ordinal a, Ordinal b) {
        if ((atom(a)) && (atom(b)) && (a.val() <= b.val())) return zeroO();
        if ((atom(a)) && (atom(b))) return new Ordinal(a.val() - b.val());
        int tmp = cmpO(fe(a), fe(b));
        if (tmp == -1) return zeroO();
        if (tmp == +1) return a;
        if (fc(a) < fc(b)) return zeroO();
        if (fc(a) > fc(b)) return dot(new Ordinal(fe(a), fc(a) - fc(b)), rest(a));
        return minusO(rest(a), rest(b));
    }

    private static long expW(long x, long y) {
        long res = 1;
        while (y > 0) {
            if (y % 2 == 1) res *= x;
            y /= 2;
            x *= x;
        }
        return res;
    }

    private static Ordinal pmult(Ordinal a, Ordinal b, int n) {
        if ((isZero(a)) || (isZero(b))) return zeroO();
        if ((atom(a)) && (atom(b))) return new Ordinal(a.val() * b.val());
        if (atom(b)) return dot(new Ordinal(fe(a), fc(a) * b.val()), rest(a));
        int m = count(fe(a), fe(b), n);
        return dot(new Ordinal(padd(fe(a), fe(b), m), fc(b)), pmult(a, rest(b), m));
    }

    private static boolean isZero(Ordinal a) {
        return atom(a) && a.val() == 0;
    }

    private static Ordinal padd(Ordinal a, Ordinal b, int n) {
        return append(firstn(a, n), addO(restn(a, n), b));
    }

    private static Ordinal append(Ordinal a, Ordinal b) {
        if (atom(a)) return b;
        return dot(first(a), append(rest(a), b));
    }

    private static int count(Ordinal a, Ordinal b, int n) {
        return n + c(restn(a, n), b);
    }

    private static Ordinal firstn(Ordinal a, int n) {
        if (n == 0) return null;
        return dot(first(a), firstn(a, n - 1));
    }

    private static Ordinal restn(Ordinal a, int n) {
        if (n == 0) return a;
        return restn(rest(a), n - 1);
    }

    private static int c(Ordinal a, Ordinal b) {
        if (cmpO(fe(b), fe(a)) == -1) return 1 + c(rest(a), b);
        return 0;
    }

    private static Ordinal dot(Ordinal a, Ordinal b) {
        return new Ordinal(a, b);
    }

    private static long fc(Ordinal a) {
        if (atom(a)) return 0;
        return rest(first(a)).val();
    }

    private static Ordinal fe(Ordinal a) {
        if (atom(a)) return zeroO();
        return first(first(a));
    }

    private static Ordinal zeroO() {
        return new Ordinal(0);
    }

    private static Ordinal oneO() {
        return new Ordinal(1);
    }

    private static boolean atom(Ordinal a) {
        if (a == null) return true;
        return a.val() != -1;
    }

    private static Ordinal first(Ordinal a) {
        return a.getFirstO();
    }

    private static Ordinal rest(Ordinal a) {
        return a.getRestO();
    }

    private long val() {
        return p;
    }

    private Ordinal getRestO() {
        return restO;
    }

    private Ordinal getFirstO() {
        return firstO;
    }
}
