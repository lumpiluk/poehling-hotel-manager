/* 
 * Copyright 2014 Lukas Stratmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
