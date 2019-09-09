/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.marketHubServer.util;

/**
 *
 * @author Tiago Albuquerque
 */
public class Util {
    public String nullToEmpty(String value) {
        if(value!=null) 
            return value;
        else 
            return "";
    }
}
