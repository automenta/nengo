/*
 * Created on 3-Aug-07
 */
package ca.nengo.model.impl;

import ca.nengo.TestUtil;
import ca.nengo.model.*;
import ca.nengo.util.MU;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for PassthroughNode. 
 * 
 * @author Bryan Tripp
 */
public class DirectNodeTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRun() throws SimulationException, StructuralException {
		DirectNode node1 = new DirectNode("test", 2);
		node1.getTermination(DirectNode.TERMINATION).setValues(
				new SpikeOutputImpl(new boolean[]{true, false}, Units.UNK, 0));
		node1.run(0, .01f);
		SpikeOutput out1 = (SpikeOutput) node1.getOrigin(DirectNode.ORIGIN).get();
		assertEquals(true, out1.getValues()[0]);
		assertEquals(false, out1.getValues()[1]);
		
		Map<String, float[][]> terminations2 = new HashMap<String, float[][]>(10);
		terminations2.put("a", MU.I(2));
		terminations2.put("b", MU.I(2));
		DirectNode node2 = new DirectNode("test2", 2, terminations2);
		node2.getTermination("a").setValues(new RealOutputImpl(new float[]{10, 5}, Units.UNK, 0));
		node2.getTermination("b").setValues(new RealOutputImpl(new float[]{1, 0}, Units.UNK, 0));
		node2.run(0, .01f);
		RealOutput out2 = (RealOutput) node2.getOrigin(DirectNode.ORIGIN).get();
		TestUtil.assertClose(11, out2.getValues()[0], .001f);
		TestUtil.assertClose(5, out2.getValues()[1], .001f);
		
		Map<String, float[][]> terminations3 = new HashMap<String, float[][]>(10);
		terminations3.put("a", new float[][]{new float[]{1, -1}});
		DirectNode node3 = new DirectNode("test3", 1, terminations3);
		node3.getTermination("a").setValues(new RealOutputImpl(new float[]{10, 3}, Units.UNK, 0));
		node3.run(0, .01f);
		RealOutput out3 = (RealOutput) node3.getOrigin(DirectNode.ORIGIN).get();
		TestUtil.assertClose(7, out3.getValues()[0], .001f);
	}

}
