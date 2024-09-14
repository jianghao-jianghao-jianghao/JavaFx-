package schoolmanagement.java.controllers;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import schoolmanagement.java.dao.DepartmentsDao;
import schoolmanagement.java.dao.StudentsDao;
import schoolmanagement.java.models.Departments;
import schoolmanagement.java.models.Students;
import schoolmanagement.java.utils.Alerts;
import schoolmanagement.java.utils.Directories;
import schoolmanagement.java.utils.Formatter;
import schoolmanagement.java.utils.Validators;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddStudentController {
    String getGender = null;
    String getLevel = null;
    @FXML
    private JFXButton saveBtn;
    @FXML
    private JFXButton clearBtn;
    @FXML
    private JFXProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private JFXTextField balanceField;
    @FXML
    private JFXTextField amountField;
    @FXML
    private JFXTextField courseNameField;
    @FXML
    private JFXComboBox<Departments> departmentsComboBox;
    @FXML
    private JFXRadioButton certificateRadio;
    @FXML
    private JFXRadioButton diplomaRadio;
    @FXML
    private ToggleGroup level;
    @FXML
    private JFXRadioButton degreeRadio;
    @FXML
    private TextField registrationDateField;
    @FXML
    private ToggleGroup gender;
    @FXML
    private JFXRadioButton femaleRadio;
    @FXML
    private JFXRadioButton maleRadio;
    @FXML
    private JFXTextField locationField;
    @FXML
    private JFXTextField emailAddressField;
    @FXML
    private JFXTextField mobileNoField;

    @FXML
    private AnchorPane secondaryPane;

    @FXML
    private StackPane mainPane;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private JFXTextField firstNameField;

    private ApplicationContext applicationContext;
    private StudentsDao studentsDao;
    private DepartmentsDao departmentsDao;

    public void initialize() {
        applicationContext = new ClassPathXmlApplicationContext(Directories.CONFIG_XML);
        studentsDao = (StudentsDao) applicationContext.getBean("studentsDao");
        departmentsDao = (DepartmentsDao) applicationContext.getBean("departmentsDao");

        setValues();
    }



private void setValues() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时");
    String formattedDate = dateFormat.format(new Date());
    registrationDateField.setText(formattedDate);

    ObservableList<Departments> departmentsList = FXCollections.observableArrayList(departmentsDao.getDepartments());
    departmentsComboBox.setItems(departmentsList);
}

    public void onClear() {
        firstNameField.clear();
        lastNameField.clear();
        mobileNoField.clear();
        emailAddressField.clear();
        locationField.clear();
        gender.getToggles().clear();
        registrationDateField.setText(Formatter.INSTANCE.dateTimeFormatOfNow());
        departmentsComboBox.getSelectionModel().clearSelection();
        courseNameField.clear();
        amountField.clear();

        balanceField.setText("N 0.0");
    }

    public void onSave() {
        if (firstNameField.getText().isEmpty() || firstNameField.getText().trim().isEmpty()) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "省份字段不能为空");
            return;
        }

        if (lastNameField.getText().isEmpty() || lastNameField.getText().trim().isEmpty()) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "姓名字段不能为空");
            return;
        }

        if (emailAddressField.getText().isEmpty() || emailAddressField.getText().trim().isEmpty()) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "邮箱地址字段不能为空");
            return;
        }

        if (mobileNoField.getText().isEmpty() || mobileNoField.getText().trim().isEmpty()) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "手机号码字段不能为空");
            return;
        }

        if (locationField.getText().isEmpty() || locationField.getText().trim().isEmpty()) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "地点字段不能为空");
            return;
        }

        if (!maleRadio.isSelected()) {
            if (!femaleRadio.isSelected()) {
                Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "必须选择性别");
                return;
            }
        }

        if (!degreeRadio.isSelected()) {
            if (!diplomaRadio.isSelected()) {
                if (!certificateRadio.isSelected()) {
                    Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "必须选择等级");
                    return;
                }
            }
        }

        if (courseNameField.getText().isEmpty() || courseNameField.getText().trim().isEmpty()) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "课程名称字段不能为空");
            return;
        }

        if (departmentsComboBox.getSelectionModel().isEmpty()) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "必须选择部门");
            return;
        }

        if (amountField.getText().isEmpty() || amountField.getText().trim().isEmpty()) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "金额字段不能为空");
            return;
        }

        if (amountField.getText().length() > 6) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "金额字段不能超过六个字符");
            return;
        }

        if (!Validators.INSTANCE.isValidEmail(emailAddressField.getText())) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "无效的邮箱格式");
            return;
        }

        if (!Validators.INSTANCE.isNumber(mobileNoField.getText())) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "手机号码字段必须为整数");
            return;
        }

        if (Integer.parseInt(amountField.getText()) > 200_000) {
            Alerts.INSTANCE.jfxAlert(saveBtn, "错误", "金额不得超过 N200 000");
            return;
        }

        if (maleRadio.isSelected()) getGender = "男";
        if (femaleRadio.isSelected()) getGender = "女";

        if (degreeRadio.isSelected()) getLevel = "学位";
        if (diplomaRadio.isSelected()) getLevel = "文凭";
        if (certificateRadio.isSelected()) getLevel = "证书";

        if (!studentsDao.saveStudent(students())) {
            Alerts.INSTANCE.jfxBluredAlert(saveBtn, secondaryPane, "成功", "学生数据已成功保存");
            onClear();
        }
    }

    private Students students() {
        Students students = new Students();

        students.setId(null);
        students.setFirstName(firstNameField.getText());
        students.setLastName(lastNameField.getText());
        students.setMobileNumber(mobileNoField.getText());
        students.setEmail(emailAddressField.getText());
        students.setLocation(locationField.getText());
        students.setGender(getGender);
        students.setRegistrationDate(Formatter.INSTANCE.dateTimeFormatOfNow());
        students.setLevel(getLevel);
        students.setDepartment(departmentsComboBox.getSelectionModel().getSelectedItem().toString());
        students.setCourseName(courseNameField.getText());
        students.setAmount(amountField.getText());
        students.setBalance(balanceField.getText());

        return students;
    }

    public void calculateAmount(KeyEvent event) {
        int amount = Integer.parseInt(amountField.getText());
        int calc = 200_000 - amount;
        String value = String.valueOf(calc);

        String balance = "N " + value + ".00";
        balanceField.setText(balance);
    }

}