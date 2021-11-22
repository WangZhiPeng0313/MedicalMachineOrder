package com.wes.mmo.application.window;

import com.wes.mmo.dao.EquementDetail;
import com.wes.mmo.service.task.OrderTask;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class OrderWindow {

    public static final Log LOG = LogFactory.getLog(OrderWindow.class);

    private TableView<OrderTask> orderTaskTableView;

    private EquementDetail equementDetail;

    private Stage orderStage;
    private DatePicker startDatePicker;
    private ChoiceBox startHourCheckBox;
    private ChoiceBox startMinuteCheckBox;

    private DatePicker endDatePicker;
    private ChoiceBox endHourCheckBox;
    private ChoiceBox endMinuteCheckBox;

    private DatePicker actionDatePicker;
    private ChoiceBox actionHourChoiceBox;
    private ChoiceBox actionMinuteChoicBox;

    private TextField relationProductTextField;

    public OrderWindow(TableView orderTaskTableView, EquementDetail equementDetail) {
        this.orderTaskTableView = orderTaskTableView;
        this.equementDetail = equementDetail;
    }

    public void initlize() throws IOException {
        orderStage = new Stage();
        orderStage.setTitle("预约窗口");
        orderStage.setWidth(600);
        orderStage.setHeight(400);
        URL orderFxmlUrl =  this.getClass().getResource("/fxml/order/index.fxml");
        VBox orderPane = FXMLLoader.load(orderFxmlUrl);
        Scene mainScene = new Scene(orderPane);
        orderStage.setScene(mainScene);

        // 初始化架构时间
        TextField equemenetName = (TextField) ((BorderPane)orderPane.getChildren().get(1)).getChildren().get(1);


        // 初始化开始时间
        startDatePicker = parseDatePicker(orderPane, 2, 1);
        startHourCheckBox = parseCheckBox(orderPane, 2,2,0);
        startMinuteCheckBox = parseCheckBox(orderPane, 2, 2, 1);

        endDatePicker = parseDatePicker(orderPane, 3, 1);
        endHourCheckBox = parseCheckBox(orderPane, 3,2,0);
        endMinuteCheckBox = parseCheckBox(orderPane, 3,2,1);

        // 获取定时时间
        actionDatePicker = parseDatePicker(orderPane, 4, 1);
        actionHourChoiceBox = parseCheckBox(orderPane, 4,2, 0);
        actionMinuteChoicBox = parseCheckBox(orderPane, 4,2,1);

        relationProductTextField =  (TextField) ((BorderPane)orderPane.getChildren().get(5)).getChildren().get(1);

        //
        Button orderButton = (Button) ((BorderPane)orderPane.getChildren().get(6)).getChildren().get(0);
        orderButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LOG.info("Click Order Button");

                // 获取仪器的信息

                // 获取相关信息
                LocalDate startDate = startDatePicker.getValue();
                String startTimeStr = new StringBuffer().append(startDate.getYear()).append(startDate.getMonthValue()).append(startDate.getDayOfMonth())
                        .append(startHourCheckBox.getValue().toString()).append(startMinuteCheckBox.getValue().toString()).toString();

                // 获取结束时间
                LocalDate endDate = endDatePicker.getValue();
                String endTimeStr = new StringBuffer().append(endDate.getYear()).append(endDate.getMonthValue()).append(endDate.getDayOfMonth())
                        .append(endHourCheckBox.getValue().toString()).append(endMinuteCheckBox.getValue().toString()).toString();

                // 获取执行时间
                LocalDate actionDate = actionDatePicker.getValue();
                String actionTimeStr = new StringBuffer().append(actionDate.getYear()).append(actionDate.getMonthValue()).append(actionDate.getDayOfMonth())
                        .append(actionHourChoiceBox.getValue().toString()).append(actionHourChoiceBox.getValue().toString()).toString();


                String relationProduct = relationProductTextField.getText();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
                try {
                    OrderTask orderTask = new OrderTask(equementDetail,
                            sdf.parse(startTimeStr).getTime()/1000,
                            sdf.parse(endTimeStr).getTime()/1000 - 1,
                            "", relationProduct,
                            sdf.parse(actionTimeStr).getTime()/1000);

                    orderTask.run();

                    orderTaskTableView.getItems().add(orderTask);

                    orderStage.close();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // 将预定信息添加到MainWindow的列表中

            }


        });

        // 对值进行初始化
        equemenetName.setText(equementDetail.getName());
        String hour= new SimpleDateFormat("HH").format(new Date());
        startHourCheckBox.setValue(hour);
        endHourCheckBox.setValue(hour);

        show();
    }

    private DatePicker parseDatePicker(VBox orderPane, int borderPaneIndex, int datePickerIndex) {
        BorderPane borderPane = (BorderPane) orderPane.getChildren().get(borderPaneIndex);
        DatePicker datePicker = (DatePicker) borderPane.getChildren().get(datePickerIndex);
        datePicker.setValue(LocalDate.now());
        return datePicker;
    }

    private ChoiceBox parseCheckBox(VBox orderPane, int borderPaneIndex, int timeBorderPaneIndex, int checkBoxIndex){
        BorderPane borderPane = (BorderPane) orderPane.getChildren().get(borderPaneIndex);
        BorderPane timeBorderPane = (BorderPane) borderPane.getChildren().get(timeBorderPaneIndex);
        return (ChoiceBox) timeBorderPane.getChildren().get(checkBoxIndex);
    }


    public void show(){
        orderStage.show();
    }
}