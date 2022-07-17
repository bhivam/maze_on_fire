import java.util.*;

public class Grid {
    GridTile[][] grid; // contains all the tiles in the maze.
    double flammability; // determines how quickly tiles surrounding burning tiles will catch fire.
    HashSet<GridTile> fireFringe;
    long randomSeed; // seed to create random variables
    long randomSeed2;
    Random rand;
    Random rand2; // for predicted maze comparison between agent 3 and 4
    // if you use the same one it will desync from mazes one and two for the normal
    // fire.

    public Grid(int size, double percentBlocked, double flammability, long randomSeed, long randomSeed2) {
        // instantiating variables.
        this.randomSeed = randomSeed;
        this.randomSeed2 = randomSeed2;
        this.flammability = flammability;
        this.rand = new Random(randomSeed);
        this.rand2 = new Random(randomSeed2);
        if (size % 2 == 0) // size has to be odd for there to be a middle to put fire.
            size++;

        do { // code for generating maze. making sure corners and middle are unblocked and
             // then if the
             // maze is valid the while loop will exit.
            grid = generateMaze(size, percentBlocked);
            grid[0][0].blocked = false;
            grid[0][size - 1].blocked = false;
            grid[size - 1][0].blocked = false;
            grid[size - 1][size - 1].blocked = false;
            grid[(size - 1) / 2][(size - 1) / 2].blocked = false;
        } while (!isMazeValid(size));
        grid[(size - 1) / 2][(size - 1) / 2].isBurning = true; // setting middle tile on fire.

        fireFringe = new HashSet<GridTile>(); // create fire fringe to hold all tiles on the verge of burning
        addNonBurningNeighbors((size - 1) / 2, (size - 1) / 2, fireFringe); // add the non burning neighbors to the
                                                                            // fringe.
    }

