package org.example;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import java.util.List;

public class CournotProblem extends AbstractProblem {
    private final double a; // Hệ số chặn của đường cầu
    private final double b; // Độ dốc của đường cầu
    private final List<Player> players;

    public CournotProblem(double a, double b, List<Player> players) {
        super(players.size(), players.size());
        this.a = a;
        this.b = b;
        this.players = players;
    }

    public static class Player {
        private String name;
        private Strategy strategy;
        private double marginalCost;

        public Player(String name, Strategy strategy, double marginalCost) {
            this.name = name;
            this.strategy = strategy;
            this.marginalCost = marginalCost;
        }

        public double decideQuantity(double opponentQuantity) {
            return strategy.decideQuantity(opponentQuantity);
        }
    }

    public interface Strategy {
        double decideQuantity(double opponentQuantity);
    }

    public static class FixedStrategy implements Strategy {
        private final double quantity;

        public FixedStrategy(double quantity) {
            this.quantity = quantity;
        }

        @Override
        public double decideQuantity(double opponentQuantity) {
            return quantity;
        }
    }

    public static class AdaptiveStrategy implements Strategy {
        @Override
        public double decideQuantity(double opponentQuantity) {
            return 100.0 - opponentQuantity;
        }
    }

    @Override
    public void evaluate(Solution solution) {
        double[] quantities = new double[players.size()];

        for (int i = 0; i < players.size(); i++) {
            double opponentQuantity = 0.0;
            for (int j = 0; j < players.size(); j++) {
                if (i != j) {
                    opponentQuantity += ((RealVariable)solution.getVariable(j)).getValue();
                }
            }
            quantities[i] = players.get(i).decideQuantity(opponentQuantity);
        }

        double totalQuantity = 0.0;
        for (double q : quantities) {
            totalQuantity += q;
        }

        double price = a - b * totalQuantity;

        for (int i = 0; i < players.size(); i++) {
            double profit = (price - players.get(i).marginalCost) * quantities[i];
            solution.setObjective(i, -profit); // maximize profit
        }
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(players.size(), players.size());
        for (int i = 0; i < players.size(); i++) {
            solution.setVariable(i, new RealVariable(0.0, 100.0));
        }
        return solution;
    }
}
