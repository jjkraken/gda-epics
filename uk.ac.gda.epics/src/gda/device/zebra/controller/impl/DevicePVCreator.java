/*-
 * Copyright © 2013 Diamond Light Source Ltd.
 *
 * This file is part of GDA.
 *
 * GDA is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 *
 * GDA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with GDA. If not, see <http://www.gnu.org/licenses/>.
 */

package gda.device.zebra.controller.impl;

import gda.epics.LazyPVFactory;
import gda.epics.PV;

import java.util.HashMap;
import java.util.Map;

public class DevicePVCreator {
	protected Map<String, PV<Double>> pvDoubles= new HashMap<String, PV<Double>>();
	private String deviceprefix;
	
	public PV<Double> getPVDouble(String suffix){
		PV<Double> pv = pvDoubles.get(suffix);
		if( pv == null){
			pv = LazyPVFactory.newDoublePV(deviceprefix + suffix);
			pvDoubles.put(suffix, pv);
		}
		return pv;
	}
	
	protected Map<String, PV<Integer>> pvIntegers= new HashMap<String, PV<Integer>>();
	
	public PV<Integer> getPVInteger(String suffix){
		PV<Integer> pv = pvIntegers.get(suffix);
		if( pv == null){
			pv = LazyPVFactory.newIntegerPV(deviceprefix + suffix);
			pvIntegers.put(suffix, pv);
		}
		return pv;
	}

	public DevicePVCreator(String zebraPrefix) {
		super();
		this.deviceprefix = zebraPrefix;
	}
}
