package schoolmanagement.java.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import schoolmanagement.java.dao.StudentsDao;
import schoolmanagement.java.models.Students;
import schoolmanagement.java.utils.Alerts;
import schoolmanagement.java.utils.Directories;
import schoolmanagement.java.utils.RecursiveStudent;

import java.util.List;

public class ViewStudentsController {
    ObservableList<RecursiveStudent> list = null;
    List<Students> studentsList = null;
    int id = 0;
    @FXML
    private Button editBtn;
    @FXML
    private Label fullNameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label mobileNoLabel;
    @FXML
    private Label locationLabel;
    @FXML
    private Label departmentLabel;
    @FXML
    private Label levelLabel;

    @FXML
    private TreeTableColumn<RecursiveStudent, String> courseEnrolledCol;
    @FXML
    private TreeTableColumn<RecursiveStudent, String> studentNamesCol;
    @FXML
    private TreeTableColumn<RecursiveStudent, String> idCol;
    @FXML
    private JFXTreeTableView<RecursiveStudent> treeTableView;
    @FXML
    private JFXButton refreshField;
    @FXML
    private JFXTextField searchField;
    @FXML
    private JFXRadioButton namesRadioBtn;
    @FXML
    private ToggleGroup filterGroup;
    @FXML
    private JFXRadioButton idRadioBtn;
    @FXML
    private AnchorPane secondaryPane;
    @FXML
    private StackPane mainPane;
    private ApplicationContext applicationContext;
    private StudentsDao studentsDao;
    @FXML
    private Label courseNameLabel;
    @FXML
    private Label paidLabel;
    @FXML
    private Label balanceLabel;

    public void initialize() {
        applicationContext = new ClassPathXmlApplicationContext(Directories.CONFIG_XML);
        studentsDao = (StudentsDao) applicationContext.getBean("studentsDao");

        list = FXCollections.observableArrayList();
        studentsList = studentsDao.getAllStudents();
        treeTableView.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.DELETE)) {
                onDelete();
            }
        });
        setTable();

        treeTableView.getSelectionModel().selectFirst();

        treeTableEventHandler();
    }

    private void treeTableEventHandler() {
        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            id = Integer.parseInt(newValue.getValue().getId());
            System.out.println(id);
            setDetails();
        });
    }

    private void setDetails() {
        List<Students> list = studentsDao.getStudent(String.valueOf(id));
        list.forEach(student -> {
            fullNameLabel.setText(student.getFirstName() + " " + student.getLastName());
            emailLabel.setText(student.getEmail());
            mobileNoLabel.setText(student.getMobileNumber());
            locationLabel.setText(student.getLocation());

            departmentLabel.setText(student.getDepartment());
            levelLabel.setText(student.getLevel());
            courseNameLabel.setText(student.getCourseName());

            paidLabel.setText(student.getAmount());
            balanceLabel.setText(student.getBalance());
        });
    }

    private void setTable() {
        list.clear();
        idCol.setCellValueFactory(param -> param.getValue().getValue().idProperty());
        studentNamesCol.setCellValueFactory(param -> param.getValue().getValue().studentNameProperty());
        courseEnrolledCol.setCellValueFactory(param -> param.getValue().getValue().courseEnrolledProperty());


        TreeItem<RecursiveStudent> root = new RecursiveTreeItem<>(list, RecursiveTreeObject::getChildren);
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);

        studentsList.forEach(students -> {
            list.addAll(new RecursiveStudent(students.getId(), students.getFirstName() + " " + students.getLastName(), students.getCourseName()));
        });

        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                treeTableView.setPredicate(modelTreeItem ->
                        modelTreeItem.getValue().idProperty().getValue().toLowerCase().contains(newValue)
                                || modelTreeItem.getValue().studentNameProperty().getValue().toLowerCase().contains(newValue)));
    }

    public void onRefresh() {
        list.clear();
        List<Students> refreshList = studentsDao.getAllStudents();
        ObservableList<RecursiveStudent> observableList = FXCollections.observableArrayList();
        refreshList.forEach(students -> {
            observableList.addAll(new RecursiveStudent(students.getId(), students.getFirstName() + " " + students.getLastName(), students.getCourseName()));
        });
        list.addAll(observableList);
    }

