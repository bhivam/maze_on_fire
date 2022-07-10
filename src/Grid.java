import java.util.*;

public class Grid {
    GridTile[][] grid;

    // This constructor populates the gird with a 2D array of a specified size where
    // each GridTile has a 30% chance of being blocked. If the maze is not
    // traversable a new mazes are created until a satisfactory maze is found.
    public Grid(int size, double percentBlocked) {
        if (size % 2 == 0)
            size++;

        do {
            grid = generateMaze(size, percentBlocked);
            grid[0][0].blocked = false;
            grid[0][size - 1].blocked = false;
            grid[size - 1][0].blocked = false;
            grid[size - 1][size - 1].blocked = false;
            grid[(size - 1) / 2][(size - 1) / 2].blocked = false;
        } while (!isMazeValid(size));
    }

    public boolean isMazeValid(int size) {
        if (findPathBetween(0, 0, (size - 1) / 2, (size - 1) / 2) == null
                || findPathBetween(0, size - 1, (size - 1) / 2, (size - 1) / 2) == null
                || findPathBetween(size - 1, 0, (size - 1) / 2, (size - 1) / 2) == null
                || findPathBetween(0, size - 1, (size - 1) / 2, (size - 1) / 2) == null)
            return false;

        grid[(size - 1) / 2][(size - 1) / 2].blocked = true;

        if (findPathBetween(0, 0, size - 1, size - 1) == null) {
            grid[(size - 1) / 2][(size - 1) / 2].blocked = false;
            return false;
        }

        grid[(size - 1) / 2][(size - 1) / 2].blocked = false;
        return true;
    }

    public GridTile[][] generateMaze(int size, double percentBlocked) {
        GridTile[][] newGrid = new GridTile[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                newGrid[i][j] = Math.random() < 0.3
                        ? newGrid[i][j] = new GridTile(i, j, true)
                        : new GridTile(i, j, false);
        return newGrid;
    }

    public GridTile findPathBetween(int x1, int y1, int x2, int y2) {
        LinkedHashSet<GridTile> fringe1 = new LinkedHashSet<GridTile>();
        LinkedHashSet<GridTile> fringe2 = new LinkedHashSet<GridTile>();
        HashSet<GridTile> closed_set = new HashSet<GridTile>();
        GridTile goal = grid[x2][y2];
        GridTile current1;
        GridTile current2;

        fringe1.add(grid[x1][y1]);
        fringe2.add(new GridTile(x2, y2, false));

        while (!fringe1.isEmpty() && !fringe2.isEmpty()) {

            current1 = fringe1.iterator().next();
            fringe1.remove(fringe1.iterator().next());
            current2 = fringe2.iterator().next();
            fringe2.remove(fringe2.iterator().next());

            if (fringe2.contains(current1)) {
                return current1;
            }

            if (fringe1.contains(current2)) {
                return current2;
            }

            if (current1.x > 0 && grid[current1.x - 1][current1.y].blocked == false
                    && !closed_set.contains(grid[current1.x - 1][current1.y])) {
                fringe1.add(grid[current1.x - 1][current1.y]);
                grid[current1.x - 1][current1.y].prev = current1;
            }
            if (current1.x < grid.length - 1 && grid[current1.x + 1][current1.y].blocked == false
                    && !closed_set.contains(grid[current1.x + 1][current1.y])) {
                fringe1.add(grid[current1.x + 1][current1.y]);
                grid[current1.x + 1][current1.y].prev = current1;

            }
            if (current1.y > 0 && grid[current1.x][current1.y - 1].blocked == false
                    && !closed_set.contains(grid[current1.x][current1.y - 1])) {
                fringe1.add(grid[current1.x][current1.y - 1]);
                grid[current1.x][current1.y - 1].prev = current1;

            }
            if (current1.y < grid.length - 1 && grid[current1.x][current1.y + 1].blocked == false
                    && !closed_set.contains(grid[current1.x][current1.y + 1])) {
                fringe1.add(grid[current1.x][current1.y + 1]);
                grid[current1.x][current1.y + 1].prev = current1;
            }

            closed_set.add(current1);

            if (current2.x > 0 && grid[current2.x - 1][current2.y].blocked == false
                    && !closed_set.contains(grid[current2.x - 1][current2.y])) {
                fringe2.add(grid[current2.x - 1][current2.y]);
                grid[current2.x - 1][current2.y].next = current2;
            }
            if (current2.x < grid.length - 1 && grid[current2.x + 1][current2.y].blocked == false
                    && !closed_set.contains(grid[current2.x + 1][current2.y])) {
                fringe2.add(grid[current2.x + 1][current2.y]);
                grid[current2.x + 1][current2.y].next = current2;

            }
            if (current2.y > 0 && grid[current2.x][current2.y - 1].blocked == false
                    && !closed_set.contains(grid[current2.x][current2.y - 1])) {
                fringe2.add(grid[current2.x][current2.y - 1]);
                grid[current2.x][current2.y - 1].next = current2;

            }
            if (current2.y < grid.length - 1 && grid[current2.x][current2.y + 1].blocked == false
                    && !closed_set.contains(grid[current2.x][current2.y + 1])) {
                fringe2.add(grid[current2.x][current2.y + 1]);
                grid[current2.x][current2.y + 1].next = current2;
            }

            closed_set.add(current2);

        }

        return null;

    }

    public void printMaze() {
        GridTile sol = findPathBetween(0, 0, grid.length - 1, grid.length - 1);

        HashSet<GridTile> path = new HashSet<GridTile>();
        GridTile t = sol;

        while (t.prev != null) {
            path.add(t);
            System.out.println("X: " + t.x + " Y: " + t.y);
            t = t.prev;
        }

        t = sol;

        while (t.next != null) {
            path.add(t);
            System.out.println("X: " + t.x + " Y: " + t.y);
            t = t.next;
        }

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j].blocked == true)
                    System.out.print("#");
                else if (path.contains(grid[i][j]))
                    System.out.print("a");
                else
                    System.out.print(" ");
            }
            System.out.println();
        }

    }
}