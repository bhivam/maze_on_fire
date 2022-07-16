import java.util.*;

public class GridTile {
    int costToEnter = 1;
    int accumulatedCost = 1;

    int x;
    int y;
    boolean blocked;
    boolean isBurning;
    boolean isGoingToBurn;

    double dist;
    double EstDistToGoal;

    // This is how the path from start to end node will be tracked
    GridTile prev;
    GridTile next;

    // Grid will generate all the GridTiles and but them into 2D
    public GridTile(int x, int y, boolean blocked) {
        this.x = x;
        this.y = y;
        this.blocked = blocked;
        if (blocked)
            costToEnter += 100000;
    }

    public GridTile(int x, int y, boolean blocked, boolean isBurning) {
        this.x = x;
        this.y = y;
        this.blocked = blocked;
        this.isBurning = isBurning;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GridTile other = (GridTile) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 51;
        int result = 1;
        result = prime * result + (blocked ? 1231 : 1237);
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

class CompareTile implements Comparator<GridTile> {

    @Override
    public int compare(GridTile o1, GridTile o2) {
        return (int) ((o1.dist + o1.EstDistToGoal) - (o2.dist + o2.EstDistToGoal) + 0.5);
    }
}