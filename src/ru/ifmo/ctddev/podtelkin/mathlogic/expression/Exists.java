package ru.ifmo.ctddev.podtelkin.mathlogic.expression;

import javafx.util.Pair;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ArithmeticMatcherException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.SubstitutionException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vlad107 on 04.05.16.
 */
public class Exists extends AbstractExpression {
    Variable var;
    Expression expr;

    public Exists(Variable var, Expression expr) {
        this.var = var;
        this.expr = expr;
    }

    @Override
    public boolean matchSchemaRec(Expression schema, Map<String, Integer> mapper) {
        if (schema instanceof Predicate) {
            Variable var = (Variable) ((Predicate) schema).getVar();
            List<Expression> args = ((Predicate) schema).getArgs();
            return args.isEmpty() && var.matchExpression(mapper, hashCode());
        }
        return false;
    }

    @Override
    public Pair<String, Expression> arithmeticMatcherRec(Expression expr, Set<String> bound) throws ArithmeticMatcherException {
        if ((expr instanceof Exists) && (var.hashCode() == ((Exists)expr).getVar().hashCode())) {
            boolean wasBound = !bound.add(var.toString());
            Pair<String, Expression>  subExpr;
            try {
                subExpr = this.expr.arithmeticMatcherRec(((Exists) expr).getExpr(), bound);
            } catch (ArithmeticMatcherException e) {
                if (!wasBound) bound.remove(var.toString());
                throw e;
            }
            if (!wasBound) bound.remove(var.toString());
            return subExpr;
        }
        throw new ArithmeticMatcherException("Did not match");
    }

    @Override
    public Expression substituteAll(Map<String, Expression> substitutions, Set<String> bound) throws SubstitutionException {
        boolean wasBound = !bound.add(var.toString());
        Expression newExpr = expr.substituteAll(substitutions, bound);
        if (!wasBound) bound.remove(var.toString());
        return new Exists(var, newExpr);
    }

    @Override
    public void getFreeVariablesRec(Set<String> bound, Set<String> freeVariables, boolean isPropositional) {
        boolean wasBound = !bound.add(var.toString());
        expr.getFreeVariablesRec(bound, freeVariables, isPropositional);
        if (!wasBound) bound.remove(var.toString());
    }

    @Override
    public Expression clone() {
        return new Exists((Variable) var.clone(), expr.clone());
    }

    @Override
    public void toStringRec(StringBuilder result) {
        result.append("(?");
        var.toStringRec(result);
        result.append("(");
        expr.toStringRec(result);
        result.append("))");
    }

    public Expression getVar() {
        return var;
    }

    public Expression getExpr() {
        return expr;
    }
}
