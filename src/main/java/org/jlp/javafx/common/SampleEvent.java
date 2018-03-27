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
 * The Class SampleEvent.
 */
public class SampleEvent extends Event {
	
	/** The deb sample. */
	public Date debSample;
	
	/** The fin sample. */
	public Date finSample;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The Constant SAMPLE_EVENT. */
	public static final EventType<SampleEvent> SAMPLE_EVENT = new EventType<>(Event.ANY, "SAMPLE_EVENT");

	/**
	 * Instantiates a new sample event.
	 *
	 * @param deb the deb
	 * @param fin the fin
	 */
	public SampleEvent(Date deb, Date fin) {
		super(SAMPLE_EVENT);
		this.debSample = deb;
		this.finSample = fin;
		// TODO Auto-generated constructor stub
	}

}
