package ca.nengo.ui;

import ca.nengo.config.ConfigUtil;
import ca.nengo.model.Network;
import ca.nengo.model.Node;
import ca.nengo.model.Source;
import ca.nengo.model.Target;
import ca.nengo.ui.actions.*;
import ca.nengo.ui.data.DataListView;
import ca.nengo.ui.lib.AppFrame;
import ca.nengo.ui.lib.AuxillarySplitPane;
import ca.nengo.ui.lib.style.NengoStyle;
import ca.nengo.ui.lib.actions.*;
import ca.nengo.ui.lib.misc.ShortcutKey;
import ca.nengo.ui.lib.objects.models.ModelObject;
import ca.nengo.ui.lib.util.UIEnvironment;
import ca.nengo.ui.lib.util.Util;
import ca.nengo.ui.lib.util.menus.MenuBuilder;
import ca.nengo.ui.lib.world.WorldObject;
import ca.nengo.ui.lib.world.elastic.ElasticWorld;
import ca.nengo.ui.lib.world.handlers.MouseHandler;
import ca.nengo.ui.lib.world.handlers.SelectionHandler;
import ca.nengo.ui.lib.world.piccolo.primitives.Universe;
import ca.nengo.ui.models.NodeContainer;
import ca.nengo.ui.models.UINeoNode;
import ca.nengo.ui.models.constructors.CNetwork;
import ca.nengo.ui.models.nodes.UINetwork;
import ca.nengo.ui.models.nodes.widgets.UIProbe;
import ca.nengo.ui.models.nodes.widgets.UIProjection;
import ca.nengo.ui.models.nodes.widgets.Widget;
import ca.nengo.ui.util.*;
import ca.nengo.util.Environment;
import org.simplericity.macify.eawt.Application;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by me on 2/23/15.
 */
public class AbstractNengo extends AppFrame implements NodeContainer {
    /**
     * Use the configure panel in the right side? Otherwise it's a pop-up.
     */
    public static final boolean CONFIGURE_PLANE_ENABLED = true;
    /**
     * File extension for Nengo Nodes
     */
    public static final String NEONODE_FILE_EXTENSION = "nef";
    private static final long serialVersionUID = 1L;
    /**
     * UI delegate object used to show the FileChooser
     */
    public static NeoFileChooser FileChooser;
    private NengoClipboard clipboard;

    protected DataListView dataListViewer;
    protected  ConfigurationPane configPane;
    protected AuxillarySplitPane dataViewerPane;
    protected ArrayList<AuxillarySplitPane> splitPanes;
    private ProgressIndicator progressIndicator;

    boolean confirmExit = false;

    public AbstractNengo() {
        super();
    }

    /**
     * @return The singleton instance of the NengoGraphics object
     */
    public static AbstractNengo getInstance() {
        Util.Assert(UIEnvironment.getInstance() instanceof AbstractNengo);
        return (AbstractNengo)UIEnvironment.getInstance();
    }

