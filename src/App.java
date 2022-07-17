import java.util.Random;

public class App {
    // for testing and running the environment
    public static void main(String[] args) throws Exception {
        double flammability = 0.5;
        double percentBlocked = 0.3;
        int numOfTrials = 10000;
        int size = 51;
        for (int i = 0; i < 1; i++) {
            double start = System.currentTimeMillis();
            AgentData(flammability, percentBlocked, numOfTrials, size);
            double now = System.currentTimeMillis();
            System.out.println("flammability: " + flammability);
            System.out.println((now - start) / 1000);
        }
    }

    // for printing out the data and running the testing function
    public static void AgentData(double flammability, double percentBlocked, int numOfTrials, int size) {
        int[] AgentStats = testAgents(flammability, percentBlocked, numOfTrials, size);
        System.out.println("Agent One Success Rate: " + ((double) AgentStats[0]) /
                numOfTrials);
        System.out.println("Agent Two Success Rate: " + ((double) AgentStats[1]) /
                numOfTrials);
        System.out.println("Agent Three Success Rate: " + ((double) AgentStats[2]) /
                numOfTrials);
        System.out.println("Agent Four Success Rate: " + ((double) AgentStats[3]) /
                numOfTrials);
    }

    // This sets up a new graph, makes three copies of it and assigns each agent a
    // graph.
    // The agents will return 1 for a success and 0 for a failure.
    public static int[] testAgents(double flammability, double percentBlocked, int numOfTrials, int mazeSize) {
        int goalTwo = 0;
        int goalOne = 0;
        int goalThree = 0;
        int goalFour = 0;
        Grid grid1;
        Grid grid2;
        Grid grid3;
        Grid grid4;

        for (int i = 0; i < numOfTrials; i++) {
            grid1 = new Grid(mazeSize, percentBlocked, flammability, System.currentTimeMillis(),
                    System.currentTimeMillis() + 1);
            grid2 = new Grid(grid1);
            grid3 = new Grid(grid2);
            grid4 = new Grid(grid3);
            goalOne += runAgentOneTrial(grid1);
            goalTwo += runAgentTwoTrial(grid2);
            goalThree += runAgentThreeTrial(grid3);
            goalFour += runAgentFourTrial(grid4);
        }
        return new int[] { goalOne, goalTwo, goalThree, goalFour };
    }

    // These functions are for driving the individual agents. There is no NO_PATH
    // condition for agent one since you're guaranteed to find a path on the first
    // try.

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

    public static int runAgentFourTrial(Grid grid) {
        AgentFour agent = new AgentFour(grid, 0, 0);
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
