package org.example;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.Executor;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import java.util.Arrays;
import org.example.CournotProblem.Player;
import org.example.CournotProblem.FixedStrategy;
import org.example.CournotProblem.AdaptiveStrategy;

public class Main {
    public static void main(String[] args) {
        // Định nghĩa người chơi với chiến lược và chi phí biên
        Player player1 = new Player("Firm 1", new FixedStrategy(30.0), 10.0);
        Player player2 = new Player("Firm 2", new AdaptiveStrategy(), 20.0);

        // Khởi tạo bài toán Cournot với các tham số cầu và người chơi
        CournotProblem problem = new CournotProblem(100.0, 1.0, Arrays.asList(player1, player2));

        // Thực thi thuật toán tối ưu hóa (e.g., NSGA-II)
        NondominatedPopulation result = new Executor()
                .withProblem(problem)
                .withAlgorithm("NSGAII")
                .withMaxEvaluations(10000)
                .run();

        // Tìm giải pháp tốt nhất dựa trên tổng lợi nhuận cao nhất
        Solution bestSolution = null;
        double bestTotalProfit = Double.NEGATIVE_INFINITY;

        for (Solution solution : result) {
            double totalProfit = 0.0;
            for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
                totalProfit += -solution.getObjective(i);
            }

            if (totalProfit > bestTotalProfit) {
                bestTotalProfit = totalProfit;
                bestSolution = solution;
            }
        }

        // Hiển thị giải pháp tốt nhất
        if (bestSolution != null) {
            System.out.println("Best Quantities: ");
            for (int i = 0; i < bestSolution.getNumberOfVariables(); i++) {
                System.out.print(((RealVariable)bestSolution.getVariable(i)).getValue() + " ");
            }
            System.out.println("\nBest Profits: ");
            for (int i = 0; i < bestSolution.getNumberOfObjectives(); i++) {
                System.out.print(-bestSolution.getObjective(i) + " ");
            }
            System.out.println("\nTotal Profit: " + bestTotalProfit);
        }
    }
}
