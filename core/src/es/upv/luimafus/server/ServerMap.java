package es.upv.luimafus.server;


import com.badlogic.gdx.math.Rectangle;
import es.upv.luimafus.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;


public class ServerMap extends Map {
    public int[][] map;
    public int[][] drawMap;
    public long seed;
    public CellList floor = new CellList();
    ArrayList<String> tileNames = new ArrayList<String>();
    private List<Player> players = new ArrayList<Player>();
    private Collection<Attack> attacks = new ArrayList<Attack>();
    private ServerScreen gs;

    public ServerMap(ServerScreen serverScreen, int h, int w, double density) {

        Player.n_players = 0;
        gs = serverScreen;
        map = new int[h][w];
        generateMap(density);
        setDrawMap();
        prepareMap();
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

    private void setDrawMap() {
        loadFloorNames();
        for (String value : tileNames)
            floor.add(new Cell(null, value.substring(0, 4), value.substring(4)));
    }

    private void loadFloorNames() {

        tileNames.add("11111111");
        tileNames.add("00000000");
        tileNames.add("00000010");
        tileNames.add("00000011");
        tileNames.add("00000001");
        tileNames.add("00000110");
        tileNames.add("00001111");
        tileNames.add("00001001");
        tileNames.add("00000100");
        tileNames.add("00001100");
        tileNames.add("00001000");

        tileNames.add("00000111");
        tileNames.add("00001011");
        tileNames.add("00001101");
        tileNames.add("00001110");

        tileNames.add("00000101");
        tileNames.add("00001010");

        tileNames.add("1000xx00");
        tileNames.add("0001x00x");
        tileNames.add("01000xx0");
        tileNames.add("001000xx");
        tileNames.add("1000xx10");
        tileNames.add("1000xx11");
        tileNames.add("1000xx01");
        tileNames.add("0001x01x");
        tileNames.add("01000xx1");
        tileNames.add("0001x11x");
        tileNames.add("01001xx1");
        tileNames.add("0001x10x");
        tileNames.add("01001xx0");
        tileNames.add("001001xx");
        tileNames.add("001011xx");
        tileNames.add("001010xx");

        tileNames.add("1001xx0x");
        tileNames.add("1100xxx0");
        tileNames.add("0011x0xx");
        tileNames.add("01100xxx");
        tileNames.add("1001xx1x");
        tileNames.add("1100xxx1");
        tileNames.add("0011x1xx");
        tileNames.add("01101xxx");

        tileNames.add("1010xxxx");
        tileNames.add("0101xxxx");
        tileNames.add("1101xxxx");
        tileNames.add("0111xxxx");
        tileNames.add("1111xxxx");
        tileNames.add("1011xxxx");
        tileNames.add("1110xxxx");
        tileNames.add("topA");
        tileNames.add("topB");

    }

    public void prepareMap() {
        drawMap = new int[getWidth()][getHeight()];

        for(int i = 0; i < getWidth(); i++)
            for(int j = 0; j < getHeight(); j++)
                drawMap[i][j] = floor.getIndex(Utils.getTile(map, i, j));


    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void addAttack(Attack a) {
        attacks.add(a);
    }

    public void updateState() {
        //System.out.print(".");
        for (Player p : players) {
            p.updateArea();
            p.act();
        }
        removeDead();
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
        removeDead();
    }

    private void removeDead() {

        for (User u : Server.users) {
            if (u.isReady && u.hasMap && u.p.isDead()) {
                //u.isReady = false;
                User.readyPlayers--;
                if (!u.userKnowsIsDead) {
                    Server.deadMsg(u);
                    u.userKnowsIsDead = true;
                }
            }

        }
        //players.removeIf(Player::isDead);
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



}
