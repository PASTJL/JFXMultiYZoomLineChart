/*
 * Copyright 2017 Jean-Louis Pasturel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/
package org.jlp.javafx.common;

import java.util.Date;

import javafx.event.Event;
import javafx.event.EventType;

// TODO: Auto-generated Javadoc
/**
 * The Class ZoomEvent.
 */
public class ZoomEvent extends Event {
	
	/** The deb. */
	public Date deb;
	
	/** The fin. */
	public Date fin;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The Constant ZOOM_EVENT. */
	public static final EventType<ZoomEvent> ZOOM_EVENT = new EventType<>(Event.ANY, "ZOOM_EVENT");

	/**
	 * Instantiates a new zoom event.
	 *
	 * @param deb the deb
	 * @param fin the fin
	 */
	public ZoomEvent(Date deb, Date fin) {
		super(ZOOM_EVENT);
		this.deb = deb;
		this.fin = fin;
		// TODO Auto-generated constructor stub
	}

}
