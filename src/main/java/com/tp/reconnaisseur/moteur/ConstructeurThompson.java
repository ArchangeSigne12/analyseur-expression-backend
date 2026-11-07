package com.tp.reconnaisseur.moteur;

import java.util.*;

/** Construction de Thompson : expression postfixée → AFN avec ε-transitions. */
public final class ConstructeurThompson {

    private ConstructeurThompson() {
    }

    public static Automate construire(List<Character> postfixe) {
        List<Transition> transitions = new ArrayList<>();
        int[] compteur = {0};
        Deque<int[]> pile = new ArrayDeque<>(); // fragment = {debut, fin}

        for (char c : postfixe) {
            switch (c) {
                case '.' -> {
                    int[] f2 = pile.pop();
                    int[] f1 = pile.pop();
                    transitions.add(new Transition(f1[1], Transition.EPSILON, f2[0]));
                    pile.push(new int[]{f1[0], f2[1]});
                }
                case '|' -> {
                    int[] f2 = pile.pop();
                    int[] f1 = pile.pop();
                    int s = compteur[0]++;
                    int e = compteur[0]++;
                    transitions.add(new Transition(s, Transition.EPSILON, f1[0]));
                    transitions.add(new Transition(s, Transition.EPSILON, f2[0]));
                    transitions.add(new Transition(f1[1], Transition.EPSILON, e));
                    transitions.add(new Transition(f2[1], Transition.EPSILON, e));
                    pile.push(new int[]{s, e});
                }
                case '*' -> {
                    int[] f = pile.pop();
                    int s = compteur[0]++;
                    int e = compteur[0]++;
                    transitions.add(new Transition(s, Transition.EPSILON, f[0]));
                    transitions.add(new Transition(s, Transition.EPSILON, e));
                    transitions.add(new Transition(f[1], Transition.EPSILON, f[0]));
                    transitions.add(new Transition(f[1], Transition.EPSILON, e));
                    pile.push(new int[]{s, e});
                }
                case '+' -> {
                    int[] f = pile.pop();
                    int s = compteur[0]++;
                    int e = compteur[0]++;
                    transitions.add(new Transition(s, Transition.EPSILON, f[0]));
                    transitions.add(new Transition(f[1], Transition.EPSILON, f[0]));
                    transitions.add(new Transition(f[1], Transition.EPSILON, e));
                    pile.push(new int[]{s, e});
                }
                default -> {
                    int s = compteur[0]++;
                    int e = compteur[0]++;
                    transitions.add(new Transition(s, c, e));
                    pile.push(new int[]{s, e});
                }
            }
        }
        int[] f = pile.pop();
        return new Automate(compteur[0], f[0], Set.of(f[1]), transitions).renumeroteParParcours();
    }
}
