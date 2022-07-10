public class App {
    public static void main(String[] args) throws Exception {
        int counter = 0;
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 50; i++) {
                Grid maze = new Grid(153, 0.3);
                if (maze.grid[152][152].blocked == true)
                    counter++;
            }

        }
        System.out.println(counter / 20.);
    }
}
