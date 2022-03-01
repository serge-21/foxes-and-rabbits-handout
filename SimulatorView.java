import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;
import java.awt.Dimension;
import java.util.function.*;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * setColor method.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public class SimulatorView extends JFrame
{
    private static Color EMPTY_COLOR = Color.white;               // Colors used for empty locations.
    private static Color EMPTY_COLOR_DARK = EMPTY_COLOR.darker();
    private static final Color UNKNOWN_COLOR = Color.gray;              // Color used for objects that have no defined color.

    private final Color SUCCESS_COLOR = new Color(27, 157, 21);
    private final Color FAIL_COLOR = new Color(207, 39, 39);

    private final Color DAY_COLOR = new Color(78, 206, 243);
    private final Color SUNRISE_COLOR = new Color(255, 167, 0);
    private final Color SUNSET_COLOR = new Color(75, 119,201);
    private final Color NIGHT_COLOR = new Color(36, 36, 113);

    // Constants or MAIN -> NORTH Simulation Stats Panel (simstats)
    private final String SIMSTATS_STEP_PREFIX = "Step: ";
    private final String SIMSTATS_TIME_PREFIX = "Time: ";
    private final String SIMSTATS_DAYTIME_PREFIX = "Daytime: ";
    private final String SIMSTATS_DAYCOUNT_PREFIX = "Number of days: ";

    // Constants for MAIN -> SOUTH Population Stats Panel (popstats)
    private final String POPSTATS_TOTAL_PREFIX = "Total Population: ";

    // Constants for MAIN -> EAST -> CENTRE TabbedPane (tabmenu)
    private final String TAB1_NAME = "SpawnRate";
    private final String TAB1_TOOLTIP = "Control the spawnrate of an entity on simulation restart";
    private final String TAB2_NAME = "Values";
    private final String TAB2_TOOLTIP = "Change the constants that define an entity";
    private final String TAB3_NAME = "Add";
    private final String TAB3_TOOLTIP = "Add your own custom entities";
    private final String TAB4_NAME = "Draw";
    private final String TAB4_TOOLTIP = "Draw entities on the simulation";

    private final String PLAYPAUSE_TOOLTIP = "Play or pause the simulation";
    private final String SPEED_TOOLTIP = "Toggle simulator speeds";
    private final String STEP_TOOLTIP = "Step the simulation once";
    private final String RESET_TOOLTIP = "Reset the simulation";



    private final String FULL_RESET = "Full Reset";

    // The anchor for the JLabel for North and South of mainPanel
    private final int SIMSTATS_LABEL_LAYOUT = JLabel.CENTER;
    private final int POPSTATS_LABEL_LAYOUT = SIMSTATS_LABEL_LAYOUT;

    // The spacing for the JLabel for North and South of mainPanel
    private final int LABEL_SPACING_HGAP = 20;
    private final int LABEL_SPACING_VGAP = 5;

    // The spacing for the PlayPause Buttons
    private final int PLAYPAUSE_BUTTON_SPACING_HGAP = 5;
    private final int PLAYPAUSE_BUTTON_SPACING_VGAP = 5;

    private final Dimension PLAYPAUSE_BUTTON_SIZE = new Dimension(40, 40);
    private final Dimension SMALL_BUTTON_SIZE = new Dimension(23, 23);
    private final Dimension TEXTFIELD_SIZE = new Dimension(100, 25);

    // TAB 1 SPAWNRATE
    private final Insets SPAWNRATE_INSETS = new Insets(1 ,1 ,1 ,5);

    // The Main Panel of the whole Window
    Container mainPanel;

    // Components for MAIN -> NORTH Simulation Stats Panel (sim stats)
    private JPanel simstats_Panel;
    private JLabel simStats_StepLabel, simStats_TimeLabel, simStats_DaytimeLabel, simStats_DayCountLabel;

    // Components for MAIN -> SOUTH Population Stats Panel (pop stats)
    private JPanel popStats_Panel;
    private JLabel popStats_TotalLabel, popStats_TypeLabel;
    private ArrayList<Integer> popStats_EntityCount;
    private ArrayList<JLabel> popStats_EntityLabels;

    // Components for MAIN -> EAST Options Panel (options)
    private JPanel options_Panel;
    // Components for MAIN -> EAST -> NORTH Control Buttons (playpause)
    private JPanel playpause_Panel;
    private JButton playpause_playpauseButton, playpause_speedButton, playpause_stepButton, playpause_resetButton;

    // Components for MAIN -> EAST -> CENTRE Tab Meny components (tabmenu)
    private JTabbedPane tabmenu_TabbedPane;

    // Components for MAIN -> EAST -> CENTRE -> TAB1 Spawnrate Panel (spawnrate)
    private JPanel spawnrate_Panel;
    private JTextField spawnrate_seedTextField;
    private JButton spawnrate_seedResetButton;
    private ArrayList<JSlider> spawnrate_Slider;
    private ArrayList<JCheckBox> spawnrate_CheckBox;
    private ArrayList<JButton> spawnrate_DeleteButton;
    private ArrayList<JButton> spawnrate_RestoreDefaultButton;

    // Components for MAIN -> EAST -> CENTRE -> TAB2 Value Editor Panel (valedit)
    private JPanel valedit_Panel;
    private JPanel valedit_Container;
    private ArrayList<JPanel> valedit_TypeContainerPanels;

    // Components for MAIN -> EAST -> CENTRE -> TAB3 Add Entity Panel (addent)
    private JPanel addent_Panel;
    private JPanel addent_Container;
    private HashMap<Enum<EntityStats.EntityType>, JPanel> typePanel;
    private EntityStats newEntity;

    // Components for MAIN -> EAST -> CENTRE -> TAB4 Draw Entity Panel (drawent)
    private JPanel drawent_Panel;
    private JButton drawent_EnableButton;
    private boolean drawmodeEnabled = false;

    // Components for MAIN -> EAST -> SOUTH Extras panel (extras)
    private JPanel extras_Panel;

    // Components for MAIN -> EAST -> SOUTH -> NORTH details Label
    private JLabel detailsLabel;        // For telling the user important details

    // Components for MAIN -> EAST -> SOUTH -> CENTRE Environment View (enview)
    private JPanel enview_Panel;        // For telling the user important details
    private JLabel weather;
    private ArrayList<ImageIcon> enview_clockFaces;
    private JLabel enview_clock;




    private JButton fullResetButton;
    private ImageIcon playIcon, pauseIcon, stepIcon, resetIcon, restoreIcon, deleteIcon;

    private final static ArrayList<Color> COLORS = new ArrayList<>(Arrays.asList(Color.RED,Color.BLUE,Color.MAGENTA,Color.ORANGE,Color.GREEN, Color.CYAN, Color.BLACK, Color.DARK_GRAY, Color.LIGHT_GRAY, Color.PINK));
    private HashMap<Color, EntityStats> entityColor;

    private final int MAX_ENTITIES = COLORS.size();

    private FieldView fieldView;
    private Histogram histogram;
    private PieChart pieChart;

    // A statistics object computing and storing simulation information
    private FieldStats stats;
    private final Simulator simulator;

    private int height, width;
    private Field field;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width, Simulator simulator, Field field)
    {
        this.height = height;
        this.width = width;
        this.simulator = simulator;
        this.field = field;
        stats = new FieldStats();
        fieldView = new FieldView(height, width);
        mainPanel = getContentPane();

        JFrame inspectFrame = new JFrame();
        Container inspectContainer = inspectFrame.getContentPane();
        JPanel inspectPanel = new JPanel(new BorderLayout());
        inspectContainer.add(inspectPanel, BorderLayout.CENTER);

        inspectFrame.setUndecorated(true);
        inspectFrame.setPreferredSize(new Dimension(150,110));
        inspectFrame.pack();

        fieldView.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                inspectPanel.removeAll();

                inspectFrame.setLocation(e.getXOnScreen() + 20, e.getYOnScreen() + 20);
                int fieldX = e.getX()/(fieldView.getWidth()/width);
                int fieldY = e.getY()/(fieldView.getHeight()/height);

                Entity entity = (Entity)field.getObjectAt(fieldY, fieldX);

                if (entity == null){
                    inspectFrame.setVisible(false);
                }
                else {
                    inspectFrame.setVisible(true);
                    if (entity instanceof Animal){
                        inspectPanel.add(((Animal) entity).getInspectPanel(), BorderLayout.CENTER);
                    }
                    else if(entity instanceof  Plant){
                        inspectPanel.add(((Plant) entity).getInspectPanel(), BorderLayout.CENTER);
                    }
                }
                inspectPanel.updateUI();
            }
        });

        fieldView.addMouseListener(new MouseAdapter() {
            public void mouseExited(MouseEvent evt) {
                inspectFrame.setVisible(false);
            }
        });

        playIcon = new ImageIcon("resources/play.png");
        pauseIcon = new ImageIcon("resources/pause.png");
        stepIcon = new ImageIcon("resources/step.png");
        resetIcon = new ImageIcon("resources/reset.png");
        restoreIcon = new ImageIcon("resources/restore.png");
        deleteIcon = new ImageIcon("resources/delete.png");

        setupTab1();

        mainPanel.add(fieldView, BorderLayout.CENTER);              // CENTRE Simulation Panel
        initialiseSimStatsPanel(mainPanel, BorderLayout.NORTH);     // NORTH Simulation Stats Panel
        initialisePopStatsPanel(mainPanel, BorderLayout.SOUTH);     // SOUTH Population Stats Panel
        initialiseOptionsPanel(mainPanel, BorderLayout.EAST);       // EAST Options Stats Panel

        // extra methods for diagrams and buttons
        setTitle("Fox and Rabbit Simulation");
        //makePieChart(height, width);
        //makeHistogram(height, width);
        //makeDiagramsVisible();
        setLocation(100, 50);
        setPreferredSize(new Dimension(1492,821));

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void setupTab1() {
        spawnrate_seedTextField = new JTextField(Randomizer.getSeed() + "");
        spawnrate_seedResetButton = new JButton(restoreIcon);
        spawnrate_seedResetButton.setPreferredSize(SMALL_BUTTON_SIZE);
    }


    /**
     * Initialises the Simulator Statistic panel located at the North of the main frame.
     * @param container The container you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void initialiseSimStatsPanel(Container container, String layout){
        simstats_Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, LABEL_SPACING_HGAP, LABEL_SPACING_VGAP));
        simStats_StepLabel = new JLabel(SIMSTATS_STEP_PREFIX, SIMSTATS_LABEL_LAYOUT);
        simStats_TimeLabel = new JLabel(SIMSTATS_TIME_PREFIX, SIMSTATS_LABEL_LAYOUT);
        simStats_DaytimeLabel = new JLabel(SIMSTATS_DAYTIME_PREFIX, SIMSTATS_LABEL_LAYOUT);
        simStats_DayCountLabel = new JLabel(SIMSTATS_DAYCOUNT_PREFIX, SIMSTATS_LABEL_LAYOUT);
        addAll(simstats_Panel, simStats_StepLabel, simStats_TimeLabel, simStats_DaytimeLabel, simStats_DayCountLabel);
        container.add(simstats_Panel, layout);
    }

    /**
     * Initialises the Population Statistic panel located at the South of the main frame.
     * @param container The container you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void initialisePopStatsPanel(Container container, String layout){
        popStats_Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, LABEL_SPACING_HGAP , LABEL_SPACING_VGAP));
        popStats_TotalLabel = new JLabel();
        popStats_TypeLabel = new JLabel();
        addAll(popStats_Panel, popStats_TotalLabel, new JLabel("     ", JLabel.CENTER), popStats_TypeLabel, new JLabel("     ", JLabel.CENTER));

        popStats_EntityCount = new ArrayList<>();
        popStats_EntityLabels = new ArrayList<>();
        for (EntityStats entity : simulator.getPossibleEntities()){
            popStats_EntityCount.add(0);

            JLabel currentEntity = new JLabel();
            popStats_EntityLabels.add(currentEntity);
            popStats_Panel.add(currentEntity); // IF THIS BREAKS THEN USE INDEX OF
        }
        container.add(popStats_Panel, layout);
        popStats_Panel.updateUI();
    }

    /**
     * Initialises the Options panel located at the East of the main frame.
     * @param container The container you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void initialiseOptionsPanel(Container container, String layout){
        options_Panel = new JPanel(new BorderLayout());
        container.add(options_Panel, layout);

        initialisePlaypauseButtons(options_Panel, BorderLayout.NORTH);
        initialiseTabbedMenu(options_Panel, BorderLayout.CENTER);
        initialiseExtrasPanel(options_Panel, BorderLayout.SOUTH);

    }

    /**
     * Initlialise the extras panel located South of tabs
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void initialiseExtrasPanel(JPanel panel, String layout){
        extras_Panel = new JPanel(new BorderLayout());
        panel.add(extras_Panel, layout);

        //North
        detailsLabel = new JLabel("", JLabel.CENTER);
        detailsLabel.setHorizontalAlignment(JLabel.CENTER);
        detailsLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        extras_Panel.add(detailsLabel, BorderLayout.NORTH);

        // Centre
        initialiseEnvViewPanel(extras_Panel, BorderLayout.CENTER);

        // South
        fullResetButton = new JButton(FULL_RESET);
        fullResetButton.setEnabled(simulator.isRunning());
        extras_Panel.add(fullResetButton, BorderLayout.SOUTH);

        fullResetButton.addActionListener(e -> {
            simulator.resetEntities();
            Randomizer.restoreDefaultSeed();

            refreshPanels();
        });


    }

    /**
     * Initialises the environment views located centre of extras panel.
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void initialiseEnvViewPanel(JPanel panel, String layout){
        enview_Panel = new JPanel(new BorderLayout());
        panel.add(enview_Panel, layout);


        // Weather text
        weather = new JLabel("", JLabel.CENTER);
        enview_Panel.add(weather, BorderLayout.CENTER);
        enview_Panel.setPreferredSize(new Dimension(200,200));


        // Time Clock
        JPanel clockPanel = new JPanel(new BorderLayout());
        enview_Panel.add(clockPanel, BorderLayout.SOUTH);

        enview_clockFaces = new ArrayList<>();
        for (int index = 0; index < 24; index++){
            String stage;
            if (index < 10)
                stage = "0" + index;
            else
                stage = index + "";

            ImageIcon clockIcon = new ImageIcon(String.format("resources/clock/clock_%s.png", stage));
            clockIcon = new ImageIcon(clockIcon.getImage().getScaledInstance(120,120, Image.SCALE_FAST));
            enview_clockFaces.add(clockIcon);
        }
        enview_clock = new JLabel(enview_clockFaces.get(0));
        //enview_clock.setOpaque(true);
        enview_clock.setBackground(new Color(0,0,0,0));     // Makes the clock background same as panel.
        clockPanel.add(enview_clock,BorderLayout.CENTER);

        // Pie Chart
//        JFrame lol = new JFrame("ok");
//        //Container stupidcontainer = lol.getContentPane();
//
//        JPanel pieChartPanel = new JPanel(new BorderLayout());
//
//        lol.add(pieChartPanel, BorderLayout.CENTER);
//
//        //enview_Panel.add(pieChartPanel, BorderLayout.EAST);
//        this.pieChart = new PieChart();
//        pieChartPanel.add(pieChart, BorderLayout.CENTER);
//        this.pieChart.setSize(120, 120);
//        this.pieChart.stats(popStats_EntityCount, simulator.getPossibleEntities());
//        this.pieChart.repaint();
//
//        lol.setSize(500,500);
//        lol.pack();
//        lol.setVisible(true);

    }

    private void setDetailText(String text){
        setDetailText(text, Color.BLACK);
    }

    private void setDetailText(String text, Color color){
        detailsLabel.setForeground(color);
        detailsLabel.setText("<html><p style=\"width:" + 160 + "px\">"+text+"</p></html>");
        detailsLabel.updateUI();
    }

    /**
     * Refreshes the content of tab1 and tab2.
     */
    private void refreshPanels(){
        //spawnrate_Panel.removeAll();
//        valedit_Panel.removeAll();
//        addent_Panel.removeAll();
//        drawent_Panel.removeAll();

        //popStats_Panel.removeAll();
        initialisePopStatsPanel(mainPanel, BorderLayout.SOUTH);
        //popStats_Panel.updateUI();

        drawTab1Spawnrate(spawnrate_Panel, BorderLayout.CENTER);
        drawTab2Validate(valedit_Panel, BorderLayout.CENTER);
        drawTab3Addent(addent_Panel, BorderLayout.CENTER);
        drawTab4Drawent(drawent_Panel, BorderLayout. CENTER);

        simulator.showStatus();

        //spawnrate_Panel.updateUI();
//        valedit_Panel.updateUI();
//        addent_Panel.updateUI();
//        drawent_Panel.updateUI();
    }

    /**
     * Initialises the Control Buttons located North of the Options frame.
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void initialisePlaypauseButtons(JPanel panel, String layout){
        playpause_Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, PLAYPAUSE_BUTTON_SPACING_HGAP, PLAYPAUSE_BUTTON_SPACING_VGAP));
        playpause_playpauseButton = new JButton(pauseIcon);
        playpause_playpauseButton.setToolTipText(PLAYPAUSE_TOOLTIP);
        playpause_speedButton = new JButton(simulator.getSpeedSymbol());
        playpause_speedButton.setToolTipText(SPEED_TOOLTIP);
        playpause_stepButton = new JButton(stepIcon);
        playpause_stepButton.setToolTipText(STEP_TOOLTIP);
        playpause_resetButton = new JButton(resetIcon);
        playpause_resetButton.setToolTipText(RESET_TOOLTIP);
        setSizeForAll(PLAYPAUSE_BUTTON_SIZE, playpause_playpauseButton, playpause_speedButton, playpause_stepButton, playpause_resetButton);
        addAll(playpause_Panel, playpause_playpauseButton, playpause_speedButton, playpause_stepButton, playpause_resetButton);
        panel.add(playpause_Panel, layout);

        playpause_speedButton.setEnabled(simulator.isRunning());
        playpause_stepButton.setEnabled(simulator.isRunning());
        playpause_resetButton.setEnabled(simulator.isRunning());

        // Button Events
        // Stops and Starts the simulation
        playpause_playpauseButton.addActionListener(e -> {
            simulator.toggleRunning();
            if(!simulator.isRunning()){
                playpause_playpauseButton.setIcon(playIcon);

                playpause_speedButton.setEnabled(true);
                playpause_stepButton.setEnabled(true);
                playpause_resetButton.setEnabled(true);

                fullResetButton.setEnabled(true);
                setTabsEnabled(true);
            }
            else{
                playpause_playpauseButton.setIcon(pauseIcon);

                playpause_speedButton.setEnabled(false);
                playpause_stepButton.setEnabled(false);
                playpause_resetButton.setEnabled(false);

                fullResetButton.setEnabled(false);
                setTabsEnabled(false);
            }
        });
        // Toggles the speed at which the simulator runs
        playpause_speedButton.addActionListener(e -> {
            simulator.incSpeed();
            playpause_speedButton.setText(simulator.getSpeedSymbol());
        });
        // Steps the simulation forward by one
        playpause_stepButton.addActionListener(e -> simulator.simulateOneStep());
        // Resets the simulation to default
        playpause_resetButton.addActionListener(e -> simulator.reset());
    }

    /**
     * Disables the interaction of tabs 2, 3 and 4.
     * @param isEnabled The state you want to set the tabs
     */
    void setTabsEnabled(boolean isEnabled){
        tabmenu_TabbedPane.setEnabledAt(1, isEnabled);
        setPanelEnabled((JPanel) tabmenu_TabbedPane.getComponentAt(1), isEnabled);

        tabmenu_TabbedPane.setEnabledAt(2, isEnabled);
        setPanelEnabled((JPanel) tabmenu_TabbedPane.getComponentAt(2), isEnabled);

        tabmenu_TabbedPane.setEnabledAt(3, isEnabled);
        drawent_EnableButton.setEnabled(isEnabled);
    }

    /**
     * Sets the state of all components within a panel, as well as all the components within all the panels within the panel.
     * @author Creit to https://stackoverflow.com/a/32481577/11245518
     * @param panel The panel you wish to set the state of.
     * @param isEnabled The state you wish to set the panel's components.
     */
    void setPanelEnabled(JPanel panel, Boolean isEnabled) {
        panel.setEnabled(isEnabled);
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                setPanelEnabled((JPanel) component, isEnabled);
            }
            component.setEnabled(isEnabled);
        }
    }

    /**
     * Initialises the TabPane located Centre of the Options frame.
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void initialiseTabbedMenu(JPanel panel, String layout){
        tabmenu_TabbedPane = new JTabbedPane();

        // Tab 1
        spawnrate_Panel = new JPanel(new BorderLayout());
        drawTab1Spawnrate(spawnrate_Panel, BorderLayout.CENTER);
        tabmenu_TabbedPane.addTab(TAB1_NAME, spawnrate_Panel);
        tabmenu_TabbedPane.setToolTipTextAt(0, TAB1_TOOLTIP);

        // Tab 2
        valedit_Panel = new JPanel(new BorderLayout());
        drawTab2Validate(valedit_Panel, BorderLayout.CENTER);
        tabmenu_TabbedPane.addTab(TAB2_NAME, valedit_Panel);
        tabmenu_TabbedPane.setToolTipTextAt(1, TAB2_TOOLTIP);

        // Tab 3
        addent_Panel = new JPanel(new BorderLayout());
        drawTab3Addent(addent_Panel, BorderLayout.CENTER);
        tabmenu_TabbedPane.addTab(TAB3_NAME, addent_Panel);
        tabmenu_TabbedPane.setToolTipTextAt(2, TAB3_TOOLTIP);

        // Tab 4
        drawent_Panel = new JPanel(new BorderLayout());
        drawTab4Drawent(drawent_Panel, BorderLayout.CENTER);
        tabmenu_TabbedPane.addTab(TAB4_NAME, drawent_Panel);
        tabmenu_TabbedPane.setToolTipTextAt(3, TAB4_TOOLTIP);

        setTabsEnabled(simulator.isRunning());          // SHOULDN'T THIS BE !simulator.isRunning() ERM????
        panel.add(tabmenu_TabbedPane, layout);
    }

    /**
     * Initialises the first tab of the TabbedPane, the Spawnrate tab.
     * This tab lets you change the spawnrate, remove, or disable an entity on reset.
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void drawTab1Spawnrate(JPanel panel, String layout){
        panel.removeAll();

        spawnrate_seedTextField = new JTextField(Randomizer.getSeed() + "");
        spawnrate_seedResetButton = new JButton(restoreIcon);
        spawnrate_seedResetButton.setPreferredSize(SMALL_BUTTON_SIZE);
        spawnrate_Slider = new ArrayList<>();
        spawnrate_CheckBox = new ArrayList<>();
        spawnrate_DeleteButton = new ArrayList<>();
        spawnrate_RestoreDefaultButton = new ArrayList<>();

        JPanel holder = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = SPAWNRATE_INSETS;
        gbc.gridy = 0;
        for (EntityStats stat : simulator.getPossibleEntities()) {
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0;
            gbc.gridy++;
            JCheckBox checkBox = new JCheckBox(stat.getName() + ": " + stat.getCreationProbability() + "%", true);
            spawnrate_CheckBox.add(checkBox);
            holder.add(checkBox, gbc);

            gbc.anchor = GridBagConstraints.SOUTH;
            gbc.gridx = -1;
            JButton deleteButton = new JButton(deleteIcon);
            deleteButton.setPreferredSize(SMALL_BUTTON_SIZE);
            spawnrate_DeleteButton.add(deleteButton);
            holder.add(deleteButton, gbc);

            gbc.anchor = GridBagConstraints.CENTER;
            gbc.gridx = 0;
            gbc.gridy++;
            JSlider slider = new JSlider(0, 200, (int) (stat.getCreationProbability() * 10));
            slider.setPaintTicks(true);
            slider.setMinorTickSpacing(10);
            slider.setMajorTickSpacing(50);
            spawnrate_Slider.add(slider);
            holder.add(slider, gbc);

            gbc.anchor = GridBagConstraints.NORTH;
            gbc.gridx = -1;
            JButton defaultsButton = new JButton(restoreIcon);
            defaultsButton.setPreferredSize(SMALL_BUTTON_SIZE);
            spawnrate_RestoreDefaultButton.add(defaultsButton);
            holder.add(defaultsButton, gbc);
        }

        // Makes it so the tab is anchored to the top of the tab
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.weighty = 1;
        holder.add(new JLabel(""),gbc);

        JScrollPane scrollPanel = new JScrollPane(holder);

        // HOPEFULLY FIX LATER BECAUSE I DON'T WANT THE SCROLLBAR ALWAYS THERE, BUT WITHOUT THIS THE BAR OVERLAPS
        scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        spawnrate_seedTextField.setPreferredSize(TEXTFIELD_SIZE);
        JPanel seedPanel = new JPanel(new FlowLayout());
        seedPanel.add(new JLabel("Seed: "));
        seedPanel.add(spawnrate_seedTextField);
        seedPanel.add(spawnrate_seedResetButton);
        scrollPanel.setColumnHeaderView(seedPanel);

        panel.add(scrollPanel, layout);

        // Event Listeners
        // Accepts input for Seed Box. Changes the seed of the Randomizer
        spawnrate_seedTextField.addKeyListener(new KeyAdapter() {
            // BETTER WAYS TO DO THIS BUT A BIT COMPLICATED (KeyListener bad)
            // ALSO WEIRD PROBLEM WITH STRING INPUT, LIKE INITIALLY 1111 AND 11111 ARE THE SAME
            public void keyTyped(KeyEvent e) {
                char character = e.getKeyChar();
                if ((((character < '0') || (character > '9')) && (character != KeyEvent.VK_BACK_SPACE)) || (spawnrate_seedTextField.getText().length() > 8)) {
                    e.consume();
                }
                Randomizer.setSeed(Integer.parseInt(spawnrate_seedTextField.getText()));
            }
        });

        // Resets the field
        spawnrate_seedResetButton.addActionListener(e -> {
            Randomizer.restoreDefaultSeed();
            spawnrate_seedTextField.setText(Randomizer.getSeed() + "");
        });

        //Creates listener for each existing entity in the simulation
        for (EntityStats entity : simulator.getPossibleEntities()){
            int index = simulator.getPossibleEntities().indexOf(entity);
            JSlider currentSlider = spawnrate_Slider.get(index);
            JCheckBox currentCheckBox = spawnrate_CheckBox.get(index);
            JButton currentDeleteButton = spawnrate_DeleteButton.get(index);
            JButton currentDefaultsButton = spawnrate_RestoreDefaultButton.get(index);

            currentSlider.addChangeListener(e -> {
                double currentSpawnProb = (double) currentSlider.getValue() / 10;

                currentCheckBox.setText(entity.getName() + ": " + currentSpawnProb + "%");
                entity.setCreationProbability(currentSpawnProb);
            });

            currentCheckBox.addItemListener(e -> {
                currentSlider.setEnabled(currentCheckBox.isSelected());
                entity.toggleEnabled();
            });

            currentDeleteButton.addActionListener(e -> {
                if (!(simulator.getPossibleEntities().size() > 1)) {
                    setDetailText("Removal Failed. There needs to be at least one entity!", FAIL_COLOR);
                }
                else if (simulator.isRunning()){
                    setDetailText("Please don't remove while simulator is running", FAIL_COLOR);
                }
                else {
                        simulator.removeEntity(entity);
                        setDetailText("Successfully removed the " + entity.getName() + " entity!", SUCCESS_COLOR);


                        simulator.removeFromOrganisms(entity);
                        field.removeAllObjectsOf(entity);
                        simulator.removeEntity(entity);


                        simulator.showStatus();
                        refreshPanels();
                    }

            });

            currentDefaultsButton.addActionListener(e -> {
                double defaultSpawnProb = entity.getDefaults().getCreationProbability();

                currentSlider.setValue((int)(defaultSpawnProb * 10));
                currentCheckBox.setText(entity.getName() + ": " + defaultSpawnProb + "%");
                entity.setCreationProbability(defaultSpawnProb);
            });
        }

        panel.updateUI();
    }

    /**
     * Initialises the second tab of the TabbedPane, the Value Editor tab.
     * This tab lets you live edit an entities properties in the simulation.
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void drawTab2Validate(JPanel panel, String layout){
        panel.removeAll();

        JComboBox<EntityStats> valuesComboBox = new JComboBox<>(simulator.getPossibleEntities().toArray(new EntityStats[0]));
        panel.add(valuesComboBox, BorderLayout.NORTH);

        valedit_Container = new JPanel(new BorderLayout()); // THIS METHOD ISN'T GREAT BUT ROLL WITH IT
        panel.add(valedit_Container, BorderLayout.CENTER);


        entityColor = new HashMap<>();
        for (Color color : COLORS){
            entityColor.put(color, null);
        }

        valedit_TypeContainerPanels = new ArrayList<>();
        for (EntityStats stat : simulator.getPossibleEntities()){
            JPanel currentStatSliderContainer = new JPanel(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();

//            JSlider breedingSlider;
//            JLabel valueBreedingLabel = new JLabel("Breeding Probability: " + stat.getBreedingProbability());
//            JButton valueBreedingDefaultButton;

//            // Breeding Probability SLIDER
//            gbc = new GridBagConstraints();
//            gbc.gridx = 0;
//            gbc.gridy = 2;
//            gbc.gridwidth = 2;
//            gbc.anchor = GridBagConstraints.WEST;
//            gbc.fill = GridBagConstraints.HORIZONTAL;
//            breedingSlider = new JSlider(0, 40, (int) (stat.getBreedingProbability() * 100));
//            breedingSlider.setPaintTicks(true);
//            breedingSlider.setSnapToTicks(true);
//            breedingSlider.setMinorTickSpacing(1);
//            breedingSlider.setMajorTickSpacing(5);
//            currentStatSliderContainer.add(breedingSlider, gbc);
//
//            breedingSlider.addChangeListener(e -> {
//                double currentBreedProb = (double)breedingSlider.getValue()/100;
//
//                stat.setBreedingProbability(currentBreedProb);
//                valueBreedingLabel.setText("Breeding Probability: " + currentBreedProb);
//            });

//            // Breeding PROBABILITY LABEL
//            gbc = new GridBagConstraints();
//            gbc.gridx = 0;
//            gbc.gridy = 1;
//            gbc.gridwidth = 2;
//            gbc.anchor = GridBagConstraints.WEST;
//            //valueBreedingLabel = new JLabel("Breeding Probability:");
//            currentStatSliderContainer.add(valueBreedingLabel, gbc);
//
//            // 2: Breeding Probability DEFAULT
//            gbc = new GridBagConstraints();
//            gbc.gridx = 2;
//            gbc.gridy = 2;
//            gbc.fill = GridBagConstraints.HORIZONTAL;
//            valueBreedingDefaultButton = new JButton(restoreIcon);
//            valueBreedingDefaultButton.setPreferredSize(SMALL_BUTTON_SIZE);
//            currentStatSliderContainer.add(valueBreedingDefaultButton, gbc);
//
//            valueBreedingDefaultButton.addActionListener(e -> {
//                double defaultBreedProb = stat.getDefaults().getBreedingProbability();
//
//                breedingSlider.setValue((int)(defaultBreedProb * 100));
//                valueBreedingLabel.setText("Breeding Probability: " + defaultBreedProb);
//                stat.setBreedingProbability(defaultBreedProb);
//            });

            createSlider(currentStatSliderContainer, 1, "Breeding Prob", EntityStats.BREEDINGPROBABILITY_MAX, 0.01, stat::getBreedingProbability, stat::setBreedingProbability, stat.getDefaults()::getBreedingProbability);


            // IF ANIMAL
            if (!stat.getEntityType().equals(EntityStats.EntityType.PLANT)){
                AnimalStats animalStat = (AnimalStats)stat;

                // isNocturnal
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 3;
                gbc.insets = new Insets(3,0,3,0);
                JCheckBox nocturnalBox = new JCheckBox("Nocturnal", animalStat.isNocturnal());
                currentStatSliderContainer.add(nocturnalBox, gbc);

                // DEFAULT BUTTON
                gbc.gridx = 2;
                JButton defaultButton = new JButton(restoreIcon);
                defaultButton.setPreferredSize(new Dimension(23, 23));
                currentStatSliderContainer.add(defaultButton, gbc);

                nocturnalBox.addItemListener(e -> {
                    animalStat.setNocturnal(nocturnalBox.isSelected());
                    });

                defaultButton.addActionListener(e -> {
                    boolean defaultValue = animalStat.getDefaults().isNocturnal();

                    nocturnalBox.setSelected(defaultValue);
                    animalStat.setNocturnal(defaultValue);
                });

                createSlider(currentStatSliderContainer, 4, "Breeding Age",AnimalStats.BREEDINGAGE_MAX, animalStat::getBreedingAge, animalStat::setBreedingAge, animalStat.getDefaults()::getBreedingAge);
                createSlider(currentStatSliderContainer, 6, "Max Age", AnimalStats.MAXAGE_MAX, animalStat::getMaxAge, animalStat::setMaxAge, animalStat.getDefaults()::getMaxAge);
                createSlider(currentStatSliderContainer, 8, "Max Litter Size", AnimalStats.MAXLITTERSIZE_MAX, animalStat::getMaxLitterSize, animalStat::setMaxLitterSize, animalStat.getDefaults()::getMaxLitterSize);
                createSlider(currentStatSliderContainer, 10, "Hunger Value", AnimalStats.HUNGERVALUE_MAX, animalStat::getHungerValue, animalStat::setHungerValue, animalStat.getDefaults()::getHungerValue);
                gbc.gridy = 11;
            }
            else{ // IF PLANT
                PlantStats plantStat = (PlantStats)stat;

                createSlider(currentStatSliderContainer, 3, "Food Value", PlantStats.FOODVALUE_MAX, plantStat::getFoodValue, plantStat::setFoodValue, plantStat.getDefaults()::getFoodValue);
                createSlider(currentStatSliderContainer, 5, "Max Level", PlantStats.MAXLEVEL_MAX, plantStat::getMaxLevel, plantStat::setMaxLevel, plantStat.getDefaults()::getMaxLevel);
                gbc.gridy = 6;
            }

            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JComboBox<Color> colorComboBox = new JComboBox<>(COLORS.toArray(new Color[0]));
            colorComboBox.setSelectedIndex(simulator.getPossibleEntities().indexOf(stat));
            colorComboBox.setBackground(stat.getColor());

            entityColor.replace(stat.getColor(), stat);

            ComboBoxRenderer renderer = new ComboBoxRenderer(colorComboBox, entityColor);
            colorComboBox.setRenderer(renderer);

            currentStatSliderContainer.add(colorComboBox, gbc);

            colorComboBox.addActionListener(e -> {
                Color selectedColor = (Color) colorComboBox.getSelectedItem();
                if (entityColor.get(selectedColor) == null){
                    entityColor.replace(stat.getColor(), null);
                    entityColor.replace(selectedColor, stat);
                    stat.setColor(selectedColor);
                    colorComboBox.setBackground(selectedColor);
                    setDetailText("Changed colour of " + stat.getName() + "." , SUCCESS_COLOR);
                }
                else{
                    setDetailText("Colour already taken by " + entityColor.get(selectedColor).getName() + "!", FAIL_COLOR);
                }
            });

            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            currentStatSliderContainer.add(new Label(stat.getEntityType().toString()), gbc);


            // Adds Panel to ArrayList
            valedit_TypeContainerPanels.add(currentStatSliderContainer);
        }

        valedit_Container.add(valedit_TypeContainerPanels.get(0), BorderLayout.CENTER);

        valuesComboBox.addActionListener(e -> {
            int index = valuesComboBox.getSelectedIndex();
            valedit_Container.removeAll();
            valedit_Container.add(valedit_TypeContainerPanels.get(index), BorderLayout.CENTER);
            valedit_Container.updateUI();
        });

        panel.add(valedit_Container, BorderLayout.CENTER);

        panel.updateUI();
    }

    /**
     * Initialises the third tab of the TabbedPane, the Add Entity tab.
     * This tab lets you add a custom entity to the simulaton.
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void drawTab3Addent(JPanel panel, String layout){
        panel.removeAll();

        JPanel nameAndTypesPanel = new JPanel(new GridBagLayout());

        addent_Container = new JPanel(new BorderLayout());

        newEntity = new EntityStats();
        AnimalStats newAnimal = new AnimalStats();
        PlantStats newPlant = new PlantStats();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<EntityStats.EntityType> typeComboBox = new JComboBox(EntityStats.EntityType.values());
        nameAndTypesPanel.add(typeComboBox, gbc);

        typeComboBox.addItemListener(e -> {
            EntityStats.EntityType type =  (EntityStats.EntityType) typeComboBox.getSelectedItem();

            newEntity = new EntityStats();
            if (type.equals(EntityStats.EntityType.PLANT)){
                newEntity = newPlant;
            }
            else {
                newEntity = newAnimal;
            }
            newEntity.setEntityType(type);

            addent_Container.removeAll();
            addent_Container.add(typePanel.get(type),BorderLayout.CENTER);
            addent_Container.updateUI();
        });

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<Color> colorComboBox  = new JComboBox<>(COLORS.toArray(new Color[0]));
        ComboBoxRenderer renderer = new ComboBoxRenderer(colorComboBox, entityColor);
        colorComboBox.setRenderer(renderer);
        nameAndTypesPanel.add(colorComboBox, gbc);

        colorComboBox.addActionListener(e -> {
            Color selectedColor = (Color) colorComboBox.getSelectedItem();
            if (entityColor.get(selectedColor) == null){
//                entityColor.replace(newEntity.getColor(), null);
//                entityColor.replace(selectedColor, newEntity);
                newEntity.setColor(selectedColor);
                colorComboBox.setBackground(selectedColor);
                //setDetailText("Changed colour of " + stat.getName() + "." , SUCCESS_COLOR);
            }
            else{
                //setDetailText("Colour already taken by " + entityColor.get(selectedColor).getName() + "!", FAIL_COLOR);
            }
        });

        tabmenu_TabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabmenu_TabbedPane.getSelectedIndex() != 2){
                    newEntity.setColor(null);
                    colorComboBox.setBackground(null);
                }
            }
        });

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        JTextField nameTextField = new JTextField();
        nameTextField.setText("SampleName");
        nameTextField.setPreferredSize(TEXTFIELD_SIZE);
        nameAndTypesPanel.add(nameTextField, gbc);

        nameTextField.getDocument().addDocumentListener(new DocumentListener() {
            Runnable setNameAction = () -> {newEntity.setName(nameTextField.getText());};

            @Override
            public void changedUpdate(DocumentEvent e) {
                setNameAction.run();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                setNameAction.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setNameAction.run();
            }

        });

        panel.add(nameAndTypesPanel, BorderLayout.NORTH);

        // Panel with input boxes
        JPanel inputBoxesPanel = new JPanel(new GridBagLayout());

        //AnimalStats newAnimal = new AnimalStats();
        newEntity = newAnimal;

        createSlider(inputBoxesPanel, 0, "Creation Prob", EntityStats.CREATIONPROBABILITY_MAX, 0.1, newAnimal::getCreationProbability, newAnimal::setCreationProbability);
        createSlider(inputBoxesPanel, 2, "Breeding Prob", EntityStats.BREEDINGPROBABILITY_MAX, 0.01, newAnimal::getBreedingProbability, newAnimal::setBreedingProbability);

//        createAddSliderComponentDouble(inputBoxesPanel, 0, "Creation Prob", EntityStats.CREATIONPROBABILITY_MAX, 0.1, newAnimal::getCreationProbability, newAnimal::setCreationProbability);
//        createAddSliderComponentDouble(inputBoxesPanel, 2, "Breeding Prob", EntityStats.BREEDINGPROBABILITY_MAX, 0.01, newAnimal::getBreedingProbability, newAnimal::setBreedingProbability);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JCheckBox nocturnalBox = new JCheckBox("Nocturnal", false);
        inputBoxesPanel.add(nocturnalBox, gbc);

        nocturnalBox.addItemListener(e -> {
            newAnimal.setNocturnal(nocturnalBox.isSelected());
            });

        createSlider(inputBoxesPanel, 5, "Breeding Age", AnimalStats.BREEDINGAGE_MAX, newAnimal::getBreedingAge, newAnimal::setBreedingAge);
        createSlider(inputBoxesPanel, 7, "Max Age", AnimalStats.MAXAGE_MAX, newAnimal::getMaxAge, newAnimal::setMaxAge);
        createSlider(inputBoxesPanel, 9, "Max Litter Size", AnimalStats.MAXLITTERSIZE_MAX, newAnimal::getMaxLitterSize, newAnimal::setMaxLitterSize);
        createSlider(inputBoxesPanel, 11, "Hunger Value", AnimalStats.HUNGERVALUE_MAX, newAnimal::getHungerValue, newAnimal::setHungerValue);

//
//        createAddSliderComponentInt(inputBoxesPanel, 5, "Breeding Age", AnimalStats.BREEDINGAGE_MAX, newAnimal::getBreedingAge, newAnimal::setBreedingAge);
//        createAddSliderComponentInt(inputBoxesPanel, 7, "Max Age", AnimalStats.MAXAGE_MAX, newAnimal::getMaxAge, newAnimal::setMaxAge);
//        createAddSliderComponentInt(inputBoxesPanel, 9, "Max Litter Size", AnimalStats.MAXLITTERSIZE_MAX, newAnimal::getMaxLitterSize, newAnimal::setMaxLitterSize);
//        createAddSliderComponentInt(inputBoxesPanel, 11, "Hunger Value", AnimalStats.HUNGERVALUE_MAX, newAnimal::getHungerValue, newAnimal::setHungerValue);

        typePanel = new HashMap<>();
        typePanel.put(EntityStats.EntityType.PREY, inputBoxesPanel);
        typePanel.put(EntityStats.EntityType.PREDATOR, inputBoxesPanel);

        inputBoxesPanel = new JPanel(new GridBagLayout());

        //PlantStats newPlant = new PlantStats();
        //newEntity = newPlant;

        createSlider(inputBoxesPanel, 0, "Creation Prob", EntityStats.CREATIONPROBABILITY_MAX,0.1, newPlant::getCreationProbability, newPlant::setCreationProbability);
        createSlider(inputBoxesPanel, 2, "Breeding Prob", EntityStats.BREEDINGPROBABILITY_MAX,0.01, newPlant::getBreedingProbability, newPlant::setBreedingProbability);
        createSlider(inputBoxesPanel, 4, "Food Value", PlantStats.FOODVALUE_MAX, newPlant::getFoodValue, newPlant::setFoodValue);
        createSlider(inputBoxesPanel, 6, "Max Level", PlantStats.MAXLEVEL_MAX, newPlant::getMaxLevel, newPlant::setMaxLevel);

        typePanel.put(EntityStats.EntityType.PLANT, inputBoxesPanel);

        addent_Container.add(typePanel.get(EntityStats.EntityType.PREY), BorderLayout.CENTER);
        panel.add(addent_Container, layout);

        // Buttons
        JPanel addAndClearButtonsPanel = new JPanel(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton addButton = new JButton("Add");
        addAndClearButtonsPanel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            boolean nameTaken = false;
            for (EntityStats entity : simulator.getPossibleEntities()){
                if (entity.getName().equals(newEntity.getName())){
                    nameTaken = true;
                }
            }
            if (!(simulator.getPossibleEntities().size() < MAX_ENTITIES)){
                setDetailText("Too many entities! Max of " + MAX_ENTITIES + ".", FAIL_COLOR);
            }
            else if (nameTaken) {
                setDetailText("Entity with that name already exists!", FAIL_COLOR);
            }
            else if (newEntity.getColor() == null){
                setDetailText("Please choose a colour!", FAIL_COLOR);
            }
            else {
                try {
                    simulator.addEntityToPossibilities(newEntity.clone());
                } catch (CloneNotSupportedException ex) {
                    ex.printStackTrace();
                }
                setDetailText("Successfully added new " + newEntity.getName() + " " + newEntity.getEntityType().toString().toLowerCase() + "!", SUCCESS_COLOR);
                newEntity.resetToDefault();
                refreshPanels();
            }
        });

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton clearButton = new JButton("Clear");
        addAndClearButtonsPanel.add(clearButton, gbc);

        clearButton.addActionListener(e -> {
            newEntity = new EntityStats();
            newEntity.resetToDefault();

            panel.removeAll();
            drawTab3Addent(panel, layout);
            panel.updateUI();
        });

        panel.add(addAndClearButtonsPanel, BorderLayout.SOUTH);

        panel.updateUI();
    }

    /**
     * Initialises the fourth tab of the TabbedPane, the Draw Entity tab.
     * This tab lets you draw entities whereever you want on the field.
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void drawTab4Drawent(JPanel panel, String layout){
        panel.removeAll();

        drawent_EnableButton = new JButton();
        panel.add(drawent_EnableButton, BorderLayout.NORTH);

        JPanel drawent_OptionsPanel = new JPanel(new GridBagLayout());
        panel.add(drawent_OptionsPanel, layout);

        ButtonGroup buttonGroup = new ButtonGroup();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<EntityStats> entityComboBox = new JComboBox<>(simulator.getPossibleEntities().toArray(new EntityStats[0]));
        drawent_OptionsPanel.add(entityComboBox, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        drawent_OptionsPanel.add(new JLabel("Brush Size: "), gbc);

        gbc.gridx = 1;
        JRadioButton smallBrush = new JRadioButton("Small", true);
        drawent_OptionsPanel.add(smallBrush, gbc);
        buttonGroup.add(smallBrush);

        gbc.gridy++;
        JRadioButton mediumBrush = new JRadioButton("Medium");
        drawent_OptionsPanel.add(mediumBrush, gbc);
        buttonGroup.add(mediumBrush);

        gbc.gridy++;
        JRadioButton largeBrush = new JRadioButton("Large");
        drawent_OptionsPanel.add(largeBrush, gbc);
        buttonGroup.add(largeBrush);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        drawent_OptionsPanel.add(new JLabel(" "), gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        drawent_OptionsPanel.add(new JLabel("Eraser Size: "), gbc);

        gbc.gridx = 1;
        JRadioButton smallEraser = new JRadioButton("Small");
        drawent_OptionsPanel.add(smallEraser, gbc);
        buttonGroup.add(smallEraser);

        gbc.gridy++;
        JRadioButton mediumEraser = new JRadioButton("Medium");
        drawent_OptionsPanel.add(mediumEraser, gbc);
        buttonGroup.add(mediumEraser);

        gbc.gridy++;
        JRadioButton largeEraser = new JRadioButton("Large");
        drawent_OptionsPanel.add(largeEraser, gbc);
        buttonGroup.add(largeEraser);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton clearFieldButton = new JButton("Clear Field");
        drawent_OptionsPanel.add(clearFieldButton, gbc);

        clearFieldButton.addActionListener(e -> {
            simulator.clearScreen();
            simulator.showStatus();
        });

        // These specify the coordinate translations for the brush size.
        int[][] smallTranslations = {
                {0,0}};

        int[][] mediumTranslations = {
                {-1, 0},
                {0, -1}, {0, 0}, {0, 1},
                {1, 0}};

        int[][] largeTranslations = {
                {-2, -1}, {-2, 0}, {-2, 1},
                {-1, -2}, {-1, -1}, {-1, 0}, {-1, 1}, {-1, 2},
                {0, -2}, {0, -1}, {0, 0}, {0, 1}, {0, 2},
                {1, -2}, {1, -1}, {1, 0}, {1, 1}, {1, 2},
                {2, -1}, {2, 0}, {2, 1}};

        Consumer<MouseEvent> draw = (e) -> {
            int fieldX = e.getX()/(fieldView.getWidth()/width);
            int fieldY = e.getY()/(fieldView.getHeight()/height);

            int[][] translations;
            if (smallBrush.isSelected() || smallEraser.isSelected()){
                translations = smallTranslations;
            }
            else if (mediumBrush.isSelected() || mediumEraser.isSelected()){
                translations = mediumTranslations;
            }
            else{ //if (largeBrush.isSelected() || largeEraser.isSelected()){
                translations = largeTranslations;
            }

            Consumer<Location> action;
            if (smallBrush.isSelected() || mediumBrush.isSelected() || largeBrush.isSelected()) {
                action = (location) -> {simulator.addEntityToSimulator((EntityStats)entityComboBox.getSelectedItem(), true, field, location);};
            }
            else{ // if(smallEraser.isSelected() || mediumEraser.isSelected() || largeEraser.isSelected()){
                action = (location) -> {simulator.removeEntityInSimulator(field, location);};
            }

            for (int[] coordinate : translations) {
                Location location = new Location(fieldY + coordinate[0], fieldX + coordinate[1]);
                if (location.withinBounds(height - 1, width - 1)) {
                    action.accept(location);
                }
            }
            simulator.showStatus();
        };

        MouseListener drawClickLocationAction = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (drawmodeEnabled) {
                    draw.accept(e);
                }
            }
        };
        fieldView.addMouseListener(drawClickLocationAction);

        MouseAdapter drawDragLocationAction = new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (drawmodeEnabled) {
                    draw.accept(e);
                }
            }
        };
        fieldView.addMouseMotionListener(drawDragLocationAction);

        Runnable setDisabled = () -> {
            drawmodeEnabled = false;
            drawent_EnableButton.setText("Enable Draw Mode");
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            for (Component component : drawent_OptionsPanel.getComponents()){
                component.setEnabled(false);
            }
        };
        setDisabled.run();

        drawent_EnableButton.addActionListener(e -> {
            if (!drawmodeEnabled) {
                drawmodeEnabled = true;
                fullResetButton.setEnabled(false);
                drawent_EnableButton.setText("Disable Draw Mode");
                setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                for (Component component : drawent_OptionsPanel.getComponents()){
                    component.setEnabled(true);
                }
                for (Component component : playpause_Panel.getComponents()){
                    component.setEnabled(false);
                }
                tabmenu_TabbedPane.setEnabledAt(0, false);
                tabmenu_TabbedPane.setEnabledAt(1, false);
                tabmenu_TabbedPane.setEnabledAt(2, false);

            }
            else{
                fullResetButton.setEnabled(true);
                setDisabled.run();
                for (Component component : playpause_Panel.getComponents()){
                    component.setEnabled(true);
                }
                tabmenu_TabbedPane.setEnabledAt(0, true);
                tabmenu_TabbedPane.setEnabledAt(1, true);
                tabmenu_TabbedPane.setEnabledAt(2, true);
            }


        });

        panel.updateUI();
    }

    private void createAddSliderComponentDouble(JPanel panel, int position, String label, double max, double step, DoubleSupplier getMethod, DoubleConsumer setMethod) {
        String stepString = Double.toString(Math.abs(step));
        int decimalPlace = stepString.length() - stepString.indexOf(".") - 1;
        int multiplier = (int)Math.pow(10, decimalPlace);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = position;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel inputLabel = new JLabel(label + ": " + getMethod.getAsDouble());
        panel.add(inputLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = position + 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JSlider inputSlider = new JSlider(0, (int)(max * multiplier), (int)(getMethod.getAsDouble() * multiplier));
        inputSlider.setPaintTicks(true);
        inputSlider.setMinorTickSpacing(multiplier);
        inputSlider.setMajorTickSpacing(10 * multiplier);
        panel.add(inputSlider, gbc);

        inputSlider.addChangeListener(e -> {
            setMethod.accept((double)inputSlider.getValue()/multiplier);
            inputLabel.setText(label + ": " + getMethod.getAsDouble());
        });
    }

    private void createAddSliderComponentInt(JPanel panel, int position, String label, int max, IntSupplier getMethod, IntConsumer setMethod) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3,0,3,0);
        gbc.gridx = 0;
        gbc.gridy = position;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel inputLabel = new JLabel(label + ": " + getMethod.getAsInt());
        panel.add(inputLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = position + 1;
        //gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSlider inputSlider = new JSlider(0, max, getMethod.getAsInt());
        inputSlider.setPaintTicks(true);
        inputSlider.setMinorTickSpacing(1);
        inputSlider.setMajorTickSpacing(10);
        panel.add(inputSlider, gbc);

        inputSlider.addChangeListener(e -> {
            setMethod.accept(inputSlider.getValue());
            inputLabel.setText(label + ": " + getMethod.getAsInt());
        });

    }

    private void createSlider(JPanel panel, int position, String label, double max, double step, DoubleSupplier getMethod, DoubleConsumer setMethod, DoubleSupplier getDefaultMethod) {
        int multiplier;
        if (step == 1.0){
            multiplier = 1;
        }
        else {
            String stepString = Double.toString(Math.abs(step));
            int decimalPlace = stepString.length() - stepString.indexOf(".") - 1;
            multiplier = (int) Math.pow(10, decimalPlace);
        }

        // Label
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3,0,3,0);
        gbc.gridx = 0;
        gbc.gridy = position;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel inputLabel = new JLabel(label + ": " + removeTrailingZero(String.valueOf(getMethod.getAsDouble())));
        panel.add(inputLabel, gbc);

        // Slider
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = position + 1;
        //gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JSlider inputSlider = new JSlider(1, (int)(max * multiplier), (int)(getMethod.getAsDouble() * multiplier));
        inputSlider.setPaintTicks(true);
        inputSlider.setMinorTickSpacing(multiplier);
        inputSlider.setMajorTickSpacing(10 * multiplier);
        panel.add(inputSlider, gbc);

        inputSlider.addChangeListener(e -> {
            setMethod.accept((double) inputSlider.getValue() / multiplier);
            inputLabel.setText(label + ": " + removeTrailingZero(String.valueOf(getMethod.getAsDouble())));
        });

        if (getDefaultMethod != null){
            // Restore to Defaults Button
            gbc.gridx = 2;
            JButton defaultButton = new JButton(restoreIcon);
            defaultButton.setPreferredSize(SMALL_BUTTON_SIZE);
            panel.add(defaultButton, gbc);

            defaultButton.addActionListener(e -> {
                double defaultValue = getDefaultMethod.getAsDouble();

                inputSlider.setValue((int)((defaultValue) * multiplier));
                setMethod.accept(defaultValue);
            });
        }

    }

    private void createSlider(JPanel panel, int position, String label, double max, double step, DoubleSupplier getMethod, DoubleConsumer setMethod) {
        createSlider(panel, position, label, max, step, getMethod, setMethod, null);
    }

    private void createSlider(JPanel panel, int position, String label, int max, IntSupplier getMethod, IntConsumer setMethod, IntSupplier getDefaultMethod) {
        if (getDefaultMethod == null)
            createSlider(panel, position, label, max, 1.0, convert(getMethod), convert(setMethod), null);
        else
            createSlider(panel, position, label, max, 1.0, convert(getMethod), convert(setMethod), convert(getDefaultMethod));
    }

    private void createSlider(JPanel panel, int position, String label, int max, IntSupplier getMethod, IntConsumer setMethod) {
        createSlider(panel, position, label, max, getMethod, setMethod, null);
    }

    private DoubleSupplier convert(IntSupplier consumer) {
        return () -> consumer.getAsInt();
    }

    private DoubleConsumer convert(IntConsumer consumer) {
        return i -> consumer.accept((int)i);
    }

    private String removeTrailingZero(String string){
        if (string.endsWith(".0")){
            string = string.substring(0, string.length() - 2);
        }
        return string;
    }

    /**
     * Creates a selection of components and event listeners and adds them to a panel.
     * @param panel The panel to add the components to.
     * @param position The horizontal location the component should be placed in.
     * @param label The label for the method.
     * @param getMethod The getMethod for the attribute.
     * @param setMethod The setMethod for the attribute.
     * @param getDefaultMethod The getMethod for the default attribute.
     */
    private void createValeditSpinner(JPanel panel, int position, String label, int max, IntSupplier getMethod, IntConsumer setMethod, IntSupplier getDefaultMethod){

                // LABEL
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3,0,3,0);
        gbc.gridx = 0;
        gbc.gridy = position;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel inputLabel = new JLabel(label + ": " + getMethod.getAsInt());
        panel.add(inputLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = position + 1;
        //gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSlider inputSlider = new JSlider(0, max, getMethod.getAsInt());
        inputSlider.setPaintTicks(true);
        inputSlider.setMinorTickSpacing(1);
        inputSlider.setMajorTickSpacing(10);
        panel.add(inputSlider, gbc);

        inputSlider.addChangeListener(e -> {
            setMethod.accept(inputSlider.getValue());
            inputLabel.setText(label + ": " + getMethod.getAsInt());
        });

        // DEFAULT BUTTON
        gbc.gridx = 2;
        JButton defaultButton = new JButton(restoreIcon);
        defaultButton.setPreferredSize(new Dimension(23, 23));
        panel.add(defaultButton, gbc);

        defaultButton.addActionListener(e -> {
            int defaultValue = getDefaultMethod.getAsInt();

            inputSlider.setValue(defaultValue);
            setMethod.accept(defaultValue);
        });

        //        // LABEL
//        GridBagConstraints gbc;
//        gbc = new GridBagConstraints();
//        gbc.insets = new Insets(3,0,3,0);
//        gbc.gridx = 0;
//        gbc.gridy = position;
//        gbc.anchor = GridBagConstraints.WEST;
//        panel.add(new Label(label + ":"), gbc);
//
//        // SPINNER
//        gbc.gridx = 1;
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        JSpinner spinner = new JSpinner();
//        spinner.setValue(getMethod.getAsInt());
//        panel.add(spinner, gbc);
//
//        // DEFAULT BUTTON
//        gbc.gridx = 2;
//        JButton defaultButton = new JButton(restoreIcon);
//        defaultButton.setPreferredSize(new Dimension(23, 23));
//        panel.add(defaultButton, gbc);
//
//        //CREDIT TO https://stackoverflow.com/a/7587253/11245518
//        //Lets an event be called from any change of value. Before it was only if button or enter was pressed.
//        JComponent comp = spinner.getEditor();
//        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
//        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
//        formatter.setCommitsOnValidEdit(true);
//        spinner.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                try {spinner.commitEdit();}
//                catch ( java.text.ParseException ignored) {}
//
//                setMethod.accept((Integer) spinner.getValue());
//            }
//        });
//
//        defaultButton.addActionListener(e -> {
//            int defaultValue = getDefaultMethod.getAsInt();
//
//            spinner.setValue(defaultValue);
//            setMethod.accept(defaultValue);
//        });
    }

    /**
     * Adds all the JComponents to the target JComponent.
     * @param target The JComponent you wish to add to.
     * @param components All the JComponents you want to add to the target.
     */
    private void addAll(JComponent target, JComponent... components){
        for (JComponent component : components){
            target.add(component);
        }
    }

    /**
     * Sets the passes Dimension for all JComponents.
     * @param dimension The desired dimension for all components.
     * @param components All the JComponents you want to set the dimension of.
     */
    private void setSizeForAll(Dimension dimension, JComponent... components){
        for (JComponent component : components){
            component.setPreferredSize(dimension);
        }
    }

    private void makeDiagramsVisible(){
        JFrame diagrams = new JFrame("Histogram and PieChart");
        diagrams.setSize(1150, 550);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(this.histogram);
        panel.add(this.pieChart);
        diagrams.add(panel);
        diagrams.setVisible(true);
    }


    private void makePieChart(int height, int width) {
        this.pieChart = new PieChart();
        this.pieChart.setSize(height * 4, width * 2);
        //this.pieChart.stats(this.getPopulationDetails());
        this.pieChart.repaint();
    }

    private void makeHistogram(int height, int width) {
        this.histogram = new Histogram();
        this.histogram.setSize(height * 2, width * 2);
        //this.histogram.stats(this.getPopulationDetails());
        this.histogram.repaint();
    }

    private void updateWeather(Weather currentWeather){
        weather.setText("<html>"+ currentWeather.toString() +"</html>");
        //enview_Panel.add(weather, BorderLayout.CENTER);
        //enview_Panel.setPreferredSize(new Dimension(200,200));
        //extras_Panel.add(enview_Panel, BorderLayout.CENTER);
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field, String daytime, int daycount, String time, Weather currentWeather){
        if(!isVisible()) {
            setVisible(true);
        }

        paintEnviewPanel(step);

        simStats_StepLabel.setText(SIMSTATS_STEP_PREFIX + step);
        simStats_TimeLabel.setText(SIMSTATS_TIME_PREFIX + time);
        simStats_DaytimeLabel.setText(SIMSTATS_DAYTIME_PREFIX + daytime);
        simStats_DayCountLabel.setText(SIMSTATS_DAYCOUNT_PREFIX + daycount);
        updateWeather(currentWeather);
        stats.reset();

        fieldView.preparePaint();

        popStats_EntityCount = new ArrayList<>();
        for (EntityStats entity : simulator.getPossibleEntities()){
            popStats_EntityCount.add(0);
        }

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Entity animal = (Entity)field.getObjectAt(row, col);
                if(animal != null) {
//                    int index = simulator.getPossibleEntities().indexOf(animal.getStats());
//                    int currentCount = popStats_EntityCount.get(index);
//                    popStats_EntityCount.set(index, ++currentCount);
                    //popStats_EntityCount.set(simulator.getPossibleEntities().indexOf(animal.getStats()), popStats_EntityCount.get(index) + 1);

                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, animal.getStats().getColor());
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();
        //this.histogram.stats(this.getPopulationDetails());
        //this.histogram.repaint();
//        this.pieChart.stats(popStats_EntityCount, simulator.getPossibleEntities());
//        this.pieChart.repaint();

        popStats_TotalLabel.setText(POPSTATS_TOTAL_PREFIX + stats.getTotalCount(field));
        popStats_TypeLabel.setText(stats.getPopulationDetails(field));
        for (EntityStats entity : simulator.getPossibleEntities()){
            int index = simulator.getPossibleEntities().indexOf(entity);
            popStats_EntityLabels.get(index).setText(entity.getName() + ": " + popStats_EntityCount.get(index));
        }
        fieldView.repaint();
    }

    /**
     * Paints the contents of Environment View panel with new stats
     * @param step The current step of the simulation.
     */
    private void paintEnviewPanel(int step){
//        double doubleStep = step;
//        double partOfDay = (doubleStep % Simulator.STEP_PER_DAY)/Simulator.STEP_PER_DAY;
//        double partOfSection;
//        Color oldColor, newColor;
//        if (partOfDay >= 0 && partOfDay < 0.25){
//            oldColor = NIGHT_COLOR;
//            newColor = SUNRISE_COLOR;
//            partOfSection = partOfDay/0.25;
//        }
//        else if (partOfDay >= 0.25 && partOfDay < 0.5){
//            oldColor = SUNRISE_COLOR;
//            newColor = DAY_COLOR;
//
//            partOfSection = (partOfDay - 0.25)/0.25;
//        }
//        else if (partOfDay >= 0.25 && partOfDay < 0.75){
//            oldColor = DAY_COLOR;
//            newColor = SUNSET_COLOR;
//
//            partOfSection = (partOfDay - 0.5)/0.25;
//        }
//        else{// if (partOfDay >= 0.75 && partOfDay < 1){
//            oldColor = SUNSET_COLOR;
//            newColor = NIGHT_COLOR;
//
//            partOfSection = (partOfDay - 0.75)/0.25;
//        }
//
//        enview_Panel.setBackground(new Color(getColorChangeValue(oldColor.getRed(), newColor.getRed(), partOfSection), getColorChangeValue(oldColor.getGreen(), newColor.getGreen(), partOfSection), getColorChangeValue(oldColor.getBlue(), newColor.getBlue(), partOfSection)));

        enview_clock.setIcon(enview_clockFaces.get(step % Simulator.STEP_PER_DAY));
        enview_clock.repaint();
        enview_clock.revalidate();
    }

    /**
     *
     * @param oldValue
     * @param newValue
     * @param position The double position through thr number (0 to 1). For exmaple 0.5 will set the number halfway between the two values.
     * @return
     */
    private int getColorChangeValue(int oldValue, int newValue, double position){
        return (int)(oldValue + ((newValue-oldValue)*(position)));
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }

    /**
     * Provide a graphical view of a rectangular field. This is
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                    gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }

        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}


// CREDIT TO https://stackoverflow.com/q/10951449/11245518
/**
 * This class is responsible for displaying and manipulating the colour of the organism
 * @author Cosmo
 */
