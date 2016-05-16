package ru.ifmo.ctddev.podtelkin.mathlogic.annotation;

/**
 * Created by vlad107 on 04.05.16.
 */
public class ForAllRule implements Annotation {
    private final int index;

    public ForAllRule(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "введение квантора всеобщности " + Integer.toString(index);
    }

    public int getIndex() {
        return index;
    }
}
