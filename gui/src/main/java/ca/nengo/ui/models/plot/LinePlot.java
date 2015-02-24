package ca.nengo.ui.models.plot;


import ca.nengo.model.*;
import ca.nengo.model.impl.AbstractNode;
import ca.nengo.model.impl.PassthroughTermination;
import ca.nengo.ui.lib.world.PaintContext;
import ca.nengo.ui.models.UIBuilder;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.icons.EmptyIcon;
import ca.nengo.ui.models.nodes.widgets.UITermination;
import ca.nengo.util.ScriptGenException;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

public class LinePlot extends AbstractNode implements UIBuilder {

    final Termination input = new PassthroughTermination(this, "input", 1);
    private String label = "?";

    public class LinePlotUI extends UINeoNode<LinePlot> {

        public LinePlotUI() {
            super(LinePlot.this);

            setIcon(new EmptyIcon(this));
            //img = new BufferedImage(400, 200, BufferedImage.TYPE_4BYTE_ABGR);
            //setIcon(new WorldObjectImpl(new PXImage(img)));
            setBounds(0, 0, 251, 91);

            addWidget(UITermination.createTerminationUI(this, getInput()));

            repaint();

        }

        @Override
        public String getTypeName() {
            return "X";
        }



        @Override
        public void paint(PaintContext paintContext) {
            super.paint(paintContext);

            float w = 10;
            float h = 10;
            float x = (float)(Math.random() * getBounds().getWidth());
            float y = (float)(Math.random() * getBounds().getHeight());

            Graphics2D g = paintContext.getGraphics();
            g.setColor(Color.getHSBColor((float)Math.random(), (float)Math.random(), (float)Math.random()));
            g.drawOval((int)x,(int)y,(int)w,(int)h);
            //g.dispose();

            g.drawString(label, 50, 50);
        }

    }



    private Termination getInput() {
        return input;
    }

    public LinePlot(String name) {
        super(name);
    }

    @Override
    public UINeoNode newUI() {
        return new LinePlotUI();
    }


    @Override
    public void run(float startTime, float endTime) throws SimulationException {

        label = "?";

        if ((input!=null && input.getInput()!=null)) {
            InstantaneousOutput i = input.getInput();
            if (i instanceof SpikeOutput) {
                SpikeOutput so = (SpikeOutput) input.getInput();
                boolean[] v = so.getValues();
                label = (Arrays.toString(v));
            }
            else if (i instanceof RealOutput) {
                float[] v = ((RealOutput) input.getInput()).getValues();
                label = (Arrays.toString(v));
            }

        }


        //System.out.println(LinePlot.this + " " + startTime + " " + endTime);

    }

    @Override
    public void reset(boolean randomize) {

    }

    @Override
    public Node[] getChildren() {
        return new Node[0];
    }

    @Override
    public String toScript(HashMap<String, Object> scriptData) throws ScriptGenException {
        return "";
    }
}
