package ru.ifmo.ctddev.podtelkin.mathlogic.expression;

import javafx.util.Pair;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ArithmeticMatcherException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.SubstitutionException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by vlad107 on 04.05.16.
 */
public abstract class AbstractExpression implements Expression {
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        toStringRec(result);
        return result.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean matchSchema(Expression schema) {
        Map<String, Integer> mapper = new HashMap<>();
        return matchSchemaRec(schema, mapper);
    }

    @Override
    public Pair<String, Expression> arithmeticMatcher(String var, Expression expr) throws ArithmeticMatcherException {
        Set<String> bound = getFreeVariables(false);
        bound.remove(var);
        return arithmeticMatcherRec(expr, bound);
    }

    @Override
    public Expression substituteVariable(String var, Expression expr) throws SubstitutionException {
        Map<String, Expression> substitutions = new HashMap<>();
        substitutions.put(var, expr);
        Set<String> bound = new HashSet<>();
        return substituteAll(substitutions, bound);
    }

    @Override
    public boolean isFreeVariable(String var) {
        return getFreeVariables(false).contains(var);
    }

    @Override
    public Set<String> getFreeVariables(boolean isPropositional) {
        Set<String> bound = new HashSet<>();
        Set<String> freeVariables = new HashSet<>();
        getFreeVariablesRec(bound, freeVariables, isPropositional);
        return freeVariables;
    }

    @Override
    public abstract Expression clone();
}
