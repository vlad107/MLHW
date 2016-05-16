package ru.ifmo.ctddev.podtelkin.mathlogic.annotation;

/**
 * Created by vlad107 on 04.05.16.
 */
public class Axiom implements Annotation {
    private final int index;

    public Axiom(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Сх. акс. " + Integer.toString(index);
    }
}
