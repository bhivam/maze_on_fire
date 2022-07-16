import java.util.*;

import java.util.*;

public class AgentFour {

    final int[][] neighborOffsets = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

    GridTile startPos;
    GridTile endPos;
    GridTile currentPos;
    Grid maze;
    boolean pathExists = true;

    HashSet<GridTile> visited = new HashSet<GridTile>();
    HashSet<GridTile> path = new HashSet<GridTile>();

    int numVisited = 0;
    int numPath = 0;

    public AgentFour(Grid maze, int i, int j) {
        this.maze = maze;
        startPos = maze.grid[i][j];
        currentPos = startPos;
        endPos = maze.grid[maze.grid.length - 1][maze.grid.length - 1];
        for (int k = 0; k < maze.grid.length; k++)
            for (int l = 0; l < maze.grid.length; l++)
                maze.grid[i][j].EstDistToGoal = (maze.grid.length - 1) * 2 - i - j;
        // Math.pow((Math.pow((maze.grid.length - 1 - i), 2)
        // + Math.pow((maze.grid.length - 1 - i), 2)), 0.5);
        findPath();
    }

    public void clearPreviousPath() {
        for (int i = 0; i < maze.grid.length; i++) {
            for (int j = 0; j < maze.grid.length; j++) {
                maze.grid[i][j].prev = null;
                maze.grid[i][j].next = null;
            }
        }
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < maze.grid.length && y >= 0 && y < maze.grid.length;
    }

    public boolean findPath() {
        // clearPreviousPath();
        HashSet<GridTile> closedSet = new HashSet<GridTile>();
        HashMap<GridTile, GridTile> prev = new HashMap<GridTile, GridTile>();

        PriorityQueue<GridTile> fringe = new PriorityQueue<GridTile>(11, new Comparator<GridTile>() {
            @Override
            public int compare(GridTile o1, GridTile o2) {
                return (int) ((o1.accumulatedCost + o1.EstDistToGoal) - (o2.accumulatedCost + o2.EstDistToGoal) + 0.5);
            }
        });

        for (int i = 0; i < maze.grid.length; i++) {
            for (int j = 0; j < maze.grid.length; j++) {
                maze.grid[i][j].accumulatedCost = 99999;
            }
        }

        currentPos.accumulatedCost = 1;
        prev.put(currentPos, currentPos);
        fringe.add(currentPos);

        GridTile v;
        while (!fringe.isEmpty()) {
            v = fringe.poll();
            int childX;
            int childY;
            for (int i = 0; i < neighborOffsets.length; i++) {
                childX = v.x + neighborOffsets[i][0];
                childY = v.y + neighborOffsets[i][1];

                if (isValid(childX, childY)) {
                    if (v.accumulatedCost
                            + maze.grid[childX][childY].costToEnter < maze.grid[childX][childY].accumulatedCost
                                    + maze.grid[childX][childY].EstDistToGoal) {
                        GridTile child = maze.grid[childX][childY];
                        child.accumulatedCost = v.accumulatedCost
                                + maze.grid[childX][childY].costToEnter;
                        fringe.add(child);
                        numVisited++;
                        child.prev = v;
                    }
                }
            }
        }
        GridTile path = endPos;

        while (path != currentPos) {
            if (path.prev == null) {
                numPath = 0;
                numVisited = 0;
                return false;
            }
            path.prev.next = path;
            path = path.prev;
            numPath++;
        }
        return true;
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
            pathExists = findPath();
        if (pathExists) {
            currentPos = currentPos.next;
            maze.stepFire();
            maze.stepPredictedFire();
        } else {
            state = AgentState.NO_PATH;
        }

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