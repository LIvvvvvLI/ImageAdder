package picture;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import javafx.util.StringConverter;
import org.opencv.core.Core;

public class Main extends Application {

    final static boolean DEBUG = false;

    private boolean isDefault = true;    // 默认上方照片为backgroundImage，下方为topImage

    private void initGUI(Stage stage) {
        Pane imagePane = new AnchorPane();

        // ImageShower 1
        ImageShower imageShower1 = new ImageShower("Image1", 160, 100, true);
        imagePane.getChildren().add(imageShower1);
        imageShower1.place(20, 20);

        // ImageShower 2
        ImageShower imageShower2 = new ImageShower("Image2", 160, 100, true);
        imagePane.getChildren().add(imageShower2);
        imageShower2.place(20, 280);


        ResultPane resultPane = new ResultPane(null, null);
        resultPane.setBorder(new Border(new BorderStroke(
                Paint.valueOf("#000000"),  // 颜色
                BorderStrokeStyle.SOLID,  // 实线
                new CornerRadii(0),  // 圆角程度
                new BorderWidths(1)  // 宽度
        )));

        imageShower1.getImageView().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED &&
                        imageShower1.getImageFile() != null && imageShower2.getImageFile() != null) {
                    resultPane.setBackgroundImg(imageShower1.getImage().clone());
                    resultPane.setTopImg(imageShower2.getImage().clone());
                    resultPane.syncSize();
                    Main.this.isDefault = false;
                    imageShower1.setStyle("-fx-background-color: gray");
                    imageShower2.setStyle("-fx-background-color: null");
                }
            }
        });

        imageShower2.getImageView().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED &&
                        imageShower1.getImageFile() != null && imageShower2.getImageFile() != null) {
                    resultPane.setBackgroundImg(imageShower2.getImage().clone());
                    resultPane.setTopImg(imageShower1.getImage().clone());
                    resultPane.syncSize();
                    Main.this.isDefault = false;
                    imageShower1.setStyle("-fx-background-color: null");
                    imageShower2.setStyle("-fx-background-color: gray");
                }
            }
        });

        Slider alphaSlider = new Slider(0, 1, 1);  // min, max, value
        alphaSlider.setPrefWidth(10);
        alphaSlider.setPrefHeight(50);
        alphaSlider.setOrientation(Orientation.VERTICAL);  // 设置方向
        alphaSlider.setMajorTickUnit(1);  // 设置主刻度之间的距离
        alphaSlider.setShowTickLabels(true);  // 设置刻度标签
        // 更改刻度标签格式
        alphaSlider.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double aDouble) {
                return String.format(" %.0f%%", (1.0d - aDouble) * 100.0d);
            }

            @Override
            public Double fromString(String s) {
                return Double.valueOf(s);
            }
        });

        alphaSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                try {
                    resultPane.getSaveButton().setText(String.format("%.1f%%", 100.0d - 100.0d * t1.doubleValue()));
                    // 默认以上方Image为底
                    if (Main.this.isDefault) {
                        resultPane.setBackgroundImg(imageShower1.getImage().clone());
                        resultPane.setTopImg(imageShower2.getImage().clone());
                        resultPane.syncSize();
                        if (DEBUG) {
                            System.out.println("isDefault");
                        }
                    }
                    // 非默认时则说明Back和top已在ImageView事件中设置
                    resultPane.changeImage(1 - t1.doubleValue());
                } catch (NullPointerException e) {
                    if (DEBUG) {
                        System.out.println("NullPointer");
                        e.printStackTrace();
                    }
                }
            }
        });

        HBox hRoot = new HBox(20);
        hRoot.getChildren().addAll(imagePane, alphaSlider, resultPane);
        HBox.setMargin(alphaSlider, new Insets(30, 10, 0, 10));  // 上右下左
        hRoot.setPadding(new Insets(10, 10, 50, 10));
        HBox.setMargin(resultPane, new Insets(20, 0, 0, 0));
        if (DEBUG) {
            hRoot.setStyle("-fx-background-color: green");
        }
        hRoot.setPrefHeight(500);

        Scene scene = new Scene(hRoot, 800, 580);
        stage.setScene(scene);
        stage.setTitle("Add Picture");
        stage.setResizable(false);
    }

    @Override
    public void start(Stage stage) throws Exception {
        initGUI(stage);
        stage.show();
    }

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Application.launch(args);
    }
}
