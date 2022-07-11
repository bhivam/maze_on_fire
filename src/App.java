public class App {
    public static void main(String[] args) throws Exception {
        AgentOneData();
    }

    public static void AgentOneData() {
        int burned = 0;
        int goal = 0;
        double goalRate = 0;
        int numOfTrials = 1000000;
        int numOfTests = 1;
        for (int i = 0; i < numOfTests; i++) {
            int[] AgentOneStats = testAgentOne(0.05, 0.3, numOfTrials, 10);
            burned = AgentOneStats[0];
            goal = AgentOneStats[1];
            System.out.println(
                    "Agent one burned " + AgentOneStats[0] + " times and reached the goal " + AgentOneStats[1]
                            + " times.");
            goalRate += ((double) goal) / numOfTrials;
        }
        System.out.println(goalRate / numOfTests);
    }

    public static int[] testAgentOne(double flammability, double percentBlocked, int numOfTrials, int mazeSize) {
        AgentOne naiveAgent;
        AgentState status;
        int burning = 0;
        int goal = 0;
        for (int i = 0; i < numOfTrials; i++) {
            naiveAgent = new AgentOne(new Grid(mazeSize, percentBlocked, flammability), 0, 0);
            status = AgentState.SAFE;
            while (true) {
                status = naiveAgent.stepAgent();
                if (status == AgentState.BURNING) {
                    burning++;
                    break;
                }
                if (status == AgentState.GOAL) {
                    goal++;
                    break;
                }
            }
        }
        return new int[] { burning, goal };
    }
}
