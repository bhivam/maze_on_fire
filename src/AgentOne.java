import java.util.HashSet;

enum AgentState {
    BURNING,
    SAFE,
    GOAL
}

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

    public void createPath() {
        GridTile pathInfo = maze.findPathBetween(startPos.x, startPos.y, maze.grid.length - 1, maze.grid.length - 1);
        while (!pathInfo.equals(startPos)) {
            pathInfo.prev.next = pathInfo;
            pathInfo = pathInfo.prev;
        }
    }

    public AgentState stepAgent() {
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
