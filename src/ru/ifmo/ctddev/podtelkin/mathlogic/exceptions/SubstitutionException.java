package ru.ifmo.ctddev.podtelkin.mathlogic.exceptions;

/**
 * Created by vlad107 on 04.05.16.
 */
public class SubstitutionException extends Exception {
    private String X;
    private String Y;
    private String a;

    public SubstitutionException() { super(); }

    public SubstitutionException(String X, String a) {
        this.X = X;
        this.a = a;
    }

    public SubstitutionException(String X, String Y, String a) {
        super("терм " + X + " не свободен для подстановки в формулу " + Y + " вместо переменной " + a);
    }

    public String getX() {
        return X;
    }

    public String getA() {
        return a;
    }
}
