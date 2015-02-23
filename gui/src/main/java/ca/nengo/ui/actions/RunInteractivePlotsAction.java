package ca.nengo.ui.actions;

import ca.nengo.sim.Simulator;
import ca.nengo.ui.NengoClassic;
import ca.nengo.ui.lib.actions.ActionException;
import ca.nengo.ui.lib.actions.StandardAction;
import ca.nengo.ui.models.nodes.UINetwork;
import org.python.util.PythonInterpreter;

/**
 * TODO
 * 
 * @author TODO
 */
public class RunInteractivePlotsAction extends StandardAction {
    private static final long serialVersionUID = 1L;
    private UINetwork uiNetwork;

    /**
     * @param uiNetwork TODO
     */
    public RunInteractivePlotsAction(UINetwork uiNetwork) {
        super("Run interactive plots","Interactive Plots");
        this.uiNetwork = uiNetwork;
    }

    protected void action() throws ActionException {
        Simulator simulator = uiNetwork.getSimulator();
        PythonInterpreter pi = NengoClassic.getInstance().getPythonInterpreter();

        simulator.resetNetwork(false, true);
        pi.set("_interactive_network", uiNetwork);
        pi.exec("import timeview");
        pi.exec("reload(timeview)");
        pi.exec("timeview.View(_interactive_network.model,ui=_interactive_network.viewerEnsured)");
        pi.exec("del _interactive_network");
        
    }
}
