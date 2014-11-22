package es.upv.luimafus;


/**
 * Created by Luis on 20/11/2014.
 */
public class AStar {
    public NodeList open;
    public NodeList closed;
    public NodeList successors;
    private Map GameMap;
    public AStar(Map GameMap) {
        this.GameMap = GameMap;
    }

    public Node calcPath(Player a, Player b) {
        Node q = null;
        open.add(new Node(a.getX(),a.getY(),0));
        while(!open.isEmpty()) {
            q = open.getFirst();
            if(q.x == b.getX() && q.y == b.getY())
                return q;
            open.remove(q);
            closed.add(q);

            successors.clear();
            if(GameMap.isFloor(q.x-1,q.y))
                successors.add(new Node(q.x-1,q.y,q,b));
            if(GameMap.isFloor(q.x+1,q.y))
                successors.add(new Node(q.x+1,q.y,q,b));
            if(GameMap.isFloor(q.x,q.y+1))
                successors.add(new Node(q.x,q.y+1,q,b));
            if(GameMap.isFloor(q.x,q.y-1))
                successors.add(new Node(q.x,q.y-1,q,b));

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

    public int getNext(Player a, Player b) {

        open = new NodeList();
        closed = new NodeList();
        successors = new NodeList();

        int dir = -1;
        Node q = calcPath(a,b);
        Node p = q;
        if(p != null) {
            while (p.parent != null) {

                q = p;
                p = p.parent;

                //System.out.print(" (" +q.x + "," +q.y+")<-");
            }
            //System.out.println();
            dir = Utils.posToDir(a.getX(),a.getY(),q.x,q.y);


        }
        return dir;
    }
}
