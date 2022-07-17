import java.util.*;

public class AgentThree {

    // makes searching through neighboring tile offsets much easier
    final int[][] neighborOffsets = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

    GridTile startPos;
    GridTile endPos;
    GridTile currentPos;
    Grid maze;
    boolean pathExists = true; // path will exist by default since that is a criteria of a valid maze

    public AgentThree(Grid maze, int i, int j) {
        // setting up the instance variables to get ready for stepping the agent
        this.maze = maze;
        this.startPos = maze.grid[i][j];
        this.currentPos = startPos;
        this.endPos = maze.grid[maze.grid.length - 1][maze.grid.length - 1];
        // calculating the minimum distance from a given node to the goal
        for (int k = 0; k < maze.grid.length; k++)
            for (int l = 0; l < maze.grid.length; l++)
                maze.grid[i][j].EstDistToGoal = (maze.grid.length - 1) * 2 - i - j;
        // doing the initial findPath
        findPath();
    }

    public void clearPreviousPath() {
        // To get rid of any remnants of the old search that can interfere
        // with the new one. Creates many bugs if this isn't here.
        for (int i = 0; i < maze.grid.length; i++) {
            for (int j = 0; j < maze.grid.length; j++) {
                maze.grid[i][j].prev = null;
                maze.grid[i][j].next = null;
            }
        }
    }

    public boolean isValid(int x, int y) {
        // checks if a coordinate is in the bounds of the grid.
        return x >= 0 && x < maze.grid.length && y >= 0 && y < maze.grid.length;
    }

    public boolean findPath() {
        clearPreviousPath(); // cleaning up the maze
        // setting up data structures
        HashSet<GridTile> closedSet = new HashSet<GridTile>(); // has a O(1) contains method
        PriorityQueue<GridTile> fringe = new PriorityQueue<GridTile>(1, new CompareTile());
        // priority queue for A*, has a custom comparator from GridTile file.

        // Setting up a distance which is too high to get naturally
        for (int i = 0; i < maze.grid.length; i++) {
            for (int j = 0; j < maze.grid.length; j++) {
                maze.grid[i][j].dist = (int) Math.pow(maze.grid.length, 3);
            }
        }
        // the Current position should be at a distance 0 to start off the serach
        // correctly
        currentPos.dist = 0;
        fringe.add(currentPos); // adding to fringe so while loop can start

        GridTile v; // node
        double d; // distance of node

        while (!fringe.isEmpty()) {
            v = fringe.poll(); // popping off fringe
            d = v.dist;

            if (!closedSet.contains(v)) {
                int childX;
                int childY;
                for (int i = 0; i < neighborOffsets.length; i++) { // getting child nodes
                    childX = v.x + neighborOffsets[i][0];
                    childY = v.y + neighborOffsets[i][1];

                    if (isValid(childX, childY) && !maze.grid[childX][childY].isBurning
                            && !maze.grid[childX][childY].isGoingToBurn && !maze.grid[childX][childY].blocked) {
                        // whole if statement short circuits if child is not valid. Then checking
                        // if it is a restricted state or not.
                        if (d + 1 + maze.grid[childX][childY].EstDistToGoal < maze.grid[childX][childY].dist
                                + maze.grid[childX][childY].EstDistToGoal) {
                            // checking if the distance through the current node is smaller than the
                            // distance that the child node already has.

                            GridTile child = maze.grid[childX][childY];
                            child.dist = d + 1; // if it is set the distance.
                            fringe.add(child); // load it onto the fringe
                            child.prev = v; // and record the path
                        }
                    }
                }
                closedSet.add(v); // make sure you don't expand this again
            }

        }
        // this is to check if the path exists or not
        GridTile path = endPos;

        while (path != currentPos) {
            if (path.prev == null) {
                return findPathNormally();
                // will do the same thing except will not check for predicted fires
            }
            path.prev.next = path;
            path = path.prev;
        }
        return true;
    }

    public boolean findPathNormally() {
        // refer to above comments.
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

            if (!closedSet.contains(v)) {
                int childX;
                int childY;
                for (int i = 0; i < neighborOffsets.length; i++) {
                    childX = v.x + neighborOffsets[i][0];
                    childY = v.y + neighborOffsets[i][1];

                    if (isValid(childX, childY)) {
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
                return false;
            }
            path.prev.next = path;
            path = path.prev;
        }
        return true;
    }

    public AgentState stepAgent() {
        // this is the function which drives the class interaction with the maze.
        AgentState state;
        if (currentPos.equals(endPos))
            state = AgentState.GOAL;
        else if (currentPos.isBurning)
            state = AgentState.BURNING;
        else
            state = AgentState.SAFE;
        if (pathBurning()) // makes sure that the path is not on fire.
            pathExists = findPath();
        if (pathExists) {
            currentPos = currentPos.next;
            maze.stepFire(); // stepping predicted and real fire.
            maze.stepPredictedFire();
        } else {
            state = AgentState.NO_PATH; // path doesn't exist you have to return that there was no path
        }

        return state;
    }

    // to check if the path is on fire
    public boolean pathBurning() {
        GridTile path = endPos;
        while (path != currentPos) {
            if (path.isBurning)
                return true;
            path = path.prev;
        }
        return false;
    }

    // to visualize maze for debugging
    public void printMaze() {
        // adding path to a hash set and then iterating through the whole maze
        // printing symbols based on the characteristic of the tile and whether it is
        // in the path.
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