@FXML
public void onEdit() {
    // Create a dialog layout
    JFXDialogLayout content = new JFXDialogLayout();
    JFXDialog dialog = new JFXDialog(mainPane, content, JFXDialog.DialogTransition.CENTER);
    content.setAlignment(Pos.CENTER);
    content.setHeading(new Text("修改数据"));

    // Create input fields pre-filled with current data
    JFXTextField firstNameField = new JFXTextField(fullNameLabel.getText().split(" ")[0]);
    firstNameField.setPromptText("名字");
    JFXTextField lastNameField = new JFXTextField(fullNameLabel.getText().split(" ")[1]);
    lastNameField.setPromptText("姓氏");
    JFXTextField emailField = new JFXTextField(emailLabel.getText());
    emailField.setPromptText("邮箱");
    JFXTextField mobileNoField = new JFXTextField(mobileNoLabel.getText());
    mobileNoField.setPromptText("手机号");
    JFXTextField locationField = new JFXTextField(locationLabel.getText());
    locationField.setPromptText("位置");

    VBox box = new VBox(firstNameField, lastNameField, emailField, mobileNoField, locationField);
    box.setSpacing(15);
    box.setAlignment(Pos.CENTER);
    content.setBody(box);

    // Create buttons
    JFXButton saveBtn = new JFXButton("保存");
    JFXButton cancelBtn = new JFXButton("取消");
    saveBtn.getStyleClass().add("dial-btn");
    cancelBtn.getStyleClass().add("dial-btn");

    // Set button actions
    saveBtn.setOnAction(event -> {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
            emailField.getText().isEmpty() || mobileNoField.getText().isEmpty() || locationField.getText().isEmpty()) {
            Alerts.INSTANCE.jfxAlert(mainPane, "错误", "所有字段都必须填写");
            return;
        }

        // Update the student data


        // Update the UI
        setDetails();
        dialog.close();
    });

    cancelBtn.setOnAction(event -> dialog.close());

    content.setActions(saveBtn, cancelBtn);
    dialog.show();
}

    public void onDelete() {
        BoxBlur blur = new BoxBlur(3.0, 3.0, 3);
        secondaryPane.setEffect(blur);

        JFXDialogLayout content = new JFXDialogLayout();
        JFXDialog dialog = new JFXDialog(mainPane, content, JFXDialog.DialogTransition.TOP);
        content.setAlignment(Pos.CENTER);
        content.setHeading(new Text(""));
        VBox box = new VBox();
        box.setSpacing(15);
        box.setAlignment(Pos.CENTER);

        Label text = new Label("您确定要删除这个学生，ID = " + id);

        box.getChildren().addAll(text);
        content.setBody(box);

        JFXButton okBtn = new JFXButton("确定");
        JFXButton cancelBtn = new JFXButton("取消");

        okBtn.getStyleClass().add("dial-btn");
        cancelBtn.getStyleClass().add("dial-btn");

        okBtn.setOnAction(event -> {
            removeItem();
            dialog.close();
        });

        okBtn.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                removeItem();
                dialog.close();
            }
        });

        cancelBtn.setOnAction(event -> {
            dialog.close();
        });

        content.setActions(okBtn, cancelBtn);

        dialog.setOnDialogClosed(event -> {
            secondaryPane.setEffect(null);
        });
        dialog.setLayoutX(100);
        dialog.show();

    }

    private void removeItem() {
        studentsDao.deleteStudent(String.valueOf(id));
        int index = treeTableView.getSelectionModel().getSelectedIndex();
        list.remove(index);
    }
}