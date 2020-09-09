/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package k4fernandovera;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Fernando
 */
public class FXMLDocumentController implements Initializable {

    private static Agenda agenda = new Agenda();
    private static FileChooser fileChooser = new FileChooser();
    private static File file;

    @FXML
    private Button boton;
    @FXML
    private TextField nombre;
    @FXML
    private TextField telefono;
    @FXML
    private static Stage stage;
    @FXML
    private ListView pru;
    @FXML
    private Button backButton;
    @FXML
    private Label label;
    static boolean borrar = false;

    public static void setStage(Stage stage) {
        FXMLDocumentController.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichero Txt", "*.txt"));
        hideItems();
    }

    @FXML
    private void handleClick(MouseEvent event) {
        String[] parts = pru.getSelectionModel().getSelectedItem().toString().split("📞");
        if (borrar) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle(null);
            alert.setHeaderText(null);
            alert.setContentText("¿Estas seguro que quieres borrar a "+parts[0]+"?");
            alert.initStyle(StageStyle.UNDECORATED);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("style.css").toExternalForm());
            dialogPane.getStyleClass().add("alert");
            dialogPane.setPrefSize(250, 200);
            alert.initOwner(stage);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                boolean existeParaBorrar = agenda.buscarContacto(parts[0]);
                if (existeParaBorrar) {
                    agenda.borrarContacto(parts[0]);
                    pru.getItems().remove(pru.getSelectionModel().getSelectedItem());
                    showContacts();
                    label.setStyle("-fx-text-fill:#4599d6;");
                    label.setText("AGENDA");
                } else {
                    label.setText("No se ha encontrado");
                }
            }
        } else {
            label.setStyle("-fx-text-fill:#4599d6;");
            label.setText("Editando contacto");
            pru.setVisible(false);
            showItems();
            boton.setText("Editar Contacto");
            nombre.setText(parts[0].trim());
            telefono.setText(parts[1].trim());
            boton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    boolean legalCharsName = nameValorIsValid();
                    boolean legalCharsNumbers = numberValorIsValid();
                    if (legalCharsName && legalCharsNumbers) {
                        agenda.anhadirContacto(nombre.getText(), Integer.valueOf(telefono.getText()));
                        agenda.borrarContacto(parts[0]);
                        label.setText("Editado correctamente");
                        label.setStyle("-fx-text-fill:#4599d6;");
                        pru.setVisible(true);
                        showContacts();
                    } else {
                        printAlert("Algunos caracteres introducidos pueden no ser validos, recuerda el numero ha de ser de 9 digitos");
                    }
                }
            });
        }
    }

    @FXML
    private void handleNewContact(ActionEvent event) {
        label.setText("Añadiendo contacto nuevo");
        label.setStyle("-fx-text-fill:#45e05f;");
        borrar = false;
        pru.setVisible(false);
        showItems();
        boton.setText("Añadir Contacto");
        boton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                boolean legalCharsName = nameValorIsValid();
                boolean legalCharsNumbers = numberValorIsValid();
                if (legalCharsName && legalCharsNumbers) {
                    agenda.anhadirContacto(nombre.getText(), Integer.valueOf(telefono.getText()));
                    label.setText("Añadido correctamente");
                    label.setStyle("-fx-text-fill:#45e05f;");
                    showContacts();
                } else {
                    printAlert("Algunos caracteres introducidos pueden no ser validos, recuerda el numero ha de ser de 9 digitos");
                }
            }
        });
    }

