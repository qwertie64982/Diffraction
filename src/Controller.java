import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;

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
    private final int inputLength = 101;     // May require some tinkering

    // Slider bounds
    private final double MIN_SLIT_SEPARATION = 0;
    private final double MAX_SLIT_SEPARATION = 10;
    private final double MIN_WAVELENGTH = 400;
    private final double MAX_WAVELENGTH = 700;
    private final double MIN_SLIT_WIDTH = 0.5;
    private final double MAX_SLIT_WIDTH = 3;
    private final double MIN_DISTANCE = 500;
    private final double MAX_DISTANCE = 1000;

    public Controller() {
        // nothing here for now
    }

    @FXML public void initialize() {
        slitSeparation = 0;
        slitSeparationSlider.setValue(slitSeparation);
        wavelength = 400;
        wavelengthSlider.setValue(wavelength);
        slitWidth = 0.5;
        slitWidthSlider.setValue(slitWidth);
        distance = 500;
        distanceSlider.setValue(distance);

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
        updateUI();
    }

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

    private void updateUI() {
        // Clears old graphs before drawing graphs
        graph.getChildren().clear();
        intensityMap.getChildren().clear();
        apertureGraph.getChildren().clear();

        // Calculates outputs for graphs based on slider values
        calculateOutput();

        // Array of mapped values - mappedValues[0] = x coordinates, mappedValues[1] = y coordinates, and mappedValues[2]=r,g,b values
        double [][] mappedValues = mapValues(graph.widthProperty().get(), graph.heightProperty().get());

        double lineWidth = intensityMap.widthProperty().get()/mappedValues[0].length;
        double xPos = 0;

        Color basicColor = getBasicColor();

        Line intensityLine;
        Line graphLine;

        // Generates both the intensity map and the graph at through the same for loop
        for (int i = 0; i < mappedValues[0].length - 1; i++) {
            intensityLine = new Line(xPos, intensityMap.getHeight(),xPos + lineWidth,0);
            graphLine = new Line(mappedValues[0][i], mappedValues[1][i], mappedValues[0][i + 1], mappedValues[1][i + 1]);
            graphLine.setStrokeWidth(1);

            if (basicColor == Color.BLUE) {
                intensityLine.setStroke(Color.rgb(0,0,((int) (mappedValues[2][i]))));
                graphLine.setStroke(Color.BLUE);
            } else if (basicColor == Color.GREEN) {
                intensityLine.setStroke(Color.rgb(0,((int) (mappedValues[2][i])),0));
                graphLine.setStroke(Color.GREEN);
            } else if (basicColor == Color.RED) {
                intensityLine.setStroke(Color.rgb(((int) (mappedValues[2][i])), 0, 0));
                graphLine.setStroke(Color.RED);
            } else {
                intensityLine.setStroke(Color.BLACK);
                graphLine.setStroke(Color.BLACK);
            }
            xPos += lineWidth;
            intensityMap.getChildren().add(intensityLine);

            graph.getChildren().add(graphLine);
        }

        // Sets up the aperture representation
        double _middle = (apertureGraph.widthProperty().get() / 2.0); //Puts the line the in the middle of the screen
        double _width = apertureGraph.widthProperty().get() / 30.0; //Arbitrary proportion of the pane
        double _distance = apertureGraph.widthProperty().get() / 20.0;

        if (singleButton.isSelected()) { //Generates aperture visualization
            Line line = new Line(_middle,apertureGraph.heightProperty().get(),_middle,0);
            line.setStroke(basicColor);
            line.setStrokeWidth(_width * slitWidth);
            apertureGraph.getChildren().add(line);
        } else {
            double distanceFromMiddle = _distance * slitSeparation / 2;
            Line line1 = new Line(_middle-distanceFromMiddle, apertureGraph.heightProperty().get(),_middle-distanceFromMiddle,0);
            line1.setStroke(basicColor);
            line1.setStrokeWidth(_width * slitWidth);
            Line line2 = new Line(_middle+distanceFromMiddle, apertureGraph.heightProperty().get(),_middle+distanceFromMiddle,0);
            line2.setStroke(basicColor);
            line2.setStrokeWidth(_width * slitWidth);
            apertureGraph.getChildren().addAll(line1, line2);
        }


        // Set the Diffraction overhead image based on wavelength and slit amount
        Image image;
        if (basicColor == Color.BLUE) {
            if (singleButton.isSelected())
                image = new Image("file:res/blueSingle.PNG");
            else
                image = new Image("file:res/blueDouble.PNG");
        }
        else if (basicColor == Color.RED) {
            if (singleButton.isSelected())
                image = new Image("file:res/redSingle.PNG");
            else
                image = new Image("file:res/redDouble.PNG");
        }
        else {
            if (singleButton.isSelected())
                image = new Image("file:res/greenSingle.PNG");
            else
                image = new Image("file:res/greenDouble.PNG");
        }
        imageView.setImage(image);

        // Sets the distance between peaks to a label
        NumberFormat formatter = new DecimalFormat("#.000000");
        diffractionDifferenceText.setText("Diffraction Peak Distance: " + formatter.format(getFirstDiffractionDistance()));
    }

    public Color getBasicColor() {
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

    public void calculateOutput() {
        inputValues = new double[inputLength];
        for (int i = 0; i < inputLength; i++) {
            inputValues[i] = (i - 50.0) / 1250;              // this may need some tinkering to get right
        }

        outputValues = new double[inputValues.length];
        for (int i = 0; i < inputValues.length; i++) {
            outputValues[i] = calculateIntensity(inputValues[i]);
        }
    }

    public double[][] mapValues(double width, double height) {
        double[][] mappedValues = new double[3][inputLength];
        double xMax = inputLength / 1000.0;
        double xMin = -xMax;

        for (int i = 0; i < inputLength; i++) {
            mappedValues[0][i] = ((inputValues[i] - xMin) * width) / (xMax-xMin);
            mappedValues[1][i] = height - (outputValues[i] * height);
            mappedValues[2][i] = outputValues[i] * 255;
        }
        return  mappedValues;
    }

    private double calculateIntensity(double xVal) {
        double betaVal = (Math.PI * xVal * slitWidth) / (wavelength * distance);
        double val = (Math.sin(betaVal)) / betaVal; // Can't divide by 0: handled in next line
        val = (Double.isNaN(val)) ? 1 : val * val;
        if (doubleButton.isSelected()) {
            double twoSlitVal = Math.cos((Math.PI * slitSeparation * xVal) / (wavelength * distance));
            val *= twoSlitVal * twoSlitVal;
        }
        System.out.println(xVal + "     " + val);
        return  val;
    }

    private double getFirstDiffractionDistance() {
//        double angle = wavelength / slitWidth;
        if (singleButton.isSelected()) {
            return (1 * wavelength * distance) / slitWidth;
        } else {
            return (1 * wavelength * distance) / slitSeparation;
        }
    }
}
