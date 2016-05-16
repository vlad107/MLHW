package ru.ifmo.ctddev.podtelkin.mathlogic.expression;

import javafx.util.Pair;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ArithmeticMatcherException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.SubstitutionException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vlad107 on 04.05.16.
 */
public class Negative extends AbstractExpression {
    private Expression A;

    public Negative(Expression A) {
        this.A = A;
    }


    @Override
    public boolean matchSchemaRec(Expression schema, Map<String, Integer> mapper) {
        if (schema instanceof Negative) {
            return A.matchSchemaRec(((Negative)schema).getA(), mapper);
        }
        if (schema instanceof Predicate) {
            Variable var = (Variable) ((Predicate) schema).getVar();
            List<Expression> args = ((Predicate) schema).getArgs();
            return args.isEmpty() && var.matchExpression(mapper, hashCode());
        }
        return false;
    }

    @Override
    public Pair<String, Expression> arithmeticMatcherRec(Expression expr, Set<String> bound) throws ArithmeticMatcherException {
        if (expr instanceof Negative) {
            return A.arithmeticMatcherRec(((Negative)expr).getA(), bound);
        }
        throw new ArithmeticMatcherException("Did not match");
    }

    @Override
    public void getFreeVariablesRec(Set<String> bound, Set<String> freeVariables, boolean isPropositional) {
        A.getFreeVariablesRec(bound, freeVariables, isPropositional);
    }

    @Override
    public Expression substituteAll(Map<String, Expression> substitutions, Set<String> bound) throws SubstitutionException {
        return new Negative(A.substituteAll(substitutions, bound));
    }


    @Override
    public void toStringRec(StringBuilder result) {
        result.append("(!");
        A.toStringRec(result);
        result.append(")");
    }

    @Override
    public Expression clone() {
        return new Negative(A.clone());
    }

    public Expression getA() {
        return A;
    }
}
