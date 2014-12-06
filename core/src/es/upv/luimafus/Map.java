package es.upv.luimafus;


import com.badlogic.gdx.math.Rectangle;

import java.util.*;


public class Map {
    public int[][] drawMap;
    public Player humanPlayer;
    public long seed;
    private int[][] map;
    private List<Player> players = new ArrayList<Player>();
    private Collection<Attack> attacks = new ArrayList<Attack>();
    private GameScreen gs;

    public Map() {

    }

    public Map(GameScreen s, int[][] map) {
        Player.n_players = 0;
        gs = s;
        this.map = map;
        prepareMap();


    }

    public Map(GameScreen s,int h, int w, double density) {
        Player.n_players = 0;
        gs = s;
        map = new int[h][w];
        generateMap(density);
        prepareMap();
        //seed = (long)(Math.random()*Long.MAX_VALUE);

    }

    public void generateMap(double density) {
        List<Rectangle> rooms = new ArrayList<>();
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                map[j][i] = 0;
            }
        }
        double area = getHeight() * getWidth() * density;
        area = Math.min(area, (getHeight() * getWidth()) - (getHeight() + getWidth()) * 2 + 4);
        double covered = 0;
        Random random = new Random(/*seed*/);
        while (area > covered) {

            int iy = 1+(int)(random.nextDouble() * (getWidth()-1));
            int fy = 1+(int)(random.nextDouble() * (getWidth()-1));
            if (fy < iy) {
                int foo = fy;
                fy = iy;
                iy = foo;
            }

            int ix = 1+(int)(random.nextDouble() * (getHeight()-1));
            int fx = 1+(int)(random.nextDouble() * (getHeight()-1));
            if (fx < ix) {
                int foo = fx;
                fx = ix;
                ix = foo;
            }
            //TODO: i < f
            Rectangle test = new Rectangle(ix,iy,fx-ix,fy-iy);

            if(test.area() > area/8)
                continue;


            boolean overlaps = false;
            for(Rectangle r : rooms)
                if(r.overlaps(test)) {
                    overlaps = true;
                    break;
                }
            if(overlaps)
                continue;
            else
                rooms.add(new Rectangle(test));

            for (int i = ix; i < fx; i++) {
                for (int j = iy; j < fy; j++) {
                    map[j][i] = 1;
                }
            }
            covered = 0;
            for (int i = 0; i < getHeight(); i++) {
                for (int j = 0; j < getWidth(); j++) {
                    if(map[j][i] == 1)
                        covered++;
                }
            }
        }
        //add corridors
        for(Rectangle r : rooms) {
            AStar aStar = new AStar(this);
            Rectangle next;
            //do {
                //next = rooms.get(MathUtils.random(0, rooms.size() - 1));
                next = rooms.get((rooms.indexOf(r)+1)%rooms.size());
            //} while (next.equals(r));
            Node q = aStar.getPath((int) r.getX(), (int) r.getY(), (int) next.getX(), (int) next.getY());

            if(q != null) {
                do {
                    if(q.x > 1 && q.y > 1 && q.x < getWidth() && q.y < getHeight())
                        map[q.x][q.y] = 1;
                    q = q.parent;
                } while (q != null);

            }
        }

        //invert cells
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                map[j][i] = map[j][i] == 1 ? 0 : 1;
            }
        }
    }

    public void prepareMap() {
        drawMap = new int[getWidth()][getHeight()];

        for(int i = 0; i < getWidth(); i++)
            for(int j = 0; j < getHeight(); j++)
                drawMap[i][j] = gs.assets.floor.getIndex(Utils.getTile(map, i, j));


    }

    public void addPlayer(Player p) {
        players.add(p);
        humanPlayer = players.iterator().next();
    }

    public void addAttack(Attack a) {
        attacks.add(a);
    }

    public void updateState() {
        Collections.sort(players);
        for (Player p : players) {
            p.updateArea();
            if(p.isBot())
                p.botMove(closestTo(p));
            else
                p.act();
        }
        players.removeIf(Player::isDead);
        attacks.removeIf(Attack::isOver);
    }
    public void updateAttacks() {
        for (Attack a: attacks) {
            a.updatePos();
        }
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getWidth(); j++) {
                for (Attack a : attacks) {
                    if (!a.isOver() && a.getX() == j && a.getY() == i) {
                        if (map[j][i] == 1 && a.getDirection() != -1) {
                            a.kill();
                        }
                        for (Player p : players) {
                            if (p.getX() == a.getX() && p.getY() == a.getY() && p.getID() != a.getFather()) {
                                a.kill();
                                p.hit(a.getDamage());
                            }
                        }
                    }
                }
            }
        }
        players.removeIf(Player::isDead);
        attacks.removeIf(Attack::isOver);
    }

    public int getWidth() {
        return map.length;
    }

    public int getHeight() {
        return map[0].length;
    }

    public boolean canMove(int x, int y) {
        for(Player p : players) {
            if(p.getX() == x && p.getY() == y)
                return false;
        }
        return !(x < 0 || y < 0 || x >= map.length || y >= map[0].length) && map[x][y] == 0;
    }

    public void act(int a) {
        humanPlayer.setAction(a);
    }

    public Player nextPlayer() {
        return nextTo(humanPlayer);
    }
    private Player nextTo(Player p) {
        return players.get((players.indexOf(p)+1)%players.size());
    }

    public int getCell(int x, int y) {
        return map[x][y];
    }

    public Player closestTo(Player p) {
        Player g = null;
        int min = 99999;
        if(players.size() == 1)
            return null;
        for(Player e: players) {
            if(e != p) {
                int dist = Utils.trueDistance(e,p);
                if(dist < min) {
                    min = dist;
                    g = e;
                }
            }
        }
        return g;
    }

    public boolean haveAWinner() {
        return players.size() < 1;// == 1
    }
    public String winner() {
        String text;
        if(players.size() == 1) {
            if(players.get(0).getID() == 0)
                text = "You win!";
            else
                text = "Bot #" + players.get(0).getID() + " wins";
        }
        else
            text = "Everybody died";
        return text;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Collection<Attack> getAttacks() {
        return attacks;
    }

    public boolean isFloor(int x, int y) {
        try {
            return map[x][y] == 0;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
}
