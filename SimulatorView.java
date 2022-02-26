import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;
import java.awt.Dimension;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29
 */
public class SimulatorView extends JFrame
{
    private static final Color EMPTY_COLOR = Color.white;               // Colors used for empty locations.
    private static final Color UNKNOWN_COLOR = Color.gray;              // Color used for objects that have no defined color.

    // Constants or MAIN -> NORTH Simulation Stats Panel (simstats)
    private final String SIMSTATS_STEP_PREFIX = "Step: ";
    private final String SIMSTATS_TIME_PREFIX = "Time: ";
    private final String SIMSTATS_DAYTIME_PREFIX = "Daytime: ";
    private final String SIMSTATS_DAYCOUNT_PREFIX = "Number of days: ";

    // Constants for MAIN -> SOUTH Population Stats Panel (popstats)
    private final String POPSTATS_TOTAL_PREFIX = "Total Population: ";

    // Constants for MAIN -> EAST -> CENTRE TabbedPane (tabmenu)
    private final String TAB1_NAME = "SpawnRate";
    private final String TAB2_NAME = "Values";
    private final String TAB3_NAME = "Add";

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

    // Components for MAIN -> NORTH Simulation Stats Panel (simstats)
    private JPanel simstats_Panel;
    private JLabel simstats_StepLabel, simstats_TimeLabel, simstats_DaytimeLabel, simstats_DayCountLabel;

    // Components for MAIN -> SOUTH Population Stats Panel (popstats)
    private JPanel popstats_Panel;
    private JLabel popstats_TotalLabel, popstats_TypeLabel;
    private ArrayList<Integer> popstats_EntityCount;
    private ArrayList<JLabel> popstats_EntityLabels;

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


    private JButton fullResetButton;




    private ImageIcon playIcon, pauseIcon, stepIcon, resetIcon, restoreIcon, deleteIcon;



    private final static ArrayList<Color> COLORS = new ArrayList<>(Arrays.asList(Color.RED,Color.BLUE,Color.MAGENTA,Color.ORANGE,Color.GREEN, Color.CYAN, Color.BLACK, Color.DARK_GRAY, Color.LIGHT_GRAY, Color.PINK));
    private HashMap<Color, EntityStats> entityColor;

    private JButton music;
    private FieldView fieldView;
    private Histogram histogram;
    private Music mu = new Music();
    private boolean isPlaying = false;
    private PieChart pieChart;

    // A statistics object computing and storing simulation information
    private FieldStats stats;
    private final Simulator simulator;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width, Simulator simulator, Field field)
    {
        this.simulator = simulator;
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

            public void mouseMoved(java.awt.event.MouseEvent e) {
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

        fieldView.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                inspectFrame.setVisible(false);
            }
        });

        playIcon = new ImageIcon("resources/play.png");
        pauseIcon = new ImageIcon("resources/pause.png");
        stepIcon = new ImageIcon("resources/step.png");
        resetIcon = new ImageIcon("resources/reset.png");
        restoreIcon = new ImageIcon("resources/restore.png");
        deleteIcon = new ImageIcon("resources/delete.png");



        mainPanel.add(fieldView, BorderLayout.CENTER);              // CENTRE Simulation Panel
        initialiseSimstatsPanel(mainPanel, BorderLayout.NORTH);     // NORTH Simulation Stats Panel
        initialisePopstatsPanel(mainPanel, BorderLayout.SOUTH);     // SOUTH Population Stats Panel
        initialiseOptionsPanel(mainPanel, BorderLayout.EAST);       // EAST Options Stats Panel



        // extra methods for diagrams and buttons
        setTitle("Fox and Rabbit Simulation");
        //makePieChart(height, width);
        //makeHistogram(height, width);
        //makeDiagramsVisible();
        setLocation(100, 50);
        setPreferredSize(new Dimension(1492,821));

