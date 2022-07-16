import java.util.Random;

public class App {
    public static void main(String[] args) throws Exception {
        double flammability = 0.15;
        double percentBlocked = 0.3;
        int numOfTrials = 10000;
        int size = 51;
        for (int i = 0; i < 1; i++) {
            double start = System.currentTimeMillis();
            AgentData(flammability, percentBlocked, numOfTrials, size);
            double now = System.currentTimeMillis();
            System.out.println((now - start) / 1000);
        }
    }

    public static void AgentData(double flammability, double percentBlocked, int numOfTrials, int size) {
        int[] AgentStats = testAgents(flammability, percentBlocked, numOfTrials, size);
        System.out.println("Agent One Success Rate: " + ((double) AgentStats[0]) /
                numOfTrials);
        System.out.println("Agent Two Success Rate: " + ((double) AgentStats[1]) /
                numOfTrials);
        System.out.println("Agent Three Success Rate: " + ((double) AgentStats[2]) /
                numOfTrials);
    }

    public static int[] testAgents(double flammability, double percentBlocked, int numOfTrials, int mazeSize) {
        int goalTwo = 0;
        int goalOne = 0;
        int goalThree = 0;
        Grid grid1;
        Grid grid2;
        Grid grid3;

        for (int i = 0; i < numOfTrials; i++) {
            grid1 = new Grid(mazeSize, percentBlocked, flammability, System.currentTimeMillis());
            grid2 = new Grid(grid1);
            grid3 = new Grid(grid2);
            goalOne += runAgentOneTrial(grid1);
            goalTwo += runAgentTwoTrial(grid2);
            goalThree += runAgentThreeTrial(grid3);
        }
        return new int[] { goalOne, goalTwo, goalThree };
    }

    public static int runAgentOneTrial(Grid grid) {
        AgentOne agent = new AgentOne(grid, 0, 0);
        AgentState status = AgentState.SAFE;
        while (true) {
            status = agent.stepAgent();
            if (status == AgentState.BURNING)
                break;
            if (status == AgentState.GOAL)
                return 1;
        }
        return 0;

    }

    public static int runAgentTwoTrial(Grid grid) {
        AgentTwo agent = new AgentTwo(grid, 0, 0);
        AgentState status = AgentState.SAFE;
        while (true) {
            status = agent.stepAgent();
            if (status == AgentState.BURNING)
                break;
            if (status == AgentState.GOAL)
                return 1;
            if (status == AgentState.NO_PATH)
                break;
        }
        return 0;
    }

    public static int runAgentThreeTrial(Grid grid) {
        AgentThree agent = new AgentThree(grid, 0, 0);
        AgentState status = AgentState.SAFE;
        while (true) {
            status = agent.stepAgent();
            if (status == AgentState.BURNING)
                break;
            if (status == AgentState.GOAL)
                return 1;
            if (status == AgentState.NO_PATH)
                break;
        }
        return 0;
    }
}
