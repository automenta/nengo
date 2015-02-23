package ca.nengo.ui.lib.world;

import java.awt.*;

public class PaintContext {
	private Graphics2D graphics;
	private double scale;

	public PaintContext(Graphics2D graphics, double scale) {
		super();
		this.graphics = graphics;
		this.scale = scale;
	}

	public Graphics2D getGraphics() {
		return graphics;
	}

	public double getScale() {
		return scale;
	}

}
