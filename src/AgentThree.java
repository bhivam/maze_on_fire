import java.util.*;

public class AgentThree {

    final int[][] neighborOffsets = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

    GridTile startPos;
    GridTile endPos;
    GridTile currentPos;
    Grid maze;
    Grid firePredictionMaze;

    public AgentThree(Grid maze, int i, int j) {
        this.maze = maze;
        startPos = maze.grid[i][j];
        currentPos = startPos;
        endPos = maze.grid[maze.grid.length - 1][maze.grid.length - 1];
        for (int k = 0; k < maze.grid.length; k++)
            for (int l = 0; l < maze.grid.length; l++)
                maze.grid[i][j].EstDistToGoal = (maze.grid.length - 1) * 2 - i - j;
        firePredictionMaze = new Grid(maze);
        firePredictionMaze.stepFire();
        firePredictionMaze.stepFire();
        firePredictionMaze.stepFire();
        findPath();
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < maze.grid.length && y >= 0 && y < maze.grid.length;
    }

    public void findPath() {
        HashSet<GridTile> closedSet = new HashSet<GridTile>();
        HashMap<GridTile, GridTile> prev = new HashMap<GridTile, GridTile>();

        PriorityQueue<GridTile> fringe = new PriorityQueue<GridTile>(1, new CompareTile());

        for (int i = 0; i < maze.grid.length; i++) {
            for (int j = 0; j < maze.grid.length; j++) {
                maze.grid[i][j].dist = (int) Math.pow(maze.grid.length, 3);
            }
        }

        currentPos.dist = 0;
        prev.put(currentPos, currentPos);
        fringe.add(currentPos);

        GridTile v;
        int d;

        while (!fringe.isEmpty()) {
            v = fringe.poll();
            d = v.dist;

            if (!closedSet.contains(v)) {
                int childX;
                int childY;
                for (int i = 0; i < neighborOffsets.length; i++) {
                    childX = v.x + neighborOffsets[i][0];
                    childY = v.y + neighborOffsets[i][1];

                    if (isValid(childX, childY) && !maze.grid[childX][childY].isBurning
                            && !maze.grid[childX][childY].blocked
                            && !firePredictionMaze.grid[childX][childY].isBurning) {
                        if (d + 1 < maze.grid[childX][childY].dist) {
                            GridTile child = maze.grid[childX][childY];
                            child.dist = d + 1;
                            if (fringe.contains(child))
                                fringe.remove(child);
                            fringe.add(child);
                            child.prev = v;
                        }
                    }
                }
                closedSet.add(v);
            }

        }
        GridTile path = endPos;

        while (path != currentPos) {
            if (path.prev == null) {
                System.out.println("uhoh");
            }
            path.prev.next = path;
            path = path.prev;
        }

    }

    public AgentState stepAgent() {
        AgentState state;
        if (currentPos.equals(endPos))
            state = AgentState.GOAL;
        else if (currentPos.isBurning)
            state = AgentState.BURNING;
        else
            state = AgentState.SAFE;
        if (pathBurning())
            findPath();
        currentPos = currentPos.next;
        maze.stepFire();
        firePredictionMaze.stepFire();

        return state;
    }

    public boolean pathBurning() {
        GridTile path = endPos;
        while (path != currentPos) {
            if (path.isBurning)
                return true;
            path = path.prev;
        }
        return false;
    }

    public void printMaze() {
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
