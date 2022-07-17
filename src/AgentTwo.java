import java.util.*;

public class AgentTwo {

    final int[][] neighborOffsets = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

    GridTile startPos;
    GridTile endPos;
    GridTile currentPos;
    Grid maze;
    boolean pathExists = true;

    public AgentTwo(Grid maze, int i, int j) {
        this.maze = maze;
        startPos = maze.grid[i][j];
        currentPos = startPos;
        endPos = maze.grid[maze.grid.length - 1][maze.grid.length - 1];
        for (int k = 0; k < maze.grid.length; k++)
            for (int l = 0; l < maze.grid.length; l++)
                maze.grid[i][j].EstDistToGoal = (maze.grid.length - 1) * 2 - i - j;
        findPath();
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < maze.grid.length && y >= 0 && y < maze.grid.length;
    }

    public void clearPreviousPath() {
        for (int i = 0; i < maze.grid.length; i++) {
            for (int j = 0; j < maze.grid.length; j++) {
                maze.grid[i][j].prev = null;
                maze.grid[i][j].next = null;
            }
        }
    }

    public boolean findPath() {
        clearPreviousPath();
        HashSet<GridTile> closedSet = new HashSet<GridTile>();

        PriorityQueue<GridTile> fringe = new PriorityQueue<GridTile>(1, new CompareTile());

        for (int i = 0; i < maze.grid.length; i++) {
            for (int j = 0; j < maze.grid.length; j++) {
                maze.grid[i][j].dist = (int) Math.pow(maze.grid.length, 3);
            }
        }

        currentPos.dist = 0;
        fringe.add(currentPos);

        GridTile v;
        double d;

        while (!fringe.isEmpty()) {
            v = fringe.poll();
            d = v.dist;

            int childX;
            int childY;
            for (int i = 0; i < neighborOffsets.length; i++) {
                childX = v.x + neighborOffsets[i][0];
                childY = v.y + neighborOffsets[i][1];

                if (isValid(childX, childY) && !maze.grid[childX][childY].isBurning
                        && !maze.grid[childX][childY].blocked
                        && !closedSet.contains(maze.grid[childX][childY])) {
                    if (d + 1 + maze.grid[childX][childY].EstDistToGoal < maze.grid[childX][childY].dist
                            + maze.grid[childX][childY].EstDistToGoal) {
                        GridTile child = maze.grid[childX][childY];
                        child.dist = d + 1;
                        fringe.add(child);
                        child.prev = v;
                    }
                }
            }
        }
        GridTile path = endPos;

        while (path != currentPos) {
            if (path.prev == null) {
                return false;
            }
            path.prev.next = path;
            path = path.prev;
        }
        return true;
    }

    public AgentState stepAgent() {
        // only stepping normal fire, otherwise same as Agent 3
        AgentState state;
        if (currentPos.equals(endPos))
            state = AgentState.GOAL;
        else if (currentPos.isBurning)
            state = AgentState.BURNING;
        else
            state = AgentState.SAFE;
        if (pathBurning())
            pathExists = findPath(); // replan if path is burning
        if (pathExists) {
            currentPos = currentPos.next;
            maze.stepFire();
        } else {
            state = AgentState.NO_PATH;
        }

        return state;
    }

    public boolean pathBurning() {
        // checking for burning path
        GridTile path = endPos;
        while (path != currentPos) {
            if (path.isBurning)
                return true;
            path = path.prev;
        }
        return false;
    }

    public void printMaze() {
        // printing maze for debugging
        findPath();
        HashSet<GridTile> fullPath = new HashSet<GridTile>();

        GridTile path = endPos;
        while (path != currentPos) {
            fullPath.add(path);
            path = path.prev;
        }

        for (int i = 0; i < maze.grid.length; i++) {
            for (int j = 0; j < maze.grid.length; j++) {
                if (maze.grid[i][j].isBurning)
                    System.out.print("f");
                else if (maze.grid[i][j].blocked)
                    System.out.print("#");
                else if (fullPath.contains(maze.grid[i][j]))
                    System.out.print("@");
                else
                    System.out.print(" ");
            }
            System.out.println();
        }

    }
}
