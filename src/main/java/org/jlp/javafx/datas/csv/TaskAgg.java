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
package org.jlp.javafx.datas.csv;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.jlp.javafx.common.StrategyPeriod;
import org.jlp.javafx.datas.AggDataDated;

// TODO: Auto-generated Javadoc
/**
 * The Class TaskAgg.
 */
public class TaskAgg implements Callable<Integer> {

	/** The name serie. */
	public String nameSerie;

	/** The list hms datas. */
	public List<Map<Long, AggDataDated>> listHmsDatas;

	/** The hm datas closed. */
	public Map<Long, AggDataDated> hmDatasClosed = new HashMap<Long, AggDataDated>();

	/** The close strategy. */
	public StrategyPeriod closeStrategy = StrategyPeriod.BEGIN;

	/**
	 * Instantiates a new task agg.
	 *
	 * @param name
	 *            the name
	 * @param listHmsDatas
	 *            the list hms datas
	 */
	public TaskAgg(String name, List<Map<Long, AggDataDated>> listHmsDatas) {
		this(name, listHmsDatas, StrategyPeriod.BEGIN);
	}

	/**
	 * Instantiates a new task agg.
	 *
	 * @param name
	 *            the name
	 * @param listHmsDatas
	 *            the list hms datas
	 * @param strat
	 *            the strat
	 */
	public TaskAgg(String name, List<Map<Long, AggDataDated>> listHmsDatas, StrategyPeriod strat) {
		this.closeStrategy = strat;
		this.nameSerie = name;
		this.listHmsDatas = listHmsDatas;
	}

	/**
	 * Call.
	 *
	 * @return the integer
	 * @throws Exception the exception
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Integer call() throws Exception {
		hmDatasClosed = listHmsDatas.get(0);
		if (listHmsDatas.size() > 1) {
			for (int i = 1; i < listHmsDatas.size(); i++) {
				for (Entry<Long, AggDataDated> entry : listHmsDatas.get(i).entrySet()) {
					if (hmDatasClosed.containsKey(entry.getKey())) {
						AggDataDated aggData = hmDatasClosed.get(entry.getKey()).merge(entry.getValue());
						hmDatasClosed.put(entry.getKey(), aggData);

					} else {
						hmDatasClosed.put(entry.getKey(), entry.getValue());
					}
				}
			}

		} else {
			for (Entry<Long, AggDataDated> entry : hmDatasClosed.entrySet()) {

				hmDatasClosed.put(entry.getKey(), entry.getValue());
			}
		}
		/* AggDataDated must be closed */
		for (Entry<Long, AggDataDated> entry : hmDatasClosed.entrySet()) {

			hmDatasClosed.put(entry.getKey(), entry.getValue().close(closeStrategy));
		}
		return 0;
	}

}
