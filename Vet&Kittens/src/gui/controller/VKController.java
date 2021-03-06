package gui.controller;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import data.model.cat.Cat;
import data.model.login.Login;
import gui.controller.tablemodel.CatTableModel;
import gui.view.panel.AltaCatPanel;
import gui.view.panel.AltaVetPanel;
import gui.view.panel.BuscaCatPanel;
import gui.view.panel.LoginVetPanel;
import gui.view.panel.MainPanel;
import gui.view.service.IconService;
import gui.view.window.AboutWindow;
import gui.view.window.LoginWindow;
import gui.view.window.MainWindow;
import logic.CatServiceImpl;
import logic.service.exception.AccessDeniedException;

public class VKController implements ActionListener {

	public IconService iconService = new IconService();

	// PANELES
	private MainPanel mainPanel;
	private AltaVetPanel altaVetPanel;
	private LoginVetPanel buscaVetPanel;
	private AltaCatPanel altaCatPanel;
	private BuscaCatPanel buscaCatPanel;

	// VENTANAS
	private AboutWindow aboutWindow;
	private MainWindow mainWindow;
	private LoginWindow loginWindow;

	// PANEL ACTIVO
	private JPanel panelEnUso; // El panel en uso

	// SQL-> JAVA OBJ
	public List<Cat> catList;

	// TableModel de Cat
	public CatTableModel catTableModel = new CatTableModel();

	// GETTERS & SETTERS
	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	public AboutWindow getAboutWindow() {
		return aboutWindow;
	}

	public void setAboutWindow(AboutWindow acercaDeWindow) {
		this.aboutWindow = acercaDeWindow;
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public AltaVetPanel getAltaVetPanel() {
		return altaVetPanel;
	}

	public LoginVetPanel getBuscaVetPanel() {
		return buscaVetPanel;
	}

	public void setMainPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}

	public void setAltaVetPanel(AltaVetPanel altaPanel) {
		this.altaVetPanel = altaPanel;
	}

	public void setBuscaVetPanel(LoginVetPanel buscaPanel) {
		this.buscaVetPanel = buscaPanel;
	}

	public AltaCatPanel getAltaCatPanel() {
		return altaCatPanel;
	}

	public void setAltaCatPanel(AltaCatPanel altaCatPanel) {
		this.altaCatPanel = altaCatPanel;
	}

	public BuscaCatPanel getBuscaCatPanel() {
		return buscaCatPanel;
	}

	public void setBuscaCatPanel(BuscaCatPanel buscaCatPanel) {
		this.buscaCatPanel = buscaCatPanel;
	}

	public JPanel getPanelEnUso() {
		return panelEnUso;
	}

	public void setPanelEnUso(JPanel actualPanel) {
		this.panelEnUso = actualPanel;
	}

	// CONSTRUCTORES ENCAPSULADOS
	public MainWindow createMainWindow() {
		return new MainWindow(this);
	}

	public AboutWindow createAboutWindow(VKController controller) {
		return new AboutWindow(controller);
	}

	public LoginWindow createLoginWindow() {
		return new LoginWindow(this);
	}

	public MainPanel createMainPanel(VKController controller) {
		return new MainPanel(controller);
	}

	public AltaVetPanel createAltaVetPanel() {
		return new AltaVetPanel();
	}

	public LoginVetPanel createBuscaVetPanel() {
		return new LoginVetPanel();
	}

	public AltaCatPanel createAltaCatPanel() {
		return new AltaCatPanel(this);
	}

