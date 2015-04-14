/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roborescue.impl;

import java.util.Objects;

/**
 *
 * @author henrique
 * @param <T>
 */
public class SearchNode<T> implements Comparable<SearchNode<T>> {

    private final SearchNode father;
    private final State<T> state;
    private final Double pathCost;

    public SearchNode(SearchNode father, State<T> state, Double fixedCost) {
        this.father = father;
        this.state = state;
        this.pathCost = fixedCost;
    }

    @Override
    public int compareTo(SearchNode<T> t) {
        return this.state.compareTo(t.state);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.state);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof SearchNode) {
            final SearchNode<?> other = (SearchNode<?>) obj;
            return Objects.equals(this.state, other.state);
        }
        
        return Objects.equals(this.state, obj);
    }

    public SearchNode getFather() {
        return father;
    }

    public State<T> getState() {
        return state;
    }
    
    public Double getCostF(){
        return state.getHeuristicCost() + pathCost;
    }

}
