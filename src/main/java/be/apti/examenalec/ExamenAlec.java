package be.apti.examenalec;

import be.apti.examenalec.exceptions.DrankjesLimietOverschredenException;
import be.apti.examenalec.models.Bestelling;
import be.apti.examenalec.models.Drank;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

public class ExamenAlec extends Application {
    private static SessionFactory factory;
    private Bestelling nieuweBestelling;

    @Override
    public void init() throws Exception {
        super.init();
        try {
            factory = new Configuration().configure().addPackage("be.apti.examenalec").addAnnotatedClass(Bestelling.class).buildSessionFactory();
            nieuweBestelling = new Bestelling();
        } catch (HibernateException exception) {
            throw new ExceptionInInitializerError(exception);
        }

    }

    @Override
    public void start(Stage stage) throws Exception {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        Scene scene = new Scene(gridPane, 1000, 1000);
        stage.setScene(scene);
        stage.setTitle("Bestelling");
        stage.show();
        GridPane invoer = new GridPane();
        ComboBox<Drank> comboBoxDrank = new ComboBox<>(FXCollections.observableArrayList(List.of(new Drank("Water", 1.0), new Drank("Bier", 2), new Drank("Wijn", 3), new Drank("Frisdrank", 1.5))));
        TextField textFieldAantal = new TextField();
        GridPane.setMargin(textFieldAantal, new Insets(0, 0, 0, 5));
        ListView listView = new ListView();
        Label labelTotalePrijs = new Label();
        Button buttonVoegToe = new Button("Voeg toe");
        buttonVoegToe.setOnAction(actionEvent -> {
            try {
                nieuweBestelling.addDrank(comboBoxDrank.getValue(), Integer.parseInt(textFieldAantal.getText()));
            } catch (DrankjesLimietOverschredenException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Limiet overschreden");
                alert.setContentText(e.getMessage());
                alert.show();
            }
            listView.setItems(FXCollections.observableArrayList(nieuweBestelling.getDranklijst()));
            labelTotalePrijs.setText("Totale prijs: €" + String.format("%.2f", nieuweBestelling.getPrijs()));
        });
        Button maakBestelling = new Button("Maak bestelling");
        ListView listViewBestellingen = new ListView();
        maakBestelling.setOnAction(actionEvent -> {
            nieuweBestelling.getDrankjes().forEach((drank, integer) -> {
                addDrankje(drank);
            });
            addData(nieuweBestelling);
            listViewBestellingen.setItems(FXCollections.observableArrayList(getAllBestellingen()));
            nieuweBestelling = new Bestelling();
            listView.getItems().clear();
        });

        Label labelBestelling = new Label();
        ListView listViewBestellingInhoud = new ListView();


        listViewBestellingen.setOnMouseClicked(mouseEvent -> {
            Bestelling bestelling = (Bestelling)listViewBestellingen.getSelectionModel().getSelectedItems().get(0);
            labelBestelling.setText(bestelling.toString());
            listViewBestellingInhoud.setItems(FXCollections.observableArrayList(bestelling.getDranklijst()));
        });


        invoer.add(comboBoxDrank, 0, 0);
        invoer.add(textFieldAantal, 2, 0);
        invoer.add(buttonVoegToe, 3, 0);
        invoer.add(listView, 0, 1);
        invoer.add(labelTotalePrijs, 0, 2);
        invoer.add(maakBestelling, 0, 3);

        gridPane.setAlignment(Pos.TOP_CENTER);
        GridPane.setMargin(invoer, new Insets(10));
        gridPane.add(invoer, 0, 0);
        gridPane.add(labelBestelling, 1, 0);
        gridPane.add(listViewBestellingInhoud, 1, 1);

        gridPane.add(listViewBestellingen, 0, 1);
    }

    private static void addData(Bestelling bestelling) {
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(bestelling);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                if (transaction != null) transaction.rollback();
            }
        }
    }

    private static void addDrankje(Drank drank){
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                session.save(drank);
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                if (transaction != null) transaction.rollback();
            }
        }
    }

    private static List<Bestelling> getAllBestellingen() {
        List<Bestelling> laptops = new ArrayList<>();
        try (Session session = factory.openSession()) {
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<Bestelling> criteriaQuery = builder.createQuery(Bestelling.class);
                criteriaQuery.from(Bestelling.class);
                laptops = session.createQuery(criteriaQuery).getResultList();
                transaction.commit();
            } catch (Exception ex) {
                ex.printStackTrace();

                if (transaction != null) transaction.rollback();
            }
        }
        return laptops;
    }
}
