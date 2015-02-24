package ca.nengo.ui.models.plot;


import ca.nengo.model.Node;
import ca.nengo.model.SimulationException;
import ca.nengo.model.impl.AbstractNode;
import ca.nengo.ui.lib.world.PaintContext;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.icons.EmptyIcon;
import ca.nengo.util.ScriptGenException;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;

public class LinePlotUI extends UINeoNode {


    public LinePlotUI(String name) {
        super(null);
        setModel(new AbstractNode(name, Collections.EMPTY_LIST, Collections.EMPTY_LIST) {
            @Override
            public void run(float startTime, float endTime) throws SimulationException {

                System.out.println(LinePlotUI.this + " " + startTime + " "+ endTime);
                repaint();
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
        });
        setIcon(new EmptyIcon(this));
        //img = new BufferedImage(400, 200, BufferedImage.TYPE_4BYTE_ABGR);
        //setIcon(new WorldObjectImpl(new PXImage(img)));
        setBounds(0, 0,251,91);



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

    }
}
