package ru.ifmo.ctddev.podtelkin.mathlogic.kripke;

import ru.ifmo.ctddev.podtelkin.mathlogic.expression.*;

import java.util.*;

/**
 * Created by vlad107 on 10.05.16.
 *
 */
public class KripkeBuilder {
    private Map<String, Integer> variables;
    private final Expression expr;
    private final int MAX_NUM_VERTEX = 8;
    private int nVariables;

    public KripkeBuilder(Expression expr) {
        this.expr = expr;
    }

    public Kripke tryToBuild() {
        Set<String> varSet = expr.getFreeVariables(true);
        List<String> varList = new ArrayList<>(varSet);
        variables = new HashMap<>();
        nVariables = varList.size();
        for (int i = 0; i < nVariables; i++) variables.put(varList.get(i), i);
        for (int mask = 0; mask < (1<<nVariables); mask++) {
            Kripke curWorld = new Kripke(mask);
            if (genWorld(curWorld)) {
                curWorld.fillVariables(varList);
                return curWorld;
            }
        }
        return null;
    }

    boolean genWorld(Kripke curWorld) {
        if (!isProvable(0, curWorld, expr)) return true;
        if (curWorld.n > MAX_NUM_VERTEX) return false;
        int n = curWorld.n;
        for (int i = 0; i < n; i++) {
            int prevMask = curWorld.varMasks.get(i);
            for (int newMask = (1<<nVariables) - 1; newMask >= prevMask; newMask--) {
                if ((newMask & prevMask) == prevMask) {
                    curWorld.graph.add(new ArrayList<>());
                    curWorld.graph.get(i).add(n);
                    curWorld.varMasks.add(newMask);
                    curWorld.n++;
                    if (genWorld(curWorld)) return true;
                    curWorld.graph.remove(curWorld.graph.size() - 1);
                    curWorld.graph.get(i).remove(curWorld.graph.get(i).size() - 1);
                    curWorld.varMasks.remove(curWorld.varMasks.size() - 1);
                    curWorld.n--;
                }
            }
        }
        return false;
    }

    boolean isProvable(int curV, Kripke world, Expression expr) {
        if (expr instanceof Conjunction) {
            return isProvable(curV, world, ((Conjunction) expr).getA()) &&
                    isProvable(curV, world, ((Conjunction) expr).getB());
        } else if (expr instanceof Disjunction) {
            return isProvable(curV, world, ((Disjunction) expr).getA()) ||
                    isProvable(curV, world, ((Disjunction) expr).getB());
        } else if (expr instanceof Implication) {
            Expression A = ((Implication) expr).getA();
            Expression B = ((Implication) expr).getB();
            ArrayList<Integer> childs = world.genChilds(curV);
            for (int child : childs) {
                if (isProvable(child, world, A) && !isProvable(child, world, B)) {
                    return false;
                }
            }
            return true;
        } else if (expr instanceof Negative) {
            Expression A = ((Negative) expr).getA();
            ArrayList<Integer> childs = world.genChilds(curV);
            for (int child : childs) {
                if (isProvable(child, world, A)) return false;
            }
            return true;
        } else if (expr instanceof Predicate) {
            String name = expr.toString();
            return (world.varMasks.get(curV)&(1<<variables.get(name))) != 0;
        }
        return false;
    }
}
