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
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

public class LinePlot extends AbstractNode implements UIBuilder {

    final Termination input = new PassthroughTermination(this, "input", 1);
    private String label = "?";

    Deque<Float> history = new ConcurrentLinkedDeque<>(); //TODO use seomthing more efficient
    final int maxHistory = 128;

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

            Graphics2D g = paintContext.getGraphics();

            int nh = history.size();
            float x = 0, y = 0;
            float dx = (float) Math.ceil(getBounds().getWidth() / nh);

            float min = Float.POSITIVE_INFINITY, max = Float.NEGATIVE_INFINITY;
            Iterator<Float> hi = history.iterator();
            while (hi.hasNext()) {
                float v = hi.next();
                if (v < min) min = v;
                if (v > max) max = v;
            }

            final float bh = (float)getBounds().getHeight();

            hi = history.iterator();

            while (hi.hasNext()) {
                float v = hi.next();
                y = (v - min) / (max - min) * bh;

                g.setColor(Color.getHSBColor((float)Math.sin(v), 1, 1));
                g.fillRect((int) x, (int) y, (int) dx, (int) dx);

                System.out.println(x + ' '  + v);
                x += dx;
            }

            g.setColor(Color.WHITE);
            g.drawString(label, 10, 10);
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
                push( v[0]  ? 1f : 0f);
                label = (Arrays.toString(v));
            }
            else if (i instanceof RealOutput) {
                float[] v = ((RealOutput) input.getInput()).getValues();
                push(v[0]);
                label = (Arrays.toString(v));
            }

        }


        //System.out.println(LinePlot.this + " " + startTime + " " + endTime);

    }


    protected void push(float f) {
        history.addLast(f);
        if (history.size() == maxHistory)
            history.removeFirst();
    }
    @Override
    public void reset(boolean randomize) {

        history.clear();
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
