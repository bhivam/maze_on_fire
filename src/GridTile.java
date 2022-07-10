public class GridTile {
    int x;
    int y;
    boolean blocked;
    boolean isBurning;

    // This is how the path from start to end node will be tracked
    GridTile prev;
    GridTile next;

    // Grid will generate all the GridTiles and but them into 2D
    public GridTile(int x, int y, boolean blocked) {
        this.x = x;
        this.y = y;
        this.blocked = blocked;
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