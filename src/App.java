public class App {
    public static void main(String[] args) throws Exception {
        AgentOneData();
    }

    public static void AgentOneData() {
        int numOfTrials = 1000;
        int[] AgentOneStats = testAgentOne(0.05, 0.3, numOfTrials, 51);
        System.out.println(((double) AgentOneStats[1]) / numOfTrials);
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
