import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Controller {
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

    private double slitSeparation;
    private double wavelength;
    private Color color;
    private double slitWidth;
    private double distance;

    double[] outputValues;
    double[] inputValues;
    final int inputLength = 100;

    // TODO: Make it so when the user enters a number too big/small into the TextFields, they move the slider to the max/min values
    // TODO: Get rid of update methods for slit separation/wavelength/slit width/distance because they are only 2 lines

    public Controller() {
        // nothing here for now
    }

    @FXML public void initialize() {
        slitSeparationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newSlitSeparation = slitSeparationSlider.getValue();
            updateSlitSeparation(newSlitSeparation);
        });

        wavelengthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newWavelength = wavelengthSlider.getValue();
            wavelength = newWavelength;
            updateColor(wavelengthToColor(newWavelength));
        });

        slitWidthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newSlitWidth = slitWidthSlider.getValue();
            updateSlitWidth(newSlitWidth);
        });

        distanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double newDistance = distanceSlider.getValue();
            updateDistance(newDistance);
        });
    }

    @FXML private void handleSlitSeparationButton() {
        try {
            double newSlitSeparation = Double.parseDouble(slitSeparationTextField.getText());
            if (newSlitSeparation >= 0 && newSlitSeparation <= 10) {
                updateSlitSeparation(newSlitSeparation);
                slitSeparationSlider.setValue(newSlitSeparation);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateSlitSeparation(double newSlitSeparation) {
        slitSeparation = newSlitSeparation;
        updateUI();
    }

    @FXML private void handleWavelengthButton() {
        try {
            double newWavelength = Double.parseDouble(wavelengthTextField.getText());
            if (newWavelength >= 400 && newWavelength <= 700) {
                updateColor(wavelengthToColor(newWavelength));
                wavelength = newWavelength;
                wavelengthSlider.setValue(newWavelength);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateColor(Color newColor) {
        color = newColor;
        updateUI();
    }

    @FXML private void handleSlitWidthButton() {
        try {
            double newSlitWidth = Double.parseDouble(slitWidthTextField.getText());
            if (newSlitWidth >= 0.5 && newSlitWidth <= 3) {
                updateSlitWidth(newSlitWidth);
                slitWidthSlider.setValue(newSlitWidth);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateSlitWidth(double newSlitWidth) {
        slitWidth = newSlitWidth;
        updateUI();
    }

    @FXML private void handleDistanceButton() {
        try {
            double newDistance = Double.parseDouble(distanceTextField.getText());
            if (newDistance >= 500 && newDistance <= 1000) {
                updateDistance(newDistance);
                distanceSlider.setValue(newDistance);
            }
        } catch (NumberFormatException e) {
            // Ignore
        }
    }

    private void updateDistance(double newDistance) {
        distance = newDistance;
        updateUI();
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
                image = new Image("res/blueSingle.PNG");
            else
                image = new Image("res/blueDouble.PNG");
        }
        else if (basicColor == Color.RED) {
            if (singleButton.isSelected())
                image = new Image("res/redSingle.PNG");
            else
                image = new Image("res/redDouble.PNG");
        }
        else {
            if (singleButton.isSelected())
                image = new Image("res/greenSingle.PNG");
            else
                image = new Image("res/greenDouble.PNG");
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
            inputValues[i] = 50 - i;              // this may need some timkering to get right
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
