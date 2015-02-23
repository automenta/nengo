package ca.nengo.ui.test;

import ca.nengo.model.impl.NetworkImpl;
import ca.nengo.model.neuron.impl.SpikingNeuron;
import ca.nengo.ui.Nengrow;
import ca.nengo.ui.models.nodes.UINetwork;
import ca.nengo.ui.models.viewers.NetworkViewer;


public class TestPlotNode extends Nengrow {

    //https://github.com/nengo/nengo_1.4/blob/master/simulator-ui/docs/simplenodes.rst

    @Override
    public void init() throws Exception {
        NetworkImpl network = new NetworkImpl();
        network.addNode(new SpikingNeuron(null, null, 1, 0, "A"));
        network.addNode( new SpikingNeuron(null, null, 1, 0, "B"));


        UINetwork networkUI = (UINetwork) addNodeModel(network);

        //addNodeModel(new SpikingNeuron(null, null, 1, 0, "C"));



        System.out.println(networkUI + " " + networkUI.getClass());

    }

    public static void main(String[] args) {
        new TestPlotNode();
    }
}
