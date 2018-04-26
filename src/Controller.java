/*
 * Diffraction simulation
 *
 * Maxwell Sherman, Malik Al Ali, Daniel Cole
 * Spring 2018, CSCI 2300-M01
 */

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Main controller class for Main.java
 */
public class Controller {
    // Views
    @FXML private Slider wavelengthSlider;
    @FXML private Slider slitWidthSlider;
    @FXML private Slider distanceSlider;
    @FXML private Slider slitSeparationSlider;
    @FXML private TextField wavelengthTextField;
    @FXML private TextField slitWidthTextField;
    @FXML private TextField distanceTextField;
    @FXML private TextField slitSeparationTextField;
    @FXML private Text diffractionDifferenceText;
    @FXML private ImageView imageView;
    @FXML private RadioButton singleButton;
    @FXML private RadioButton doubleButton;
    @FXML private Pane graph;
    @FXML private Pane intensityMap;
    @FXML private Pane apertureGraph;
    @FXML private ToggleGroup slitNumber;

    // State of experiment
    private double slitSeparation;
    private double wavelength;
    private Color color;
    private double slitWidth;
    private double distance;

    // Calculations
    private double[] outputValues;
    private double[] inputValues;
    private final int INPUT_LENGTH = 1001;

    // Slider bounds
    private final double MIN_SLIT_SEPARATION = 0;
    private final double MAX_SLIT_SEPARATION = 10;
    private final double MIN_WAVELENGTH = 400;
    private final double MAX_WAVELENGTH = 700;
    private final double MIN_SLIT_WIDTH = 0.5;
    private final double MAX_SLIT_WIDTH = 3;
    private final double MIN_DISTANCE = 500;
    private final double MAX_DISTANCE = 1000;

    /**
     * Main constructor (runs before window is loaded)
     */
    public Controller() {
        slitSeparation = 0;
        wavelength = 400;
        slitWidth = 0.5;
        distance = 500;
    }

