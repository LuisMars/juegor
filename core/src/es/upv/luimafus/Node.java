package es.upv.luimafus;

/**
 http://web.mit.edu/eranki/www/tutorials/search/
 */
public class Node {
    Node parent;
    public int x, y;
    public int cost, prevCost, guessCost;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Node(int x, int y, int cost) {
        this(x, y);
        this.cost = cost;
    }

    public Node(int x, int y, Node parent, int gX, int gY) {
        this(x,y);
        this.parent = parent;

        prevCost = parent.prevCost+1;
        guessCost = Utils.distance(x, y, gX, gY);
        cost = prevCost + guessCost;
    }

    public boolean hasSamePosition(Node n) {
        return x == n.x && y == n.y;
    }

    public boolean isWorseThan(Node n) {
        return n.cost < cost;
    }

    public boolean equals(Object o) {
        if (o instanceof Node) {
            Node n = (Node) o;
            return n.x == x && n.y == y;
        } else {
            return false;
        }
    }
}
