package picture;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ResultPane extends StackPane {
    private Button saveButton = null;
    private ImageView imageView = null;
    private Mat image = null;

    private Mat backgroundImg = null, topImg = null;

    public ResultPane(Mat img1, Mat img2) {
        this.setPrefWidth(500);
        this.setAlignment(Pos.TOP_CENTER);
        if (Main.DEBUG) {
            this.setStyle("-fx-background-color: gray");
        }

        image = new Mat();

        initButton(img1, img2);
        initImageView();
        this.getChildren().addAll(saveButton, imageView);
    }

    private void initButton(Mat img1, Mat img2) {
        saveButton = new Button("Save");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                File imageFile = ImageShower.chooseFile("Save picture", 2);
                if (imageFile != null && !image.size().empty()) {
                    Imgcodecs.imwrite(imageFile.getPath(), image);
                    System.out.println("Save to " + imageFile.getPath());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("message");
                    alert.setHeaderText(null);
                    alert.setContentText("Save to " + imageFile.getPath());
                    alert.show();
                }
            }
        });
        saveButton.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                saveButton.setText("Save");
            }
        });
        StackPane.setMargin(saveButton, new Insets(10, 0, 0 ,0));
    }

    private void initImageView() {
        imageView = new ImageView();
        imageView.setFitWidth(500);
        imageView.setFitHeight(500);
    }

    public void setImage(Image image) {
        imageView.setImage(image);
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public void changeImage(double value) throws NullPointerException {
        if (backgroundImg == null || topImg == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Choose Background Picture!");
            alert.show();
            return;
        }

        if (backgroundImg.size().empty() || topImg.size().empty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Loading Module Error!");
            alert.show();
            return;
        }

        // 叠加
        Core.addWeighted(backgroundImg, 1 - value, topImg, value, 0, image);

        // 将Mat转化为byte[]
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".png", image, matOfByte);
        byte[] byteArray = matOfByte.toArray();

        // 设置Image
        try (InputStream in = new ByteArrayInputStream(byteArray)) {
            setImage(new Image(in));
        } catch (IOException e) {
            if (Main.DEBUG) {
                e.printStackTrace();
            }
        }

        // 确保按钮始终在最上层
        if (this.getChildren().contains(saveButton)) {
            this.getChildren().remove(saveButton);
            this.getChildren().add(saveButton);
        }
    }

    public void setBackgroundImg(Mat backgroundImg) {
        this.backgroundImg = backgroundImg;
    }

    public void setTopImg(Mat topImg) {
        this.topImg = topImg;
    }

    public void syncSize() {
        if (topImg == null || backgroundImg == null) {
            return;
        }
        if (!topImg.size().empty() && !backgroundImg.size().empty()) {
            Imgproc.resize(topImg, topImg, new Size(backgroundImg.width(), backgroundImg.height()));
        }
    }

    public void clear() {
        imageView.setImage(null);
        saveButton.setText("Save");
        image = null;
    }
}
