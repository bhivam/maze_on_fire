public class Environment {
    public static void main(String[] args) {

    }

    /*
     * This function generates an 51x51 2D array with values
     * 0 (unblocked) and 1 (blocked). 30% of the entries will
     * be blocked and the rest unblocked.
     */
    public int[][] generate_maze() {
        int[][] new_arr = new int[51][51];
        for (int i = 0; i < 51; i++)
            for (int j = 0; j < 51; j++)
                if (Math.random() < 0.3)
                    new_arr[i][j] = 1;
        return new_arr;
    }

    /*
     * To check the validity of the paths you must use a search
     * algorithm to check the paths from various start and end
     * points.
     */

    public boolean check_valid_maze(int[][] maze) {

        return false;
    }

    /*
     * Checks if there is a valid path between two points in a
     * maze.
     */
    public boolean is_path(int[][] maze, int x1, int y1, int x2, int y2) {

        return false;
    }

}
