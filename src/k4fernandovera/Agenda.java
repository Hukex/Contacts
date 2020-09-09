/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package k4fernandovera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author DAW
 */
public class Agenda implements Serializable {

    private static FileChooser fileChooser = new FileChooser();
    private static File file;
    private static Stage stage;
    private static String salto = "\n";
    private List<Contacto> contactos = new ArrayList<>();
    private int posicionDelContacto;

    public void anhadirContacto(String nombre, int numero) {
        contactos.add(new Contacto(nombre, numero));
    }

    public String verNombreContacto() {
        Contacto c = contactos.get(posicionDelContacto - 1);
        return c.getNombre();
    }

    public int verTelefonoContacto() {
        Contacto c = contactos.get(posicionDelContacto - 1);
        return c.getTelefono();
    }

    public boolean buscarContacto(String nombre) {
        posicionDelContacto = 0;
        Iterator<Contacto> cI = contactos.iterator();
        boolean resultado = false;
        while (cI.hasNext() && !resultado) {
            Contacto c = cI.next();
            if (c.getNombre().equalsIgnoreCase(nombre)) {
                resultado = true;
            }
            posicionDelContacto++;
        }
        return (resultado);
    }

    public void ordenarContactos() {
        contactos.sort(new Comparator<Contacto>() {
            @Override
            public int compare(Contacto o1, Contacto o2) {
                return (o1.getNombre().compareToIgnoreCase(o2.getNombre()));
            }
        }
        );
    }

    public void vaciarAgenda() {
        contactos.removeAll(contactos);
    }

    public String mostrarContactos() {
        String resultado = "";
        for (Contacto c : contactos) {
            resultado += c.toString() + salto;
        }
        return (resultado);
    }
    public List<Contacto> pasarContactos(){
    return contactos;
    }

    public String guardarAgenda() {
        String resultado = "";
        for (Contacto c : contactos) {
            resultado += (c.stringPersonalizado());
        }
        return (resultado);
    }

    public void borrarContacto(String nombre) {
        Iterator<Contacto> cI = contactos.iterator();
        boolean resultado = false;
        while (cI.hasNext() && !resultado) {
            Contacto c = cI.next();
            if (c.getNombre().equalsIgnoreCase(nombre)) {
                contactos.remove(c);
                resultado = true;
            }
        }
    }

    public boolean escribirArchivoBinario() {
        boolean result = false;
        fileChooser.setTitle("Guardar el fichero de contactos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichero Binario", "*.dat"));
        file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream(file))) {
                ous.writeObject(this);
                result = true;
            } catch (IOException e) {
            }
        }
        return result;
    }

    public static Agenda leerArchivoBinario() {
        Agenda agenda = null;
        fileChooser.setTitle("Selecciona un fichero de contactos");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichero Binario", "*.dat"));
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            agenda = new Agenda();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                agenda = (Agenda) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
            }
        }
        return agenda;
    }
}
