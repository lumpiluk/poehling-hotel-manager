/**
 * 
 */
package application.customControls;

import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;

/**
 * Skin class for the calendar pane. Required by JavaFX for custom controls.
 * @author lumpiluk
 */
@SuppressWarnings("rawtypes")
public class CustomerPaneSkin extends SkinBase {

	/**
	 * @param control
	 */
	@SuppressWarnings("unchecked")
	public CustomerPaneSkin(Control control) {
		super(control);
	}

}
