import java.util.*;

public class AgentFour {

    final int[][] neighborOffsets = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
    // to make iterating through child nodes easy

    GridTile startPos;
    GridTile endPos;
    GridTile currentPos;
    Grid maze;
    boolean pathExists = true;

    HashSet<GridTile> visited = new HashSet<GridTile>();
    HashSet<GridTile> path = new HashSet<GridTile>();

    int numVisited = 0; // used in debugging
    int numPath = 0;

    public AgentFour(Grid maze, int i, int j) {
        // for instantiating variables and finding the distances.
        this.maze = maze;
        startPos = maze.grid[i][j];
        currentPos = startPos;
        endPos = maze.grid[maze.grid.length - 1][maze.grid.length - 1];
        for (int k = 0; k < maze.grid.length; k++)
            for (int l = 0; l < maze.grid.length; l++)
                maze.grid[i][j].EstDistToGoal = (maze.grid.length - 1) * 2 - i - j;
        findPath();
    }

    public void clearPreviousPath() {
        // clearing all pointers on grid.
        for (int i = 0; i < maze.grid.length; i++) {
            for (int j = 0; j < maze.grid.length; j++) {
                maze.grid[i][j].prev = null;
                maze.grid[i][j].next = null;
            }
        }
    }

    public boolean isValid(int x, int y) {
        // checking coordinate for grid bounds
        return x >= 0 && x < maze.grid.length && y >= 0 && y < maze.grid.length;
    }

    public boolean findPath() {
        clearPreviousPath();
        HashMap<GridTile, GridTile> prev = new HashMap<GridTile, GridTile>();

        PriorityQueue<GridTile> fringe = new PriorityQueue<GridTile>(11, new Comparator<GridTile>() {
            @Override
            public int compare(GridTile o1, GridTile o2) {
                return (int) ((o1.accumulatedCost + o1.EstDistToGoal) - (o2.accumulatedCost + o2.EstDistToGoal) + 0.5);
            }
        });

        for (int i = 0; i < maze.grid.length; i++) {
            for (int j = 0; j < maze.grid.length; j++) {
                maze.grid[i][j].accumulatedCost = 99999; // setting the initial cost of other nodes.
            }
        }

        // initial conditions to run the search
        currentPos.accumulatedCost = 0;
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

                if (isValid(childX, childY)) { // only checking for validity
                    if (v.accumulatedCost
                            + maze.grid[childX][childY].costToEnter < maze.grid[childX][childY].accumulatedCost
                                    + maze.grid[childX][childY].EstDistToGoal) {
                        // only exploring elements which have a shorter cost through the current
                        // element.
                        GridTile child = maze.grid[childX][childY];
                        child.accumulatedCost = v.accumulatedCost
                                + maze.grid[childX][childY].costToEnter;
                        // setting the new cost
                        fringe.add(child);
                        numVisited++; // debugging purposes
                        child.prev = v;
                    }
                }
            }
        }
        GridTile path = endPos;

        while (path != currentPos) { // checking for null nodes and creating path for stepAgent function
            if (path.prev == null) {
                numPath = 0; // debugging
                numVisited = 0; // debugging
                return false;
            }
            path.prev.next = path;
            path = path.prev;
            numPath++;
        }
        return true;
    }

    public AgentState stepAgent() {
        // same as agent 3
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
        // check to see if path is burning
        GridTile path = endPos;
        while (path != currentPos) {
            if (path.isBurning)
                return true;
            path = path.prev;
        }
        return false;
    }

    public void printMaze() {
        // maze printing for debugging purposes.
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