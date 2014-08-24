/**
 * 
 */
package application.customControls;

import java.io.IOException;

/**
 * @author lumpiluk
 *
 */
public abstract class AbstractForm extends AbstractControl {

	public static enum Mode {
		DISPLAY, // form disabled, just show customers selected in table
		ADD, // form enabled, after pressing OK new customer will be added
		EDIT; // form enabled, after pressing OK selected customer will be updated
	}
	
	/**
	 * @throws IOException
	 */
	public AbstractForm() throws IOException {
		super();
	}

}
