import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.awt.Dimension;

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
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private final String DAY = "Daytime: ";
    private final String NUM_OF_DAYS = "Number of days: ";
    private final String TIME_OF_DAY = "";
    //private JLabel stepLabel, population, infoLabel, dayLabel, numOfDaysLabel, time;

    private JPanel statsPanel, entityPanel, optionPanel, playpausePanel, populationTabPanel;

    private JLabel infoLabel, stepLabel, timeLabel, dayLabel, populationLabel;
    private JLabel prey1Label, prey2Label, predator1Label, predator2Label, plant1Label;

    private JButton playpauseButton, speedButton, stepButton, resetButton;
    private JTabbedPane optionsTabbedPane;
    private JSlider prey1Slider, prey2Slider, predator1Slider, predator2Slider, plant1Slider;
    private JCheckBox prey1CheckBox, prey2CheckBox, predator1CheckBox, predator2CheckBox, plant1CheckBox;

    private final Dimension BUTTON_SIZE = new Dimension(40,40);
    private final Font BUTTON_FONT =new Font("Arial", Font.BOLD, 10);

    private JCheckBox rabbitsCheckBox;
    private JButton music;
    private FieldView fieldView;
    private Histogram histogram;
    private Music mu = new Music();
    private boolean isPlaying = false;
    private PieChart pieChart;
    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    private Simulator simulator;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width, Simulator simulator)
    {
        this.simulator = simulator;
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("Fox and Rabbit Simulation");
        //stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        //infoLabel = new JLabel("      ", JLabel.CENTER);
        populationLabel = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        fieldView = new FieldView(height, width);
        //time = new JLabel(TIME_OF_DAY, JLabel.CENTER);

        // extra stats.
        //numOfDaysLabel = new JLabel(NUM_OF_DAYS, JLabel.CENTER);
        //dayLabel = new JLabel(DAY, JLabel.CENTER);

        rabbitsCheckBox = new JCheckBox("remove all rabbits from view?");
        music = new JButton("play music bb");

        Container mainPanel = getContentPane();

        // CENTRE Simulation Panel
        mainPanel.add(fieldView, BorderLayout.CENTER);

        // NORTH Stats Panel
        statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        infoLabel = new JLabel("info??: -");
        stepLabel = new JLabel("Step: -");
        timeLabel = new JLabel("Time: --:--");
        dayLabel = new JLabel("Day: -");
        addAll(statsPanel, infoLabel, stepLabel, timeLabel, dayLabel, music);
        mainPanel.add(statsPanel, BorderLayout.NORTH);

        // SOUTH Entity Population Panel
        entityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        prey1Label = new JLabel("Prey1: -");
        prey2Label = new JLabel("Prey2: -");
        predator1Label = new JLabel("Predator1: -");
        predator2Label = new JLabel("Predator2: -");
        plant1Label = new JLabel("Plant1: -");
        addAll(entityPanel, prey1Label, prey2Label, predator1Label, predator2Label, plant1Label);
        mainPanel.add(entityPanel, BorderLayout.SOUTH);

        // EAST Options Panel
        optionPanel = new JPanel(new BorderLayout());

            // NORTH PlayPause Panel
            playpausePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            playpauseButton = new JButton("P");
            speedButton = new JButton(simulator.getSpeedSymbol());
            stepButton  = new JButton("S");
            resetButton = new JButton("R");
            setSizeForAll(BUTTON_SIZE, playpauseButton, speedButton, stepButton, resetButton);
            setFontForAll(BUTTON_FONT, playpauseButton, speedButton, stepButton, resetButton);
            addAll(playpausePanel, playpauseButton, speedButton, stepButton, resetButton);
            //playpauseButton.setBorder(BorderFactory.createTitledBorder("Controls"));
            optionPanel.add(playpausePanel, BorderLayout.NORTH);

            //CENTRE Tabbed Options
            optionsTabbedPane = new JTabbedPane();

                //Tab 1: Population
                populationTabPanel = new JPanel();
                populationTabPanel.setLayout(new BoxLayout(populationTabPanel, BoxLayout.Y_AXIS));
                prey1CheckBox = new JCheckBox("Prey1: " + Simulator.getPrey1Prob(), true);
                prey1Slider = new JSlider(0, 20, (int)(Simulator.getPrey1Prob() * 100));
                prey2CheckBox = new JCheckBox("Prey2: " + Simulator.getPrey2Prob(), true);
                prey2Slider = new JSlider(0, 20, (int)(Simulator.getPrey2Prob() * 100));
                predator1CheckBox = new JCheckBox("Predator1: " + Simulator.getPredator1Prob(), true);
                predator1Slider = new JSlider(0, 20, (int)(Simulator.getPredator1Prob() * 100));
                predator2CheckBox = new JCheckBox("Predator2: " + Simulator.getPredator2Prob(), true);
                predator2Slider = new JSlider(0, 20, (int)(Simulator.getPredator2Prob() * 100));
                plant1CheckBox = new JCheckBox("Plant1: " + Simulator.getPlant1Prob(), true);
                plant1Slider = new JSlider(0, 20, (int)(Simulator.getPlant1Prob() * 100));
                initialiseSliders(prey1Slider, prey2Slider, predator1Slider, predator2Slider, plant1Slider);
                addAll(populationTabPanel, prey1CheckBox, prey1Slider, prey2CheckBox, prey2Slider, predator1CheckBox, predator1Slider, predator2CheckBox, predator2Slider, plant1CheckBox, plant1Slider);
                prey1CheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                optionsTabbedPane.addTab("Population", populationTabPanel);

        optionPanel.add(optionsTabbedPane, BorderLayout.CENTER);

        mainPanel.add(optionPanel, BorderLayout.EAST);

        // Listeners
        // Buttons
        playpauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulator.toggleRunning();
            }
        });
        speedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simulator.incSpeed();
                speedButton.setText(simulator.getSpeedSymbol());
            }
        });
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // need to run SimulateOneStep() in Simulator.... how do we do that lol.
                simulator.simulateOneStep();
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // need to run reset() in Simulator .... :sob:
                simulator.reset();
            }
        });

        // CheckBoxes
        prey1CheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                prey1Slider.setEnabled(prey1CheckBox.isSelected());
                Simulator.togglePrey1Enabled();
            }
        });
        prey2CheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                prey2Slider.setEnabled(prey2CheckBox.isSelected());
                Simulator.togglePrey2Enabled();
            }
        });
        predator1CheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                predator1Slider.setEnabled(predator1CheckBox.isSelected());
                Simulator.togglePredator1Enabled();
            }
        });
        predator2CheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                predator2Slider.setEnabled(predator2CheckBox.isSelected());
                Simulator.togglePredator2Enabled();
            }
        });
        plant1CheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                plant1Slider.setEnabled(plant1CheckBox.isSelected());
                Simulator.togglePlant1Enabled();
            }
        });

        // Sliders
        prey1Slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                prey1CheckBox.setText("Prey1: " + ((double) prey1Slider.getValue() / 100));
                Simulator.setPrey1Prob(prey1Slider.getValue());
            }
        });
        prey2Slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                prey2CheckBox.setText("Prey2: " + ((double) prey2Slider.getValue() / 100));
                Simulator.setPrey2Prob(prey2Slider.getValue());
            }
        });
        predator1Slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                predator1CheckBox.setText("Predator1: " + ((double) predator1Slider.getValue() / 100));
                Simulator.setPredator1Prob(predator1Slider.getValue());
            }
        });
        predator2Slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                predator2CheckBox.setText("Predator2: " + ((double) predator2Slider.getValue() / 100));
                Simulator.setPredator2Prob(predator2Slider.getValue());
            }
        });
        plant1Slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                plant1CheckBox.setText("Plant1: " + ((double) plant1Slider.getValue() / 100));
                Simulator.setPlant1Prob(plant1Slider.getValue());
            }
        });








