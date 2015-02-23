package ca.nengo.ui.lib.world.handlers;

import ca.nengo.ui.lib.world.piccolo.WorldImpl;
import org.piccolo2d.event.PInputEvent;

import java.text.NumberFormat;

/**
 * Shows the mouse coordinates using the status bar
 * 
 * @author Shu Wu
 */
public class TopWorldStatusHandler extends AbstractStatusHandler {

	public TopWorldStatusHandler(WorldImpl world) {
		super(world);
	}

	/**
	 * @param event
	 *            Input event
	 * @return String related to that event
	 */
	@Override
	protected String getStatusMessage(PInputEvent event) {
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMaximumFractionDigits(2);
		return "Top Window";
	}

}