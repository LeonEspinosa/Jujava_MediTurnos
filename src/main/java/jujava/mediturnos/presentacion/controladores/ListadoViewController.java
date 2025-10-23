package jujava.mediturnos.presentacion.controladores;

import com.example.myjavafx.model.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controlador para listado-view.fxml.
 * Maneja la lógica específica de la vista de listado.
 */
public class ListadoViewController {

    // 1. Inyección de Componentes (deben coincidir con fx:id)
    @FXML
    private TextField txtDni;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnEliminar;
    @FXML
    private TableView<Usuario> tblUsuarios;
    @FXML
    private TableColumn<Usuario, String> colDni;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colApellido;
    @FXML
    private TableColumn<Usuario, String> colRol;

    // Referencia al controlador principal (de lógica/datos)
    private MainController dataController;

    /**
     * Llamado automáticamente por JavaFX después de cargar el FXML.
     * Es el lugar perfecto para configurar la vista.
     */
    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla
        colDni.setCellValueFactory(cellData -> cellData.getValue().dniProperty());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colApellido.setCellValueFactory(cellData -> cellData.getValue().apellidoProperty());
        colRol.setCellValueFactory(cellData -> cellData.getValue().rolProperty());

        // Escuchar la selección de la tabla e informar al dataController
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (dataController != null) {
                        dataController.setUsuarioSeleccionado(newSelection);
                    }
                });
    }

    /**
     * Método de inicialización manual.
     * Usado por MainViewController para pasar el MainController y cargar datos.
     */
    public void init(MainController mainController) {
        this.dataController = mainController;
        // Cargar los datos iniciales del controlador principal
        tblUsuarios.setItems(mainController.getFilteredData());
    }

    // 2. Métodos de Acción (llamados desde onAction en FXML)

    @FXML
    private void handleBuscar() {
        if (dataController != null) {
            dataController.buscarUsuarioPorDNI(txtDni.getText());
        }
    }

    @FXML
    private void handleEliminar() {
        if (dataController != null) {
            dataController.eliminarUsuarioSeleccionado();
        }
    }
}