    public Grid(Grid copy) {
        // constructor for making a deep copy of a grid
        // getting all instance variables from the grid being copied.
        this.randomSeed = copy.randomSeed;
        this.flammability = copy.flammability;
        this.rand = new Random(randomSeed);
        this.rand2 = new Random(randomSeed2);
        int size = copy.grid.length;

        grid = new GridTile[size][size];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                grid[i][j] = new GridTile(i, j, copy.grid[i][j].blocked);
            }
        }
        grid[(size - 1) / 2][(size - 1) / 2].isBurning = true; // setting middle to fire.

        fireFringe = new HashSet<GridTile>(); // contains tiles about to catch fire.
        addNonBurningNeighbors((size - 1) / 2, (size - 1) / 2, fireFringe);// adding neighbors of center burning
                                                                           // element.
    }

    public boolean isMazeValid(int size) { // checking path between corners and center
        if (findPathBetween(0, 0, (size - 1) / 2, (size - 1) / 2) == null
                || findPathBetween(0, size - 1, (size - 1) / 2, (size - 1) / 2) == null
                || findPathBetween(size - 1, 0, (size - 1) / 2, (size - 1) / 2) == null
                || findPathBetween(0, size - 1, (size - 1) / 2, (size - 1) / 2) == null)
            return false;

        grid[(size - 1) / 2][(size - 1) / 2].blocked = true; // blocking center since fire will eventually be there.

        if (findPathBetween(0, 0, size - 1, size - 1) == null) {
            grid[(size - 1) / 2][(size - 1) / 2].blocked = false;
            return false; // checking for path from top right to bottom left corners.
        }

        grid[(size - 1) / 2][(size - 1) / 2].blocked = false; // putting the center back to unblocked.
        return true;
    }

    public GridTile[][] generateMaze(int size, double percentBlocked) { // populates maze with gridtiles.
        GridTile[][] newGrid = new GridTile[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                newGrid[i][j] = Math.random() < percentBlocked
                        ? newGrid[i][j] = new GridTile(i, j, true)
                        : new GridTile(i, j, false);

        return newGrid;
    }

    public void stepFire() {
        Stack<GridTile> currentFireFringe = new Stack<GridTile>();
        currentFireFringe.addAll(fireFringe);// adding all elements to a stack
        // doing this because you only want to act on the elements currently available
        while (!currentFireFringe.isEmpty()) {
            GridTile current = currentFireFringe.pop(); // popping off the last one.
            if (rand.nextDouble(1) < (1 - Math.pow(1 - flammability, burningNeighbors(current.x, current.y)))) {
                // making check to see if it is going to catch fire
                grid[current.x][current.y].isBurning = true; // burning
                grid[current.x][current.y].costToEnter += 100000; // cost of burning tile for agent 4
                addNonBurningNeighbors(current.x, current.y, fireFringe);
                // get the non burning neighbors to put into the firefringe.
                fireFringe.remove(current); // remove the current one from the real fringe so it is not redone.
            }
        }
    }

    public void stepPredictedFire() {
        // effectively the same function but does this for the predicted fire.
        for (int i = 0; i < grid.length; i++) { // resetting predictive burn
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j].isGoingToBurn)
                    grid[i][j].costToEnter -= 50; // removing the cost to enter due to prediction of burning
                grid[i][j].isGoingToBurn = false;
            }
        }
        Stack<GridTile> currentFireFringe = new Stack<GridTile>();
        HashSet<GridTile> predictedFireFringe = new HashSet<>();
        predictedFireFringe.addAll(fireFringe);

        // using the current fringe without changing it since predicted fire is
        // disconnected
        // from the real fire in every way except starting conditions.

        for (int i = 0; i < 3; i++) {
            currentFireFringe.addAll(predictedFireFringe);
            while (!currentFireFringe.isEmpty()) {
                GridTile current = currentFireFringe.pop();
                if (rand2.nextDouble(
                        1) < (1 - Math.pow(1 - flammability, futureBurningNeighbors(current.x, current.y)))) {
                    grid[current.x][current.y].isGoingToBurn = true;
                    grid[current.x][current.y].costToEnter += 50;
                    addFutureNonBurningNeighbors(current.x, current.y, predictedFireFringe);
                    predictedFireFringe.remove(current);
                }
            }
        }
    }

    public void addFutureNonBurningNeighbors(int x, int y, HashSet<GridTile> fringe) {
        // prediction version of fucntion
        if (x > 0 && !grid[x - 1][y].isBurning && !grid[x - 1][y].blocked)
            fringe.add(grid[x - 1][y]);
        if (x < grid.length - 1 && !grid[x + 1][y].isBurning && !grid[x + 1][y].blocked)
            fringe.add(grid[x + 1][y]);
        if (y > 0 && !grid[x][y - 1].isBurning && !grid[x][y - 1].blocked)
            fringe.add(grid[x][y - 1]);
        if (y < grid.length - 1 && !grid[x][y + 1].isBurning && !grid[x][y + 1].blocked)
            fringe.add(grid[x][y + 1]);
    }

    public void addNonBurningNeighbors(int x, int y, HashSet<GridTile> fringe) {
        // checks all non burning neighbors and adds them to the fringe.
        if (x > 0 && !grid[x - 1][y].isBurning && !grid[x - 1][y].blocked)
            fringe.add(grid[x - 1][y]);
        if (x < grid.length - 1 && !grid[x + 1][y].isBurning && !grid[x + 1][y].blocked)
            fringe.add(grid[x + 1][y]);
        if (y > 0 && !grid[x][y - 1].isBurning && !grid[x][y - 1].blocked)
            fringe.add(grid[x][y - 1]);
        if (y < grid.length - 1 && !grid[x][y + 1].isBurning && !grid[x][y + 1].blocked)
            fringe.add(grid[x][y + 1]);
    }

    public int futureBurningNeighbors(int x, int y) {
        // predictive version of function.
        int count = 0;
        if (x > 0 && (grid[x - 1][y].isBurning || grid[x - 1][y].isGoingToBurn))
            count++;
        if (x < grid.length - 1 && (grid[x + 1][y].isBurning || grid[x + 1][y].isGoingToBurn))
            count++;
        if (y > 0 && (grid[x][y - 1].isBurning || grid[x][y - 1].isGoingToBurn))
            count++;
        if (y < grid.length - 1 && (grid[x][y + 1].isBurning || grid[x][y + 1].isGoingToBurn))
            count++;
        return count;
    }

    public int burningNeighbors(int x, int y) {
        int count = 0;
        // giving the number of burning neighbors for the check to see if a tile will
        // burn.
        if (x > 0 && grid[x - 1][y].isBurning)
            count++;
        if (x < grid.length - 1 && grid[x + 1][y].isBurning)
            count++;
        if (y > 0 && grid[x][y - 1].isBurning)
            count++;
        if (y < grid.length - 1 && grid[x][y + 1].isBurning)
            count++;
        return count;
    }

    public GridTile findPathBetween(int x1, int y1, int x2, int y2) {
        // bi directional bfs algorithm.
        LinkedHashSet<GridTile> fringe1 = new LinkedHashSet<GridTile>();
        LinkedHashSet<GridTile> fringe2 = new LinkedHashSet<GridTile>();
        // linked hash sets for good performance while also maintaining FIFO
        HashSet<GridTile> closed_set = new HashSet<GridTile>();
        GridTile current1;
        GridTile current2;

        fringe1.add(grid[x1][y1]);
        fringe2.add(grid[grid.length - 1][grid.length - 1]);

        while (!fringe1.isEmpty() && !fringe2.isEmpty()) {

            // this is how to remove the oldest element. Iterator is in FIFO ordering
            current1 = fringe1.iterator().next();
            fringe1.remove(fringe1.iterator().next());
            current2 = fringe2.iterator().next();
            fringe2.remove(fringe2.iterator().next());

            // checking for intersection of trees
            if (fringe2.contains(current1))
                return current1;

            if (fringe1.contains(current2))
                return current2;

            // first tree making checks
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
            // second tree making checks on what to add and not add
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

}