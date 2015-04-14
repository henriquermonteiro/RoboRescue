/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package roborescue.impl;

/**
 *
 * @author henrique
 */
public abstract class Action<T> {
    protected final State<T> pai;
    protected State<T> destino;
    
    public Action(State<T> pai) {
        this.pai = pai;
    }

    public Action setDestino(State<T> destino) {
        this.destino = destino;
        
        return this;
    }
    
    public abstract Double getCostFor();
    
    public State<T> getDestino(){
        return destino;
    }
}
