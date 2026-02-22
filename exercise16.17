import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Exercise16_17 extends Application {

    @Override
    public void start(Stage primaryStage) {
        Text text = new Text("Show Colors");
        text.setFont(Font.font(30));

        // Create sliders
        Slider redSlider = createSlider();
        Slider greenSlider = createSlider();
        Slider blueSlider = createSlider();
        Slider opacitySlider = new Slider(0, 1, 1); // opacity 0–1
        opacitySlider.setShowTickLabels(true);
        opacitySlider.setShowTickMarks(true);

        // Labels
        Label redLabel = new Label("Red");
        Label greenLabel = new Label("Green");
        Label blueLabel = new Label("Blue");
        Label opacityLabel = new Label("Opacity");

        // Layout for sliders
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(20));
        pane.setHgap(10);
        pane.setVgap(10);

        pane.add(redLabel, 0, 0);
        pane.add(redSlider, 1, 0);

        pane.add(greenLabel, 0, 1);
        pane.add(greenSlider, 1, 1);

        pane.add(blueLabel, 0, 2);
        pane.add(blueSlider, 1, 2);

        pane.add(opacityLabel, 0, 3);
        pane.add(opacitySlider, 1, 3);

        // Update color when sliders move
        Runnable updateColor = () -> {
            double r = redSlider.getValue() / 255.0;
            double g = greenSlider.getValue() / 255.0;
            double b = blueSlider.getValue() / 255.0;
            double o = opacitySlider.getValue();

            text.setFill(new Color(r, g, b, o));
        };

        redSlider.valueProperty().addListener(e -> updateColor.run());
        greenSlider.valueProperty().addListener(e -> updateColor.run());
        blueSlider.valueProperty().addListener(e -> updateColor.run());
        opacitySlider.valueProperty().addListener(e -> updateColor.run());

        BorderPane root = new BorderPane();
        root.setTop(text);
        BorderPane.setAlignment(text, Pos.CENTER);
        root.setCenter(pane);

        Scene scene = new Scene(root, 450, 300);
        primaryStage.setTitle("Exercise16_17");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Slider createSlider() {
        Slider s = new Slider(0, 255, 0);
        s.setShowTickLabels(true);
        s.setShowTickMarks(true);
        return s;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