    public void setApplication(Application application) {
        application.addApplicationListener(this);
        application.setEnabledPreferencesMenu(false);
        BufferedImage icon = new BufferedImage(256,256,BufferedImage.TYPE_INT_ARGB);
        try {
            icon = ImageIO.read(getClass().getClassLoader().getResource("ca/nengo/ui/nengologo256.png"));
            setIconImage(icon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        application.setApplicationIconImage(icon);
    }



    /**
     * @return Top Node Container available in the Application Window. Null, if
     *          the Top Window is not a Node Container
     */
    protected NodeContainer getRoot() {
        ca.nengo.ui.lib.world.piccolo.objects.Window window = getTopWindow();
        NodeContainer nodeContainer = null;

        if (window != null) {
            WorldObject wo = window.getContents();
            if (wo instanceof NodeContainer) {
                nodeContainer = (NodeContainer) wo;
            }
        } else {
            nodeContainer = this;
        }

        return nodeContainer;
    }

    @Override
    protected void initialize() {
        clipboard = new NengoClipboard();
        clipboard.addClipboardListener(new NengoClipboard.ClipboardListener() {

            public void clipboardChanged() {
                updateEditMenu();
            }

        });

        SelectionHandler.addSelectionListener(new SelectionHandler.SelectionListener() {
            public void selectionChanged(Collection<WorldObject> objs) {
                updateEditMenu();
                updateRunMenu();
                //updateScriptConsole();
                updateConfigurationPane();
            }
        });

        super.initialize();

        UIEnvironment.setDebugEnabled(false);

        //initializeSimulatorSourceFiles();

        if (FileChooser == null) {
            FileChooser = new NeoFileChooser();
        }

        /// Set up Environment variables
        Environment.setUserInterface(true);

        /// Register plugin classes
        //		registerPlugins();

        setExtendedState(NengoConfigManager.getUserInteger(NengoConfigManager.UserProperties.NengoWindowExtendedState,
                JFrame.MAXIMIZED_BOTH));
    }

//    /**
//     * Find and initialize the main simulator source code
//     */
//    private void initializeSimulatorSourceFiles() {
//
//        String savedSourceLocation = NengoConfigManager.getNengoConfig().getProperty("simulator_source");
//
//        String simulatorSource = (savedSourceLocation != null) ? savedSourceLocation
//                : "../simulator/src/java/main";
//
//        File simulatorSourceFile = new File(simulatorSource);
//        if (!simulatorSourceFile.exists()) {
//            Util.debugMsg("Could not find simulator source files at "
//                    + simulatorSourceFile.getAbsoluteFile().toString());
//        }
//
//        JavaSourceParser.addSource(simulatorSourceFile);
//    }

    @Override
    protected void initLayout(Universe canvas) {
        System.setProperty("swing.aatext", "true");


        canvas.requestFocus();

        progressIndicator=new ProgressIndicator();
        getContentPane().add(progressIndicator, BorderLayout.SOUTH);
    }

    protected AuxillarySplitPane getDataViewer() {
        return new AuxillarySplitPane(new JPanel(), dataListViewer,
                "Data Viewer", AuxillarySplitPane.Orientation.Left);
    }

    protected NengoWorld getNengoWorld() {
        return (NengoWorld) getWorld();
    }

    @Override
    protected void constructShortcutKeys(List<ShortcutKey> shortcuts) {
        super.constructShortcutKeys(shortcuts);
    }

    /**
     * Prompt user to save models in NengoGraphics.
     * This is most likely called right before the application is exiting.
     */
    protected boolean promptToSaveModels() {
        boolean saveSuccessful = true;

        for (WorldObject wo : getWorld().getGround().getChildren()) {
            if (wo instanceof UINeoNode) {
                SaveNodeAction saveAction = new SaveNodeAction((UINeoNode) wo, true);
                saveAction.doAction();
                saveSuccessful = saveSuccessful && saveAction.getSaveSuccessful();
            }
        }

        return saveSuccessful;
    }

    @Override
    protected void updateEditMenu() {
        super.updateEditMenu();

        StandardAction copyAction = null;
        StandardAction cutAction = null;
        StandardAction pasteAction = null;
        StandardAction removeAction = null;

        Collection<WorldObject> selectedObjects = SelectionHandler.getActiveSelection();

        if (selectedObjects != null && selectedObjects.size() > 0) {
            ArrayList<UINeoNode> selectedArray = new ArrayList<UINeoNode>();
            ArrayList<ModelObject> selectedModelObjects = new ArrayList<ModelObject>();
            for (WorldObject obj : selectedObjects) {
                if (obj instanceof UINeoNode) {
                    selectedArray.add((UINeoNode)obj);
                }
                if (obj instanceof ModelObject) {
                    selectedModelObjects.add((ModelObject)obj);
                }
            }

            cutAction = new CutAction("Cut", selectedArray);
            copyAction = new CopyAction("Copy", selectedArray);
            removeAction = new RemoveModelAction("Remove", selectedModelObjects);
        } else {
            cutAction = new DisabledAction("Cut", "No object selected");
            copyAction = new DisabledAction("Copy", "No object selected");
            removeAction = new DisabledAction("Remove", "No objects to remove");
        }

        if (getClipboard().hasContents()) {
            pasteAction = new StandardAction("Paste") {
                private static final long serialVersionUID = 1L;
                @Override
                protected void action() {
                    // look for the active mouse handler. If it exists, it should contain
                    // the current mouse position (from the mousemoved event), so use this
                    // to create a new PasteEvent
                    PasteAction a;
                    MouseHandler mh = MouseHandler.getActiveMouseHandler();
                    if (mh != null) {
                        a = new PasteAction("Paste", (NodeContainer)mh.getWorld(), true);
                        Point2D pos = mh.getMouseMovedRelativePosition();
                        if (pos != null) {
                            a.setPosition(pos.getX(), pos.getY());
                        }
                    } else {
                        a = new PasteAction("Paste", AbstractNengo.getInstance(), true);
                    }
                    a.doAction();
                }
            };
        } else {
            pasteAction = new DisabledAction("Paste", "No object is in the clipboard");
        }

        editMenu.addAction(copyAction, KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C,
                MENU_SHORTCUT_KEY_MASK));
        editMenu.addAction(cutAction, KeyEvent.VK_X, KeyStroke.getKeyStroke(KeyEvent.VK_X,
                MENU_SHORTCUT_KEY_MASK));
        editMenu.addAction(pasteAction, KeyEvent.VK_V, KeyStroke.getKeyStroke(KeyEvent.VK_V,
                MENU_SHORTCUT_KEY_MASK));
        editMenu.addAction(removeAction, KeyEvent.VK_R, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,
                0));

    }

