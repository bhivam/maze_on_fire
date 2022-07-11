public class App {
    public static void main(String[] args) throws Exception {
        AgentOne naiveAgent = new AgentOne(new Grid(51, 0.3, 0.05), 0, 0);
        AgentState status = AgentState.SAFE;
        while (true) {
            status = naiveAgent.stepAgent();
            if (status == AgentState.BURNING) {
                System.out.println("BURNING");
                break;
            }
            if (status == AgentState.GOAL) {
                System.out.println("GOAL");
                break;
            }
            if (status == AgentState.SAFE)
                System.out.println("SAFE");
        }
    }
}
