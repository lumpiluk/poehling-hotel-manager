/**
 * 
 */
package application.customControls;

import java.io.IOException;

import util.Messages;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Region;

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
public abstract class AbstractControl extends Region {

	private static final String fxmlPath = "/%s.fxml";
	
	public AbstractControl() throws IOException {

	}
	
	public void load() throws IOException {
		FXMLLoader loader = new FXMLLoader();
    	loader.setLocation(getClass().getResource(String.format(
    			fxmlPath, this.getClass().getSimpleName())));
    	loader.setResources(Messages.getBundle());
    	this.getChildren().add((Node)loader.load());
	}
	
	@Override
	protected void layoutChildren() {
	    for (Node node : getChildren()) {
	        layoutInArea(node, 0, 0, getWidth(), getHeight(), 0, HPos.LEFT, VPos.TOP);
	    }
	}
}
