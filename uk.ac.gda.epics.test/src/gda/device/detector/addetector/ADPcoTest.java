/*-
 * Copyright © 2011 Diamond Light Source Ltd.
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

package gda.device.detector.addetector;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gda.TestHelpers;
import gda.device.detector.addetector.ADDetector;
import gda.device.detector.addetector.ADPco;
import gda.device.detector.addetector.triggering.SingleExposurePco;
import gda.device.detector.areadetector.v17.ADBase.ImageMode;
import gda.device.detector.areadetector.v17.ADDriverPco;
import gda.device.detector.areadetector.v17.NDFile.FileWriteMode;
import gda.device.detector.areadetector.v17.NDPluginBase;
import gda.epics.PV;

import org.junit.Test;
import org.mockito.InOrder;

// TODO: Move file writer enable/disable test up into ADDetectorTest
public class ADPcoTest extends ADDetectorTest {

	private ADPco adPco;
	private ADDriverPco mockAdDriverPco;
	private PV<Boolean> mockArmModePv;
	private NDPluginBase mockNdFilePluginBase;


	@Override
	public ADDetector det() {
		return adPco;
	}

	public ADPco pco() {
		return adPco;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void createDetector() {
		adPco = new ADPco();
		mockAdDriverPco = mock(ADDriverPco.class);
		mockArmModePv = mock(PV.class);
		when(mockAdDriverPco.getArmModePV()).thenReturn(mockArmModePv);
		adPco.setCollectionStrategy(new SingleExposurePco(mockAdBase, mockAdDriverPco, 0.1)); // default strategy

	}

	@Override
	protected void setUpNoConfigure() throws Exception {
		createDetector();
		super.setUpNoConfigure();
		mockNdFilePluginBase = mock(NDPluginBase.class);
		when(mockNdFile.getPluginBase()).thenReturn(mockNdFilePluginBase);

	}
	
	@Override
	@Test
	public void testAtScanStart() throws Exception {
		TestHelpers.setUpTest(ADPcoTest.class, "testPrepareForCollection", true);
		det().setReadFilepath(true);
		super.testAtScanStart();
		InOrder inOrder = inOrder(mockAdBase, mockArmModePv, mockNdFile, mockNdFilePluginBase);
		
		// Triggering
		inOrder.verify(mockAdBase).stopAcquiring();
		inOrder.verify(mockAdBase).setTriggerMode(1);
		inOrder.verify(mockAdBase).setImageModeWait(ImageMode.SINGLE);
		inOrder.verify(mockAdBase).setNumImages(1);

		// Arming
		inOrder.verify(mockArmModePv).putCallback(true);
		
		// File writing
		inOrder.verify(mockNdFile).setFileName("testdet");
		inOrder.verify(mockNdFile).setFileNumber(0);
		inOrder.verify(mockNdFilePluginBase).enableCallbacks();
		inOrder.verify(mockNdFilePluginBase).setBlockingCallbacks(1);
		inOrder.verify(mockNdFile).setFileWriteMode(FileWriteMode.SINGLE);
	}
	
	@Test
	public void testAtScanEnd() throws Exception {
		pco().setReadFilepath(true);
		pco().atScanEnd();
		checkDisableFileWriter();
	}
	@Test
	public void testAtCommandFailure() throws Exception {
		pco().setReadFilepath(true);
		pco().atCommandFailure();
		checkDisableFileWriter();
	}
	@Override
	@Test
	public void testStop() throws Exception {
		pco().setReadFilepath(true);
		pco().stop();
		checkDisableFileWriter();
		verify(mockAdBase).stopAcquiring();

	}

	private void checkDisableFileWriter() throws Exception {
		InOrder inOrder = inOrder(mockNdFile, mockNdFilePluginBase);
		inOrder.verify(mockNdFilePluginBase).disableCallbacks();
		inOrder.verify(mockNdFilePluginBase).setBlockingCallbacks(0);
		inOrder.verify(mockNdFile).setFileWriteMode(FileWriteMode.STREAM);
	}

}
