package es.upv.luimafus;


/**
 * Created by Luis on 20/11/2014.
 */
public class AStar {
    public NodeList open;
    public NodeList closed;
    public NodeList successors;
    public int startX, startY, goalX, goalY;

    private Map GameMap;

    public AStar(Map GameMap) {
        this.GameMap = GameMap;
        open = new NodeList();
        closed = new NodeList();
        successors = new NodeList();
    }


    public Node calcPath(Player a, Player b) {
        return calcPath(a.getX(),a.getY(),b.getX(),b.getY());
    }



    public Node calcPath(int x, int y, int gX, int gY) {
        open.clear();
        closed.clear();
        successors.clear();
        Node q = null;
        open.add(new Node(x,y,0));
        while(!open.isEmpty()) {
            q = open.getFirst();
            if(q.x == gX && q.y == gY)
                return q;
            open.remove(q);
            closed.add(q);

            successors.clear();
            if(GameMap.isFloor(q.x-1,q.y))
                successors.add(new Node(q.x-1,q.y,q,gX,gY));
            if(GameMap.isFloor(q.x+1,q.y))
                successors.add(new Node(q.x+1,q.y,q,gX,gY));
            if(GameMap.isFloor(q.x,q.y+1))
                successors.add(new Node(q.x,q.y+1,q,gX,gY));
            if(GameMap.isFloor(q.x,q.y-1))
                successors.add(new Node(q.x,q.y-1,q,gX,gY));

            for(Node s : successors) {
                boolean isBetter;
                if (closed.contains(s))
                    continue;
                if(!open.contains(s)){
                    open.add(s);
                    isBetter = true;
                }
                //else if(open.get(open.indexOf(s)).isWorseThan(s))

                else isBetter = s.isWorseThan(q);

                if(!isBetter)
                    s.parent = null;
            }
            closed.add(q);
        }
        return null;
    }

    public boolean hasPath(Player a, Player b) {
        //open = new NodeList();
        //closed = new NodeList();
        //successors = new NodeList();
        return calcPath(a,b) != null;

    }

    public Node getPath(int x, int y, int gX, int gY) {
        return calcPath(x,y,gX,gY);
    }

    public int getNext(Player a, Player b) {

        int dir = -1;
        Node q = calcPath(a,b);
        Node p = q;
        if(p != null) {
            while (p.parent != null) {

                q = p;
                p = p.parent;
            }
            dir = Utils.posToDir(a.getX(), a.getY(), q.x, q.y);


        }
        return dir;
    }
}
