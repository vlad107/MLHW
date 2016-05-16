package ru.ifmo.ctddev.podtelkin.mathlogic.annotation;

/**
 * Created by vlad107 on 04.05.16.
 */
public class ExistsRule implements Annotation {
    private final int index;

    public ExistsRule(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "введение квантора существования " + index;

    }
}
