/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roborescue.impl;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author henrique
 */
public class Map {

    public static final Integer WIDTH = 2427;
    public static final Integer HEIGHT = 1500;

    public static final int discr_width = 50;
    public static final int discr_heigth = 50;
    
    private Point objective;
    private static Double areaDeSeguranca_px = 75.0;

    private static final Map map = new Map();

    public static Point translateToPx(Point pop) {
        return new Point((pop.x * discr_width) + (discr_width/2), (pop.y * discr_heigth) + (discr_heigth/2));
    }
    
    public static Point translateToDiscrete(double x, double y){
        if (x > 0 && y > 0 && x <= WIDTH && y <= HEIGHT) {
            return new Point(((Double) Math.floor(x / discr_width)).intValue(), ((Double) Math.floor(y / discr_heigth)).intValue());
        }

        throw new IllegalArgumentException("Argumentos inv'alidos");
    }
    
    private Double[][] posicoes;

    public static Map getInstance() {
        return map;
    }

    public Point getObjective() {
        return objective;
    }

    public void setObjective(Point objective) {
        this.objective = objective;
    }

    public static Point getPositionFor(Double x, Double y) {
        if (x > 0 && y > 0 && x <= WIDTH && y <= HEIGHT) {
            return new Point(((Double) Math.floor(x / discr_width)).intValue(), ((Double) Math.floor(y / discr_heigth)).intValue());
        }

        throw new IllegalArgumentException("Argumentos inv'alidos");
    }

    public void setPosicoes(Double[][] posicoes) {
        this.posicoes = posicoes;
    }

    public State<Point> getStateForPoint(Point p) {
        return new State2(p);
    }
    
    public State<Point> getStateForPointDiscrete(double x, double y) {
        return new State2(translateToDiscrete(x, y));
    }

    class State2 extends State<Point> {

        public State2(Point state) {
            super(state);
            
            double dist = distance(state, objective);
            
            if (dist < 0) dist *= -1;
            
            cost = dist;
        }

        public boolean isOccupied(Double[][] posicoes) {
            if(state.x > 43){
                int k = 5;
            }
            for (int k = 0; k < posicoes.length; k++) {
                Point p = translateToDiscrete( posicoes[k][0], posicoes[k][1]);
                
//                Double security = areaDeSeguranca_px / discr_width;
                
                Ellipse2D circle = new Ellipse2D.Double(posicoes[k][0] - (areaDeSeguranca_px/2), posicoes[k][1] - (areaDeSeguranca_px/2), areaDeSeguranca_px, areaDeSeguranca_px);
//                Rectangle rect = new Rectangle((int)(p.x - (security/2)), (int)(p.y - (security/2)), (int)security.intValue(), (int)security.intValue());
                
                Rectangle rectState = new Rectangle(state.x * discr_width, state.y * discr_heigth, discr_width, discr_heigth);

                if (circle.intersects(rectState)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public List<Action<Point>> avaliableStates() {
            ArrayList<Action<Point>> list = new ArrayList<>();
            

            if (this.state.x > 0) {
                State<Point> sta = new State2(new Point(this.state.x - 1, this.state.y));
                if (!((State2) sta).isOccupied(posicoes)) {
                    Action<Point> action = new Action2(this).setDestino(sta);
                    sta.setCameFrom(action);
                    list.add(action);
                }
            }
            if (this.state.y > 0) {
                State<Point> sta = new State2(new Point(this.state.x, this.state.y - 1));
                if (!((State2) sta).isOccupied(posicoes)) {
                    Action<Point> action = new Action2(this).setDestino(sta);
                    sta.setCameFrom(action);
                    list.add(action);
                }
            }

            if (this.state.x < WIDTH / discr_width) {
                State<Point> sta = new State2(new Point(this.state.x + 1, this.state.y));
                if (!((State2) sta).isOccupied(posicoes)) {
                    Action<Point> action = new Action2(this).setDestino(sta);
                    sta.setCameFrom(action);
                    list.add(action);
                }
            }
            if (this.state.y < HEIGHT / discr_heigth) {
                State<Point> sta = new State2(new Point(this.state.x, this.state.y + 1));
                if (!((State2) sta).isOccupied(posicoes)) {
                    Action<Point> action = new Action2(this).setDestino(sta);
                    sta.setCameFrom(action);
                    list.add(action);
                }
            }

            if (this.state.x > 0 && this.state.y > 0) {
                State<Point> sta = new State2(new Point(this.state.x - 1, this.state.y - 1));
                if (!((State2) sta).isOccupied(posicoes)) {
                    Action<Point> action = new Action2(this).setDestino(sta);
                    sta.setCameFrom(action);
                    list.add(action);
                }
            }

            if (this.state.x > 0 && this.state.y < HEIGHT / discr_heigth) {
                State<Point> sta = new State2(new Point(this.state.x - 1, this.state.y + 1));
                if (!((State2) sta).isOccupied(posicoes)) {
                    Action<Point> action = new Action2(this).setDestino(sta);
                    sta.setCameFrom(action);
                    list.add(action);
                }
            }

            if (this.state.x < WIDTH / discr_width && this.state.y < HEIGHT / discr_heigth) {
                State<Point> sta = new State2(new Point(this.state.x + 1, this.state.y + 1));
                if (!((State2) sta).isOccupied(posicoes)) {
                    Action<Point> action = new Action2(this).setDestino(sta);
                    sta.setCameFrom(action);
                    list.add(action);
                }
            }

            if (this.state.x < WIDTH / discr_width && this.state.y > 0) {
                State<Point> sta = new State2(new Point(this.state.x + 1, this.state.y - 1));
                if (!((State2) sta).isOccupied(posicoes)) {
                    Action<Point> action = new Action2(this).setDestino(sta);
                    sta.setCameFrom(action);
                    list.add(action);
                }
            }

            return list;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof State2) {
                return state.equals(((State2) o).state);
            }

            return super.equals(o);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }

        @Override
        public int compareTo(State<Point> t) {
            int catetoX = (this.state.x > t.state.x ? this.state.x - t.state.x : t.state.x - this.state.x);
            int catetoY = (this.state.y > t.state.y ? this.state.y - t.state.y : t.state.y - this.state.y);

            return (int)Math.sqrt(((catetoX * catetoX) + (catetoY * catetoY)));
        }

    }

    class Action2 extends Action<Point> {

        public Action2(State<Point> pai) {
            super(pai);
        }

        @Override
        public Double getCostFor() {
            //Distância Euclidiana
            int catetoX = (pai.state.x > destino.state.x ? pai.state.x - destino.state.x : destino.state.x - pai.state.x);
            int catetoY = (pai.state.y > destino.state.y ? pai.state.y - destino.state.y : destino.state.y - pai.state.y);

            return Math.sqrt(((catetoX * catetoX) + (catetoY * catetoY)));
        }

    }

    public static Double distance(Point position, Point objetivo) {
        int catetoX = (position.x > objetivo.x ? position.x - objetivo.x : objetivo.x - position.x);
        int catetoY = (position.y > objetivo.y ? position.y - objetivo.y : objetivo.y - position.y);

        return Math.sqrt(((catetoX * catetoX) + (catetoY * catetoY)));
    }
    
    public static Double distance_px(Point position, Point objetivo) {
        int catetoX = (position.x > objetivo.x ? position.x - objetivo.x : objetivo.x - position.x) * discr_width;
        int catetoY = (position.y > objetivo.y ? position.y - objetivo.y : objetivo.y - position.y) * discr_heigth;

        return Math.sqrt(((catetoX * catetoX) + (catetoY * catetoY)));
    }
}
