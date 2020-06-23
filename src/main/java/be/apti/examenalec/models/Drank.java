package be.apti.examenalec.models;

import javax.persistence.*;

@Entity
@Table(name="drank")
public class Drank implements Comparable<Drank> {
    @Id
    @GeneratedValue
    private int id;
    private String naam;
    private double prijs;

    public Drank() {
    }

    public Drank(String naam, double prijs) {
        this.naam = naam;
        this.prijs = prijs;
    }

    public long getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    @Override
    public int compareTo(Drank o) {
        return Double.compare(o.getPrijs(), this.prijs);
    }

    @Override
    public String toString() {
        return naam + " €" + String.format("%.2f", prijs);
    }
}
