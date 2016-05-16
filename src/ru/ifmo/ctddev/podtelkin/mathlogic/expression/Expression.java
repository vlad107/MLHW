package ru.ifmo.ctddev.podtelkin.mathlogic.expression;

import javafx.util.Pair;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ArithmeticMatcherException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.SubstitutionException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;
import java.util.Set;

public interface Expression {
    void toStringRec(StringBuilder result);
    boolean matchSchemaRec(Expression schema, Map<String, Integer> mapper) throws NotImplementedException;
    Pair<String, Expression> arithmeticMatcherRec(Expression expr, Set<String> bound) throws ArithmeticMatcherException;
    Expression substituteAll(Map<String, Expression> substitutions, Set<String> bound) throws SubstitutionException;
    Set<String> getFreeVariables(boolean isPropositional);
    boolean isFreeVariable(String var);

    String toString();
    int hashCode();
    boolean matchSchema(Expression schema);
    Pair<String, Expression> arithmeticMatcher(String var, Expression expr) throws ArithmeticMatcherException;
    Expression substituteVariable(String var, Expression expr) throws SubstitutionException;
    void getFreeVariablesRec(Set<String> bound, Set<String> freeVariables, boolean isPropositional);
    Expression clone();
}
