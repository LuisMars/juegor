package es.upv.luimafus.server;


import com.badlogic.gdx.math.Rectangle;
import es.upv.luimafus.*;
import es.upv.luimafus.Map;

import java.util.*;


public class ServerMap extends Map {
    public int[][] map;
    public int[][] drawMap;

    private List<Player> players = new ArrayList<Player>();
    private Collection<Attack> attacks = new ArrayList<Attack>();
    private ServerScreen gs;
    public long seed;

    public CellList floor = new CellList();
    ArrayList<String> s = new ArrayList<String>();


    public ServerMap(ServerScreen s, int h, int w, double density) {

        Player.n_players = 0;
        gs = s;
        map = new int[h][w];
        generateMap(density);
        setDrawMap();
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
                drawMap[i][j] = floor.getIndex(Utils.getTile(map, i, j));


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

    public boolean movePlayer(Player p, int x, int y) {
        boolean canM = canMove(x,y);
        if(canM)
            p.moveTo(x,y);
        return canM;
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
        return players.size() == 1;
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

    private void setDrawMap() {
        loadFloorNames();
        for (String value : s)
            floor.add(new Cell(null, value.substring(0, 4), value.substring(4)));
    }

    private void loadFloorNames() {

        s.add("11111111");
        s.add("00000000");
        s.add("00000010");
        s.add("00000011");
        s.add("00000001");
        s.add("00000110");
        s.add("00001111");
        s.add("00001001");
        s.add("00000100");
        s.add("00001100");
        s.add("00001000");

        s.add("00000111");
        s.add("00001011");
        s.add("00001101");
        s.add("00001110");

        s.add("00000101");
        s.add("00001010");

        s.add("1000xx00");
        s.add("0001x00x");
        s.add("01000xx0");
        s.add("001000xx");
        s.add("1000xx10");
        s.add("1000xx11");
        s.add("1000xx01");
        s.add("0001x01x");
        s.add("01000xx1");
        s.add("0001x11x");
        s.add("01001xx1");
        s.add("0001x10x");
        s.add("01001xx0");
        s.add("001001xx");
        s.add("001011xx");
        s.add("001010xx");

        s.add("1001xx0x");
        s.add("1100xxx0");
        s.add("0011x0xx");
        s.add("01100xxx");
        s.add("1001xx1x");
        s.add("1100xxx1");
        s.add("0011x1xx");
        s.add("01101xxx");

        s.add("1010xxxx");
        s.add("0101xxxx");
        s.add("1101xxxx");
        s.add("0111xxxx");
        s.add("1111xxxx");
        s.add("1011xxxx");
        s.add("1110xxxx");
        s.add("topA");
        s.add("topB");

    }

}
