/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.phdev.faciltransferencia.teste;

/**
 *
 * @author Paulo Henrique Gonçalves Bacelar
 */
public interface OnReadListener {
    
    int onRead(byte[] bytes, int bufferSize);
    
}
