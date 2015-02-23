/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "Probeable.java". Description: 
"An object that can be probed for a history of its state OVER THE MOST RECENT 
  NETWORK TIME STEP"

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

/*
 * Created on May 3, 2006
 */
package ca.nengo.model;

import java.util.Properties;

import ca.nengo.util.TimeSeries;

/**
 * <p>An object that can be probed for a history of its state OVER THE MOST RECENT 
 * NETWORK TIME STEP. A Probeable must declare a list of state variables via 
 * the method listStates(), and is responsible for storing store a history of these 
 * state variables covering the most recent network time step (data from past 
 * time steps can be discarded).   
 * 
 * @author Bryan Tripp
 */
public interface Probeable {
	
	/**
	 * Note that the units of TimeSeries' for a given state do not change over time (ie at different 
	 * time steps). 
	 * 
	 * CAUTION: The TimeSeries should not contain a reference to any arrays that you are going to change 
	 * later. The caller owns what you return.  
	 *  
	 * @param stateName A state variable name
	 * @return History of values for the named state variable. The history must cover the most recent
	 * 		network time step, and no more. There should be no overlap in the time points returned 
	 * 		for different steps. 
	 * @throws SimulationException if the Probeable does not have the requested state 
	 */
	public TimeSeries getHistory(String stateName) throws SimulationException;
	
	/**
	 * @return List of state variable names, eg "V", and associated descriptions
	 * 		eg "membrane potential (mV)" 
	 */
	public Properties listStates();
}
