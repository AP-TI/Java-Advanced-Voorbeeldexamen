package be.apti.examenalec.exceptions;

public class DrankjesLimietOverschredenException extends Exception{
    public DrankjesLimietOverschredenException() {
        super("U heeft de limiet van 150 drankjes per bestelling overschreden!");
    }
}
