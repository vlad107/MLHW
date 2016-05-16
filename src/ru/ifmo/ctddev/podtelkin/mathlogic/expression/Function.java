package ru.ifmo.ctddev.podtelkin.mathlogic.expression;

import javafx.util.Pair;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.ArithmeticMatcherException;
import ru.ifmo.ctddev.podtelkin.mathlogic.exceptions.SubstitutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vlad107 on 04.05.16.
 */
public class Function extends AbstractExpression {
    private Variable var;
    private List<Expression> args;

    public Function(Variable var, List<Expression> args) {
        this.var = var;
        this.args = args;
    }


    @Override
    public boolean matchSchemaRec(Expression schema, Map<String, Integer> mapper) {
        if (schema instanceof Function) {
            Variable curVar = (Variable)((Function)schema).getVar();
            List<Expression> schArgs = ((Function)schema).getArgs();
            if (curVar.matchExpression(mapper, var.hashCode()) && (args.size() == schArgs.size())) {
                for (int i = 0; i < args.size(); i++) {
                    if (!args.get(i).matchSchemaRec(schArgs.get(i), mapper)) {
                        return false;
                    }
                }
            }
            return false;
        }
        if (schema instanceof Variable) {
            return ((Variable)schema).matchExpression(mapper, hashCode());
        }
        return false;
    }

    @Override
    public Pair<String, Expression> arithmeticMatcherRec(Expression expr, Set<String> bound) throws ArithmeticMatcherException {
        if (expr instanceof Function) {
            if ((this.args.size() == ((Function)expr).getArgs().size()) &&
              (this.var.toString().equals(((Function) expr).getVar().toString()))) {
                Pair<String, Expression> subExpr = null;
                for (int i = 0; i < args.size(); i++) {
                    Pair<String, Expression> curSubExpr2 = args.get(i).arithmeticMatcherRec(((Function) expr).getArgs().get(i), bound);
                    if (curSubExpr2 == null) continue;
                    if (subExpr == null) {
                        subExpr = curSubExpr2;
                        continue;
                    }
                    if (!subExpr.toString().equals(curSubExpr2.toString())) {
                        throw new ArithmeticMatcherException();
                    }
                }
                return subExpr;
            }
        }
        throw new ArithmeticMatcherException("Did not match");
    }

    @Override
    public Expression substituteAll(Map<String, Expression> substitutions, Set<String> bound) throws SubstitutionException {
        List<Expression> newArgs = new ArrayList<>();
        for (Expression arg : args) newArgs.add(arg.substituteAll(substitutions, bound));
        return new Function(var, newArgs);
    }

    @Override
    public void getFreeVariablesRec(Set<String> bound, Set<String> freeVariables, boolean isPropositional) {
        for (Expression expr : args) expr.getFreeVariablesRec(bound, freeVariables, isPropositional);
    }

    @Override
    public Expression clone() {
        List<Expression> newArgs = new ArrayList<>();
        for (Expression arg : args) newArgs.add(arg.clone());
        return new Function((Variable) var.clone(), newArgs);
    }

    @Override
    public void toStringRec(StringBuilder result) {
        var.toStringRec(result);
        if (!args.isEmpty()) {
            result.append("(");
            for (int i = 0; i < args.size(); i++) {
                args.get(i).toStringRec(result);
                if (i + 1 < args.size()) result.append(",");
            }
            result.append(")");
        }
    }

    public Expression getVar() {
        return var;
    }

    public List<Expression> getArgs() {
        return args;
    }
}
