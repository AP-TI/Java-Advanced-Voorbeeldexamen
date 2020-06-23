package be.apti.examenalec.models;


import be.apti.examenalec.exceptions.DrankjesLimietOverschredenException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Entity
@Table(name = "bestellingen")
public class Bestelling {
    private static final double KORTING = 0.9;
    private static final int KORTING_AANTAL = 10;
    private static final int MAX_AANTAL = 150;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<Drank, Integer> drankjes;

    public Bestelling() {
        drankjes = new HashMap<>();
    }

    public Bestelling(Map<Drank, Integer> drankjes) {
        this.drankjes = drankjes;
    }

    public Map<Drank, Integer> getDrankjes() {
        return drankjes;
    }

    public void setDrankjes(Map<Drank, Integer> drankjes) {
        this.drankjes = drankjes;
    }

    public List<String> getDranklijst() {
        List<String> result = new ArrayList<>();
        drankjes.forEach((drank, aantal) -> {
            result.add(drank + " - " + aantal);
        });
        return result;
    }

    public void addDrank(Drank drank, int aantal) throws DrankjesLimietOverschredenException {
        AtomicInteger totaal = new AtomicInteger();
        drankjes.forEach((drank1, aantal1) -> {
            totaal.addAndGet(aantal1);
        });
        if (totaal.get() + aantal > MAX_AANTAL) throw new DrankjesLimietOverschredenException();
        drankjes.merge(drank, aantal, Integer::sum);


    }

    public long getId() {
        return id;
    }

    private boolean krijgtKorting() {
        AtomicInteger result = new AtomicInteger();
        drankjes.forEach((drank, aantal) -> {
            result.addAndGet(aantal);
        });
        return result.get() >= KORTING_AANTAL;
    }

    private double duursteDrankje() {
        var ref = new Object() {
            double duurste = 0;
        };
        drankjes.forEach((drank, aantal) -> {
            double prijs = drank.getPrijs() * aantal;
            if (prijs > ref.duurste) ref.duurste = prijs;
        });
        return ref.duurste;
    }

    public double getPrijs() {
        AtomicReference<Double> result = new AtomicReference<>((double) 0);
        drankjes.forEach((drank, hoeveelheid) -> {
            result.updateAndGet(v -> (v + (!krijgtKorting() ? drank.getPrijs() : drank.getPrijs() * hoeveelheid == duursteDrankje() ? drank.getPrijs() * KORTING : drank.getPrijs()) * hoeveelheid));
        });
        return result.get();
    }

    @Override
    public String toString() {
        return "Bestelling id: " + id + ", Totale prijs: €" + String.format("%.2f", getPrijs());
    }
}
