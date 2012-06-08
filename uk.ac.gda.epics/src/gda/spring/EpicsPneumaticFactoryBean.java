/*-
 * Copyright © 2009 Diamond Light Source Ltd.
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

package gda.spring;

import gda.configuration.epics.ConfigurationNotFoundException;
import gda.device.enumpositioner.EpicsPneumaticCallback;
import gda.epics.interfaces.PneumaticCallbackType;

import org.springframework.beans.factory.FactoryBean;

/**
 * A {@link FactoryBean} for creating {@link EpicsPneumaticCallback} objects.
 */
public class EpicsPneumaticFactoryBean extends EpicsConfigurationFactoryBeanBase<EpicsPneumaticCallback> {

	private String deviceName;
	
	/**
	 * Sets the EPICS device name which will be used to obtain the PV record
	 * name.
	 * 
	 * @param deviceName the EPICS device name
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	@Override
	public Class<?> getObjectType() {
		return EpicsPneumaticCallback.class;
	}

	private EpicsPneumaticCallback epicsPneumaticCallback;
	
	@Override
	protected void createObject() throws ConfigurationNotFoundException {
		PneumaticCallbackType config = getEpicsConfiguration().getConfiguration(deviceName, PneumaticCallbackType.class);
		epicsPneumaticCallback = new EpicsPneumaticCallback();
		epicsPneumaticCallback.setName(name);
		epicsPneumaticCallback.setPvNames(config);
	}

	@Override
	public EpicsPneumaticCallback getObject() throws Exception {
		return epicsPneumaticCallback;
	}

}
