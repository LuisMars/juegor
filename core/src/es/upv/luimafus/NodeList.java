package es.upv.luimafus;

import java.util.ArrayList;

/**
 * Created by Luis on 20/11/2014.
 */
public class NodeList extends ArrayList<Node> {

    public boolean add(Node node) {
        if(isEmpty()) {
            return super.add(node);
        }
        else if (get(0).cost > node.cost) {
            add(0,node);
        }
        else if (get(size()-1).cost < node.cost) {
            add(size(), node);
        }
        else {
            int i = 0;
            while (get(i).cost < node.cost) i++;
            add(i, node);
        }
        return true;
    }


    public Node getFirst() {
        return get(0);
    }

}