    /**
     * Initializer (runs once window is loaded but not shown)
     */
    @FXML public void initialize() {
        // Set slider values
        slitSeparationSlider.setValue(slitSeparation);
        wavelengthSlider.setValue(wavelength);
        slitWidthSlider.setValue(slitWidth);
        distanceSlider.setValue(distance);

        // GUI listeners
        slitSeparationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            slitSeparation = slitSeparationSlider.getValue();
            updateUI();
        });
        wavelengthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newWavelength = wavelengthSlider.getValue();
            color = wavelengthToColor(newWavelength);
            wavelength = newWavelength;
            updateUI();
        });
        slitWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            slitWidth = slitWidthSlider.getValue();
            updateUI();
        });
        distanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            distance = distanceSlider.getValue();
            updateUI();
        });
        slitNumber.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                updateUI();
            }
        });
    }

    /**
     * Runs when the slit separation OK button is pressed
     * Sets the new slit separation and updates the UI
     */
    @FXML private void handleSlitSeparationButton() {
        try {
            double newSlitSeparation = Double.parseDouble(slitSeparationTextField.getText());
            if (newSlitSeparation >= MIN_SLIT_SEPARATION && newSlitSeparation <= MAX_SLIT_SEPARATION) {
                slitSeparation = newSlitSeparation;
                slitSeparationSlider.setValue(newSlitSeparation);
                updateUI();
            } else if (newSlitSeparation < MIN_SLIT_SEPARATION) {
                slitSeparation = MIN_SLIT_SEPARATION;
                slitSeparationSlider.setValue(MIN_SLIT_SEPARATION);
                updateUI();
            } else { // newSlitSeparation > MAX_SLIT_SEPARATION
                slitSeparation = MAX_SLIT_SEPARATION;
                slitSeparationSlider.setValue(MAX_SLIT_SEPARATION);
                updateUI();
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    /**
     * Runs when the wavelength OK button is pressed
     * Sets the new wavelength/color and updates the UI
     */
    @FXML private void handleWavelengthButton() {
        try {
            double newWavelength = Double.parseDouble(wavelengthTextField.getText());
            if (newWavelength >= MIN_WAVELENGTH && newWavelength <= MAX_WAVELENGTH) {
                color = wavelengthToColor(newWavelength);
                wavelength = newWavelength;
                wavelengthSlider.setValue(newWavelength);
                updateUI();
            } else if (newWavelength < MIN_WAVELENGTH) {
                color = wavelengthToColor(MIN_WAVELENGTH);
                wavelength = MIN_WAVELENGTH;
                wavelengthSlider.setValue(MIN_WAVELENGTH);
                updateUI();
            } else { // newWavelength > MAX_WAVELENGTH
                color = wavelengthToColor(MAX_WAVELENGTH);
                wavelength = MAX_WAVELENGTH;
                wavelengthSlider.setValue(MAX_WAVELENGTH);
                updateUI();
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    /**
     * Runs when the slit width OK button is pressed
     * Sets the new slit width and updates the UI
     */
    @FXML private void handleSlitWidthButton() {
        try {
            double newSlitWidth = Double.parseDouble(slitWidthTextField.getText());
            if (newSlitWidth >= MIN_SLIT_WIDTH && newSlitWidth <= MAX_SLIT_WIDTH) {
                slitWidth = newSlitWidth;
                slitWidthSlider.setValue(newSlitWidth);
                updateUI();
            } else if (newSlitWidth < MIN_SLIT_WIDTH) {
                slitWidth = MIN_SLIT_WIDTH;
                slitWidthSlider.setValue(MIN_SLIT_WIDTH);
                updateUI();
            } else { // newSlitWidth > MAX_SLIT_WIDTH
                slitWidth = MAX_SLIT_WIDTH;
                slitWidthSlider.setValue(MAX_SLIT_WIDTH);
                updateUI();
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    /**
     * Runs when the distance OK button is pressed
     * Sets the new distance and updates the UI
     */
    @FXML private void handleDistanceButton() {
        try {
            double newDistance = Double.parseDouble(distanceTextField.getText());
            if (newDistance >= MIN_DISTANCE && newDistance <= MAX_DISTANCE) {
                distance = newDistance;
                distanceSlider.setValue(newDistance);
                updateUI();
            } else if (newDistance < MIN_DISTANCE) {
                distance = MIN_DISTANCE;
                distanceSlider.setValue(MIN_DISTANCE);
                updateUI();
            } else { // newDistance > MAX_DISTANCE
                distance = MAX_DISTANCE;
                distanceSlider.setValue(MAX_DISTANCE);
                updateUI();
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    /**
     * Accurately converts wavelength of light to RGB
     * @param wavelength wavelength (nm)
     * @return Color object from wavelength
     */
    private Color wavelengthToColor(double wavelength) {
        // Credit for algorithm: http://www.physics.sfasu.edu/astro/color/spectra.html
        // FORTRAN is an adventure

        double red, green, blue;
        double opacity = 1;

        if (wavelength >= 380 && wavelength < 440) {
            red = -(wavelength - 440) / (440-380);
            green = 0;
            blue = 1;
        } else if (wavelength >= 440 && wavelength < 490) {
            red = 0;
            green = (wavelength - 440) / (490-440);
            blue = 1;
        } else if (wavelength >= 490 && wavelength < 510) {
            red = 0;
            green = 1;
            blue = -(wavelength - 510) / (510 - 490);
        } else if (wavelength >= 510 && wavelength < 580) {
            red = (wavelength - 510) / (580 - 510);
            green = 1;
            blue = 0;
        } else if (wavelength >= 580 && wavelength < 645) {
            red = 1;
            green = -(wavelength - 645) / (645 - 580);
            blue = 0;
        } else if (wavelength >= 645 && wavelength < 781) {
            red = 1;
            green = 0;
            blue = 0;
        } else {
            red = 0;
            green = 0;
            blue = 0;
            opacity = 0;
        }
        return new Color(red, green, blue, opacity);
    }

    /**
     * Updates all the major UI (graph, gradient, and diagrams)
     */
    void updateUI() {
        // Thanks to the "inspirational" code given to us, we were able to get similar-looking diagrams

        // Clears old graphs before drawing graphs
        graph.getChildren().clear();
        intensityMap.getChildren().clear();
        apertureGraph.getChildren().clear();

        // Calculates outputs for graphs based on "state of experiment" variables
        calculateOutput();

        // mappedValues[0] = x coordinates
        // mappedValues[1] = y coordinates
        // mappedValues[2] = RGB values
        double [][] mappedValues = mapValues(graph.widthProperty().get(), graph.heightProperty().get());

        double lineWidth = intensityMap.widthProperty().get() / mappedValues[0].length;
        double xPos = 0;

        Color basicColor = getBasicColor();
        Color advancedColor = wavelengthToColor(wavelength);

        Line intensityLine;
        Line graphLine;

        // Generates both the intensity map and the graph
        for (int i = 0; i < mappedValues[0].length - 1; i++) {
            intensityLine = new Line(xPos, intensityMap.getHeight(),xPos + lineWidth,0);
            graphLine = new Line(mappedValues[0][i], mappedValues[1][i], mappedValues[0][i + 1], mappedValues[1][i + 1]);
            graphLine.setStrokeWidth(1);
            graphLine.setStroke(advancedColor);

            // This is where it would be really complicated to use true color
            if (basicColor == Color.BLUE) {
                intensityLine.setStroke(Color.rgb(0,0,((int) (mappedValues[2][i]))));
            } else if (basicColor == Color.GREEN) {
                intensityLine.setStroke(Color.rgb(0,((int) (mappedValues[2][i])),0));
            } else {
                intensityLine.setStroke(Color.rgb(((int) (mappedValues[2][i])), 0, 0));
            }
            xPos += lineWidth;
            intensityMap.getChildren().add(intensityLine);

            graph.getChildren().add(graphLine);
        }

        // Sets up the aperture representation
        double _middle = (apertureGraph.widthProperty().get() / 2.0); // Puts the line the in the middle of the screen
        double _width = apertureGraph.widthProperty().get() / 30.0; // Arbitrary proportion of the pane
        double _distance = apertureGraph.widthProperty().get() / 20.0;

        if (singleButton.isSelected()) { // Generates aperture visualization
            Line line = new Line(_middle,apertureGraph.heightProperty().get(),_middle,0);
            line.setStroke(advancedColor);
            line.setStrokeWidth(_width * slitWidth);
            apertureGraph.getChildren().add(line);
        } else {
            double distanceFromMiddle = _distance * slitSeparation / 2;
            Line line1 = new Line(_middle-distanceFromMiddle, apertureGraph.heightProperty().get(),_middle-distanceFromMiddle,0);
            line1.setStroke(advancedColor);
            line1.setStrokeWidth(_width * slitWidth);
            Line line2 = new Line(_middle+distanceFromMiddle, apertureGraph.heightProperty().get(),_middle+distanceFromMiddle,0);
            line2.setStroke(advancedColor);
            line2.setStrokeWidth(_width * slitWidth);
            apertureGraph.getChildren().addAll(line1, line2);
        }

        // Set the diffraction overhead image based on wavelength and slit amount
        // This would also be difficult to achieve with true color
        Image image;
        try {
            if (basicColor == Color.BLUE) {
                if (singleButton.isSelected())
                    image = SwingFXUtils.toFXImage(ImageIO.read(getClass().getClassLoader().getResource("blueSingle.PNG")), null);
                else
                    image = SwingFXUtils.toFXImage(ImageIO.read(getClass().getClassLoader().getResource("blueDouble.PNG")), null);
            } else if (basicColor == Color.RED) {
                if (singleButton.isSelected())
                    image = SwingFXUtils.toFXImage(ImageIO.read(getClass().getClassLoader().getResource("redSingle.PNG")), null);
                else
                    image = SwingFXUtils.toFXImage(ImageIO.read(getClass().getClassLoader().getResource("redDouble.PNG")), null);
            } else {
                if (singleButton.isSelected())
                    image = SwingFXUtils.toFXImage(ImageIO.read(getClass().getClassLoader().getResource("greenSingle.PNG")), null);
                else
                    image = SwingFXUtils.toFXImage(ImageIO.read(getClass().getClassLoader().getResource("greenDouble.PNG")), null);
            }
            imageView.setImage(image);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        // Sets the distance between peaks to a label
        NumberFormat formatter = new DecimalFormat("0.######");
        diffractionDifferenceText.setText("Diffraction Peak Distance: " + formatter.format(getFirstDiffractionDistance()) + "cm");
    }

    /**
     * Gets a basic version of the color for diagrams
     * (Simplifies math a lot)
     * @return approximation of color
     */
    private Color getBasicColor() {
        if (wavelength >= 400 && wavelength < 500) {
            return Color.BLUE;
        }
        else if (wavelength >= 500 && wavelength < 600){
            return Color.GREEN;
        }
        else if (wavelength >= 600 && wavelength <= 700) {
            return Color.RED;
        } else {
            return Color.BLACK;
        }
    }

    /**
     * Finds the x and y coordinates for the graph/gradient
     * x ranges from -1.1cm to 1.1cm on the graph
     * y stays constant for ease of viewing
     */
    private void calculateOutput() {
        inputValues = new double[INPUT_LENGTH];
        int halfInputLength = (INPUT_LENGTH - 1) / 2;
        for (int i = 0; i < INPUT_LENGTH; i++) {
            inputValues[i] = (i - halfInputLength) / (halfInputLength / 1.1); // +- 1.1 cm
        }

        outputValues = new double[inputValues.length];
        for (int i = 0; i < inputValues.length; i++) {
            outputValues[i] = calculateIntensity(inputValues[i]);
        }
    }

    /**
     * Assembles all the coordinates into one 2D array
     * @param width width of pane
     * @param height height of pane
     * @return all coordinates in one array
     */
    private double[][] mapValues(double width, double height) {
        double[][] mappedValues = new double[3][INPUT_LENGTH];
        double xMax = INPUT_LENGTH / 1000.0;
        double xMin = -xMax;

        for (int i = 0; i < INPUT_LENGTH; i++) {
            mappedValues[0][i] = ((inputValues[i] - xMin) * width) / (xMax-xMin); // x-coordinates
            mappedValues[1][i] = height - (outputValues[i] * height);             // y-coordinates
            mappedValues[2][i] = outputValues[i] * 255;                           // RGB values
        }
        return  mappedValues;
    }

    /**
     * Finds the intensity of each x-coordinate
     * @param xVal x value
     * @return intensity
     */
    private double calculateIntensity(double xVal) {
        // slit width mm -> cm, wavelength nm -> cm
        double betaVal = (Math.PI * xVal * (slitWidth / 10)) / ((wavelength / 10000000) * distance);
        double val = (Math.sin(betaVal)) / betaVal; // Can't divide by 0 - handled in next line
        val = (Double.isNaN(val)) ? 1 : val * val;
        if (doubleButton.isSelected()) {
            // slit separation mm -> cm, wavelength nm -> cm
            double twoSlitVal = Math.cos((Math.PI * (slitSeparation / 10) * xVal) / ((wavelength / 10000000) * distance));
            val *= twoSlitVal * twoSlitVal;
        }
//        System.out.println(xVal + "     " + val);              // DEBUG
        return  val;
    }

    /**
     * Finds the distance to the first peak (m = 1)
     * @return distance in cm
     */
    private double getFirstDiffractionDistance() {
        if (singleButton.isSelected()) {
            // distance to the first dark band
            // wavelength nm -> cm, slit width mm -> cm
            double darkDistance = (1 * (wavelength / 10000000) * distance) / (slitWidth / 10);
            // multiply by 1.5 to get the light band between the first and second dark bands
            return darkDistance * 1.5;
        } else {
            // distance to the first dark band
            // wavelength nm -> cm, slit separation mm -> cm
            double darkDistance = (1 * (wavelength / 10000000) * distance) / (slitSeparation / 10);
            // multiply by 1.5 to get the light band between the first and second dark bands
            return darkDistance * 1.5;
        }
    }
}