//        JPanel infoPane = new JPanel();
//        infoPane.setLayout(new FlowLayout());
//        infoPane.add(stepLabel);
//        infoPane.add(dayLabel);
//        infoPane.add(numOfDaysLabel);
//        infoPane.add(time);
//        infoPane.add(music);
//
//        JPanel checkBoxes = new JPanel(new BorderLayout());
//        checkBoxes.add(rabbitsCheckBox, BorderLayout.CENTER);
//
//        Container contents = getContentPane();
//        contents.add(infoPane, BorderLayout.NORTH);
//        contents.add(checkBoxes, BorderLayout.EAST);
//        contents.add(fieldView, BorderLayout.CENTER);
//        contents.add(population, BorderLayout.SOUTH);

        makePieChart(height, width);
        makeHistogram(height, width);
        makeDiagramsVisible();
        giveButtonsFunctions();
        setLocation(100, 50);
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void addAll(JComponent target, JComponent... objects){
        for (JComponent object : objects){
            target.add(object);
        }
    }
    private void setSizeForAll(Dimension dimension, JComponent... objects){
        for (JComponent object : objects){
            object.setPreferredSize(dimension);
        }
    }
    private void setFontForAll(Font font, JComponent... objects){
        for (JComponent object : objects){
            object.setFont(font);
        }
    }

    private void initialiseSliders(JSlider... sliders){
        for (JSlider slider : sliders){
            slider.setPaintTicks(true);
            slider.setSnapToTicks(true);
            slider.setMinorTickSpacing(1);
            slider.setMajorTickSpacing(5);
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

    private void giveButtonsFunctions(){
        rabbitsCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                toggleVisibility(new Rabbit());
            }
        });

        music.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                music();
            }
        });

    }

    private void music(){
        if(!isPlaying){
            mu.setFile(this.getClass().getResourceAsStream("newBeats.wav"));
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
        this.pieChart.stats(this.getPopulationDetails());
        this.pieChart.repaint();
    }

    private void makeHistogram(int height, int width) {
        this.histogram = new Histogram();
        this.histogram.setSize(height * 2, width * 2);
        this.histogram.stats(this.getPopulationDetails());
        this.histogram.repaint();
    }

    public HashMap<Color, Counter> getPopulationDetails() {
        HashMap<Class, Counter> classStats = this.stats.getPopulation();
        HashMap<Color, Counter> colorStats = new HashMap();

        for(Class c : classStats.keySet()) {
            colorStats.put(this.getColor(c), classStats.get(c));
        }
        return colorStats;
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    public void toggleVisibility(Entity animal){
        if(rabbitsCheckBox.isSelected()){
            animal.toggleDrawable();
            setColor(animal.getClass(), EMPTY_COLOR);
        }else{
            setColor(animal.getClass(), animal.getColor());
        }
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animal){
//        return animal.getColor();
        Color col = colors.get(animal);
        if(col == null) {
            // no color defined for this class
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
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
            
        stepLabel.setText(STEP_PREFIX + step);
        timeLabel.setText(DAY + day);
        dayLabel.setText(NUM_OF_DAYS + numOfDays);
        //time.setText(TIME_OF_DAY + currentTime);
        stats.reset();
        
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();
        this.histogram.stats(this.getPopulationDetails());
        this.histogram.repaint();
        this.pieChart.stats(this.getPopulationDetails());
        this.pieChart.repaint();

        populationLabel.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
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