    @Override
    protected void updateRunMenu() {
        super.updateRunMenu();

        StandardAction simulateAction = null;
        //StandardAction interactivePlotsAction = null;
        UINeoNode node = null;
        WorldObject selectedObj = SelectionHandler.getActiveObject();

        if (selectedObj != null) {
            if (selectedObj instanceof UINeoNode) {
                node = (UINeoNode) selectedObj;
            } else if (selectedObj instanceof UIProjection) {
                if (((UIProjection) selectedObj).getTermination() != null) {
                    node = ((UIProjection) selectedObj).getTermination().getNodeParent();
                } else {
                    node = ((UIProjection) selectedObj).getOriginUI().getNodeParent();
                }
            } else if (selectedObj instanceof Widget){
                node = ((Widget) selectedObj).getNodeParent();
            } else if (selectedObj instanceof UIProbe) {
                node = ((UIProbe) selectedObj).getProbeParent();
            }
        }

        if (node != null) {
            while (node.getNetworkParent() != null) {
                node = node.getNetworkParent();
            }
            if (node instanceof UINetwork) {
                UINetwork network = (UINetwork) node;

                simulateAction = new RunSimulatorAction("Simulate " + network.getName(), network);
                //interactivePlotsAction = new RunInteractivePlotsAction(network);
            }
            else {
                throw new RuntimeException("Nodes not in a network");
            }
        } else {
            simulateAction = new DisabledAction("Simulate", "No object selected");
            //interactivePlotsAction = new DisabledAction("Interactive Plots", "No object selected");
        }

        runMenu.addAction(simulateAction, KeyEvent.VK_F4, KeyStroke.getKeyStroke(KeyEvent.VK_F4,
                0));
        //runMenu.addAction(interactivePlotsAction, KeyEvent.VK_F5, KeyStroke.getKeyStroke(KeyEvent.VK_F5,
          //      0));
    }

    @Override
    protected ElasticWorld createWorld() {
        return new NengoWorld();
    }

    /**
     * @see ca.nengo.ui.models.NodeContainer#addNodeModel(ca.nengo.model.Node)
     */
    public UINeoNode addNodeModel(Node node) throws ContainerException {
        return addNodeModel(node, null, null);
    }