//     @FXML
//     private void handleSearchContact(ActionEvent event) {
//         showItems();
// //      printAlert("Introduce su nombre para buscar el contacto");
//         boton.setText("Buscar Contacto");
//         boton.setOnAction(new EventHandler<ActionEvent>() {
//             @Override
//             public void handle(ActionEvent e) {
//                 boolean existe = agenda.buscarContacto(nombre.getText());
//                 if (existe) {
// //                    printAlert("Nombre: " + nombre.getText() + System.lineSeparator() + "Numero de Telefono: " + String.valueOf(agenda.verTelefonoContacto()));
//                 } else {
// //                    printAlert("No existe ese contacto");
//                 }
//             }
//         });
//     }
    @FXML
    private void handleDeleteContact(ActionEvent event) {
        label.setText("Selecciona el contacto para borrar");
        label.setStyle("-fx-text-fill:red;");
        showContacts();
        borrar = true;
        boton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                boolean existeParaBorrar = agenda.buscarContacto(nombre.getText());
                if (existeParaBorrar) {
                    agenda.borrarContacto(nombre.getText());
                }
            }
        });
    }

    @FXML
    private void handleShowContacts(ActionEvent event) {
        showContacts();
        label.setStyle("-fx-text-fill:#4599d6;");
        label.setText("AGENDA");

    }

    private void showContacts() {
        borrar = false;
        hideItems();
        pru.setVisible(true);
        agenda.ordenarContactos();
        pru.getItems().removeAll(pru.getItems());
        pru.getItems().addAll(agenda.pasarContactos());

    }

    @FXML
    private void handleSaveContactsTXT(ActionEvent event) {
        hideItems();
        boolean saveSuccessfull = EscribirBuffer();
        if (saveSuccessfull) {
            printAlert("Guardado correctamente");
        } else {
            printAlert("Error al guardar el fichero txt");
        }
    }

    @FXML
    private void handleLoadContactsTXT(ActionEvent event) throws IOException {
        hideItems();
        boolean openSuccessfull = LeerBuffer();
        if (openSuccessfull) {
            printAlert("Cargada correctamente ");
            label.setStyle("-fx-text-fill:#4599d6;");
            label.setText("AGENDA");
            showContacts();

        } else {
            printAlert("Error al cargar el fichero txt");
        }
    }

    @FXML
    private void handleSaveContactsFromBinaryFile(ActionEvent event) {
        hideItems();
        boolean openSuccessfull = agenda.escribirArchivoBinario();
        if (openSuccessfull) {
            printAlert("Guardada correctamente");
        } else {
            printAlert("Error al guardar fichero binario");
        }
    }

    @FXML
    private void handleLoadContactsFromBinaryFile(ActionEvent event
    ) {
        hideItems();
        Agenda agendaNueva = agenda;
        agenda = Agenda.leerArchivoBinario();
        if (agenda != null) {
            printAlert("Cargada correctamente");
            label.setStyle("-fx-text-fill:#4599d6;");
            label.setText("AGENDA");
            showContacts();
        } else {
            agenda = agendaNueva;
            printAlert("Error al cargar fichero binario");
        }
    }

    @FXML
    private void handleExit(ActionEvent event
    ) {
        stage.close();
    }

    private void hideItems() {
        boton.setVisible(false);
        backButton.setVisible(false);
        nombre.setVisible(false);
        telefono.setVisible(false);
    }

    private void showItems() {
        boton.setVisible(true);
        backButton.setVisible(true);
        nombre.setVisible(true);
        nombre.clear();
        telefono.setVisible(true);
        telefono.clear();
    }

    static public boolean LeerBuffer() throws IOException {
        boolean result = false;
        agenda.vaciarAgenda();
        fileChooser.setTitle("Selecciona un fichero de contactos");
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String linea;
                while ((linea = br.readLine()) != null) {

                    String[] partes = linea.split(",");
                    for (int i = 0, j = 1; i < partes.length && j < partes.length; i = i + 2, j = j + 2) {
                        String nombre = partes[i];
                        String telefono = partes[j];
                        int tel = Integer.valueOf(telefono);
                        agenda.anhadirContacto(nombre, tel);
                    }
                }
                result = true;
            } catch (IOException e) {
            }
        }
        return result;
    }

    static public boolean EscribirBuffer() {
        boolean result = false;
        fileChooser.setTitle("Guardar el fichero de contactos");
        file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                String texto = agenda.guardarAgenda();
                bw.write(texto);
                result = true;
            } catch (IOException e) {
            }
        }
        return (result);
    }

    public boolean nameValorIsValid() {
        boolean result = false;
        Pattern p;
        p = Pattern.compile("^[A-Za-z0-9]*$");
        Matcher m = p.matcher(nombre.getText());
        if (m.find()) {
            result = true;
        }
        return result;
    }

    public boolean numberValorIsValid() {
        boolean result = false;
        Pattern p;
        p = Pattern.compile("^[0-9]{9}$");
        Matcher m = p.matcher(telefono.getText());
        if (m.find()) {
            result = true;
        }
        return result;
    }

    public void printAlert(String t) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(t);
        alert.initStyle(StageStyle.UNDECORATED);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");
        dialogPane.setPrefSize(250, 200);
        alert.initOwner(stage);
        alert.showAndWait();
    }
}
