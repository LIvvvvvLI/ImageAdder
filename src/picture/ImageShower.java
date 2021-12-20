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
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class ImageShower extends VBox implements Place {
    private Button imageButton = null;
    private ImageView imageView = null;
    private File imageFile = null;
    private Mat image = null;
    private final boolean hasBorder;

    public ImageShower(String buttonText, double width, double height, boolean hasBorder) {
        this.hasBorder = hasBorder;

        initPanel(width);

        initButton(buttonText, width, height);

        initImageView(width, 1.218 * width);

        this.getChildren().addAll(imageButton, imageView);
    }

    private void initPanel(double width) {
        this.setSpacing(3);  // 指定节点间的垂直间距
        this.setWidth(width);
        if (Main.DEBUG) {
            this.setStyle("-fx-background-color: gray");  // 指定背景色
        }
        this.setPadding(new Insets(3, 3, 3, 3));
        this.setAlignment(Pos.CENTER);  // 设置子节点的对齐方式
        if (hasBorder) {
            this.setBorder(new Border(new BorderStroke(
                    Paint.valueOf("#000000"),  // 颜色
                    BorderStrokeStyle.SOLID,  // 实线
                    new CornerRadii(0),  // 圆角程度
                    new BorderWidths(1)  // 宽度
            )));
        }
    }

    private void initImageView(double viewWidth, double viewHeight) {
        imageView = new ImageView();
        imageView.setFitHeight(viewHeight);
        imageView.setFitWidth(viewWidth);
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED) {

                }
            }
        });
    }

    private void initButton(String buttonText, double btWidth, double btHeight) {
        imageButton = new Button(buttonText);
        imageButton.setMaxSize(btWidth, btHeight);
        imageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                imageFile = chooseFile("Choose one picture", 1);
                try {
                    setImage(imageFile);
                } catch (IOException e) {
                    // 未选择文件或选择的文件不存在
                    System.out.println("Open file Error");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Open File Error!");
                    alert.show();
                }
            }
        });
    }

    public void setImage(File viewImage) throws IOException {
        if (viewImage == null) {
            return;
        }

        imageFile = viewImage;
//        image = Imgcodecs.imread(imageFile.getPath());  // 直接读取会因为中文路径产生错误

        // 将InputStream转化为Mat
        FileInputStream inputStream = new FileInputStream(imageFile);
        byte[] byteArray = new byte[inputStream.available()];
        inputStream.read(byteArray);
        inputStream.close();
        MatOfByte matOfByte = new MatOfByte(byteArray);
        image = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR);

        // 处理imdecode返回null
        if (image.size().empty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Open File Error!");
            alert.show();
            clear();
            return;
        }

        // 同步UI
        imageView.setImage(new Image(new FileInputStream(imageFile)));
        imageButton.setText(imageFile.getName());
    }

    public static File chooseFile(String frameTitle, int type) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(frameTitle);
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg"),
                new FileChooser.ExtensionFilter("all files", "*.*")
        );
        if (type == 1) {
            return fileChooser.showOpenDialog(null);  // showOpenDialog的参数用于指定打开位置
        } else if (type == 2) {
            return fileChooser.showSaveDialog(null);  // showSaveDialog的参数用于指定打开位置
        } else {
            return null;
        }
    }

    public void clear() {
        imageView.setImage(null);
        imageButton.setText("Empty");
        imageFile = null;
        image = null;
    }

    public Button getImageButton() {
        return imageButton;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Mat getImage() {
        return image;
    }

    public File getImageFile() {
        return imageFile;
    }

    /**
     * 在父布局中放置节点。在父布局调用add()方法后调用后，否则无效。
     * @param x 节点在父布局中的横坐标
     * @param y 节点在父布局中的纵坐标
     * @return 放置成功返回 true，否则返回 false
     */
    @Override
    public boolean place(double x, double y) {
        if (this.getParent() instanceof AnchorPane) {
            AnchorPane.setLeftAnchor(this, x);
            AnchorPane.setTopAnchor(this, y);
            return true;
        } else {
            // when getParent == null or getParent != AnchorPane
            return false;
        }
    }
}
