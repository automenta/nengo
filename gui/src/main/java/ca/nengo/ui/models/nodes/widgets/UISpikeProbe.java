/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "UISpikeProbe.java". Description: 
""

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

package ca.nengo.ui.models.nodes.widgets;

import ca.nengo.model.Group;
import ca.nengo.ui.actions.PlotSpikePattern;
import ca.nengo.ui.lib.util.menus.PopupMenuBuilder;
import ca.nengo.ui.models.nodes.UIGroup;
import ca.nengo.ui.models.tooltips.TooltipBuilder;

public class UISpikeProbe extends UIProbe {

	public UISpikeProbe(UIGroup nodeAttachedTo) {
		super(nodeAttachedTo, nodeAttachedTo.getModel());

		getProbeParent().showPopupMessage("Collecting spikes on " + getProbeParent().getName());
		getModel().collectSpikes(true);

		// setProbeColor(ProbeIcon.SPIKE_PROBE_COLOR);
	}

	@Override
	protected void constructTooltips(TooltipBuilder tooltips) {
		super.constructTooltips(tooltips);
		tooltips.addProperty("Attached to", getModel().getName());
	}

	@Override
	public Group getModel() {
		return (Group) super.getModel();
	}

	@Override
	public UIGroup getProbeParent() {
		return (UIGroup) super.getProbeParent();
	}

	@Override
	public String getTypeName() {
		return "Spike Collector";
	}

	@Override
	protected void constructMenu(PopupMenuBuilder menu) {
		super.constructMenu(menu);

		if (getModel().getSpikePattern() != null) {
			menu.addAction(new PlotSpikePattern(getModel().getSpikePattern()));
		}
	}

	@Override
	protected void prepareToDestroyModel() {
		getModel().collectSpikes(false);

		getProbeParent().showPopupMessage(
				"Spike collection stopped on " + getProbeParent().getName());

		super.prepareToDestroyModel();
	}

	@Override
	public void doubleClicked() {
		(new PlotSpikePattern(getModel().getSpikePattern())).doAction();
	}
}
