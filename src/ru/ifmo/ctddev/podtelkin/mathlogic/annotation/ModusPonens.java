package ru.ifmo.ctddev.podtelkin.mathlogic.annotation;

/**
 * Created by vlad107 on 04.05.16.
 */
public class ModusPonens implements Annotation {
    private final int alphaIndex;
    private final int gammaIndex;

    public ModusPonens(int alphaIndex, int gammaIndex) {
        this.alphaIndex = alphaIndex;
        this.gammaIndex = gammaIndex;
    }

    @Override
    public String toString() {
        return "M.P. " + Integer.toString(gammaIndex) + " " + Integer.toString(alphaIndex);
    }

    public int getAlphaIndex() {
        return alphaIndex;
    }

    public int getGammaIndex() {
        return gammaIndex;
    }
}
