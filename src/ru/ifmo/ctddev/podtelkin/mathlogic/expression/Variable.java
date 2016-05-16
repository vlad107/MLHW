package ru.ifmo.ctddev.podtelkin.mathlogic.expression;

import javafx.util.Pair;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ArithmeticMatcherException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.SubstitutionException;

import java.util.Map;
import java.util.Set;

/**
 * Created by vlad107 on 04.05.16.
 */
public class Variable extends AbstractExpression {
    private final String varName;

    public Variable(String varName) {
        this.varName = varName;
    }

    @Override
    public void toStringRec(StringBuilder result) {
        result.append(varName);
    }

    @Override
    public boolean matchSchemaRec(Expression schema, Map<String, Integer> mapper) {
        if (schema instanceof Variable) {
            return ((Variable)schema).matchExpression(mapper, hashCode());
        }
        return false;
    }

    @Override
    public Pair<String, Expression> arithmeticMatcherRec(Expression expr, Set<String> bound) throws ArithmeticMatcherException {
        if (!bound.contains(varName)) return new Pair<>(varName, expr);
        if ((expr instanceof Variable) && (expr.toString().equals(varName))) return null;
        throw new ArithmeticMatcherException("Did not match");
    }

    @Override
    public Expression substituteAll(Map<String, Expression> substitutions, Set<String> bound) throws SubstitutionException {
        if ((substitutions.containsKey(varName)) && (!bound.contains(varName))) {
            Expression subExpr = substitutions.get(varName);
            Set<String> freeVars = subExpr.getFreeVariables(false);
            for (String curVar : bound) {
                if (freeVars.contains(curVar)) {
                    throw new SubstitutionException(subExpr.toString(), varName);
                }
            }
            return subExpr.clone();
        }
        return new Variable(varName);
    }

    @Override
    public Expression clone() {
        return new Variable(varName);
    }


    @Override
    public void getFreeVariablesRec(Set<String> bound, Set<String> freeVariables, boolean isPropositional) {
        if (!bound.contains(varName)) freeVariables.add(varName);
    }

    public boolean matchExpression(Map<String, Integer> mapper, Integer hashCode) {
        if (matchExpressionWithoutInsert(mapper, hashCode)) {
            mapper.put(varName, hashCode);
            return true;
        }
        return false;
    }

    public boolean matchExpressionWithoutInsert(Map<String, Integer> mapper, Integer hashCode) {
        return !mapper.containsKey(varName) || mapper.get(varName).equals(hashCode);
    }
}
