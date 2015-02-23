package ca.nengo.ui;

//import org.java.ayatana.ApplicationMenu;
//import org.java.ayatana.AyatanaDesktop;

import ca.nengo.ui.lib.AuxillarySplitPane;
import ca.nengo.ui.lib.Style.NengoStyle;
import ca.nengo.ui.lib.objects.models.ModelObject;
import ca.nengo.ui.lib.util.UIEnvironment;
import ca.nengo.ui.lib.util.UserMessages;
import ca.nengo.ui.lib.util.Util;
import ca.nengo.ui.lib.world.WorldObject;
import ca.nengo.ui.lib.world.handlers.SelectionHandler;
import ca.nengo.ui.lib.world.piccolo.primitives.Universe;
import ca.nengo.ui.script.ScriptConsole;
import ca.nengo.ui.util.ScriptWorldWrapper;
import org.python.util.PythonInterpreter;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.DefaultApplication;

public class NengoClassic extends AbstractNengo {

    private PythonInterpreter pythonInterpreter;
    private ScriptConsole scriptConsole;
    private AuxillarySplitPane scriptConsolePane;

    /**
     * Nengo version number
     */
    public static final double VERSION = 1.4;
    /**
     * String used in the UI to identify Nengo
     */
    public static final String APP_NAME = "Nengo V" + VERSION;
    /**
     * Description of Nengo to be shown in the "About" Dialog box
     */
    public static final String ABOUT =
            "<H3>" + APP_NAME + "</H3>"
                    + "<a href=http://www.nengo.ca>www.nengo.ca</a>"
                    + "<p>&copy; Centre for Theoretical Neuroscience (ctn.uwaterloo.ca) 2006-2012</p>"
                    + "<b>Contributors:</b> Bryan&nbsp;Tripp, Shu&nbsp;Wu, Chris&nbsp;Eliasmith, Terry&nbsp;Stewart, James&nbsp;Bergstra, "
                    + "Trevor&nbsp;Bekolay, Dan&nbsp;Rasmussen, Xuan&nbsp;Choo, Travis&nbsp;DeWolf, "
                    + "Yan&nbsp;Wu, Eric&nbsp;Crawford, Eric&nbsp;Hunsberger, Carter&nbsp;Kolbeck, "
                    + "Jonathan&nbsp;Lai, Oliver&nbsp;Trujillo, Peter&nbsp;Blouw, Pete&nbsp;Suma, Patrick&nbsp;Ji, Jeff&nbsp;Orchard</p>"
                    + "<p>This product contains several open-source libraries (copyright their respective authors). "
                    + "For more information, consult <tt>lib/library-licenses.txt</tt> in the installation directory.</p>"
                    + "<p>This product includes software developed by The Apache Software Foundation (http://www.apache.org/).</p>";


    @Override
    protected void initLayout(Universe canvas) {
        super.initLayout(canvas);

        pythonInterpreter = new PythonInterpreter();


        scriptConsole = new ScriptConsole(pythonInterpreter);
        NengoStyle.applyStyle(scriptConsole);

        splitPanes.add(scriptConsolePane);
    }

    public static NengoClassic getInstance() {
        Util.Assert(UIEnvironment.getInstance() instanceof NengoClassic);
        return (NengoClassic)UIEnvironment.getInstance();
    }

    @Override
    public String getAboutString() {
        return ABOUT;
    }

    @Override
    public String getAppName() {
        return APP_NAME;
    }

    public String getAppWindowTitle() {
        return "Nengo Workspace";
    }
    /**
     * @return TODO
     */
    public PythonInterpreter getPythonInterpreter() {
        return pythonInterpreter;
    }

    /**
     * @return the script console
     */
    public ScriptConsole getScriptConsole() {
        return scriptConsole;
    }

    /**
     * @return is the script console pane visible
     */
    public boolean isScriptConsoleVisible() {
        return scriptConsolePane.isAuxVisible();
    }

    protected void updateScriptConsole() {
        Object model = SelectionHandler.getActiveModel();
        scriptConsole.setCurrentObject(model);
    }

    protected AuxillarySplitPane getDataViewer() {
        return new AuxillarySplitPane(scriptConsolePane, dataListViewer,
                "Data Viewer", AuxillarySplitPane.Orientation.Left);
    }

    private void initScriptConsole() {
        scriptConsolePane = new AuxillarySplitPane(configPane.toJComponent(), scriptConsole,
                "Script Console", AuxillarySplitPane.Orientation.Bottom);


        scriptConsole.addVariable("world", new ScriptWorldWrapper(this));

        // add listeners
        getWorld().getGround().addChildrenListener(new WorldObject.ChildListener() {

            public void childAdded(WorldObject wo) {
                if (wo instanceof ModelObject) {
                    final ModelObject modelObject = ((ModelObject) wo);
                    //                    final Object model = modelObject.getModel();
                    final String modelName = modelObject.getName();

                    try {
                        //scriptConsole.addVariable(modelName, model);

                        modelObject.addPropertyChangeListener(WorldObject.Property.REMOVED_FROM_WORLD,
                                new WorldObject.Listener() {
                                    public void propertyChanged(WorldObject.Property event) {
                                        scriptConsole.removeVariable(modelName);
                                        modelObject.removePropertyChangeListener(WorldObject.Property.REMOVED_FROM_WORLD,
                                                this);
                                    }
                                });

                    } catch (Exception e) {
                        UserMessages.showError("Error adding network: " + e.getMessage());
                    }
                }
            }

            public void childRemoved(WorldObject wo) {
                /*
                 * Do nothing here. We don't remove the variable here directly
                 * because the network has already been destroyed and no longer
                 * has a reference to it's model.
                 */

            }

        });

    }

    @Override
    protected void initialize() {
        super.initialize();

        /// Attach listeners for Script Console
        initScriptConsole();
    }

    /**
     * Runs NengoGraphics with a default name
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Nengo");
        Application application = new DefaultApplication();

        NengoClassic ng = new NengoClassic();
        ng.setApplication(application);
        //if (AyatanaDesktop.isSupported()) {
        //	ApplicationMenu.tryInstall(ng);
        //}
    }


}
