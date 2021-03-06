/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "IntegratorExample.java". Description: 
"In this example, an Integrator network is constructed
  
  @author Shu Wu"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
 */

package ca.nengo.ui.test.depr;

import ca.nengo.model.SimulationException;
import ca.nengo.model.StructuralException;
import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.ui.AbstractNengo;
import ca.nengo.ui.actions.RunSimulatorAction;
import ca.nengo.ui.models.nodes.UINetwork;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Test Data Viewer
 * 
 * @author Shu Wu
 */
public class DataViewerTest {
	public static float tau = .05f;

	private UINetwork network;

	public void createUINetwork(AbstractNengo abstractNengo) {

		network = new UINetwork(new NetworkImpl());
		abstractNengo.getWorld().getGround().addChild(network);
		network.openViewer();
		network.getViewer().getGround().setElasticEnabled(true);


		(new Thread(new Runnable() {
			public void run() {
				try {
					buildNetwork(network.getModel());
				} catch (StructuralException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (SimulationException e) {
					e.printStackTrace();
				}

			}

		})).start();

	}

	private void buildNetwork(NetworkImpl network) throws StructuralException,
			InterruptedException, SimulationException {
		TestUtil.buildNetwork(network);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				doPostUIStuff();
			}
		});

	}

	private void doPostUIStuff() {
		RunSimulatorAction simulatorRunner = new RunSimulatorAction("Run",
				network, 0f, 1f, 0.0002f);
		simulatorRunner.doAction();

		AbstractNengo.getInstance().setDataViewerPaneVisible(true);
	}

	// private UIStateProbe integratorProbe;

	public static void main(String[] args) {
		new DataViewerTest();
	}

	public DataViewerTest() {
		try {
			run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.getTargetException().printStackTrace();
		}

	}

	private void run() throws InterruptedException, InvocationTargetException {

		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
					createUINetwork(new AbstractNengo());

			}
		});

	}

}