class ComboBoxRenderer extends JPanel implements ListCellRenderer {
    private static final long serialVersionUID = -1L;
    private Color[] colors;
    private String[] strings;

    JPanel textPanel;
    JLabel text;

    HashMap<Color,EntityStats>  hashmap;

    // TRIED TO MAKE IT ACCEPT <Color, Object> BUT IT WASNT LIKING IT LIKE BRUH
    public ComboBoxRenderer(JComboBox comboBox, HashMap<Color,EntityStats>  hashmap){
        this.hashmap = hashmap;
        textPanel = new JPanel();
        textPanel.add(this);
        text = new JLabel();
        text.setOpaque(true);
        text.setFont(comboBox.getFont());
        textPanel.add(text);
    }
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

//        if (value == null) {
//            text.setText("Pick a colour!");
//            return text;
//        }
//        else {
            text.setBackground((Color) value);
            text.setHorizontalAlignment(JLabel.CENTER);
            text.setText(" ");
            list.setSelectionBackground((Color) value);

            if (isColorDark((Color) value)) {
                text.setForeground(Color.WHITE);
            } else {
                text.setForeground(Color.BLACK);
            }


            if (isSelected) {
                text.setBackground(((Color) value).darker());
            } else {
                text.setBackground((Color) value);
            }

            if (hashmap.get((Color) value) != null) {
                text.setBackground(((Color) value).darker());
                text.setText(hashmap.get((Color) value).toString());
            }
            return text;
//        }
    }

    /**
     * Determines if a colour would be considered "Dark"
     * @author Credit to https://stackoverflow.com/a/24261119/11245518
     * @param color the colour to be tested
     * @return True if dark
     */
    public static boolean isColorDark(Color color){
        double darkness = 1 - (0.299 * color.getRed()+ 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        if(darkness<0.5){
            return false;
        }else{
            return true;
        }
    }
}