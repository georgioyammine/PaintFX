module PaintFx {
    requires java.base;
    requires java.logging;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.prefs;
    requires org.jfxtras.styles.jmetro;
//    requires org.controlsfx.controls;
    requires java.desktop;
    requires javafx.swing;

    exports launcher;
    exports controllers;
    opens controllers to javafx.fxml;

}