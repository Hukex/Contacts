/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package k4fernandovera;

import java.io.Serializable;

/**
 *
 * @author DAW
 */
public class Contacto implements Serializable {

    private String nombre;
    private int telefono;

    @Override
    public String toString() {
        return nombre + "📞 " + telefono;
    }

    public String stringPersonalizado() {
        return nombre + "," + telefono + System.lineSeparator();
    }

    public Contacto(String nombre, int telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

}
