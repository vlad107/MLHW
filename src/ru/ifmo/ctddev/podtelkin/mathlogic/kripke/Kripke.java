package ru.ifmo.ctddev.podtelkin.mathlogic.kripke;

import com.sun.deploy.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad107 on 10.05.16.
 */
public class Kripke {
    int n;
    ArrayList<ArrayList<Integer>> graph;
    ArrayList<ArrayList<String>> variables;
    ArrayList<Integer> varMasks;

    public Kripke(int mask) {
        n = 1;
        graph = new ArrayList<>(0);
        graph.add(new ArrayList<>());
        varMasks = new ArrayList<>();
        varMasks.add(mask);
    }

    public ArrayList<Integer> genChilds(int curV) {
        ArrayList<Integer> childs = new ArrayList<>();
        genSubTree(curV, childs);
        return childs;
    }

    private void genSubTree(int x, ArrayList<Integer> list) {
        for (int i : graph.get(x)) genSubTree(i, list);
        list.add(x);
    }

    public void fillVariables(List<String> varList) {
        variables = new ArrayList<>();
        for (int i = 0; i < n; i++) variables.add(new ArrayList<>());
        fillVariablesRec(0, varList);
    }

    private void fillVariablesRec(int x, List<String> varList) {
        for (int i = 0; i < varList.size(); i++) {
            if ((varMasks.get(x) & (1<<i)) != 0) {
                variables.get(x).add(varList.get(i));
            }
        }
        for (int child : graph.get(x)) fillVariablesRec(child, varList);
    }

    public String toString() {
        List<String> strList = new ArrayList<>();
        goRec(0, " ", strList);
        return StringUtils.join(strList, "\n");
    }

    void goRec(int cur, String tab, List<String> strList) {
        String str = tab + Integer.toString(cur) + " ";
        str += StringUtils.join(variables.get(cur), ", ");
        strList.add(str);
        for (int child : graph.get(cur)) goRec(child, tab + "    ", strList);
    }
}
