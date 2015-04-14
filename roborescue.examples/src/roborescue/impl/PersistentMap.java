/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roborescue.impl;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author henrique
 */
public class PersistentMap extends Map {

    private Double[][] heuristica;
    private Properties prop;
    private static String fileName = "LRTAstar.txt";

    private Boolean dirty;
    private Boolean formation_flag = false;

    public PersistentMap() {
        super();
        heuristica = new Double[WIDTH / discr_width][HEIGHT / discr_heigth];
        prop = new Properties();
        dirty = false;
    }

    public void loadProperties() {
        FileInputStream inp = null;
        try {
            inp = new FileInputStream(fileName);
            prop.load(inp);
            inp.close();
            if (!prop.isEmpty()) {
                for (String key : prop.stringPropertyNames()) {
                    String[] parts = key.split(":");
                    if (parts.length == 2) {
                        heuristica[Integer.parseInt(parts[0])][Integer.parseInt(parts[1])] = Double.parseDouble(prop.getProperty(key));
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            formation_flag = true;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                if (inp != null) {
                    inp.close();
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public Double getHeuristica(Point locale) {
        return heuristica[locale.x][locale.y];
    }

    @Override
    public void setObjective(Point objective) {
        super.setObjective(objective);

        if (formation_flag) {
            for (int k = 0; k < heuristica.length; k++) {
                for (int j = 0; j < heuristica[0].length; j++) {
//                    prop.setProperty(k + "|" + j, distance_px(new Point(k, j), objective).toString());
                    heuristica[k][j] = distance_px(new Point(k, j), objective);
                }
            }
            
            dirty = true;

            formation_flag = false;
        }
    }

    @Override
    public State<Point> getStateForPointDiscrete(double x, double y) {
        State<Point> sta = super.getStateForPointDiscrete(x, y);
        
        sta.setOverwriteHeuristic(heuristica[sta.getState().x][sta.getState().y]);
        
        return sta;
    }

    @Override
    public State<Point> getStateForPoint(Point p) {
        State<Point> sta = super.getStateForPoint(p);
        
        sta.setOverwriteHeuristic(heuristica[sta.getState().x][sta.getState().y]);
        
        return sta;
    }

    public void saveProperties() throws IOException {
        if (dirty) {
            for (int k = 0; k < heuristica.length; k++) {
                for (int j = 0; j < heuristica[0].length; j++) {
                    prop.setProperty(k + ":" + j, (heuristica[k][j] != null ? heuristica[k][j].toString() : "0.0"));
                }
            }

            FileOutputStream out = new FileOutputStream(fileName);
            prop.store(out, "");
            out.close();

            dirty = false;
        }
    }

    public void setCostForPoint(Double cost, Point point) {
        if (point.x >= 0 && point.x <= heuristica.length && point.y >= 0 && point.y <= heuristica[0].length) {
            heuristica[point.x][point.y] = cost;
            dirty = true;
        }
    }
}