//        // ADDED FOR DEBUGGING AND TRYING TO FIND THE BEST PREFERRED SIZE WHERE THINGS DIDNT BUG OUT
//        addComponentListener(new ComponentAdapter() {
//             public void componentResized(ComponentEvent componentEvent) {
//                 setTitle("Width:" + getWidth() + " Height: " + getHeight());
//             }
//        });

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }


    /**
     * Initialises the Simulator Statistic panel located at the North of the main frame.
     * @param container The container you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void initialiseSimstatsPanel(Container container, String layout){
        simstats_Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, LABEL_SPACING_HGAP, LABEL_SPACING_VGAP));
        simstats_StepLabel = new JLabel(SIMSTATS_STEP_PREFIX, SIMSTATS_LABEL_LAYOUT);
        simstats_TimeLabel = new JLabel(SIMSTATS_TIME_PREFIX, SIMSTATS_LABEL_LAYOUT);
        simstats_DaytimeLabel = new JLabel(SIMSTATS_DAYTIME_PREFIX, SIMSTATS_LABEL_LAYOUT);
        simstats_DayCountLabel = new JLabel(SIMSTATS_DAYCOUNT_PREFIX, SIMSTATS_LABEL_LAYOUT);
        addAll(simstats_Panel, simstats_StepLabel, simstats_TimeLabel, simstats_DaytimeLabel, simstats_DayCountLabel);
        container.add(simstats_Panel, layout);
    }

    /**
     * Initialises the Population Statistic panel located at the South of the main frame.
     * @param container The container you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void initialisePopstatsPanel(Container container, String layout){
        popstats_Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, LABEL_SPACING_HGAP , LABEL_SPACING_VGAP));
        popstats_TotalLabel = new JLabel();
        popstats_TypeLabel = new JLabel();
        addAll(popstats_Panel, popstats_TotalLabel, new JLabel("     ", JLabel.CENTER), popstats_TypeLabel, new JLabel("     ", JLabel.CENTER));

        popstats_EntityLabels = new ArrayList<>();
        for (EntityStats entity : simulator.getPossibleEntities()){
            JLabel currentEntity = new JLabel();
            popstats_EntityLabels.add(currentEntity);
            popstats_Panel.add(currentEntity); // IF THIS BREAKS THEN USE INDEX OF
        }
        container.add(popstats_Panel, layout);
    }

    /**
     * Initialises the Options panel located at the East of the main frame.
     * @param container The container you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void initialiseOptionsPanel(Container container, String layout){
        options_Panel = new JPanel(new BorderLayout());
        initialisePlaypauseButtons(options_Panel, BorderLayout.NORTH);
        initialiseTabbedMenu(options_Panel, BorderLayout.CENTER);

        fullResetButton = new JButton(FULL_RESET);
        options_Panel.add(fullResetButton, BorderLayout.SOUTH);

        fullResetButton.addActionListener(e -> {
            simulator.resetEntities();
            Randomizer.restoreDefaultSeed();

            refreshOptions();
        });

        container.add(options_Panel, layout);
    }

    /**
     * Refreshes the content of tab1 and tab2.
     */
    private void refreshOptions(){
        spawnrate_Panel.removeAll();
        valedit_Panel.removeAll();

        drawTab1Spawnrate(spawnrate_Panel, BorderLayout.CENTER);
        drawTab2Valedit(valedit_Panel, BorderLayout.CENTER);

        spawnrate_Panel.updateUI();
        valedit_Panel.updateUI();
    }

    /**
     * Initialises the Control Buttons located North of the Options frame.
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void initialisePlaypauseButtons(JPanel panel, String layout){
        playpause_Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, PLAYPAUSE_BUTTON_SPACING_HGAP, PLAYPAUSE_BUTTON_SPACING_VGAP));
        playpause_playpauseButton = new JButton(pauseIcon);
        playpause_speedButton = new JButton(simulator.getSpeedSymbol());
        playpause_stepButton = new JButton(stepIcon);
        playpause_resetButton = new JButton(resetIcon);
        setSizeForAll(PLAYPAUSE_BUTTON_SIZE, playpause_playpauseButton, playpause_speedButton, playpause_stepButton, playpause_resetButton);
        addAll(playpause_Panel, playpause_playpauseButton, playpause_speedButton, playpause_stepButton, playpause_resetButton);
        panel.add(playpause_Panel, layout);

        // Button Events
        // Stops and Starts the simulation
        playpause_playpauseButton.addActionListener(e -> {
            simulator.toggleRunning();
            if(!simulator.isRunning())
                playpause_playpauseButton.setIcon(playIcon);
            else
                playpause_playpauseButton.setIcon(pauseIcon);
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

        // Tab 2
        valedit_Panel = new JPanel(new BorderLayout());
        drawTab2Valedit(valedit_Panel, BorderLayout.CENTER);
        tabmenu_TabbedPane.addTab(TAB2_NAME, valedit_Panel);

        // Tab 3
        addent_Panel = new JPanel(new BorderLayout());
        drawTab3Addent(addent_Panel, BorderLayout.CENTER);
        tabmenu_TabbedPane.addTab(TAB3_NAME, addent_Panel);

        panel.add(tabmenu_TabbedPane, layout);
    }

    /**
     * Initialises the first tab of the TabbedPane, the Spawnrate tab.
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void drawTab1Spawnrate(JPanel panel, String layout){
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
            gbc.gridx = 1;
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
            gbc.gridx = 1;
            JButton defaultsButton = new JButton(restoreIcon);
            defaultsButton.setPreferredSize(SMALL_BUTTON_SIZE);
            spawnrate_RestoreDefaultButton.add(defaultsButton);
            holder.add(defaultsButton, gbc);
        }

        // Makes it so the tab is anchored to the top of the tab
        gbc.gridy++;
        gbc.weightx = 1;
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
                simulator.removeEntity(entity);

                panel.removeAll();
                drawTab1Spawnrate(panel, layout);
                panel.updateUI();
            });

            currentDefaultsButton.addActionListener(e -> {
                double defaultSpawnProb = entity.getDefaults().getCreationProbability();

                currentSlider.setValue((int)(defaultSpawnProb * 10));
                currentCheckBox.setText(entity.getName() + ": " + defaultSpawnProb + "%");
                entity.setCreationProbability(defaultSpawnProb);
            });
        }

    }

    /**
     * Initialises the second tab of the TabbedPane, the Value Editor tab.
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void drawTab2Valedit(JPanel panel, String layout){
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

            GridBagConstraints gbc;

            JSlider breedingSlider;
            JLabel valueBreedingLabel = new JLabel("Breeding Probability: " + stat.getBreedingProbability());;
            JButton valueBreedingDefaultButton;

            // Breeding Probability SLIDER
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            breedingSlider = new JSlider(0, 40, (int) (stat.getBreedingProbability() * 100));
            breedingSlider.setPaintTicks(true);
            breedingSlider.setSnapToTicks(true);
            breedingSlider.setMinorTickSpacing(1);
            breedingSlider.setMajorTickSpacing(5);
            currentStatSliderContainer.add(breedingSlider, gbc);

            breedingSlider.addChangeListener(e -> {
                double currentBreedProb = (double)breedingSlider.getValue()/100;

                stat.setBreedingProbability(currentBreedProb);
                valueBreedingLabel.setText("Breeding Probability: " + currentBreedProb);
            });

            // Breeding PROBABILITY LABEL
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.WEST;
            //valueBreedingLabel = new JLabel("Breeding Probability:");
            currentStatSliderContainer.add(valueBreedingLabel, gbc);

            // 2: Breeding Probability DEFAULT
            gbc = new GridBagConstraints();
            gbc.gridx = 2;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            valueBreedingDefaultButton = new JButton(restoreIcon);
            valueBreedingDefaultButton.setPreferredSize(SMALL_BUTTON_SIZE);
            currentStatSliderContainer.add(valueBreedingDefaultButton, gbc);

            valueBreedingDefaultButton.addActionListener(e -> {
                double defaultBreedProb = stat.getDefaults().getBreedingProbability();

                breedingSlider.setValue((int)(defaultBreedProb * 100));
                valueBreedingLabel.setText("Breeding Probabiity: " + defaultBreedProb);
                stat.setBreedingProbability(defaultBreedProb);
            });

            // IF ANIMAL
            if (stat.getEntityType() != EntityStats.EntityType.PLANT){
                AnimalStats animalStat = (AnimalStats)stat;

                createValeditSpinner(currentStatSliderContainer, 3, "Breeding Age", animalStat::getBreedingAge, animalStat::setBreedingAge, animalStat.getDefaults()::getBreedingAge);
                createValeditSpinner(currentStatSliderContainer, 4, "Max Age", animalStat::getMaxAge, animalStat::setMaxAge, animalStat.getDefaults()::getMaxAge);
                createValeditSpinner(currentStatSliderContainer, 5, "Max Litter Size", animalStat::getMaxLitterSize, animalStat::setMaxLitterSize, animalStat.getDefaults()::getMaxLitterSize);
                createValeditSpinner(currentStatSliderContainer, 6, "Hunger Value", animalStat::getHungerValue, animalStat::setHungerValue, animalStat.getDefaults()::getHungerValue);
                gbc.gridy = 7;
            }
            else{ // IF PLANT
                PlantStats plantStat = (PlantStats)stat;

                createValeditSpinner(currentStatSliderContainer, 3, "Food Value", plantStat::getFoodValue, plantStat::setFoodValue, plantStat.getDefaults()::getFoodValue);
                createValeditSpinner(currentStatSliderContainer, 4, "Max Level", plantStat::getMaxLevel, plantStat::setMaxLevel, plantStat.getDefaults()::getMaxLevel);
                gbc.gridy = 5;
            }

            gbc.gridx = 0;
            gbc.gridwidth = 3;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JComboBox<Color> colorComboBox = new JComboBox<>(COLORS.toArray(new Color[0]));
            //colorComboBox.setPreferredSize(new Dimension(23, 23));
            colorComboBox.setSelectedIndex(simulator.getPossibleEntities().indexOf(stat));
            colorComboBox.setBackground(stat.getColor());

            entityColor.replace(stat.getColor(), stat);

//            // CREDIT TO https://stackoverflow.com/a/7485978/11245518
//            // Removes Combobox arrow
//            colorComboBox.setUI(new BasicComboBoxUI() {
//                protected JButton createArrowButton() {
//                    return new JButton() {
//                        public int getWidth() {
//                            return 0;
//                        }
//                    };
//                }
//            });

            ComboBoxRenderer renderer = new ComboBoxRenderer(colorComboBox);
            colorComboBox.setRenderer(renderer);

            currentStatSliderContainer.add(colorComboBox, gbc);

            colorComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Color selectedColor = (Color) colorComboBox.getSelectedItem();
                    if (selectedColor != null){
                        entityColor.replace(stat.getColor(), null);
                        entityColor.replace(selectedColor, stat);
                        stat.setColor(selectedColor);
                        colorComboBox.setBackground(selectedColor);
                    }

                }
            });

            // Makes it so the tab is anchored to the top of the tab
//            gbc.gridy++;
//            gbc.gridx = 0;
//            gbc.weightx = 1;
//            gbc.weighty = 1;
//            currentStatSliderContainer.add(new JLabel(""),gbc);

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

    }

    /**
     * Initialises the third tab of the TabbedPane, the Add Entity tab.
     * @param panel The panel you want to initialise components to.
     * @param layout The position to assign the components.
     */
    private void drawTab3Addent(JPanel panel, String layout){
        JPanel nameAndTypesPanel = new JPanel(new GridBagLayout());

        addent_Container = new JPanel(new BorderLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<EntityStats.EntityType> typeComboBox = new JComboBox(EntityStats.EntityType.values());
        nameAndTypesPanel.add(typeComboBox, gbc);

        typeComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                EntityStats.EntityType type =  (EntityStats.EntityType) typeComboBox.getSelectedItem();

                newEntity.setEntityType(type);

                addent_Container.removeAll();
                addent_Container.add(typePanel.get(type),BorderLayout.CENTER);
                addent_Container.updateUI();
            }
        });

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<Color> colorComboBox  = new JComboBox<>(COLORS.toArray(new Color[0]));
        ComboBoxRenderer renderer = new ComboBoxRenderer(colorComboBox);
        colorComboBox.setPreferredSize(new Dimension(43, 23));
        colorComboBox.setRenderer(renderer);
        nameAndTypesPanel.add(colorComboBox, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        JTextField nameTextField = new JTextField();
        nameTextField.setText("SampleName");
        nameTextField.setPreferredSize(TEXTFIELD_SIZE);
        nameAndTypesPanel.add(nameTextField, gbc);

        panel.add(nameAndTypesPanel, BorderLayout.NORTH);

        // Panel with input boxes
        JPanel inputBoxesPanel = new JPanel(new GridBagLayout());

        AnimalStats newAnimal = new AnimalStats();
        newEntity = newAnimal;
        createAddSpinnerComponentDouble(inputBoxesPanel, 0, "Breeding Prob", 20, 0.1, newAnimal::getBreedingProbability, newAnimal::setBreedingProbability);
        createAddSpinnerComponentDouble(inputBoxesPanel, 1, "Creation Prob", 0.2, 0.01, newAnimal::getCreationProbability, newAnimal::setCreationProbability);
        createAddSpinnerComponentInteger(inputBoxesPanel, 2, "Breeding Age", 20,1, newAnimal::getBreedingAge, newAnimal::setBreedingAge);
        createAddSpinnerComponentInteger(inputBoxesPanel, 3, "Max Age", 100,1, newAnimal::getMaxAge, newAnimal::setMaxAge);
        createAddSpinnerComponentInteger(inputBoxesPanel, 4, "Max Litter Size", 8,1, newAnimal::getMaxLitterSize, newAnimal::setMaxLitterSize);
        createAddSpinnerComponentInteger(inputBoxesPanel, 5, "Hunger Value", 50,1, newAnimal::getHungerValue, newAnimal::setHungerValue);

        typePanel = new HashMap<>();
        typePanel.put(EntityStats.EntityType.PREY, inputBoxesPanel);
        typePanel.put(EntityStats.EntityType.PREDATOR, inputBoxesPanel);

        inputBoxesPanel = new JPanel(new GridBagLayout());

        PlantStats newPlant = new PlantStats();
        createAddSpinnerComponentDouble(inputBoxesPanel, 0, "Breeding Prob", 0.2,0.1, newPlant::getBreedingProbability, newPlant::setBreedingProbability);
        createAddSpinnerComponentDouble(inputBoxesPanel, 1, "Creation Prob", 0.2,0.01, newPlant::getCreationProbability, newPlant::setCreationProbability);
        createAddSpinnerComponentInteger(inputBoxesPanel, 2, "Food Value", 30,1, newPlant::getFoodValue, newPlant::setFoodValue);
        createAddSpinnerComponentInteger(inputBoxesPanel, 3, "Max Level", 10,1, newPlant::getMaxLevel, newPlant::setMaxLevel);

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
            simulator.addEntity(newEntity);

            newEntity.resetToDefault();

            panel.removeAll();
            drawTab3Addent(panel, layout);
            panel.updateUI();

            refreshOptions();
        });

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton clearButton = new JButton("Clear");
        addAndClearButtonsPanel.add(clearButton, gbc);

        clearButton.addActionListener(e -> {
            newEntity.resetToDefault();

            panel.removeAll();
            drawTab3Addent(panel, layout);
            panel.updateUI();
        });

        panel.add(addAndClearButtonsPanel, BorderLayout.SOUTH);
    }

    private void createAddSpinnerComponentDouble(JPanel panel, int position, String label, double max, double step, DoubleSupplier getMethod, DoubleConsumer setMethod) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = position;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel inputLabel = new JLabel(label + ":");
        panel.add(inputLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = position;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSpinner inputSpinner = new JSpinner(new SpinnerNumberModel(0, 0, max, step));
        //inputSpinner.setValue(getMethod.getAsDouble());

        //CREDIT https://stackoverflow.com/a/6449462/11245518
        JFormattedTextField txt = ((JSpinner.NumberEditor) inputSpinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        NumberFormatter numberFormatter = (NumberFormatter) txt.getFormatter();
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        numberFormatter.setFormat(decimalFormat);
        numberFormatter.setAllowsInvalid(false);

        panel.add(inputSpinner, gbc);

        //CREDIT TO https://stackoverflow.com/a/7587253/11245518
        JComponent comp = inputSpinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        inputSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {inputSpinner.commitEdit();}
                catch ( java.text.ParseException err ) {}

                setMethod.accept((Double)inputSpinner.getValue());
            }
        });
    }

    private void createAddSpinnerComponentInteger(JPanel panel, int position, String label, int max, int step, IntSupplier getMethod, IntConsumer setMethod) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = position;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel inputLabel = new JLabel(label + ":");
        panel.add(inputLabel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = position;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSpinner inputSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0, max, step));
        //inputSpinner.setValue(getMethod.getAsDouble());
        panel.add(inputSpinner, gbc);

        //CREDIT TO https://stackoverflow.com/a/7587253/11245518
        JComponent comp = inputSpinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        inputSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {inputSpinner.commitEdit();}
                catch ( java.text.ParseException err ) {}

                setMethod.accept((Integer)inputSpinner.getValue());
            }
        });
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
    private void createValeditSpinner(JPanel panel, int position, String label, IntSupplier getMethod, IntConsumer setMethod, IntSupplier getDefaultMethod){
        // LABEL
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(3,0,3,0);
        gbc.gridx = 0;
        gbc.gridy = position;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new Label(label + ":"), gbc);

        // SPINNER
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSpinner spinner = new JSpinner();
        spinner.setValue(getMethod.getAsInt());
        panel.add(spinner, gbc);

        // DEFAULT BUTTON
        gbc.gridx = 2;
        JButton defaultButton = new JButton(restoreIcon);
        defaultButton.setPreferredSize(new Dimension(23, 23));
        panel.add(defaultButton, gbc);

        //CREDIT TO https://stackoverflow.com/a/7587253/11245518
        //Lets an event be called from any change of value. Before it was only if button or enter was pressed.
        JComponent comp = spinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {spinner.commitEdit();}
                catch ( java.text.ParseException err ) {}

                setMethod.accept((Integer) spinner.getValue());
            }
        });

        defaultButton.addActionListener(e -> {
            int defaultBreedingAge = getDefaultMethod.getAsInt();

            spinner.setValue(defaultBreedingAge);
            setMethod.accept(defaultBreedingAge);
        });
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

    private void music(){
        if(!isPlaying){
            mu.setFile(this.getClass().getResourceAsStream("resources/newBeats.wav"));
            mu.play();
            isPlaying = true;
            music.setText("Music on");
        }else{
            mu.stop();
            isPlaying = false;
            music.setText("Music off");
        }
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


    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field, String day, int numOfDays, String currentTime){
        if(!isVisible()) {
            setVisible(true);
        }

        simstats_StepLabel.setText(SIMSTATS_STEP_PREFIX + step);
        simstats_TimeLabel.setText(SIMSTATS_TIME_PREFIX + currentTime);
        simstats_DaytimeLabel.setText(SIMSTATS_DAYTIME_PREFIX + day);
        simstats_DayCountLabel.setText(SIMSTATS_DAYCOUNT_PREFIX + numOfDays);
        stats.reset();
        
        fieldView.preparePaint();

        popstats_EntityCount = new ArrayList<>();
        for (EntityStats entity : simulator.getPossibleEntities()){
            popstats_EntityCount.add(0);
        }

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Entity animal = (Entity)field.getObjectAt(row, col);
                if(animal != null) {
                    int index = simulator.getPossibleEntities().indexOf(animal.getStats());
                    //popstats_EntityCount.set(simulator.getPossibleEntities().indexOf(animal.getStats()), popstats_EntityCount.get(index) + 1);

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
        //this.pieChart.stats(this.getPopulationDetails());
        //this.pieChart.repaint();

        popstats_TotalLabel.setText(POPSTATS_TOTAL_PREFIX + stats.getTotalCount(field));
        popstats_TypeLabel.setText(stats.getPopulationDetails(field));
        for (EntityStats entity : simulator.getPossibleEntities()){
            int index = simulator.getPossibleEntities().indexOf(entity);
            popstats_EntityLabels.get(index).setText(entity.getName() + ": " + popstats_EntityCount.get(index));
        }
        fieldView.repaint();
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
class ComboBoxRenderer extends JPanel implements ListCellRenderer {
    private static final long serialVersionUID = -1L;
    private Color[] colors;
    private String[] strings;

    JPanel textPanel;
    JLabel text;

    public ComboBoxRenderer(JComboBox combo){
        textPanel = new JPanel();
        textPanel.add(this);
        text = new JLabel();
        text.setOpaque(true);
        text.setFont(combo.getFont());
        textPanel.add(text);
    }
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        text.setBackground((Color)value);
        text.setText(" ");
        list.setSelectionBackground((Color) value);

        if (isSelected)
        {
            text.setBackground(((Color) value).darker());
        }
        else
        {
            text.setBackground((Color)value);
        }

        return text;
    }
}