    public UINeoNode addNodeModel(Node node, Double posX, Double posY) throws ContainerException {
        NodeContainer nodeContainer = getRoot();
        if (nodeContainer != this && nodeContainer != null) {
            // Delegate to the top Node Container in the Application
            return nodeContainer.addNodeModel(node, posX, posY);
        } else if (nodeContainer == this) {
            UINeoNode nodeUI = getNengoWorld().addNodeModel(node, posX, posY);
            try {
                DragAction.dropNode(nodeUI);
            } catch (UserCancelledException e) {
                // the user should not be given a chance to do this
                throw new ContainerException("Unexpected cancellation of action by user");
            }
            return nodeUI;
        } else {
            throw new ContainerException("There are no containers to put this node");
        }
    }

    @Override
    public WorldObject addNodeModel(WorldObject obj, Double posX, Double posY) throws ContainerException {
        getNengoWorld().addNodeModel(obj, posX, posY);
        return obj;
    }

    /**
     * @param network TODO
     */
    public void captureInDataViewer(Network network) {
        dataListViewer.captureSimulationData(network);
    }

    /**
     * @param obj Object to configure
     */
    public void configureObject(Object obj) {
        if (CONFIGURE_PLANE_ENABLED) {
            configPane.toJComponent().setAuxVisible(true);
            configPane.configureObj(obj);
        } else {
            ConfigUtil.configure(UIEnvironment.getInstance(), obj);
        }
    }

    @Override
    public void exitAppFrame() {
        if (confirmExit && getWorld().getGround().getChildrenCount() > 0)
        {
            int response = JOptionPane.showConfirmDialog(this,
                    "Save models?",
                    "Exiting " + getAppName(),
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (response == JOptionPane.YES_OPTION)
            {
                if (!promptToSaveModels()) {
                    return;
                }
            }
            else if (response == JOptionPane.CANCEL_OPTION ||response == JOptionPane.CLOSED_OPTION)
            {
                // cancel exit
                return;
            }
        }

        saveUserConfig();
        super.exitAppFrame();
    }

    private void saveUserConfig() {
        NengoConfigManager.setUserProperty(NengoConfigManager.UserProperties.NengoWindowExtendedState,
                getExtendedState());

        NengoConfigManager.saveUserConfig();
    }

    @Override
    public String getAboutString() {
        return "";
    }

    @Override
    public String getAppName() {
        return "";
    }

    public String getAppWindowTitle() {
        return "";
    }

    /**
     * @return TODO
     */
    public NengoClipboard getClipboard() {
        return clipboard;
    }

    public Node getNodeModel(String name) {
        NodeContainer nodeContainer = getRoot();
        if (nodeContainer != this && nodeContainer != null) {
            // Delegate to the top Node Container in the Application
            return nodeContainer.getNodeModel(name);
        } else if (nodeContainer == this) {
            return getNengoWorld().getNodeModel(name);
        }
        return null;
    }


    public ProgressIndicator getProgressIndicator() {
        return progressIndicator;
    }



    @Override
    public void initFileMenu(MenuBuilder fileMenu) {

        fileMenu.addAction(new CreateModelAction("New Network", this, new CNetwork()));


        fileMenu.getJMenu().addSeparator();

        fileMenu.addAction(new SaveNetworkAction("Save Selected Network"),
                KeyEvent.VK_S,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_SHORTCUT_KEY_MASK));


        fileMenu.addAction(new GenerateScriptAction("Generate Script"),
                KeyEvent.VK_G,
                KeyStroke.getKeyStroke(KeyEvent.VK_G, MENU_SHORTCUT_KEY_MASK));

        fileMenu.getJMenu().addSeparator();

        fileMenu.addAction(new ClearAllAction("Clear all"));

        fileMenu.getJMenu().addSeparator();
    }



    public Point2D localToView(Point2D localPoint) {
        return getNengoWorld().localToView(localPoint);
    }

