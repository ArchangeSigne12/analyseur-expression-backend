package com.tp.reconnaisseur.moteur;

import java.util.*;

/** Construction par sous-ensembles : AFN (ε-transitions) → AFD partiel (sans état puits). */
public final class ConstructeurAfd {

    private ConstructeurAfd() {
    }

    public static Automate depuis(Automate afn, Set<Character> alphabet) {
        Map<Integer, List<Transition>> sortantes = new HashMap<>();
        for (Transition t : afn.transitions()) {
            sortantes.computeIfAbsent(t.de(), k -> new ArrayList<>()).add(t);
        }
        List<Character> symboles = new ArrayList<>(new TreeSet<>(alphabet));

        List<Set<Integer>> sousEnsembles = new ArrayList<>();
        Map<Set<Integer>, Integer> index = new HashMap<>();
        List<Transition> transitions = new ArrayList<>();

        Set<Integer> depart = fermeture(sortantes, Set.of(afn.initial()));
        sousEnsembles.add(depart);
        index.put(depart, 0);

        for (int i = 0; i < sousEnsembles.size(); i++) {
            Set<Integer> courant = sousEnsembles.get(i);
            for (char c : symboles) {
                Set<Integer> cible = new TreeSet<>();
                for (int etat : courant) {
                    for (Transition t : sortantes.getOrDefault(etat, List.of())) {
                        if (t.symbole() == c) {
                            cible.add(t.vers());
                        }
                    }
                }
                if (cible.isEmpty()) {
                    continue;
                }
                cible = fermeture(sortantes, cible);
                Integer j = index.get(cible);
                if (j == null) {
                    j = sousEnsembles.size();
                    sousEnsembles.add(cible);
                    index.put(cible, j);
                }
                transitions.add(new Transition(i, c, j));
            }
        }
        Set<Integer> finaux = new TreeSet<>();
        for (int i = 0; i < sousEnsembles.size(); i++) {
            for (int f : afn.finaux()) {
                if (sousEnsembles.get(i).contains(f)) {
                    finaux.add(i);
                }
            }
        }
        return new Automate(sousEnsembles.size(), 0, finaux, transitions);
    }

    static Set<Integer> fermeture(Map<Integer, List<Transition>> sortantes, Set<Integer> etats) {
        Set<Integer> resultat = new TreeSet<>(etats);
        Deque<Integer> pile = new ArrayDeque<>(etats);
        while (!pile.isEmpty()) {
            int e = pile.pop();
            for (Transition t : sortantes.getOrDefault(e, List.of())) {
                if (t.estEpsilon() && resultat.add(t.vers())) {
                    pile.push(t.vers());
                }
            }
        }
        return resultat;
    }
}
