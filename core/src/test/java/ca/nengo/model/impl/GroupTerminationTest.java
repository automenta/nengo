/**
 * 
 */
package ca.nengo.model.impl;

import ca.nengo.TestUtil;
import ca.nengo.model.StructuralException;
import ca.nengo.model.Termination;
import junit.framework.TestCase;

/**
 * Unit tests for EnsembleTermination. 
 * 
 * @author Bryan Tripp
 */
public class GroupTerminationTest extends TestCase {

	private static float ourTau = .005f;
	private static float ourTolerance = 1e-5f;
	
	private Termination[] myNodeTerminations;
	private GroupTermination myEnsembleTermination;
	
	/**
	 * @param arg0
	 */
	public GroupTerminationTest(String arg0) {
		super(arg0);
	}

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		myNodeTerminations = new Termination[10];
		for (int i = 0; i < myNodeTerminations.length; i++) {
			myNodeTerminations[i] = new LinearExponentialTermination(null, ""+i, new float[]{1}, ourTau);
		}
		
		myEnsembleTermination = new GroupTermination(null, "test", myNodeTerminations);
	}

	/**
	 * Test method for {@link GroupTermination#setModulatory(boolean)}.
	 */
	public void testSetModulatory() {
		assertFalse(myEnsembleTermination.getModulatory());
		myNodeTerminations[0].setModulatory(true);
		assertFalse(myEnsembleTermination.getModulatory());
		
		myEnsembleTermination.setModulatory(true);
		assertTrue(myEnsembleTermination.getModulatory());
		assertTrue(myNodeTerminations[0].getModulatory());
		assertTrue(myNodeTerminations[1].getModulatory());
	}

	/**
	 * Test method for {@link GroupTermination#setTau(float)}.
	 * @throws StructuralException 
	 */
	public void testSetTau() throws StructuralException {
		TestUtil.assertClose(ourTau, myEnsembleTermination.getTau(), ourTolerance);
		myNodeTerminations[0].setTau(ourTau*2);
		assertTrue(myEnsembleTermination.getTau() > ourTau*1.01f);
		
		myEnsembleTermination.setTau(ourTau*2);
		TestUtil.assertClose(ourTau*2, myEnsembleTermination.getTau(), ourTolerance);
		TestUtil.assertClose(ourTau*2, myNodeTerminations[0].getTau(), ourTolerance);
		TestUtil.assertClose(ourTau*2, myNodeTerminations[0].getTau(), ourTolerance);
	}

}
