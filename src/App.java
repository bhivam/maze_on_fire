public class App {
    public static void main(String[] args) throws Exception {
        Grid maze = new Grid(10, 0.3, 0.2);
        maze.printMaze();
        for (int i = 0; i < 5; i++) {
            System.out.println("----------------------------");
            maze.stepFire();
            maze.printMaze();
        }
    }
}
