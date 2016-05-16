package ru.ifmo.ctddev.podtelkin.mathlogic.expression;

import javafx.util.Pair;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ArithmeticMatcherException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.SubstitutionException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;
import java.util.Set;

/**
 * Created by vlad107 on 04.05.16.
 */
public class Zero extends AbstractExpression {
    @Override
    public void toStringRec(StringBuilder result) {
        result.append("0");
    }

    @Override
    public boolean matchSchemaRec(Expression schema, Map<String, Integer> mapper) throws NotImplementedException {
        if (schema instanceof Zero) return true;
        if (schema instanceof Variable) {
            return ((Variable)schema).matchExpression(mapper, hashCode());
        }
        return false;
    }

    @Override
    public void getFreeVariablesRec(Set<String> bound, Set<String> freeVariables, boolean isPropositional) {
    }

    @Override
    public Expression clone() {
        return new Zero();
    }

    @Override
    public Expression substituteAll(Map<String, Expression> substitutions, Set<String> bound) throws SubstitutionException {
        return new Zero();
    }

    @Override
    public Pair<String, Expression> arithmeticMatcherRec(Expression expr, Set<String> bound) throws ArithmeticMatcherException {
        if (expr instanceof Zero) return null;
        throw new ArithmeticMatcherException("Did not match");
    }
}
