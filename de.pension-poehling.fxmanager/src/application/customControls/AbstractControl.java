/**
 * 
 */
package application.customControls;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import util.Messages;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Abstract class for custom controls using FXML. This class handles loading
 * of the FXML file and provides the constructor to do so (Cannot be done in
 * the default constructor of a controller class as it might cause some kind of
 * stack overflow).<br />
 * Created with help from <a href="http://programmingwithpassion.wordpress.com/
 * 2013/07/07/creating-a-reusable-javafx-custom-control/">this guide</a>.
 * @author lumpiluk
 *
 */
public abstract class AbstractControl extends Control {

	private static final String fxmlPath = "/%s.fxml";
	
	private static final String cssPath = "/%s.css";
	
	private static final String skinClass = "%sSkin";
	
	/**
	 * Creates a new instance of the desired control.
	 * To actually create the JavaFX control the load()-method has to be called
	 * additionally!
	 * @throws IOException
	 */
	public AbstractControl() throws IOException {

	}
	
	/**
	 * Loads the FXML file and creates a new instance of the requested
	 * AbstractControl extending class.<br />
	 * (Had to be done in this way due to limitations of JavaFX and in order to
	 * still be able to keep this abstract class...)
	 * @throws IOException
	 */
	public static AbstractControl getInstance(Class<? extends AbstractControl> c)
			throws IOException {
		System.out.println("Loading " + c.getResource(String.format(
    			fxmlPath, c.getSimpleName())));
		
		FXMLLoader loader = new FXMLLoader();
		
		loader.setLocation(c.getResource(String.format(
    			fxmlPath, c.getSimpleName())));
    	loader.setResources(Messages.getBundle());
    	
    	Node node = (Node) loader.load();
    	AbstractControl a = loader.getController();
    	
    	a.getChildren().add(node);
    	
    	return loader.getController();
	}
	
	
	/*public void load() throws IOException {	
    	this.getChildren().add((Node)loader.load());
	}*/
	
	/**
	 * Return the path to the CSS file so things are set up right
	 * @see javafx.scene.control.Control#getUserAgentStylesheet()
	 */
	@Override
	protected String getUserAgentStylesheet() {
		String css;
		//try {
			css = getClass().getResource(String.format(cssPath,
					this.getClass().getSimpleName())).toExternalForm();
		/*} catch (NullPointerException e) {
			Messages.showError(e, Messages.ErrorType.UI);
			css = null;
		}*/
		return css;
	}
	
	/**
	 * @return the default skin for this custom control. (Required when
	 * extending javafx.scene.control.Control)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Skin createDefaultSkin() {
		Skin defaultSkin;
		try {
			defaultSkin = (Skin)Class.forName(String.format(skinClass,
					this.getClass().getName()))
					.getDeclaredConstructor(Control.class).newInstance(this);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			Messages.showError(e, Messages.ErrorType.UI);
			defaultSkin = null;
		}
		return defaultSkin;
	}
	
	@Override
	protected void layoutChildren() {
	    for (Node node : getChildren()) {
	        layoutInArea(node, 0, 0, getWidth(), getHeight(), 0, HPos.LEFT, VPos.TOP);
	    }
	}
}
