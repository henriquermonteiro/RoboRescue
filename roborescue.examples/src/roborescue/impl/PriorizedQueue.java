/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roborescue.impl;

import java.util.ArrayList;

/**
 *
 * @author henrique
 */
public class PriorizedQueue<T> {

    private ArrayList<ValuedObject<T, Integer>> queue;

    public PriorizedQueue() {
        queue = new ArrayList<ValuedObject<T, Integer>>();
    }

    public void add(T obj, Integer value) {
        for(int k = 0; k < queue.size(); k++){
            if(queue.get(k).getV().compareTo(value) > 0){
                queue.add(k, new ValuedObject<>(obj, value));
                return;
            }
        }
        
        queue.add(new ValuedObject<>(obj, value));
    }

    public T pop() {
        if (queue.isEmpty()) {
            return null;
        }
        
        return queue.remove(0).getO();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    class ValuedObject<O, V extends Comparable> {

        private O o;
        private V v;

        public ValuedObject(O o2, V v2) {
            o = o2;
            v = v2;
        }

        public O getO() {
            return o;
        }

        public void setO(O o) {
            this.o = o;
        }

        public V getV() {
            return v;
        }

        public void setV(V v) {
            this.v = v;
        }

        public int compare(V v2) {
            return v.compareTo(v2);
        }
    }
}
