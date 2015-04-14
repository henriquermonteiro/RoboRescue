/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roborescue.impl;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author henrique
 */
public abstract class State<T> implements Comparable<State<T>>{
    protected T state;
    protected Double cost;
    protected Action<T> cameFrom;
    protected Double pathCost;

    public State(T state) {
        this.state = state;
        pathCost = 0.0;
    }

    public Action<T> getCameFrom() {
        return cameFrom;
    }

    public State<T> setCameFrom(Action<T> cameFrom) {
        this.cameFrom = cameFrom;
        pathCost = cameFrom.getCostFor() + cameFrom.getDestino().getPathCost();
        
        return this;
    }

    public Double getPathCost() {
        return pathCost;
    }

    public void setPathCost(Double pathCost) {
        this.pathCost = pathCost;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.state);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if(obj instanceof SearchNode){
            obj = ((SearchNode)obj).getState();
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final State<?> other = (State<?>) obj;
        if (Objects.equals(this.state, other.state)) {
            return true;
        }
        return false;
    }
    
    public abstract List<Action<T>> avaliableStates();
    
    public T getState(){
        return state;
    }
    
    public Double getHeuristicCost(){
        return cost;
    }
    
    
}