    /**
     * @param node TODO
     * @return TODO
     */
    public boolean removeNodeModel(Node node) {
        ModelObject modelToDestroy = null;
        for (WorldObject wo : getWorld().getGround().getChildren()) {
            if (wo instanceof ModelObject) {
                ModelObject modelObject = (ModelObject) wo;

                if (modelObject.getModel() == node) {
                    modelToDestroy = modelObject;
                    break;
                }
            }
        }
        if (modelToDestroy != null) {
            modelToDestroy.destroyModel();
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param visible TODO
     */
    public void setDataViewerPaneVisible(boolean visible) {
        if (dataViewerPane.isAuxVisible() != visible) {
            (new ToggleScriptPane(null, dataViewerPane)).doAction();
        }
    }

    /**
     * @param isVisible TODO
     */
    public void setDataViewerVisible(boolean isVisible) {
        dataViewerPane.setAuxVisible(isVisible);
    }

    /**
     * @return the configuration (inspector) pane
     */
    public ConfigurationPane getConfigPane() {
        return configPane;
    }

    protected void updateConfigurationPane() {
        if (configPane!=null) {
            if (configPane.toJComponent().isAuxVisible()) {
                configPane.configureObj(SelectionHandler.getActiveModel());
            }
        }
    }

    public void toggleConfigPane() {
        AuxillarySplitPane pane = configPane.toJComponent();
        pane.setAuxVisible(!pane.isAuxVisible());
        updateConfigurationPane();
    }


    /**
     * TODO
     *
     * @author TODO
     */
    public static class ToggleScriptPane extends StandardAction {

        private static final long serialVersionUID = 1L;
        private final AuxillarySplitPane splitPane;

        /**
         * @param description TODO
         * @param spliPane TODO
         */
        public ToggleScriptPane(String description, AuxillarySplitPane spliPane) {
            super(description);
            this.splitPane = spliPane;
        }

        @Override
        protected void action() throws ActionException {
            splitPane.setAuxVisible(!splitPane.isAuxVisible());
        }

    }

    static class ConfigurationPane {
        final AuxillarySplitPane auxSplitPane;
        Object currentObj;

        public ConfigurationPane(Container mainPanel) {
            super();
            auxSplitPane = new AuxillarySplitPane(mainPanel, null, "Inspector",
                    AuxillarySplitPane.Orientation.Right);
            auxSplitPane.getAuxPaneWrapper().setBackground(NengoStyle.COLOR_CONFIGURE_BACKGROUND);
            currentObj=null;
        }

        public Object getCurrentObj() {
            return currentObj;
        }

        public void configureObj(Object obj) {
            if (obj==currentObj) {
                return;
            }
            currentObj=obj;

            int location=auxSplitPane.getDividerLocation();

            if (obj==null) {
                ConfigUtil.ConfigurationPane configurationPane = ConfigUtil.createConfigurationPane(obj);
                configurationPane.getTree().setBackground(NengoStyle.COLOR_CONFIGURE_BACKGROUND);
                auxSplitPane.setAuxPane(configurationPane,"Inspector");
            } else {
                ConfigUtil.ConfigurationPane configurationPane = ConfigUtil.createConfigurationPane(obj);
                configurationPane.getTree().setBackground(NengoStyle.COLOR_CONFIGURE_BACKGROUND);

                // style.applyStyle(configurationPane.getTree());
                // style.applyStyle(configurationPane.getCellRenderer());

                String name;
                if (obj instanceof Node) {
                    name = ((Node) obj).getName();
                } else if (obj instanceof Target) {
                    name = ((Target) obj).getName();
                } else if (obj instanceof Source) {
                    name = ((Source) obj).getName();
                } else {
                    name = "Inspector";
                }
                auxSplitPane.setAuxPane(configurationPane, name + " (" + obj.getClass().getSimpleName()
                        + ')');
            }
            auxSplitPane.setDividerLocation(location);

        }

        public AuxillarySplitPane toJComponent() {
            return auxSplitPane;
        }
    }
}