	public BuscaCatPanel createBuscaCatPanel() {
		return new BuscaCatPanel(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();

		if (command.contentEquals(ActionCommands.EXIT)) {
			Integer option = JOptionPane.showConfirmDialog((Component) e.getSource(), "�Confirma salida?");
			if (option == 0)
				System.exit(0);
		} else if (command.contentEquals(ActionCommands.ALTAVET)) {
			loadAltaVetPanel(mainWindow);
		} else if (command.contentEquals(ActionCommands.BUSCARVET)) {
			loadBuscaVetPanel(mainWindow);
		} else if (command.contentEquals(ActionCommands.ABOUT)) {
			VKController.loadAboutWindow(this);
		} else if (command.contentEquals(ActionCommands.ALTACAT)) {
			loadAltaCatPanel();
		} else if (command.contentEquals(ActionCommands.BUSCACAT)) {
			loadBuscaCatPanel();
		} else if (command.contentEquals(ActionCommands.CONSULTA_TABLA_CAT)) {
			loadCatList();
		} else if (command.contentEquals(ActionCommands.BACK_TO_MAIN)) {
			loadMainPanel(mainWindow, this);
		} else if (command.contentEquals(ActionCommands.RESET_CAT_VIEW)) {
			clearFields();
			clearTable();
		} else if (command.contentEquals(ActionCommands.INSERT)) {
			insertNewCat();
		} else if (command.contentEquals(ActionCommands.LOGIN)) {
			login();
		} else if (command.contentEquals(ActionCommands.CANCEL_LOGIN)) {
			exitApp();
		} else if (command.contentEquals(ActionCommands.LOGOUT)) {
			logOut();
		} else if (command.contentEquals(ActionCommands.VISIT_EDU)) {
			visitUrl(0);
		} else if (command.contentEquals(ActionCommands.VISIT_GITHUB)) {
			visitUrl(1);
		} else {
			return;
		}

	}

	private void visitUrl(Integer option) {
		try {
			if (option == 0) {
				Desktop.getDesktop().browse(new URL("https://eduardado.github.io/").toURI());
			}
			if (option == 1) {
				Desktop.getDesktop().browse(new URL("https://github.com/eduardado/interfaces-actividad-3").toURI());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void logOut() {
		loadLoginWindow();

	}

	private void exitApp() {
		System.exit(0);

	}

	public void login() {

		Login login = CatServiceImpl.getCatService().retrieveLogin(loginWindow.ffUser, loginWindow.ffPassword);
		try {
			CatServiceImpl.getCatService().checkPassword(login);
			loginWindow.ffPassword.setText("");
			loginWindow.ffUser.setText("");
			loginWindow.dispose();
		} catch (AccessDeniedException e1) {
			JOptionPane.showMessageDialog(null, e1.getMessage(), e1.TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	private void insertNewCat() {
		Cat cat = null;
		CatServiceImpl catService = new CatServiceImpl();
		try {
			cat = createCatObjectFromCatPanel();
			if (cat != null) {
				catService.insertCat(cat);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		test(cat);

	}

	// para ver por consola si se crean bien los gatos
	private void test(Cat cat) {
		System.out.println(cat);

	}

	/**
	 * Lee los datos del panel de alta y crea un objeto Cat
	 * 
	 * @return un objeto Cat
	 */
	private Cat createCatObjectFromCatPanel() {
		Cat cat = null;

		// panel data
		String altaChip;
		String altaName;
		String altaWeight;
		Date altaDobDate;
		Date altaVaccineADate;
		Boolean panelCompleto = true;
		// recoge los datos del panel
		try {
			altaChip = altaCatPanel.getTfChipSerial().getText();
			if (altaChip == null)
				panelCompleto = false;
			altaName = altaCatPanel.getTfName().getText();
			if (altaName == null)
				panelCompleto = false;
			altaWeight = altaCatPanel.getTfWeight().getText();
			if (altaWeight == null)
				panelCompleto = false;
			altaDobDate = (Date) altaCatPanel.getDobDatePicker().getModel().getValue();
			if (altaDobDate == null)
				panelCompleto = false;
			altaVaccineADate = (Date) altaCatPanel.getVaccineDatePicker().getModel().getValue();
			if (altaVaccineADate == null)
				panelCompleto = false;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (panelCompleto) {
			try {
				cat = new Cat();
				cat.setChipSerial(Long.parseLong(altaCatPanel.getTfChipSerial().getText()));
				cat.setName(altaCatPanel.getTfName().getText());
				cat.setWeight(Double.parseDouble(altaCatPanel.getTfWeight().getText()));
				Date selectedDOBDate = (Date) altaCatPanel.getDobDatePicker().getModel().getValue();
				cat.setDob(selectedDOBDate);
				Date selectedVaccineDate = (Date) altaCatPanel.getVaccineDatePicker().getModel().getValue();
				cat.setVaccineA(selectedVaccineDate);

				altaCatPanel.getLbInfo().setText(cat.getHtmlString()); // informa al usuario del estado: objeto creado con �xito
			} catch (Exception e) {
				// informa al usuario
				altaCatPanel.getLbInfo().setText("> Data Introduced error. Please Check tooltips");
			}
		} else {
			altaCatPanel.getLbInfo().setText("> Cat not registered: Fill all Fields please");
		}
		return cat;
	}

	private void clearTable() {
		catTableModel.clearTable();
		catTableModel.fireTableDataChanged();
	}

	private void clearFields() {
		buscaCatPanel.getTfID().setText("");
		buscaCatPanel.getTfName().setText("");
		buscaCatPanel.getTfChipSerial().setText("");
		buscaCatPanel.getDobDatePicker().getModel().setValue(null);
		buscaCatPanel.getVaccineDatePicker().getModel().setValue(null);
		buscaCatPanel.getTfWeight().setText("");
	}

	public void loadCatList() {
		CatServiceImpl catService = new CatServiceImpl();

		String catId;
		String catChipSerial;
		String catName;
		String catWeight;
		String catDOB = null;
		String catVaccineA = null;

		try {
			catId = buscaCatPanel.getTfID().getText();
			catChipSerial = buscaCatPanel.getTfChipSerial().getText();
			catName = buscaCatPanel.getTfName().getText();
			catWeight = buscaCatPanel.getTfWeight().getText();

			// conversi�n de Date a String

			// tomamos los valores como objeto Date
			Date dobDate = (Date) buscaCatPanel.getDobDatePicker().getModel().getValue();
			Date vaccineADate = (Date) buscaCatPanel.getVaccineDatePicker().getModel().getValue();

			// creamos un formato de salida para el String
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			if (dobDate != null) { // Por si el usuario no selecciona ninguna fecha
				catDOB = dateFormat.format(dobDate);
				System.out.println(catDOB);
			}
			if (vaccineADate != null) {
				catVaccineA = dateFormat.format(vaccineADate);
				System.out.println(catVaccineA);
			}

			// consulta desde la capa de servicios
			catList = catService.queryCatTable(catId, catChipSerial, catName, catWeight, catDOB, catVaccineA);
		} catch (SQLException e) {
			e.getMessage();
			e.printStackTrace();
		}

		if (catList == null) { // cuando la consulta no devuelve nada
			catList = new ArrayList<Cat>(); // instancio una lista para que TableModel no meta un petardazo :)
			buscaCatPanel.getLbInfo().setText(":( Sorry. No cat was found with the info provided"); // informo al usuario
			buscaCatPanel.getLbInfo().setHorizontalAlignment(SwingConstants.CENTER); // con estilo
			buscaCatPanel.getLbInfo().setHorizontalAlignment(SwingConstants.CENTER);
		}

		catTableModel.setDatos(catList);// lista de Cat -> modelo de tabla
		catTableModel.fireTableDataChanged(); // refrescar tabla

	}

	/*
	 * CAMBIOS DE PANELES Y CARGA DE VENTANAS Decid� implementar as� la
	 * instanciaci�n y carga de paneles para usar la memoria lo m�nimo posible: 1)
	 * Cada vez que se carga un panel, se comprueba si est� instanciado 2) Si no
	 * est� instanciado se instancia y se guarda la direcci�n de memoria en los
	 * atributos del controller 3) Si ya est� instanciado, se usan las referencias
	 * del controller
	 */

	/**
	 * Cambia paneles de la aplicaci�n
	 * 
	 * @param el panel que se quiere poner
	 */
	private void changePanel(JPanel panel) {
		mainWindow.contentPane.removeAll(); // elimina paneles de la ventana principal
		mainWindow.loadMenu(this); // carga el men�
		mainWindow.contentPane.add(panel); // a�ade el panel que queremos

		setPanelEnUso(panel); // se establece como panel en uso

		mainWindow.revalidate(); // se vuelven a pintar los paneles y sus componentes
		mainWindow.repaint();
	}

	private void loadBuscaCatPanel() {
		BuscaCatPanel buscaCatPanel;

		if (getBuscaCatPanel() == null) {
			buscaCatPanel = createBuscaCatPanel();
			setBuscaCatPanel(buscaCatPanel);
		} else {
			buscaCatPanel = getBuscaCatPanel(); // si ya estaba instanciado se coge del controller
		}

		changePanel(buscaCatPanel);
	}

	private void loadAltaCatPanel() {
		AltaCatPanel altaCatPanel;

		if (getAltaCatPanel() == null) {
			altaCatPanel = createAltaCatPanel();
			setAltaCatPanel(altaCatPanel);
		} else {
			altaCatPanel = getAltaCatPanel(); // si ya estaba instanciado se coge del controller
		}

		changePanel(altaCatPanel);

	}

	public void loadMainPanel(MainWindow mainWindow, VKController controller) {
		MainPanel mainPanel;

		if (getMainPanel() == null) {
			mainPanel = createMainPanel(controller);
			setMainPanel(mainPanel);
		} else {
			mainPanel = getMainPanel();
		}

		// changePanel(mainPanel);

		mainWindow.contentPane.removeAll();
		mainWindow.loadMenu(this);
		mainWindow.contentPane.add(mainPanel);

		// se establece como panel en uso
		setPanelEnUso(mainPanel);

		mainWindow.revalidate();
		mainWindow.repaint();
	}

	public void loadAltaVetPanel(MainWindow mainWindow) {
		AltaVetPanel altaPanel;

		if (getAltaVetPanel() == null) {
			altaPanel = createAltaVetPanel();
			setAltaVetPanel(altaPanel);
		} else {
			altaPanel = getAltaVetPanel();
		}

		changePanel(altaPanel);

	}

	public void loadBuscaVetPanel(MainWindow mainWindow) {
		LoginVetPanel buscaVetPanel;
		if (getBuscaVetPanel() == null) {
			buscaVetPanel = createBuscaVetPanel();
			setBuscaVetPanel(buscaVetPanel);
		} else {
			buscaVetPanel = getBuscaVetPanel();
		}

		changePanel(buscaVetPanel);

	}

	public static void loadAboutWindow(VKController controller) {
		AboutWindow aboutWindow;

		if (controller.getAboutWindow() == null) {
			aboutWindow = controller.createAboutWindow(controller);
			controller.setAboutWindow(aboutWindow);
		} else {
			aboutWindow = controller.getAboutWindow();
		}

		aboutWindow.setVisible(true);
	}

	public void loadLoginWindow() {

		if (loginWindow == null) {
			loginWindow = createLoginWindow();
		}
		loginWindow.setVisible(true);

	}

}
