package ru.ifmo.ctddev.podtelkin.mathlogic.annotation;

/**
 * Created by vlad107 on 04.05.16.
 */
public class Hypothesis implements Annotation {
    private final int index;

    public Hypothesis(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Гипотеза " + Integer.toString(index);
    }
}
