package ru.ifmo.ctddev.podtelkin.mathlogic.expression;

import javafx.util.Pair;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ArithmeticMatcherException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.SubstitutionException;

import java.util.Map;
import java.util.Set;

/**
 * Created by vlad107 on 04.05.16.
 */
public class Mult extends AbstractExpression {
    private Expression A;
    private Expression B;

    public Mult(Expression A, Expression B) {
        this.A = A;
        this.B = B;
    }


    @Override
    public boolean matchSchemaRec(Expression schema, Map<String, Integer> mapper) {
        if (schema instanceof Mult) {
            return A.matchSchemaRec(((Mult)schema).getA(), mapper) &&
                    B.matchSchemaRec(((Mult)schema).getB(), mapper);
        }
        if (schema instanceof Variable) {
            return ((Variable)schema).matchExpression(mapper, hashCode());
        }
        return false;

    }
    @Override
    public Pair<String, Expression> arithmeticMatcherRec(Expression expr, Set<String> bound) throws ArithmeticMatcherException {
        if (expr instanceof Mult) {
            Pair<String, Expression>  u = A.arithmeticMatcherRec(((Mult)expr).getA(), bound);
            Pair<String, Expression> v = B.arithmeticMatcherRec(((Mult)expr).getB(), bound);
            if ((u != null) && (v != null) && (u.hashCode() != v.hashCode())) {
                throw new ArithmeticMatcherException("Several matches to one variable");
            }
            return (u == null ? v : u);
        }
        throw new ArithmeticMatcherException("Did not match");
    }

    @Override
    public Expression substituteAll(Map<String, Expression> substitutions, Set<String> bound) throws SubstitutionException {
        return new Mult(A.substituteAll(substitutions, bound), B.substituteAll(substitutions, bound));
    }

    @Override
    public void getFreeVariablesRec(Set<String> bound, Set<String> freeVariables, boolean isPropositional) {
        A.getFreeVariablesRec(bound, freeVariables, isPropositional);
        B.getFreeVariablesRec(bound, freeVariables, isPropositional);
    }

    @Override
    public void toStringRec(StringBuilder result) {
        result.append("(");
        A.toStringRec(result);
        result.append("*");
        B.toStringRec(result);
        result.append(")");
    }

    @Override
    public Expression clone() {
        return new Mult(A.clone(), B.clone());
    }

    public Expression getA() {
        return A;
    }

    public Expression getB() {
        return B;
    }
}
