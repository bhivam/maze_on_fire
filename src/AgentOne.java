import java.util.*;

public class AgentOne {

    GridTile startPos;
    GridTile endPos;
    GridTile currentPos;
    Grid maze;

    public AgentOne(Grid maze, int i, int j) {
        this.maze = maze;
        startPos = maze.grid[i][j];
        currentPos = startPos;
        endPos = maze.grid[maze.grid.length - 1][maze.grid.length - 1];
        createPath();
    }

    public GridTile findPathBetween(int x1, int y1, int x2, int y2) {
        LinkedHashSet<GridTile> fringe1 = new LinkedHashSet<GridTile>();
        LinkedHashSet<GridTile> fringe2 = new LinkedHashSet<GridTile>();
        HashSet<GridTile> closed_set = new HashSet<GridTile>();
        GridTile current1;
        GridTile current2;

        GridTile[][] grid = maze.grid;

        fringe1.add(grid[x1][y1]);
        fringe2.add(grid[grid.length - 1][grid.length - 1]);

        current1 = fringe1.iterator().next();

        while (!fringe1.isEmpty() && !fringe2.isEmpty()) {

            current1 = fringe1.iterator().next();
            fringe1.remove(fringe1.iterator().next());
            current2 = fringe2.iterator().next();
            fringe2.remove(fringe2.iterator().next());

            if (fringe2.contains(current1))
                return current1;

            if (fringe1.contains(current2))
                return current2;

            if (current1.x > 0 && grid[current1.x - 1][current1.y].blocked == false
                    && !closed_set.contains(grid[current1.x - 1][current1.y])
                    && grid[current1.x - 1][current1.y].isBurning == false) {
                fringe1.add(grid[current1.x - 1][current1.y]);
                grid[current1.x - 1][current1.y].prev = current1;
            }
            if (current1.x < grid.length - 1 && grid[current1.x + 1][current1.y].blocked == false
                    && !closed_set.contains(grid[current1.x + 1][current1.y])
                    && grid[current1.x + 1][current1.y].isBurning == false) {
                fringe1.add(grid[current1.x + 1][current1.y]);
                grid[current1.x + 1][current1.y].prev = current1;
            }
            if (current1.y > 0 && grid[current1.x][current1.y - 1].blocked == false
                    && !closed_set.contains(grid[current1.x][current1.y - 1])
                    && grid[current1.x][current1.y - 1].isBurning == false) {
                fringe1.add(grid[current1.x][current1.y - 1]);
                grid[current1.x][current1.y - 1].prev = current1;
            }
            if (current1.y < grid.length - 1 && grid[current1.x][current1.y + 1].blocked == false
                    && !closed_set.contains(grid[current1.x][current1.y + 1])
                    && grid[current1.x][current1.y + 1].isBurning == false) {
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
        return current1;
    }

    public void createPath() {
        GridTile pathInfo = findPathBetween(startPos.x, startPos.y, maze.grid.length - 1, maze.grid.length - 1);
        while (!pathInfo.equals(startPos)) {
            pathInfo.prev.next = pathInfo;
            pathInfo = pathInfo.prev;
        }
    }

    public AgentState stepAgent() {
        if (currentPos == null)
            return AgentState.BURNING;
        if (currentPos.isBurning)
            return AgentState.BURNING;
        if (currentPos.equals(maze.grid[maze.grid.length - 1][maze.grid.length - 1]))
            return AgentState.GOAL;
        currentPos = currentPos.next;
        maze.stepFire();
        return AgentState.SAFE;

    }

    public void printMaze() {
        for (int i = 0; i < maze.grid.length; i++) {
            for (int j = 0; j < maze.grid.length; j++) {
                if (maze.grid[i][j].isBurning)
                    System.out.print("f");
                else if (maze.grid[i][j].blocked)
                    System.out.print("#");
                else if (maze.grid[i][j].equals(currentPos))
                    System.out.println("A");
                else
                    System.out.print(" ");
            }
            System.out.println();
        }
    }

}
